package com.model;


import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * Created by Administrator on 2018/5/5.
 */

public class RNModule extends ReactContextBaseJavaModule {

    public RNModule (ReactApplicationContext reactContext){
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNModule";
    }

    @ReactMethod
    public void showMyToast(String msg, Callback callback){
        Toast.makeText(getReactApplicationContext(),msg,Toast.LENGTH_LONG).show();
        callback.invoke("show toast");
    }

    @ReactMethod
    public void sendDat2RN(){
        Toast.makeText(getReactApplicationContext(),"send data to Rn",Toast.LENGTH_LONG).show();
        sendDataToRn();
    }

    /* android 通过 RCTDeviceEventEmitter向 JS 发送数据*/
    public void sendDataToRn(){
        WritableMap writableMap = new WritableNativeMap();
        String str = "{\"a\": \"A\",\"b\": \"B\"}";
        writableMap.putString("data", str);
        sendTransMisson(getReactApplicationContext(), "EventName", writableMap);
    }

    public void sendTransMisson(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @ReactMethod
    public void startActivity(String name, String params){
        try{
            Activity currentActivity = getCurrentActivity();
            if(null!=currentActivity){
                Class toActivity = Class.forName(name);
                Intent intent = new Intent(currentActivity,toActivity);
                intent.putExtra("params", params);
                currentActivity.startActivity(intent);
            }
        }catch(Exception e){
            throw new JSApplicationIllegalArgumentException(
                    "不能打开Activity : "+e.getMessage());
        }
    }



}
