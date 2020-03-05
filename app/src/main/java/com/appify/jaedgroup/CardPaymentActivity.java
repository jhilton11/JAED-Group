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
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.appify.jaedgroup.model.EstateTransaction;
import com.appify.jaedgroup.services.PaystackService;
import com.appify.jaedgroup.utils.RetrofitInstance;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class CardPaymentActivity extends AppCompatActivity {
    private CardForm cardForm;
    private ProgressDialog dialog;
    private co.paystack.android.model.Card pCard;
    private EstateTransaction trans;

    private int amount;

    private static final String backend_url = "https://jaed-test-app.herokuapp.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_payment);

        trans = (EstateTransaction) getIntent().getSerializableExtra("transaction");
        if (trans != null) {
            amount = trans.getAmountPaid();
        }

        PaystackSdk.initialize(getApplicationContext());

        cardForm = findViewById(R.id.card_form);
        dialog = new ProgressDialog(this);

        TextView amountTv = (TextView) (cardForm.getRootView().findViewById(R.id.payment_amount));
        amountTv.setText(String.valueOf(amount));

        Button amtBtn = (Button) (cardForm.getRootView().findViewById(R.id.btn_pay));
        amtBtn.setText("Pay #" + String.valueOf(amount));

//        amtBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                performTest();
//            }
//        });

        cardForm.setPayBtnClickListner(new OnPayBtnClickListner() {
            @Override
            public void onClick(com.craftman.cardform.Card card) {
//                String cardNumber = card.getNumber();
//                int expiryMonth = card.getExpMonth();
//                int expiryYear = card.getExpYear();
//                String cvv = card.getCVC();
//
//                pCard = new co.paystack.android.model.Card(cardNumber, expiryMonth, expiryYear, cvv);
//                performCharge(pCard);
                performTest();
            }
        });
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
                Toast.makeText(CardPaymentActivity.this, "Congrats payment is successful", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                trans.setTransactionStatus("Successful");
                sendTransactionToDatabase();
            } else {
                Toast.makeText(CardPaymentActivity.this, "Sorry payment failed", Toast.LENGTH_SHORT).show();
                trans.setTransactionStatus("Failed");
                sendTransactionToDatabase();
                dialog.dismiss();
            }

            if (error != null) {
                Log.e("Error: ", error);
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

    private void sendTransactionToDatabase() {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("estatetransaction").document();
        docRef.set(trans).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CardPaymentActivity.this, "Transaction successfully added", Toast.LENGTH_SHORT).show();
                    Log.d(getClass().getSimpleName(), "Estate successfully added");
                    startActivity(new Intent(CardPaymentActivity.this, TransactionResultActivity.class));
                } else {
                    Log.d(getClass().getSimpleName(), "Unable to add estate to database");
                }
            }
        });
    }
}
