package com.rosa.swift.core.business.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.rosa.swift.SwiftApplication;
import com.rosa.swift.core.network.json.sap.documents.JDriverDocumentType;

import java.io.File;
import java.net.URLConnection;

public class SupportUtils {

    public static Intent getIntentView(File file, JDriverDocumentType type) {
        return getIntentView(file, getFileType(file, type));
    }

    public static Intent getIntentView(File file) {
        return getIntentView(file, URLConnection.guessContentTypeFromName(file.getName()));
    }

    private static Intent getIntentView(File file, String type) {
        Context context = SwiftApplication.getApplication().getApplicationContext();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(getFileUrl(context, file), type);
        return intent;
    }

    private static Uri getFileUrl(Context context, File file) {
        return FileProvider.getUriForFile(context,
                context.getApplicationContext().getPackageName() + ".provider", file);
    }

    private static String getFileType(File file, JDriverDocumentType type) {
        switch (type) {
            case PDF:
                return "application/pdf";
            case BMP:
                return "image/bmp";
            case GIF:
                return "image/gif";
            case JPEG:
                return "image/jpeg";
            case JPG:
                return "image/jpg";
            case PNG:
                return "image/png";
            case DOC:
                return "application/msword";
            case DOCX:
                return "application/msword";
            case RTF:
                return "text/plain";
            case TXT:
                return "text/plain";
            case XLS:
                return "application/vnd.ms-excel";
            case XLSX:
                return "application/vnd.ms-excel";
            case ZIP:
                return "application/zip";
            case UNKNOWN:
            default:
                return URLConnection.guessContentTypeFromName(file.getName());
        }
    }


}
