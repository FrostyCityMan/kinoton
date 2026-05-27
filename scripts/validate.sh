#!/usr/bin/env bash
set -euo pipefail

APP_DIR="${APP_DIR:-/home/ubuntu/app}"
ENV_FILE="${ENV_FILE:-${APP_DIR}/app.env}"
LOG_FILE="${LOG_FILE:-${APP_DIR}/app.log}"

if [[ -f "${ENV_FILE}" ]]; then
  set -a
  # shellcheck disable=SC1090
  source "${ENV_FILE}"
  set +a
fi

SERVER_PORT="${SERVER_PORT:-8080}"
HEALTH_PATH="${HEALTH_PATH:-/actuator/health}"
HEALTH_URL="${HEALTH_URL:-http://127.0.0.1:${SERVER_PORT}${HEALTH_PATH}}"
HEALTH_OUTPUT="/tmp/kinoton-health.json"

for _ in $(seq 1 60); do
  if curl -fsS "${HEALTH_URL}" > "${HEALTH_OUTPUT}" 2>/dev/null; then
    if grep -q '"status":"UP"' "${HEALTH_OUTPUT}"; then
      echo "Health check passed: ${HEALTH_URL}"
      exit 0
    fi
  fi

  sleep 2
done

echo "Health check failed: ${HEALTH_URL}" >&2
cat "${HEALTH_OUTPUT}" >&2 2>/dev/null || true
tail -n 100 "${LOG_FILE}" >&2 2>/dev/null || true
exit 1
