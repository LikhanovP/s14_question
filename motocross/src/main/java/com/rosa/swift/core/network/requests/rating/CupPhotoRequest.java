package com.rosa.swift.core.network.requests.rating;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.mvp.ratings.photosessions.repositories.PhotoSessionTaskDto;

import java.io.File;

public class CupPhotoRequest {

    @SerializedName("SESSION_ID")
    private String mSessionId;

    @SerializedName("CUP_SESSION_ID")
    private String mCupSessionId;

    @SerializedName("CUP_VIEW_ID")
    private String mCupViewId;

    @SerializedName("PHOTO_FILENAME")
    private String mFileName;

    @SerializedName("PHOTO")
    private String mPhoto;

    public CupPhotoRequest(PhotoSessionTaskDto sessionTask, String convertedImage) {
        if (sessionTask != null) {
            String photoPath = sessionTask.getPhotoPath();
            mSessionId = DataRepository.getInstance().getSessionId();
            mCupSessionId = sessionTask.getCupSessionId();
            mCupViewId = sessionTask.getCupViewId();
            mFileName = new File(photoPath).getName();
            mPhoto = convertedImage;
        }
    }

}