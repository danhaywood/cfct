#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
DEMO_ENV="${SQLCOMPARER_DEMO_ENV:-${REPO_ROOT}/demo/sqlcomparer.env}"
DEMO_TABLES="${SQLCOMPARER_DEMO_TABLES:-${REPO_ROOT}/demo/tables.txt}"
CLI_JAR="${SQLCOMPARER_CLI_JAR:-${REPO_ROOT}/sqlcomparer-cli/target/sqlcomparer-cli-0.0.1-SNAPSHOT.jar}"

usage() {
  cat <<USAGE
Usage: $(basename "$0") [additional CLI arguments]

Builds or locates the SQL comparer CLI jar, then runs it with:
  --env-file ${DEMO_ENV}
  --tables-file ${DEMO_TABLES}

Additional arguments are appended to the CLI invocation.
Start the fixture first with:
  ${SCRIPT_DIR}/fixture-sqlserver.sh start

Environment overrides:
  SQLCOMPARER_DEMO_ENV     Demo env file path
  SQLCOMPARER_DEMO_TABLES  Demo tables file path
  SQLCOMPARER_CLI_JAR      CLI jar path
USAGE
}

if [[ "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
  usage
  exit 0
fi

if [[ ! -f "${DEMO_ENV}" ]]; then
  echo "Error: demo env file not found: ${DEMO_ENV}" >&2
  exit 1
fi

if [[ ! -f "${DEMO_TABLES}" ]]; then
  echo "Error: demo tables file not found: ${DEMO_TABLES}" >&2
  exit 1
fi

jar_needs_build() {
  if [[ ! -f "${CLI_JAR}" ]]; then
    return 0
  fi

  if find "${REPO_ROOT}/pom.xml" \
          "${REPO_ROOT}/sqlcomparer-api" \
          "${REPO_ROOT}/sqlcomparer-impl" \
          "${REPO_ROOT}/sqlcomparer-cli" \
          -type f \
          \( -name 'pom.xml' -o -name '*.java' -o -name '*.properties' -o -name '*.xml' \) \
          -newer "${CLI_JAR}" \
          -print -quit | grep -q .; then
    return 0
  fi

  return 1
}

if jar_needs_build; then
  echo "Building sqlcomparer-cli..."
  (cd "${REPO_ROOT}" && mvn -pl sqlcomparer-cli -am package -DskipTests)
fi

if [[ ! -f "${CLI_JAR}" ]]; then
  echo "Error: CLI jar was not created: ${CLI_JAR}" >&2
  exit 1
fi

exec java -jar "${CLI_JAR}" \
  --env-file "${DEMO_ENV}" \
  --tables-file "${DEMO_TABLES}" \
  "$@"
