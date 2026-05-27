(function () {
    const rows = document.getElementById("stageRows");
    const template = document.getElementById("stageRowTemplate");
    const addButton = document.getElementById("addStageButton");

    if (!rows || !template || !addButton) {
        return;
    }

    function updateSummary(row) {
        const probabilityInput = row.querySelector('[data-stage-field="probability"]');
        const chip = row.querySelector(".summary-chip");

        if (!probabilityInput || !chip) {
            return;
        }

        const probability = Number(probabilityInput.value || 0);
        chip.textContent = probability >= 90 ? "확정매출" : "기대매출";
    }

    function refreshNames() {
        rows.querySelectorAll(".stage-row").forEach(function (row, index) {
            row.querySelectorAll("[data-stage-field]").forEach(function (input) {
                input.name = "stages[" + index + "]." + input.dataset.stageField;
                input.id = "stages" + index + "." + input.dataset.stageField;
            });
            updateSummary(row);
        });
    }

    rows.addEventListener("click", function (event) {
        const button = event.target.closest("[data-remove-stage]");
        if (!button) {
            return;
        }

        const activeRows = rows.querySelectorAll(".stage-row");
        if (activeRows.length <= 1) {
            return;
        }

        button.closest(".stage-row").remove();
        refreshNames();
    });

    rows.addEventListener("input", function (event) {
        if (!event.target.matches('[data-stage-field="probability"]')) {
            return;
        }

        updateSummary(event.target.closest(".stage-row"));
    });

    addButton.addEventListener("click", function () {
        const fragment = template.content.cloneNode(true);
        rows.appendChild(fragment);
        refreshNames();
    });
})();
