package de.thm.ap.groupexpenses.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String testmessage = intent.getStringExtra("paymentKey");
        Toast.makeText(context, testmessage, Toast.LENGTH_SHORT).show();
    }
}
