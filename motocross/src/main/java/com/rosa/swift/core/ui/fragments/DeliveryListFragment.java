package com.rosa.swift.core.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.rosa.motocross.R;
import com.rosa.swift.core.business.DeliverySearch;
import com.rosa.swift.core.data.dto.common.Delivery;

import java.util.List;

public class DeliveryListFragment extends ListFragment implements CabinetFragment {
    private static int code = R.id.pcab_action_delivery_list;
    private static String MODE = "mode";

    public int getMode() {
        return mode;
    }

    private int mode;
    private FragmentListener mListener;
    private DeliverySearch.SearchOptions options;
    private DeliverySearch.SearchResult result;
    private boolean need_refresh;

    private List<Delivery> items;

    public static DeliveryListFragment newInstance(int mode) {
        DeliveryListFragment fragment = new DeliveryListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MODE, mode);
        fragment.setArguments(bundle);
        return fragment;
    }

    public DeliveryListFragment() {
        setHasOptionsMenu(true);
    }

    public boolean setSearchOptions(DeliverySearch.SearchOptions options) {
        boolean ret = (this.options == null) || !this.options.equals(options) || (result == null);
        this.options = options;
        return ret;
    }

    public void setSearchResult(DeliverySearch.SearchResult result) {
        this.result = result;
        if (isAdded()) updateView();
    }

    private void updateView() {
        String[] deliveryText = new String[result.getDeliveryList().size()];
        int i = 0;
        for (Delivery delivery : result.getDeliveryList()) {
            deliveryText[i++] = delivery.getNumber();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_expandable_list_item_1, deliveryText);
        setListAdapter(adapter);
    }

    @Override
    public void refreshData() {
        if (mListener != null) mListener.doFind(options);
        else need_refresh = true;
    }

    @Override
    public String getTitle() {
        return mode == 0 ? "Список доставок" : "Результат поиска";
    }

    @Override
    public String getPCabTag() {
        return getTitle();
    }

    @Override
    public String getBackStackName() {
        return mode == 0 ? getTitle() : null;
    }

    @Override
    public boolean getDrawerEnabled() {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mode = getArguments().getInt(MODE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (need_refresh) {
            need_refresh = false;
            refreshData();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int item_pos = position;
                PopupMenu m = new PopupMenu(getActivity(), view);
                m.inflate(R.menu.delivery_fragment);
                m.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        mListener.onDeliveryMenuItemSelected(result.getDeliveryList().get(item_pos), menuItem);
                        return false;
                    }
                });
                m.show();
                return false;
            }
        });
        setListShown(true);
        setEmptyText("Ничего не найдено");
        if (result != null) updateView();
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (null != mListener) {
            try {
                mListener.onDeliverySelect(result.getDeliveryList().get(position));
            } catch (Exception ignored) {

            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.refresh_menu_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_list:
                if (mode == 0 && options != null)
                    options.forToday();
                refreshData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
