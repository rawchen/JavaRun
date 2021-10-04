package com.rawchen.javarun.onedrive;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author RawChen
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OneDriveService {

    @Value("${onedrive.clientId}")
    protected String clientId;

    @Value("${onedrive.redirectUri}")
    protected String redirectUri;

    @Value("${onedrive.clientSecret}")
    protected String clientSecret;

    @Value("${onedrive.scope}")
    protected String scope;

    @Value("${onedrive.authenticateUrl}")
    protected String authenticateUrl;

    public OneDriveToken getToken(String code) {
        String param = "client_id=" + getClientId() +
                "&redirect_uri=" + getRedirectUri() +
                "&client_secret=" + getClientSecret() +
                "&code=" + code +
                "&scope=" + getScope() +
                "&grant_type=authorization_code";

        HttpRequest post = HttpUtil.createPost(authenticateUrl);

        post.body(param, "application/x-www-form-urlencoded");
        HttpResponse response = post.execute();
        return JSONObject.parseObject(response.body(), OneDriveToken.class);
    }

    public String getAuthenticateEndPoint() {
        return authenticateUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getScope() {
        return scope;
    }

}