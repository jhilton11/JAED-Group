package com.appify.jaedgroup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appify.jaedgroup.utils.Constants;
import com.appify.jaedgroup.utils.tasks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {
    private TextInputEditText nameEt, passwordEt, emailEt;
    private TextInputLayout nameTl, passwordTl, emailTl;
    private Button signUnBtn;
    private TextView signInText;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        nameTl = findViewById(R.id.name_tl);
        passwordTl = findViewById(R.id.password_tl);
        emailTl = findViewById(R.id.email_tl);

        nameEt = findViewById(R.id.name_et);
        passwordEt = findViewById(R.id.password_et);
        emailEt = findViewById(R.id.email_et);

        signUnBtn = findViewById(R.id.signup_btn);
        signInText = findViewById(R.id.signin_text);

        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);

        signUnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("signInText", "Sign in text clicked");
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void signUp() {
        if (verifyInput()) {
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            String name = nameEt.getText().toString();
            String email = emailEt.getText().toString();
            String password = passwordEt.getText().toString();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task2) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Toast.makeText(SignUpActivity.this, "Please verify your email. An activation email has been sent to " + user.getEmail(), Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    dialog.dismiss();
                                    Log.d("Signup", task2.getException().toString());
                                    tasks.displayAlertDialog(SignUpActivity.this,"", task2.getException().getMessage());
                                }
                            }
                        });
                    } else {
                        dialog.dismiss();
                        Log.d("Signup", task.getException().toString());
                        tasks.displayAlertDialog(SignUpActivity.this, "", task.getException().getMessage());
                    }
                }
            });
        }
    }

    private boolean verifyInput() {
        if (TextUtils.isEmpty(nameEt.getText().toString())) {
            nameTl.setError("Name field is empty");
            return false;
        } else if (passwordEt.getText().length()<8){
            passwordTl.setError("Password must be at least 8 characters long");
            return false;
        }  else if (!tasks.validateEmail(emailEt.getText().toString())) {
            emailTl.setError("Email address is not valid");
            return false;
        }
        nameTl.setError(null);
        passwordTl.setError(null);
        emailTl.setError(null);

        return true;
    }
}
