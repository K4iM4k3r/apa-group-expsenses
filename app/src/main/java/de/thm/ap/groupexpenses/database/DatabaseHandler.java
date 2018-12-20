package de.thm.ap.groupexpenses.database;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import de.thm.ap.groupexpenses.model.User;

public class DatabaseHandler {

    public static void createUser(User user){
        FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS)
                .document(user.getUid())
                .set(user);
    }
    
    public static void createUserWithFeedback(User user, OnSuccessListener<Void> successListener, OnFailureListener failureListener){
        FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS)
                .document(user.getUid())
                .set(user).addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

}
