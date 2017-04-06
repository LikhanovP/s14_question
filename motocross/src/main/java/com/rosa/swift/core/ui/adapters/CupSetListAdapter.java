package com.rosa.swift.core.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.business.utils.StringUtils;
import com.rosa.swift.core.network.json.sap.cup.JCupSetInfoOut;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by inurlikaev on 23.04.2016.
 */
public class CupSetListAdapter extends ArrayAdapter<JCupSetInfoOut.JCupSetInfo> {
    private OnItemClickListener onItemClickListener = null;

    private int resource;
    private Context context;
    private List<JCupSetInfoOut.JCupSetInfo> values;
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    protected static class ViewHolder {
        TextView tvDate;
        TextView tvMark;
        ImageView ivStar;
        JCupSetInfoOut.JCupSetInfo cupSetInfo;
        int position;
    }

    public CupSetListAdapter(Context context, int resource, List<JCupSetInfoOut.JCupSetInfo> values) {
        super(context, resource, values);
        this.resource = resource;
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;

        JCupSetInfoOut.JCupSetInfo item = getItem(position);
        if (item == null) return null;

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.tvDate = (TextView) rowView.findViewById(R.id.cup_item_date);
            holder.tvMark = (TextView) rowView.findViewById(R.id.cup_item_mark);
            holder.ivStar = (ImageView) rowView.findViewById(R.id.cup_item_star);
            rowView.setTag(holder);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        ViewHolder viewHolder = (ViewHolder) v.getTag();
                        onItemClickListener.onItemClick(v, viewHolder.cupSetInfo, viewHolder.position);
                    }
                }
            });
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.position = position;
        holder.cupSetInfo = item;

        Date date = StringUtils.SAPDateToDate(item.cup_audat);
        if (date != null) {
            holder.tvDate.setText(dateFormat.format(date));
        }

        holder.tvMark.setText(item.cup_mark);

        float floatMark = 0;
        if (!StringUtils.isNullOrEmpty(item.cup_mark)) {
            try {
                floatMark = Float.parseFloat(item.cup_mark);
            } catch (NumberFormatException e) {
                Log.e(e.getMessage());
            }
        }

        int idMark = R.drawable.ic_rate_star_off;

        if (floatMark > 6.7)
            idMark = R.drawable.ic_rate_star_on;
        else if (floatMark > 3.3)
            idMark = R.drawable.ic_rate_star_half;

        holder.ivStar.setImageResource(idMark);

        return rowView;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, JCupSetInfoOut.JCupSetInfo cupSetInfo, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getCount() {
        if (values == null)
            return 0;
        else return values.size();
    }
}

