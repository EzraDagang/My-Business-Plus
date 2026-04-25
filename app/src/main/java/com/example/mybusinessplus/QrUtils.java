package com.example.mybusinessplus;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

public class QrUtils {

    public static Bitmap generateQrFromBase64(String base64String, int size) throws Exception {
        // 1. Decode Base64 to Plain Text
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        String decodedData = new String(decodedBytes, "UTF-8");

        // 2. Generate QR BitMatrix
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    decodedData,
                    BarcodeFormat.QR_CODE,
                    size, size
            );
        } catch (WriterException e) {
            return null;
        }

        // 3. Convert BitMatrix to Bitmap (Using Vibrant Blue)
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        int blueColor = Color.parseColor("#0059B2");

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? blueColor : Color.WHITE);
            }
        }
        return bitmap;
    }
}