package com.appify.jaedgroup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.appify.jaedgroup.model.InvestmentTransaction;
import com.appify.jaedgroup.utils.tasks;
import com.google.firebase.auth.FirebaseAuth;

import java.util.UUID;

public class NewInvestmentActivity extends AppCompatActivity {
    private EditText nameEt, dobEt, phoneNoEt, addressEt, emailEt, nextOfinNameEt, nextOfKinPhoneNoEt;
    private RadioGroup maritalStatusRg, genderRg;
    private Button proceedBtn;
    private View layout;

    private InvestmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_investment);

        layout = findViewById(R.id.layout);
        nameEt = findViewById(R.id.name_et);
        dobEt = findViewById(R.id.dateOfBirth_et);
        phoneNoEt = findViewById(R.id.phone_no_et);
        addressEt = findViewById(R.id.address_et);
        emailEt = findViewById(R.id.email_et);
        nextOfinNameEt = findViewById(R.id.nextOfKin_et);
        nextOfKinPhoneNoEt = findViewById(R.id.nextOfKin_phone_et);
        maritalStatusRg = findViewById(R.id.maritalStatusBtnGrp);
        genderRg = findViewById(R.id.genderRG);
        proceedBtn = findViewById(R.id.button_next);

        tasks.displayAlertDialog(this, "Disclaimer", "This is a disclaimer");

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proceed();
            }
        });
    }

    private boolean getDetails() {
        transaction = new InvestmentTransaction();

        if (TextUtils.isEmpty(nameEt.getText().toString())) {
            tasks.makeSnackbar(layout, "Name field is empty");
            return false;
        } else if (TextUtils.isEmpty(dobEt.getText().toString())) {
            tasks.makeSnackbar(layout, "Date of birth field is empty");
            return  false;
        } else if (TextUtils.isEmpty(phoneNoEt.getText().toString())) {
            tasks.makeSnackbar(layout, "Phone number field is empty");
            return false;
        } else if (TextUtils.isEmpty(addressEt.getText().toString())) {
            tasks.makeSnackbar(layout, "Address field is empty");
            return false;
        } else if (TextUtils.isEmpty(nextOfinNameEt.getText().toString())) {
            tasks.makeSnackbar(layout, "Next of kin field is empty");
            return false;
        } else if (TextUtils.isEmpty(nextOfKinPhoneNoEt.getText().toString())) {
            tasks.makeSnackbar(layout, "Next of kin phone number field is empty");
            return false;
        } else if (maritalStatusRg.getCheckedRadioButtonId() == -1) {
            tasks.makeSnackbar(layout, "Marital status not selected");
            return false;
        } else if (genderRg.getCheckedRadioButtonId() == -1) {
            tasks.makeSnackbar(layout, "Gender not selected");
            return false;
        }
        transaction.setId(UUID.randomUUID().toString());
        transaction.setName(nameEt.getText().toString().trim());
        transaction.setPhoneNo(phoneNoEt.getText().toString().trim());
        transaction.setAddress(addressEt.getText().toString().trim());
        transaction.setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail().trim());
        transaction.setNextOfKinName(nextOfinNameEt.getText().toString().trim());
        transaction.setNextOfKinPhoneNo(nextOfKinPhoneNoEt.getText().toString().trim());
        RadioButton mRb = findViewById(maritalStatusRg.getCheckedRadioButtonId());
        transaction.setMaritalStatus(mRb.getText().toString().trim());
        RadioButton gRb = findViewById(genderRg.getCheckedRadioButtonId());
        transaction.setGender(gRb.getText().toString().trim());
        transaction.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        return true;
    }

    private void proceed(){
        if (getDetails()) {
            Intent intent = new Intent(this, CompleteInvestmentActivity.class);
            intent.putExtra("transaction", transaction);
            startActivity(intent);
        }
    }
}
