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
import com.appify.jaedgroup.model.OptionType;
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

    private Map<String, Integer> prices;
    private String id, estateType;
    private int price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        id = EstateTransaction.getInstance().getEstateId();

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
                EstateTransaction.getInstance().setEstateType(estateType);

                price = prices.get(estateType);
                priceTv.setText("Price: N" + price);

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
        if (true) {
            dialog.show();
            CollectionReference reference = FirebaseFirestore.getInstance().collection("options");
            reference.whereEqualTo("id", "jrZlc134NNvgHiH1zrEA").orderBy("price", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        prices = new HashMap<>();
                        ArrayList<OptionType> arrayList = new ArrayList<>();
                        for (DocumentSnapshot snapshot: queryDocumentSnapshots.getDocuments()) {
                            OptionType type = snapshot.toObject(OptionType.class);
                            arrayList.add(type);
                            prices.put(type.getType(), type.getPrice());
                        }
                        populateRadioGroup(arrayList);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(OrderSummaryActivity.this, "No data loaded", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
        } else {
            Log.d(getClass().getSimpleName(), "The value of id is null");
        }
    }

    private void populateRadioGroup(ArrayList<OptionType> types) {
        for (OptionType type: types) {
            RadioButton rb = new RadioButton(this);
            rb.setText(type.getType());
            radioGroup.addView(rb);
        }
    }

    private void goToPayment() {
        Intent intent = new Intent(this, CardPaymentActivity.class);
        intent.putExtra("price", price);
        startActivity(intent);
    }
}
