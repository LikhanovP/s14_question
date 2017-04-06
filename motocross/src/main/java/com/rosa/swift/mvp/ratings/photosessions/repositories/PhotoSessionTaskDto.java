package com.rosa.swift.mvp.ratings.photosessions.repositories;

import com.rosa.swift.core.business.utils.DocumentsUtils;
import com.rosa.swift.core.network.json.sap.common.DriverSessionResponse.PhotoSessionTask;

import java.io.File;
import java.io.Serializable;
import java.util.UUID;

public class PhotoSessionTaskDto implements Serializable {
    public static final int EMPTY_TASK = -1;

    private String mCupSessionId;

    private String mCupViewId;

    private String mCupViewTitle;

    private String mCupViewPhotoPath;

    private String mPhotoPath;

    private String mPhotoDocumentId;

    private int mIndex;

    public PhotoSessionTaskDto(PhotoSessionTask task, int index) {
        mCupSessionId = UUID.randomUUID().toString();
        mCupViewId = task.getViewId();
        mCupViewTitle = task.getViewTitle();
        mCupViewPhotoPath = DocumentsUtils.getCupViewPhotosDir() + File.separator +
                DocumentsUtils.getCupViewPhotoFilename(task.getViewId(), task.getPhotoDocumentId());
        mPhotoDocumentId = task.getPhotoDocumentId();
        mIndex = index;
    }

    public void setPhotoPath(String photoPath) {
        mPhotoPath = photoPath;
    }

    public int getIndex() {
        return mIndex;
    }

    public String getCupSessionId() {
        return mCupSessionId;
    }

    public String getCupViewId() {
        return mCupViewId;
    }

    public String getCupViewTitle() {
        return mCupViewTitle;
    }

    public String getCupViewPhotoPath() {
        return mCupViewPhotoPath;
    }

    public String getPhotoPath() {
        return mPhotoPath;
    }

    public String getPhotoDocumentId() {
        return mPhotoDocumentId;
    }
}
