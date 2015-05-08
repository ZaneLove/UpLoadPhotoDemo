package com.zanelove.photoapp.utils;

import android.content.Context;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;

public class HttpUtil {
	public static final int METHOD_GET = 1;
	public static final int METHOD_POST = 2;

	// ConnectTimeoutException
	public static HttpEntity getEntity(String uri, List<NameValuePair> params, int method) throws ConnectException, IOException {
		HttpEntity entity = null;
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
		HttpUriRequest request = null;
		switch (method) {
		case METHOD_GET:
			StringBuilder sb = new StringBuilder(uri);
			if (params != null && !params.isEmpty()) {
				sb.append('?');
				for (NameValuePair pair : params) {
					sb.append(pair.getName()).append('=').append(pair.getValue()).append('&');
				}
				sb.deleteCharAt(sb.length() - 1);
			}
			request = new HttpGet(sb.toString());
			break;
		case METHOD_POST:
			request = new HttpPost(uri);
			if (params != null && !params.isEmpty()) {
				UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(params, "UTF-8");
				((HttpPost) request).setEntity(reqEntity);

			}
			break;
		}
		HttpResponse response = client.execute(request);

		if (response.getStatusLine().getStatusCode() == 200) {
			entity = response.getEntity();
		} else {

		}
		return entity;
	}

	public static String getData(Context context, String uri, List<NameValuePair> params, int method) throws ConnectException, IOException {
		HttpEntity entity = getEntity(uri, params, method);
		if(entity != null) {
			return EntityUtils.toString(entity);
		} else {
			return "error";
		}
	}
}
