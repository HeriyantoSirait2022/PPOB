package com.qdi.rajapay.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class NumberUtils {

    private static DecimalFormat getFormatter() {
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setDecimalSeparator(',');
        formatSymbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#,###,###",formatSymbols);
        return formatter;
    }

    public static String format(float number) {
        return getFormatter().format(number);
    }

    public static String format(double number) {
        return getFormatter().format(number);
    }

    public static String format(int number) {
        return getFormatter().format(number);
    }

    public static String format(long number) {
        return getFormatter().format(number);
    }
}
