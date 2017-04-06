package com.rosa.swift.mvp.history;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rosa.motocross.R;
import com.rosa.swift.mvp.history.repository.DeliveryDto;

import java.util.ArrayList;
import java.util.List;

public class DeliveriesHistoryAdapter extends RecyclerView.Adapter<DeliveriesHistoryAdapter.ViewHolder> {
    private static final int ITEM_COUNT_FOR_EMPTY_LIST = 1;

    private DeliveriesHistoryPresenter mPresenter;
    private List<DeliveryDto> mDeliveryList = new ArrayList<>();
    private static final int EMPTY_STATE = 0;
    private static final int NON_EMPTY_STATE = 1;

    private Context mContext;

    public DeliveriesHistoryAdapter(DeliveriesHistoryPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public int getItemCount() {
        if (mDeliveryList != null && !mDeliveryList.isEmpty()) return mDeliveryList.size();
        else return ITEM_COUNT_FOR_EMPTY_LIST;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) mContext = parent.getContext();

        @LayoutRes int layoutId;

        if (viewType == NON_EMPTY_STATE)
            layoutId = R.layout.item_deliveries;
        else
            layoutId = R.layout.item_empty_deliveries;

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);

        return new ViewHolder(view, mPresenter);
    }

    @Override
    public int getItemViewType(int position) {
        if (mDeliveryList.isEmpty())
            return EMPTY_STATE;
        else
            return NON_EMPTY_STATE;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == EMPTY_STATE) return;

        DeliveryDto dto = mDeliveryList.get(position);

        holder.mDeliveryNumber.setText(mContext.getString(R.string.delivery_number_mask, dto.getTkNum()));
    }

    public void setList(List<DeliveryDto> list) {
        mDeliveryList = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private DeliveriesHistoryPresenter mPresenter;
        private TextView mDeliveryNumber;

        public ViewHolder(View itemView, DeliveriesHistoryPresenter presenter) {
            super(itemView);
            mPresenter = presenter;
            mDeliveryNumber = (TextView) itemView.findViewById(R.id.delivery_number);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getItemViewType() == NON_EMPTY_STATE) {
                mPresenter.onListItemClick(getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (getItemViewType() == NON_EMPTY_STATE) {
                mPresenter.openOptionsForDelivery(
                        mDeliveryList.get(getAdapterPosition()).getTkNum(), v);
            }
            return true;
        }
    }
}