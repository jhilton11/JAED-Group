package com.appify.jaedgroup.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;

public class TextFormatters {

    class CurrencyEdittextTextWatcher implements TextWatcher {

        private String current = "";
        private EditText editText;

        public CurrencyEdittextTextWatcher(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            if(!s.toString().equals(current)){
               editText.removeTextChangedListener(this);

               String cleanString = s.toString().replaceAll("[#,.]", "");

               double parsed = Double.parseDouble(cleanString);
               String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

               current = formatted;
               editText.setText(formatted);
               editText.setSelection(formatted.length());

               editText.addTextChangedListener(this);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    public static class CardTextWatcher implements TextWatcher {

        private static final char space = ' ';

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Remove spacing char
            if (s.length() > 0 && (s.length() % 5) == 0) {
                final char c = s.charAt(s.length() - 1);
                if (space == c) {
                    s.delete(s.length() - 1, s.length());
                }
            }
            // Insert char where needed.
            if (s.length() > 0 && (s.length() % 5) == 0) {
                char c = s.charAt(s.length() - 1);
                // Only if its a digit where there should be a space we insert a space
                if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                    s.insert(s.length() - 1, String.valueOf(space));
                }
            }
        }
    }

    public static class ExpiryDateTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() > 0 && (editable.length() % 3) == 0) {
                final char c = editable.charAt(editable.length() - 1);
                if ('/' == c) {
                    editable.delete(editable.length() - 1, editable.length());
                }
            }
            if (editable.length() > 0 && (editable.length() % 3) == 0) {
                char c = editable.charAt(editable.length() - 1);
                if (Character.isDigit(c) && TextUtils.split(editable.toString(), String.valueOf("/")).length <= 2) {
                    editable.insert(editable.length() - 1, String.valueOf("/"));
                }
            }
        }
    }

    public static class CVVTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
