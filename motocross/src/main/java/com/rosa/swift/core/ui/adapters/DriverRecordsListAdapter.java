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
import com.rosa.swift.core.network.json.sap.driverRecords.JDriverRecords;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by inurlikaev on 23.04.2016.
 */
public class DriverRecordsListAdapter extends ArrayAdapter<JDriverRecords.JDriverRecord> {
    private OnItemClickListener onItemClickListener = null;

    private int resource;
    private Context context;
    private List<JDriverRecords.JDriverRecord> values;
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    protected static class ViewHolder {
        TextView tvDate;
        TextView tvMark;
        ImageView ivStar;
        TextView tvTitle;
        JDriverRecords.JDriverRecord driverRecords;
        int position;
    }

    public DriverRecordsListAdapter(Context context, int resource, List<JDriverRecords.JDriverRecord> values) {
        super(context, resource, values);
        this.resource = resource;
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;

        JDriverRecords.JDriverRecord item = getItem(position);
        if (item == null) return null;

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.tvDate = (TextView) rowView.findViewById(R.id.driver_records_item_date);
            holder.tvMark = (TextView) rowView.findViewById(R.id.driver_records_item_mark);
            holder.tvTitle = (TextView) rowView.findViewById(R.id.driver_records_item_title);
            holder.ivStar = (ImageView) rowView.findViewById(R.id.driver_records_item_star);
            rowView.setTag(holder);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        ViewHolder viewHolder = (ViewHolder) v.getTag();
                        onItemClickListener.onItemClick(v, viewHolder.driverRecords, viewHolder.position);
                    }
                }
            });
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.position = position;
        holder.driverRecords = item;

        holder.tvDate.setText(dateFormat.format(StringUtils.SAPDateToDate(item.record_erdat)));
        if (item.record_mark == null) {
            item.record_mark = "0.000";
        }
        holder.tvMark.setText(String.format("%.0f", Float.parseFloat(item.record_mark)));
        if (item.record_title != null && item.record_title.length() > 17) {
            holder.tvTitle.setText(item.record_title.substring(0, 17) + "...");
        } else {
            holder.tvTitle.setText(item.record_title);
        }

        float floatMark = 0;
        if (!StringUtils.isNullOrEmpty(item.record_mark)) {
            try {
                floatMark = Float.parseFloat(item.record_mark);
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
        void onItemClick(View v, JDriverRecords.JDriverRecord driverRecords, int position);
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

