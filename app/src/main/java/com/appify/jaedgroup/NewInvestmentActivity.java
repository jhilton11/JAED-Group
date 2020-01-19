package com.appify.jaedgroup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.appify.jaedgroup.utils.tasks;

public class NewInvestmentActivity extends AppCompatActivity {
    private EditText nameEt, ageEt, phoneNoEt, addressEt, emailEt;
    private Button proceedBtn;

    private String name, age, phoneNo, address, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_investment);

        tasks.displayAlertDialog(this, "Disclaimer", "This is a disclaimer");

//        proceedBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    private void proceed(){
        Intent intent = new Intent();
        startActivity(intent);
    }
}
