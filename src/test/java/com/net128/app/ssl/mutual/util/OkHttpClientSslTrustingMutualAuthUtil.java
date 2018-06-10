package com.net128.app.ssl.mutual.util;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class OkHttpClientSslTrustingMutualAuthUtil {
    private final static Logger logger= LoggerFactory.getLogger(OkHttpClientSslTrustingMutualAuthUtil.class);
    private static KeyStore readKeyStore(String keyStoreLocation, String keyStorePass) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        char[] password = keyStorePass.toCharArray();
        try (InputStream is = OkHttpClientSslTrustingMutualAuthUtil.class.getResourceAsStream(keyStoreLocation)) {
            ks.load(is, password);
        }
        return ks;
    }

    public static OkHttpClient.Builder getClientBuilder(String keyStoreLocation, String keyStorePassword) {
        X509TrustManager x509TrustManager = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
                logger.debug("authType: {}", String.valueOf(authType));
            }

            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
                logger.debug("authType: {}", String.valueOf(authType));
            }
        };

        final TrustManager[] trustManagers = new TrustManager[]{x509TrustManager};

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        try {
//            KeyStore trustStore = readKeyStore(trusttStoreLocation, trustStorePass);
//            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//            trustManagerFactory.init(trustStore);

            KeyStore keyStore = readKeyStore(keyStoreLocation, keyStorePassword);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

            final String PROTOCOL = "SSL";
            SSLContext sslContext = SSLContext.getInstance(PROTOCOL);

            SecureRandom secureRandom = new SecureRandom();
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagers, secureRandom);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            okHttpClientBuilder.sslSocketFactory(sslSocketFactory, x509TrustManager);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HostnameVerifier hostnameVerifier = (hostname, session) -> {
            logger.debug("hostname: {}", String.valueOf(hostname));
            if (hostname.matches("(localhost|server|127.0.0.1|postman-echo.com)")) {
                return true;
            }
            return false;
        };

        okHttpClientBuilder.hostnameVerifier(hostnameVerifier);

        return okHttpClientBuilder;
    }
}
