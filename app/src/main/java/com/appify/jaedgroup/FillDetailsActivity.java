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

import com.appify.jaedgroup.model.Estate;
import com.appify.jaedgroup.model.EstateTransaction;
import com.appify.jaedgroup.utils.Constants;
import com.appify.jaedgroup.utils.tasks;
import com.google.firebase.auth.FirebaseAuth;

public class FillDetailsActivity extends AppCompatActivity {
    private Button payBtn;
    private EditText nameEt, occupatonEt, phoneNoEt, addressEt, nextOfKinAddressEt, dateOfBirthEt, nextOfKinPhoneNoEt;
    private RadioGroup maritalStatusRG, purposeBG, alternateBG;
    private View layout;
    private ActionBar toolbar;

    private EstateTransaction transaction;
    private String purposeOfLand, id, email, userId, estateName, maritalStatus, nextOfKinPhoneNo;
    private boolean willAcceptAlternativePlot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_estate_details);

        Estate estate = (Estate) getIntent().getSerializableExtra(Constants.ESTATE_EXTRA);
        if (estate != null) {
            id = estate.getId();
            estateName = estate.getName();
            userId = FirebaseAuth.getInstance().getUid();
            email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }

        layout = findViewById(R.id.layout);
        nameEt = findViewById(R.id.name_et);
        occupatonEt = findViewById(R.id.occupation_et);
        addressEt = findViewById(R.id.address_et);
        phoneNoEt = findViewById(R.id.phone_no_et);
        nextOfKinAddressEt = findViewById(R.id.nextOfKin_et);
        dateOfBirthEt = findViewById(R.id.dateOfBirth_et);
        nextOfKinPhoneNoEt = findViewById(R.id.nextOfKin_phone);
        maritalStatusRG = findViewById(R.id.maritalStatusBtnGrp);
        purposeBG = findViewById(R.id.purposeOfLandBtnGrp);
        alternateBG = findViewById(R.id.acceptAlternateLandStatusBtnGrp);

        payBtn = findViewById(R.id.pay_btn);

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payforEstate();
            }
        });

        purposeBG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = findViewById(i);
                purposeOfLand = rb.getText().toString();
            }
        });

        maritalStatusRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = findViewById(i);
                maritalStatus = rb.getText().toString();
            }
        });

        alternateBG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = findViewById(i);
                String checked = rb.getText().toString();

                if (checked.equals("Yes")) {
                    willAcceptAlternativePlot = true;
                } else {
                    willAcceptAlternativePlot = false;
                }
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
            if (tasks.checkNetworkStatus(this)) {
                transaction = new EstateTransaction(nameEt.getText().toString(), id, estateName, phoneNoEt.getText().toString(),
                        addressEt.getText().toString(), dateOfBirthEt.getText().toString(), occupatonEt.getText().toString(), maritalStatus,
                        nextOfKinPhoneNo, nextOfKinAddressEt.getText().toString(), nextOfKinAddressEt.getText().toString(), purposeOfLand,
                        willAcceptAlternativePlot, email, userId);
                Intent intent = new Intent(this, OrderSummaryActivity.class);
                intent.putExtra("estateTransaction", transaction);
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
        if (nameEt.getText().toString().trim().length()<1) {
            return false;
        } else if (dateOfBirthEt.getText().toString().trim().length()<1) {
            return false;
        } else if (phoneNoEt.getText().toString().trim().length()<1) {
            return false;
        } else if (addressEt.getText().toString().trim().length()<1) {
            return false;
        } else if (occupatonEt.getText().toString().trim().length()<1) {
            return false;
        }  else if (maritalStatusRG.getCheckedRadioButtonId() == -1) {
            return false;
        } else if (alternateBG.getCheckedRadioButtonId() == -1) {
            return false;
        } else if (purposeBG.getCheckedRadioButtonId() == -1) {
            return false;
        }
        return true;
    }

    private void createTransactionObject() {
        transaction = new EstateTransaction();
    }
}
