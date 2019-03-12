package de.thm.ap.groupexpenses.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.view.activity.BaseActivity;

public class ForegroundService extends Service {

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
        String input= intent.getStringExtra("inputExtra");
        Intent notifcationIntent = new Intent(this, BaseActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifcationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, App.PaymentID)
                .setContentTitle("Example Payment")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_euro_symbol_black_24dp)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        return START_NOT_STICKY;
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
