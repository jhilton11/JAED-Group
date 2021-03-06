package com.appify.jaedgroup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.appify.jaedgroup.model.User;
import com.appify.jaedgroup.utils.tasks;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 12345;
    private Button gmailButton;
    private Button signInButton;
    private TextView signInText;
    private TextView verifyEmailTv;
    private TextView verifyEmailBtn;
    private TextInputEditText usernameEt;
    private TextInputEditText passwordEt;
    private View layout;
    private ProgressDialog dialog;

    private String username, password;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialog = new ProgressDialog(this);
        layout = findViewById(R.id.login_layout);
        gmailButton = findViewById(R.id.google_signin_btn);
        signInButton = findViewById(R.id.email_signin_btn);
        signInText = findViewById(R.id.signup_text);
        verifyEmailTv = findViewById(R.id.verify_email_tv);
        verifyEmailBtn = findViewById(R.id.verify_email_btn);
        usernameEt = findViewById(R.id.et_username);
        passwordEt = findViewById(R.id.et_password);

        dialog.setCancelable(false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        gmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithEmail(usernameEt.getText().toString().trim(), passwordEt.getText().toString().trim());
            }
        });

        signInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d("onStart", "Dialog should show");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (user.isEmailVerified()) {
                dialog.dismiss();
                signIn();
                Log.d("onStart", "User is email verified");
            } else {
                displayVerifyEmailView();
                Log.d("tag", "Email is not verified");
            }
        } else {
            Log.d("onStart", "User is not signed in");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    dialog.setMessage("Please wait....");
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account);
                    Log.d("msg", "Successfully logged into google");
                } catch (ApiException e) {
                    Log.w("error", ""+task.getException().toString());
                    tasks.makeSnackbar(layout, "Unable to log in " + task.getException().toString());
                    dialog.dismiss();
                }
            } else {
                tasks.displayAlertDialog(LoginActivity.this, "Login failure", "Unable to login to google. This service requires Google Play Services. Please make Google Play is running on your device and then reboot");
                dialog.dismiss();
            }
        }
    }

    private void googleSignIn() {
        if (tasks.checkNetworkStatus(this)) {
            dialog.show();
            Log.d("msg", "Trying to login to gmail");
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            tasks.makeSnackbar(layout, "No network. Pls check network connection and try again");
        }
    }

    private void signIn() {
        Log.d("login", "Login successful");
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.d("msg", "Firebase sign in was successful");
                            signIn();
                        } else {
                            tasks.makeSnackbar(layout, "Unable to log in " + task.getException().getMessage());
                            dialog.dismiss();
                            Log.d("firebaseAuthWithGoogle", task.getException().toString());
                        }
                    }
                });
    }

    private void signInWithEmail(String name, String pass) {
        if (tasks.checkNetworkStatus(this)) {
            if (checkUsernameAndPassword(name, pass)) {
                dialog.show();
                Log.d("msg", "Trying to log into Firebase");
                mAuth.signInWithEmailAndPassword(name, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                        Log.d("msg", "Sign in with email successful");
                                        signIn();
                                    } else {
                                        displayVerifyEmailView();
                                    }
                                } else {
                                    dialog.dismiss();
                                    tasks.displayAlertDialog(LoginActivity.this, "", task.getException().getMessage());
                                    Log.e("error", task.getException().toString());
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("error", e.getMessage());
                    }
                });
            }
        } else {
            tasks.makeSnackbar(layout, "No network. Pls check network connection and try again");
        }
    }

    private boolean checkUsernameAndPassword(String username, String password) {
        if (username.length()<1 || password.length()<1) {
            tasks.makeSnackbar(layout, "Username or password field is empty");
            return false;
        } else if (!tasks.validateEmail(username)) {
            tasks.makeSnackbar(layout, "Email address is not valid");
            return false;
        }
        return true;
    }

    //ToDo: Display view to make user verify email
    private void displayVerifyEmailView() {
        verifyEmailTv.setVisibility(View.VISIBLE);
        verifyEmailTv.setText("Thank you for signing up. " +
                "But your email " + mAuth.getCurrentUser().getEmail() +" has not been verified. Please check your mail to activate your account");
        verifyEmailBtn.setVisibility(View.VISIBLE);
        verifyEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyEmail();
            }
        });
    }

    private void verifyEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    tasks.displayAlertDialog(LoginActivity.this,"", "Verification email has been sent. Please verify your email");
                }
            }
        });
    }
}
