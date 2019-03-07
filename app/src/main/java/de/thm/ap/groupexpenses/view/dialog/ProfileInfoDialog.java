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

        String userFirstNameString = user.getFirstName();
        String userLastNameString = user.getLastName();
        String userNicknameString = user.getNickname();
        String userInfoString = user.getInfo();

        final int USER_FIRST_NAME_MAX_LENGTH = 20;
        if (userFirstNameString.length() > USER_FIRST_NAME_MAX_LENGTH) {
            userFirstNameString = userFirstNameString.substring(0, USER_FIRST_NAME_MAX_LENGTH) + "...";
        }
        final int USER_LAST_NAME_MAX_LENGTH = 20;
        if (userLastNameString.length() > USER_LAST_NAME_MAX_LENGTH) {
            userLastNameString = userLastNameString.substring(0, USER_LAST_NAME_MAX_LENGTH) + "...";
        }
        final int USER_NICKNAME_MAX_LENGTH = 20;
        if (userNicknameString.length() > USER_NICKNAME_MAX_LENGTH) {
            userNicknameString = userNicknameString.substring(0, USER_NICKNAME_MAX_LENGTH) + "...";
        }
        final int USER_INFO_MAX_LENGTH = 200;
        if (userInfoString.length() > USER_INFO_MAX_LENGTH) {
            userInfoString = userInfoString.substring(0, USER_INFO_MAX_LENGTH) + "...";
        }

        String user_full_name_string = userFirstNameString + " " + userLastNameString;
        user_full_name.setText(user_full_name_string);
        user_nickname.setText(userNicknameString);
        if(user.getInfo().equals("")){
            user_info.setText(context.getString(R.string.air_of_mystery));
        } else {
            user_info.setText(userInfoString);
        }

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