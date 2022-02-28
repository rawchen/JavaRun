package com.rawchen.javarun.modules.onedrive;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 此包为OneDrive的FODI列表程序相关类。
 * 生成token与cf所需代码。
 *
 * @author RawChen
 */
public class OneDriveToken {

    @JSONField(name = "access_token")
    private String accessToken;

    @JSONField(name = "refresh_token")
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public String toString() {
        return "OneDriveToken{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
