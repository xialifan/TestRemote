package com.game.sdk.reconstract.ad.adchannel;

import android.app.Application;
import android.util.Log;

import com.game.sdk.reconstract.ad.ADCore;
import com.game.sdk.reconstract.ad.base.AdConfig;
import com.game.sdk.reconstract.ad.callback.AdCallBack;
import com.bytedance.msdk.api.AdError;
import com.bytedance.msdk.api.AdSlot;
import com.bytedance.msdk.api.TTAdConstant;
import com.bytedance.msdk.api.TTMediationAdSdk;
import com.bytedance.msdk.api.TTSettingConfigCallback;
import com.bytedance.msdk.api.TTVideoOption;
import com.bytedance.msdk.api.reward.RewardItem;
import com.bytedance.msdk.api.reward.TTRewardAd;
import com.bytedance.msdk.api.reward.TTRewardedAdListener;
import com.bytedance.msdk.api.reward.TTRewardedAdLoadCallback;
import com.game.sdk.reconstract.ad.ADMix;
import com.game.sdk.reconstract.ad.base.AdConfigManager;
import com.game.sdk.reconstract.manager.ConfigManager;
import com.game.sdk.reconstract.model.AdInfoEntity;
import com.game.sdk.reconstract.presenter.UserModel;
import com.reyun.tracking.sdk.Tracking;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ADUnifyJrtt extends ADChannelBase{

    private static final String TAG = "ADUnifyJrtt";

    public volatile static ADUnifyJrtt mInstance;
//    private AdCallBack mAdCallBack;

    private TTRewardAd mttRewardAd;
//    private static Application mApplication;
//    private static String aId;
//    private static String appName;

//    private List<AdInfoEntity.AdsRuleBean.ListBean> beanList;
    private boolean watchComplete = false;
    private boolean loadSuccess;

    private TTRewardedAdListener mTTRewardedAdListener;
    private int nowId = 0;

    /**
     * 获取 ADJrtt 入口
     *
     * @return
     */
    public static ADUnifyJrtt getInstance() {
        if (mInstance == null) {
            synchronized (ADUnifyJrtt.class) {
                if (mInstance == null) {
                    mInstance = new ADUnifyJrtt();
                }
            }
        }
        return mInstance;
    }

    public void loadUnifyAd(int loadid) {
        laodAdWithCallback(loadid);
    }

    public void AdJrttInit(String appid, String appName,List<AdInfoEntity.AdsRuleBean.ListBean> listBeans) {
        super.init(appId,appName,listBeans);
//        mApplication = application;
//        this.beanList = listBeans;
        TTAdManagerHolder.init(mApplication,appid,appName);
//        mAdCallBack = AdConfigManager.getInstance().getAdCallBack();
    }

    /**
     * config回调
     */
    private TTSettingConfigCallback mSettingConfigCallback = new TTSettingConfigCallback() {
        @Override
        public void configLoad() {
            Log.e(TAG, "load ad 在config 回调中加载广告");
//            loadAd(0);
            ADCore.getInstance().reloadCurrentAd();
        }
    };

    /**
     * 加载广告
     */
    private void laodAdWithCallback(final int adUnitId) {
        /**
         * 判断当前是否存在config 配置 ，如果存在直接加载广告 ，如果不存在则注册config加载回调
         */
        if (TTMediationAdSdk.configLoadSuccess()) {
            Log.e(TAG, "load ad 当前config配置存在，直接加载广告");
            loadAd(adUnitId);
        } else {
            Log.e(TAG, "load ad 当前config配置不存在，正在请求config配置....");
            TTMediationAdSdk.registerConfigCallback(mSettingConfigCallback); //不用使用内部类，否则在ondestory中无法移除该回调
        }
    }

    TTVideoOption videoOption = new TTVideoOption.Builder()
            .setMuted(false)//对所有SDK的激励广告生效，除需要在平台配置的SDK，如穿山甲SDK
            .setAdmobAppVolume(0.5f)//配合Admob的声音大小设置[0-1]
            .build();

    private void loadAd(final int index) {
        nowId = index;
        Log.e(TAG, "id=" + adRulebeanList.get(index).getPosid());
        mttRewardAd = new TTRewardAd(mApplication, adRulebeanList.get(index).getPosid());


        //创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setTTVideoOption(videoOption) //视频声音相关的配置
                //AdSlot.TYPE_EXPRESS_AD 标识gdt使用动态模板激励视频;AdSlot.TYPE_NATIVE_AD:使用原生激励视频，默认为原生。
                //注意：要保证该聚合广告位下的gdt激励视频代码位，要么都是模版格式，要么都是自渲染格式，不能混用；并在此AdSlot中设置相应的类型。
                .setAdStyleType(AdSlot.TYPE_EXPRESS_AD)
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(1)  //奖励的数量
                .setUserID(UserModel.getInstance().getUser().getId())//用户id,必传参数
                .setOrientation(TTAdConstant.VERTICAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                //设置激励视频服务端验证时的自定义数据，参数是map，键为各adn（如AdSlot.CUSTOM_DATA_KEY_PANGLE），值为要传的字符串。
                .setCustomData(new HashMap<String, String>())
                .build();

        //请求广告
        mttRewardAd.loadRewardAd(adSlot, new TTRewardedAdLoadCallback() {

            @Override
            public void onRewardVideoLoadFail(AdError adError) {
                Log.e(TAG, "adError=" + adError.toString());
                loadSuccess = false;
                String msg = String.format(Locale.getDefault(), "onError, error code: %d, error msg: %s",
                        adError.code, adError.message);
                Log.d(TAG,msg);
                if (index + 1 >= adRulebeanList.size()) {
                    ADCore.getInstance().loadAd(0);
                    AdConfig.getInstance().setADJrttSuccess(false);
                    return;
                }
//                    ADTc.getInstance().loadAd(id + 1);
                ADCore.getInstance().loadAd(index + 1);
                mAdCallBack.onAdFailed(msg);
            }

            @Override
            public void onRewardVideoAdLoad() {
                Log.e(TAG, "onRewardVideoAdLoad");
                loadSuccess = true;
            }

            @Override
            public void onRewardVideoCached() {
                Log.e(TAG, "onRewardVideoCached");
                loadSuccess = true;//激励视频资源加载成功
            }
        });
    }


    public void JrttRewardVideoShow() {
        Log.e(TAG, "JrttRewardVideoShow");

        if (loadSuccess && mttRewardAd != null && mttRewardAd.isReady()) {
            //在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
            //该方法直接展示广告
            //展示广告，并传入广告展示的场景
            mttRewardAd.showRewardAd(ADMix.getMainActivity(), mTTRewardedAdListener = new TTRewardedAdListener() {
                @Override
                public void onRewardedAdShow() {
                    Log.d(TAG, "onRewardedAdShow");

                    if (ConfigManager.getInstance().getAdInfoEntity().getAds_rule().getPolling_type().equals("polling")) {
                        if (nowId == adRulebeanList.size() - 1) {
                            ADCore.getInstance().loadAd(0);
                        } else {
                            if (nowId + 1 >= adRulebeanList.size()) {
                                AdConfig.getInstance().setADJrttSuccess(false);
                                return;
                            }
                            ADCore.getInstance().loadAd(nowId + 1);
                        }
                    } else {
                        Log.d(TAG, "onRewardedAdShow----ready reload");
                        ADCore.getInstance().reloadCurrentAd();
                    }
                }


                @Override
                public void onRewardClick() {

                }

                @Override
                public void onRewardedAdClosed() {
                    Log.d(TAG, "onRewardedAdClosed");
//                    if (ConfigManager.getInstance().getAdInfoEntity().getAds_rule().getPolling_type().equals("polling")) {
//                        if (nowId == beanList.size() - 1) {
////                            ADUnifyJrtt.getInstance().loadAd(0);
//                            ADCore.getInstance().loadAd(0);
//                        } else {
//                            if (nowId + 1 >= beanList.size()) {
//                                AdConfig.getInstance().setADJrttSuccess(false);
//                                return;
//                            }
////                            ADUnifyJrtt.getInstance().loadAd(nowId + 1);
//                            ADCore.getInstance().loadAd(nowId + 1);
//                        }
//                    } else {
////                        ADUnifyJrtt.getInstance().loadAd(0);
//                        ADCore.getInstance().reloadCurrentAd();
//                    }
                }

                @Override
                public void onVideoComplete() {

                }

                @Override
                public void onVideoError() {

                }

                @Override
                public void onRewardVerify(RewardItem rewardItem) {
                    toast("verify:" + rewardItem.rewardVerify() + " amount:" + rewardItem.getAmount() +
                            " name:" + rewardItem.getRewardName());
                    if (rewardItem.rewardVerify()) {
                        AdConfigManager.getInstance().getAdCallBack().onVideoComplete(AdConfig.getInstance().getmExtra());
                    } else {
                        AdConfigManager.getInstance().getAdCallBack().onAdFailed("奖励无效");
                    }
                }

                @Override
                public void onSkippedVideo() {

                    AdConfigManager.getInstance().getAdCallBack().onAdFailed("跳过广告");
                }
            });

            Tracking.setAdShow("csj", appId, "1");
        } else {
            toast("请先加载广告");
            AdConfigManager.getInstance().getAdCallBack().onAdFailed("广告预加载失败");
//            loadAd(0);
            ADCore.getInstance().reloadCurrentAd();
            Tracking.setAdShow("csj", appId, "2");
        }
    }


    private boolean mHasShowDownloadActive = false;


    public void toast(String toast) {
        Log.e(TAG, toast);
//        Toast.makeText(ADMix.getMainActivity(),toast,Toast.LENGTH_SHORT).show();
    }

}
