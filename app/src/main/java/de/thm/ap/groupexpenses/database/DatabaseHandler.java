package de.thm.ap.groupexpenses.database;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.User;

public class DatabaseHandler {

    public interface Callback{
        void onResult(User user);
    }

    /**
     * Update User Data if it exists, else create new User
     * @param user User with all Data
     */
    public static void updateUser(User user){
        FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS)
                .document(user.getUid())
                .set(user);
    }

    /**
     * Update User Data if it exists, else create new User and further you can add Listener
     * @param user User with all Data
     * @param successListener Successful run
     * @param failureListener Failure occurred
     */
    public static void updateUserWithFeedback(User user, OnSuccessListener<Void> successListener, OnFailureListener failureListener){
        FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS)
                .document(user.getUid())
                .set(user).addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }

    /**
     * Looks if an User with uid exits and than call the callback
     * @param uid UserId of the User
     * @param callback is called when user exits
     */
    public static void queryUser(String uid, Callback callback){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS).document(uid);
        docRef.get().addOnSuccessListener(documentSnapshot -> callback.onResult(documentSnapshot.toObject(User.class)));
    }

    /**
     * Create a Event with all Information
     * @param event Container of data
     */
    public static void createEvent(Event event){
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_EVENTS).document();
        event.setEid(documentReference.getId());
        documentReference.set(event);
    }

    /**
     * Create a Event with all Information and further you can give custom listener
     * @param event Container of data
     * @param successListener Successful added Event
     * @param failureListener Failure while adding
     */
    public static void createWithFeedbackEvent(Event event, OnSuccessListener<Void> successListener, OnFailureListener failureListener){
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_EVENTS).document();
        event.setEid(documentReference.getId());
        documentReference.set(event)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
}


