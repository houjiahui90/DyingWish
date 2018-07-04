package com.houjiahui.common.utils;

import java.io.IOException;

import com.houjiahui.core.entity.AccessToken;
import com.houjiahui.core.entity.JsApiTicket;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TokenUtils {

	// 凭证获取（GET）——access_token
	public final static String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	// 微信JSSDK的ticket请求URL地址——jsapi_ticket
	public final static String WEIXIN_JSSDK_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";

	/**
	 * 获取接口访问凭证
	 * 
	 * @param appId
	 *            凭证
	 * @param appSecret
	 *            密钥
	 * @return
	 */
	public static AccessToken getAccessToken(String appId, String appSecret)
			throws ClientProtocolException, IOException {
		String requestUrl = TOKEN_URL.replace("APPID", appId).replace("APPSECRET", appSecret);
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(requestUrl);
		CloseableHttpResponse response = client.execute(httpGet);
		try {
			System.out.println("accessTokenStatusLine:" + response.getStatusLine());
			HttpEntity entity = response.getEntity();
			return new ObjectMapper().readValue(EntityUtils.toString(entity), AccessToken.class);
		} finally {
			response.close();
		}
	}

	/**
	 * 调用微信JS接口的临时票据
	 * 
	 * @param access_token
	 *            接口访问凭证
	 * @return
	 */
	public static JsApiTicket getJsApiTicket(String access_token) throws ClientProtocolException, IOException {
		String requestUrl = WEIXIN_JSSDK_TICKET_URL.replace("ACCESS_TOKEN", access_token);
		// 发起GET请求获取凭证
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(requestUrl);
		CloseableHttpResponse response = client.execute(httpGet);
		try {
			System.out.println("jsApiTicketStatusLine:" + response.getStatusLine());
			HttpEntity entity = response.getEntity();
			return new ObjectMapper().readValue(EntityUtils.toString(entity), JsApiTicket.class);
		} finally {
			response.close();
		}
	}

}
