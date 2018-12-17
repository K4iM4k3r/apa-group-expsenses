package de.thm.ap.groupexpenses.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.Stats;

public class CallLogFragment extends Fragment
{
    private View view;
    private ListView list_calllog;
    private List<Object> objectList;

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
            view=inflater.inflate(R.layout.fragment_call_log_layout, container,false);
        }else {
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
        return view;
    }

    private class CustomCallLogListAdapter extends ArrayAdapter<Object> {
        private List<Object> callLogData;
        private Context context;
        private int resource;
        private View view;
        private Holder holder;
        private Object m_object;
        public CustomCallLogListAdapter(Context context, int resource,List<Object> objects) {
            super(context, resource, objects);
            this.context=context;
            this.resource=resource;
            this.callLogData=objects;
        }

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent,false);

            holder = new Holder();
            holder.object_name = view.findViewById(R.id.name);
            holder.object_balance = view.findViewById(R.id.balance);

            m_object = callLogData.get(index);

            if(m_object instanceof Event){
                Event event = (Event)m_object;
                List<Event> eventList = (List<Event>)(List<?>) callLogData;

                holder.object_name.setText(event.getName());
                holder.object_balance.setText(Float.toString(Stats.getBalance(eventList)));

            } else if(m_object instanceof Position){
                Position position = (Position) m_object;

                holder.object_name.setText(position.getTopic());
                holder.object_balance.setText(Float.toString(position.getValue()));
            }
            return view;
        }

        private class Holder
        {
            TextView object_name;
            TextView object_balance;
        }

    }

    public void setFragmentObjects(List<Object> objects){
        objectList = objects;
        CustomCallLogListAdapter adapter = new CustomCallLogListAdapter(getActivity(),
                R.layout.row_call_log_layout, objectList);
        list_calllog = view.findViewById(R.id.list_calllog);
        list_calllog.setAdapter(adapter);

        list_calllog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemSelected(list_calllog.getItemAtPosition(position));
            }
        });
    }

    public void itemSelected(Object object){
        itemClickListener.onFragmentObjectClick(object);
    }
}

