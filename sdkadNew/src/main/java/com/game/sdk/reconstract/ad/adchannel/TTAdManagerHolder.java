package com.game.sdk.reconstract.ad.adchannel;

import android.content.Context;
import android.util.Log;

import com.bytedance.msdk.api.TTAdConfig;
import com.bytedance.msdk.api.TTAdConstant;
import com.bytedance.msdk.api.TTMediationAdSdk;
import com.bytedance.msdk.api.UserInfoForSegment;
import com.game.sdk.Platform;
import com.game.sdk.reconstract.presenter.UserModel;
import com.game.sdk.reconstract.utils.GlobalUtil;


/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 */
public class TTAdManagerHolder {

    private static boolean sInit;

    public static void init(Context context,String appid,String appName) {
        doInit(context,appid,appName);
    }

    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private static void doInit(Context context,String appid,String appName) {
        if (!sInit) {
            TTAdConfig ttConfig = buildConfig(context,appid,appName);
            TTMediationAdSdk.initialize(context, ttConfig);
            sInit = true;



        }
    }

    private static TTAdConfig buildConfig(Context context,String gromoreAid,String appName) {

        Log.i("ADUnifyJrtt", "appId" + gromoreAid);

        UserInfoForSegment userInfo = new UserInfoForSegment();
        userInfo.setUserId(UserModel.getInstance().getUser().getUid());
        return new TTAdConfig.Builder()
                .appId(gromoreAid) //必填 ，不能为空
                .appName(appName) //必填，不能为空
                .openAdnTest(false)//开启第三方ADN测试时需要设置为true，会每次重新拉去最新配置，release 包情况下必须关闭.默认false
                .isPanglePaid(false)//是否为费用户
                .setPublisherDid(GlobalUtil.getIMEI(Platform.getInstance().getContext())) //用户自定义device_id
                .openDebugLog(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .usePangleTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                .setPangleTitleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                .allowPangleShowNotify(true) //是否允许sdk展示通知栏提示
                .allowPangleShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                .setPangleDirectDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_4G,TTAdConstant.NETWORK_STATE_MOBILE) //允许直接下载的网络状态集合
                .needPangleClearTaskReset()//特殊机型过滤，部分机型出现包解析失败问题（大部分是三星）。参数取android.os.Build.MODEL
                .setUserInfoForSegment(userInfo) // 设置流量分组的信息
                .build();
    }



}
