package de.thm.ap.groupexpenses.view.dialog;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.MessageHelper;

import static de.thm.ap.groupexpenses.App.getContext;

public class InviteDialog {
    private AlertDialog.Builder inviteDialog;
    AlertDialog dialog;
    private View view;
    private Event selectedEvent;
    private Context context;

    public InviteDialog(Context context, Event selectedEvent) {
        this.context = context;
        this.selectedEvent = selectedEvent;
        inviteDialog = new AlertDialog.Builder(context);
        view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_invite_view, null);
        createDialog();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createDialog() {
        RelativeLayout sendMailLayout = view.findViewById(R.id.dialog_invite_mail_layout);
        LinearLayout sendWhatsappLayout = view.findViewById(R.id.dialog_invite_whatsapp_layout);
        LinearLayout copyLinkLayout = view.findViewById(R.id.dialog_invite_copyLink_layout);
        ImageView closeBtn = view.findViewById(R.id.dialog_invite_close_imageView);

        // send invite btn clicked
        MessageHelper messageHelper = new MessageHelper(context);
        // generate link to invite people
        String inviteURL = App.BASE_URL + selectedEvent.getEid();
        String infoText = "Hey, click the following link to join me on \"" + selectedEvent.getName() + "\".\n\n" +
                inviteURL + "\n\n" +
                "Make sure to have Group-Expenses-Omran installed!\n\n" +
                "Cya!\n" +
                App.CurrentUser.getNickname();

        sendMailLayout.setOnClickListener(v -> {
            String subject = "Invite to join " + selectedEvent.getName() + " in GEO!";
            messageHelper.sendViaMail("", subject, infoText);
            dialog.dismiss();
        });

        sendWhatsappLayout.setOnClickListener(v -> {
            MessageHelper.Providers provider = MessageHelper.Providers.WHATSAPP;
            messageHelper.sendVia(provider, infoText);
            dialog.dismiss();
        });

        copyLinkLayout.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Invite URL", inviteURL);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "copied invite link to clipboard", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        // close btn clicked
        closeBtn.setOnClickListener(v -> dialog.dismiss());

        inviteDialog.setView(view);
        dialog = inviteDialog.create();
        dialog.show();
    }
}