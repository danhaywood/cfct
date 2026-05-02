#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

if ! command -v docker >/dev/null 2>&1; then
  echo "Error: docker is required for Playwright connectivity-status tests." >&2
  exit 1
fi

cd "${REPO_ROOT}"

echo "Running headless Playwright connectivity-status tests..."
mvn -pl cfct-webapp -am \
  -Dplaywright=true \
  -Dtest=HomePageConnectionStatusPlaywrightSuccessTest,HomePageConnectionStatusPlaywrightFailureTest \
  -Dsurefire.failIfNoSpecifiedTests=false \
  test
