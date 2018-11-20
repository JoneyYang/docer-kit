package me.joney.plugin.coderkit.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

/**
 * Created by yang.qiang on 2018/09/22.
 */
public class HttpUtil {

    /**
     * Post请求，Form表单参数
     *
     * @param url        请求url地址
     * @param parameters 表单参数
     * @param headers    请求头
     * @return return
     * @throws IOException IOException
     */
    public static HttpResponse doPost(String url, Map<String, String> parameters, Map<String, String> headers) throws IOException {
        if (url == null) {
            throw new RuntimeException("请求url不能为空");
        }

        HttpClient client = HttpClients.createDefault();// 创建HttpClient对象

        HttpPost post = new HttpPost(url); // 创建POST请求

        // header设置
        if (headers != null && headers.size() >= 1) {
            headers.forEach(post::setHeader);
        }

        // 参数设置
        if (parameters != null && parameters.size() >= 1) {
            List<NameValuePair> params = new ArrayList<>();
            parameters.forEach((key, value) -> params.add(new BasicNameValuePair(key, value)));
            post.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
        }

        // 得到响应并转化成字符串
        return client.execute(post);
    }


    /**
     * Get请求
     *
     * @param url         URL
     * @param queryParams 查询参数
     * @param headers     请求头
     */
    public static HttpResponse doGet(String url, Map<String, String> queryParams, Map<String, String> headers) throws Exception {

        URIBuilder uriBuilder = new URIBuilder(url);

        if (queryParams != null && !queryParams.isEmpty()) {
            queryParams.forEach(uriBuilder::addParameter);
        }

        HttpGet httpGet = new HttpGet(url);

        if (headers != null && !headers.isEmpty()) {
            headers.forEach(httpGet::setHeader);
        }

        HttpClient client = HttpClients.createDefault();// 创建HttpClient对象
        return client.execute(httpGet);

    }
}
