package com.game.sdk.reconstract.ad;

import android.app.Activity;
import android.app.Application;
import android.nfc.Tag;
import android.util.Log;
import android.widget.Toast;


import com.game.sdk.reconstract.ad.base.AdConfig;
import com.game.sdk.reconstract.ad.adchannel.ADJrtt;
import com.game.sdk.reconstract.ad.adchannel.ADTc;
import com.game.sdk.reconstract.ad.adchannel.ADUnifyJrtt;
import com.game.sdk.reconstract.ad.base.AdConfigManager;
import com.game.sdk.reconstract.ad.callback.AdCallBack;
import com.game.sdk.reconstract.manager.ConfigManager;
import com.game.sdk.reconstract.model.AdInfoEntity;

import java.util.ArrayList;
import java.util.List;

public class ADCore {

    private static final String TAG = ADCore.class.getName();
    private volatile static ADCore mInstance;
    private Application mApplication;
    private Activity mActivity;
    private AdCallBack adCallBack;

    public static final String AD_TYPE_GROMORE = "gromore";
    public static final String AD_TYPE_OCEANENGINEOCEAN = "oceanengine";
    public static final String AD_TYPE_ADNET = "adnet";

    private String currentAdvertiser = ""; // 当前加载广告的类型
    private int nowAdIndex = 0;

    /**
     * 获取 ADCore 入口
     *
     * @return
     */
    public static ADCore getInstance() {
        if (mInstance == null) {
            synchronized (ADCore.class) {
                if (mInstance == null) {
                    mInstance = new ADCore();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取当前 APP 的上下文环境
     *
     * @return
     */
    public Application getApplication() {
        return mApplication;
    }

    /**
     * 获取游戏主界面的  Activity
     *
     * @return
     */
    public Activity getMainActivity() {
        return mActivity;
    }

    /**
     * 初始化Application
     *
     * @param context
     */
    public void initApp(Application context) {
        mApplication = context;
    }

    /**
     * 初始化mainacitivity
     *
     * @param context
     */
    public void initMainActivity(Activity context) {
        mActivity = context;
        if (!AdConfigManager.getInstance().getAttrsValue(AdConfigManager.getInstance().getNodeByName(ADMix.getApplication(), "gmsdk/tcad"), "appId").isEmpty()) {
            AdConfig.getInstance().setTcAppId(AdConfigManager.getInstance().getAttrsValue(AdConfigManager.getInstance().getNodeByName(ADMix.getApplication(), "gmsdk/tcad"), "appId"));
        }
        initAD();
    }

    public void setAdCallBack(AdCallBack callBack) {
        adCallBack = callBack;
        AdConfigManager.getInstance().setAdCallBack(callBack);
    }


    public String getCurrentAdvertiser() {
        return currentAdvertiser;
    }

    public void setCurrentAdvertiser(String currentAdvertiser) {
        this.currentAdvertiser = currentAdvertiser;
        Log.i(TAG, "setCurrentAdvertiser:" + currentAdvertiser);
    }



    /**
     * 加载视频广告
     */
    public void initAD() {
//        if(checkThirdAdSDK()){
//            return;
//        }

        if (adCallBack == null) {
            Toast.makeText(getMainActivity(), "请先设置广告回调", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ConfigManager.getInstance().getAdInfoEntity() == null) {
            Toast.makeText(getMainActivity(), "getAdInfoEntity == null", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.i(TAG, "getAdInfoEntity():" + ConfigManager.getInstance().getAdInfoEntity().toString());
        List<AdInfoEntity.AdsRuleBean.ListBean> pollslist = new ArrayList<>();
        AdInfoEntity.AdsRuleBean ads_rule = ConfigManager.getInstance().getAdInfoEntity().getAds_rule();
        List<AdInfoEntity.AdsRuleBean.ListBean> allAdRulesBeanList = ads_rule.getList(); // 所有广告
        for (int i = 0; i < allAdRulesBeanList.size(); i++) {
            if (ads_rule.getPolls_num().isEmpty()) {
                pollslist.add(allAdRulesBeanList.get(i));
            } else {//根据轮查数复制bean
                for (int j = 0; j < Integer.parseInt(ads_rule.getPolls_num()); j++) {
                    pollslist.add(allAdRulesBeanList.get(i));
                }
            }
        }


//        List<AdInfoEntity.AdsRuleBean.ListBean> adRules =  ConfigManager.getInstance().getAdInfoEntity().getAds_rule().getList();
//        if( adRules == null ||adRules.size() == 0){
//            SDKLog.e("ADUnify", "adRules empty");
//            return;
//        }

        if (pollslist == null || pollslist.size() == 0) {
            Log.i(TAG, "adRulelist 为空");
            return;
        }

        String gromoreAppid = null;  // 聚合广告appid
        String adnetAppid = null; // 腾讯广告 appid
        String oceanengineAppid = null; //穿山甲非聚合 appid
        for (int i = 0; i < pollslist.size(); i++) {
            String adver = pollslist.get(i).getAdvertiser();
            if (AD_TYPE_GROMORE.equals(adver)) {
                gromoreAppid = pollslist.get(i).getAppid();
            } else if (AD_TYPE_ADNET.equals(adver)) {
                adnetAppid = pollslist.get(i).getAppid();
            } else if (AD_TYPE_OCEANENGINEOCEAN.equals(adver)) {
                oceanengineAppid = pollslist.get(i).getAppid();
            }
        }

        if (gromoreAppid == null && adnetAppid == null && oceanengineAppid == null) {
            Log.i(TAG, "未配置正确广告类型Advertiser");
            return;
        }

        String appName = AdConfigManager.getInstance().getAttrsValue(AdConfigManager.getInstance().getNodeByName(mActivity, "gmsdk/jrttunifyad"), "appName");

        if (gromoreAppid != null) {
            ADUnifyJrtt.getInstance().AdJrttInit(gromoreAppid, appName,pollslist);
        }
        if (adnetAppid != null) {
            ADTc.getInstance().initSdk(gromoreAppid, appName,pollslist);
        }
        if (oceanengineAppid != null) {
            ADJrtt.getInstance().AdJrttInit(gromoreAppid, appName,pollslist);
        }

//        ADUnifyJrtt.getInstance().AdJrttInit(pollslist, getApplication());
//        ADUnifyJrtt.getInstance().loadUnifyAd(0);

        String firstAdType = pollslist.get(0).getAdvertiser();
        setCurrentAdvertiser(firstAdType);
        loadAd(firstAdType, 0);
    }

    /**
     * 检测所有相关的jar包和aar是否导入
     * @return
     */
    private boolean checkThirdAdSDK() {
        String errorMessage = "";
        boolean isPassCheck = false;
        //检查主sdk
        try {
            errorMessage = "guaimaoSDK";
            Class.forName("com.game.sdk.GMSDK");
            isPassCheck = true;
        } catch (ClassNotFoundException e) {
//            errorMessage += "GuaiMaoSdk.jar ";
            isPassCheck = false;
        }

        //检查聚合sdk
//        try {
//            Class.forName("com.game.sdk.GMSDK");
//        } catch (ClassNotFoundException e) {
//            errorMessage += "GuaiMaoSdk.jar ";
//            isError = true;
//        }
        return isPassCheck;
    }

    public void reloadCurrentAd(){
        Log.i(TAG,"reload:"+currentAdvertiser+"/"+nowAdIndex);
        loadAd(currentAdvertiser,nowAdIndex);
    }

    public void loadAd(int adIndex) {
        String adType = ConfigManager.getInstance().getAdInfoEntity().getAds_rule().getList().get(adIndex).getAdvertiser();
        loadAd(adType, adIndex);
    }

    public void loadAd(String adType, int adIndex) {
        this.nowAdIndex = adIndex;
        setCurrentAdvertiser(adType);
        Log.i(TAG,"当前加载广告类型:"+currentAdvertiser+" 广告Index:"+nowAdIndex);
        switch (adType) {
            case AD_TYPE_GROMORE:
                ADUnifyJrtt.getInstance().loadUnifyAd(nowAdIndex);
                break;
            case AD_TYPE_OCEANENGINEOCEAN:
                ADJrtt.getInstance().loadAd(nowAdIndex);
                break;
            case AD_TYPE_ADNET:
                ADTc.getInstance().loadAd(nowAdIndex);
                break;
        }
    }


    public void ShowVideoAD(int adType, String extra) {
        if (adType != 13) {
            return;
        }
        AdConfig.getInstance().setmExtra(extra);

        switch (currentAdvertiser) {
            case AD_TYPE_ADNET:
                ADTc.getInstance().tcRewardVideoShow();
                break;
            case AD_TYPE_GROMORE:
                ADUnifyJrtt.getInstance().JrttRewardVideoShow();
                break;
            case AD_TYPE_OCEANENGINEOCEAN:
                ADJrtt.getInstance().JrttRewardVideoShow();
                break;
        }

    }


}
