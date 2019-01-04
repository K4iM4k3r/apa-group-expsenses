package de.thm.ap.groupexpenses.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.List;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.Stats;
import de.thm.ap.groupexpenses.model.User;

public class ObjectListFragment<T> extends Fragment
{
    private View view;
    private ListView object_listView;
    private CustomCallLogListAdapter adapter;
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

    public void createFragmentObjects(List<T> objectList, String type){
        TextView noObjects_textView = view.findViewById(R.id.fragment_no_object_text);

        if(!objectList.isEmpty()){
            if(type.equals("Position"))
                setRelatedEventToPosition(objectList);
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

    public void updateFragmentObjects(List<T> objectList, String type){
        switch (type){
            case "Removal":
                updateListView(objectList, type);
                updateTotalBalance(objectList, type);
                break;
            case "Position":
                setRelatedEventToPosition(objectList);
                // don't break here!
            case "Event":
                if(objectList.size() == 1){     // first Event/Position was just added to list
                    init(objectList, type);
                } else if(objectList.size() > 0){   // there are already objects in list
                    updateListView(objectList, type);
                    updateTotalBalance(objectList, type);
                }
                break;

                default:

        }


    }

    private void init(List<T> objectList, String type){
        view.findViewById(R.id.fragment_no_object_text).setVisibility(View.GONE);
        headerView = getLayoutInflater().inflate(R.layout.fragment_object_list_header, null);
        updateTotalBalance(objectList, type);

        adapter = new CustomCallLogListAdapter(getActivity(),
                R.layout.fragment_object_list_row, objectList, type);
        object_listView = view.findViewById(R.id.fragment_listView);
        object_listView.addHeaderView(headerView);
        object_listView.setAdapter(adapter);
        object_listView.setOnItemClickListener((parent, view, position, id) ->
                itemSelected(object_listView.getItemAtPosition(position)));
    }

    private void updateListView(List<T> objectList, String type){
        //adapter.clear();
        //adapter.addAll(objects);
        // why does clear and add all not work? code below works (maybe because no database)
        adapter = new CustomCallLogListAdapter(getActivity(),
                R.layout.fragment_object_list_row, objectList, type);
        object_listView = view.findViewById(R.id.fragment_listView);
        object_listView.setAdapter(adapter);
        object_listView.setOnItemClickListener((parent, view, position, id) ->
                itemSelected(object_listView.getItemAtPosition(position)));
    }

    private void updateTotalBalance(List<T> objectList, String type){
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

        TextView headerVal = headerView.findViewById(R.id.object_balance_summary_val);
        if(balance < 0)
            headerVal.setTextColor(Color.parseColor("#ef4545"));    // red
        else
            headerVal.setTextColor(Color.parseColor("#2ba050"));    // green
    }

    private void setRelatedEventToPosition(List<T> positionList){
        // last el of list is the related Event to this Position- save it locally and
        // rm it from positionList
        int lastIdx = positionList.size() - 1;
        relatedEventToPosition = (Event)positionList.get(lastIdx);
        positionList.remove(lastIdx);
    }

    public void itemSelected(Object object){
        itemClickListener.onFragmentObjectClick(object);
    }

    private class CustomCallLogListAdapter extends ArrayAdapter<T> {
        private List<T> retrievedObjects;
        private Context context;
        private int resource;
        private View view;
        String type;
        private Holder holder;
        private Object m_object;
        public CustomCallLogListAdapter(Context context, int resource, List<T> objects, String type) {
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
            String fromPart = getString(R.string.from);;
            String creatorPart;
            String wholePart;
            Spannable spannable;

            switch(type){
                case "Event":
                    Event event = (Event)m_object;
                    balance = Stats.getEventBalance(event);
                    holder.object_name.setText(event.getName());
                    if(event.getCreatorId().equals(App.CurrentUser.getUid()))
                        creatorPart = getString(R.string.you);
                    else {
                        String uid = event.getCreatorId();
                        creatorPart = uid;
                    }
                    wholePart = fromPart + " " + creatorPart;
                    spannable = new SpannableString(wholePart);
                    spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#3a90e0")),
                            fromPart.length(), wholePart.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.object_creator.setText(spannable, TextView.BufferType.SPANNABLE);
                    holder.object_balance.setText(new DecimalFormat("0.00")
                            .format(balance) + " " + getString(R.string.euro));
                    break;
                case "Position":
                    Position position = (Position) m_object;
                    balance = Stats.getPositionBalance(position, relatedEventToPosition);
                    holder.object_name.setText(position.getTopic());
                    if(position.getCreatorId().equals(App.CurrentUser.getUid()))
                        creatorPart = getString(R.string.you);
                    else creatorPart = position.getCreatorId();
                    wholePart = fromPart + " " + creatorPart;
                    spannable = new SpannableString(wholePart);
                    spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#3a90e0")),
                            fromPart.length(), wholePart.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.object_creator.setText(spannable, TextView.BufferType.SPANNABLE);
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

