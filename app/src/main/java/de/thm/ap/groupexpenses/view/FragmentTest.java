package de.thm.ap.groupexpenses.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.thm.ap.groupexpenses.R;

public class FragmentTest extends Fragment {

    private TextView fragmentText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        fragmentText = view.findViewById(R.id.fragment_test);

        return view;
    }

    public void setFragmentText(String text){
        fragmentText.setText(text);
    }

}
