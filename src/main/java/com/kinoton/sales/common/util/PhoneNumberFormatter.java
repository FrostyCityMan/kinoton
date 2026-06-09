package com.kinoton.sales.common.util;

import org.springframework.util.StringUtils;

public final class PhoneNumberFormatter {

    private PhoneNumberFormatter() {
    }

    public static String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        String trimmed = value.trim();
        String digits = trimmed.replaceAll("[^0-9]", "");
        if (!StringUtils.hasText(digits)) {
            return trimmed;
        }

        if (digits.startsWith("02") && digits.length() == 9) {
            return digits.substring(0, 2) + "-" + digits.substring(2, 5) + "-" + digits.substring(5);
        }
        if (digits.startsWith("02") && digits.length() == 10) {
            return digits.substring(0, 2) + "-" + digits.substring(2, 6) + "-" + digits.substring(6);
        }
        if (digits.length() == 10) {
            return digits.substring(0, 3) + "-" + digits.substring(3, 6) + "-" + digits.substring(6);
        }
        if (digits.length() == 11) {
            return digits.substring(0, 3) + "-" + digits.substring(3, 7) + "-" + digits.substring(7);
        }
        return trimmed;
    }
}
