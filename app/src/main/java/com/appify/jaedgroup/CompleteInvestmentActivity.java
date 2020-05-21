package com.appify.jaedgroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appify.jaedgroup.model.Investment;
import com.appify.jaedgroup.model.InvestmentTransaction;
import com.appify.jaedgroup.utils.Constants;
import com.appify.jaedgroup.utils.tasks;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CompleteInvestmentActivity extends AppCompatActivity {
    private TextView totalTv, infoTv, todayDateTv, futureDateTv;
    private EditText amountTv;
    private Button payBtn;
    private View layout;
    private double amount;
    private static String NAIRA = Constants.getCurrencySymbol();
    private InvestmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_investment);

        totalTv = findViewById(R.id.total_tv);
        infoTv = findViewById(R.id.info_tv);
        amountTv = findViewById(R.id.amount_et);
        todayDateTv = findViewById(R.id.today_date_tv);
        futureDateTv = findViewById(R.id.future_date_tv);
        layout = findViewById(R.id.layout);
        payBtn = findViewById(R.id.pay_btn);

        transaction = (InvestmentTransaction)getIntent().getSerializableExtra("transaction");

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePayment();
            }
        });

        amountTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()>0) {
                    amount = Double.parseDouble(amountTv.getText().toString().trim());
                    double finalAmount = 1.2 * amount;
                    totalTv.setText("The return on your investment is " + NAIRA + tasks.getCurrencyString(finalAmount));
                }
            }
        });
    }

    private void makePayment() {
        amount = Double.parseDouble(amountTv.getText().toString().trim());

        if (amount>=200000) {
            transaction.setAmountPaid(amount);
            Intent intent = new Intent(CompleteInvestmentActivity.this, PayInvestmentActivity.class);
            intent.putExtra("transaction", transaction);
            startActivity(intent);
        } else {
            tasks.makeSnackbar(layout, "The minimum amount of you can invest is #200,000");
        }
    }
}
