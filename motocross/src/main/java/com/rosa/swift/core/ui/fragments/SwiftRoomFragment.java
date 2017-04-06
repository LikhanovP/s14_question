package com.rosa.swift.core.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rosa.motocross.R;
import com.rosa.swift.core.business.ChatMessageSentCallback;
import com.rosa.swift.core.business.IWaitSessionLoadParam;
import com.rosa.swift.core.business.WaitSessionLoadTask;
import com.rosa.swift.core.business.utils.SapRequestUtils;
import com.rosa.swift.core.network.json.sap.swchat.JChatMessage;
import com.rosa.swift.core.network.json.sap.swchat.JRoom;
import com.rosa.swift.core.network.json.sap.swchat.JRoomOut;
import com.rosa.swift.core.network.requests.chat.RoomRequest;
import com.rosa.swift.core.network.services.sap.ServiceCallback;
import com.rosa.swift.core.network.services.sap.WSException;
import com.rosa.swift.core.ui.adapters.RoomAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.rosa.swift.core.business.utils.Constants.SAP_TRUE_FLAG;

/**
 * Created by yalang on 05.12.2014.
 */
public class SwiftRoomFragment extends ListFragment implements CabinetFragment, ChatMessageSentCallback {
    private static int code = R.id.pcab_action_swift_room;

    private JRoom room;
    private FragmentListener mListener;
    private JRoomOut result;
    private RoomAdapter adapter;
    private View header_view;
    private boolean loading;

    public SwiftRoomFragment() {
        setHasOptionsMenu(true);
    }

    public static SwiftRoomFragment getInstance(JRoom room) {
        SwiftRoomFragment fragment = new SwiftRoomFragment();
        fragment.setRoom(room);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            JRoom room = (JRoom) savedInstanceState.getSerializable("room");
            if (room != null) {
                this.room = room;
                JRoomOut roomOut = (JRoomOut) savedInstanceState.getSerializable("room_out");
                if (roomOut != null)
                    result = roomOut;
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        header_view = getLayoutInflater(savedInstanceState).inflate(R.layout.swift_message_loader, null);
        header_view.setVisibility(View.GONE);
        getListView().addHeaderView(header_view, null, false);
        getListView().setHeaderDividersEnabled(false);

        adapter = new RoomAdapter(this);
        setListAdapter(adapter);

        getListView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        getListView().setStackFromBottom(true);
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    if (room != null && room.message_count != 0 && !loading)
                        loadRoomAsync();
                }
            }
        });
    }

    private void updateView(int items_added) {
        int index = 0;
        int top = 0;
        if (items_added != 0) {
            index = getListView().getFirstVisiblePosition() + items_added;
            View v = getListView().getChildAt(getListView().getHeaderViewsCount());
            top = (v == null) ? 0 : v.getTop();
        }
        adapter.notifyDataSetChanged();
        if (items_added != 0)
            getListView().setSelectionFromTop(index, top);
        if (room.message_count != 0) {
            header_view.setVisibility(View.VISIBLE);
        } else
            header_view.setVisibility(View.GONE);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.chat_room_fragment, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mListener != null)
            mListener.onFragmentStart(code);
    }

    public void setRoom(JRoom room) {
        this.room = room;
        result = null;
        loadRoomAsync();
    }

    private void loadRoom() {
        loading = true;
        String messageId = "";
        if (result != null && result.messageList != null && !result.messageList.isEmpty()) {
            messageId = result.messageList.get(0).message_id;
        }
        RoomRequest request = new RoomRequest(room.room_id, messageId);
        SapRequestUtils.getChatRoomComplete(request, new ServiceCallback() {
            @Override
            public void onEndedRequest() {
            }

            @Override
            public void onFinished(String evParams) {
                try {
                    int items_added;
                    Gson g = new Gson();
                    JRoomOut new_result = g.fromJson(evParams, JRoomOut.class);
                    items_added = new_result.messageList.size();
                    result = new_result;
                    room = result.room;
                    updateView(items_added);
                } catch (Exception ignored) {
                } finally {
                    loading = false;
                }
            }

            @Override
            public void onFinishedWithException(WSException ex) {

            }

            @Override
            public void onCancelled() {
                loading = false;
            }
        });
    }

    public class LoadRoomAsyncParam implements IWaitSessionLoadParam {
        public void execute() {
            loadRoom();
        }

        public void onException(Exception ex) {
            if (SwiftRoomFragment.this.getActivity() != null) {
                Toast.makeText(SwiftRoomFragment.this.getActivity(), "Ошибка при получении сообщений", Toast.LENGTH_SHORT).show();
            }
        }

        public void onCancelled() {

        }
    }

    //проверям и ждем пока сессия не подключится
    private void loadRoomAsync() {
        new WaitSessionLoadTask().execute(new LoadRoomAsyncParam());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_list:
                loadRoomAsync();
                break;
            case R.id.action_send_message:
                sendMessage();
                break;
            case R.id.action_mark_as_read:
                markRoomAsRead();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void markRoomAsRead() {
        if (mListener != null)
            mListener.markRoomAsRead(room);
    }

    private void sendMessage() {
        if (mListener != null)
            mListener.sendChatMessage(room.room_id, this);
    }

    public boolean addNewMessage(JChatMessage message) {
        if (result == null) {
            result = new JRoomOut();
            result.messageList = new ArrayList<>();
        } else {
            if (!result.room.room_id.equals(message.room_id)) return false; //на всякий случай
        }
        result.messageList.add(message);
        updateView(0);
        return true;
    }

    @Override
    public void refreshData() {
        /*empty*/
    }

    @Override
    public String getTitle() {
        return room != null ? room.getTitleTopic() : "";
    }

    @Override
    public String getPCabTag() {
        return room != null ? room.room_id : "";
    }

    @Override
    public String getBackStackName() {
        return null;
    }

    @Override
    public boolean getDrawerEnabled() {
        return true;
    }

    @Override
    public void onSentComplete(JChatMessage message, String message_id) {
        if (!"0000000000".equals(message_id)) {
            message.read = SAP_TRUE_FLAG;
            addNewMessage(message);
        }
    }

    public void markRoomAsReadComplete(JRoom room) {
        if (result == null) {
            return;
        } else {
            if ((result.room != null) && (result.room.room_id != null) && !result.room.room_id.equals(room.room_id))
                return; //на всякий случай
        }
        for (JChatMessage cm : result.messageList)
            cm.read = SAP_TRUE_FLAG;
        updateView(0);
    }

    public List<JChatMessage> getMessages() {
        return result != null ? result.messageList : new ArrayList<>();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("room", room);
        outState.putSerializable("room_out", result);
    }
}
