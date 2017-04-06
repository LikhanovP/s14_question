package com.rosa.swift.core.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.rosa.motocross.R;


public class DialogActivity extends AppCompatActivity {

    private static final String DIALOG_TITLE = "DIALOG_TITLE";
    private static final String DIALOG_MESSAGE = "DIALOG_MESSAGE";
    private static final String DIALOG_POSITIVE = "DIALOG_POSITIVE";
    private static final String DIALOG_POSITIVE_INTENT = "DIALOG_HANDLER";

    public static Intent newIntent(Context context, String title, String message) {
        return newIntent(context, title, message, null, null);
    }

    public static Intent newIntent(Context context, String title, String message,
                                   String positiveBtn, Intent positiveIntent) {
        Intent intent = new Intent(context, DialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DIALOG_TITLE, title);
        intent.putExtra(DIALOG_MESSAGE, message);
        intent.putExtra(DIALOG_POSITIVE, positiveBtn);
        intent.putExtra(DIALOG_POSITIVE_INTENT, positiveIntent);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String title = "";
        String message = "";
        String positive = getString(android.R.string.ok);
        final Intent positiveIntent = intent.getParcelableExtra(DIALOG_POSITIVE_INTENT);

        if (intent.hasExtra(DIALOG_TITLE) && intent.hasExtra(DIALOG_MESSAGE)) {
            title = intent.getStringExtra(DIALOG_TITLE);
            message = intent.getStringExtra(DIALOG_MESSAGE);
            if (intent.hasExtra(DIALOG_POSITIVE)) {
                positive = intent.getStringExtra(DIALOG_POSITIVE);
            }
        }

        if (!TextUtils.isEmpty(message)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setIcon(R.drawable.ic_launcher)
                    .setMessage(message)
                    .setPositiveButton(positive,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (positiveIntent != null) {
                                        startActivity(positiveIntent);
                                    }
                                    finish();
                                }
                            })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface arg0) {
                            finish();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}
