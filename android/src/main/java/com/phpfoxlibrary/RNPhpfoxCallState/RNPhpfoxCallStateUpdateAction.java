package com.phpfoxlibrary.RNPhpfoxCallState;
import com.facebook.react.bridge.JavaScriptModule;


public interface RNPhpfoxCallStateUpdateAction extends JavaScriptModule {
  void callStateUpdated(String state, String phoneNumber);
}