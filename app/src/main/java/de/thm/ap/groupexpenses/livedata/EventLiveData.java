package de.thm.ap.groupexpenses.livedata;

import android.arch.lifecycle.LiveData;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import javax.annotation.Nullable;

import de.thm.ap.groupexpenses.model.Event;

public class EventLiveData extends LiveData<Event> {
    private static final String TAG = "FbaseQueryLiveData";
    private DocumentReference docRef;
    private final MyValueEventListener listener = new MyValueEventListener();
    private ListenerRegistration listenerRegistration;
    private boolean listenerRemovePending = false;
    private final Handler handler = new Handler();

    public EventLiveData(DocumentReference docRef) {
        this.docRef = docRef;
    }

    private final Runnable removeListener = new Runnable() {
        @Override
        public void run() {
            listenerRegistration.remove();
            listenerRemovePending = false;
        }
    };

    @Override
    protected void onActive() {
        super.onActive();

        Log.d(TAG, "onActive");
        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener);
        }
        else {
            listenerRegistration = docRef.addSnapshotListener(listener);
        }
        listenerRemovePending = false;
    }

    @Override
    protected void onInactive() {
        super.onInactive();

        Log.d(TAG, "onInactive: ");
        // Listener removal is schedule on a two second delay
        handler.postDelayed(removeListener, 2000);
        listenerRemovePending = true;
    }

    private class MyValueEventListener implements EventListener<DocumentSnapshot> {
        @Override
        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
            if (e != null){
                Log.e(TAG, "Can't listen to query snapshots: " + documentSnapshot + ":::" + e.getMessage());
                return;
            }
            if(documentSnapshot != null){
                setValue(documentSnapshot.toObject(Event.class));
            }
        }
    }
}
