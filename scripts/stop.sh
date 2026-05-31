#!/usr/bin/env bash
set -euo pipefail

APP_DIR="${APP_DIR:-/home/ubuntu/app}"
PID_FILE="${PID_FILE:-${APP_DIR}/app.pid}"
SERVICE_NAME="${SERVICE_NAME:-kinoton}"

stop_systemd_service() {
  if ! command -v systemctl >/dev/null 2>&1; then
    return
  fi

  if ! systemctl list-unit-files "${SERVICE_NAME}.service" >/dev/null 2>&1; then
    return
  fi

  if systemctl is-active --quiet "${SERVICE_NAME}.service"; then
    systemctl stop "${SERVICE_NAME}.service" || true
  fi

  for _ in $(seq 1 30); do
    if ! systemctl is-active --quiet "${SERVICE_NAME}.service"; then
      echo "Application service stopped: ${SERVICE_NAME}.service"
      rm -f "${PID_FILE}"
      return
    fi

    sleep 1
  done

  echo "Application service did not stop cleanly: ${SERVICE_NAME}.service" >&2
  systemctl kill "${SERVICE_NAME}.service" || true
  systemctl reset-failed "${SERVICE_NAME}.service" || true
  rm -f "${PID_FILE}"
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
    echo "Application is not running."
    return
  fi

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
      echo "Legacy application process stopped."
      return
    fi

    sleep 1
  done

  kill -9 ${pids} 2>/dev/null || true
  rm -f "${PID_FILE}"
  echo "Legacy application process force stopped."
}

stop_systemd_service
stop_legacy_processes
