
package com.phpfoxlibrary;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import id.zelory.compressor.Compressor;

public class RNPhpfoxImageCompresserModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private final String TAG = "ImageCompresser";

    public RNPhpfoxImageCompresserModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNPhpfoxImageCompresser";
    }


    public String getFileAbsolutePath(String originalImagePath) {
        Uri fileUri = Uri.parse(originalImagePath);
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = reactContext.getContentResolver().query(fileUri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    File doCompress(File image, Double limit) throws IOException {

        String[] paths = image.getName().split("\\.(?=[^\\.]+$)");
        String compressedFileName = paths[0] + "-compressed";
        File file = null;
        if (paths.length > 1) {
            compressedFileName += "." + paths[1];
        }

        if(image.length() < 1){
            return null;
        }

        Compressor compressor = new Compressor(reactContext)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setDestinationDirectoryPath(reactContext.getCacheDir().getAbsolutePath());


        Double quality = 1.0 - limit / image.length();

        for (; quality > 0.1; quality -= 0.1) {
            compressor.setQuality((int) (quality * 100));
            file = compressor.compressToFile(image, compressedFileName);
            if (file.length() < limit) {
                return file;
            }
        }

        return file;
    }

    WritableMap compressImage(final ReadableMap options) throws IOException {

        WritableMap result = Arguments.createMap();
        String originalImagePath = options.getString("path");
        String absolutePath = Uri.parse(originalImagePath).getPath();

        Double limit = options.getDouble("limit");
        File image = new File(absolutePath);

        if(!image.exists()){
            throw new IOException("could not found " + absolutePath);
        };

        Long originalFileSize = image.length();

        result.putString("path", originalImagePath);
        result.putDouble("limit", limit);
        result.putDouble("quality", 1.0);
        result.putString("original_path", originalImagePath);
        result.putDouble("original_filesize", originalFileSize);

        if (originalFileSize <= limit) {
            return result;
        }

        File compressedFile = doCompress(image, limit);

        if(compressedFile != null){
            result.putDouble("quality", compressedFile.length() / limit);
            result.putDouble("filesize", compressedFile.length());
            result.putString("path", compressedFile.toURI().toString());
        }

        return result;
    }

    @ReactMethod
    public void compressImage(ReadableMap options, @Nullable Callback failureCallback, @Nullable Callback successCallback) {
        try {
            Log.d(TAG, "Compress");
            WritableMap result = compressImage(options);
            successCallback.invoke(result);
        } catch (Exception e) {
            e.printStackTrace();
            failureCallback.invoke(e.getMessage());
        }
    }
}