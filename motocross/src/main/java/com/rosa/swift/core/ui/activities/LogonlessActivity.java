package com.rosa.swift.core.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.CommonUtils;
import com.rosa.swift.core.network.servers.ServerCollection;


/**
 * Created by aboyarkin on 16.08.13.
 */
@SuppressWarnings("unused")
public class LogonlessActivity extends MvpAppCompatActivity {

    protected ProgressDialog mPrDlg;
    protected ProgressDialog mSimpleProgress;

    protected ServerCollection serverManager;

    /*Режим отображения всех сообщений*/
    protected boolean god_mode;
    protected int god_mod_cnt;

    @Override
    protected void onStop() {
        super.onStop();
        if (mPrDlg != null && mPrDlg.isShowing()) {
            mPrDlg.dismiss();
        }
        if (mSimpleProgress != null && mSimpleProgress.isShowing()) {
            mSimpleProgress.dismiss();
        }
    }

    public void showProgress() {
        showProgress(R.string.action_waiting, null);
    }

    public void showProgress(int resId) {
        showProgress(getString(resId), null);
    }

    public void showProgress(String message) {
        showProgress(message, null);
    }

    protected void showProgress(int resId, DialogInterface.OnCancelListener onCancelListener) {
        showProgress(getString(resId), onCancelListener);
    }

    protected void showProgress(String message, DialogInterface.OnCancelListener onCancelListener) {
        // Странно работает тема. Диалог выводится не по центру, а в верхней части экрана
        mPrDlg = new ProgressDialog(this, R.style.SdvorTheme_ProgressDialog); //, AlertDialog.THEME_HOLO_LIGHT);
        mPrDlg.setIndeterminate(true);
        //mPrDlg.setIndeterminateDrawable(getResources().getDrawable(R.drawable.sdvor_progress_anim)); //deprecated
        mPrDlg.setIndeterminateDrawable(ContextCompat.getDrawable(
                this, R.drawable.sdvor_progress_anim));

        if (onCancelListener != null) {
            mPrDlg.setCancelable(true);
            mPrDlg.setOnCancelListener(onCancelListener);
        } else
            mPrDlg.setCancelable(false);
        mPrDlg.setMessage(message);
        mPrDlg.show();
    }

    public void hideProgress() {
        if (mPrDlg != null) {
            try {
                mPrDlg.dismiss();
            } catch (IllegalArgumentException e) {
                // Не слишком правильный, но рабочий способ не валиться, если hideProgress
                // был вызван после удаления активности
            }
        }
    }

    public void showSimpleProgress() {
        if (mSimpleProgress == null) {
            mSimpleProgress = new ProgressDialog(this, R.style.ProgressDialog);
            mSimpleProgress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mSimpleProgress.setCancelable(false);
        }

        mSimpleProgress.show();
        mSimpleProgress.setContentView(R.layout.layout_progress_splash);
    }

    public void hideSimpleProgress() {
        if (mSimpleProgress != null) {
            mSimpleProgress.hide();
            mSimpleProgress = null;
        }
    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    protected void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public boolean GetGodMode() {
        return god_mode;
    }

    public void showInfoMessage(int stringId) {
        showInfoMessage(getString(stringId));
    }

    public void showInfoMessage(String message) {
        CommonUtils.ShowInfoMessage(this, message);
    }

    public void showErrorMessage(int stringId) {
        showErrorMessage(getString(stringId));
    }

    public void showErrorMessage(String message) {
        CommonUtils.ShowErrorMessage(this, message);
    }

}
