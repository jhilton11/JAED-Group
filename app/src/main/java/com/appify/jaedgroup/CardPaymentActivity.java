package com.appify.jaedgroup;

import androidx.appcompat.app.AppCompatActivity;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class CardPaymentActivity extends AppCompatActivity {
    private CardForm cardForm;
    private ProgressDialog dialog;
    private co.paystack.android.model.Card pCard;

    private String name, age, address, phoneNo;

    private static final String backend_url = "https://jaed-test-app.herokuapp.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_payment);

        name = getIntent().getStringExtra("name");
        age = getIntent().getStringExtra("age");
        address = getIntent().getStringExtra("address");
        phoneNo = getIntent().getStringExtra("phoneNo");

        PaystackSdk.initialize(getApplicationContext());

        cardForm = findViewById(R.id.card_form);
        dialog = new ProgressDialog(this);

        TextView amount = (TextView) (cardForm.getRootView().findViewById(R.id.payment_amount));
        amount.setText("N500,000");

        Button amtBtn = (Button) (cardForm.getRootView().findViewById(R.id.btn_pay));
        amtBtn.setText("N500,000");

        amtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performTest();
            }
        });

//        cardForm.setPayBtnClickListner(new OnPayBtnClickListner() {
//            @Override
//            public void onClick(Card card) {
//                String cardNumber = card.getNumber();
//                int expiryMonth = card.getExpMonth();
//                int expiryYear = card.getExpYear();
//                String cvv = card.getCVC();
//
//                pCard = new co.paystack.android.model.Card(cardNumber, expiryMonth, expiryYear, cvv);
//                performCharge();
//            }
//        });
    }

    private void performCharge(Card card) {
        //TODO: Set card charge details to the appropriate values
        Charge charge = new Charge();
        charge.setCard(card);
        charge.setPlan("");
        charge.setEmail("scolaguys@yahoo.co.uk");
        charge.setAmount(4000);

        dialog.show();

        PaystackSdk.chargeCard(CardPaymentActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                //TODO: Send Payment reference to the server as transaction is successful;
                new VerifyOnServer().execute(transaction.getReference());
                //TODO: Display transaction successful and exit to new activity
                Toast.makeText(CardPaymentActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void beforeValidate(Transaction transaction) {
                //TODO: Request OTP
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                //TODO: Handle error details here.
                dialog.dismiss();
                Toast.makeText(CardPaymentActivity.this, "Transaction failed. Details:"+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performTest () {
        String cardNumber = "5060666666666666666";
        int expiryMonth = 11;
        int expiryYear = 21;
        String cvv = "123";

        pCard = new co.paystack.android.model.Card(cardNumber, expiryMonth, expiryYear, cvv);
        performCharge(pCard);
    }

    private class VerifyOnServer extends AsyncTask<String, Void, String> {
        private String reference;
        private String error;

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                Toast.makeText(CardPaymentActivity.this, "Congrats payment is successful", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(CardPaymentActivity.this, "Sorry payment failed", Toast.LENGTH_SHORT).show();
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
