package com.model;

import com.facebook.react.bridge.ReactContext;

/**
 * Created by Administrator on 2018/5/10.
 */

public class RNManager {

    private static ReactContext reactContext;
    private static RNManager rnManager=null;

    public static ReactContext getReactContext() {
        return reactContext;
    }

    public static void setReactContext(ReactContext reactContext) {
        RNManager.reactContext = reactContext;
    }

    public static RNManager getInstance(){
        if (rnManager == null) {
            synchronized (RNManager.class) {
                if (rnManager == null) {
                    rnManager = new RNManager();
                }
            }
        }
        return rnManager;
    }
}
