#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

"${SCRIPT_DIR}/fixture-sqlserver.sh" start

cd "${REPO_ROOT}"
exec mvn -pl cfct-webapp -am spring-boot:run \
  -Dspring-boot.run.arguments="--cfct.webapp.comparison.connection.server=localhost:14333 --cfct.webapp.comparison.connection.username=sa --cfct.webapp.comparison.connection.password=Str0ng_password!123 --cfct.webapp.comparison.connection.left-database=left_db --cfct.webapp.comparison.connection.right-database=right_db"
