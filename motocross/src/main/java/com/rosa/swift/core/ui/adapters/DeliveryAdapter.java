package com.rosa.swift.core.ui.adapters;

import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
public class DeliveryAdapter extends BaseAdapter {

    public static class ViewHolder {
        protected TextView dlv_text_view;
        //protected Button assignButton;
    }

    private MainActivity context;

    public DeliveryAdapter(MainActivity context) {
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return context.getDeliveryListForView().size();
    }

    @Override
    public Object getItem(int i) {
        return context.getDeliveryListForView().get(i);
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
            holder.dlv_text_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.displayDelivery((String) view.getTag());
                }
            });
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
            holder.dlv_text_view.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }

        Delivery delivery = (Delivery) getItem(position);
        String deliveryText = DataRepository.getInstance().getDeliveryDataHtml(
                TemplateType.DMLS, delivery);

        holder.dlv_text_view.setText(Html.fromHtml(deliveryText));
        holder.dlv_text_view.setTag(delivery.getNumber());
        //анимация для новой доставки
        if (delivery.isNew()) { //todo покрасивше?
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(400);
            //anim.setStartOffset(new Random().nextInt(30));
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(3);
            rowView.startAnimation(anim);
            context.vibrate();
            delivery.setNew(false);
        }
        String transportType = delivery.getTransportCode();
        if (!TextUtils.isEmpty(transportType)) {
            if (DataRepository.getInstance().getSessionId() != null) {
                if (!transportType.equals(DataRepository.getInstance().getTypeTransport())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.dlv_text_view.setBackground(
                                context.getResources().getDrawable(R.drawable.delivery_vsart_color));
                    } else {
                        holder.dlv_text_view.setBackgroundDrawable(
                                context.getResources().getDrawable(R.drawable.delivery_vsart_color));
                    }
                }
            }
        }

        return rowView;
    }

}