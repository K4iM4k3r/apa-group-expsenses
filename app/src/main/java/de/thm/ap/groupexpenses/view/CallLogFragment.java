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

public class CallLogFragment extends Fragment
{
    private View view;
    private ListView object_listView;
    private List<Object> objectList;

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
            view=inflater.inflate(R.layout.fragment_layout, container,false);
        }else {
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
        return view;
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

    public void setFragmentObjects(List<Object> objects, String type){
        objectList = objects;
        TextView noObjects_textView = view.findViewById(R.id.fragment_no_object_text);
        View headerView = getLayoutInflater().inflate(R.layout.object_list_header, null);

        if(!objects.isEmpty()){
            noObjects_textView.setVisibility(View.GONE);
            TextView obj_name = headerView.findViewById(R.id.object_balance_summary_text);
            TextView obj_val = headerView.findViewById(R.id.object_balance_summary_val);
            RelativeLayout headerLayout = headerView.findViewById(R.id.object_list_header_layout);
            float balance;

            switch (type){
                case "Event":
                    List<Event> eventList = (List<Event>)(List<?>) objects;
                    balance = Stats.getBalance(eventList);

                    obj_name.setText(R.string.event_total_balance);

                    obj_val.setText(new DecimalFormat("0.00").format(balance)
                            + " " + getString(R.string.euro));

                    if(balance < 0)
                        headerLayout.setBackgroundColor(Color
                                .parseColor("#ef4545"));    // red
                    break;

                case "Position":
                    obj_name.setText(R.string.position_total_balance);
                    balance = 0;
                    for(int idx = 0; idx < objects.size(); ++idx)
                        balance += ((Position)objects.get(idx)).getValue();

                    obj_val.setText(new DecimalFormat("0.00").format(balance)
                            + " " + getString(R.string.euro));

                    headerLayout.setBackgroundColor(Color
                            .parseColor("#95aebc"));    // grey
                    break;

                    default:

            }

            CustomCallLogListAdapter adapter = new CustomCallLogListAdapter(getActivity(),
                    R.layout.row_call_log_layout, objectList);
            object_listView = view.findViewById(R.id.fragment_listView);
            object_listView.addHeaderView(headerView);
            object_listView.setAdapter(adapter);

            FloatingActionButton createObjBtn = view.findViewById(R.id.create_object_btn);
            createObjBtn.setOnClickListener(view -> createObjectBtnClicked());

            object_listView.setOnItemClickListener((parent, view, position, id) ->
                    itemSelected(object_listView.getItemAtPosition(position)));
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

    public void itemSelected(Object object){
        itemClickListener.onFragmentObjectClick(object);
    }

    public void createObjectBtnClicked(){
        itemClickListener.onCreateBtnClick();
    }
}

