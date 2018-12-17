package de.thm.ap.groupexpenses.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.User;

public class CallLogFragment extends Fragment
{
    private View view;
    private ListView list_calllog;
    private ArrayList<Event> eventLog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if(view==null)
        {
            view=inflater.inflate(R.layout.fragment_call_log_layout, container,false);
        }
        else
        {
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }

        eventLog = getEvents();
        CustomCallLogListAdapter adapter=new CustomCallLogListAdapter(getActivity(),R.layout.row_call_log_layout, eventLog);
        list_calllog=(ListView)view.findViewById(R.id.list_calllog);
        list_calllog.setAdapter(adapter);
        return view;
    }

    @SuppressLint("NewApi")
    public ArrayList<Event> getEvents()
    {
        ArrayList<Event> events = new ArrayList<>();
        ArrayList<User> userList = new ArrayList<>();
        User myUser = new User(1, "Lukas", "Hilfrich", "l.hilfrich@gmx.de");
        User myUser2 = new User(2, "Hendrik", "Kegel", "oof");
        App.CurrentUser = myUser;
        userList.add(myUser);
        userList.add(myUser2);
        userList.add(new User(3, "Kai", "Schäfer", "oof2"));
        userList.add(new User(4, "David", "Omran", "oof3"));
        userList.add(new User(5, "Ulf", "Smolka", "ka"));
        //userList.add(new User(6, "Dominik", "Herz", "kjlkalsd"));
        //userList.add(new User(7, "Aris", "Christidis", "lolo"));
        //userList.add(new User(8, "KQC", "NA", "xD"));
        //userList.add(new User(9, "Adam", "Bdam", "dontEvenknow"));
        //userList.add(new User(10, "Max", "Muster", "maybe@fdm"));
        //userList.add(new User(11, "Rainer", "Rein", "lalalala"));

        Event testEvent = new Event(
                new User(1, "Lukas", "Hilfrich", "l.hilfrich@gmx.de"),
                "TestEvent1",
                "13.08.2019",
                "Eventinfo",
                userList
        );

        Event testEvent2 = new Event(
                new User(1, "Hendrik", "Kegel", "dontknow"),
                "TestEvent2",
                "01.12.2033",
                "Eventinfo blblbablablabla",
                userList
        );
        testEvent.addPosition(new Position(myUser, "TestPosition", 30));
        testEvent.addPosition(new Position(myUser, "TestPosition2", 30));
        //testEvent.addPosition(new Position(myUser, "TestPosition3", -98));

        testEvent2.addPosition(new Position(myUser2, "TestPosition4", 30));
        //testEvent2.addPosition(new Position(myUser, "TestPosition5", -17));
        //testEvent2.addPosition(new Position(myUser, "TestPosition6", 128));

        events.add(testEvent);
        events.add(testEvent2);

        return events;
    }

    private class CustomCallLogListAdapter extends ArrayAdapter<Event>
    {
        private ArrayList<Event> callLogData;
        private Context context;
        private int resource;
        private View view;
        private Holder holder;
        private Event m_event;
        public CustomCallLogListAdapter(Context context, int resource,ArrayList<Event> objects)
        {
            super(context, resource, objects);
            this.context=context;
            this.resource=resource;
            this.callLogData=objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(resource, parent,false);

            holder=new Holder();
            holder.text_number=(TextView)view.findViewById(R.id.text_calllog_number);
            holder.text_date=(TextView)view.findViewById(R.id.text_calllog_date);
            holder.text_time=(TextView)view.findViewById(R.id.text_calllog_time);

            m_event =callLogData.get(position);

            //Date date=new Date(Long.parseLong(m_event.get(CallLog.Calls.DATE)));
            //java.text.DateFormat dateFormat= DateFormat.getDateFormat(context);
            //java.text.DateFormat timeformat=DateFormat.getTimeFormat(context);


            //holder.text_number.setText(m_event.get(CallLog.Calls.NUMBER));
            //holder.text_time.setText(timeformat.format(date));
            //holder.text_date.setText(dateFormat.format(date));

            return view;
        }

        public class Holder
        {
            TextView text_number;
            TextView text_date;
            TextView text_time;
        }

    }

    public void setFragmentText(String text){
        //
    }
}

