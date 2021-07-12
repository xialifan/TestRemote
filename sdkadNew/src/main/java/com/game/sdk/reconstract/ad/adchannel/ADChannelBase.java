package com.game.sdk.reconstract.ad.adchannel;

import android.app.Application;

import com.game.sdk.reconstract.ad.ADCore;
import com.game.sdk.reconstract.ad.base.AdConfigManager;
import com.game.sdk.reconstract.ad.callback.AdCallBack;
import com.game.sdk.reconstract.manager.ConfigManager;
import com.game.sdk.reconstract.model.AdInfoEntity;

import java.util.List;

public class ADChannelBase {
    protected List<AdInfoEntity.AdsRuleBean.ListBean> adRulebeanList;

    protected String appId;
    protected String appName;
    protected AdCallBack mAdCallBack;
    protected String pollType;
    protected Application mApplication;


    public void init(String appId,String appName,List<AdInfoEntity.AdsRuleBean.ListBean> adRulebeanList){
        this.appId = appId;
        this.appName = appName;
        AdConfigManager.getInstance().getAdCallBack();
        this.adRulebeanList = adRulebeanList;
        this.mApplication = ADCore.getInstance().getApplication();
    }

}
