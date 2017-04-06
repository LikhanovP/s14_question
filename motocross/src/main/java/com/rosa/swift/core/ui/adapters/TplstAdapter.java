package com.rosa.swift.core.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rosa.motocross.R;
import com.rosa.swift.core.data.dto.common.Tplst;

import java.util.List;

public class TplstAdapter extends ItemsAdapter<Tplst> {

    public TplstAdapter(Context context, List<Tplst> items) {
        super(context, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = getInflater().inflate(R.layout.list_view_tplst_layout,
                    parent, false);
        }

        Tplst tplst = getItems().get(position);
        TextView nameTxt = (TextView) itemView.findViewById(R.id.name_txt);
        TextView addressTxt = (TextView) itemView.findViewById(R.id.address_txt);
        nameTxt.setText(tplst.getName());
        addressTxt.setText(tplst.getAddress());

        return itemView;
    }
}
