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

    private InvestmentTransaction trans;

    private static final String backend_url = "https://jaed-test-app.herokuapp.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_investment);

        PaystackSdk.initialize(this);
        trans = (InvestmentTransaction) getIntent().getSerializableExtra("trans");

        cardNumberEt = findViewById(R.id.card_number_et);
        expiryEt = findViewById(R.id.card_expiry_et);
        cvvEt = findViewById(R.id.card_cvv);
        totalTv = findViewById(R.id.total_tv);
        payBtn = findViewById(R.id.pay_btn);
        layout = findViewById(R.id.layout);
        dialog = new ProgressDialog(this);

        formatInputs();
        totalTv.setText("Total: " + String.valueOf(trans.getAmountPaid()));
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePayment();
            }
        });
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

    private boolean verifyInput() {
        if (TextUtils.isEmpty(cardNumberEt.getText().toString())) {
            return false;
        } else if (TextUtils.isEmpty(cvvEt.getText().toString())) {
            return false;
        } else if (TextUtils.isEmpty(expiryEt.getText().toString()) && expiryEt.getText().length() <4) {
            return false;
        }

        String cardnumber = cardNumberEt.getText().toString().trim();
        int expiryMonth = Integer.parseInt(expiryEt.getText().toString().trim().substring(0,1));
        int expiryYear = Integer.parseInt(expiryEt.getText().toString().trim().substring(2,3));
        String cvv = cvvEt.getText().toString().trim();
        Card card = new Card(cardnumber, expiryMonth, expiryYear, cvv);

        if (card.isValid()) {
            return true;
        } else {
            Toast.makeText(this, "Card is not valid", Toast.LENGTH_SHORT).show();;
            return false;
        }
    }

    private void makePayment() {
        if (true) {
            dialog.show();
            performCharge(getTestCard());
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

            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                trans.setReference(transaction.getReference());
                dialog.dismiss();
                Toast.makeText(PayInvestmentActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
        cardNumberEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int pos = 0;
                char space = ' ';
                while (true) {
                    if (pos >= editable.length()) break;
                    if (space == editable.charAt(pos) && (((pos + 1) % 5) != 0 || pos + 1 == editable.length())) {
                        editable.delete(pos, pos + 1);
                    } else {
                        pos++;
                    }
                }

                // Insert char where needed.
                pos = 4;
                while (true) {
                    if (pos >= editable.length()) break;
                    final char c = editable.charAt(pos);
                    // Only if its a digit where there should be a space we insert a space
                    if ("0123456789".indexOf(c) >= 0) {
                        editable.insert(pos, "" + space);
                    }
                    pos += 5;
                }
            }
        });
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
                Toast.makeText(PayInvestmentActivity.this, "Sorry payment failed", Toast.LENGTH_SHORT).show();
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
                URL url = new URL(backend_url + "/verify/" + this.reference);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                url.openStream()));

                String inputLine;
                inputLine = in.readLine();
                in.close();
                return inputLine;
            } catch (Exception e) {
                error = e.getClass().getSimpleName() + ": " + e.getMessage();
            }
            return null;
        }
    }
}
