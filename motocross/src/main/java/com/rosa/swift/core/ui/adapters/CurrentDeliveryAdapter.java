package com.rosa.swift.core.ui.adapters;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rosa.motocross.R;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.common.Delivery;
import com.rosa.swift.core.data.dto.deliveries.templates.TemplateType;
import com.rosa.swift.core.ui.activities.MainActivity;


/**
 * Created by yalang on 18.09.13.
 */
public class CurrentDeliveryAdapter extends BaseAdapter {

    public static class ViewHolder {
        protected TextView dlv_text_view;
    }

    private MainActivity context;

    public CurrentDeliveryAdapter(MainActivity context) {
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return context.getCurrentDeliveryListForView().size();
    }

    @Override
    public Object getItem(int i) {
        return context.getCurrentDeliveryListForView().get(i);
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

            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.dlv_item_layout, null);

            holder = new ViewHolder();
            holder.dlv_text_view = (TextView) rowView.findViewById(R.id.dlv_item_text_view);
            holder.dlv_text_view.setOnClickListener(view ->
                    context.selectDeliveryByNumber((String) view.getTag()));
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        Delivery delivery = (Delivery) getItem(position);
        String deliveryText = DataRepository.getInstance().getDeliveryDataHtml(
                TemplateType.DCL, delivery);

        holder.dlv_text_view.setText(Html.fromHtml(deliveryText));
        holder.dlv_text_view.setTag(delivery.getNumber());

        return rowView;
    }

}