package com.phpfoxlibrary;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

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
    public RNPhpfoxShareHandlerModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
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
        String type = intent.getType();

        WritableArray resultUris = Arguments.createArray();

        if (Intent.ACTION_SEND.equals(action)) {
            Uri mediaUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            resultUris.pushString(mediaUri.toString());
        }

        if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
            ArrayList<Uri> mediaUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            for(Uri mediaUri : mediaUris) {
                resultUris.pushString(mediaUri.toString());
            }
        }

        promise.resolve(resultUris);
    }

    private boolean isSupported(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();

        return ((Intent.ACTION_SEND.equals(action) || Intent.ACTION_SEND_MULTIPLE.equals(action))
                && type != null
                && (type.startsWith("image/") || type.startsWith("video/")));
    }
}
