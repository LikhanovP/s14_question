package com.rosa.swift.core.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.DocumentsUtils;
import com.rosa.swift.core.business.utils.ImageUtils;
import com.rosa.swift.core.business.utils.Log;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.core.ui.activities.LogonlessActivity;
import com.rosa.swift.mvp.ratings.photosessions.repositories.PhotoSessionTaskDto;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by inurlikaev
 */
public class CupRequestFragment extends Fragment {

    private static final int CUP_VIEW_PHOTO_SIZE_DP = 160;

    PhotoSessionTaskDto cupRequestItem;

    private TextView cupViewTitle;
    private TextView cupViewIndex;
    private TextView cupViewTotal;
    private ImageView cupViewPhoto;
    private ImageView cupPhoto;
    private ImageView picturePhoto;
    private ProgressBar photoSpinner;
    private TextView cupViewPhotoCaption;

    private LogonlessActivity baseActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View mainView = inflater.inflate(R.layout.fragment_cup_session, container, false);

        cupViewTitle = (TextView) mainView.findViewById(R.id.cup_view_title);
        cupViewIndex = (TextView) mainView.findViewById(R.id.cup_view_index);
        cupViewTotal = (TextView) mainView.findViewById(R.id.cup_view_total);
        cupViewPhoto = (ImageView) mainView.findViewById(R.id.cup_view_photo);
        picturePhoto = (ImageView) mainView.findViewById(R.id.cup_photo_camera_image);
        photoSpinner = (ProgressBar) mainView.findViewById(R.id.cup_view_photo_progress);
        cupViewPhotoCaption = (TextView) mainView.findViewById(R.id.cup_view_photo_caption);
        cupPhoto = (ImageView) mainView.findViewById(R.id.cup_photo);
        picturePhoto.setVisibility(View.VISIBLE);
        refreshView();

        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LogonlessActivity) {
            baseActivity = (LogonlessActivity) context;
        }
    }

    public void setCupRequestItem(PhotoSessionTaskDto crItem) {
        cupRequestItem = crItem;
        refreshView();
    }

    private static void setImageViewSource(ImageView iView, Bitmap bitmap, ImageView picturePhoto) {
        if (bitmap != null) {
            picturePhoto.setVisibility(View.INVISIBLE);
            iView.setImageBitmap(bitmap);
        } else {
            iView.setImageDrawable(null);
        }
    }

    public void refreshView() {
        if (cupRequestItem != null && cupViewTitle != null && cupViewPhoto != null && cupPhoto != null) {
            cupViewTitle.setText(cupRequestItem.getCupViewTitle());
            cupViewIndex.setText(Integer.toString(cupRequestItem.getIndex()));
            cupViewTotal.setText(DataRepository.getInstance().getPhotoDocumentTasks().size());

            refreshCupViewPhoto();
            refreshCupPhoto();
        }
    }

    private void refreshCupViewPhoto() {
        cupViewPhoto.setImageDrawable(null);
        photoSpinner.setVisibility(View.VISIBLE);
        cupViewPhotoCaption.setVisibility(View.GONE);
        new GetCupViewPhotoAsyncTask(cupViewPhoto, photoSpinner, cupViewPhotoCaption).execute(cupRequestItem.getCupViewId());
    }

    private void refreshCupPhoto() {

        Display display = baseActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y - Math.round(CUP_VIEW_PHOTO_SIZE_DP * getResources().getDisplayMetrics().density);
        Bitmap bitmap = ImageUtils.getSampledBitmap(cupRequestItem.getPhotoPath(), width, height);
        setImageViewSource(cupPhoto, bitmap, picturePhoto);
    }

    private class GetCupViewPhotoAsyncTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewWeakReference;
        private final WeakReference<ProgressBar> progressBarWeakReference;
        private final WeakReference<TextView> textViewWeakReference;

        public GetCupViewPhotoAsyncTask(ImageView view) {
            this(view, null);
        }

        public GetCupViewPhotoAsyncTask(ImageView view, ProgressBar spinner) {
            this(view, spinner, null);
        }

        public GetCupViewPhotoAsyncTask(ImageView view, ProgressBar spinner, TextView textView) {
            imageViewWeakReference = new WeakReference<ImageView>(view);
            progressBarWeakReference = new WeakReference<ProgressBar>(spinner);
            textViewWeakReference = new WeakReference<TextView>(textView);
        }

        private void progressBarStop() {
            if (progressBarWeakReference != null) {
                ProgressBar spinner = progressBarWeakReference.get();
                if (spinner != null) {
                    spinner.setVisibility(View.GONE);
                }
            }
        }

        private void showTextView() {
            if (textViewWeakReference != null) {
                TextView textView = textViewWeakReference.get();
                if (textView != null) {
                    textView.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String cupViewId = params[0];
            return downloadCupViewPhoto(cupViewId);
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
                    showTextView();
                }
            }
        }
    }

    private Bitmap downloadCupViewPhoto(String cupViewId) {
        try {
            File photoFile = DocumentsUtils.getCupViewPhotoFile(cupViewId, DataRepository.getInstance().getPhotoSessionTasks());
            if (photoFile == null) return null;

            String path = photoFile.getPath();

            float density = getResources().getDisplayMetrics().density;
            int width = Math.round(CUP_VIEW_PHOTO_SIZE_DP * density);
            int height = width;

            Bitmap roundedBitmap = null;
            Bitmap scaledBitmap = ImageUtils.getSampledBitmap(path, width, height, true);
            if (scaledBitmap != null) {
                roundedBitmap = ImageUtils.getRoundedCornerBitmap(scaledBitmap, 10 * getResources().getDisplayMetrics().density);
                scaledBitmap.recycle();
            }

            return roundedBitmap;
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
        return null;
    }

}
