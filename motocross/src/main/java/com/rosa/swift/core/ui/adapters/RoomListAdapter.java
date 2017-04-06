package com.rosa.swift.core.ui.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rosa.motocross.R;
import com.rosa.swift.core.network.json.sap.swchat.JRoomInfo;
import com.rosa.swift.core.ui.fragments.SwiftRoomListFragment;

/**
 * Created by yalang on 10.12.2014.
 */
public class RoomListAdapter extends BaseAdapter {

    public static class ViewHolder {
        protected TextView messages_cnt_text_view;
        protected TextView caption_text_view;
    }

    private SwiftRoomListFragment context;

    public RoomListAdapter(SwiftRoomListFragment context) {
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return context.getRooms().size();
    }

    @Override
    public Object getItem(int i) {
        return context.getRooms().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View rowView = convertView;

        if (rowView == null) {

            LayoutInflater inflater = context.getActivity().getLayoutInflater();
            rowView = inflater.inflate(R.layout.room_list_layout, null);

            holder = new ViewHolder();
            holder.messages_cnt_text_view = (TextView) rowView.findViewById(R.id.message_cnt_view);
            holder.caption_text_view = (TextView) rowView.findViewById(R.id.caption_view);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        JRoomInfo room_info = (JRoomInfo) getItem(position);
        holder.caption_text_view.setText(room_info.room.getTitleTopic());
        String text = String.format("Всего: %d Новых: %d", room_info.room.message_count, room_info.new_messages);
        holder.messages_cnt_text_view.setText(text);
        if (room_info.new_messages != 0) {
            holder.caption_text_view.setTypeface(null, Typeface.BOLD);
            holder.messages_cnt_text_view.setTypeface(null, Typeface.BOLD);
        } else {
            holder.caption_text_view.setTypeface(null, Typeface.NORMAL);
            holder.messages_cnt_text_view.setTypeface(null, Typeface.NORMAL);
        }

        return rowView;
    }

}
