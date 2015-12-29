package com.webonise.urbanfarmers.utilities;

public class DecimalFormattedString {

    public static String getTwoDecimalFormattedString(String stringToFormat) {
        return String.format("%.2f", Double.valueOf(stringToFormat));
    }
}
