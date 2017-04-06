package com.rosa.swift.core.business.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.rosa.motocross.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommonUtils {

    private static void ShowMessage(Context context, String title, String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            Log.e(e);
        }
    }

    public static void ShowErrorMessage(Context context, String s) {
        ShowMessage(context, "Ошибка!", s);
    }

    public static void ShowErrorMessage(Context context, int strId) {
        ShowErrorMessage(context, context.getString(strId));
    }

    public static void ShowInfoMessage(Context context, int strId) {
        ShowMessage(context, "Информация!", context.getString(strId));
    }

    public static void ShowInfoMessage(Context context, String s) {
        ShowMessage(context, "Информация!", s);
    }

    public static void ShowInfoMessageWithButtons(Context context, int titleId, String message,
                                                  int yesBtn, int noBtn, final DialogHandler handler) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(titleId)
                .setMessage(message)
                .setPositiveButton(yesBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        handler.YesClick();
                    }
                })
                .setNegativeButton(noBtn, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        handler.NoClick();
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void confirm(Context context, int titleId, int msgId,
                               final DialogHandler handler) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(titleId)
                .setMessage(msgId)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        handler.YesClick();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        handler.NoClick();
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showItemsDialog(Context context, int titleId, ListAdapter adapter,
                                       final DialogInterface.OnClickListener listener) {
        if (adapter != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle(context.getString(titleId))
                    .setAdapter(adapter, listener)
                    .setCancelable(true);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public static void showToast(Context context, int stringId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.toast_layout, null, false);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(context.getString(stringId));

        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    public static String getLogs() {
        StringBuilder log = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec("logcat -v time -s -t 200 -d Motocross:*");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line + "\n");
            }
        } catch (IOException e) {
            log.append("Log reading error.");
        }
        return log.toString();
    }
}
