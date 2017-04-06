package com.rosa.swift.mvp.assignments.base;

import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rosa.motocross.R;
import com.rosa.swift.mvp.assignments.base.repository.Dto.AssignmentDto;

import java.util.ArrayList;
import java.util.List;

public class AssignmentListAdapter extends RecyclerView.
        Adapter<AssignmentListAdapter.AssignmentHolder> {

    private static final int EMPTY_STATE = 0;
    private static final int NON_EMPTY_STATE = 1;

    private AssignmentListPresenter mPresenter;

    private List<AssignmentDto> mAssignments = new ArrayList<>();

    public AssignmentListAdapter(AssignmentListPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public int getItemCount() {
        return mAssignments != null && !mAssignments.isEmpty() ? mAssignments.size() : 1;
    }

    @Override
    public AssignmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        @LayoutRes
        int layoutId = viewType == NON_EMPTY_STATE ? R.layout.list_item_assignment :
                R.layout.item_empty_assignments;

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new AssignmentHolder(view, mPresenter);
    }

    @Override
    public void onBindViewHolder(AssignmentHolder holder, int position) {
        if (getItemViewType(position) == EMPTY_STATE) {
            return;
        }

        holder.bindAssignment(mAssignments.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return mAssignments.isEmpty() ? EMPTY_STATE : NON_EMPTY_STATE;
    }

    public void setList(List<AssignmentDto> assignments) {
        mAssignments = assignments;
        notifyDataSetChanged();
    }

    class AssignmentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AssignmentListPresenter mPresenter;

        private TextView mDescriptionText;

        private AssignmentDto mAssignment;

        AssignmentHolder(View itemView, AssignmentListPresenter presenter) {
            super(itemView);
            mPresenter = presenter;
            mDescriptionText = (TextView) itemView.findViewById(R.id.description_txt);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getItemViewType() == NON_EMPTY_STATE) {
                mPresenter.onAssignmentClick(getAdapterPosition());
            }
        }

        void bindAssignment(AssignmentDto assignment) {
            mAssignment = assignment;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mDescriptionText.setText(Html.fromHtml(mAssignment.getHtmlDescriptionForList(),
                        Html.FROM_HTML_MODE_LEGACY));
            } else {
                mDescriptionText.setText(Html.fromHtml(mAssignment.getHtmlDescriptionForList()));
            }
        }

    }

}
