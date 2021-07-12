package com.game.sdk.reconstract.ad.callback;

public interface AdCallBack {
    //广告加载失败或者播放失败
    abstract void onAdFailed(String errormsg);
    //广告加载出错
    abstract void onAdError(String errormsg);
    //广告播放完毕，可以领奖励了
    abstract void onVideoComplete(String extra);
}
