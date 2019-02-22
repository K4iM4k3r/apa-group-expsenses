package de.thm.ap.groupexpenses.view.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.model.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    public static final String CONFIRM_PROCESS = "confirm_process";
    private FirebaseAuth auth;
    private TextView tvStatus;
    private TextView tvDetail;
    private EditText edEmail;
    private EditText edPassword;
    private TextView tvForgot;
    private Button btnSendPassword;
    private LinearLayout layoutEmailPassword;
    private LinearLayout layoutSignedIn;
    @VisibleForTesting
    public ProgressDialog mProgressDialog;


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
        findViewById(R.id.sendVerificationAgain).setOnClickListener(this);
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

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();

        if (auth.getUid() != null) {
            DatabaseHandler.queryUser(auth.getUid(), user -> App.CurrentUser = user);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if(sp.getBoolean(CONFIRM_PROCESS, false)){
            new ConfirmTask().execute();
            Log.i(TAG, "resume, confirmprozess");

        }
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (isInvalidForm()) {
            return;
        }

        showProgressDialog();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = auth.getCurrentUser();
                        Snackbar.make(tvStatus, getString(R.string.create_account_successful), Snackbar.LENGTH_LONG).show();
                        sendEmailVerification();
                        assert user != null;
                        User createUser = new User(user.getUid(), user.getEmail());
                        DatabaseHandler.updateUserWithFeedback(createUser, l -> Log.d(TAG, "Userdata successfull written"), f -> Log.d(TAG, "User data couldnt written"));
                        updateUI(user);
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                        sp.edit().putBoolean(CONFIRM_PROCESS, true).apply();

                        new ConfirmTask().execute();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Snackbar.make(tvStatus, getString(R.string.create_account_failed), Snackbar.LENGTH_LONG).show();
                        updateUI(null);
                    }
                    hideProgressDialog();
                });
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);

        if (isInvalidForm()) {
            return;
        }
        showProgressDialog();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            Snackbar.make(tvStatus, getString(R.string.auth_successful), Snackbar.LENGTH_LONG).show();

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            Uri uri = user.getPhotoUrl();
                            if (uri != null) {
                                StorageReference profilePic = storage.getReferenceFromUrl(String.valueOf(uri));
                                File filePic = new File(getExternalFilesDir(null), "profilePic.jpg");

                                profilePic.getFile(filePic).addOnSuccessListener(taskSnapshot -> {
                                    // Local file has been created
                                    Snackbar.make(tvStatus, getString(R.string.success_download_ProfilePic), Snackbar.LENGTH_LONG).show();
                                }).addOnFailureListener(exception -> Snackbar.make(tvStatus, getString(R.string.error_download_ProfilePic), Snackbar.LENGTH_LONG).show());

                            }
                            startActivity(new Intent(this, EventActivity.class));
                            finish();
                        } else {
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
                });
    }

    private void signOut() {
        auth.signOut();
        updateUI(null);
    }

    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.sendVerificationAgain).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(this, task -> {
                        // [START_EXCLUDE]
                        // Re-enable button
                        findViewById(R.id.sendVerificationAgain).setEnabled(true);

                        if (task.isSuccessful()) {
                            Snackbar.make(tvStatus, getString(R.string.info_verification_fmt, user.getEmail()), Snackbar.LENGTH_LONG).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Snackbar.make(tvStatus, getString(R.string.info_verification_error), Snackbar.LENGTH_LONG).show();
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
                    user.getEmail()));
            tvDetail.setText(getString(R.string.detail_verification));

            layoutEmailPassword.setVisibility(View.GONE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);
            layoutSignedIn.setVisibility(View.VISIBLE);
            tvForgot.setVisibility(View.GONE);
            findViewById(R.id.sendVerificationAgain).setEnabled(!user.isEmailVerified());


        } else {
            tvStatus.setText(R.string.back_creation);
            tvDetail.setText(null);
            tvForgot.setVisibility(View.VISIBLE);
            findViewById(R.id.info).setVisibility(View.GONE);
            layoutEmailPassword.setVisibility(View.VISIBLE);
            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);
            layoutSignedIn.setVisibility(View.GONE);
        }
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emailCreateAccountButton:
                createAccount(edEmail.getText().toString(), edPassword.getText().toString());
                break;
            case R.id.emailSignInButton:
                signIn(edEmail.getText().toString(), edPassword.getText().toString());
                break;
            case R.id.signOutButton:
                signOut();
                break;
            case R.id.sendVerificationAgain:
                sendEmailVerification();
                break;
            case R.id.passwordForgot:
                showPasswordForgot(true);
                break;
            case R.id.sendNewPassword:
                sendPasswordResetEmail();
            default:
                break;
        }
    }

    private void sendPasswordResetEmail() {
        showProgressDialog();

        final String email = edEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            edEmail.setError("Required.");
        } else {
            edEmail.setError(null);
            auth.sendPasswordResetEmail(email).addOnCompleteListener(this, task -> {
                hideProgressDialog();

                if (task.isSuccessful()) {
                    Snackbar.make(tvStatus, getString(R.string.resetPassword_success, email), Snackbar.LENGTH_LONG).show();

                    //change Layout back to Login
                    showPasswordForgot(false);
                } else {
                    Snackbar.make(tvStatus, getString(R.string.resetPassword_error), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    private void showPasswordForgot(boolean visible) {
        if (visible) {
            edPassword.setVisibility(View.GONE);
            tvForgot.setVisibility(View.GONE);
            btnSendPassword.setVisibility(View.VISIBLE);
            layoutEmailPassword.setVisibility(View.GONE);
            layoutSignedIn.setVisibility(View.GONE);
        } else {
            edPassword.setVisibility(View.VISIBLE);
            tvForgot.setVisibility(View.VISIBLE);
            btnSendPassword.setVisibility(View.GONE);
            layoutEmailPassword.setVisibility(View.VISIBLE);
            layoutSignedIn.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class ConfirmTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                while (!firebaseUser.isEmailVerified()) {
                    try {
                        firebaseUser.reload();
                        Log.i(TAG, "wait 3 sec");
                        Thread.sleep(3 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return Boolean.FALSE;
                    }
                }
                return Boolean.TRUE;

            } else {
                return Boolean.FALSE;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result){
                startActivity(new Intent(getBaseContext(), EventActivity.class));
                Log.i(TAG, "Start MainPage");
                finish();
            }
        }
    }

}
