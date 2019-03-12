package de.thm.ap.groupexpenses.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class foregroundService extends Service {

    @Override
    /**
     * called if service is created for the first time
     */
    public void onCreate() {
        super.onCreate();
    }

    @Override
    /**
     * Triggers everytime the service is started
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    /**
     * Triggers when the service is terminated
     */
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
