package com.game.sdk.reconstract.ad.adchannel;

import android.util.Log;

import com.game.sdk.reconstract.ad.ADCore;
import com.game.sdk.reconstract.ad.ADMix;
import com.game.sdk.reconstract.ad.base.AdConfig;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.game.sdk.reconstract.manager.ConfigManager;
import com.game.sdk.reconstract.manager.UserCenterManager;
import com.game.sdk.reconstract.model.AdInfoEntity;
import com.game.sdk.reconstract.presenter.UserModel;
import com.reyun.tracking.sdk.Tracking;

import java.util.List;

public class ADJrtt extends ADChannelBase{

    public volatile static ADJrtt mInstance;
//    private AdCallBack mAdCallBack;

    private TTAdNative mTTAdNative;
    private TTRewardVideoAd mttRewardVideoAd;
//    private static Application mApplication;
//    private static String aId;
//    private static String appName;

//    private List<AdInfoEntity.AdsRuleBean.ListBean> beanList;
    private boolean watchComplete = false;
//    private AdCallBack mAdCallBack;

//    private int nowId = 0;

    private static final String TAG = "ADJrtt";

    /**
     * 获取 ADJrtt 入口
     *
     * @return
     */
    public static ADJrtt getInstance() {
        if (mInstance == null) {
            synchronized (ADJrtt.class) {
                if (mInstance == null) {
                    mInstance = new ADJrtt();
                }
            }
        }
        return mInstance;
    }

    public void AdJrttInit(String appId,String appName,List<AdInfoEntity.AdsRuleBean.ListBean> adRulebeanList) {
        super.init(appId,appName,adRulebeanList);
        Log.i(TAG,"AdJrttInit "+appId+"/"+appName);
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
        //step1:初始化sdk
        TTAdManager ttAdManager = TTAdSdk.getAdManager();
        mTTAdNative = ttAdManager.createAdNative(mApplication);
        TTAdSdk.init(mApplication,
                    new TTAdConfig.Builder()
                            .appId(appId)
                            .useTextureView(false) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                            .appName(appName)
                            .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                            .allowShowNotify(true) //是否允许sdk展示通知栏提示
                            .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI)
                           // .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                            .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                            .asyncInit(true)
                            .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_4G) //允许直接下载的网络状态集合
                            .supportMultiProcess(true) //是否支持多进程，true支持
                            .asyncInit(true)
                            .build());
//        }
    }



    public void JrttRewardVideoShow() {
        Log.e("ADJrtt", "JrttRewardVideoShow");
        if (mttRewardVideoAd != null) {
            //step6:在获取到广告后展示
            Log.e("ADJrtt", "mttRewardVideoAd != null");
//            UserCenterManager.logAdClickEvent("ads_CXJ_play_start");
            mttRewardVideoAd.showRewardVideoAd(ADMix.getMainActivity(), TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "scenes_test");
            mttRewardVideoAd = null;
            Tracking.setAdShow("csj", appId, "1");
        } else {
            toast("请先加载广告");
//            AdConfigManager.getInstance().getAdCallBack().onAdFailed("广告预加载失败");
            mAdCallBack.onAdFailed("广告预加载失败");
//            loadAd(0);
            ADCore.getInstance().reloadCurrentAd();
            Tracking.setAdShow("csj", appId, "2");
        }
    }


    private boolean mHasShowDownloadActive = false;

    public void loadAd(final int index) {
//        AdConfigManager.getInstance().setAdCallBack(mAdCallBack);
        Log.e(TAG, "loadAd");
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adRulebeanList.get(index).getPosid())
//                .setSupportDeepLink(true)
    //                .setImageAcceptedSize(1080, 1920)
                .setExpressViewAcceptedSize(500,500)
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(1)  //奖励的数量
                .setUserID(UserModel.getInstance().getUser().getUid())//用户id,必传参数
                .setMediaExtra(AdConfig.getInstance().getmExtra()) //附加参数，可选
                .setOrientation(TTAdConstant.VERTICAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        UserCenterManager.logAdClickEvent("ads_CXJ_request");
        Log.e("ADJrtt", String.valueOf(mTTAdNative == null));
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
//                UserCenterManager.logAdClickEvent("ads_CXJ_request_failure");
                toast("rewardVideoAd video onError" + code  +message);
                mAdCallBack.onAdError(message);
                if (index + 1 >= adRulebeanList.size()) {
                    ADCore.getInstance().loadAd(0);
                    AdConfig.getInstance().setADJrttSuccess(false);
                    return;
                }
                ADCore.getInstance().loadAd(index + 1);


            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                toast("rewardVideoAd video cached");
                AdConfig.getInstance().setADJrttSuccess(true);
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                toast("rewardVideoAd loaded");
                AdConfig.getInstance().setADJrttSuccess(true);
                mttRewardVideoAd = ad;
//                UserCenterManager.logAdClickEvent("ads_CXJ_matched_success");
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        toast("rewardVideoAd show");
                        if (ConfigManager.getInstance().getAdInfoEntity().getAds_rule().getPolling_type().equals("polling")) {
                            if (index == adRulebeanList.size() - 1) {
                                ADCore.getInstance().loadAd(0);
                            } else {
                                if (index + 1 >= adRulebeanList.size()) {
                                    AdConfig.getInstance().setADJrttSuccess(false);
                                    return;
                                }
                                ADCore.getInstance().loadAd(index + 1);
                            }
                        } else {
                            Log.i(TAG,"reload-----------before");
                            ADCore.getInstance().reloadCurrentAd();
                        }
                    }

                    @Override
                    public void onAdVideoBarClick() {
//                        UserCenterManager.logAdClickEvent("ads_CXJ_click");
                        Tracking.setAdClick("csj", appId);
                        toast("rewardVideoAd bar click");
                    }

                    @Override
                    public void onAdClose() {
                        watchComplete = false;
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        UserCenterManager.logAdClickEvent("ads_CXJ_play_complete");
                        watchComplete = true;
                        toast("rewardVideoAd complete");
                    }

                    @Override
                    public void onVideoError() {
                        UserCenterManager.logAdClickEvent("ads_CXJ_matched_failure");
                        toast("rewardVideoAd error");
                        mAdCallBack.onAdFailed("广告播放失败");
                        if (index == adRulebeanList.size() - 1) {
                            ADCore.getInstance().loadAd(0);
                        } else {
                            if (index + 1 >= adRulebeanList.size()) {
                                AdConfig.getInstance().setADJrttSuccess(false);
                                return;
                            }
                            ADCore.getInstance().loadAd(index+1);
                        }
                    }

                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName, int code, String msg) {
                        toast("verify:" + rewardVerify + " amount:" + rewardAmount +
                                " name:" + rewardName);
                        if (rewardVerify) {
                            mAdCallBack.onVideoComplete(AdConfig.getInstance().getmExtra());
                        }else {
                            mAdCallBack.onAdFailed("奖励无效");
                        }


                    }

                    @Override
                    public void onSkippedVideo() {

                    }

                });
                mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                        mHasShowDownloadActive = false;
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true;
                            toast("下载中，点击下载区域暂停");
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        toast("下载暂停，点击下载区域继续");
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        toast("下载失败，点击下载区域重新下载");
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        toast("下载完成，点击下载区域重新下载");
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        toast("安装完成，点击下载区域打开");
                    }
                });
            }
        });
    }

    public void toast(String toast) {
        Log.e(ADJrtt.class.getSimpleName(), toast);
    }

}
