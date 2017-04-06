package com.rosa.swift.core.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rosa.motocross.R;
import com.rosa.swift.core.business.DeliverySearch;

public class FindDeliveryFragment extends Fragment implements CabinetFragment {
    private static int code = R.id.pcab_action_find_delivery;

    private FragmentListener mListener;
    private TextView tknumTextView;
    private TextView dateTextView;

    public static FindDeliveryFragment newInstance() {
        FindDeliveryFragment fragment = new FindDeliveryFragment();
        return fragment;
    }

    public FindDeliveryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_find_delivery, container, false);
        v.findViewById(R.id.find_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindDeliveryFragment.this.onFindClick();
            }
        });
        tknumTextView = (TextView) v.findViewById(R.id.find_tknum_edit);
        dateTextView = (TextView) v.findViewById(R.id.find_date_edit);
        return v;
    }

    private void onFindClick() {
        DeliverySearch.SearchOptions s = new DeliverySearch.SearchOptions("");
        s.tknum = tknumTextView.getText().toString();
        int l = s.tknum.length();
        if (l > 0 && l < 10) {
            while (l++ != 10)
                s.tknum = "0" + s.tknum;
        }
        s.date = dateTextView.getText().toString();
        if (mListener != null)
            mListener.onFindClick(s);
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
        //do nothing
    }

    @Override
    public String getTitle() {
        return "Поиск доставок";
    }

    @Override
    public String getPCabTag() {
        return getTitle();
    }

    @Override
    public String getBackStackName() {
        return getTitle();
    }

    @Override
    public boolean getDrawerEnabled() {
        return true;
    }
}
