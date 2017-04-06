package com.rosa.swift.core.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rosa.motocross.R;
import com.rosa.swift.core.business.IWaitSessionLoadParam;
import com.rosa.swift.core.business.WaitSessionLoadTask;
import com.rosa.swift.core.business.utils.CommonUtils;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.business.utils.SapRequestUtils;
import com.rosa.swift.core.network.json.sap.swchat.JChatMessage;
import com.rosa.swift.core.network.json.sap.swchat.JRoom;
import com.rosa.swift.core.network.json.sap.swchat.JRoomInfo;
import com.rosa.swift.core.network.json.sap.swchat.JRoomsOut;
import com.rosa.swift.core.network.requests.chat.RoomRequest;
import com.rosa.swift.core.network.services.sap.ServiceCallback;
import com.rosa.swift.core.network.services.sap.WSException;
import com.rosa.swift.core.ui.activities.LogonlessActivity;
import com.rosa.swift.core.ui.adapters.RoomListAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class SwiftRoomListFragment extends ListFragment implements CabinetFragment {
    private static int code = R.id.pcab_action_swift_room_list;

    private FragmentListener mListener;
    private JRoomsOut result = new JRoomsOut();
    private String start_date;
    private int selectedFilter;
    private boolean need_refresh;
    private static String[] items;

    private RoomListAdapter adapter;

    public static SwiftRoomListFragment newInstance() {
        SwiftRoomListFragment fragment = new SwiftRoomListFragment();
        return fragment;
    }

    public SwiftRoomListFragment() {
        setHasOptionsMenu(true);
        if (items == null) {
            items = new String[6];
            items[0] = "Непрочитанные";
            items[1] = "Сегодня";
            items[2] = "Вчера";
            items[3] = "Неделя";
            items[4] = "Месяц";
            items[5] = "Все";
        }
    }

    public void setSearchResult(JRoomsOut search_result) {
        result = search_result;
        updateView();
    }

    public List<JRoomInfo> getRooms() {
        return result.roomsList;
    }

    private void updateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void refreshData() {
        if (isAdded()) {
            calculateStartDate();
            getRoomsAsync(start_date);
        } else
            need_refresh = true;
    }

    @Override
    public String getTitle() {
        return "Сообщения";
    }

    @Override
    public String getPCabTag() {
        return getTitle();
    }

    @Override
    public String getBackStackName() {
        return getTitle();
    }

    @Override
    public boolean getDrawerEnabled() {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            start_date = savedInstanceState.getString("start_date", "");
            need_refresh = savedInstanceState.getBoolean("update", false);
            selectedFilter = savedInstanceState.getInt("filter", 0);
            result = (JRoomsOut) savedInstanceState.getSerializable("result");
            if (result == null)
                result = new JRoomsOut();
        }
        adapter = new RoomListAdapter(this);
        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (need_refresh) {
            need_refresh = false;
            refreshData();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (FragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mListener != null)
            mListener.onFragmentStart(code);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (null != mListener) {
            mListener.onChatRoomSelect(result.roomsList.get(position).room);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chat_room_list_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_list:
                refreshData();
                break;
            case R.id.action_chat_filter:
                showRoomsFilter();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRoomsFilter() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_activity_driver_message);
        builder.setSingleChoiceItems(items, selectedFilter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedFilter = i;
                refreshData();
                dialogInterface.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void calculateStartDate() {
        Calendar c = new GregorianCalendar();
        switch (selectedFilter) {
            case 0:
                start_date = "";
                return;
            case 1: //сегодня
                break;
            case 2: //вчера
                c.add(Calendar.DAY_OF_MONTH, -1);
                break;
            case 3: //неделя
                c.add(Calendar.DAY_OF_MONTH, -7);
                break;
            case 4: //месяц
                c.add(Calendar.MONTH, -1);
            case 5: //все
                c.add(Calendar.YEAR, -100);
                break;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        start_date = sdf.format(c.getTime());
    }

    public void getRooms(String startDate) {
        RoomRequest request = new RoomRequest(startDate);
        SapRequestUtils.getChatRooms(request, new ServiceCallback() {
            @Override
            public void onEndedRequest() {
                hideProgress();
            }

            @Override
            public void onFinished(String evParams) {
                try {
                    Gson g = new Gson();
                    JRoomsOut jr = g.fromJson("{ \"ROOMS\" : " + evParams + "}", JRoomsOut.class);
                    setSearchResult(jr);
                } catch (Exception ex) {
                    Log.e(ex.getMessage(), ex);
                }
            }

            @Override
            public void onFinishedWithException(WSException ex) {
                showError(ex);
            }

            @Override
            public void onCancelled() {
                hideProgress();
            }
        });
    }

    public class GetRoomAsyncParam implements IWaitSessionLoadParam {
        private final String start_date;

        public GetRoomAsyncParam(String start_date) {
            this.start_date = start_date;
        }

        public void execute() {
            getRooms(start_date);
        }

        public void onException(Exception ex) {
            if (SwiftRoomListFragment.this.getActivity() != null) {
                Toast.makeText(SwiftRoomListFragment.this.getActivity(), "Ошибка при получении списка комнат", Toast.LENGTH_SHORT).show();
            }
        }

        public void onCancelled() {

        }
    }

    //проверям и ждем пока сессия не подключится
    private void getRoomsAsync(String start_date) {
        showProgress(R.string.action_load);
        GetRoomAsyncParam getRoomAsyncParam = new GetRoomAsyncParam(start_date);
        new WaitSessionLoadTask().execute(getRoomAsyncParam);
    }

    private void hideProgress() {
        if (isAdded())
            ((LogonlessActivity) getActivity()).hideProgress();
    }

    private void showProgress(int string_res_id) {
        if (isAdded())
            ((LogonlessActivity) getActivity()).showProgress(string_res_id);
    }

    private void showError(WSException ex) {
        if (isAdded()) {
            CommonUtils.ShowErrorMessage(getActivity(), ex.getMessage());
            if (((LogonlessActivity) getActivity()).GetGodMode())
                CommonUtils.ShowErrorMessage(getActivity(), ex.getFullMessage());
        }
    }

    public void addNewMessage(JChatMessage message) {
        JRoomInfo r = new JRoomInfo();
        r.room = new JRoom();
        r.room.room_id = message.room_id;
        if (result != null && result.roomsList.contains(r)) {
            result.roomsList.get(result.roomsList.indexOf(r)).new_messages++;
            updateView();
        } else {
            r.room.topic = message.text;
            r.new_messages = 1;
            if (result == null) result = new JRoomsOut();
            result.roomsList.add(r);
            refreshData();
        }
    }

    public void markRoomAsRead(JRoom room) {
        if (result != null && result.roomsList.contains(room)) {
            result.roomsList.get(result.roomsList.indexOf(room)).new_messages = 0;
            updateView();
        }
    }

    public JRoom getRoomById(String room_id) {
        JRoomInfo r = new JRoomInfo();
        r.room = new JRoom();
        r.room.room_id = room_id;
        if (result != null && result.roomsList.contains(r)) {
            return result.roomsList.get(result.roomsList.indexOf(r)).room;
        }
        return r.room;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("result", result);
        outState.putString("start_date", start_date);
        outState.putInt("filter", selectedFilter);
        outState.putBoolean("update", need_refresh);
    }
}
