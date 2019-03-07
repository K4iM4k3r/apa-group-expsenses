package de.thm.ap.groupexpenses.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

import de.hdodenhof.circleimageview.CircleImageView;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.model.User;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

public class ProfileActivity extends BaseActivity {
    private EditText edEmail, edNickname, edFirst, edLast, edInfo;
    private TextView joinDate;
    private CircleImageView profile_pic;
    private Button btnSave;
    private final int REQUEST_IMAGE_PICK = 1;
    private final String TAG = getClass().getName();
    private User user;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.my_profile);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        edEmail = findViewById(R.id.edEmail);
        edNickname = findViewById(R.id.edNickname);
        edFirst = findViewById(R.id.edFirstName);
        edLast = findViewById(R.id.edLastName);
        edInfo = findViewById(R.id.edInfo);
        Button btnEdit = findViewById(R.id.edit_profile_btn);
        btnSave = findViewById(R.id.btn_save_profile_btn);
        profile_pic = findViewById(R.id.profile_pic);
        joinDate = findViewById(R.id.tvJoinDate);
        LinearLayout profilePicLayout = findViewById(R.id.profile_pic_layout);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        edFirst.setSelectAllOnFocus(true);
        edLast.setSelectAllOnFocus(true);
        edNickname.setSelectAllOnFocus(true);
        edInfo.setSelectAllOnFocus(true);

        currentUser = super.auth.getCurrentUser();
        if (currentUser != null) {

            DatabaseHandler.queryUser(currentUser.getUid(), us -> {
                user = us;
                edFirst.setText(us.getFirstName());
                edLast.setText(us.getLastName());
                edNickname.setText(us.getNickname());
                edEmail.setText(currentUser.getEmail());
                edInfo.setText(us.getInfo());
                joinDate.setText(us.joinDateToString());
            });
        }

        btnEdit.setOnClickListener(l -> {
            edNickname.setEnabled(true);
            edLast.setEnabled(true);
            edFirst.setEnabled(true);
            edInfo.setEnabled(true);
            btnSave.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);

        });
        btnSave.setOnClickListener(l -> {
            showProgressDialog();
            if (isValidUserInput()) {
                //TOdo
                if (edNickname.getText().toString().equals(user.getNickname())) {
                    updateRoutine();
                }
                DatabaseHandler.isNicknameExisting(edNickname.getText().toString(), exists -> {
                    if (exists) {
                        edNickname.setError(getString(R.string.error_already_in_use));
                        hideProgressDialog();
                    } else {
                        updateRoutine();
                    }
                });
            } else {
                hideProgressDialog();
            }
        });

        File pic = new File(getExternalFilesDir(null), "profilePic.jpg");
        if (pic.exists()) {
            profile_pic.setImageURI(Uri.fromFile(pic));
        }

        profilePicLayout.setOnClickListener(l -> pickPhoto());

    }

    private void updateRoutine() {
        user.setFirstName(edFirst.getText().toString());
        user.setLastName(edLast.getText().toString());
        user.setNickname(edNickname.getText().toString());
        user.setInfo(edInfo.getText().toString());
        DatabaseHandler.updateUser(user);

        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(edNickname.getText().toString())
                .build();
        if (currentUser != null) {
            currentUser.updateProfile(profileUpdate);
        }
        hideProgressDialog();
        finish();
    }

    private boolean isValidUserInput() {
        boolean valid = true;
        if (TextUtils.isEmpty(edNickname.getText())) {
            edNickname.setError(getString(R.string.error_invalid_input));
            valid = false;
        }
//        if (TextUtils.isEmpty(edFirst.getText())) {
//            edFirst.setError(getString(R.string.error_invalid_input));
//            valid = false;
//        }
//        if (TextUtils.isEmpty(edLast.getText())) {
//            edLast.setError(getString(R.string.error_invalid_input));
//            valid = false;
//        }
        return valid;
    }

    private void pickPhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_IMAGE_PICK);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Intent pickPhoto = new Intent();

        Uri imageUri = FileProvider.getUriForFile(this, getString(R.string.file_provider_authority), new File(getExternalFilesDir(null), "profilePic.jpg"));

        pickPhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        pickPhoto.setType("image/*");
        pickPhoto.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(pickPhoto, "Select Picture"), REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                if (data == null || data.getData() == null) {
                    Log.i(TAG, "onResult Data is empty");
                    Snackbar.make(edEmail, getString(R.string.error_file_couldnt_upload), Snackbar.LENGTH_LONG).show();
                    return;
                } else {

                    Uri selectedImage = data.getData();
                    profile_pic.setImageURI(selectedImage);
                    savePicToAppStorage(selectedImage);
                    compressPicture();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    assert currentUser != null;
                    StorageReference storageRef = storage.getReference().child("ProfilePictures").child(currentUser.getUid() + ".jpg");
                    File file = new File(getExternalFilesDir(null), "profilePic.jpg");
                    Uri uriFile = Uri.fromFile(file);

                    UploadTask uploadTask = storageRef.putFile(uriFile);

                    uploadTask.continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            Snackbar.make(edEmail, getString(R.string.error_file_couldnt_upload), Snackbar.LENGTH_LONG).show();
                        }

                        // Continue with the task to get the download URL
                        return storageRef.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Snackbar.make(edEmail, getString(R.string.success_profile_pic_uploaded), Snackbar.LENGTH_LONG).show();
                            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri)
                                    .build();
                            currentUser.updateProfile(profileUpdate);

                            assert downloadUri != null;
                            user.setProfilePic(downloadUri.toString());
                            DatabaseHandler.updateUser(user);
                        } else {
                            // Handle failures
                            Log.i(TAG, "Error:" + task.toString());

                        }
                    });
                }
            }
        }

        compressPicture();
    }

    private void compressPicture() {
        File pic = new File(getExternalFilesDir(null), "profilePic.jpg");

        if (pic.exists()) {
            try {
                Bitmap bmp = BitmapFactory.decodeFile(pic.getPath());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
                InputStream in = new ByteArrayInputStream(bos.toByteArray());
                copyInputStreamToFile(in, pic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ProfilePic", "Picture doesn't exist, or can't find picture");
        }
    }

    private void savePicToAppStorage(Uri uri) {
        File dst = new File(getExternalFilesDir(null), "profilePic.jpg");

        try {
            File src = new File(getFromURIFilePath(uri));
            if (!src.exists()) {
                Snackbar.make(edEmail, getString(R.string.error_file_not_found), Snackbar.LENGTH_LONG).show();
            }

            FileChannel source = new FileInputStream(src).getChannel();
            FileChannel destination = new FileOutputStream(dst).getChannel();

            if (source != null) {
                destination.transferFrom(source, 0, source.size());
                source.close();

            }
            destination.close();
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(edEmail, getString(R.string.error_file_not_found), Snackbar.LENGTH_LONG).show();
        }
    }

    private String getFromURIFilePath(Uri uri) throws IOException {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) throw new IOException("Could not resolve the URI");
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(columnIndex);
        cursor.close();
        return s;
    }
}
