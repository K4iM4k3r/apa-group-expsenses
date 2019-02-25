package de.thm.ap.groupexpenses.view.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.model.Stats;
import de.thm.ap.groupexpenses.model.User;

public class ProfileInfoDialog {
    private AlertDialog.Builder profileViewDialog;
    private AlertDialog dialog;
    private User user;
    private View view;
    private TextView user_full_name, user_nickname, user_info, user_join_date;
    private CircleImageView user_image;
    private ImageView closeBtn;
    private Context context;

    public ProfileInfoDialog(User user, Context context) {
        this.context = context;
        this.user = user;
        profileViewDialog = new AlertDialog.Builder(context);
        view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.dialog_profile_view, null);
        createDialog();
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    private void createDialog() {
        user_full_name = view.findViewById(R.id.profile_dialog_full_name);
        user_nickname = view.findViewById(R.id.profile_dialog_nickname);
        user_info = view.findViewById(R.id.profile_dialog_userInfo);
        user_join_date = view.findViewById(R.id.profile_dialog_user_join_date);
        user_image = view.findViewById(R.id.profile_dialog_userPic);
        closeBtn = view.findViewById(R.id.profile_dialog_close_imageView);

        String user_full_name_string = user.getFirstName() + " " + user.getLastName();
        user_full_name.setText(user_full_name_string);
        user_nickname.setText(user.getNickname());
        if (user.getProfilePic() != null) {
            DatabaseHandler.getUserProfilePic(context, user.getUid(), opPictureUri ->
                    opPictureUri.ifPresent(user_image::setImageURI));
        }
        user_info.setText(user.getInfo());
        user_join_date.setText(user.getDateString());

        // close btn clicked
        closeBtn.setOnTouchListener((v, event) -> {
            dialog.dismiss();
            return false;
        });

        profileViewDialog.setView(view);
        dialog = profileViewDialog.create();
        dialog.show();
    }
}