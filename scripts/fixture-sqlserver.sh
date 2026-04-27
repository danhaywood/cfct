#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

CONTAINER_NAME="${SQLCOMPARER_FIXTURE_CONTAINER:-sqlcomparer-fixture-sqlserver}"
SQLSERVER_IMAGE="${SQLCOMPARER_FIXTURE_IMAGE:-mcr.microsoft.com/mssql/server:2022-latest}"
HOST_PORT="${SQLCOMPARER_FIXTURE_PORT:-14333}"
SA_PASSWORD="${SQLCOMPARER_FIXTURE_PASSWORD:-Str0ng_password!123}"
LEFT_DATABASE="${SQLCOMPARER_LEFT_DATABASE:-left_db}"
RIGHT_DATABASE="${SQLCOMPARER_RIGHT_DATABASE:-right_db}"
FIXTURE_ROOT="${REPO_ROOT}/sqlcomparer-integration-tests/src/test/resources/sql/fixtures"

usage() {
  cat <<USAGE
Usage: $(basename "$0") <start|stop|status>

Commands:
  start   Start SQL Server, create fixture databases, and load demo data.
  stop    Stop and remove the fixture SQL Server container if present.
  status  Show whether the fixture container is running.

Environment overrides:
  SQLCOMPARER_FIXTURE_CONTAINER  Container name (default: ${CONTAINER_NAME})
  SQLCOMPARER_FIXTURE_IMAGE      SQL Server image (default: ${SQLSERVER_IMAGE})
  SQLCOMPARER_FIXTURE_PORT       Host port (default: ${HOST_PORT})
  SQLCOMPARER_FIXTURE_PASSWORD   sa password (default: fixture demo password)
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

load_demo_data() {
  create_database "${LEFT_DATABASE}"
  create_database "${RIGHT_DATABASE}"

  for fixture in supplier product purchase-order purchase-order-without-business-key; do
    apply_fixture "${fixture}" "${LEFT_DATABASE}" left
    apply_fixture "${fixture}" "${RIGHT_DATABASE}" right
  done
}

start_fixture() {
  command -v docker >/dev/null 2>&1 || {
    echo "Error: docker is required but was not found." >&2
    exit 1
  }

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
  load_demo_data
  echo "Fixture SQL Server ready at localhost:${HOST_PORT}."
  echo "Databases: ${LEFT_DATABASE}, ${RIGHT_DATABASE}."
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
  case "${command}" in
    start)
      start_fixture
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
