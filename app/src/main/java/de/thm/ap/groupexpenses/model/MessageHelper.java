package de.thm.ap.groupexpenses.model;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static de.thm.ap.groupexpenses.model.MessageHelper.Providers.*;

public class MessageHelper {

    private Context context;

    public MessageHelper(Context context){
        this.context = context;
    }

    public enum Providers {
        WHATSAPP("com.whatsapp"), LINKEDIN("com.linkedin.android"), TWITTER ("com.twitter.android"),
        FACEBOOK("com.facebook.katana"), GOOGLEPLUS("com.google.android.apps.plus");

        private final String packageName;

        Providers(String packagename){
            this.packageName = packagename;
        }
    }

    public void sendVia(Providers provider, String message){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage(provider.packageName);
        intent.putExtra(Intent.EXTRA_TEXT, message);//
        context.startActivity(Intent.createChooser(intent, message));
    }

    public void sendViaMail(String recipient, String subject, String body){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{recipient});
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT   , body);
        try {
            context.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
