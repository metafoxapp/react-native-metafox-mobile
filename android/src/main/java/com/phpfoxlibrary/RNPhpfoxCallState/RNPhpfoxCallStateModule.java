package com.phpfoxlibrary.RNPhpfoxCallState;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RNPhpfoxCallStateModule
        extends ReactContextBaseJavaModule
        implements Application.ActivityLifecycleCallbacks,
        AudioManager.OnAudioFocusChangeListener,
        RNPhpfoxCallStateListener.PhoneCallStateUpdate {

  private boolean wasAppInOffHook = false;
  private boolean wasAppInRinging = false;
  private ReactApplicationContext reactContext;
  //
  private TelephonyManager telephonyManager;
  private AudioManager audioManager;
  //
  private RNPhpfoxCallStateUpdateAction jsModule = null;
  private RNPhpfoxCallStateListener RNPhpfoxCallStateListener;
  private Activity activity = null;

  public RNPhpfoxCallStateModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNPhpfoxCallState";
  }

  @ReactMethod
  public void startListener() {
    if (activity == null) {
      activity = getCurrentActivity();
      activity.getApplication().registerActivityLifecycleCallbacks(this);
    }

    telephonyManager = (TelephonyManager) this.reactContext.getSystemService(Context.TELEPHONY_SERVICE);
    RNPhpfoxCallStateListener = new RNPhpfoxCallStateListener(this);

    audioManager = (AudioManager) this.reactContext.getSystemService(Context.AUDIO_SERVICE);

    audioManager.requestAudioFocus(
            this,
            AudioManager.STREAM_RING,
            AudioManager.AUDIOFOCUS_GAIN
    );

    telephonyManager.listen(RNPhpfoxCallStateListener, PhoneStateListener.LISTEN_CALL_STATE);
  }

  @ReactMethod
  public void stopListener() {
    telephonyManager.listen(RNPhpfoxCallStateListener, PhoneStateListener.LISTEN_NONE);
    telephonyManager = null;
    RNPhpfoxCallStateListener = null;

    audioManager.abandonAudioFocus(this);
    audioManager = null;
  }

  /**
   * @return a map of constants this module exports to JS. Supports JSON types.
   */
  public Map<String, Object> getConstants() {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("Incoming", "Incoming");
    map.put("Offhook", "Offhook");
    map.put("Disconnected", "Disconnected");
    map.put("Missed", "Missed");
    return map;
  }

  // Activity Lifecycle Methods
  @Override
  public void onActivityCreated(Activity activity, Bundle savedInstanceType) {

  }

  @Override
  public void onActivityStarted(Activity activity) {

  }

  @Override
  public void onActivityResumed(Activity activity) {

  }

  @Override
  public void onActivityPaused(Activity activity) {

  }

  @Override
  public void onActivityStopped(Activity activity) {

  }

  @Override
  public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

  }

  @Override
  public void onActivityDestroyed(Activity activity) {

  }

  @Override
  public void phoneCallStateUpdated(int state, String phoneNumber) {
    jsModule = this.reactContext.getJSModule(RNPhpfoxCallStateUpdateAction.class);

    switch (state) {
      case TelephonyManager.CALL_STATE_IDLE:
        if (wasAppInOffHook == true) {
          jsModule.callStateUpdated("Disconnected", phoneNumber);
        } else if (wasAppInRinging == true) {
          jsModule.callStateUpdated("Missed", phoneNumber);
        }

        wasAppInRinging = false;
        wasAppInOffHook = false;
        break;
      case TelephonyManager.CALL_STATE_OFFHOOK:
        if (!wasAppInOffHook) {
          wasAppInOffHook = true;
          jsModule.callStateUpdated("Offhook", phoneNumber);
        }

        break;
      case TelephonyManager.CALL_STATE_RINGING:
        if (!wasAppInRinging) {
          wasAppInRinging = true;
          jsModule.callStateUpdated("Incoming", phoneNumber);
        }

        break;
    }
  }

  @Override
  public void onAudioFocusChange(int state) {
    jsModule = this.reactContext.getJSModule(RNPhpfoxCallStateUpdateAction.class);

    Log.d("RNPhpfoxCall-AudioFocus", String.valueOf(state));


    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    scheduler.schedule(new Runnable() {
      @Override
      public void run() {
        int curMode = audioManager.getMode();

        if (curMode == AudioManager.MODE_IN_CALL || curMode == AudioManager.MODE_IN_COMMUNICATION) {
          Log.d("RNPhpfoxCallStateModule", "Offhook");
          jsModule.callStateUpdated("Offhook", "audioDetect");
        } else if (curMode == AudioManager.MODE_RINGTONE) {
          Log.d("RNPhpfoxCallStateModule", "Incoming");
          jsModule.callStateUpdated("Incoming", "audioDetect");
        } else {
          Log.d("RNPhpfoxCall-Mode", String.valueOf(curMode));
        }
      }
    }, 1, TimeUnit.SECONDS);
  }
}

