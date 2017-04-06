package com.rosa.swift.core.ui.adapters;

import android.graphics.Color;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rosa.motocross.R;
import com.rosa.swift.core.network.json.sap.swchat.JChatMessage;
import com.rosa.swift.core.ui.fragments.SwiftRoomFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yalang on 10.12.2014.
 */
public class RoomAdapter extends BaseAdapter {

    public static class ViewHolder {
        protected TextView messages_cnt_text_view;
        protected TextView caption_text_view;
        protected TextView date_text_view;
    }

    private SwiftRoomFragment context;

    public RoomAdapter(SwiftRoomFragment context) {
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return context.getMessages().size();
    }

    @Override
    public Object getItem(int i) {
        return context.getMessages().get(i);
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
            rowView = inflater.inflate(R.layout.room_item_layout, null);

            holder = new ViewHolder();
            holder.messages_cnt_text_view = (TextView) rowView.findViewById(R.id.message_cnt_view);
            holder.caption_text_view = (TextView) rowView.findViewById(R.id.caption_view);
            holder.date_text_view = (TextView) rowView.findViewById(R.id.date_view);
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        JChatMessage message = (JChatMessage) getItem(position);
        holder.caption_text_view.setText(message.text);
        holder.messages_cnt_text_view.setText(message.ernam_text);
        Time t = message.getCreateDateTime();
        Date d = new Date(t.year, t.month, t.monthDay, t.hour, t.minute);
        SimpleDateFormat df = new SimpleDateFormat("d MMM HH:mm", new Locale("ru_RU"));
        holder.date_text_view.setText(df.format(d));
        if ("Я".equals(message.ernam_text)) {
            holder.messages_cnt_text_view.setTextColor(Color.RED);
        } else {
            holder.messages_cnt_text_view.setTextColor(Color.BLUE);
        }

        return rowView;
    }

}
