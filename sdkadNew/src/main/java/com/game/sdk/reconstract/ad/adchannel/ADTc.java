package com.game.sdk.reconstract.ad.adchannel;//package com.game.sdk.reconstract.ad.adchannel;

import android.os.SystemClock;
import android.util.Log;

import com.game.sdk.reconstract.ad.ADCore;
import com.game.sdk.reconstract.ad.base.AdConfig;
import com.game.sdk.reconstract.ad.base.AdConfigManager;
import com.game.sdk.reconstract.ad.ADMix;
import com.game.sdk.reconstract.manager.ConfigManager;
import com.game.sdk.reconstract.model.AdInfoEntity;
import com.qq.e.ads.rewardvideo.RewardVideoAD;
import com.qq.e.ads.rewardvideo.RewardVideoADListener;
import com.qq.e.comm.managers.GDTADManager;
import com.qq.e.comm.util.AdError;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ADTc extends ADChannelBase {
    public static final String TAG = "ADTc";

    public volatile static ADTc mInstance;

    private boolean adLoaded;//广告加载成功标志

    private RewardVideoAD rewardVideoAD;

//    private AdCallBack mAdCallBack;

    private RewardVideoADListener rewardVideoADListener;

//    private List<AdInfoEntity.AdsRuleBean.ListBean> beanList;
    private boolean watchComplete = false;
//    private static String aId;
    /**
     * 获取 ADTc 入口
     *
     * @return
     */
    public static ADTc getInstance() {
        if (mInstance == null) {
            synchronized (ADTc.class) {
                if (mInstance == null) {
                    mInstance = new ADTc();
                }
            }
        }
        return mInstance;
    }

    public void initSdk(String appid,String appName,List<AdInfoEntity.AdsRuleBean.ListBean> adRulebeanList){
        super.init(appId,appName,adRulebeanList);
        Log.i(TAG,"initSdk "+appid+"/"+appName);
//        this.aId = appid;
        GDTADManager.getInstance().initWith(ADCore.getInstance().getApplication(),appId);
//        this.beanList = ConfigManager.getInstance().getAdInfoEntity().getAds_rule().getList();
        Log.i(TAG,"initSdk over");

    }



    public void loadAd(final int id) {
        Log.i(TAG,"loadAd "+id);

        rewardVideoADListener = new RewardVideoADListener() {

            @Override
            public void onADLoad() {
                Log.i("ADtc","onADLoad");
                AdConfig.getInstance().setADTcSuccess(true);
            }

            @Override
            public void onVideoCached() {
                Log.i("ADtc","onVideoCached");
            }

            @Override
            public void onADShow() {
                Log.i("ADtc","onADShow");
                if (ConfigManager.getInstance().getAdInfoEntity().getAds_rule().getPolling_type().equals("polling")) {
                    if (id == adRulebeanList.size() - 1) {
                        ADCore.getInstance().loadAd(0);
                    } else {
                        if (id + 1 >= adRulebeanList.size()) {
                            AdConfig.getInstance().setADTcSuccess(false);
                            return;
                        }
                        ADCore.getInstance().loadAd(id+1);
                    }
                } else {
                    ADCore.getInstance().reloadCurrentAd();
                }
            }

            @Override
            public void onADExpose() {
                Log.i("ADtc","onADExpose");

            }

            @Override
            public void onReward(Map<String, Object> map) {
                Log.i("ADtc","onReward");
            }

            @Override
            public void onADClick() {
                Log.i("ADtc","onADClick");
            }

            @Override
            public void onVideoComplete() {
                Log.i("ADtc","onVideoComplete");

                watchComplete = true;
                mAdCallBack.onVideoComplete(AdConfig.getInstance().getmExtra());
            }

            @Override
            public void onADClose() {
                Log.i("ADtc","onADClose");
                watchComplete = false;
            }

            @Override
            public void onError(AdError adError) {
                String msg = String.format(Locale.getDefault(), "onError, error code: %d, error msg: %s",
                        adError.getErrorCode(), adError.getErrorMsg());
                Log.d("ADTc",msg);
                if (id + 1 >= adRulebeanList.size()) {
                    ADCore.getInstance().loadAd(0);
                    AdConfig.getInstance().setADTcSuccess(false);
                    return;
                }
                    ADCore.getInstance().loadAd(id + 1);

                mAdCallBack.onAdFailed(msg);
            }
        };
        rewardVideoAD = new RewardVideoAD(ADMix.getMainActivity(), appId, adRulebeanList.get(id).getPosid(), rewardVideoADListener);
        rewardVideoAD.loadAD();
        mAdCallBack = AdConfigManager.getInstance().getAdCallBack();
        Log.i(TAG,"loadAd over");
    }

    public void tcRewardVideoShow() {
        if (rewardVideoAD != null) {//广告展示检查1：广告成功加载，此处也可以使用videoCached来实现视频预加载完成后再展示激励视频广告的逻辑
            if (!rewardVideoAD.hasShown()) {//广告展示检查2：当前广告数据还没有展示过
                long delta = 1000;//建议给广告过期时间加个buffer，单位ms，这里demo采用1000ms的buffer
                //广告展示检查3：展示广告前判断广告数据未过期
                if (SystemClock.elapsedRealtime() < (rewardVideoAD.getExpireTimestamp() - delta)) {
                    rewardVideoAD.showAD();
                } else {
                    Log.e(ADTc.class.getSimpleName(), "激励视频广告已过期，请再次请求广告后进行广告展示！");
                }
            } else {
                Log.e(ADTc.class.getSimpleName(), "此条广告已经展示过，请再次请求广告后进行广告展示！");
            }
        } else {
            Log.e(ADTc.class.getSimpleName(), "成功加载广告后再进行广告展示！");
        }
    }


}
