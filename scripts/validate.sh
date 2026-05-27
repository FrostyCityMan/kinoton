#!/usr/bin/env bash
set -euo pipefail

APP_DIR="${APP_DIR:-/home/ubuntu/app}"
ENV_FILE="${ENV_FILE:-${APP_DIR}/app.env}"
LOG_FILE="${LOG_FILE:-${APP_DIR}/app.log}"
PID_FILE="${PID_FILE:-${APP_DIR}/app.pid}"

load_env_file() {
  while IFS= read -r line || [[ -n "${line}" ]]; do
    line="${line%$'\r'}"

    if [[ "${line}" =~ ^[[:space:]]*$ || "${line}" =~ ^[[:space:]]*# ]]; then
      continue
    fi

    line="${line#export }"

    if [[ "${line}" =~ ^[A-Za-z_][A-Za-z0-9_]*= ]]; then
      export "${line}"
    else
      echo "Ignoring invalid env line in ${ENV_FILE}: ${line}" >&2
    fi
  done < "${ENV_FILE}"
}

if [[ -f "${ENV_FILE}" ]]; then
  load_env_file
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

  if [[ -f "${PID_FILE}" ]]; then
    APP_PID="$(cat "${PID_FILE}")"
    if [[ -n "${APP_PID}" ]] && ! kill -0 "${APP_PID}" 2>/dev/null; then
      echo "Application process is not running. PID file: ${PID_FILE}" >&2
      tail -n 120 "${LOG_FILE}" >&2 2>/dev/null || true
      exit 1
    fi
  fi

  sleep 2
done

echo "Health check failed: ${HEALTH_URL}" >&2
cat "${HEALTH_OUTPUT}" >&2 2>/dev/null || true
tail -n 100 "${LOG_FILE}" >&2 2>/dev/null || true
exit 1
