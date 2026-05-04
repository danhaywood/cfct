#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

"${SCRIPT_DIR}/fixture-sqlserver.sh" start

cd "${REPO_ROOT}"
exec mvn -pl cfct-webapp -am spring-boot:run \
  -Dspring-boot.run.arguments="--spring.datasource.url=jdbc:sqlserver://localhost:14333;encrypt=false;trustServerCertificate=true --spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver --spring.datasource.username=sa --spring.datasource.password=Str0ng_password!123 --cfct.webapp.comparison.left-database=left_db --cfct.webapp.comparison.right-database=right_db"
