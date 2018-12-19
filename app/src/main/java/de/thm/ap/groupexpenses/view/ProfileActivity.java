package de.thm.ap.groupexpenses.view;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.Constants;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

public class ProfileActivity extends BaseActivity {
    private TextView tvEmail;
    private EditText edName;
    private EditText edFirst;
    private EditText edLast;
    private CircleImageView profile_pic;
    private Button btnSave;
    private final int REQUEST_IMAGE_PICK = 1;
    private final String TAG = getClass().getName();
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvEmail = findViewById(R.id.tvProfileEmail);
        edName = findViewById(R.id.edName);
        edFirst = findViewById(R.id.edFirstname);
        edLast = findViewById(R.id.edLastname);
        Button btnEdit = findViewById(R.id.edit_button);
        btnSave = findViewById(R.id.btn_save_profile);
        profile_pic = findViewById(R.id.profile_pic);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = FirebaseFirestore.getInstance();


        FirebaseUser user = super.auth.getCurrentUser();
        if(user != null){
            DocumentReference docRef = db.collection(Constants.COLLECTION_USERS).document(user.getUid());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        edFirst.setText(document.getString(Constants.DOC_USERS_FIRST_NAME));
                        edLast.setText(document.getString(Constants.DOC_USERS_LAST_NAME));
                        edName.setText(document.getString(Constants.DOC_USERS_NICKNAME));

                        tvEmail.setText(user.getEmail());
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });


        }

        btnEdit.setOnClickListener(l -> {
            edName.setEnabled(true);
            edLast.setEnabled(true);
            edFirst.setEnabled(true);
            btnSave.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
        });
        btnSave.setOnClickListener(l ->{
            showProgressDialog();
            if(isValidUserInput()){

                Map<String, Object> userInfo= new HashMap<>();
                userInfo.put(Constants.DOC_USERS_FIRST_NAME, edFirst.getText().toString());
                userInfo.put(Constants.DOC_USERS_LAST_NAME, edLast.getText().toString());
                userInfo.put(Constants.DOC_USERS_NICKNAME, edName.getText().toString());

                updateUserInfoInDB(userInfo);
                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                        .setDisplayName(edName.getText().toString())
                        .build();
                if (user != null) {
                    user.updateProfile(profileUpdate);
                }
                hideProgressDialog();
                finish();
            }
        });

        File pic = new File(getExternalFilesDir(null), "profilePic.jpg");
        if(pic.exists()){
            profile_pic.setImageURI(Uri.fromFile(pic));
        }

        profile_pic.setOnClickListener(l ->{
            pickPhoto();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            assert user != null;
            StorageReference storageRef = storage.getReference().child("ProfilePictures").child(user.getUid()+".jpg");
            File file = new File(getExternalFilesDir(null), "profilePic.jpg");
            Uri uriFile = Uri.fromFile(file);

            UploadTask uploadTask = storageRef.putFile(uriFile);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    Snackbar.make(tvEmail, getString(R.string.error_file_couldnt_upload), Snackbar.LENGTH_LONG).show();
                }

                // Continue with the task to get the download URL
                return storageRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Snackbar.make(tvEmail, getString(R.string.success_profile_pic_uploaded), Snackbar.LENGTH_LONG).show();
                    UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUri)
                            .build();
                    user.updateProfile(profileUpdate);

                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put(Constants.DOC_USERS_PROFILE_PIC_URL, downloadUri.toString());
                    updateUserInfoInDB(userInfo);
//                        Log.i(TAG, user.getPhotoUrl().toString());
                } else {
                    // Handle failures
                    Log.i(TAG, "Error:" + task.toString());

                }
            });
        });

    }

    private void updateUserInfoInDB(Map<String, Object> user) {
        db.collection(Constants.COLLECTION_USERS)
                .document(super.auth.getUid())
                .update(user)
                .addOnSuccessListener(l -> Log.d(TAG, "Userdata successfull written"))
                .addOnFailureListener(f -> Log.d(TAG, "User data couldnt written"));


    }

    private boolean isValidUserInput(){
        boolean valid = true;
        if(TextUtils.isEmpty(edName.getText())){
            edName.setError(getString(R.string.error_invalid_input));
            valid = false;
        }
        if(TextUtils.isEmpty(edFirst.getText())){
            edName.setError(getString(R.string.error_invalid_input));
            valid = false;
        }
        if(TextUtils.isEmpty(edLast.getText())){
            edName.setError(getString(R.string.error_invalid_input));
            valid = false;
        }
        return valid;
    }

    private void pickPhoto() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_IMAGE_PICK);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
        if(resultCode == RESULT_OK) {
            if(requestCode == REQUEST_IMAGE_PICK) {
                if(data == null || data.getData() == null) {
                    Log.i(TAG, "onResult Data is empty");
                    return;
                }

                Uri selectedImage = data.getData();
                profile_pic.setImageURI(selectedImage);
                savePicToAppStorage(selectedImage);
                compressPicture();
            }
        }

        compressPicture();
    }
    private void compressPicture() {
        File pic = new File(getExternalFilesDir(null), "profilePic.jpg");

        if(pic.exists()) {
            try {
                Bitmap bmp = BitmapFactory.decodeFile(pic.getPath());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
                InputStream in = new ByteArrayInputStream(bos.toByteArray());
                copyInputStreamToFile(in, pic);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.e("ProfilePic", "Picture doesn't exist, or can't find picture");
        }
    }

    private void savePicToAppStorage(Uri uri) {
        File dst = new File(getExternalFilesDir(null), "profilePic.jpg");

        try {
            File src = new File(getFromURIFilePath(uri));
            if(!src.exists()) {
                Snackbar.make(tvEmail, getString(R.string.error_file_not_found), Snackbar.LENGTH_LONG).show();
            }

            FileChannel source = new FileInputStream(src).getChannel();
            FileChannel destination = new FileOutputStream(dst).getChannel();

            if(source != null) {
                destination.transferFrom(source, 0, source.size());
                source.close();

            }
            destination.close();
        } catch(IOException e) {
            e.printStackTrace();
            Snackbar.make(tvEmail, getString(R.string.error_file_not_found), Snackbar.LENGTH_LONG).show();
        }
    }
    private String getFromURIFilePath(Uri uri) throws IOException {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if(cursor == null) throw new IOException("Could not resolve the URI");
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(columnIndex);
        cursor.close();
        return s;
    }
}
