package de.thm.ap.groupexpenses.view.fragment;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
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
import de.thm.ap.groupexpenses.model.Event;
import de.thm.ap.groupexpenses.model.Position;
import de.thm.ap.groupexpenses.model.Stats;
import de.thm.ap.groupexpenses.view.dialog.ProfileInfoDialog;

import static de.thm.ap.groupexpenses.view.fragment.CashFragment.SELECTED_EID;

public class PositionEventListFragment<T> extends Fragment {
    public static final String USER_ID = "uid";
    private View view;
    private ListView object_listView;
    private TextView fragment_header_val;
    private ObjectItemAdapter adapter;
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
        RelativeLayout fragment_header = view.findViewById(R.id.fragment_header);
        TextView fragment_header_text = view.findViewById(R.id.fragment_header_text);
        fragment_header_val = view.findViewById(R.id.fragment_header_val);
        object_listView = view.findViewById(R.id.fragment_listView);
        TextView noObjects_textView = view.findViewById(R.id.fragment_no_object_text);

        Bundle args = getArguments();

        if (args != null) {
            String eid = args.getString(SELECTED_EID);
            String uid = args.getString(USER_ID);

            if (creatorMap == null) creatorMap = new HashMap<>();

            //if you want to show the list of positions of one Event
            if (eid != null) {
                EventLiveData eventLiveData = DatabaseHandler.getEventLiveData(eid);
                eventLiveData.observe(this, event -> {
                    if (event != null && !event.getPositions().isEmpty()) {
                        fragment_header.setVisibility(View.VISIBLE);
                        //headerView.setVisibility(View.VISIBLE);
                        object_listView.setVisibility(View.VISIBLE);
                        noObjects_textView.setVisibility(View.GONE);
                        String headerText = getString(R.string.total_expenses) + ":";
                        fragment_header_text.setText(headerText);
                        //header_text.setText(headerText);
                        updateTotalBalanceOfPositions(event);
                        List<Position> positions = event.getPositions();
                        for (int idx = 0; idx < positions.size(); ++idx) {
                            creatorMap.putIfAbsent(positions.get(idx).getCreatorId(), "");
                        }
                        generateAdapter((List<T>) positions, true);
                    } else {
                        fragment_header.setVisibility(View.GONE);
                        //headerView.setVisibility(View.GONE);
                        noObjects_textView.setVisibility(View.VISIBLE);
                        noObjects_textView.setText(R.string.no_positions);
                        if (adapter != null) {
                            object_listView.setVisibility(View.GONE);
                            adapter.clear();
                        }
                    }
                });
            }
            // if you want to show the list of events
            else if (uid != null) {
                EventListLiveData listLiveData = DatabaseHandler.getEventListLiveData(uid);
                listLiveData.observe(this, eventList -> {
                    if (eventList != null && !eventList.isEmpty()) {
                        String headerText = getString(R.string.total_balance) + ":";
                        fragment_header.setVisibility(View.VISIBLE);
                        //headerView.setVisibility(View.VISIBLE);
                        object_listView.setVisibility(View.VISIBLE);
                        noObjects_textView.setVisibility(View.GONE);
                        fragment_header_text.setText(headerText);
                        //header_text.setText(headerText);
                        updateTotalBalanceOfEvents(eventList);
                        for (int idx = 0; idx < eventList.size(); ++idx) {
                            creatorMap.putIfAbsent(eventList.get(idx).getCreatorId(), "");
                        }
                        generateAdapter((List<T>) eventList, false);

                    } else {
                        fragment_header.setVisibility(View.GONE);
                        //headerView.setVisibility(View.GONE);
                        noObjects_textView.setVisibility(View.VISIBLE);
                        noObjects_textView.setText(R.string.no_events);
                        if (adapter != null) {
                            object_listView.setVisibility(View.GONE);
                            adapter.clear();
                        }
                    }
                });
            }
        }
        return view;
    }

    private void generateAdapter(List<T> objectList, boolean isPosition) {
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

    private void buildAdapter(List<T> objectList, boolean isPosition) {
        // sort object list here
        Collections.sort(objectList, LIST_SORT);
        if (adapter == null) {
            adapter = new ObjectItemAdapter(getActivity(),
                    R.layout.fragment_object_list_row, objectList, isPosition);
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
        List<Position> positions = event.getPositions();
        float total_expenses = 0;
        for (Position p : positions) {
            total_expenses += p.getValue();
        }
        fragment_header_val.setText(new DecimalFormat("0.00 €").format(total_expenses));
    }

    private void updateTotalBalanceOfEvents(List<Event> eventList) {
        float balance = Stats.getBalance(eventList);

        fragment_header_val.setText(new DecimalFormat("0.00 €").format(balance));

        if (balance < 0)
            fragment_header_val.setTextColor(Color.parseColor("#ef4545"));    // red
        else
            fragment_header_val.setTextColor(Color.parseColor("#2ba050"));    // green

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
        public View getView(int index, View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
            holder = new Holder();
            //TextView event_status = view.findViewById(R.id.event_status);
            ImageView event_status_image = view.findViewById(R.id.event_status_image);
            holder.object_name = view.findViewById(R.id.name);
            holder.object_creator = view.findViewById(R.id.creator);
            holder.object_balance = view.findViewById(R.id.balance);
            m_object = retrievedObjects.get(index);
            String creator_part = getString(R.string.creator);
            String creator_name;
            String creatorUid;
            String wholePart;
            Spannable spannable;

            if (isPosition) {
                event_status_image.setVisibility(View.GONE);
                Position position = (Position) m_object;
                float position_expense = position.getValue();
                holder.object_name.setText(position.getTopic());
                if (position.getCreatorId().equals(App.CurrentUser.getUid())) {
                    creator_name = getString(R.string.you);
                    creatorUid = App.CurrentUser.getUid();
                } else {
                    creatorUid = position.getCreatorId();
                    creator_name = creatorMap.get(creatorUid);
                    final int CREATOR_NAME_MAX_LENGTH = 20;
                    if (creator_name.length() > CREATOR_NAME_MAX_LENGTH) {
                        creator_name = creator_name.substring(0, CREATOR_NAME_MAX_LENGTH) + "...";
                    }
                }
                wholePart = creator_part + " " + creator_name;
                spannable = new SpannableString(wholePart);
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#3a90e0")),
                        creator_part.length(), wholePart.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.object_creator.setText(spannable, TextView.BufferType.SPANNABLE);
                holder.object_balance.setText(new DecimalFormat("0.00 €")
                        .format(position_expense));
            } else {    // its an Event
                Event event = (Event) m_object;
                float balance = Stats.getEventBalance(event);

                event_status_image.setVisibility(View.VISIBLE);

                switch (event.getLifecycleState()) {
                    case UPCOMING:
                        event_status_image.setImageResource(R.drawable.event_status_soon);
                        break;
                    case LIVE:
                        event_status_image.setImageResource(R.drawable.event_status_live);
                        break;
                    case LOCKED:
                        event_status_image.setImageResource(R.drawable.event_status_pay);
                        break;
                    case CLOSED:
                        event_status_image.setImageResource(R.drawable.event_status_done);
                        break;
                    case ERROR:
                    default:
                        //event_status.setText(getString(R.string.event_status_error));
                        //event_status.setBackgroundResource(R.drawable.event_status_error);
                }

                holder.object_name.setText(event.getName());

                if (event.getCreatorId().equals(App.CurrentUser.getUid())) {
                    creator_name = getString(R.string.you);
                    creatorUid = App.CurrentUser.getUid();
                } else {
                    creatorUid = event.getCreatorId();
                    creator_name = creatorMap.get(creatorUid);
                    final int CREATOR_NAME_MAX_LENGTH = 20;
                    if (creator_name.length() > CREATOR_NAME_MAX_LENGTH) {
                        creator_name = creator_name.substring(0, CREATOR_NAME_MAX_LENGTH) + "...";
                    }
                }
                wholePart = creator_part + " " + creator_name;
                spannable = new SpannableString(wholePart);
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#3a90e0")),
                        creator_part.length(), wholePart.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.object_creator.setText(spannable, TextView.BufferType.SPANNABLE);
                holder.object_balance.setText(new DecimalFormat("0.00 €")
                        .format(balance));
                if (balance < 0)
                    holder.object_balance.setTextColor(Color
                            .parseColor("#ef4545"));    // red
                else
                    holder.object_balance.setTextColor(Color
                            .parseColor("#2ba050"));    // green
            }

            holder.object_creator.setOnClickListener(v -> {
                if (creatorUid != null) {
                    DatabaseHandler.queryUser(creatorUid, user -> {
                        // view creators profile
                        new ProfileInfoDialog(user, context);
                    });
                }
            });
            return view;
        }

        private class Holder {
            TextView object_name;
            TextView object_creator;
            TextView object_balance;
        }
    }

    Comparator<T> LIST_SORT = (item1, item2) -> {
        if (item1 instanceof Event) {
            Event event1 = (Event) item1;
            Event event2 = (Event) item2;
            switch (event1.getLifecycleState()) {
                case LIVE:
                    if (event2.getLifecycleState() != Event.LifecycleState.LIVE) return -1;
                    else return 0;
                case LOCKED:
                    switch (event2.getLifecycleState()) {
                        case LIVE:
                            return 1;
                        case LOCKED:
                            return 0;
                        default:
                            return -1;
                    }
                case UPCOMING:
                    switch (event2.getLifecycleState()) {
                        case LIVE:
                        case LOCKED:
                            return 1;
                        case UPCOMING:
                            return 0;
                        default:
                            return -1;
                    }
                case CLOSED:
                    switch (event2.getLifecycleState()) {
                        case LIVE:
                        case LOCKED:
                        case UPCOMING:
                            return 1;
                        case CLOSED:
                            return 0;
                        default:
                            return -1;
                    }
                case ERROR:
                    if (event2.getLifecycleState() != Event.LifecycleState.ERROR) return 1;
                    else return 0;
            }
        } else if (item1 instanceof Position) {
            Position pos1 = (Position) item1;
            Position pos2 = (Position) item2;
            if (pos1.getValue() > pos2.getValue()) return -1;
            else return 1;
        }
        return 0;
    };
}

