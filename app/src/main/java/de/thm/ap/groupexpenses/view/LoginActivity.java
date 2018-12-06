package de.thm.ap.groupexpenses.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.thm.ap.groupexpenses.R;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth auth;
    private TextView tvStatus;
    private TextView tvDetail;
    private EditText edEmail;
    private EditText edPassword;
    private TextView tvForgot;
    private Button btnSendPassword;
    private LinearLayout layoutEmailPassword;
    private LinearLayout layoutSignedIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvStatus = findViewById(R.id.status);
        tvDetail = findViewById(R.id.detail);
        edPassword = findViewById(R.id.fieldPassword);
        edEmail = findViewById(R.id.fieldEmail);
        tvForgot = findViewById(R.id.passwordForgot);


        btnSendPassword = findViewById(R.id.sendNewPassword);
        layoutEmailPassword = findViewById(R.id.emailPasswordButtons);
        layoutSignedIn = findViewById(R.id.signedInButtons);



        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.verifyEmailButton).setOnClickListener(this);
        findViewById(R.id.sendNewPassword).setOnClickListener(this);

        tvForgot.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (isInvalidForm()) {
            return;
        }

        showProgressDialog();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Snackbar.make(tvStatus, getString(R.string.create_account_successful), Snackbar.LENGTH_LONG).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Snackbar.make(tvStatus, getString(R.string.create_account_failed), Snackbar.LENGTH_LONG).show();
                            updateUI(null);
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        
        if (isInvalidForm()) {
            return;
        }
        showProgressDialog();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            if(user !=null && user.isEmailVerified()){
                                Snackbar.make(tvStatus, getString(R.string.auth_successful), Snackbar.LENGTH_LONG).show();
                                finish();
                            }
                            else {
                                updateUI(user);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Snackbar.make(tvStatus, getString(R.string.auth_failed), Snackbar.LENGTH_LONG).show();
                            updateUI(null);
                        }

                        // task error handling
                        if (!task.isSuccessful()) {
                            tvStatus.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void signOut() {
        auth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verifyEmailButton).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // [START_EXCLUDE]
                            // Re-enable button
                            findViewById(R.id.verifyEmailButton).setEnabled(true);
    
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this,
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(LoginActivity.this,
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private boolean isInvalidForm() {
        boolean valid = true;

        String email = edEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            edEmail.setError("Required.");
            valid = false;
        } else {
            edEmail.setError(null);
        }

        String password = edPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            edPassword.setError("Required.");
            valid = false;
        } else {
            edPassword.setError(null);
        }

        return !valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {

            findViewById(R.id.info).setVisibility(View.VISIBLE);
            tvStatus.setText(getString(R.string.emailpassword_status_fmt,
                    user.getEmail(), user.isEmailVerified()));
            tvDetail.setText(getString(R.string.firebase_status_fmt, user.getUid()));

            layoutEmailPassword.setVisibility(View.GONE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
            layoutSignedIn.setVisibility(View.VISIBLE);

            findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());


        } else {
            tvStatus.setText(R.string.signed_out);
            tvDetail.setText(null);

            findViewById(R.id.info).setVisibility(View.GONE);
            layoutEmailPassword.setVisibility(View.VISIBLE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
            layoutSignedIn.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.emailCreateAccountButton:
                createAccount(edEmail.getText().toString(), edPassword.getText().toString());
                break;
            case R.id.emailSignInButton:
                signIn(edEmail.getText().toString(), edPassword.getText().toString());
                break;
            case R.id.signOutButton:
                signOut();
                break;
            case R.id.verifyEmailButton:
                sendEmailVerification();
                break;
            case R.id.passwordForgot:
                showPasswordForgot();
                break;
            case R.id.sendNewPassword:
                sendNewPassword();
            default:
                break;
        }
    }

    private void sendNewPassword() {
        showProgressDialog();

        final String email = edEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            edEmail.setError("Required.");
        } else {
            edEmail.setError(null);
            auth.sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    hideProgressDialog();

                    if (task.isSuccessful()) {
                        Snackbar.make(tvStatus, getString(R.string.resetPassword_success, email), Snackbar.LENGTH_LONG).show();

                        //change Layout back to Login
                        edPassword.setVisibility(View.VISIBLE);
                        tvForgot.setVisibility(View.VISIBLE);
                        layoutEmailPassword.setVisibility(View.VISIBLE);
                        layoutSignedIn.setVisibility(View.VISIBLE);
                        btnSendPassword.setVisibility(View.GONE);

                    }
                    else {
                        Snackbar.make(tvStatus, getString(R.string.resetPassword_error), Snackbar.LENGTH_LONG).show();
                    }
                }
            });

        }

    }

    private void showPasswordForgot() {
        edPassword.setVisibility(View.GONE);
        tvForgot.setVisibility(View.GONE);
        btnSendPassword.setVisibility(View.VISIBLE);
        layoutEmailPassword.setVisibility(View.GONE);
        layoutSignedIn.setVisibility(View.GONE);

    }
}
