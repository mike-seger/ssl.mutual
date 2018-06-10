package com.net128.app.ssl.mutual;

import com.net128.app.ssl.mutual.util.OkHttpClientSslTrustingMutualAuthUtil;
import okhttp3.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OkHttpClientTest {
    private final static Logger logger= LoggerFactory.getLogger(OkHttpClientTest.class);
    private static String keyStorePass="client1234";
    private static String keyStoreLocation="/openssl/certs/client.jks";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient.Builder clientBuilder=
        OkHttpClientSslTrustingMutualAuthUtil.getClientBuilder(keyStoreLocation, keyStorePass, true);

    public static void main(String args []) {
        String [] newArgs={
            "https://postman-echo.com/post",
            "https://localhost:8443/api/post"
        };
        List<String> urlList=new ArrayList();
        urlList.addAll(Arrays.asList(newArgs));
        urlList.addAll(Arrays.asList(args));
        new OkHttpClientTest().run(urlList);
    }

    private OkHttpClient client;

    public void run(List<String> serverUrl) {
        client = clientBuilder.build();
        serverUrl.forEach(this::run);
    }

    private void run(String url)  {
        try {
            RequestBody body = RequestBody.create(JSON, new JSONObject()
                .put("param1", "value1")
                .put("param2", "value2").toString());
            Request request = new Request.Builder().url(url).post(body).build();
            try (Response response = client.newCall(request).execute()) {
                System.out.println(response.body().string());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
