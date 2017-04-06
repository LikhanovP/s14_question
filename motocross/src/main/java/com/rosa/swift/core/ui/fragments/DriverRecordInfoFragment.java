package com.rosa.swift.core.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.CommonUtils;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.business.utils.SapRequestUtils;
import com.rosa.swift.core.business.utils.StringUtils;
import com.rosa.swift.core.network.json.sap.driverRecords.JDriverRecordInfo;
import com.rosa.swift.core.network.json.sap.driverRecords.JDriverRecords;
import com.rosa.swift.core.network.services.sap.ServiceCallback;
import com.rosa.swift.core.network.services.sap.WSException;
import com.rosa.swift.core.ui.activities.LogonlessActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by egorov on 07.12.2016.
 */

public class DriverRecordInfoFragment extends Fragment implements CabinetFragment {

    private JDriverRecords.JDriverRecord driverRecordInfo;
    private JDriverRecordInfo recordInfo;
    private LogonlessActivity baseActivity;
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private DateFormat timeFormat = new SimpleDateFormat("hh:mm");

    public static DriverRecordInfoFragment getInstance(JDriverRecords.JDriverRecord driverRecordInfo) {
        DriverRecordInfoFragment fragment = new DriverRecordInfoFragment();
        fragment.setDriverRecordInfo(driverRecordInfo);
        return fragment;
    }

    TextView info_record_title, info_record_text, info_record_mark, info_record_tknum, info_record_time;

    public void setDriverRecordInfo(JDriverRecords.JDriverRecord driverRecordInfo) {
        this.driverRecordInfo = driverRecordInfo;
        doGetRecordInfo(driverRecordInfo.record_id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.driver_record_info, container, false);
        info_record_title = (TextView) view.findViewById(R.id.info_record_title);
        info_record_text = (TextView) view.findViewById(R.id.info_record_text);
        info_record_mark = (TextView) view.findViewById(R.id.info_record_mark);
        info_record_tknum = (TextView) view.findViewById(R.id.info_record_tknum);
        info_record_time = (TextView) view.findViewById(R.id.info_record_time);
        return view;
    }

    public void doGetRecordInfo(String recordId) {
        showProgress(R.string.get_data_progress);
        SapRequestUtils.getDriverRecordInfo(recordId, new ServiceCallback() {
            @Override
            public void onFinished(String evParam) {
                try {
                    if (!StringUtils.isNullOrEmpty(evParam)) {
                        recordInfo = null;
                        try {
                            Gson g = new Gson();
                            recordInfo = g.fromJson(evParam, JDriverRecordInfo.class);
                        } catch (Exception ex) {
                            Log.e(ex.getMessage());
                        }
                        updateView();
                    }
                } catch (Exception e) { //callback.onSearchCompletedError(e);
                    Log.e(e.getMessage());
                }
            }

            @Override
            public void onEndedRequest() {
                hideProgress();
            }

            @Override
            public void onFinishedWithException(WSException ex) { //callback.onSearchCompletedError(ex);
                CommonUtils.ShowErrorMessage(getActivity(), ex.getMessage());
            }

            @Override
            public void onCancelled() { //callback.onSearchCompleted(null);
                hideProgress();
            }
        });
    }

    private void updateView() {
        if (recordInfo.record_mark == null) {
            recordInfo.record_mark = "0";
        }
        info_record_mark.setText(String.format("%.0f", Float.parseFloat(recordInfo.record_mark)) + " из 10");
        info_record_time.setText(timeFormat.format(StringUtils.getDateFromSapDateTime(recordInfo.record_erdat, recordInfo.record_erzet)));

        if (recordInfo.record_title != null) {
            info_record_title.setText(recordInfo.record_title);
        }
        if (recordInfo.record_text != null) {
            info_record_text.setText(recordInfo.record_text);
        }
        if (recordInfo.record_tknum != null) {
            info_record_tknum.setText(recordInfo.record_tknum);
        }
    }

    private void showProgress(int stringId) {
        if (baseActivity != null) {
            baseActivity.showProgress(stringId);
        }
    }

    private void hideProgress() {
        if (baseActivity != null) {
            baseActivity.hideProgress();
        }
    }

    //region Стандартное
    @Override
    public String getTitle() {
        return dateFormat.format(StringUtils.SAPDateToDate(driverRecordInfo.record_erdat));
    }

    @Override
    public String getPCabTag() {
        return null;
    }

    @Override
    public void refreshData() {

    }

    @Override
    public boolean getDrawerEnabled() {
        return false;
    }

    @Override
    public String getBackStackName() {
        return null;
    }
    //endregion
}
