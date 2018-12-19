package de.thm.ap.groupexpenses.fragment;

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
    private CustomCallLogListAdapter adapter;
    //private RelativeLayout headerLayout;
    private View headerView;
    private Event relatedEventToPosition;

    ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onFragmentObjectClick(Object object);
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

    public void createFragmentObjects(List<Object> objectList, String type){
        TextView noObjects_textView = view.findViewById(R.id.fragment_no_object_text);

        if(!objectList.isEmpty()){
            init(objectList, type);
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
    }

    public void updateFragmentObjects(List<Object> objectList, String type){
        if(objectList.size() == 1){    // first object was just added to list
            init(objectList, type);
        } else if(objectList.size() > 0){   // there are already objects in list
            //adapter.clear();
            //adapter.addAll(objects);
            // why does clear and add all not work? code below works (maybe because no database)
            adapter = new CustomCallLogListAdapter(getActivity(),
                    R.layout.fragment_object_list_row, objectList, type);
            object_listView = view.findViewById(R.id.fragment_listView);
            object_listView.setAdapter(adapter);
            object_listView.setOnItemClickListener((parent, view, position, id) ->
                    itemSelected(object_listView.getItemAtPosition(position)));

            updateTotalBalance(objectList, type);
        }

    }

    private void init(List<Object> objectList, String type){
        view.findViewById(R.id.fragment_no_object_text).setVisibility(View.GONE);
        headerView = getLayoutInflater().inflate(R.layout.fragment_object_list_header, null);
        //headerLayout = headerView.findViewById(R.id.object_list_header_layout);

        switch(type){   // init individual object strings and colors
            case "Event":
                // empty
                break;
            case "Position":
                // last el of list is related Event to this Position- save it locally and rm it
                // from objectList
                Event event = (Event)objectList.get(objectList.size() - 1);
                relatedEventToPosition = event;
                objectList.remove(objectList.size() - 1);
                break;
        }
        updateTotalBalance(objectList, type);

        adapter = new CustomCallLogListAdapter(getActivity(),
                R.layout.fragment_object_list_row, objectList, type);
        object_listView = view.findViewById(R.id.fragment_listView);
        object_listView.addHeaderView(headerView);
        object_listView.setAdapter(adapter);
        object_listView.setOnItemClickListener((parent, view, position, id) ->
                itemSelected(object_listView.getItemAtPosition(position)));
    }

    private void updateTotalBalance(List<Object> objectList, String type){
        TextView obj_val = headerView.findViewById(R.id.object_balance_summary_val);
        float balance = 0;

        switch (type){
            case "Event":
                List<Event> eventList = (List<Event>)(List<?>) objectList;
                balance = Stats.getBalance(eventList);

                obj_val.setText(new DecimalFormat("0.00").format(balance)
                        + " " + getString(R.string.euro));
                break;

            case "Position":
                for(int idx = 0; idx < objectList.size(); ++idx)
                    balance += Stats.getPositionBalance((Position)objectList.get(idx),
                            relatedEventToPosition);

                obj_val.setText(new DecimalFormat("0.00").format(balance)
                        + " " + getString(R.string.euro));
                break;

            default:

        }
    }

    public void itemSelected(Object object){
        itemClickListener.onFragmentObjectClick(object);
    }

    private class CustomCallLogListAdapter extends ArrayAdapter<Object> {
        private List<Object> retrievedObjects;
        private Context context;
        private int resource;
        private View view;
        String type;
        private Holder holder;
        private Object m_object;
        public CustomCallLogListAdapter(Context context, int resource, List<Object> objects, String type) {
            super(context, resource, objects);
            this.type = type;
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
            holder.object_creator = view.findViewById(R.id.creator);
            holder.object_balance = view.findViewById(R.id.balance);
            m_object = retrievedObjects.get(index);
            float balance = 0;

            switch(type){
                case "Event":
                    Event event = (Event)m_object;
                    balance = Stats.getEventBalance(event);
                    holder.object_name.setText(event.getName());
                    holder.object_creator.setText(event.getCreator());
                    holder.object_balance.setText(new DecimalFormat("0.00")
                            .format(balance) + " " + getString(R.string.euro));
                    break;
                case "Position":
                    Position position = (Position) m_object;
                    balance = Stats.getPositionBalance(position, relatedEventToPosition);
                    holder.object_name.setText(position.getTopic());
                    holder.object_creator.setText(position.getCreator().toString());
                    holder.object_balance.setText(new DecimalFormat("0.00")
                            .format(balance)+ " " + getString(R.string.euro));
                    break;
                default:

            }
            if(balance < 0)
                holder.object_balance.setTextColor(Color
                        .parseColor("#ef4545"));    // red
            else
                holder.object_balance.setTextColor(Color
                        .parseColor("#2ba050"));    // green
            return view;
        }

        private class Holder {
            TextView object_name;
            TextView object_creator;
            TextView object_balance;
        }
    }
}

