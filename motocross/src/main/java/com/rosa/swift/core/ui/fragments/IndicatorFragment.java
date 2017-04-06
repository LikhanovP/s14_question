package com.rosa.swift.core.ui.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rosa.motocross.R;
import com.rosa.swift.SwiftApplication;
import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.business.utils.SapRequestUtils;
import com.rosa.swift.core.business.utils.StringUtils;
import com.rosa.swift.core.data.dto.cup.CupStatus;
import com.rosa.swift.core.network.json.sap.cup.JCupSetInfoOut;
import com.rosa.swift.core.network.json.sap.driverRecords.JDriverRecords;
import com.rosa.swift.core.network.responses.photosession.CupStatusResponse;
import com.rosa.swift.core.network.responses.photosession.CupViewsResponse;
import com.rosa.swift.core.network.services.sap.ServiceCallback;
import com.rosa.swift.core.network.services.sap.WSException;
import com.rosa.swift.core.ui.activities.MainActivity;
import com.rosa.swift.core.ui.adapters.CupSetListAdapter;
import com.rosa.swift.core.ui.adapters.DriverRecordsListAdapter;
import com.rosa.swift.core.ui.views.ArcProgressBar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IndicatorFragment extends Fragment implements CabinetFragment, View.OnClickListener {

    private JDriverRecords result;

    private FragmentListener mListener;
    FragmentTransaction fragmentTransaction;
    private FragmentListener mListenerCup;

    private LinearLayout headerLin;
    private View avgArcView;
    private ListView dirverRecordsListView;
    private TextView avgMarkTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private static final String DRIVER_RECORDS_INFO_LIST = "DRIVER_RECORDS_INFO_LIST";
    private DecimalFormat decimalFormat;


    //cup
    private TextView mTitleCupTxtCup;
    private TextView mDateNextCupTxtCup;
    private TextView mTimeLeftCupTxtCup;
    private View avgArcViewCup;
    private ListView cupListViewCup;
    private TextView avgMarkTextViewCup;
    private View mCupItemTopViewCup;

    private Handler mTimerHandlerCup;

    private SwipeRefreshLayout swipeRefreshLayoutCup;

    private JCupSetInfoOut resultCup;

    private static final String CUP_SET_INFO_LIST = "CUP_SET_INFO_LIST";

    private CupStatus mCupStatus;
    private Date mCupDate;
    private String mCupDateString;

    private long mTimeLeftToNextCup = Constants.STOP_TIME_FOR_CUP_SESSION;

    private SimpleDateFormat mCupDateFormat = new SimpleDateFormat(
            Constants.PATTERN_DATE_CUP_SESSION_NEXT,
            Locale.getDefault());

    private LinearLayout linHeaderCup;
    private LinearLayout ContainerCup;

    //end


    public static IndicatorFragment newInstance() {
        IndicatorFragment indicatorFragment = new IndicatorFragment();
        Bundle bundle = new Bundle();
        indicatorFragment.setArguments(bundle);
        return indicatorFragment;
    }

    public IndicatorFragment() {
        setHasOptionsMenu(true);
        setupDecimalFormat();
    }

    @Override
    public void onResume() {
        super.onResume();
        //swipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_indicator, container, false);

        fragmentTransaction = getFragmentManager().beginTransaction();

        //driver
        dirverRecordsListView = (ListView) view.findViewById(R.id.driver_records_list_view);
        avgMarkTextView = (TextView) view.findViewById(R.id.driver_records_list_avg_mark);
        avgArcView = view.findViewById(R.id.driver_records_list_avg_arc);
        headerLin = (LinearLayout) view.findViewById(R.id.linHeader);

        //cup
        cupListViewCup = (ListView) view.findViewById(R.id.cup_list_view);
        avgMarkTextViewCup = (TextView) view.findViewById(R.id.cup_list_avg_mark);
        avgArcViewCup = view.findViewById(R.id.cup_list_avg_arc);
        mCupItemTopViewCup = view.findViewById(R.id.cup_list_item_top);
        mTitleCupTxtCup = (TextView) view.findViewById(R.id.title_cup_session_txt);
        mDateNextCupTxtCup = (TextView) view.findViewById(R.id.date_next_cup_session_txt);
        mTimeLeftCupTxtCup = (TextView) view.findViewById(R.id.time_left_txt);

        mTimerHandlerCup = new Handler();
        mCupItemTopViewCup.setOnClickListener(this);

        ContainerCup = (LinearLayout) view.findViewById(R.id.conCup);

        linHeaderCup = (LinearLayout) view.findViewById(R.id.linHeader_cup);

        ContainerCup.setVisibility(View.GONE);
        linHeaderCup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContainerCup.getVisibility() == View.GONE) {
                    ContainerCup.setVisibility(View.VISIBLE);
                } else {
                    ContainerCup.setVisibility(View.GONE);
                }
            }
        });
        dirverRecordsListView.setVisibility(View.GONE);
        headerLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dirverRecordsListView.getVisibility() == View.GONE) {
                    dirverRecordsListView.setVisibility(View.VISIBLE);
                } else {
                    dirverRecordsListView.setVisibility(View.GONE);
                }
            }
        });


        if (savedInstanceState != null) {
            result = (JDriverRecords) savedInstanceState.getSerializable(DRIVER_RECORDS_INFO_LIST);
            resultCup = (JCupSetInfoOut) savedInstanceState.getSerializable(CUP_SET_INFO_LIST);
        }

        updateMarkViewCup();
        updateMarkView();
        lockNextCupView();
        return view;
    }

    private void lockNextCupView() {
        mCupItemTopViewCup.setEnabled(false);
        mTitleCupTxtCup.setTextAppearance(getContext(), R.style.CupTopItemUnavailableText);
        mDateNextCupTxtCup.setTextAppearance(getContext(), R.style.CupTopItemUnavailableText);
        mTimeLeftCupTxtCup.setTextAppearance(getContext(), R.style.CupTopItemUnavailableText);
    }

    private void doFindClosedCup() {
        SapRequestUtils.getAllCupForPeriod(new ServiceCallback() {

            @Override
            public void onFinished(String evParam) {
                try {
                    Gson g = new Gson();
                    resultCup = g.fromJson(evParam, JCupSetInfoOut.class);
                    swipeRefreshSetRefreshingCup(false);
                    updateMarkViewCup();
                } catch (Exception e) {
                    Log.e(e.getMessage());
                }
            }

            @Override
            public void onEndedRequest() {
            }

            @Override
            public void onFinishedWithException(WSException ex) {
            }

            @Override
            public void onCancelled() {
            }
        });
    }

    private void swipeRefreshSetRefreshingCup(boolean refreshing) {
        if (swipeRefreshLayoutCup != null)
            swipeRefreshLayoutCup.setRefreshing(refreshing);
    }

    private void updateMarkViewCup() {
        float avgMarkCup = 0;

        if (mListenerCup != null) {
            if (resultCup != null) {
                avgMarkCup = getAvgMarkFromResultCup(resultCup.cup_sets_info);

                CupSetListAdapter adapter = new CupSetListAdapter(getActivity(),
                        R.layout.cup_item_layout, resultCup.cup_sets_info);
                adapter.setOnItemClickListener(new CupSetListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, JCupSetInfoOut.JCupSetInfo cupSetInfo, int position) {
                        IndicatorFragment.this.onItemClickCup(cupSetInfo);
                    }
                });

                cupListViewCup.setAdapter(adapter);
            }
            avgMarkTextViewCup.setText(decimalFormat.format(avgMarkCup));

            //баловство
            ArcProgressBar superDrawable = new ArcProgressBar(getActivity());
            superDrawable.setWidth(84);
            superDrawable.setHeight(84);
            superDrawable.setArcStrokeWidth(16);
            superDrawable.setMaxValue(10);
            superDrawable.setBackgroundColor(R.color.app_gray_color);
            superDrawable.setProgressColor(R.color.app_red_color);
            superDrawable.setGradient(R.color.app_red_color, R.color.sdvor_yellow, R.color.app_green_color);

            superDrawable.setValue(avgMarkCup);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                avgArcViewCup.setBackground(superDrawable);
            } else {
                avgArcViewCup.setBackgroundDrawable(superDrawable);
            }
            //конец баловства
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_list:
                refreshData();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDecimalFormat() {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(' ');
        decimalFormat = new DecimalFormat("#.###", otherSymbols);
    }

    private float getAvgMarkFromResultCup(List<JCupSetInfoOut.JCupSetInfo> cupSetsInfo) {
        float avgMark = 0;
        if (cupSetsInfo != null) {
            for (JCupSetInfoOut.JCupSetInfo setInfo : cupSetsInfo) {
                try {
                    avgMark += (StringUtils.isNullOrEmpty(setInfo.cup_mark) ? 0 : Float.parseFloat(setInfo.cup_mark));
                } catch (NumberFormatException e) {
                    Log.e(e.getMessage());
                }
            }
            if (cupSetsInfo.size() > 0)
                avgMark = avgMark / cupSetsInfo.size();
        }
        return avgMark;
    }

    private float getAvgMarkFromResult(List<JDriverRecords.JDriverRecord> driver_records_info) {
        float avgMark = 0;
        if (driver_records_info != null) {
            for (JDriverRecords.JDriverRecord records_info : driver_records_info) {
                try {
                    avgMark += (StringUtils.isNullOrEmpty(records_info.record_mark) ? 0 : Float.parseFloat(records_info.record_mark));
                } catch (NumberFormatException e) {
                    Log.e(e.getMessage());
                }
            }
            if (driver_records_info.size() > 0)
                avgMark = avgMark / driver_records_info.size();
        }
        return avgMark;
    }

    private void onItemClick(JDriverRecords.JDriverRecord driverRecord) {
        if (mListener != null)
            mListener.onDriverRecordsSelected(driverRecord);
    }

    private void onItemClickCup(JCupSetInfoOut.JCupSetInfo cupSetInfo) {
        if (mListenerCup != null)
            mListenerCup.onCupSelected(cupSetInfo);
    }

    private void updateMarkView() {
        float avgMark = 0;

        if (mListener != null) {
            if (result != null) {
                avgMark = getAvgMarkFromResult(result.driver_records_info);

                DriverRecordsListAdapter adapter = new DriverRecordsListAdapter(getActivity(),
                        R.layout.driver_records_item_layout, result.driver_records_info);
                adapter.setOnItemClickListener(new DriverRecordsListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, JDriverRecords.JDriverRecord driverRecords, int position) {
                        IndicatorFragment.this.onItemClick(driverRecords);
                    }
                });

                dirverRecordsListView.setAdapter(adapter);
            }
            avgMarkTextView.setText(String.format("%.3f", avgMark));
            avgMarkTextView.setText(decimalFormat.format(avgMark));

            //баловство
            ArcProgressBar superDrawable = new ArcProgressBar(getActivity());
            superDrawable.setWidth(84);
            superDrawable.setHeight(84);
            superDrawable.setArcStrokeWidth(16);
            superDrawable.setMaxValue(10);
            superDrawable.setBackgroundColor(R.color.app_gray_color);
            superDrawable.setProgressColor(R.color.app_red_color);
            superDrawable.setGradient(R.color.app_red_color, R.color.sdvor_yellow, R.color.app_green_color);

            superDrawable.setValue(avgMark);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                avgArcView.setBackground(superDrawable);
            } else {
                avgArcView.setBackgroundDrawable(superDrawable);
            }
            //конец баловства
        }
    }


    private void swipeRefreshSetRefreshing(boolean refreshing) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(refreshing);

    }

    private void doFindDriverRecords() {
        SapRequestUtils.getDriverRecords(new ServiceCallback() {
            @Override
            public void onFinished(String evParam) {
                try {
                    Gson g = new Gson();
                    result = g.fromJson(evParam, JDriverRecords.class);
                    swipeRefreshSetRefreshing(false);
                    updateMarkView();
                } catch (Exception e) {
                    Log.e(e.getMessage());
                }
            }

            @Override
            public void onEndedRequest() {
            }

            @Override
            public void onFinishedWithException(WSException ex) {
            }

            @Override
            public void onCancelled() {
            }
        });
    }


    @Override
    public void onViewCreated(final View _view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(_view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void refreshData() {
        swipeRefreshSetRefreshing(true);
        swipeRefreshSetRefreshingCup(true);
        doFindDriverRecords();
        doFindClosedCup();
    }

    @Override
    public String getTitle() {
        return SwiftApplication.getApplication().getResources().getString(R.string.pcab_action_indicator_text);
    }

    @Override
    public boolean getDrawerEnabled() {
        return true;
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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (FragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentListener");
        }
        try {
            mListenerCup = (FragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FragmentListener");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mListenerCup = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        stopTimerForNextCup();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mListener != null) {
            mListener.onFragmentStart(R.id.pcab_action_indicator);
        }
        startTimerForNextCup();
        if (mListenerCup != null)
            mListenerCup.onFragmentStart(R.id.pcab_action_cup_set_list);
    }

    private void startTimerForNextCup() {
        requestCupStatus();
    }

    private MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    private void requestCupStatus() {
        getMainActivity().showProgress(R.string.cup_session_get_status_progress);
        try {
            SapRequestUtils.getCupStatus(new ServiceCallback() {
                @Override
                public void onEndedRequest() {
                    getMainActivity().hideProgress();
                }

                @Override
                public void onFinished(String evParams) {
                    try {
                        //информацию о следующей фотосессии
                        mCupStatus = new CupStatus(new Gson().fromJson(evParams, CupStatusResponse.class));
                        mCupDate = mCupStatus.getCupDate();
                        if (mCupStatus.getCupDate() != null && mCupDate != null) {
                            //если исходные даты корректны подготовим их выводу на представлении
                            mCupDateString = mCupDateFormat.format(mCupStatus.getCupDate());
                            mTimeLeftToNextCup = mCupStatus.getTimeToNextCup();

                            if (mTimeLeftToNextCup > Constants.STOP_TIME_FOR_CUP_SESSION) {
                                //запускаем таймер и отображаем дату следующей фотосессии
                                mTimerHandlerCup.postDelayed(updateTimerThread, 0);
                            }
                        }
                    } catch (Exception e) {
                        mCupStatus = null;
                        mCupDate = null;
                        Log.e(e.getMessage(), e);
                        getMainActivity().hideProgress();
                    }
                }

                @Override
                public void onFinishedWithException(WSException ex) {
                    getMainActivity().showErrorMessage(ex.getMessage());
                    getMainActivity().hideProgress();
                }

                @Override
                public void onCancelled() {
                    getMainActivity().hideProgress();
                }
            });
        } catch (Exception ignored) {
            getMainActivity().hideProgress();
            getMainActivity().showErrorMessage(R.string.cup_session_get_status_error_msg);
        }
    }

    //TODO: 21.11.2016 перевести на AsyncTask, вынести в отдельный класс
    private Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            if (mTimeLeftToNextCup > Constants.DELTA_TIME_FOR_CUP_SESSION) {
                mTimeLeftToNextCup -= Constants.DELTA_TIME_FOR_CUP_SESSION;

                updateTimerForNextCupSession(mTimeLeftToNextCup);
                mTimerHandlerCup.postDelayed(this, Constants.DELTA_TIME_FOR_CUP_SESSION);
            } else {
                stopTimerForNextCup();
            }
        }
    };

    private void stopTimerForNextCup() {
        mTimeLeftToNextCup = Constants.STOP_TIME_FOR_CUP_SESSION;
        mTimerHandlerCup.removeCallbacks(updateTimerThread);

        lockNextCupView();
    }

    private void updateTimerForNextCupSession(long currentTimeInMillis) {
        checkForCupAvailable();

        //парсим секунды
        if (currentTimeInMillis >= Constants.STOP_TIME_FOR_CUP_SESSION) {
            long deltaSecs = currentTimeInMillis / 1000;
            long days = deltaSecs / 86400;
            long hours = (deltaSecs % 86400) / 3600;
            long minutes = (deltaSecs % 3600) / 60;
            long seconds = deltaSecs % 60;

            String timeString = String.format("%1$s д : %2$s ч : %3$s мин : %4$s сек.",
                    days, hours, minutes, seconds);

            mDateNextCupTxtCup.setText(mCupDateString);
            mTimeLeftCupTxtCup.setText(timeString);
        }
    }

    private void checkForCupAvailable() {
        if (mCupStatus != null) {
            if (mCupStatus.isAvailableForTime(mTimeLeftToNextCup)) {
                unlockNextCupView();
            }
        }
    }

    private void unlockNextCupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mCupItemTopViewCup.setEnabled(true);
            mTitleCupTxtCup.setTextAppearance(R.style.CupViewItemAvailableText);
            mDateNextCupTxtCup.setTextAppearance(R.style.CupViewItemAvailableText);
            mTimeLeftCupTxtCup.setTextAppearance(R.style.CupViewItemAvailableText);
        } else {
            Context context = getContext();
            if (context != null) {
                mCupItemTopViewCup.setEnabled(true);
                mTitleCupTxtCup.setTextAppearance(context, R.style.CupViewItemAvailableText);
                mDateNextCupTxtCup.setTextAppearance(context, R.style.CupViewItemAvailableText);
                mTimeLeftCupTxtCup.setTextAppearance(context, R.style.CupViewItemAvailableText);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.refresh_menu_fragment, menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(DRIVER_RECORDS_INFO_LIST, result);
        outState.putSerializable(CUP_SET_INFO_LIST, resultCup);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cup_list_item_top:
                startCupTaskPerforming();
                break;
        }
    }

    private void startCupTaskPerforming() {
        getMainActivity().showProgress(R.string.cup_session_get_tasks_progress);
        try {
            SapRequestUtils.getCupTasks(new ServiceCallback() {
                @Override
                public void onEndedRequest() {
                    getMainActivity().hideProgress();
                }

                @Override
                public void onFinished(String evParams) {
                    getMainActivity().hideProgress();
                    if (!TextUtils.isEmpty(evParams)) {
                        CupViewsResponse cupInfo = new Gson().fromJson(evParams, CupViewsResponse.class);
                        performCupTask(cupInfo);
                    } else {
                        getMainActivity().showErrorMessage(R.string.cup_screen_set_msg_get_tasks_error);
                    }
                }

                @Override
                public void onFinishedWithException(WSException ex) {
                    getMainActivity().showErrorMessage(R.string.cup_screen_set_msg_get_tasks_error);
                }

                @Override
                public void onCancelled() {
                    getMainActivity().hideProgress();
                }

            });
        } catch (Exception ignored) {
            getMainActivity().hideProgress();
            getMainActivity().showErrorMessage(R.string.cup_screen_set_msg_get_tasks_error);
        }
    }

    private void performCupTask(CupViewsResponse cupInfoRes) {
        if (cupInfoRes != null) {
            getMainActivity().startCupRequest(false, false, false);
        }
    }
}
