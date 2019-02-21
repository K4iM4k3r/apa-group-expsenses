package de.thm.ap.groupexpenses;


import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ConfirmWorker extends Worker {

    public ConfirmWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            return currentUser.isEmailVerified() ? Result.success() : Result.failure();
        }
        else {
            return Result.failure();
        }
    }
}
