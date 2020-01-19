package com.appify.jaedgroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.appify.jaedgroup.model.EstateTransaction;
import com.appify.jaedgroup.utils.tasks;

public class FillDetailsActivity extends AppCompatActivity {
    private Button payBtn;
    private EditText nameEt, occupatonEt, phoneNoEt, addressEt, nextOfKinAddressEt, dateOfBirthEt;
    private RadioGroup maritalStatusRG, purposeBG, alternateBG;
    private View layout;
    private ActionBar toolbar;

    private EstateTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_estate_details);

        layout = findViewById(R.id.layout);
        nameEt = findViewById(R.id.name_et);
        occupatonEt = findViewById(R.id.occupation_et);
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
        String name, age, address, phoneNo, occupation, maritalStatus;
        if (true) {
            if (tasks.checkNetworkStatus(this)) {
//                name = nameEt.getText().toString();
//                age = dateOfBirthEt.getText().toString();
//                phoneNo = phoneNoEt.getText().toString();
//                address = addressEt.getText().toString();
                Intent intent = new Intent(this, OrderSummaryActivity.class);
//                intent.putExtra("name", name);
//                intent.putExtra("age", age);
//                intent.putExtra("phoneNo", phoneNo);
//                intent.putExtra("address", address);
                startActivity(intent);
            } else {
                tasks.makeSnackbar(layout, "No Internet Connection. Pls check network and try again.");
            }
        } else {
            tasks.makeSnackbar(layout, "One of the fields has not been properly filled");
        }
    }

    //TODO: Validate input for the edittext fields
    private boolean validateInput() {
//        if (nameEt.getText().toString().trim().length()<1) {
//            return false;
//        } else if (dateOfBirthEt.getText().toString().trim().length()<1) {
//            return false;
//        } else if (phoneNoEt.getText().toString().trim().length()<1) {
//            return false;
//        } else if (addressEt.getText().toString().trim().length()<1) {
//            return false;
//        } else if (occupatonEt.getText().toString().trim().length()<1) {
//            return false;
//        }  else if (maritalStatusRG.getCheckedRadioButtonId() == -1) {
//            return false;
//        } else if (alternateBG.getCheckedRadioButtonId() == -1) {
//            return false;
//        } else if (purposeBG.getCheckedRadioButtonId() == -1) {
//            return false;
//        }
        return true;
    }

    private void createTransactionObject() {
        transaction = EstateTransaction.getInstance();
        //
    }
}
