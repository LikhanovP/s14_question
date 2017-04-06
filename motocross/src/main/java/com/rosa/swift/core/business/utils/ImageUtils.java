package com.rosa.swift.core.business.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import com.rosa.motocross.R;
import com.rosa.swift.SwiftApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by inurlikaev on 20.04.2016.
 */
public class ImageUtils {
    public static Bitmap getSampledBitmap(String imagePath, int newImageWidth, int newImageHeight) {
        return getSampledBitmap(imagePath, newImageWidth, newImageHeight, false);
    }

    //получить сэмплированный рисунок приблизительно близкий по размеру - заданным размерам
    public static Bitmap getSampledBitmap(String imagePath, int newImageWidth, int newImageHeight, boolean lockBounds) {
        Bitmap newBitmap = null;
        if (!StringUtils.isNullOrEmpty(imagePath)) {
            try {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imagePath, bmOptions);

                int imageW = bmOptions.outWidth;
                int imageH = bmOptions.outHeight;

                float rw = 1;
                if (newImageWidth > 0) {
                    rw = (float) imageW / newImageWidth;
                }

                float rh = 1;
                if (newImageHeight > 0) {
                    rh = (float) imageH / newImageHeight;
                }

                bmOptions.inJustDecodeBounds = false;
                if (lockBounds) {
                    bmOptions.inSampleSize = Math.round(Math.max(rw, rh));
                } else {
                    bmOptions.inSampleSize = Math.round(Math.min(rw, rh));
                }

                newBitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
            } catch (Exception e) {
                Log.e(e.getMessage());
            }
        }

        return newBitmap;
    }

    @Nullable
    public static String getBitmapBase64New(String imagePath, int quality) {
        String encodedString = null;
        try {
            if (!TextUtils.isEmpty(imagePath) && new File(imagePath).exists()) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BitmapFactory.decodeFile(imagePath)
                        .compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                encodedString = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
            }
        } catch (Exception exception) {
            Log.e(exception);
        }
        return encodedString;
    }

    //закруглить углы
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        if (bitmap == null) return null;
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private static byte[] intToByteDirect(int[] source) {
        if (source == null) return null;
        byte[] result = new byte[source.length];
        for (int i = 0; i < source.length; i++) {
            result[i] = (byte) source[i];
        }
        return result;
    }

    //получить расширение рисунка по двоичным данным из base64
    public static String getImageExtFromBase64(String base64Data) {
        if (StringUtils.isNullOrEmpty(base64Data)) return null;
        int strLen = base64Data.length();

        String signature;
        if (strLen > 12) {
            signature = base64Data.substring(0, 12);
        } else {
            signature = base64Data;
        }

        byte[] signBytes = android.util.Base64.decode(signature, android.util.Base64.DEFAULT);

        final byte[] jpeg = resourcesGetByteArray(R.array.image_signature_jpeg);
        final byte[] png = resourcesGetByteArray(R.array.image_signature_png);
        final byte[] gif = resourcesGetByteArray(R.array.image_signature_gif);
        final byte[] bmp = resourcesGetByteArray(R.array.image_signature_bmp);
        final byte[] tif1 = resourcesGetByteArray(R.array.image_signature_tif1);
        final byte[] tif2 = resourcesGetByteArray(R.array.image_signature_tif2);

        if (byteArrayStartsWith(signBytes, jpeg)) return "jpg";
        if (byteArrayStartsWith(signBytes, png)) return "png";
        if (byteArrayStartsWith(signBytes, gif)) return "gif";
        if (byteArrayStartsWith(signBytes, bmp)) return "bmp";
        if (byteArrayStartsWith(signBytes, tif1) || byteArrayStartsWith(signBytes, tif2))
            return "tif";

        return null;
    }

    private static byte[] resourcesGetByteArray(int resourceId) {
        try {
            return intToByteDirect(SwiftApplication.getApplication().getResources().getIntArray(resourceId));
        } catch (Exception ignored) {
            return null;
        }

    }

    private static boolean byteArrayStartsWith(byte[] bytes, byte[] prefix) {
        if (bytes == prefix) {
            return true;
        }
        if (bytes == null || prefix == null || bytes.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (bytes[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

}
