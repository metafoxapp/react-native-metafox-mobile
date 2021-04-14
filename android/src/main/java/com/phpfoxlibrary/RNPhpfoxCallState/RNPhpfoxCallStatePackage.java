package com.phpfoxlibrary.RNPhpfoxCallState;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import javax.annotation.Nonnull;

public class RNPhpfoxCallStatePackage implements ReactPackage {
  @Override
  public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
    List<NativeModule> modules = new ArrayList<>();

    RNPhpfoxCallStateModule callDetectionModule = new RNPhpfoxCallStateModule(reactContext);
    modules.add(callDetectionModule);

    return modules;
  }

  public List<NativeModule> createJSModules(@Nonnull ReactApplicationContext reactContext) {
    return Collections.emptyList();
  }

  @Override
  public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
    return Collections.emptyList();
  }
}

