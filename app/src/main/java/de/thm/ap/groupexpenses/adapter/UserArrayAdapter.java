package de.thm.ap.groupexpenses.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.model.User;

public class UserArrayAdapter extends ArrayAdapter<User> {
    private final static int VIEW_RESOURCE = R.layout.user_list_item;


    public UserArrayAdapter(Context ctx, List<User> users) {
            super(ctx, VIEW_RESOURCE, users);

    }

    @NonNull
    @Override
    public View getView(int pos, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(VIEW_RESOURCE, null);
        }

        User user = getItem(pos);
        if(user != null){
            CircleImageView picture = view.findViewById(R.id.user_pic);
            if(user.getProfilePic() != null){
                DatabaseHandler.getUserProfilePic(parent.getContext(), user.getUid(), opPictureUri -> opPictureUri.ifPresent(picture::setImageURI));
            }
            TextView nickname = view.findViewById(R.id.list_item_nickname);
            nickname.setText(user.getNickname());
            TextView firstname = view.findViewById(R.id.list_item_firstname);
            firstname.setText(user.getFirstName());
            TextView lastname = view.findViewById(R.id.list_item_lastname);
            lastname.setText(user.getLastName());
        }

        return view;
    }
}
