
package com.phpfoxlibrary;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

public class RNPhpfoxMobileModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNPhpfoxMobileModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNPhpfoxMobile";
  }

  @ReactMethod
  public void setWindowColor(final String colorCode) {
    try {
      final Activity currentActivity = getCurrentActivity();
      if (currentActivity == null) {
        return;
      }
      currentActivity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
          int colorHexCode = Color.parseColor(colorCode);
          currentActivity.getWindow().setBackgroundDrawable(new ColorDrawable(colorHexCode));
        }
      });
    } catch (Error e) {
      return;
    }
  }
}