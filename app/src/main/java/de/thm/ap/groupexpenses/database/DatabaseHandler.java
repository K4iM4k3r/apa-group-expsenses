package de.thm.ap.groupexpenses.database;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.User;

public class DatabaseHandler {

    public interface Callback<T>{
        void onResult(T result);
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
     * Looks if an User with uid exits and then call the callback
     * @param uid UserId of the User
     * @param callback is called when user exits
     */
    public static void queryUser(String uid, Callback<User> callback){
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
        documentReference.set(event).addOnCompleteListener(c ->{
            queryUser(event.getCreatorId(), creator -> {
                if(creator != null){
                    creator.addEvent(event.getEid());
                    updateUser(creator);
                }
            });

            event.getMembers().forEach(m -> queryUser(m, member -> {
                if(member != null){
                    member.addEvent(event.getEid());
                    updateUser(member);
                }
            }));
        });
    }

    public static void onUserChangeListener(String uid, Callback<User> callback){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS).document(uid);
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
//                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                User user = snapshot.toObject(User.class);
//                Log.d(TAG, "Current data: " + snapshot.getData());
                callback.onResult(user);
            } else {
                callback.onResult(null);
//                Log.d(TAG, "Current data: null");
            }
        });
    }


    /**
     * Looks if an Event with eid exits and than call the callback with the result
     * @param eid Event Id
     * @param callback Callback
     */
    public static void queryEvent(String eid, Callback<Event> callback){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_EVENTS).document(eid);
        docRef.get().addOnSuccessListener(documentSnapshot -> callback.onResult(documentSnapshot.toObject(Event.class)));
    }


    public static void getAllUserEvents(String uid, Callback<List<Event>> callback){
        List<Event> result = new ArrayList<>();
        queryUser(uid, user -> {
            final int lengthEvents = user.getEvents().size();
            user.getEvents().forEach(eid -> queryEvent(eid, event -> {
                result.add(event);
                if(result.size() == lengthEvents){
                    callback.onResult(result);
                }
            }));
        });
    }

}


