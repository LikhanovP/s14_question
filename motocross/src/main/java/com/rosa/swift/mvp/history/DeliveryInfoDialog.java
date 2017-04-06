package com.rosa.swift.mvp.history;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rosa.motocross.R;
import com.rosa.swift.mvp.history.repository.DeliveryDto;

public class DeliveryInfoDialog extends DialogFragment {
    private static final String DELIVERY = "DELIVERY";

    private TextView mDetailInfo;
    private AppCompatButton mCancelButton;
    private ImageButton mMoreActionButton;

    private DeliveryDto mDelivery;

    public static DeliveryInfoDialog newInstance(DeliveryDto delivery) {
        DeliveryInfoDialog dialogFragment = new DeliveryInfoDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(DELIVERY, delivery);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDelivery = getArguments().getParcelable(DELIVERY);

        @SuppressLint("InflateParams")
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_delivery_detail_info, null);

        setUpViews(view);
        initClickListeners();

        //noinspection deprecation
        mDetailInfo.setText(Html.fromHtml(mDelivery.getHtmlDescription()));

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setCancelable(false)
                .create();
    }

    private void setUpViews(View rootView) {
        mDetailInfo = (TextView) rootView.findViewById(R.id.delivery_description);
        mCancelButton = (AppCompatButton) rootView.findViewById(R.id.cancel);
        mMoreActionButton = (ImageButton) rootView.findViewById(R.id.more_actions);
    }

    private void initClickListeners() {
        mMoreActionButton.setOnClickListener(v ->
                ((IDeliveriesHistoryView) getParentFragment())
                        .showPopupMenuForDelivery(mDelivery.getTkNum(), v)
        );

        mCancelButton.setOnClickListener(v -> dismiss());
    }
}
