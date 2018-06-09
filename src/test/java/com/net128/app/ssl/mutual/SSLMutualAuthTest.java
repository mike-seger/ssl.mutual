package com.net128.app.ssl.mutual;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.PrivateKeyDetails;
import org.apache.http.ssl.PrivateKeyStrategy;
import org.apache.http.ssl.SSLContexts;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SSLMutualAuthTest {
    private final static String certDir="/openssl/certs/";
    //private final static String certDir="/jsse/certs/";

    public static void main(String[] args) {
        String [] newArgs={
            "https://postman-echo.com/post",
            "https://localhost:8443/api/post"
        };
        List<String> urlList=new ArrayList();
        urlList.addAll(Arrays.asList(newArgs));
        urlList.addAll(Arrays.asList(args));
        new SSLMutualAuthTest().run(urlList);
    }
//    private String serverUrl="https://postman-echo.com/post";

    public void run(List<String> serverUrl) {
        serverUrl.stream().forEach(url -> run(url));
    }

    public void run(String serverUrl) {
        try {
            final String CERT_ALIAS = "client";
            final String clientCertPassword = "client1234";
            final String clientTrustCertPassword = "trust1234";

            KeyStore identityKeyStore = KeyStore.getInstance("jks");
            try (InputStream identityKeyStoreStream = getClass().getResourceAsStream(certDir+"client.jks")){
                identityKeyStore.load(identityKeyStoreStream, clientCertPassword.toCharArray());

                KeyStore trustKeyStore = KeyStore.getInstance("jks");
                try (InputStream trustKeyStoreStream = getClass().getResourceAsStream(certDir+"trust.jks")){
                    trustKeyStore.load(trustKeyStoreStream, clientTrustCertPassword.toCharArray());

                    SSLContext sslContext = SSLContexts.custom()
                        // load identity keystore
                        .loadKeyMaterial(identityKeyStore, clientCertPassword.toCharArray(), new PrivateKeyStrategy() {
                            public String chooseAlias(Map<String, PrivateKeyDetails> aliases, Socket socket) {
                                return CERT_ALIAS;
                            }
                        })
                        // load trust keystore
                        .loadTrustMaterial(trustKeyStore, null)
                        .build();

                    SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
                        new String[]{"TLSv1.2", "TLSv1.1"},
                            null,
                            SSLConnectionSocketFactory.getDefaultHostnameVerifier());

                    CloseableHttpClient client = HttpClients.custom()
                        .setSSLSocketFactory(sslConnectionSocketFactory)
                        .build();

                    // Call a SSL-endpoint
                    callEndPoint(client, serverUrl,
                        new JSONObject()
                                .put("param1", "value1")
                                .put("param2", "value2")
                    );
                }
            }
        } catch (Exception ex) {
            System.out.println("Boom, we failed: " + ex);
            ex.printStackTrace();
        }
    }

    private void callEndPoint(CloseableHttpClient aHTTPClient, String aEndPointURL, JSONObject aPostParams) {

        try {
            System.out.println("Calling URL: " + aEndPointURL);
            HttpPost post = new HttpPost(aEndPointURL);
            post.setHeader("Accept", "application/json");
            post.setHeader("Content-type", "application/json");

            StringEntity entity = new StringEntity(aPostParams.toString());
            post.setEntity(entity);

            System.out.println("**POST** request Url: " + post.getURI());
            System.out.println("Parameters : " + aPostParams);

            HttpResponse response = aHTTPClient.execute(post);

            int responseCode = response.getStatusLine().getStatusCode();
            System.out.println("Response Code: " + responseCode);
            System.out.println("Content:-\n");
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception ex) {
            System.out.println("Boom, we failed: " + ex);
            ex.printStackTrace();
        }
    }

}