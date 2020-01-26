package com.appify.jaedgroup.utils;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public final class Constants {

    public static final String ESTATE_EXTRA = "estate";

    public static final int PICK_IMAGE = 1;

    public static String getCurrencySymbol() {//NumberFormat format = NumberFormat.getCurrencyInstance(Locale.);

        String currency = Currency.getInstance(Locale.getDefault()).getSymbol();

        return currency;
    }
}
