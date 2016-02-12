package com.webonise.gardenIt.utilities;

public class DecimalFormattedString {

    public static String getTwoDecimalFormattedString(String stringToFormat) {
        return String.format("%.2f", Double.valueOf(stringToFormat));
    }
}
