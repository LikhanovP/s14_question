package com.rosa.swift.mvp.assignments.relocations.relocation;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.rosa.motocross.R;
import com.rosa.swift.core.ui.activities.RelocationActivity;
import com.rosa.swift.core.ui.base.BaseFragment;
import com.rosa.swift.mvp.assignments.base.current.IAssignmentView;
import com.rosa.swift.mvp.assignments.base.repository.Dto.DriverMessageDto;

import java.util.List;


public class RelocationFragment extends BaseFragment implements IAssignmentView,
        NavigationView.OnNavigationItemSelectedListener {
    private static final String ARG_RELOCATION_NUMBER = "ARG_RELOCATION_NUMBER";

    @InjectPresenter
    public RelocationPresenter mPresenter;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private TextView mDescriptionText;

    private RelocationActivity mContext;

    private String mRelocationNumber;

    public static RelocationFragment newInstance(String number) {
        Bundle args = new Bundle();
        args.putString(ARG_RELOCATION_NUMBER, number);
        RelocationFragment fragment = new RelocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @ProvidePresenter
    RelocationPresenter provideRelocationPresenter() {
        return new RelocationPresenter(mRelocationNumber);
    }

    //region Lifecycle

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (RelocationActivity) getContext();
        mRelocationNumber = getArguments().getString(ARG_RELOCATION_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        final View view = inflater.inflate(R.layout.fragment_relocation, container, false);
        setInflatedView(view);
        initViews();
        initDrawerMenu();
        initToolbar();

        setHasOptionsMenu(true);

        return view;
    }

    //endregion

    private void initToolbar() {
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Перемещение");
        actionbar.setSubtitle(mRelocationNumber);
    }

    private void initViews() {
        mDrawerLayout = $(R.id.relocation_drawer);
        mNavigationView = $(R.id.navigation_view);
        mDescriptionText = $(R.id.relocation_txt);

        mDescriptionText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    //region Menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_relocation_current, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_assignment_complete:
                completeAssignment();
                break;
            case R.id.action_assignment_print:
                sendPrint();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //endregion

    //region NavigationMenu

    @Override
    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_incident:

                break;
            case R.id.action_send_message:
                sendMessage();
                break;
            case R.id.action_open_gate:
                openGate();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initDrawerMenu() {
        Menu menu = mNavigationView.getMenu();
        mNavigationView.setNavigationItemSelectedListener(this);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            switch (item.getItemId()) {
                case R.id.action_open_cabinet:
                    item.setVisible(false);
                    break;
                case R.id.action_open_gate:
                    item.setVisible(mPresenter.isOpenGateEnable());
                    break;
                case R.id.action_incident_photo:
                    item.setVisible(false);
                    break;
                case R.id.action_show_documents:
                    item.setVisible(false);
                    break;
                case R.id.action_update:
                    item.setVisible(false);
                    break;
            }
        }
    }

    //endregion

    //region IView

    @Override
    public void showError(String message) {
        mContext.showErrorMessage(message);
    }

    @Override
    public void showMessage(String message) {
        mContext.showInfoMessage(message);
    }

    @Override
    public void showError(int stringId) {
        mContext.showErrorMessage(stringId);
    }

    @Override
    public void showMessage(int stringId) {
        mContext.showInfoMessage(stringId);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(int stringId) {
        Toast.makeText(mContext, getString(stringId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {
        mContext.showSimpleProgress();
    }

    @Override
    public void hideProgress() {
        mContext.hideSimpleProgress();
    }

    //endregion

    //region IAssignmentView

    //region Show

    @Override
    public void showDescription(String htmlDescription) {
        if (!TextUtils.isEmpty(htmlDescription)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mDescriptionText.setText(Html.fromHtml(htmlDescription,
                        Html.FROM_HTML_MODE_LEGACY));
            } else {
                mDescriptionText.setText(Html.fromHtml(htmlDescription));
            }
        } else {
            mDescriptionText.setText(null);
        }
    }

    //endregion

    //region Complete

    @Override
    public void completeAssignment() {
        mPresenter.onFinishAssignmentClick();
    }

    @Override
    public void showConfirmCompleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog dialog = builder
                .setTitle(R.string.dialog_complete_relocation_title)
                .setView(R.layout.dialog_confirm)
                .setCancelable(false)
                .show();
        TextView messageText = (TextView) dialog.findViewById(R.id.message_text);
        Button positiveBtn = (Button) dialog.findViewById(R.id.positive_button);
        Button negativeBtn = (Button) dialog.findViewById(R.id.negative_button);

        if (messageText != null) {
            messageText.setText(R.string.dialog_complete_relocation_message);
        }
        if (positiveBtn != null) {
            positiveBtn.setText(R.string.dialog_complete_relocation_positive);
            positiveBtn.setOnClickListener(v -> {
                mPresenter.finishAssignment(mRelocationNumber);
                dialog.cancel();
            });
        }
        if (negativeBtn != null) {
            negativeBtn.setText(R.string.dialog_complete_relocation_negative);
            negativeBtn.setOnClickListener(view -> dialog.cancel());
        }
    }

    //endregion

    //region SendPrint

    @Override
    public void sendPrint() {
        mPresenter.onSendPrintClick();
    }

    @Override
    public void showConfirmSendPrintDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog dialog = builder
                .setTitle(R.string.dialog_send_print_title)
                .setView(R.layout.dialog_confirm)
                .setCancelable(false)
                .show();
        TextView messageText = (TextView) dialog.findViewById(R.id.message_text);
        Button positiveBtn = (Button) dialog.findViewById(R.id.positive_button);
        Button negativeBtn = (Button) dialog.findViewById(R.id.negative_button);

        if (messageText != null) {
            messageText.setText(R.string.dialog_send_print_message);
        }
        if (positiveBtn != null) {
            positiveBtn.setText(R.string.dialog_send_print_positive);
            positiveBtn.setOnClickListener(v -> {
                mPresenter.sendPrint(mRelocationNumber);
                dialog.cancel();
            });
        }
        if (negativeBtn != null) {
            negativeBtn.setText(R.string.dialog_send_print_negative);
            negativeBtn.setOnClickListener(view -> dialog.cancel());
        }
    }

    //endregion

    //region SendMessage

    @Override
    public void sendMessage() {
        mPresenter.onSendMessageClick();
    }

    @Override
    public void showMessageTypesDialog(List<DriverMessageDto> messages) {
        ArrayAdapter<DriverMessageDto> adapter = new ArrayAdapter<>(mContext,
                android.R.layout.select_dialog_singlechoice, messages);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.title_activity_driver_message))
                .setAdapter(adapter, (dialog, index) -> mPresenter.selectType(messages.get(index)))
                .setCancelable(true);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void showCommentDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_input, null, false);
        EditText input = (EditText) view.findViewById(R.id.input_edit_text);
        input.setHint(R.string.dialog_send_comment_hint);
        input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(R.string.dialog_send_comment_title)
                .setView(view)
                .setPositiveButton(R.string.dialog_send_comment_apply,
                        (dialog, index) -> mPresenter.sendMessage(input.getText().toString()));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //endregion

    //region OpenGate

    @Override
    public void openGate() {
        mPresenter.onOpenGateClick();
    }

    @Override
    public void showOpenGateDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_input, null, false);
        EditText input = (EditText) view.findViewById(R.id.input_edit_text);
        input.setHint(R.string.dialog_open_gate_hint);
        input.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(R.string.dialog_open_gate_title)
                .setView(view)
                .setPositiveButton(R.string.dialog_open_gate_apply,
                        (dialog, which) -> mPresenter.openGate(input.getText().toString()));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //endregion

    @Override
    public void createIncidentPhoto() {

    }

    @Override
    public void openPersonalCabinet() {

    }


    @Override
    public void showDocuments() {

    }

    @Override
    public void showAddressesMap() {

    }

    @Override
    public void showLocationMap() {

    }

    @Override
    public void update() {

    }

    @Override
    public void close() {
        //TODO: ipopov 20.02.2017 избавиться от этого костыля
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportFragmentManager()
                .beginTransaction()
                .remove(this)
                .commit();
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            activity.setTitle("Перемещения");
        }
    }

    //endregion

}
