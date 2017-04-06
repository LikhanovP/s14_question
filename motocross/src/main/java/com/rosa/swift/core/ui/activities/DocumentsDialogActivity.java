package com.rosa.swift.core.ui.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.CommonUtils;
import com.rosa.swift.core.business.utils.DocumentsUtils;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.business.utils.SapRequestUtils;
import com.rosa.swift.core.business.utils.StringUtils;
import com.rosa.swift.core.business.utils.SupportUtils;
import com.rosa.swift.core.network.json.sap.documents.JDriverDocumentType;
import com.rosa.swift.core.network.json.sap.documents.JDriverDocuments;
import com.rosa.swift.core.network.services.sap.ServiceCallback;
import com.rosa.swift.core.network.services.sap.WSException;
import com.rosa.swift.core.ui.adapters.DocumentsListAdapter;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by inurlikaev on 30.05.2016.
 */
public class DocumentsDialogActivity extends Activity {
    private String tknum;
    private JDriverDocuments driverDocuments;

    private ListView documentsListView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlv_documents_list_layout);

        documentsListView = (ListView) findViewById(R.id.dlv_documents_list);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.dlv_documents_list_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        doGetDocumentsList();
                    }
                }
        );

        if (savedInstanceState != null) {
            driverDocuments = (JDriverDocuments) savedInstanceState.getSerializable("driverDocuments");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent i = this.getIntent();
        tknum = i.getStringExtra("tknum");

        doGetDocumentsList();
    }

    private void doGetDocumentsList() {
        SapRequestUtils.getAllDocsList(tknum, new ServiceCallback() {
            @Override
            public void onFinished(String evParam) {
                try {
                    Gson g = new Gson();
                    driverDocuments = g.fromJson(evParam, JDriverDocuments.class);
                    swipeRefreshSetRefreshing(false);
                    updateView();
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

    private void updateView() {
        if (driverDocuments != null && driverDocuments.documentsList != null) {
            DocumentsListAdapter documentsListAdapter = new DocumentsListAdapter(this, R.layout.dlv_documents_item_layout, driverDocuments.documentsList);
            documentsListAdapter.setOnItemClickListener(new DocumentsListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, JDriverDocuments.JDriverDocument driverDocument, int position) {
                    DocumentsDialogActivity.this.onItemClick(v, driverDocument, true);
                }
            });
            documentsListView.setAdapter(documentsListAdapter);
        }
    }


    public void swipeRefreshSetRefreshing(boolean refreshing) {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(refreshing);
    }

    private void onItemClick(View view, JDriverDocuments.JDriverDocument driverDocument, boolean withConfirm) {
        final JDriverDocuments.JDriverDocument driverDoc = driverDocument;
        final JDriverDocumentType documentType = JDriverDocumentType.getType(driverDocument.fileType);
        final View rowView = view;
        File documentFile = DocumentsUtils.getSavedDocumentFile(driverDoc);
        if (documentFile != null) {
            if (withConfirm) {

                int fileTypeIconId = JDriverDocumentType.getTypeIconId(documentType);
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setIcon(fileTypeIconId)
                        .setTitle(R.string.open_confirm)
                        .setPositiveButton(R.string.open_local, (dialog, which) -> onItemClick(rowView, driverDoc, false))
                        .setNegativeButton(R.string.open_download, (dialog, which) -> downloadDocumentFile(rowView, driverDoc));

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return;
            } else {
                openDocumentFile(documentFile, documentType);
                return;
            }
        }
        downloadDocumentFile(rowView, driverDoc);
    }

    private void downloadDocumentFile(View view, JDriverDocuments.JDriverDocument driverDocument) {
        ProgressBar spinner = (ProgressBar) view.findViewById(R.id.dlv_doc_item_progress);
        spinner.setVisibility(View.VISIBLE);
        new DownloadDocumentAsyncTask(spinner).execute(driverDocument);
    }

    private void openDocumentFile(File file, JDriverDocumentType type) {
        try {
            startActivity(SupportUtils.getIntentView(file, type));
        } catch (ActivityNotFoundException e) {
            CommonUtils.ShowErrorMessage(this,
                    "Не установлено приложение для просмотра документов такого типа!");
        } catch (Exception exception) {
            Log.e("Error file opening", exception);
            CommonUtils.ShowErrorMessage(this, "Не удалось открыть документ");
        }
    }

    private class DownloadDocumentAsyncTask extends AsyncTask<JDriverDocuments.JDriverDocument, Void, File> {
        private JDriverDocumentType mDocumentType;
        private final WeakReference<ProgressBar> progressBarWeakReference;
        private String exceptionMsg;

        public DownloadDocumentAsyncTask(ProgressBar spinner) {
            progressBarWeakReference = new WeakReference<ProgressBar>(spinner);
        }

        private void progressBarStop() {
            if (progressBarWeakReference != null) {
                ProgressBar spinner = progressBarWeakReference.get();
                if (spinner != null) {
                    spinner.setVisibility(View.GONE);
                }
            }
        }

        @Override
        protected File doInBackground(JDriverDocuments.JDriverDocument... params) {
            JDriverDocuments.JDriverDocument driverDocument = params[0];
            mDocumentType = JDriverDocumentType.getType(driverDocument.fileType);
            try {
                return DocumentsUtils.downloadDocumentFile(driverDocument);
            } catch (Exception ex) {
                exceptionMsg = ex.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(File file) {
            progressBarStop();
            if (isCancelled()) {
                file = null;
            }

            if (file != null) {
                openDocumentFile(file, mDocumentType);
            }

            if (!StringUtils.isNullOrEmpty(exceptionMsg)) {
                Toast.makeText(DocumentsDialogActivity.this, exceptionMsg, Toast.LENGTH_LONG).show();
            }
        }
    }
}
