#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

CONTAINER_NAME="${CFCT_FIXTURE_CONTAINER:-cfct-fixture-sqlserver}"
SQLSERVER_IMAGE="${CFCT_FIXTURE_IMAGE:-mcr.microsoft.com/mssql/server:2022-latest}"
HOST_PORT="${CFCT_FIXTURE_PORT:-14333}"
SA_PASSWORD="${CFCT_FIXTURE_PASSWORD:-Str0ng_password!123}"
LEFT_DATABASE="${CFCT_LEFT_DATABASE:-left_db}"
RIGHT_DATABASE="${CFCT_RIGHT_DATABASE:-right_db}"
INVALID_TARGET_DATABASE="${CFCT_INVALID_TARGET_DATABASE:-${RIGHT_DATABASE}_missing}"
FIXTURE_ROOT="${REPO_ROOT}/cfct-integration-tests/src/test/resources/sql/fixtures"

usage() {
  cat <<USAGE
Usage: $(basename "$0") <start|restart|stop|status> [--restart] [--invalid-target-db] [--missing-system-tables]

Commands:
  start        Start SQL Server, create fixture databases, and load demo data.
  restart      Recreate SQL Server container, then create fixture databases and load demo data.
  stop         Stop and remove the fixture SQL Server container if present.
  status       Show whether the fixture container is running.

Flags:
  --restart               Recreate the container even if already running.
  --invalid-target-db     Do not create the target database and print a non-existent target name for manual failure testing.
  --missing-system-tables Remove required target system objects after fixture load for manual failure testing.

Environment overrides:
  CFCT_FIXTURE_CONTAINER       Container name (default: ${CONTAINER_NAME})
  CFCT_FIXTURE_IMAGE           SQL Server image (default: ${SQLSERVER_IMAGE})
  CFCT_FIXTURE_PORT            Host port (default: ${HOST_PORT})
  CFCT_FIXTURE_PASSWORD        sa password (default: fixture demo password)
  CFCT_INVALID_TARGET_DATABASE Missing target name used with --invalid-target-db (default: ${INVALID_TARGET_DATABASE})
USAGE
}

container_exists() {
  docker ps -a --format '{{.Names}}' | grep -Fxq "${CONTAINER_NAME}"
}

container_running() {
  docker ps --format '{{.Names}}' | grep -Fxq "${CONTAINER_NAME}"
}

sqlcmd_path() {
  local path
  for path in /opt/mssql-tools18/bin/sqlcmd /opt/mssql-tools/bin/sqlcmd; do
    if docker exec "${CONTAINER_NAME}" test -x "${path}" >/dev/null 2>&1; then
      printf '%s\n' "${path}"
      return 0
    fi
  done
  return 1
}

exec_sql() {
  local database="$1"
  local sqlcmd
  sqlcmd="$(sqlcmd_path)" || {
    echo "Error: sqlcmd not found in ${CONTAINER_NAME}." >&2
    exit 1
  }
  docker exec -i "${CONTAINER_NAME}" "${sqlcmd}" -C -S localhost -U sa -P "${SA_PASSWORD}" -d "${database}" -b
}

wait_for_sqlserver() {
  echo "Waiting for SQL Server readiness..."
  local deadline=$((SECONDS + 180))
  until sqlcmd_path >/dev/null 2>&1 && printf 'SELECT 1;\nGO\n' | exec_sql master >/dev/null 2>&1; do
    if (( SECONDS >= deadline )); then
      echo "Error: SQL Server did not become ready within 180 seconds." >&2
      docker logs --tail 80 "${CONTAINER_NAME}" >&2 || true
      exit 1
    fi
    sleep 2
  done
}

create_database() {
  local database="$1"
  printf "IF DB_ID(N'%s') IS NULL CREATE DATABASE [%s];\nGO\n" "${database}" "${database}" | exec_sql master >/dev/null
}

apply_fixture() {
  local fixture="$1"
  local database="$2"
  local side="$3"
  local schema_file="${FIXTURE_ROOT}/${fixture}/schema.sql"
  local data_file="${FIXTURE_ROOT}/${fixture}/${side}-data.sql"

  if [[ ! -f "${schema_file}" || ! -f "${data_file}" ]]; then
    echo "Error: missing fixture files for ${fixture} (${side})." >&2
    exit 1
  fi

  echo "Loading ${fixture} ${side} data into ${database}..."
  exec_sql "${database}" < "${schema_file}" >/dev/null
  exec_sql "${database}" < "${data_file}" >/dev/null
}

drop_required_target_objects() {
  local database="$1"
  echo "Removing required target system objects from ${database} for manual missing-system-tables testing..."
  cat <<'SQL' | exec_sql "${database}" >/dev/null
IF OBJECT_ID(N'causewayExtAuditTrail.AuditTrailEntry', N'V') IS NOT NULL DROP VIEW causewayExtAuditTrail.AuditTrailEntry;
IF OBJECT_ID(N'causewayExtAuditTrail.AuditTrailEntry', N'U') IS NOT NULL DROP TABLE causewayExtAuditTrail.AuditTrailEntry;
IF OBJECT_ID(N'causewayExtCommandLog.CommandLogEntry', N'V') IS NOT NULL DROP VIEW causewayExtCommandLog.CommandLogEntry;
IF OBJECT_ID(N'causewayExtCommandLog.CommandLogEntry', N'U') IS NOT NULL DROP TABLE causewayExtCommandLog.CommandLogEntry;
IF OBJECT_ID(N'util.LogicalTypeTableMapping', N'V') IS NOT NULL DROP VIEW util.LogicalTypeTableMapping;
IF OBJECT_ID(N'util.LogicalTypeTableMapping', N'U') IS NOT NULL DROP TABLE util.LogicalTypeTableMapping;
GO
SQL
}

load_demo_data() {
  local invalid_target_db="${1:-false}"
  local missing_system_tables="${2:-false}"

  create_database "${LEFT_DATABASE}"

  if [[ "${invalid_target_db}" == "true" ]]; then
    echo "Skipping target database creation for manual invalid-target testing."
    echo "Use target database: ${INVALID_TARGET_DATABASE}"
  else
    create_database "${RIGHT_DATABASE}"
  fi

  for fixture in supplier product customer-address purchase-order purchase-order-without-business-key; do
    apply_fixture "${fixture}" "${LEFT_DATABASE}" left
    if [[ "${invalid_target_db}" != "true" ]]; then
      apply_fixture "${fixture}" "${RIGHT_DATABASE}" right
    fi
  done

  if [[ "${invalid_target_db}" != "true" && "${missing_system_tables}" == "true" ]]; then
    drop_required_target_objects "${RIGHT_DATABASE}"
  fi
}

start_fixture() {
  local restart="${1:-false}"
  local invalid_target_db="${2:-false}"
  local missing_system_tables="${3:-false}"

  command -v docker >/dev/null 2>&1 || {
    echo "Error: docker is required but was not found." >&2
    exit 1
  }

  if [[ "${restart}" == "true" ]] && container_exists; then
    echo "Restart requested, removing fixture container ${CONTAINER_NAME}..."
    docker rm -f "${CONTAINER_NAME}" >/dev/null
  fi

  if container_running; then
    echo "Fixture SQL Server is already running as ${CONTAINER_NAME}."
  else
    if container_exists; then
      echo "Removing stopped fixture container ${CONTAINER_NAME}..."
      docker rm "${CONTAINER_NAME}" >/dev/null
    fi
    echo "Starting ${SQLSERVER_IMAGE} as ${CONTAINER_NAME} on localhost:${HOST_PORT}..."
    docker run \
      --name "${CONTAINER_NAME}" \
      -e ACCEPT_EULA=Y \
      -e MSSQL_PID=Developer \
      -e MSSQL_SA_PASSWORD="${SA_PASSWORD}" \
      -p "${HOST_PORT}:1433" \
      -d "${SQLSERVER_IMAGE}" >/dev/null
  fi

  wait_for_sqlserver
  load_demo_data "${invalid_target_db}" "${missing_system_tables}"
  echo "Fixture SQL Server ready at localhost:${HOST_PORT}."
  if [[ "${invalid_target_db}" == "true" ]]; then
    echo "Databases: ${LEFT_DATABASE}."
    echo "Invalid target database for manual testing: ${INVALID_TARGET_DATABASE}."
  else
    echo "Databases: ${LEFT_DATABASE}, ${RIGHT_DATABASE}."
    if [[ "${missing_system_tables}" == "true" ]]; then
      echo "Missing-system-tables mode enabled on target database: ${RIGHT_DATABASE}."
    fi
  fi
}

stop_fixture() {
  if container_exists; then
    echo "Stopping and removing ${CONTAINER_NAME}..."
    docker rm -f "${CONTAINER_NAME}" >/dev/null
    echo "Fixture SQL Server stopped."
  else
    echo "Fixture SQL Server container ${CONTAINER_NAME} is not present."
  fi
}

status_fixture() {
  if container_running; then
    echo "Fixture SQL Server is running."
    echo "Container: ${CONTAINER_NAME}"
    echo "Server: localhost:${HOST_PORT}"
  elif container_exists; then
    echo "Fixture SQL Server container exists but is stopped."
    echo "Container: ${CONTAINER_NAME}"
  else
    echo "Fixture SQL Server is not present."
  fi
}

main() {
  local command="${1:-}"
  shift || true

  local restart_requested="false"
  local invalid_target_db="false"
  local missing_system_tables="false"

  while (($# > 0)); do
    case "$1" in
      --restart)
        restart_requested="true"
        ;;
      --invalid-target-db)
        invalid_target_db="true"
        ;;
      --missing-system-tables)
        missing_system_tables="true"
        ;;
      *)
        echo "Error: unknown flag '$1'." >&2
        usage >&2
        exit 2
        ;;
    esac
    shift
  done

  if [[ "${invalid_target_db}" == "true" && "${missing_system_tables}" == "true" ]]; then
    echo "Error: --invalid-target-db and --missing-system-tables cannot be used together." >&2
    exit 2
  fi

  case "${command}" in
    start)
      start_fixture "${restart_requested}" "${invalid_target_db}" "${missing_system_tables}"
      ;;
    restart)
      start_fixture true "${invalid_target_db}" "${missing_system_tables}"
      ;;
    stop)
      stop_fixture
      ;;
    status)
      status_fixture
      ;;
    -h|--help|help|"")
      usage
      ;;
    *)
      echo "Error: unknown command '${command}'." >&2
      usage >&2
      exit 2
      ;;
  esac
}

main "$@"
