package de.thm.ap.groupexpenses.database;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.livedata.EventListLiveData;
import de.thm.ap.groupexpenses.livedata.EventLiveData;
import de.thm.ap.groupexpenses.model.User;
import de.thm.ap.groupexpenses.livedata.UserListLiveData;
import de.thm.ap.groupexpenses.livedata.UserLiveData;

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

    public static void isNicknameExist(String nickname, Callback<Boolean> callback){
        CollectionReference usersRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS);
        Query query = usersRef.whereEqualTo(Constants.DOC_USERS_NICKNAME, nickname);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> callback.onResult(!queryDocumentSnapshots.isEmpty()));
    }

    /**
     * Looks if an user with the nickname exits and give the user back in the callback
     * @param nickname user nickname
     * @param callback give the searched user back or null
     */
    public static void queryUserByNickname(String nickname, Callback<User> callback){
        CollectionReference usersRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS);
        Query query = usersRef.whereEqualTo(Constants.DOC_USERS_NICKNAME, nickname);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {

            if(!queryDocumentSnapshots.isEmpty()){
                callback.onResult(queryDocumentSnapshots.getDocuments().get(0).toObject(User.class));
            }
            else {
                callback.onResult(null);
            }
        });
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
                if(member != null && !member.getEvents().contains(event.getEid())){
                    member.addEvent(event.getEid());
                    updateUser(member);
                }
            }));
        });
    }

    public static void updateEvent(Event event){
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_EVENTS).document(event.getEid());
        documentReference.set(event).addOnCompleteListener(c -> event.getMembers().forEach(m -> queryUser(m, member -> {
            if(member != null && !member.getEvents().contains(event.getEid())){
                member.addEvent(event.getEid());
                updateUser(member);
            }
        })));
    }

    public static UserLiveData qetUserLiveData(String uid){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS).document(uid);
        return new UserLiveData(docRef);
    }

    public static void getUserProfilePic(Context ctx, String uid, Callback<Optional<Uri>> callback){
        String filename = uid + ".jpg";
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("ProfilePictures").child(filename);
        File file = new File(ctx.getExternalFilesDir(null), filename);

        storageRef.getFile(file).addOnSuccessListener(taskSnapshot -> {
            callback.onResult(Optional.of(Uri.fromFile(file)));
            // Local temp file has been created
        }).addOnFailureListener(exception -> {
            callback.onResult(Optional.empty());
        });
    }

    @Deprecated
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
    @SuppressWarnings("WeakerAccess")
    public static void queryEvent(String eid, Callback<Event> callback){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_EVENTS).document(eid);
        docRef.get().addOnSuccessListener(documentSnapshot -> callback.onResult(documentSnapshot.toObject(Event.class)));
    }


    public static EventLiveData getEventLiveData(String eid){
        DocumentReference docRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_EVENTS).document(eid);
        return new EventLiveData(docRef);
    }

    public static EventListLiveData getEventListLiveData(String uid){
        CollectionReference usersRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_EVENTS);
        Query query = usersRef.whereArrayContains(Constants.DOC_EVENTS_MEMBERS, uid);
        return new EventListLiveData(query);
    }

    public static UserListLiveData getAllMembersOfEvent(String eid){
        CollectionReference usersRef = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS);
        Query query = usersRef.whereArrayContains(Constants.DOC_USERS_EVENTS, eid);
        return new UserListLiveData(query);
    }

    /**
     * @deprecated use instead getEventListLiveData
     * @param uid
     * @param callback
     */
    @Deprecated
    public static void getAllUserEvents(String uid, Callback<List<Event>> callback){
        List<Event> result = new ArrayList<>();
        queryUser(uid, user -> {
            final int lengthEvents = user.getEvents().size();
            if(lengthEvents == 0){
                callback.onResult(result);
            }
            else{
                user.getEvents().forEach(eid -> queryEvent(eid, event -> {
                    result.add(event);
                    if(result.size() == lengthEvents){
                        callback.onResult(result);
                    }
                }));
            }
        });
    }


    public static void getAllFriendsOfUser(String uid, Callback<List<User>> callback) {
        List<User> result = new ArrayList<>();
        queryUser(uid, user -> {
            if (user.getFriendsIds() != null) {
                final int lengthFriends = user.getFriendsIds().size();
                if (lengthFriends == 0) {
                    callback.onResult(result);
                } else {
                    user.getFriendsIds().forEach(fid -> queryUser(fid, friend -> {
                        result.add(friend);
                        if (result.size() == lengthFriends) {
                            callback.onResult(result);
                        }
                    }));
                }
            }
        });
    }

    /**
     * @deprecated Use getAllMembersOfEvent without callback
     */
    @Deprecated
    public static void getAllMembersOfEvent(String eid, Callback<List<User>> callback){
        List<User> result = new ArrayList<>();
        queryEvent(eid, event -> {
            if(event.getMembers() != null){
                final int lengthMembers = event.getMembers().size();
                if(lengthMembers == 0){
                    callback.onResult(result);
                }
                else{
                    event.getMembers().forEach(fid -> queryUser(fid, member -> {
                        result.add(member);
                        if(result.size() == lengthMembers){
                            callback.onResult(result);
                        }
                    }));
                }
            }
        });
    }

}


