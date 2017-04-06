package com.rosa.swift.mvp.assignments.relocations;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.rosa.motocross.R;
import com.rosa.swift.core.ui.base.BaseFragment;
import com.rosa.swift.core.ui.decorators.RecyclerViewDivider;
import com.rosa.swift.mvp.assignments.base.AssignmentListAdapter;
import com.rosa.swift.mvp.assignments.base.AssignmentListPresenter;
import com.rosa.swift.mvp.assignments.base.IAssignmentListView;
import com.rosa.swift.mvp.assignments.base.repository.Dto.AssignmentDto;
import com.rosa.swift.mvp.assignments.relocations.relocation.RelocationFragment;

import java.util.List;

public class RelocationListFragment extends BaseFragment implements IAssignmentListView {

    @InjectPresenter
    public AssignmentListPresenter mPresenter;

    private RecyclerView mRelocationList;

    private AssignmentListAdapter mRelocationListAdapter;

    public static RelocationListFragment newInstance() {
        return new RelocationListFragment();
    }

    //region Lifecycle

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_assignment_list, container, false);
        setInflatedView(view);
        getActivity().setTitle("Перемещения");

        initViews();
        initRecyclerView();

        return view;
    }

    //endregion

    private void initViews() {
        mRelocationList = $(R.id.assignment_recycler_view);
    }

    private void initRecyclerView() {
        mRelocationListAdapter = new AssignmentListAdapter(mPresenter);
        mRelocationList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRelocationList.addItemDecoration(new RecyclerViewDivider(getContext()));
        mRelocationList.setAdapter(mRelocationListAdapter);
    }

    //region IAssignmentListView

    @Override
    public void showItems(List<AssignmentDto> assignments) {
        mRelocationListAdapter.setList(assignments);
    }

    @Override
    public void showRelocation(String number) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() == 0) {
            Fragment relocationFragment = RelocationFragment.newInstance(number);
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, relocationFragment)
                    .addToBackStack("Relocation")
                    .commit();
        }
    }

    @Override
    public void close() {
        getActivity().finish();
    }

    //endregion
}
