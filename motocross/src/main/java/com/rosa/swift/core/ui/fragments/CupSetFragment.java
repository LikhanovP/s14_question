package com.rosa.swift.core.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.LruCache;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.CommonUtils;
import com.rosa.swift.core.business.utils.DocumentsUtils;
import com.rosa.swift.core.business.utils.ImageUtils;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.business.utils.SapRequestUtils;
import com.rosa.swift.core.business.utils.StringUtils;
import com.rosa.swift.core.network.json.sap.cup.JCupSetInfoOut;
import com.rosa.swift.core.network.json.sap.cup.JCupSetOut;
import com.rosa.swift.core.network.services.sap.ServiceCallback;
import com.rosa.swift.core.network.services.sap.WSException;
import com.rosa.swift.core.ui.activities.LogonlessActivity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Locale;

public class CupSetFragment extends Fragment implements CabinetFragment {

    private JCupSetOut cupSet;
    private JCupSetInfoOut.JCupSetInfo cupSetInfo;
    private Hashtable photoFiles;

    private ListView cupSetListView;

    private LogonlessActivity baseActivity;
    private LruCache<String, Bitmap> mMemoryCache;

    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public CupSetFragment() {
        setHasOptionsMenu(true);
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory());
        final int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
    }

    public static CupSetFragment getInstance(JCupSetInfoOut.JCupSetInfo cupSetInfo) {
        CupSetFragment fragment = new CupSetFragment();
        fragment.setCupSetInfo(cupSetInfo);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cup_set_list_layout, container, false);

        cupSetListView = (ListView) view.findViewById(R.id.cup_set_list_view);

        if (savedInstanceState != null) {
            cupSet = (JCupSetOut) savedInstanceState.getSerializable("cupSet");
            photoFiles = (Hashtable) savedInstanceState.getSerializable("photoFiles");
            updateView();
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("cupSet", cupSet);
        outState.putSerializable("photoFiles", photoFiles);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LogonlessActivity) {
            baseActivity = (LogonlessActivity) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setCupSetInfo(JCupSetInfoOut.JCupSetInfo cupSetInfo) {
        this.cupSetInfo = cupSetInfo;
        doGetCupSet(cupSetInfo.cup_id);
    }

    private void updateView() {
        if (baseActivity != null) {
            if (cupSet != null) {
                ArrayAdapter<JCupSetOut.JCupSet> cupSetArrayAdapter = new ArrayAdapter<JCupSetOut.JCupSet>(baseActivity, R.layout.cup_set_item_layout, cupSet.cup_set) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        JCupSetOut.JCupSet cupSetItem = cupSet.cup_set.get(position);

                        View rowView = convertView;
                        if (rowView == null) {
                            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                            rowView = inflater.inflate(R.layout.cup_set_item_layout, parent, false);
                        }

                        TextView cupViewTitleTextView = (TextView) rowView.findViewById(R.id.cup_view_title);
                        ImageView cupPhotoImageView = (ImageView) rowView.findViewById(R.id.cup_photo);
                        TextView cupNoteTextView = (TextView) rowView.findViewById(R.id.cup_note);
                        TextView cupItemMarkTextView = (TextView) rowView.findViewById(R.id.cup_item_mark);
                        ProgressBar photoSpinner = (ProgressBar) rowView.findViewById(R.id.cup_photo_progress);

                        //заполняем данные
                        String cupViewTitle = "";
                        String cupNote = "";
                        Bitmap cupPhotoBitmap = null;
                        float cupMark = 0;
                        if (cupSetItem != null) {
                            cupViewTitle = cupSetItem.cup_view_title;
                            if (!StringUtils.isNullOrEmpty(cupSetItem.cup_note)) {
                                cupNote = cupSetItem.cup_note;
                            }
                            try {
                                cupMark = Float.parseFloat(cupSetItem.cup_mark);
                            } catch (Exception e) {
                                Log.e(e.getMessage());
                            }
                            cupPhotoBitmap = getBitmapFromMemCache(cupSetItem.photo_filename);
                        }

                        cupViewTitleTextView.setText(cupViewTitle);
                        if (!StringUtils.isNullOrEmpty(cupNote))
                            cupNoteTextView.setText(cupNote);
                        else cupNoteTextView.setVisibility(View.GONE);
                        cupItemMarkTextView.setText(String.format(
                                Locale.getDefault(), "%.0f", cupMark));

                        if (cupPhotoBitmap != null) {
                            photoSpinner.setVisibility(View.GONE);
                            cupPhotoImageView.setImageBitmap(cupPhotoBitmap);
                        } else {
                            cupPhotoImageView.setImageDrawable(null);
                            photoSpinner.setVisibility(View.VISIBLE);
                            new GetCupPhotoAsyncTask(cupPhotoImageView, photoSpinner).execute(cupSetItem);
                        }

                        return rowView;
                    }

                };

                cupSetListView.setAdapter(cupSetArrayAdapter);
            }
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

    public void doGetCupSet(String cupId) {
        photoFiles = new Hashtable();
        showProgress(R.string.get_data_progress);
        SapRequestUtils.getCupSet(cupId, new ServiceCallback() {
            @Override
            public void onFinished(String evParam) {
                try {
                    if (!StringUtils.isNullOrEmpty(evParam)) {
                        cupSet = null;
                        try {
                            Gson g = new Gson();
                            cupSet = g.fromJson(evParam, JCupSetOut.class);
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

    private class GetCupPhotoAsyncTask extends AsyncTask<JCupSetOut.JCupSet, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewWeakReference;
        private final WeakReference<ProgressBar> progressBarWeakReference;

        public GetCupPhotoAsyncTask(ImageView view) {
            this(view, null);
        }

        public GetCupPhotoAsyncTask(ImageView view, ProgressBar spinner) {
            imageViewWeakReference = new WeakReference<ImageView>(view);
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
        protected Bitmap doInBackground(JCupSetOut.JCupSet... cupSets) {
            JCupSetOut.JCupSet cupSet = cupSets[0];
            return downloadCupPhoto(cupSet);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewWeakReference != null) {
                ImageView imageView = imageViewWeakReference.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        imageView.setImageDrawable(null);
                    }
                    progressBarStop();
                }
            }
        }
    }

    private Bitmap downloadCupPhoto(JCupSetOut.JCupSet cupSet) {
        try {
            File photoFile = DocumentsUtils.getCupPhotoFile(cupSet);
            if (photoFile == null) return null;

            String path = photoFile.getPath();

            Display display = baseActivity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.x;

            Bitmap scaledBitmap = ImageUtils.getSampledBitmap(path, width, height, true);
            Bitmap roundedBitmap = ImageUtils.getRoundedCornerBitmap(scaledBitmap, 10 * getResources().getDisplayMetrics().density);
            scaledBitmap.recycle();
            CupSetFragment.this.addBitmapToMemoryCache(cupSet.photo_filename, roundedBitmap);

            return roundedBitmap;
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
        return null;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        if (!StringUtils.isNullOrEmpty(key))
            return mMemoryCache.get(key);
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void refreshData() {

    }

    @Override
    public String getTitle() {
        float floatMark = 0;

        try {
            floatMark = Float.parseFloat(cupSetInfo.cup_mark);
        } catch (Exception e) {
            Log.e(e.getMessage());
        }

        return dateFormat.format(StringUtils.SAPDateToDate(cupSetInfo.cup_audat)) + " / Оценка: " + String.format("%.3f", floatMark);
    }

    @Override
    public String getPCabTag() {
        return cupSetInfo != null ? cupSetInfo.cup_id : "";
    }

    @Override
    public String getBackStackName() {
        return null;
    }

    @Override
    public boolean getDrawerEnabled() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                baseActivity.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
