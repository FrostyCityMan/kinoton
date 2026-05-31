#!/usr/bin/env bash
set -euo pipefail

APP_USER="${APP_USER:-ubuntu}"
APP_GROUP="${APP_GROUP:-ubuntu}"
APP_DIR="${APP_DIR:-/home/ubuntu/app}"
APP_JAR="${APP_JAR:-${APP_DIR}/app.jar}"
ENV_FILE="${ENV_FILE:-${APP_DIR}/app.env}"
LOG_DIR="${LOG_DIR:-${APP_DIR}/logs}"
LOG_FILE="${LOG_FILE:-${APP_DIR}/app.log}"
PID_FILE="${PID_FILE:-${APP_DIR}/app.pid}"
SERVICE_NAME="${SERVICE_NAME:-kinoton}"
SERVICE_FILE="/etc/systemd/system/${SERVICE_NAME}.service"

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

require_root() {
  if [[ "${EUID}" -ne 0 ]]; then
    echo "start.sh must run as root because it installs and restarts a systemd service." >&2
    exit 1
  fi
}

require_env() {
  local name="$1"
  local value="${!name:-}"

  if [[ -z "${value}" ]]; then
    echo "Required environment variable is missing or empty: ${name}" >&2
    exit 1
  fi
}

stop_legacy_processes() {
  local pids=""

  if [[ -f "${PID_FILE}" ]]; then
    local pid
    pid="$(cat "${PID_FILE}")"
    if [[ -n "${pid}" ]] && kill -0 "${pid}" 2>/dev/null; then
      pids="${pid}"
    fi
  fi

  if [[ -z "${pids}" ]]; then
    pids="$(pgrep -f "java .*${APP_DIR}/app.jar" || true)"
  fi

  if [[ -z "${pids}" ]]; then
    pids="$(pgrep -f "java .*${APP_DIR}/kinoton.jar" || true)"
  fi

  if [[ -z "${pids}" ]]; then
    rm -f "${PID_FILE}"
    return
  fi

  echo "Stopping legacy java process: ${pids}"
  kill ${pids} 2>/dev/null || true

  for _ in $(seq 1 30); do
    local running=""
    for pid in ${pids}; do
      if kill -0 "${pid}" 2>/dev/null; then
        running="true"
      fi
    done

    if [[ -z "${running}" ]]; then
      rm -f "${PID_FILE}"
      return
    fi

    sleep 1
  done

  kill -9 ${pids} 2>/dev/null || true
  rm -f "${PID_FILE}"
}

install_service_file() {
  cat > "${SERVICE_FILE}" <<SERVICE
[Unit]
Description=Kinoton Sales Management Application
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
User=${APP_USER}
Group=${APP_GROUP}
WorkingDirectory=${APP_DIR}
Environment="APP_DIR=${APP_DIR}"
Environment="APP_JAR=${APP_JAR}"
EnvironmentFile=-${ENV_FILE}
ExecStart=/usr/bin/env bash -lc 'exec "\${JAVA_BIN:-/usr/bin/java}" \${JAVA_OPTS:-} -jar "\${APP_JAR}" \${APP_ARGS:-}'
SuccessExitStatus=143
Restart=always
RestartSec=10
StartLimitIntervalSec=300
StartLimitBurst=10
StandardOutput=append:${LOG_FILE}
StandardError=append:${LOG_FILE}
SyslogIdentifier=${SERVICE_NAME}

[Install]
WantedBy=multi-user.target
SERVICE
}

require_root

if [[ ! -f "${APP_JAR}" ]]; then
  echo "Application jar not found: ${APP_JAR}" >&2
  exit 1
fi

if [[ -f "${ENV_FILE}" ]]; then
  load_env_file
fi

require_env "DB_URL"
require_env "DB_USERNAME"
require_env "DB_PASSWORD"

install -d -o "${APP_USER}" -g "${APP_GROUP}" "${APP_DIR}" "${LOG_DIR}"
touch "${LOG_FILE}"
chown "${APP_USER}:${APP_GROUP}" "${LOG_FILE}"

stop_legacy_processes
install_service_file

systemctl daemon-reload
systemctl enable "${SERVICE_NAME}.service"
systemctl reset-failed "${SERVICE_NAME}.service" || true
systemctl restart "${SERVICE_NAME}.service"

sleep 5

if ! systemctl is-active --quiet "${SERVICE_NAME}.service"; then
  echo "Application service failed to start: ${SERVICE_NAME}.service" >&2
  systemctl status "${SERVICE_NAME}.service" --no-pager >&2 || true
  journalctl -u "${SERVICE_NAME}.service" -n 120 --no-pager >&2 || true
  tail -n 120 "${LOG_FILE}" >&2 2>/dev/null || true
  exit 1
fi

systemctl status "${SERVICE_NAME}.service" --no-pager
echo "Application service started: ${SERVICE_NAME}.service"
