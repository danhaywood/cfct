#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="${SCRIPT_DIR}"
ENV_FILE="${CFCT_ENV_FILE:-${PWD}/.env}"
TABLES_FILE="${CFCT_TABLES_FILE:-}"
CLI_JAR="${CFCT_CLI_JAR:-${REPO_ROOT}/cfct-cli/target/cfct-cli-0.0.1-SNAPSHOT.jar}"
BUILD_COMMAND="mvn -pl cfct-cli -am package"

usage() {
  cat <<USAGE
Usage: $(basename "$0") [--env-file <path>] [additional CLI arguments]

Runs the CFCT (Command Footprint Comparison Tool) CLI with:
  --env-file <path>

Env-file precedence (highest to lowest):
  1) --env-file <path>
  2) CFCT_ENV_FILE
  3) .env in the current directory

Table selection must be provided either by passing CLI table arguments, for example:
  --tables-file path/to/tables.txt
  -t dbo.Supplier,dbo.Product

If CFCT_TABLES_FILE is set, this wrapper also passes:
  --tables-file \${CFCT_TABLES_FILE}

Additional arguments are appended to the CLI invocation.
For example, pass --output-format json for JSON output or write Excel output with --output-format excel -o comparison.xlsx.

Before running this script, build the CLI jar with:
  ${BUILD_COMMAND}

Environment overrides:
  CFCT_ENV_FILE     Env file path (default: .env in the current directory)
  CFCT_TABLES_FILE  Optional tables file path (no default)
  CFCT_CLI_JAR      CLI jar path (default: ${CLI_JAR})
USAGE
}

if [[ "${1:-}" == "-h" || "${1:-}" == "--help" ]]; then
  usage
  exit 0
fi

PASSTHROUGH_ARGS=()
while [[ $# -gt 0 ]]; do
  case "$1" in
    --env-file)
      if [[ $# -lt 2 ]]; then
        echo "Error: --env-file requires a path argument" >&2
        exit 1
      fi
      ENV_FILE="$2"
      shift 2
      ;;
    --env-file=*)
      ENV_FILE="${1#*=}"
      if [[ -z "${ENV_FILE}" ]]; then
        echo "Error: --env-file requires a non-empty path" >&2
        exit 1
      fi
      shift
      ;;
    *)
      PASSTHROUGH_ARGS+=("$1")
      shift
      ;;
  esac
done

if [[ ! -f "${ENV_FILE}" ]]; then
  echo "Error: env file not found: ${ENV_FILE}" >&2
  echo "Create .env in the current directory, copy .env.TEMPLATE, or set CFCT_ENV_FILE/use --env-file with an existing file." >&2
  exit 1
fi

TABLE_ARGS=()
if [[ -n "${TABLES_FILE}" ]]; then
  if [[ ! -f "${TABLES_FILE}" ]]; then
    echo "Error: tables file not found: ${TABLES_FILE}" >&2
    echo "Set CFCT_TABLES_FILE to an existing table list file or pass --tables-file explicitly." >&2
    exit 1
  fi
  TABLE_ARGS=(--tables-file "${TABLES_FILE}")
fi

if [[ ! -f "${CLI_JAR}" ]]; then
  echo "Error: CLI jar not found: ${CLI_JAR}" >&2
  echo "Build it first with:" >&2
  echo "  ${BUILD_COMMAND}" >&2
  echo "Or set CFCT_CLI_JAR to an existing executable jar." >&2
  exit 1
fi

exec java -jar "${CLI_JAR}" \
  --env-file "${ENV_FILE}" \
  "${TABLE_ARGS[@]}" \
  "${PASSTHROUGH_ARGS[@]}"
