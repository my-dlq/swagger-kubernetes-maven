package club.mydlq.swagger.kubernetes.utils;

import club.mydlq.swagger.kubernetes.entity.ServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Http 请求工具
 * Http Request Tool
 */
@Slf4j
public class HttpUtils {

    private HttpUtils() {
    }

    // Timeout Setting
    private static final int TIMEOUT_CONNECT = 200;
    private static final int TIMEOUT_CONNECT_REQUEST = 200;
    private static final int TIMEOUT_SOCKET = 200;

    /**
     * 验证 uri 是否为 Swagger Api URL
     * Verify that URI is Swagger Api URL
     *
     * @param serviceInfos
     */
    public static void checkUrl(List<ServiceInfo> serviceInfos) {
        List<ServiceInfo> newServiceInfos = new ArrayList<>();
        // HttpClient 客户端
        CloseableHttpClient httpCilent = null;
        HttpGet httpGet = null;
        try {
            httpCilent = HttpClients.createDefault();
            // 过滤 URL
            for (ServiceInfo serviceInfo : serviceInfos) {
                // get request
                httpGet = createHttpGet(serviceInfo.getHost() + ":" +
                        serviceInfo.getPort() + serviceInfo.getPath());
                // get result
                String result = getHttpRequestResult(httpCilent, httpGet);
                if (!ValidationUtils.isSwagger(result)) {
                    newServiceInfos.add(serviceInfo);
                }
            }
            // 关闭 httpclient
            httpCilent.close();
        } catch (IOException e) {
            log.error("Close HttpClient Excepiton", e);
        }
        // 将非 swagger api 移除
        serviceInfos.removeAll(newServiceInfos);
    }

    /**
     * 执行 HTTP 请求，获取响应结果
     * Execute HTTP requests to obtain response results.
     *
     * @param httpGet
     * @return
     */
    private static String getHttpRequestResult(CloseableHttpClient httpClient, HttpGet httpGet) {
        String result = null;
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                result = EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (IOException e) {
            return result;
        }
        return result;
    }

    /**
     * 创建 HttpGet 请求对象
     * Create HttpGet request object.
     *
     * @param uri
     * @return
     */
    private static HttpGet createHttpGet(String uri) {
        // 设置 RequestConfig
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT_CONNECT)                   //设置连接超时时间
                .setConnectionRequestTimeout(TIMEOUT_CONNECT_REQUEST) //设置请求超时时间
                .setSocketTimeout(TIMEOUT_SOCKET)                     //设置Socket超时时间
                .setRedirectsEnabled(false)                           //默认允许自动重定向
                .build();
        HttpGet httpGet = new HttpGet();
        httpGet.setURI(URI.create(uri));
        httpGet.setConfig(requestConfig);
        return httpGet;
    }

}