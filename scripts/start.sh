#!/usr/bin/env bash
set -euo pipefail

APP_DIR="${APP_DIR:-/home/ubuntu/app}"
APP_JAR="${APP_JAR:-${APP_DIR}/app.jar}"
ENV_FILE="${ENV_FILE:-${APP_DIR}/app.env}"
LOG_DIR="${LOG_DIR:-${APP_DIR}/logs}"
LOG_FILE="${LOG_FILE:-${APP_DIR}/app.log}"
PID_FILE="${PID_FILE:-${APP_DIR}/app.pid}"

mkdir -p "${LOG_DIR}"

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

if [[ ! -f "${APP_JAR}" ]]; then
  echo "Application jar not found: ${APP_JAR}" >&2
  exit 1
fi

if [[ -f "${ENV_FILE}" ]]; then
  load_env_file
fi

if [[ -f "${PID_FILE}" ]]; then
  OLD_PID="$(cat "${PID_FILE}")"
  if [[ -n "${OLD_PID}" ]] && kill -0 "${OLD_PID}" 2>/dev/null; then
    echo "Application is already running with PID ${OLD_PID}." >&2
    exit 1
  fi
fi

nohup java ${JAVA_OPTS:-} -jar "${APP_JAR}" ${APP_ARGS:-} > "${LOG_FILE}" 2>&1 &
APP_PID="$!"
echo "${APP_PID}" > "${PID_FILE}"

sleep 5

if ! kill -0 "${APP_PID}" 2>/dev/null; then
  echo "Application failed to start." >&2
  tail -n 100 "${LOG_FILE}" >&2 || true
  exit 1
fi

echo "Application started with PID ${APP_PID}."
