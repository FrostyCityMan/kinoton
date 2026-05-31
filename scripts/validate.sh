#!/usr/bin/env bash
set -euo pipefail

APP_DIR="${APP_DIR:-/home/ubuntu/app}"
ENV_FILE="${ENV_FILE:-${APP_DIR}/app.env}"
LOG_FILE="${LOG_FILE:-${APP_DIR}/app.log}"
SERVICE_NAME="${SERVICE_NAME:-kinoton}"

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
HEALTH_OUTPUT="/tmp/${SERVICE_NAME}-health.json"

for _ in $(seq 1 60); do
  if systemctl is-active --quiet "${SERVICE_NAME}.service"; then
    if curl -fsS "${HEALTH_URL}" > "${HEALTH_OUTPUT}" 2>/dev/null; then
      if grep -q '"status":"UP"' "${HEALTH_OUTPUT}"; then
        echo "Health check passed: ${HEALTH_URL}"
        systemctl status "${SERVICE_NAME}.service" --no-pager
        exit 0
      fi
    fi
  else
    echo "Application service is not active: ${SERVICE_NAME}.service" >&2
    systemctl status "${SERVICE_NAME}.service" --no-pager >&2 || true
    journalctl -u "${SERVICE_NAME}.service" -n 120 --no-pager >&2 || true
    tail -n 120 "${LOG_FILE}" >&2 2>/dev/null || true
    exit 1
  fi

  sleep 2
done

echo "Health check failed: ${HEALTH_URL}" >&2
cat "${HEALTH_OUTPUT}" >&2 2>/dev/null || true
systemctl status "${SERVICE_NAME}.service" --no-pager >&2 || true
journalctl -u "${SERVICE_NAME}.service" -n 120 --no-pager >&2 || true
tail -n 120 "${LOG_FILE}" >&2 2>/dev/null || true
exit 1
