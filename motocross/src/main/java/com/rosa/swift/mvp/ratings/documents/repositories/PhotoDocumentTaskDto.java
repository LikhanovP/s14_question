package com.rosa.swift.mvp.ratings.documents.repositories;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.rosa.swift.core.business.utils.Constants;
import com.rosa.swift.core.business.utils.DocumentsUtils;
import com.rosa.swift.core.business.utils.StringUtils;
import com.rosa.swift.core.network.json.sap.common.DriverSessionResponse;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class PhotoDocumentTaskDto {
    public static final int EMPTY_TASK = -1;

    private String mSessionId;

    private String mDocumentType;

    private String mPhotoPath;

    private String mDatePhoto;

    private String mActDatePhoto;

    private boolean mActive;

    private boolean mCompleted;

    private boolean mCanceled;

    public PhotoDocumentTaskDto(DriverSessionResponse.PhotoDocumentTask documentTask) {
        if (documentTask != null) {
            mSessionId = UUID.randomUUID().toString();
            mDocumentType = documentTask.getPhotoDocumentType();
            mDatePhoto = documentTask.getPhotoDate();
            mActDatePhoto = documentTask.getPhotoActualDate();
            mActive = documentTask.getSendTask() != null && documentTask.getSendTask().equals(Constants.SAP_TRUE_FLAG);
            if (!TextUtils.isEmpty(documentTask.getPhotoDocumentType())) {
                mPhotoPath = DocumentsUtils.getPhotoDocumentsDirectory() + File.separator +
                        documentTask.getPhotoDocumentType();
            }
        }
    }

    //region Getters and setters

    public String getSessionId() {
        return mSessionId;
    }

    public String getDocumentType() {
        return mDocumentType;
    }

    public String getPhotoPath() {
        return mPhotoPath;
    }

    @Nullable
    public Date getDatePhoto() {
        return StringUtils.SAPDateToDate(mDatePhoto);
    }

    public void setDatePhoto(String datePhoto) {
        mDatePhoto = datePhoto;
    }

    @Nullable
    public Date getActDatePhoto() {
        return StringUtils.SAPDateToDate(mActDatePhoto);
    }

    public boolean isUnhandled() {
        return mActive && (mCanceled || mCompleted);
    }

    //endregion

    public void markAsCompleted() {
        mCompleted = true;
    }

    public void markAsCanceled() {
        mCanceled = true;
    }

    //region Comparing

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        PhotoDocumentTaskDto that = (PhotoDocumentTaskDto) object;
        return mDocumentType.equals(that.mDocumentType);
    }

    @Override
    public int hashCode() {
        return mDocumentType.hashCode();
    }

    //endregion

}
