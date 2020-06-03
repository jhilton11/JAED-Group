package com.appify.jaedgroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appify.jaedgroup.model.EstateTransaction;
import com.appify.jaedgroup.services.PaystackService;
import com.appify.jaedgroup.utils.Constants;
import com.appify.jaedgroup.utils.RetrofitInstance;
import com.appify.jaedgroup.utils.TextFormatters;
import com.appify.jaedgroup.utils.tasks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class CardPaymentActivity extends AppCompatActivity {
    private EditText cardEt, cardExpEt, cardCvv;
    private Button payBtn;
    private ProgressDialog dialog;
    private EstateTransaction trans;

    private int amount;

    private StringBuilder sbError;
    private static final String backend_url = "https://jaed-test-app.herokuapp.com";
    private static final String backend_live_url = "https://jaed-group.herokuapp.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_payment);

        trans = (EstateTransaction) getIntent().getSerializableExtra("transaction");
        if (trans != null) {
            TextView estateName = findViewById(R.id.estate_name);
            TextView landType = findViewById(R.id.land_type);
            TextView amountTv = findViewById(R.id.total_tv);

            estateName.setText("Estate: " + trans.getEstateName());
            landType.setText("Option selected: " + trans.getEstateType());
            amount = trans.getAmountPaid()/100;
            amountTv.setText("Total: " + Constants.NAIRA + tasks.getCurrencyString(amount));
        }

        cardExpEt = findViewById(R.id.card_expiry_et);
        cardEt = findViewById(R.id.card_number_et);
        cardCvv = findViewById(R.id.card_cvv);
        payBtn = findViewById(R.id.pay_btn);

        formatInputs();

        PaystackSdk.initialize(getApplicationContext());

        dialog = new ProgressDialog(this);

        payBtn.setOnClickListener(view -> {
            if (tasks.checkNetworkStatus(CardPaymentActivity.this)) {
                makePayment();
            }
        });

        getSupportActionBar().setTitle("Make Payment");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void performCharge(Card card) {
        //TODO: Set card charge details to the appropriate values
        Charge charge = new Charge();
        charge.setCard(card);
        charge.setPlan("");
        charge.setEmail(trans.getEmail());
        charge.setAmount(trans.getAmountPaid());

        dialog.show();
        dialog.setCancelable(false);
        dialog.setMessage("Please wait....Do not attempt to cancel trans");

        PaystackSdk.chargeCard(CardPaymentActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                //TODO: Send Payment reference to the server as trans is successful;
                trans.setReference(transaction.getReference());
                new VerifyOnServer().execute(transaction.getReference());
                //verify(trans.getReference());
                //TODO: Display trans successful and exit to new activity
                Toast.makeText(CardPaymentActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                //TODO: Request OTP
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                //TODO: Handle error details here.
                trans.setReference(transaction.getReference());
                dialog.dismiss();
                tasks.displayAlertDialog(CardPaymentActivity.this,"Transaction failed", error.getMessage());
            }
        });
    }

    private boolean verifyInput() {
        sbError = new StringBuilder("The following fields have not been filled: ");
        boolean cardValid = true, cvvValid = true, expValid = true;
        if (TextUtils.isEmpty(cardEt.getText().toString().trim())) {
            cardValid = false;
            sbError.append("Card number, ");
        }
        if (TextUtils.isEmpty(cardCvv.getText().toString().trim())) {
            cvvValid = false;
            sbError.append("CVV, ");
        }
        if (TextUtils.isEmpty(cardExpEt.getText().toString()) || cardExpEt.getText().length() <5) {
            expValid = false;
            sbError.append("Expiry Date, ");
        }

        return cardValid && cvvValid && expValid;
    }

    private void makePayment() {
        if (verifyInput()) {
            int month = Integer.parseInt(cardExpEt.getText().toString().trim().substring(0, 2));
            int year = 2000 + Integer.parseInt(cardExpEt.getText().toString().trim().substring(3,5));
            String cv = cardCvv.getText().toString();
            String cn = cardEt.getText().toString().trim();
            Card testCard = new Card(cn, month, year, cv);
            if (testCard.isValid()) {
                Log.d("test_card", "Card type: " + testCard.getType());
                performCharge(testCard);
            } else {
                Log.d("onClick", "Card not valid");
                Toast.makeText(CardPaymentActivity.this, "Card not valid", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, sbError.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void performTest () {
        String cardNumber = "5060666666666666666";
        int expiryMonth = 11;
        int expiryYear = 21;
        String cvv = "123";

        Card card = new Card(cardNumber, expiryMonth, expiryYear, cvv);
        performCharge(card);
    }

    private void verify(final String reference) {
        PaystackService service = RetrofitInstance.getRetrofitInstance().create(PaystackService.class);

        Call<String> call = service.getJSONString(reference);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (dialog.isShowing()) {
                    dialog.dismiss();

                    if (response.isSuccessful()) {
                        Log.e("Succeeded: ", response.message());
                        Toast.makeText(CardPaymentActivity.this, "Verification is successful", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        Log.e("Error", response.errorBody().toString());
                        Toast.makeText(CardPaymentActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                    }
                }

                Log.d("tag", "JSON String:" + response);
                Toast.makeText(CardPaymentActivity.this, "Response: "+response, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(CardPaymentActivity.this, "Error: "+t.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(CardPaymentActivity.this, "Payment is successful", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                trans.setTransactionStatus("Successful");
                sendTransactionToDatabase();
            } else {
                tasks.displayAlertDialog(CardPaymentActivity.this, "Payment failed", "Reason: " + result);
                Toast.makeText(CardPaymentActivity.this, "Sorry payment failed", Toast.LENGTH_SHORT).show();
                trans.setTransactionStatus("Failed");
                sendTransactionToDatabase();
            }

            if (error != null) {
                Log.e("Error: ", error);
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
            }
            return null;
        }
    }

    private void sendTransactionToDatabase() {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("estatetransaction").document();
        docRef.set(trans).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    //Toast.makeText(CardPaymentActivity.this, "Transaction successfully added", Toast.LENGTH_SHORT).show();
                    Log.d(getClass().getSimpleName(), "Transaction successfully added");
                    startActivity(new Intent(CardPaymentActivity.this, TransactionResultActivity.class));
                } else {
                    dialog.dismiss();
                    Log.d(getClass().getSimpleName(), "Unable to add estate to database");
                }
            }
        });
    }

    private void formatInputs() {
        cardEt.addTextChangedListener(new TextFormatters.CardTextWatcher());
        cardExpEt.addTextChangedListener(new TextFormatters.ExpiryDateTextWatcher());
    }
}
