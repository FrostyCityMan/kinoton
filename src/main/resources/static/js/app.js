(() => {
    const modalState = {
        confirmCallback: null,
        cancelCallback: null,
        lastInvalidAt: 0
    };

    function selectModal() {
        return document.querySelector("[data-feedback-modal]");
    }

    function selectModalElements() {
        const modal = selectModal();
        if (!modal) {
            return null;
        }

        return {
            modal,
            title: modal.querySelector("[data-feedback-modal-title]"),
            message: modal.querySelector("[data-feedback-modal-message]"),
            chip: modal.querySelector("[data-feedback-modal-chip]"),
            confirmButtons: modal.querySelectorAll("[data-feedback-modal-confirm]"),
            cancelButtons: modal.querySelectorAll("[data-feedback-modal-cancel]"),
            dismissButtons: modal.querySelectorAll("[data-feedback-modal-dismiss]")
        };
    }

    function configureModal(options) {
        const elements = selectModalElements();
        if (!elements) {
            return false;
        }

        const type = options.type || "notice";
        elements.title.textContent = options.title || "알림";
        elements.message.textContent = options.message || "";
        elements.chip.textContent = selectChipText(type);
        elements.modal.dataset.feedbackModalType = type;

        elements.confirmButtons.forEach((button) => {
            button.textContent = options.confirmText || "확인";
            button.hidden = options.hideConfirm === true;
        });
        elements.cancelButtons.forEach((button) => {
            button.textContent = options.cancelText || "취소";
            button.hidden = options.hideCancel === true;
        });

        modalState.confirmCallback = options.onConfirm || null;
        modalState.cancelCallback = options.onCancel || null;
        elements.modal.hidden = false;
        document.body.classList.add("modal-open");

        const focusTarget = options.hideConfirm === true
            ? (elements.cancelButtons[0] || elements.dismissButtons[0])
            : elements.confirmButtons[0];
        if (focusTarget) {
            focusTarget.focus();
        }
        return true;
    }

    function selectChipText(type) {
        if (type === "warning") {
            return "경고";
        }
        if (type === "confirm") {
            return "확인";
        }
        return "알림";
    }

    function closeModal(callback) {
        const elements = selectModalElements();
        if (!elements) {
            return;
        }

        elements.modal.hidden = true;
        document.body.classList.remove("modal-open");

        const callbackToRun = callback;
        modalState.confirmCallback = null;
        modalState.cancelCallback = null;
        if (callbackToRun) {
            callbackToRun();
        }
    }

    function showAlert(title, message, type) {
        if (!configureModal({
            title,
            message,
            type,
            confirmText: "확인",
            hideCancel: true
        })) {
            return;
        }
    }

    function showConfirm(message, onConfirm) {
        configureModal({
            title: "처리 확인",
            message,
            type: "confirm",
            confirmText: "확인",
            cancelText: "취소",
            onConfirm
        });
    }

    function selectText(element) {
        return (element.textContent || "").replace(/\s+/g, " ").trim();
    }

    function selectFeedbackMessages(selector) {
        return Array.from(document.querySelectorAll(selector))
            .filter((element) => element.offsetParent !== null || element.getClientRects().length > 0)
            .map(selectText)
            .filter((message) => message.length > 0);
    }

    function showInitialFeedback() {
        const errors = selectFeedbackMessages(".form-error");
        const messages = selectFeedbackMessages(".form-message");
        const targets = Array.from(document.querySelectorAll(".form-error, .form-message"));

        targets.forEach((element) => {
            element.hidden = true;
        });

        if (errors.length > 0) {
            showAlert("입력값 확인", errors.join("\n"), "warning");
            return;
        }
        if (messages.length > 0) {
            showAlert("알림", messages.join("\n"), "notice");
        }
    }

    function selectFieldLabel(field) {
        if (field.id) {
            const label = document.querySelector(`label[for="${field.id}"]`);
            if (label) {
                return selectText(label);
            }
        }
        const parentLabel = field.closest("label");
        if (parentLabel) {
            return selectText(parentLabel.querySelector("span") || parentLabel);
        }
        return "";
    }

    function showValidationModal(event) {
        const field = event.target;
        if (!(field instanceof HTMLInputElement || field instanceof HTMLSelectElement || field instanceof HTMLTextAreaElement)) {
            return;
        }

        event.preventDefault();
        const now = Date.now();
        if (now - modalState.lastInvalidAt < 300) {
            return;
        }
        modalState.lastInvalidAt = now;

        const label = selectFieldLabel(field);
        const message = field.validationMessage || "입력 값을 확인하세요.";
        showAlert("입력값 확인", label ? `${label}: ${message}` : message, "warning");
    }

    function bindConfirmForms() {
        document.querySelectorAll("form[data-confirm-message]").forEach((form) => {
            form.addEventListener("submit", (event) => {
                if (form.dataset.modalConfirmed === "true") {
                    delete form.dataset.modalConfirmed;
                    return;
                }

                event.preventDefault();
                const submitter = event.submitter;
                const message = form.dataset.confirmMessage || "처리하시겠습니까?";
                showConfirm(message, () => {
                    form.dataset.modalConfirmed = "true";
                    if (typeof form.requestSubmit === "function") {
                        form.requestSubmit(submitter || undefined);
                        return;
                    }
                    form.submit();
                });
            });
        });
    }

    function bindReportPeriodForms() {
        document.querySelectorAll("[data-report-filter-form]").forEach((form) => {
            const periodTypeSelect = form.querySelector("[data-report-period-type]");
            const yearField = form.querySelector("[data-report-year-field]");
            const monthField = form.querySelector("[data-report-month-field]");
            const yearSelect = yearField?.querySelector("select");
            const monthSelect = monthField?.querySelector("select");

            if (!periodTypeSelect || !yearField || !monthField || !yearSelect || !monthSelect) {
                return;
            }

            const syncPeriodFields = () => {
                const periodType = periodTypeSelect.value;
                const isAll = periodType === "ALL";
                const isMonthly = periodType === "MONTHLY";

                yearField.hidden = isAll;
                monthField.hidden = !isMonthly;
                yearSelect.disabled = isAll;
                monthSelect.disabled = !isMonthly;
            };

            periodTypeSelect.addEventListener("change", syncPeriodFields);
            form.addEventListener("submit", syncPeriodFields);
            syncPeriodFields();
        });
    }

    document.addEventListener("DOMContentLoaded", () => {
        const elements = selectModalElements();
        if (!elements) {
            return;
        }

        elements.confirmButtons.forEach((button) => {
            button.addEventListener("click", () => closeModal(modalState.confirmCallback));
        });
        elements.cancelButtons.forEach((button) => {
            button.addEventListener("click", () => closeModal(modalState.cancelCallback));
        });
        elements.dismissButtons.forEach((button) => {
            button.addEventListener("click", () => closeModal(modalState.cancelCallback));
        });
        elements.modal.addEventListener("click", (event) => {
            if (event.target === elements.modal) {
                closeModal(modalState.cancelCallback);
            }
        });
        document.addEventListener("keydown", (event) => {
            if (event.key === "Escape" && !elements.modal.hidden) {
                closeModal(modalState.cancelCallback);
            }
        });
        document.addEventListener("invalid", showValidationModal, true);

        bindConfirmForms();
        bindReportPeriodForms();
        showInitialFeedback();
    });

    window.KinotonModal = {
        alert: showAlert,
        confirm: showConfirm
    };
})();
