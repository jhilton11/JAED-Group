package com.appify.jaedgroup.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Layout;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import retrofit2.Retrofit;

public final class tasks {

    public static void makeSnackbar(View layout, String text) {
        Snackbar.make(layout, text, Snackbar.LENGTH_LONG).show();
    }

    public static boolean validateEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static boolean checkNetworkStatus(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        if (manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
        manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }

    public static void displayAlertDialog(Context context, String title, String msg) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    public static String getCurrencyString(int amount) {
        String stringMoney;

        stringMoney = NumberFormat.getNumberInstance(Locale.getDefault()).format(amount);

        return stringMoney;
    }

    public static String getCurrencyString(double amount) {
        String stringMoney;

        stringMoney = NumberFormat.getNumberInstance(Locale.getDefault()).format(amount);

        return stringMoney;
    }

    public static String getCurrencyString(String amount) {
        double amt = Double.parseDouble(amount);

        return getCurrencyString(amt);
    }
}
