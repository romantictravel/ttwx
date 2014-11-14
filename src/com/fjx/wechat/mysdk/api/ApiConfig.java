package com.fjx.wechat.mysdk.api;


import com.fjx.wechat.mysdk.constants.WechatApiConstants;

/**
 * 微信api配置类
 */
public class ApiConfig {

	private AccessToken accessToken;
	private String appId = null;
	private String appSecret = null;
	
	public void init(AccessToken accessToken) {
		setAccessToken(accessToken);
	}
	
	public void init(AccessToken accessToken, String appId, String appSecret) {
		setAccessToken(accessToken);
		setAppId(appId);
		setAppSecret(appSecret);
	}

	public AccessToken getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(AccessToken accessToken) {
		this.accessToken = accessToken;
	}


	public  String getAppId() {
		if (appId == null)
			throw new RuntimeException("init ApiConfig.setAppId(...) first");
		return appId;
	}
	
	public  void setAppId(String appId) {
		if (appId == null)
			throw new IllegalArgumentException("appId can not be null");
		this.appId = appId;
	}
	
	public  String getAppSecret() {
		if (appSecret == null)
			throw new RuntimeException("init ApiConfig.setAppSecret(...) first");
		return appSecret;
	}
	
	public  void setAppSecret(String appSecret) {
		if (appSecret == null)
			throw new IllegalArgumentException("appSecret can not be null");
		this.appSecret = appSecret;
	}
}



