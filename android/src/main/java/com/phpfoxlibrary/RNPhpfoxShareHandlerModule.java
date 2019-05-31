package com.phpfoxlibrary;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class RNPhpfoxShareHandlerModule extends ReactContextBaseJavaModule {
    private ReactApplicationContext context;

    public RNPhpfoxShareHandlerModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    @Nonnull
    @Override
    public String getName() {
        return "RNPhpfoxShareHandlerModule";
    }

    @ReactMethod
    public void getData(Promise promise) {
        Activity currentActivity = getCurrentActivity();

        if (currentActivity == null) {
            promise.resolve(null);
            return;
        }

        Intent intent = currentActivity.getIntent();

        if (intent == null || !isSupported(intent)) {
            promise.resolve(null);
            return;
        }

        String action = intent.getAction();
        String intentType = intent.getType();

        WritableArray resultItems = Arguments.createArray();
        ContentResolver contentResolver = this.context.getContentResolver();

        if (Intent.ACTION_SEND.equals(action)) {
            WritableMap shareObject = Arguments.createMap();
            String type;
            String item;

            if (intentType.equals("text/plain")) {
              type = intentType;
              item = intent.getStringExtra(Intent.EXTRA_TEXT);
            } else {
              Uri mediaUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
              type = contentResolver.getType(mediaUri);
              item = mediaUri.toString();
            }

            shareObject.putString("type", type);
            shareObject.putString("item", item);
            resultItems.pushMap(shareObject);
        }

        if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            ArrayList<Uri> mediaUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            for(Uri mediaUri : mediaUris) {
                WritableMap shareObject = Arguments.createMap();
                shareObject.putString("type", contentResolver.getType(mediaUri));
                shareObject.putString("item", mediaUri.toString());
                resultItems.pushMap(shareObject);
            }
        }

        promise.resolve(resultItems);
    }

    private boolean isSupported(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();

        return ((Intent.ACTION_SEND.equals(action) || Intent.ACTION_SEND_MULTIPLE.equals(action))
                && type != null
                && (type.startsWith("image/")
                || type.startsWith("video/")
                || type.equals("text/plain")));
    }
}
