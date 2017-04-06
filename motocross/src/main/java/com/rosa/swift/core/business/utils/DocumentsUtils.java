package com.rosa.swift.core.business.utils;

import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.rosa.swift.SwiftApplication;
import com.rosa.swift.core.network.json.sap.cup.JCupGetPhotoIn;
import com.rosa.swift.core.network.json.sap.cup.JCupGetPhotoOut;
import com.rosa.swift.core.network.json.sap.cup.JCupSetOut;
import com.rosa.swift.core.network.json.sap.cup.JCupViewPhoto;
import com.rosa.swift.core.network.json.sap.documents.JDocumentFile;
import com.rosa.swift.core.network.json.sap.documents.JDriverDocumentKey;
import com.rosa.swift.core.network.json.sap.documents.JDriverDocuments;
import com.rosa.swift.core.network.json.sap.photoBase.PhotoDocumentRequest;
import com.rosa.swift.core.network.json.sap.photoBase.PhotoDocumentResponse;
import com.rosa.swift.core.network.services.sap.ServiceCallback;
import com.rosa.swift.core.network.services.sap.ServiceFunction;
import com.rosa.swift.core.network.services.sap.WSException;
import com.rosa.swift.core.network.services.sap.ZMotoService;
import com.rosa.swift.mvp.ratings.documents.repositories.PhotoDocumentTaskDto;
import com.rosa.swift.mvp.ratings.photosessions.repositories.PhotoSessionTaskDto;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class DocumentsUtils {

    private static final String MOTOCROSS_PICTURES_DIR = "Motocross/";
    private static final String CUP_VIEW_PHOTOS_DIR = "CupViews/";
    private static final String CUP_PHOTOS_DIR = "CupPhotos/";
    private static final String PHOTO_DOCUMENTS_DIR = "PhotoDocuments/";
    private static final String SCHEMES_DIR = "schemes";
    private static final String DOCUMENTS_DIR = "documents";


    //region PhotoSessionFiles

    public static File getCupPhotosDir() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                MOTOCROSS_PICTURES_DIR + CUP_PHOTOS_DIR
        );

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) return null;
        }

        return mediaStorageDir;
    }

    //использовать только асинхронно
    public static File getCupPhotoFile(JCupSetOut.JCupSet cupSet) {
        if (StringUtils.isNullOrEmpty(cupSet.photo_filename)) return null;

        File mediaStorageDir = getCupPhotosDir();
        if (mediaStorageDir == null) return null;

        File photoFile = new File(mediaStorageDir.getPath() + File.separator + cupSet.photo_filename);
        if (!photoFile.exists()) {
            Gson g = new Gson();
            JCupGetPhotoIn cupGetPhotoIn = new JCupGetPhotoIn();
            cupGetPhotoIn.cup_id = cupSet.cup_id;
            cupGetPhotoIn.photo_id = cupSet.photo_id;
            String jInp = g.toJson(cupGetPhotoIn, JCupGetPhotoIn.class);
            String jres = null;
            try {
                jres = ZMotoService.getInstance().callService(ServiceFunction.GetCupPhoto, jInp);
            } catch (WSException e) {
                Log.e(e.getMessage());
            }

            if (StringUtils.isNullOrEmpty(jres))
                return null;

            JCupGetPhotoOut cupGetPhotoOut = g.fromJson(jres, JCupGetPhotoOut.class);
            DocumentsUtils.saveBase64File(photoFile, cupGetPhotoOut.photo);
        }

        return photoFile;
    }

    public static File getCupViewPhotoFile(String cupViewId, List<PhotoSessionTaskDto> taskList) {
        if (StringUtils.isNullOrEmpty(cupViewId)) return null;

        File cvFile = getCupViewPhotoFileLocal(cupViewId);

        PhotoSessionTaskDto cvInfo = null;
        //List<CupTask>
        for (PhotoSessionTaskDto cvi : taskList) {
            if (cvi.getCupViewId().equals(cupViewId)) {
                cvInfo = cvi;
                break;
            }
        }

        if (cvInfo != null) {
            String cupViewPhotoFilename = getCupViewPhotoFilename(
                    cvInfo.getCupViewId(), cvInfo.getPhotoDocumentId());

            //проверяем совпадает ли фото в полученном ракурсе с сохраенным фото
            if (cvFile != null) {
                if (!cupViewPhotoFilename.equals(cvFile.getName())) {
                    cvFile.delete();
                    cvFile = null;
                }
            }
        }

        if (cvFile == null) {
            String jres = null;
            try {
                jres = ZMotoService.getInstance().callService(ServiceFunction.GetCupViewsPhoto, cupViewId);
            } catch (WSException e) {
                Log.e(e.getMessage());
            }

            if (StringUtils.isNullOrEmpty(jres))
                return null;

            Gson g = new Gson();
            JCupViewPhoto cupViewPhoto = g.fromJson(jres, JCupViewPhoto.class);

            File cupViewPhotosDir = getCupViewPhotosDir();
            if (cupViewPhotosDir == null) return null;

            cvFile = new File(cupViewPhotosDir.getPath() + File.separator +
                    getCupViewPhotoFilename(cupViewPhoto.cup_view_id, cupViewPhoto.view_photo_docid));

            DocumentsUtils.saveBase64File(cvFile, cupViewPhoto.cup_view_photo);
        }

        return cvFile;
    }

    public static File getCupViewPhotosDir() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                MOTOCROSS_PICTURES_DIR + CUP_VIEW_PHOTOS_DIR
        );

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) return null;
        }

        return mediaStorageDir;
    }

    private static File getCupViewPhotoFileLocal(String cupViewId) {
        File mediaStorageDir = getCupViewPhotosDir();
        if (mediaStorageDir == null) return null;

        File[] files = mediaStorageDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith(cupViewId)) {
                    return file;
                }
            }
        }
        return null;
    }

    public static String getCupViewPhotoFilename(String cupViewId, String viewPhotoDocid) {
        return String.format("%s_%s.jpg", cupViewId, viewPhotoDocid.replace(" ", "_"));
    }

    //endregion

    public static File getSavedDocumentFile(JDriverDocuments.JDriverDocument driverDocument) {
        if (StringUtils.isNullOrEmpty(driverDocument.fileName)) return null;

        File mediaStorageDir = new File(getSavedDocumentsDir(), driverDocument.fileName);
        return mediaStorageDir.exists() ? mediaStorageDir : null;
    }

    public static File downloadDocumentFile(JDriverDocuments.JDriverDocument driverDocument) throws Exception {
        if (StringUtils.isNullOrEmpty(driverDocument.fileName)) return null;

        File mediaStorageDir = getSavedDocumentsDir();
        if (mediaStorageDir == null) return null;

        File documentFile = new File(mediaStorageDir.getPath(), driverDocument.fileName);

        if (documentFile.exists()) {
            documentFile.delete();
            documentFile = new File(mediaStorageDir.getPath(), driverDocument.fileName);
        }

        Gson g = new Gson();
        String fileData;
        JDriverDocumentKey docKey = new JDriverDocumentKey();
        docKey.tknum = driverDocument.tknum;
        docKey.documentKey = driverDocument.documentKey;
        docKey.documentType = driverDocument.documentType;

        String jInp = g.toJson(docKey, JDriverDocumentKey.class);
        String jres = null;
        try {
            jres = ZMotoService.getInstance().callService(ServiceFunction.GetDocument, jInp);
        } catch (WSException e) {
            throw new Exception(e.getMessage());
        }

        if (StringUtils.isNullOrEmpty(jres))
            throw new Exception("Не удалось загрузить файл!");

        JDocumentFile jDocFile = g.fromJson(jres, JDocumentFile.class);

        if (StringUtils.isNullOrEmpty(jDocFile.data)) {
            if (!StringUtils.isNullOrEmpty(jDocFile.error)) {
                throw new Exception(jDocFile.error);
            }
        }

        DocumentsUtils.saveBase64File(documentFile, jDocFile.data);

        return documentFile;
    }

    public static File getLocationSchemaFile(String tknum) {
        if (StringUtils.isNullOrEmpty(tknum)) return null;

        File mediaStorageDir = SwiftApplication.getApplication().getExternalFilesDir(SCHEMES_DIR);
        if (mediaStorageDir == null) return null;

        File[] files = mediaStorageDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith(tknum)) {
                    return file;
                }
            }
        }

        Gson g = new Gson();
        JCupGetPhotoIn cupGetPhotoIn = new JCupGetPhotoIn();
        String jInp = g.toJson(cupGetPhotoIn, JCupGetPhotoIn.class);
        String jres = null;
        try {
            jres = ZMotoService.getInstance().callService(ServiceFunction.GetLocationSchema, tknum);
        } catch (WSException e) {
            Log.e(e.getMessage());
        }

        if (StringUtils.isNullOrEmpty(jres))
            return null;

        String extension = ImageUtils.getImageExtFromBase64(jres);

        File schemaFile = new File(mediaStorageDir.getPath() + File.separator + tknum + "_schema." + extension);

        DocumentsUtils.saveBase64File(schemaFile, jres);

        return schemaFile;
    }

    private static File getSavedDocumentsDir() {
        return SwiftApplication.getApplication().getExternalFilesDir(DOCUMENTS_DIR);
    }

    @Deprecated
    public static File getPhotoDocumentsDirectory() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                MOTOCROSS_PICTURES_DIR + PHOTO_DOCUMENTS_DIR
        );

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) return null;
        }

        return mediaStorageDir;
    }

    @Nullable
    public static String getPhotoDocumentsPath() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                MOTOCROSS_PICTURES_DIR + PHOTO_DOCUMENTS_DIR
        );

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) return null;
        }

        return mediaStorageDir.getPath();
    }

    public static void saveBase64File(File file, String base64Data) {
        if (file == null || base64Data == null) {
            return;
        }

        FileOutputStream fos = null;

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            byte[] decodedString = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT);

            fos = new FileOutputStream(file);
            fos.write(decodedString);

            fos.flush();
            fos.close();

        } catch (Exception e) {
            Log.e(e.getMessage());
        } finally {
            if (fos != null) {
                fos = null;
            }
        }
    }

}
