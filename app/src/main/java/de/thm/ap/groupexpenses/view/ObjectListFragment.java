package de.thm.ap.groupexpenses.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.Stats;

public class ObjectListFragment extends Fragment
{
    private View view;
    private ListView object_listView;
    private List<Object> objectList;
    private CustomCallLogListAdapter adapter;
    private RelativeLayout headerLayout;
    private View headerView;

    ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onFragmentObjectClick(Object object);
        void onCreateBtnClick();
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            itemClickListener = (ItemClickListener)activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_object_list, container,false);
        }else {
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
        return view;
    }

    public void createFragmentObjects(List<Object> objects, String type){
        objectList = objects;
        TextView noObjects_textView = view.findViewById(R.id.fragment_no_object_text);

        if(!objects.isEmpty()){
            init(objects, type);
        } else {
            switch (type){
                case "Event":
                    noObjects_textView.setText(R.string.no_events);
                    break;
                case "Position":
                    noObjects_textView.setText(R.string.no_positions);
                    break;
            }
        }
        FloatingActionButton createObjBtn = view.findViewById(R.id.create_object_btn);
        createObjBtn.setOnClickListener(view -> createObjectBtnClicked());
    }

    public void updateFragmentObjects(List<Object> objects, String type){
        objectList = objects;

        if(objects.size() == 1){    // first object was just added to list
            init(objects, type);
        } else if(objects.size() > 0){
            //adapter.clear();
            //adapter.addAll(objects);
            // why does clear and add all not work? code below works (maybe because no database)
            adapter = new CustomCallLogListAdapter(getActivity(),
                    R.layout.fragment_object_list_row, objectList);
            object_listView = view.findViewById(R.id.fragment_listView);
            object_listView.setAdapter(adapter);
            object_listView.setOnItemClickListener((parent, view, position, id) ->
                    itemSelected(object_listView.getItemAtPosition(position)));

            updateTotalBalance(objects, type);
        }

    }

    private void init(List<Object> objects, String type){
        view.findViewById(R.id.fragment_no_object_text).setVisibility(View.GONE);
        headerView = getLayoutInflater().inflate(R.layout.fragment_object_list_header, null);
        headerLayout = headerView.findViewById(R.id.object_list_header_layout);
        TextView obj_name = headerView.findViewById(R.id.object_balance_summary_text);

        switch(type){   // init individual object strings and colors
            case "Event":
                obj_name.setText(R.string.event_total_balance);
                break;
            case "Position":
                obj_name.setText(R.string.position_total_balance);
                headerLayout.setBackgroundColor(Color
                        .parseColor("#95aebc"));    // grey
                break;
        }
        updateTotalBalance(objects, type);

        adapter = new CustomCallLogListAdapter(getActivity(),
                R.layout.fragment_object_list_row, objectList);
        object_listView = view.findViewById(R.id.fragment_listView);
        object_listView.addHeaderView(headerView);
        object_listView.setAdapter(adapter);
        object_listView.setOnItemClickListener((parent, view, position, id) ->
                itemSelected(object_listView.getItemAtPosition(position)));
    }

    private void updateTotalBalance(List<Object> objects, String type){
        TextView obj_val = headerView.findViewById(R.id.object_balance_summary_val);
        float balance;

        switch (type){
            case "Event":
                List<Event> eventList = (List<Event>)(List<?>) objects;
                balance = Stats.getBalance(eventList);

                obj_val.setText(new DecimalFormat("0.00").format(balance)
                        + " " + getString(R.string.euro));

                if(balance < 0)
                    headerLayout.setBackgroundColor(Color
                            .parseColor("#ef4545"));    // red
                break;

            case "Position":
                balance = 0;
                for(int idx = 0; idx < objects.size(); ++idx)
                    balance += ((Position)objects.get(idx)).getValue();

                obj_val.setText(new DecimalFormat("0.00").format(balance)
                        + " " + getString(R.string.euro));
                break;

            default:

        }
    }

    public void itemSelected(Object object){
        itemClickListener.onFragmentObjectClick(object);
    }

    public void createObjectBtnClicked(){
        itemClickListener.onCreateBtnClick();
    }

    private class CustomCallLogListAdapter extends ArrayAdapter<Object> {
        private List<Object> retrievedObjects;
        private Context context;
        private int resource;
        private View view;
        private Holder holder;
        private Object m_object;
        public CustomCallLogListAdapter(Context context, int resource,List<Object> objects) {
            super(context, resource, objects);
            this.context = context;
            this.resource = resource;
            this.retrievedObjects = objects;
        }

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent,false);
            holder = new Holder();
            holder.object_name = view.findViewById(R.id.name);
            holder.object_balance = view.findViewById(R.id.balance);
            m_object = retrievedObjects.get(index);

            if(m_object instanceof Event){
                Event event = (Event)m_object;
                holder.object_name.setText(event.getName());
                holder.object_balance.setText(new DecimalFormat("0.00")
                        .format(Stats.getEventBalance(event)) + " " + getString(R.string.euro));
            } else if(m_object instanceof Position){
                Position position = (Position) m_object;
                holder.object_name.setText(position.getTopic());
                holder.object_balance.setText(new DecimalFormat("0.00")
                        .format( position.getValue())+ " " + getString(R.string.euro));
            }
            return view;
        }

        private class Holder {
            TextView object_name;
            TextView object_balance;
        }
    }
}

