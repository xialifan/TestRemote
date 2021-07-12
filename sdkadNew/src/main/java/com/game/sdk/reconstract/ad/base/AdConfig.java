package com.game.sdk.reconstract.ad.base;

import com.game.sdk.reconstract.model.User;

public class AdConfig {

    private volatile static AdConfig mInstance;

    /**
     * 获取 ADCore 入口
     *
     * @return
     */
    public static AdConfig getInstance() {
        if (mInstance == null) {
            synchronized (AdConfig.class) {
                if (mInstance == null) {
                    mInstance = new AdConfig();
                }
            }
        }
        return mInstance;
    }


    private boolean ADTcSuccess;

    private boolean ADJrttSuccess;

    private String ADShowType;

    private User mUser;

    private String mExtra;

    private String IMEI;

    private String gameId;
    private String tcAppId;
    private String tcAdId;
    private String jrttAdId;
    private String jrttAdName;

    private String localInfo;

    public String getLocalInfo() {
        return localInfo;
    }

    public void setLocalInfo(String localInfo) {
        this.localInfo = localInfo;
    }

    public String getJrttAdName() {
        return jrttAdName;
    }

    public void setJrttAdName(String jrttAdName) {
        this.jrttAdName = jrttAdName;
    }

    public String getTcAppId() {
        return tcAppId;
    }

    public void setTcAppId(String tcAppId) {
        this.tcAppId = tcAppId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getmExtra() {
        return mExtra;
    }

    public void setmExtra(String mExtra) {
        this.mExtra = mExtra;
    }

    public User getmUser() {
        return mUser;
    }

    public void setmUser(User mUser) {
        this.mUser = mUser;
    }

    public String getADShowType() {
        return ADShowType;
    }

    public void setADShowType(String ADShowType) {
        this.ADShowType = ADShowType;
    }

    public boolean isADTcSuccess() {
        return ADTcSuccess;
    }

    public void setADTcSuccess(boolean ADTcSuccess) {
        this.ADTcSuccess = ADTcSuccess;
    }

    public boolean isADJrttSuccess() {
        return ADJrttSuccess;
    }

    public void setADJrttSuccess(boolean ADJrttSuccess) {
        this.ADJrttSuccess = ADJrttSuccess;
    }


}
