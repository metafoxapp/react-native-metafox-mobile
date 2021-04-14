package com.phpfoxlibrary.RNPhpfoxCallState;

import android.telephony.PhoneStateListener;

public class RNPhpfoxCallStateListener extends PhoneStateListener {
    private PhoneCallStateUpdate callStatCallBack;

    public RNPhpfoxCallStateListener(PhoneCallStateUpdate callStatCallBack) {
        super();
        this.callStatCallBack = callStatCallBack;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        this.callStatCallBack.phoneCallStateUpdated(state, incomingNumber);
    }

    interface PhoneCallStateUpdate {
        void phoneCallStateUpdated(int state, String incomingNumber);
    }
}