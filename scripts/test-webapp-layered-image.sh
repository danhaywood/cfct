#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
IMAGE_NAME="cfct-webapp:layered-local"

cd "${REPO_ROOT}"

mvn -pl cfct-webapp -am package -DskipTests

java -Djarmode=layertools -jar cfct-webapp/target/cfct-webapp-0.0.1-SNAPSHOT.jar list > /dev/null

docker build -f cfct-webapp/Dockerfile -t "${IMAGE_NAME}" .

CID="$(docker run -d -p 18080:8080 "${IMAGE_NAME}")"
cleanup() {
  docker rm -f "${CID}" > /dev/null 2>&1 || true
}
trap cleanup EXIT

for _ in {1..45}; do
  if [[ "$(docker inspect -f '{{.State.Running}}' "${CID}" 2>/dev/null || echo false)" != "true" ]]; then
    echo "Layered image smoke test failed: container exited unexpectedly" >&2
    docker logs "${CID}" >&2 || true
    exit 1
  fi

  if docker logs "${CID}" 2>&1 | grep -q "Started SqlComparerWebApplication"; then
    echo "Layered image smoke test passed"
    exit 0
  fi

  sleep 2
done

echo "Layered image smoke test failed: startup log marker not observed within timeout" >&2
docker logs "${CID}" >&2 || true
exit 1
