package com.rosa.swift.mvp.ratings.documents;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.ui.fragments.CabinetFragment;
import com.rosa.swift.core.ui.fragments.FragmentListener;

import static android.app.Activity.RESULT_OK;

public class PhotoDocumentListFragment extends ListFragment implements CabinetFragment {
    private FragmentListener mListener;
    private PhotoDocumentsAdapter myListAdapter;

    public static PhotoDocumentListFragment newInstance() {
        PhotoDocumentListFragment fragment = new PhotoDocumentListFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public PhotoDocumentListFragment() {
        setHasOptionsMenu(true);
    }

    //region LifeCycle

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myListAdapter = new PhotoDocumentsAdapter(getActivity(), R.layout.list_item_photo_document,
                DataRepository.getInstance().getPhotoDocumentTasks());
        setListAdapter(myListAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mListener != null)
            mListener.onFragmentStart(R.id.pcab_action_photo_base);
    }

    @Override
    public void onResume() {
        super.onResume();
        myListAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        return inflater.inflate(R.layout.fragment_photo_base, null);
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

    //endregion

    //region CabinetFragment implementation

    @Override
    public void refreshData() {

    }

    @Override
    public String getTitle() {
        return "Личные данные";
    }

    @Override
    public boolean getDrawerEnabled() {
        return true;
    }

    @Override
    public String getPCabTag() {
        return null;
    }

    @Override
    public String getBackStackName() {
        return null;
    }

    //endregion

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        startActivityForResult(PhotoDocumentActivity.newInstance(getContext(), position),
                Constants.REQUEST_PHOTO_DOCUMENT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_PHOTO_DOCUMENT: {
                if (resultCode == RESULT_OK) {
                    getActivity().onBackPressed();
                }
            }
        }
    }

}
