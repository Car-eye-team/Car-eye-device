/*  car eye 车辆管理平台 
 * car-eye管理平台   www.car-eye.cn
 * car-eye开源网址:  https://github.com/Car-eye-team
 * Copyright
 */


package com.sh.camera.version;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;


public class HttpUtils {
	public static final int METHOD_GET = 1;
	public static final int METHOD_POST = 2;

	/**
	 * 向指定的资源路径发送请求获取响应实体对象并返回
	 * 
	 * @param uri
	 *            资源路径
	 * @param params
	 *            向服务端发送请求时的实体数据
	 * @param method
	 *            请求方法
	 * @return
	 * @throws IOException
	 */
	public static HttpEntity getEntity(String uri, List<NameValuePair> params,
			int method) throws IOException {
		HttpEntity entity = null;
		// 创建客户端对象
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 40000);
		client.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
		// 创建请求对象
		HttpUriRequest request = null;
		switch (method) {
		case METHOD_GET:
			StringBuilder sb = new StringBuilder(uri);
			if (params != null && !params.isEmpty()) {
				sb.append('?');
				for (NameValuePair pair : params) {
					sb.append(pair.getName()).append('=')
					.append(pair.getValue()).append('&');
				}
				sb.deleteCharAt(sb.length() - 1);
			}
			request = new HttpGet(sb.toString());
			System.out.println("---------------------------"+sb.toString());
			break;
		case METHOD_POST:
//			LogUtil.i("HttpUtil.uri", uri+","+params.toString());
			request = new HttpPost(uri);
			if (params != null && !params.isEmpty()) {
				// 创建请求实体对象
				UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(
						params,HTTP.UTF_8);
				// 设置请求实体
				((HttpPost) request).setEntity(reqEntity);
			}
			break;
		}
		// 执行请求获取相应对象
		HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			entity = response.getEntity();
		}
		return entity;
	}

	public static HttpEntity getEntityDelay(String uri, List<NameValuePair> params,
			int method) throws IOException {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return getEntity(uri, params, method);
	}

	/**
	 * 获取实体对象的内容长度并返回
	 * 
	 * @param entity
	 * @return
	 */
	public static long getEntity(HttpEntity entity) {
		long len = 0;
		if (entity != null) {
			len = entity.getContentLength();
		}
		return len;
	}

	/**
	 * 获取指定的响应实体对象的网络输入流
	 * 
	 * @param entity
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 */

	public static InputStream getStream(HttpEntity entity)
			throws IllegalStateException, IOException {
		InputStream in = null;
		if (entity != null) {
			in = entity.getContent();
		}
		return in;
	}

}
