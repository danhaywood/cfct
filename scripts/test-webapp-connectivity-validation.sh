#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

if ! command -v docker >/dev/null 2>&1; then
  echo "Error: docker is required for webapp connectivity-validation tests." >&2
  exit 1
fi

cd "${REPO_ROOT}"

echo "Running webapp connectivity-validation tests (Testcontainers)..."
mvn -pl cfct-webapp -am \
  -Dtest=SqlServerConnectivityValidationServiceContainerTest \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test
