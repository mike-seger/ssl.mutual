package com.net128.app.ssl.mutual;

import com.net128.app.ssl.mutual.util.HttpClientSslTrustingMutualAuthUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SSLMutualAuthTest {
    private final static Logger logger= LoggerFactory.getLogger(OkHttpClientTest.class);
    private final static String certDir = "/openssl/certs/";
    //private final static String certDir="/jsse/certs/";
    final String trustStoreLocation = certDir + "trust.jks";
    final String trustStorePassword = "trust1234";
    final String clientStoreLocation = certDir + "client.jks";
    final String clientStorePassword = "client1234";

    public static void main(String[] args) {
        String[] newArgs = {
            "https://postman-echo.com/post",
            "https://localhost:8443/api/post"
        };
        List<String> urlList = new ArrayList();
        urlList.addAll(Arrays.asList(newArgs));
        urlList.addAll(Arrays.asList(args));
        new SSLMutualAuthTest().run(urlList);
    }

    public void run(List<String> serverUrl) {
        serverUrl.stream().forEach(url -> run(url));
    }

    public void run(String serverUrl) {
        try {
            SSLConnectionSocketFactory sslConnectionSocketFactory =
                HttpClientSslTrustingMutualAuthUtil.createSSLConnectionSocketFactory(
                    clientStoreLocation, clientStorePassword, trustStoreLocation, trustStorePassword);
            CloseableHttpClient client = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();

            HttpPost httpPost = new HttpPost(serverUrl);
            HttpEntity entity = new ByteArrayEntity(new JSONObject()
                    .put("param1", "value1")
                    .put("param2", "value2").toString().getBytes("UTF-8"));
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);
            String result = EntityUtils.toString(response.getEntity());
            logger.info("Got: {}", result);
        } catch (Exception ex) {
            logger.error("Test failed: ", ex);
        }
    }
}