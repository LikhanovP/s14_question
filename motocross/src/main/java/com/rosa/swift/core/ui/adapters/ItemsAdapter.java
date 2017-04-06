package com.rosa.swift.core.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rosa.motocross.R;

import java.util.List;

public class ItemsAdapter<T> extends BaseAdapter {

    private List<T> mItems;
    private Context mContext;
    private LayoutInflater mInflater;

    public ItemsAdapter(Context context, List<T> items) {
        mItems = items;
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    protected List<T> getItems() {
        return mItems;
    }

    protected LayoutInflater getInflater() {
        return mInflater;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public T getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = mInflater.inflate(R.layout.list_view_item_layout, parent, false);
        }

        TextView textView = (TextView) itemView.findViewById(R.id.caption_txt);
        textView.setText(mItems.get(position).toString());

        return itemView;
    }

}
