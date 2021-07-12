package com.game.sdk.reconstract.ad;

import android.app.Activity;
import android.app.Application;

import com.game.sdk.reconstract.ad.callback.AdCallBack;


public class ADMix {

    public static Application getApplication(){
        return ADCore.getInstance().getApplication();
    }

    public static Activity getMainActivity(){
        return ADCore.getInstance().getMainActivity();
    }

    /**
     * Application 初始化
     */
    public static void initApp(Application context) {
        ADCore.getInstance().initApp(context);

    }

    /**
     * Main Activity 初始化
     */
    public static void initMainActivity(Activity context) {
        ADCore.getInstance().initMainActivity(context);

    }

    /**
     * @param adCallBack
     */
    public static void setAdCallBack(AdCallBack adCallBack){
        ADCore.getInstance().setAdCallBack(adCallBack);
    }

    /**
     * 显示广告
     * @param extra 调用广告时传的透传参数 在广告播放成功回调中回原样返回
     */
    public static void showVideoAD(final int adType,final String extra){
        getMainActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ADCore.getInstance().ShowVideoAD(adType,extra);
            }
        });
    }

    public static void AdSuccess(){

    }

}
