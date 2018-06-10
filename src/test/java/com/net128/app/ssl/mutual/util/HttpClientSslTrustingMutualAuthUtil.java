package com.net128.app.ssl.mutual.util;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

public class HttpClientSslTrustingMutualAuthUtil {
    public static SSLConnectionSocketFactory createSSLConnectionSocketFactory(
            String keyStoreLocation, String keyStorePassword, String trustStoreLocation, String trustStorePassword)
                throws CertificateException, NoSuchAlgorithmException, IOException, UnrecoverableKeyException, KeyStoreException, KeyManagementException {
        KeyStore identityKeyStore = KeyStore.getInstance("jks");
        try (InputStream identityKeyStoreStream = HttpClientSslTrustingMutualAuthUtil.class.getResourceAsStream(keyStoreLocation)) {
            identityKeyStore.load(identityKeyStoreStream, keyStorePassword.toCharArray());
        }
        SSLContextBuilder builder=SSLContexts.custom();
        builder.loadKeyMaterial(identityKeyStore, keyStorePassword.toCharArray());

        if(trustStoreLocation !=null) {
            KeyStore trustKeyStore = KeyStore.getInstance("jks");
            try (InputStream trustKeyStoreStream = HttpClientSslTrustingMutualAuthUtil.class.getResourceAsStream(trustStoreLocation)) {
                trustKeyStore.load(trustKeyStoreStream, trustStorePassword.toCharArray());
            }
            builder.loadTrustMaterial(trustKeyStore, null);
        }

        SSLContext sslContext = builder.build();

        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
            new String[]{"TLSv1.2", "TLSv1.1"},null,
            SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        return sslConnectionSocketFactory;
    }
}
