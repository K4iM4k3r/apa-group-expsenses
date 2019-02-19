package de.thm.ap.groupexpenses.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import de.thm.ap.groupexpenses.App;
import de.thm.ap.groupexpenses.R;
import de.thm.ap.groupexpenses.database.DatabaseHandler;
import de.thm.ap.groupexpenses.livedata.EventListLiveData;
import de.thm.ap.groupexpenses.livedata.EventLiveData;
import de.thm.ap.groupexpenses.livedata.UserListLiveData;
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.Stats;

import static de.thm.ap.groupexpenses.view.fragment.CashFragment.SELECTED_EID;

public class PositionEventListFragment<T> extends Fragment {
    private static final String USERID = "uid";
    private View view;
    private ListView object_listView;
    private View headerView;
    private ObjectItemAdapter adapter;
    private Event relatedEventToPosition;
    private HashMap<String, String> creatorMap;

    ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onFragmentObjectClick(Object object);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            itemClickListener = (ItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_object_list, container, false);
        } else {
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
        }
        headerView = getLayoutInflater().inflate(R.layout.fragment_object_list_header, null);

        TextView noObjects_textView = view.findViewById(R.id.fragment_no_object_text);

        Bundle args = getArguments();

        if (args != null) {
            String eid = args.getString(SELECTED_EID);
            String uid = args.getString(USERID);
            //if you want to show the list of positions of one Event
            if(eid != null){
                EventLiveData eventLiveData = DatabaseHandler.getEventLiveData(eid);
                eventLiveData.observe(this, event -> {
                    if (event != null) {
                        noObjects_textView.setVisibility(View.GONE);
                        relatedEventToPosition = event;
                        updateTotalBalanceOfPositions(event);
                        List<Position> positions = event.getPositions();
                        if (creatorMap == null) creatorMap = new HashMap<>();

                        for (int idx = 0; idx < positions.size(); ++idx) {
                            creatorMap.putIfAbsent(positions.get(idx).getCreatorId(), "");
                        }
                        generateAdapter((List<T>) positions, true);
                    }
                });
            }
            // if you want to show the list of events
            else if(uid != null) {
                EventListLiveData listLiveData = DatabaseHandler.getEventListLiveData(uid);
                listLiveData.observe(this, eventList ->{
                    if (eventList != null) {
                        noObjects_textView.setVisibility(View.GONE);
                        updateTotalBalanceOfEvents(eventList);
                        for (int idx = 0; idx < eventList.size(); ++idx) {
                            creatorMap.putIfAbsent(eventList.get(idx).getCreatorId(), "");
                        }
                        generateAdapter((List<T>) eventList, false);

                    }
                });
            }
        }
        return view;
    }

    private void generateAdapter(List<T> objectList, boolean isPosition){
        if (creatorMap.containsValue("")) {
            Set<String> keysWithoutVal = getKeysByValue(creatorMap, "");
            for (String uid : keysWithoutVal) {
                DatabaseHandler.queryUser(uid, user -> {
                    if (user != null) {
                        creatorMap.put(uid, user.getNickname());
                    } else {    // USER NOT FOUND!!!
                        creatorMap.put(uid, getString(R.string.deleted_user));
                    }
                    if (!creatorMap.containsValue("")) {
                        buildAdapter(objectList, isPosition);
                    }
                });
            }
        } else {
            buildAdapter(objectList, isPosition);
        }
    }

    public void updateList(List<T> objectList, Event relatedEvent) {
        TextView noObjects_textView = view.findViewById(R.id.fragment_no_object_text);
        boolean isPosition = relatedEvent != null;
        if (!objectList.isEmpty()) {
            if (isPosition) relatedEventToPosition = relatedEvent;
            noObjects_textView.setVisibility(View.GONE);
            updateTotalBalance(objectList, isPosition);

            if (creatorMap == null) creatorMap = new HashMap<>();
            if (isPosition) {
                for (int idx = 0; idx < objectList.size(); ++idx) {
                    creatorMap.putIfAbsent(((Position) objectList.get(idx)).getCreatorId(), "");
                }
            } else {
                for (int idx = 0; idx < objectList.size(); ++idx) {
                    creatorMap.putIfAbsent(((Event) objectList.get(idx)).getCreatorId(), "");
                }
            }
            if (creatorMap.containsValue("")) {
                Set<String> keysWithoutVal = getKeysByValue(creatorMap, "");
                for (String uid : keysWithoutVal) {
                    DatabaseHandler.queryUser(uid, user -> {
                        if (user != null) {
                            creatorMap.put(uid, user.getNickname());
                        } else {    // USER NOT FOUND!!!
                            creatorMap.put(uid, getString(R.string.deleted_user));
                        }
                        if (!creatorMap.containsValue("")) {
                            buildAdapter(objectList, isPosition);
                        }
                    });
                }
            } else {
                buildAdapter(objectList, isPosition);
            }
        } else {
            noObjects_textView.setVisibility(View.VISIBLE);
            if (!isPosition) {
                noObjects_textView.setText(R.string.no_events);
            } else {
                noObjects_textView.setText(R.string.no_positions);
            }
            if (adapter != null) {
                object_listView.removeHeaderView(headerView);
                adapter.notifyDataSetChanged();
            }
        }

    }

    private void buildAdapter(List<T> objectList, boolean isPosition) {
        if (adapter == null) {
            adapter = new ObjectItemAdapter(getActivity(),
                    R.layout.fragment_object_list_row, objectList, isPosition);
            object_listView = view.findViewById(R.id.fragment_listView);
            object_listView.addHeaderView(headerView);
            object_listView.setAdapter(adapter);
            object_listView.setOnItemClickListener((parent, view, position, id) ->
                    itemSelected(object_listView.getItemAtPosition(position)));
        } else {
            adapter.clear();
            adapter.addAll(objectList);
            adapter.notifyDataSetChanged();
        }
    }

    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        return map.entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), value))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    private void updateTotalBalanceOfPositions(Event event) {
        TextView obj_val = headerView.findViewById(R.id.object_balance_summary_val);
        List<Position> positions = event.getPositions();
        float balance = 0;

        for (int idx = 0; idx < positions.size(); ++idx) {
            balance += Stats.getPositionBalance(positions.get(idx), event);
        }
        obj_val.setText(new DecimalFormat("0.00 €").format(balance));

        if (balance < 0)
            obj_val.setTextColor(Color.parseColor("#ef4545"));    // red
        else
            obj_val.setTextColor(Color.parseColor("#2ba050"));    // green
    }

    private void updateTotalBalanceOfEvents(List<Event> eventList) {
        TextView obj_val = headerView.findViewById(R.id.object_balance_summary_val);
        float balance = 0;

        balance = Stats.getBalance(eventList);
        obj_val.setText(new DecimalFormat("0.00 €").format(balance));

        if (balance < 0)
            obj_val.setTextColor(Color.parseColor("#ef4545"));    // red
        else
            obj_val.setTextColor(Color.parseColor("#2ba050"));    // green
    }
    private void updateTotalBalance(List<T> objectList, boolean isPosition) {
        TextView obj_val = headerView.findViewById(R.id.object_balance_summary_val);
        float balance = 0;

        if (isPosition) {
            for (int idx = 0; idx < objectList.size(); ++idx)
                balance += Stats.getPositionBalance((Position) objectList.get(idx),
                        relatedEventToPosition);

            obj_val.setText(new DecimalFormat("0.00").format(balance)
                    + " " + getString(R.string.euro));
        } else {
            List<Event> eventList = (List<Event>) objectList;
            balance = Stats.getBalance(eventList);

            obj_val.setText(new DecimalFormat("0.00").format(balance)
                    + " " + getString(R.string.euro));
        }
        if (balance < 0)
            obj_val.setTextColor(Color.parseColor("#ef4545"));    // red
        else
            obj_val.setTextColor(Color.parseColor("#2ba050"));    // green
    }

    public void itemSelected(Object object) {
        itemClickListener.onFragmentObjectClick(object);
    }

    private class ObjectItemAdapter extends ArrayAdapter<T> {
        private List<T> retrievedObjects;
        private Context context;
        private int resource;
        private View view;
        boolean isPosition;
        private Holder holder;
        private Object m_object;

        ObjectItemAdapter(Context context, int resource, List<T> objects, boolean isPosition) {
            super(context, resource, objects);
            this.isPosition = isPosition;
            this.context = context;
            this.resource = resource;
            this.retrievedObjects = objects;
        }

        @NonNull
        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
            holder = new Holder();
            holder.object_name = view.findViewById(R.id.name);
            holder.object_creator = view.findViewById(R.id.creator);
            holder.object_balance = view.findViewById(R.id.balance);
            m_object = retrievedObjects.get(index);
            float balance;
            String fromPart = getString(R.string.from);
            String creatorPart;
            String wholePart;
            Spannable spannable;

            if (isPosition) {
                Position position = (Position) m_object;
                balance = Stats.getPositionBalance(position, relatedEventToPosition);
                holder.object_name.setText(position.getTopic());
                if (position.getCreatorId().equals(App.CurrentUser.getUid())) {
                    creatorPart = getString(R.string.you);
                } else {
                    creatorPart = creatorMap.get(position.getCreatorId());
                }
                wholePart = fromPart + " " + creatorPart;
                spannable = new SpannableString(wholePart);
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#3a90e0")),
                        fromPart.length(), wholePart.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.object_creator.setText(spannable, TextView.BufferType.SPANNABLE);
                holder.object_balance.setText(new DecimalFormat("0.00 €")
                        .format(balance));
            } else {    // its an Event
                Event event = (Event) m_object;
                balance = Stats.getEventBalance(event);
                holder.object_name.setText(event.getName());
                if (event.getCreatorId().equals(App.CurrentUser.getUid())) {
                    creatorPart = getString(R.string.you);
                } else {
                    creatorPart = creatorMap.get(event.getCreatorId());
                }
                wholePart = fromPart + " " + creatorPart;
                spannable = new SpannableString(wholePart);
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#3a90e0")),
                        fromPart.length(), wholePart.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.object_creator.setText(spannable, TextView.BufferType.SPANNABLE);
                holder.object_balance.setText(new DecimalFormat("0.00 €")
                        .format(balance));
            }
            if (balance < 0)
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

