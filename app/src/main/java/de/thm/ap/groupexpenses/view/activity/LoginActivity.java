package de.thm.ap.groupexpenses.view.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
    private TextView loginTyp;
    private LinearLayout creationLayout;
    private LinearLayout loginLayout;
    private LinearLayout passwordForgotLayout;
    private LinearLayout infoLayout;
    private EditText edLoginEmail;
    private EditText edLoginPassword;
    private EditText edPassForgotEmail;
    private EditText edCreateNickname;
    private EditText edCreateEmail;
    private EditText edCreatePassword;
    private EditText edCreatePasswordAgain;
    public ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // find Layouts
        infoLayout = findViewById(R.id.info);
        creationLayout = findViewById(R.id.creationLayout);
        loginLayout = findViewById(R.id.loginLayout);
        passwordForgotLayout = findViewById(R.id.passwordForgotLayout);

        // output fields
        tvStatus = findViewById(R.id.status);
        tvDetail = findViewById(R.id.detail);
        loginTyp = findViewById(R.id.loginTyp);

        //input fields
        edLoginPassword = findViewById(R.id.fieldPassword);
        edLoginEmail = findViewById(R.id.loginEmailField);
        edCreateNickname = findViewById(R.id.createfieldNickname);
        edCreateEmail = findViewById(R.id.createEmailField);
        edCreatePassword = findViewById(R.id.createfieldPassword);
        edCreatePasswordAgain = findViewById(R.id.createfieldPasswordAgain);
        edPassForgotEmail = findViewById(R.id.EmailFieldPassForgot);

        //buttons
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        findViewById(R.id.showCreationLayoutButton).setOnClickListener(this);
        findViewById(R.id.CreateAccountButton).setOnClickListener(this);
        findViewById(R.id.backButton).setOnClickListener(this);
        findViewById(R.id.sendVerificationAgain).setOnClickListener(this);
        findViewById(R.id.sendNewPassword).setOnClickListener(this);
        findViewById(R.id.passwordForgot).setOnClickListener(this);
        findViewById(R.id.backToLoginButton).setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
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

    private void createAccount(String email, String password, String nickname) {
        Log.d(TAG, "createAccount:" + email);
        if (isInvalidCreateForm()) {
            return;
        }

        showProgressDialog();

        DatabaseHandler.isNicknameExisting(nickname, exists ->{
            if(exists){
                edCreateNickname.setError(getString(R.string.error_already_in_use));
                hideProgressDialog();
            }
            else{
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Snackbar.make(tvStatus, getString(R.string.account_successfully_created), Snackbar.LENGTH_LONG).show();
                            sendEmailVerification();

                            assert user != null;
                            User createUser = new User(user.getUid(), user.getEmail());
                            createUser.setNickname(nickname);

                            DatabaseHandler.updateUserWithFeedback(createUser,
                                    l -> Log.d(TAG, "Userdata successful written"),
                                    f -> Log.d(TAG, "User data could`nt written"));

                            //show info screen
                            tvStatus.setText(getString(R.string.emailpassword_status_fmt,
                                    user.getEmail()));
                            tvDetail.setText(getString(R.string.detail_verification));
                            showInfoLayout();

                            // Add flag to check the state of the email confirm
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                            sp.edit().putBoolean(CONFIRM_PROCESS, true).apply();


                        } else {
                            // If creation fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Snackbar.make(tvStatus, getString(R.string.create_account_failed), Snackbar.LENGTH_LONG).show();
                        }
                        hideProgressDialog();
                    }
                );
            }
        });

    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);

        if (isInvalidLoginForm()) {
            return;
        }
        showProgressDialog();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    FirebaseUser user = auth.getCurrentUser();
                    if (task.isSuccessful() && user != null) {
                        // Sign in success, update UI with the signed-in user's information

                        if (user.isEmailVerified()) {
                            Log.d(TAG, "signInWithEmail:success");
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
                            Log.w(TAG, "signInWithEmail:failure:notConfirmed", task.getException());
                            tvStatus.setText(getString(R.string.emailpassword_status_fmt,
                                    user.getEmail()));
                            tvDetail.setText(getString(R.string.detail_verification));
                            showInfoLayout();
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Snackbar.make(tvStatus, getString(R.string.auth_failed), Snackbar.LENGTH_LONG).show();
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
        showLoginLayout();
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

    private boolean isInvalidLoginForm() {
        boolean valid = true;

        String email = edLoginEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            edLoginEmail.setError(getString(R.string.error_field_required));
            valid = false;
        }
        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edLoginEmail.setError(getString(R.string.error_field_no_valid_email));
            valid = false;
        }
        else {
            edLoginEmail.setError(null);
        }

        String password = edLoginPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            edLoginPassword.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            edLoginPassword.setError(null);
        }

        return !valid;
    }

    private boolean isInvalidCreateForm() {
        boolean valid = true;
        String email = edCreateEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            edCreateEmail.setError(getString(R.string.error_field_required));
            valid = false;
        }
        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edCreateEmail.setError(getString(R.string.error_field_no_valid_email));
            valid = false;
        }
        else {
            edCreateEmail.setError(null);
        }

        String password = edCreatePassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            edCreatePassword.setError(getString(R.string.error_field_required));
            valid = false;
        }
        else {
            edCreatePassword.setError(null);
        }

        String passwordAgain = edCreatePasswordAgain.getText().toString();
        if (TextUtils.isEmpty(password)) {
            edCreatePasswordAgain.setError(getString(R.string.error_field_required));
            valid = false;
        }
        else if(!password.equals(passwordAgain)){
            valid = false;
            edCreatePasswordAgain.setError(getString(R.string.error_passwords_dont_match));
        }
        else {
            edCreatePasswordAgain.setError(null);
        }

        String nickname = edCreateNickname.getText().toString();
        if (TextUtils.isEmpty(nickname)) {
            edCreateNickname.setError(getString(R.string.error_field_required));
            valid = false;
        }
        else {
            edCreateNickname.setError(null);
        }

        return !valid;
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
            case R.id.showCreationLayoutButton:
                showCreationLayout();
                break;
            case R.id.CreateAccountButton:
                createAccount(edCreateEmail.getText().toString(), edCreatePassword.getText().toString(), edCreateNickname.getText().toString());
                break;
            case R.id.emailSignInButton:
                signIn(edLoginEmail.getText().toString(), edLoginPassword.getText().toString());
                break;
            case R.id.backToLoginButton:
            case R.id.backButton:
                signOut();
                break;
            case R.id.sendVerificationAgain:
                sendEmailVerification();
                break;
            case R.id.passwordForgot:
                showPasswordForgotLayout();
                break;
            case R.id.sendNewPassword:
                sendPasswordResetEmail();
            default:
                break;
        }
    }

    private void sendPasswordResetEmail() {
        showProgressDialog();

        final String email = edPassForgotEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            edPassForgotEmail.setError("Required.");
        } else {
            edPassForgotEmail.setError(null);
            auth.sendPasswordResetEmail(email).addOnCompleteListener(this, task -> {
                hideProgressDialog();

                if (task.isSuccessful()) {
                    Snackbar.make(tvStatus, getString(R.string.resetPassword_success, email), Snackbar.LENGTH_LONG).show();

                    //change Layout back to Login
                    showPasswordForgotLayout();
                } else {
                    Snackbar.make(tvStatus, getString(R.string.resetPassword_error), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    private void showInfoLayout(){
        loginTyp.setText(getString(R.string.login_typ_info));
        infoLayout.setVisibility(View.VISIBLE);
        creationLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.GONE);
        passwordForgotLayout.setVisibility(View.GONE);
        new ConfirmTask().execute();
    }

    private void showLoginLayout(){
        loginTyp.setText(getString(R.string.login_typ_login));
        infoLayout.setVisibility(View.GONE);
        creationLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.VISIBLE);
        passwordForgotLayout.setVisibility(View.GONE);
    }

    private void showPasswordForgotLayout() {
        loginTyp.setText(getString(R.string.login_typ_password_forgot));
        infoLayout.setVisibility(View.GONE);
        creationLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.GONE);
        passwordForgotLayout.setVisibility(View.VISIBLE);
    }

    private void showCreationLayout() {
        loginTyp.setText(getString(R.string.login_typ_create));
        infoLayout.setVisibility(View.GONE);
        creationLayout.setVisibility(View.VISIBLE);
        loginLayout.setVisibility(View.GONE);
        passwordForgotLayout.setVisibility(View.GONE);
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
