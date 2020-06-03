package com.appify.jaedgroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appify.jaedgroup.model.InvestmentTransaction;
import com.appify.jaedgroup.utils.TextFormatters;
import com.appify.jaedgroup.utils.tasks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class PayInvestmentActivity extends AppCompatActivity {
    private EditText cardNumberEt, cvvEt, expiryEt;
    private TextView totalTv;
    private Button payBtn;
    private ProgressDialog dialog;
    private View layout;
    private StringBuilder sbError;

    private InvestmentTransaction trans;

    private static final String backend_url = "https://jaed-test-app.herokuapp.com";
    private static final String backend_live_url = "https://jaed-group.herokuapp.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_investment);

        PaystackSdk.initialize(this);
        trans = (InvestmentTransaction) getIntent().getSerializableExtra("transaction");

        cardNumberEt = findViewById(R.id.card_number_et);
        expiryEt = findViewById(R.id.card_expiry_et);
        cvvEt = findViewById(R.id.card_cvv);
        totalTv = findViewById(R.id.total_tv);
        payBtn = findViewById(R.id.pay_btn);
        layout = findViewById(R.id.layout);
        dialog = new ProgressDialog(this);

        formatInputs();
        totalTv.setText("Total: " + tasks.getCurrencyString(trans.getAmountPaid()));
        payBtn.setOnClickListener(view -> makePayment());

        getSupportActionBar().setTitle("Make Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (trans == null) {
            payBtn.setEnabled(false);
            trans.setUserId(FirebaseAuth.getInstance().getUid());
            tasks.makeSnackbar(layout, "Unable to proceed, pls go back and enter your details again");
        } else {
            payBtn.setEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private boolean verifyInput() {
        sbError = new StringBuilder("The following fields have not been filled: ");
        boolean cardValid = true, cvvValid = true, expValid = true;
        if (TextUtils.isEmpty(cardNumberEt.getText().toString().trim())) {
            cardValid = false;
            sbError.append("Card number, ");
        }
        if (TextUtils.isEmpty(cvvEt.getText().toString().trim())) {
            cvvValid = false;
            sbError.append("CVV, ");
        }
        if (TextUtils.isEmpty(expiryEt.getText().toString()) || expiryEt.getText().length() <5) {
            expValid = false;
            sbError.append("Expiry Date, ");
        }

        return cardValid && cvvValid && expValid;
    }

    private void makePayment() {
        if (!tasks.checkNetworkStatus(this)) {
            tasks.makeSnackbar(layout, "There is no Internet connection");
            return;
        }

        if (verifyInput()) {
            String cardNumString = cardNumberEt.getText().toString().trim();
            int month = Integer.parseInt(expiryEt.getText().toString().trim().substring(0, 2));
            int year = 2000 + Integer.parseInt(expiryEt.getText().toString().trim().substring(3,5));
            String cvv = cvvEt.getText().toString().trim();
            Log.d("cardValidity", "Card number: " + cardNumString);
            Log.d("cardValidity", "Expiry month: " + month);
            Log.d("cardValidity", "Expiry year: " + year);
            Log.d("cardValidity", "Card cvv: " + cvv);
            Card tempCard = new Card(cardNumString, month, year, cvv);
            if (tempCard.isValid()) {
                dialog.show();
                performCharge(tempCard);
            } else {
                Toast.makeText(this, "Card is not valid", Toast.LENGTH_LONG).show();
                Log.d("cardValidity", "Card ain't valid");
            }
        } else {
            tasks.displayAlertDialog(PayInvestmentActivity.this,"", sbError.toString());
        }
    }

    private void performCharge(Card card) {
        Charge charge = new Charge();
        charge.setAmount((int) (trans.getAmountPaid() * 100));
        charge.setCard(card);
        charge.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        PaystackSdk.chargeCard(this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                trans.setReference(transaction.getReference());
                new VerifyOnServer().execute(transaction.getReference());
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                trans.setReference(transaction.getReference());
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                Log.d("charge_card_error", error.toString());
                trans.setReference(transaction.getReference());
                dialog.dismiss();
                tasks.displayAlertDialog(PayInvestmentActivity.this,"Charge Error", "Error: " + error.getClass().getSimpleName());
                if (transaction.getReference() != null) {
                    Log.d("onChargeError", transaction.getReference());
                }
            }
        });
    }

    private Card getTestCard() {
        Card testCard = null;

        String cardNumber = "5060666666666666666";
        int expiryMonth = 11;
        int expiryYear = 21;
        String cvv = "123";

        testCard = new Card(cardNumber, expiryMonth, expiryYear, cvv);

        return testCard;
    }

    private void sendTransactionToDatabase() {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("investmentTransaction").document();
        docRef.set(trans).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(PayInvestmentActivity.this, "Transaction successfully added", Toast.LENGTH_SHORT).show();
                    Log.d(getClass().getSimpleName(), "Investment successfully added");
                    startActivity(new Intent(PayInvestmentActivity.this, TransactionResultActivity.class));
                } else {
                    Log.d(getClass().getSimpleName(), "Unable to add investment to database");
                }
            }
        });
    }

    private void formatInputs() {
        cardNumberEt.addTextChangedListener(new TextFormatters.CardTextWatcher());
        expiryEt.addTextChangedListener(new TextFormatters.ExpiryDateTextWatcher());
    }

    private class VerifyOnServer extends AsyncTask<String, Void, String> {
        private String reference;
        private String error;

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                Toast.makeText(PayInvestmentActivity.this, "Congrats payment is successful", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                trans.setTransactionStatus("Successful");
                sendTransactionToDatabase();
            } else {
                Log.d("error", error);
                Toast.makeText(PayInvestmentActivity.this, error, Toast.LENGTH_SHORT).show();
                trans.setTransactionStatus("Failed");
                sendTransactionToDatabase();
                dialog.dismiss();
            }

            if (error != null) {
                Log.e("Error: ", error);
                dialog.dismiss();
            }
        }

        @Override
        protected String doInBackground(String... reference) {
            try {
                this.reference = reference[0];
                URL url = new URL(backend_live_url + "/verify/" + this.reference);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                url.openStream()));

                String inputLine;
                inputLine = in.readLine();
                in.close();
                return inputLine;
            } catch (Exception e) {
                error = e.getClass().getSimpleName() + ": " + e.getMessage();
                Log.d("verify_trans_error", error);
            }
            return null;
        }
    }
}
