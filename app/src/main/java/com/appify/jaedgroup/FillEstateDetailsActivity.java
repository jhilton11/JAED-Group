package com.appify.jaedgroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.appify.jaedgroup.utils.UtilObjects;

public class FillEstateDetailsActivity extends AppCompatActivity {
    private Button payBtn;
    private EditText nameEt, ageEt, phoneNoEt, addressEt;
    private View layout;
    private ActionBar toolbar;

    private String name, age, address, phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_estate_details);

        layout = findViewById(R.id.layout);
        nameEt = findViewById(R.id.name_et);
        ageEt = findViewById(R.id.age_et);
        addressEt = findViewById(R.id.address_et);
        phoneNoEt = findViewById(R.id.phone_no_et);

        payBtn = findViewById(R.id.pay_btn);

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payforEstate();
            }
        });

        toolbar = getSupportActionBar();
        toolbar.setTitle("Enter your details");
        toolbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setHomeButtonEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void payforEstate() {
        if (validateInput()) {
            if (UtilObjects.checkNetworkStatus(this)) {
                name = nameEt.getText().toString();
                age = ageEt.getText().toString();
                phoneNo = phoneNoEt.getText().toString();
                address = addressEt.getText().toString();
                Intent intent = new Intent(this, CardPaymentActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("age", age);
                intent.putExtra("phoneNo", phoneNo);
                intent.putExtra("address", address);
                startActivity(intent);
            } else {
                UtilObjects.makeSnackbar(layout, "No Internet Connection. Pls check network and try again.");
            }
        } else {
            UtilObjects.makeSnackbar(layout, "One of the fields has not been properly filled");
        }
    }

    //TODO: Validate input for the edittext fields
    private boolean validateInput() {
        if (nameEt.getText().toString().trim().length()<1) {
            return false;
        } else if (ageEt.getText().toString().trim().length()<1) {
            return false;
        } else if (phoneNoEt.getText().toString().trim().length()<1) {
            return false;
        } else if (addressEt.getText().toString().trim().length()<1) {
            return false;
        }
        return true;
    }
}
