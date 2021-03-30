package com.phpfoxlibrary;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class RNPhpfoxPlayNotificationSoundModule extends ReactContextBaseJavaModule {

    private ReactApplicationContext reactContext;
    private MediaPlayer mPlayer;

    public RNPhpfoxPlayNotificationSoundModule(@Nullable ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @NonNull
    @Override
    public String getName() {
        return "RNPhpfoxPlayNotificationSound";
    }

    @ReactMethod
    public void playTypingSound(){
        try {
            Uri notification = Uri.parse("android.resource://"+reactContext.getPackageName() + "/" + R.raw.typing_sound);
            if (mPlayer != null) mPlayer.stop();
            mPlayer = new MediaPlayer();
            mPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build()
            );
            mPlayer.setDataSource(this.reactContext, notification);
            mPlayer.prepare();
            mPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
