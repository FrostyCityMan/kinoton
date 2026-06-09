document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("form[data-confirm-message]").forEach((form) => {
        form.addEventListener("submit", (event) => {
            const message = form.dataset.confirmMessage || "처리하시겠습니까?";
            if (!window.confirm(message)) {
                event.preventDefault();
            }
        });
    });
});
