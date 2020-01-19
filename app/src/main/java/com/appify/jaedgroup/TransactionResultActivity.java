package com.appify.jaedgroup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TransactionResultActivity extends AppCompatActivity {

    private TextView timeTv;
    private Button homeBtn;

    private int time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_result);

        timeTv = findViewById(R.id.time_tv);
        homeBtn = findViewById(R.id.home_btn);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitToHomePage();
            }
        });

        time = 5;
        final Handler handler = new Handler();
        Runnable timer = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                time--;
                timeTv.setText(time+"");

                if (time==0) {
                    exitToHomePage();
                }
            }
        };
        handler.post(timer);
    }

    private void exitToHomePage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
