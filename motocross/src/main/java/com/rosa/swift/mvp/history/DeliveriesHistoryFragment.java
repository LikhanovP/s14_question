package com.rosa.swift.mvp.history;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.rosa.motocross.R;
import com.rosa.swift.core.ui.activities.LogonlessActivity;
import com.rosa.swift.core.ui.activities.MainActivity;
import com.rosa.swift.core.ui.base.BaseFragment;
import com.rosa.swift.core.ui.decorators.RecyclerViewDivider;
import com.rosa.swift.core.ui.fragments.CabinetFragment;
import com.rosa.swift.mvp.history.repository.DeliveryDto;

import java.util.List;

public class DeliveriesHistoryFragment extends BaseFragment implements IDeliveriesHistoryView, CabinetFragment {
    public static final String TAG = "DeliveriesFragment";

    @InjectPresenter
    public DeliveriesHistoryPresenter mPresenter;

    private RecyclerView mDeliveryList;
    private EditText mDeliveryNumberEt;

    private DeliveriesHistoryAdapter mDeliveriesAdapter;

    public static DeliveriesHistoryFragment newInstance() {
        return new DeliveriesHistoryFragment();
    }

    @ProvidePresenter
    DeliveriesHistoryPresenter providerDeliveriesPresenter() {
        return new DeliveriesHistoryPresenter((MainActivity) getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deliveries, container, false);
        setInflatedView(view);

        assignViews();
        initRecyclerView();

        setHasOptionsMenu(true);
        return mInflatedView;
    }

    private void initRecyclerView() {
        mDeliveriesAdapter = new DeliveriesHistoryAdapter(mPresenter);
        mDeliveryList.setLayoutManager(new LinearLayoutManager(getContext()));
        mDeliveryList.addItemDecoration(new RecyclerViewDivider(getContext()));
        mDeliveryList.setAdapter(mDeliveriesAdapter);
    }

    private void assignViews() {
        mDeliveryList = $(R.id.delivery_list);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_find_delivery:
                mPresenter.onSearchClick();
                break;
            case R.id.action_refresh_list:
                mPresenter.onRefreshClick();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh_menu_fragment, menu);
    }

    //region===================== PCabFragment =====================

    @Override
    public String getBackStackName() {
        return null;
    }

    @Override
    public void refreshData() {
        /*empty*/
    }

    @Override
    public String getTitle() {
        return "Список доставок";
    }

    @Override
    public boolean getDrawerEnabled() {
        return true;
    }

    @Override
    public String getPCabTag() {
        return getTitle();
    }

    //endregion

    //region===================== IDeliveriesView =====================

    @Override
    public void hideLoad() {
        ((LogonlessActivity) getActivity()).hideProgress();
    }

    @Override
    public void showLoad() {
        ((LogonlessActivity) getActivity()).showProgress();
    }

    @Override
    public void showPopupMenuForDelivery(String tkNumber, View clickedView) {
        PopupMenu menu = new PopupMenu(getActivity(), clickedView);
        menu.inflate(R.menu.delivery_fragment);
        menu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.action_scan:
                    mPresenter.onScanClick();
                    break;
                case R.id.action_incident:
                    mPresenter.onIncidentClick();
                    break;
                case R.id.action_send_message:
                    mPresenter.onSendMessageClick();
                    break;
            }
            return false;
        });
        menu.show();
    }

    @Override
    public void showDetailInfo(DeliveryDto delivery) {
        DialogFragment newFragment = DeliveryInfoDialog.newInstance(delivery);
        newFragment.show(getChildFragmentManager(), "dialog");
    }

    @Override
    public void showHistoryList(List<DeliveryDto> deliveries) {
        mDeliveriesAdapter.setList(deliveries);
    }

    @Override
    public void showDeliverySearchDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = dialogBuilder.setTitle(R.string.delivery_search_dialog_title)
                .setView(R.layout.dialog_delivery_search)
                .setPositiveButton(R.string.delivery_search,
                        (d, which) -> {
                            mPresenter.findDeliveryByNumber(mDeliveryNumberEt.getText().toString());
                            closeSoftKeyboard();
                        })
                .setNegativeButton(R.string.delivery_search_cancel, null)
                .show();

        mDeliveryNumberEt = (EditText) dialog.findViewById(R.id.find_tknum_edit);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    //endregion

    @SuppressWarnings("ConstantConditions")
    private void closeSoftKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }
}