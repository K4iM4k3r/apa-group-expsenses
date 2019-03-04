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
    private CircleImageView user_image;
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
        TextView user_full_name = view.findViewById(R.id.profile_dialog_full_name);
        TextView user_nickname = view.findViewById(R.id.profile_dialog_nickname);
        TextView user_info = view.findViewById(R.id.profile_dialog_userInfo);
        TextView user_join_date = view.findViewById(R.id.profile_dialog_user_join_date);
        user_image = view.findViewById(R.id.profile_dialog_userPic);
        ImageView closeBtn = view.findViewById(R.id.profile_dialog_close_imageView);

        String userFirstName = user.getFirstName();
        String userLastName = user.getLastName();
        String userNickname = user.getNickname();
        String userInfo = user.getInfo();

        final int USER_FIRST_NAME_MAX_LENGTH = 20;
        if (userFirstName.length() > USER_FIRST_NAME_MAX_LENGTH) {
            userFirstName = userFirstName.substring(0, USER_FIRST_NAME_MAX_LENGTH) + "...";
        }
        final int USER_LAST_NAME_MAX_LENGTH = 20;
        if (userLastName.length() > USER_LAST_NAME_MAX_LENGTH) {
            userLastName = userLastName.substring(0, USER_LAST_NAME_MAX_LENGTH) + "...";
        }
        final int USER_NICKNAME_MAX_LENGTH = 20;
        if (userNickname.length() > USER_NICKNAME_MAX_LENGTH) {
            userNickname = userNickname.substring(0, USER_NICKNAME_MAX_LENGTH) + "...";
        }
        final int USER_INFO_MAX_LENGTH = 200;
        if (userInfo.length() > USER_INFO_MAX_LENGTH) {
            userInfo = userInfo.substring(0, USER_INFO_MAX_LENGTH) + "...";
        }

        String user_full_name_string = userFirstName + " " + userLastName;
        user_full_name.setText(user_full_name_string);
        user_nickname.setText(userNickname);
        user_info.setText(userInfo);
        user_join_date.setText(user.joinDateToString());
        if (user.getProfilePic() != null) {
            DatabaseHandler.getUserProfilePic(context, user.getUid(), opPictureUri ->
                    opPictureUri.ifPresent(user_image::setImageURI));
        }

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