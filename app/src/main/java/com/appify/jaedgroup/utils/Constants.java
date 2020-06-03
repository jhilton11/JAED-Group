package com.appify.jaedgroup.utils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public final class Constants {

    public static final String ESTATE_EXTRA = "estate";

    public static final int PICK_IMAGE = 1;

    public static final String NAIRA = "₦";

    public static String getCurrencySymbol() {//NumberFormat format = NumberFormat.getCurrencyInstance(Locale.);

        String currency = "₦";

        return currency;
    }

    public static int[] getMonths() {
        int[] months = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        return months;
    }

    public static int[] getYears() {
        int[] years = new int[20];

        for (int i=0; i<19; i++) {
            years[i] = 2021 + i;
        }
        return years;
    }
}
