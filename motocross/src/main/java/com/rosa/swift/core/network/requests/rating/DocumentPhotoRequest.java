package com.rosa.swift.core.network.requests.rating;

import com.google.gson.annotations.SerializedName;
import com.rosa.swift.core.data.DataRepository;
import com.rosa.swift.mvp.ratings.documents.repositories.PhotoDocumentTaskDto;

import java.io.File;

public class DocumentPhotoRequest {

    @SerializedName("SESSION_ID")
    private String mSessionId;

    @SerializedName("PHOTO_DOC_VIEW")
    private String mDocumentName;

    @SerializedName("PHOTO_FILENAME")
    private String mFileName;

    @SerializedName("PHOTO")
    private String mPhoto;

    public DocumentPhotoRequest(PhotoDocumentTaskDto documentTask, String convertedImage) {
        if (documentTask != null) {
            String photoPath = documentTask.getPhotoPath();
            mSessionId = DataRepository.getInstance().getSessionId();
            mDocumentName = documentTask.getDocumentType();
            mFileName = new File(photoPath).getName();
            mPhoto = convertedImage;
        }
    }

}
