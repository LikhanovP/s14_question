package com.rosa.swift.core.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rosa.motocross.R;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.common.Delivery;
import com.rosa.swift.core.data.dto.deliveries.templates.TemplateType;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeliveryFragment extends Fragment implements CabinetFragment {
    private static int code = R.id.pcab_action_delivery;

    private Delivery delivery;
    private FragmentListener mListener;

    public DeliveryFragment() {
        setHasOptionsMenu(true);
    }

    public static DeliveryFragment getInstance(Delivery d) {
        DeliveryFragment fragment = new DeliveryFragment();
        fragment.setDelivery(d);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            String dlv_json = savedInstanceState.getString("dlv_json", "");
            if (!"".equals(dlv_json)) {
                Gson g = new Gson();
                try {
                    delivery = (Delivery) g.fromJson(dlv_json, Delivery.class);
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        TextView textView = new TextView(getActivity());
        textView.setPadding(10, 10, 10, 10);
        if (delivery != null) {
            String deliveryText = DataRepository.getInstance().getDeliveryDataHtml(
                    TemplateType.DFD, delivery);
            textView.setText(Html.fromHtml(deliveryText));
        }
        return textView;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.delivery_fragment, menu);
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
    public void refreshData() {

    }

    @Override
    public String getTitle() {
        return delivery != null ? delivery.getNumber() : "";
    }

    @Override
    public String getPCabTag() {
        return getTitle();
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            Gson g = new Gson();
            String dlv_json = g.toJson(delivery, Delivery.class);
            outState.putString("dlv_json", dlv_json);
        } catch (Exception ignored) {
            outState.putString("dlv_json", "");
        }
    }


}
