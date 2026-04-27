#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

"${SCRIPT_DIR}/fixture-sqlserver.sh" start

cd "${REPO_ROOT}"
exec mvn -pl sqlcomparer-webapp -am spring-boot:run \
  -Dspring-boot.run.arguments="--sqlcomparer.webapp.comparison.connection.server=localhost:14333 --sqlcomparer.webapp.comparison.connection.username=sa --sqlcomparer.webapp.comparison.connection.password=Str0ng_password!123 --sqlcomparer.webapp.comparison.connection.left-database=left_db --sqlcomparer.webapp.comparison.connection.right-database=right_db"
