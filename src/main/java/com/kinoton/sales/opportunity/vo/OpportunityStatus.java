package com.kinoton.sales.opportunity.vo;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public enum OpportunityStatus {

    IN_PROGRESS("IN_PROGRESS", "진행중", "status-badge--in-progress", false),
    WON("WON", "수주완료", "status-badge--won", false),
    HOLD("HOLD", "보류", "status-badge--hold", true),
    LOST("LOST", "실주", "status-badge--lost", true);

    private final String code;
    private final String label;
    private final String cssClass;
    private final boolean reasonRequired;

    OpportunityStatus(String code, String label, String cssClass, boolean reasonRequired) {
        this.code = code;
        this.label = label;
        this.cssClass = cssClass;
        this.reasonRequired = reasonRequired;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }

    public boolean isReasonRequired() {
        return reasonRequired;
    }

    public static Optional<OpportunityStatus> selectByCode(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        String normalizedCode = code.trim().toUpperCase(Locale.ROOT);
        return Arrays.stream(values())
            .filter(status -> status.code.equals(normalizedCode))
            .findFirst();
    }

    public static String selectLabel(String code) {
        return selectByCode(code)
            .map(OpportunityStatus::getLabel)
            .orElse(code == null ? "" : code);
    }

    public static String selectCssClass(String code) {
        return selectByCode(code)
            .map(OpportunityStatus::getCssClass)
            .orElse("status-badge--unknown");
    }
}
