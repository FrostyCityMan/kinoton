(() => {
    const allowedUsersSection = document.querySelector("[data-allowed-users-section]");
    const securityLevelInputs = document.querySelectorAll("input[name='securityLevel']");

    if (!allowedUsersSection || securityLevelInputs.length === 0) {
        return;
    }

    const allowedUserCheckboxes = allowedUsersSection.querySelectorAll("input[type='checkbox']");

    const syncAllowedUsersVisibility = () => {
        const selectedSecurityLevel = Array.from(securityLevelInputs)
            .find((input) => input.checked)
            ?.value;
        const isConfidential = selectedSecurityLevel === "CONFIDENTIAL";

        allowedUsersSection.hidden = !isConfidential;
        allowedUserCheckboxes.forEach((checkbox) => {
            checkbox.disabled = !isConfidential;
        });
    };

    securityLevelInputs.forEach((input) => {
        input.addEventListener("change", syncAllowedUsersVisibility);
    });
    syncAllowedUsersVisibility();
})();
