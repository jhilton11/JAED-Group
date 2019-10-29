package com.appify.jaedgroup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appify.jaedgroup.utils.UtilObjects;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 12345;
    private Button facebookButton;
    private Button gmailButton;
    private Button signInButton;
    private TextView signInText;
    private EditText usernameEt;
    private EditText passwordEt;
    private View layout;
    private ProgressBar progressBar;

    private String username, password;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //dialog = new ProgressDialog(this);
        layout = findViewById(R.id.login_layout);
        gmailButton = findViewById(R.id.google_signin_btn);
        facebookButton = findViewById(R.id.facebook_btn);
        signInButton = findViewById(R.id.email_signin_btn);
        signInText = findViewById(R.id.signup_text);
        usernameEt = findViewById(R.id.et_username);
        passwordEt = findViewById(R.id.et_password);

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

        final LoginManager loginManager = LoginManager.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        //loginManager.setReadPermissions("email", "public_profile");
        loginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("msg", "Log in successful " + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginManager.logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                //loginManager.log
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithEmail(usernameEt.getText().toString(), passwordEt.getText().toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            signIn();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Log.d("msg", "Successfully logged into google");
            } catch (ApiException e) {
                Log.w("error", ""+task.getException().toString());
            }
        }
    }

    private void googleSignIn() {
        Log.d("msg", "Trying to login to gmail");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signIn() {
        String id = mAuth.getCurrentUser().getUid();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //dialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.d("msg", "Firebase sign in was successful");
                            signIn();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("msg", "HandleFacebookAccessToken: " + token.getToken());

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("msg", "Firebase sign in was successful");
                            signIn();
                        }
                    }
                });
    }

    private void signInWithEmail(String name, String pass) {
        if (checkUsernameAndPassword(name, pass)) {
            Log.d("msg", "Trying to log into Firebase");
            mAuth.signInWithEmailAndPassword(name, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("msg", "Sign in with email successful");
                                signIn();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("error", e.getMessage());
                }
            });
        }
    }

    private boolean checkUsernameAndPassword(String username, String password) {
        if (username.length()<1 || password.length()<1) {
            UtilObjects.makeSnackbar(layout, "Username or password field is empty");
            return false;
        } else if (!UtilObjects.validateEmail(username)) {
            UtilObjects.makeSnackbar(layout, "Email address is not valid");
            return false;
        }
        return true;
    }
}
