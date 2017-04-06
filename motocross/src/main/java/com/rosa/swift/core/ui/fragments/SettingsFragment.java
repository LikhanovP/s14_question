package com.rosa.swift.core.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.rosa.motocross.R;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.data.dto.settings.ChatSettings;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements CabinetFragment {
    private static int code = R.id.pcab_action_settings;

    private FragmentListener mListener;
    private RadioGroup mRadioGroup;

    private ChatSettings mChatSettings;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    public SettingsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChatSettings = DataRepository.getInstance().getChatSettings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int id = 0;
        switch (mChatSettings.getNotificationType()) {
            case NoNotification:
                id = R.id.notifNoneRB;
                break;
            case Vibrate:
                id = R.id.vibrateRB;
                break;
            case Sound:
                id = R.id.soundRB;
                break;
        }
        mRadioGroup = (RadioGroup) view.findViewById(R.id.chatNotifGroup);
        mRadioGroup.check(id);
    }

    private void saveSettings() {
        ChatSettings.NotificationType type;
        switch (mRadioGroup.getCheckedRadioButtonId()) {
            case R.id.notifNoneRB:
                type = ChatSettings.NotificationType.NoNotification;
                break;
            case R.id.vibrateRB:
                type = ChatSettings.NotificationType.Vibrate;
                break;
            case R.id.soundRB:
                type = ChatSettings.NotificationType.Sound;
                break;
            default:
                type = ChatSettings.NotificationType.Sound;
                break;
        }
        mChatSettings.setNotificationType(type);
        DataRepository.getInstance().setChatSettings(mChatSettings);
        Toast.makeText(getActivity(), "Сохранено", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void refreshData() {

    }

    @Override
    public String getTitle() {
        return "Настройки";
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings_fragment_menu, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mListener != null)
            mListener.onFragmentStart(code);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_settings:
                saveSettings();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
