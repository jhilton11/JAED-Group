package com.appify.jaedgroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.appify.jaedgroup.model.Estate;
import com.appify.jaedgroup.model.EstateTransaction;
import com.appify.jaedgroup.utils.Constants;
import com.appify.jaedgroup.utils.tasks;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class FillDetailsActivity extends AppCompatActivity {
    private Button payBtn, showDobBtn;
    private EditText nameEt, occupatonEt, phoneNoEt, addressEt, nextOfKinAddressEt, nextOfKinPhoneNoEt;
    private RadioGroup maritalStatusRG, purposeBG, alternateBG;
    private TextView dateOfBirthTv;
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
            Log.d(getClass().getSimpleName(), "id = " + id);
        } else {
            Log.d(getClass().getSimpleName(), "No id found");
        }

        layout = findViewById(R.id.layout);
        nameEt = findViewById(R.id.name_et);
        occupatonEt = findViewById(R.id.occupation_et);
        addressEt = findViewById(R.id.address_et);
        phoneNoEt = findViewById(R.id.phone_no_et);
        nextOfKinAddressEt = findViewById(R.id.nextOfKin_et);
        dateOfBirthTv = findViewById(R.id.dateOfBirth_tv);
        nextOfKinPhoneNoEt = findViewById(R.id.nextOfKin_phone);
        maritalStatusRG = findViewById(R.id.maritalStatusBtnGrp);
        purposeBG = findViewById(R.id.purposeOfLandBtnGrp);
        alternateBG = findViewById(R.id.acceptAlternateLandStatusBtnGrp);

        payBtn = findViewById(R.id.pay_btn);
        showDobBtn = findViewById(R.id.showDobBtn);

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

        showDobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDateOfBirth();
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
                        addressEt.getText().toString(), dateOfBirthTv.getText().toString(), occupatonEt.getText().toString(), maritalStatus,
                        nextOfKinPhoneNo, nextOfKinAddressEt.getText().toString(), nextOfKinAddressEt.getText().toString(), purposeOfLand,
                        willAcceptAlternativePlot, email, userId);
                transaction.setId(UUID.randomUUID().toString());
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
        } else if (dateOfBirthTv.getText().toString().trim().length()<1) {
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

    private void getDateOfBirth() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
//                String format = "yyyy-MM-dd";
//                SimpleDateFormat sdm = new   SimpleDateFormat(format, Locale.getDefault());
                dateOfBirthTv.setText(i2+"-"+i1+"-"+i);
            }
        }, year, month, day);
        dialog.show();
    }

    private void createTransactionObject() {
        transaction = new EstateTransaction();
    }
}
