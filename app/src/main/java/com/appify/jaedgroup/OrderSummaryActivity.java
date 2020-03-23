package com.appify.jaedgroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.appify.jaedgroup.model.EstateTransaction;
import com.appify.jaedgroup.model.EstatesInfo;
import com.appify.jaedgroup.model.OptionType;
import com.appify.jaedgroup.utils.Constants;
import com.appify.jaedgroup.utils.tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderSummaryActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private TextView priceTv;
    private ProgressDialog dialog;
    private Button button;

    private Map<String, String> prices;
    private String id, estateType;
    private int price;
    private static final String naira = "₦";
    private EstateTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        transaction = (EstateTransaction) getIntent().getSerializableExtra("estateTransaction");
        if (transaction != null) {
            id = transaction.getEstateId();
        }

        radioGroup = findViewById(R.id.radioGroup);
        priceTv = findViewById(R.id.price_tv);
        dialog = new ProgressDialog(this);
        button = findViewById(R.id.pay_btn);

        loadData();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = findViewById(i);
                estateType = rb.getText().toString();
                transaction.setEstateType(estateType);

                price = Integer.parseInt(prices.get(estateType));
                priceTv.setText("Price: " + naira + tasks.getCurrencyString(price));

                if (!button.isEnabled()) {
                    button.setEnabled(true);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPayment();
            }
        });

        ActionBar toolbar = getSupportActionBar();
        toolbar.setTitle("Complete Order");
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void loadData() {
        if (tasks.checkNetworkStatus(this)) {
            dialog.show();
            Query reference = FirebaseFirestore.getInstance().collection("estatesInfo");
            reference.whereEqualTo("name", id).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e==null && !queryDocumentSnapshots.isEmpty()) {
                        prices = new HashMap<>();
                        ArrayList<EstatesInfo> arrayList = new ArrayList<>();
                        for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                            EstatesInfo info = snapshot.toObject(EstatesInfo.class);
                            arrayList.add(info);
                            prices.put(info.getSize() + "sq m:\t\t" + naira + tasks.getCurrencyString(info.getPrice()), info.getPrice());
                        }
                        populateRadioGroup(arrayList);
                        dialog.dismiss();
                    } else {
                        if (e!=null) {
                            Log.d(getClass().getSimpleName(), "error: " + e.getMessage());
                        }
                        Toast.makeText(OrderSummaryActivity.this, "No data loaded", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
        } else {
            Log.d(getClass().getSimpleName(), "The value of id is null");
        }
    }

    private void populateRadioGroup(ArrayList<EstatesInfo> infos) {
        for (EstatesInfo info: infos) {
            RadioButton rb = new RadioButton(this);
            rb.setText(info.getSize() + "sq m:\t\t" + naira + tasks.getCurrencyString(info.getPrice()));
            radioGroup.addView(rb);
        }
    }

    private void goToPayment() {
        transaction.setAmountPaid(price * 100);
        Intent intent = new Intent(this, CardPaymentActivity.class);
        intent.putExtra("transaction", transaction);
        startActivity(intent);
    }
}
