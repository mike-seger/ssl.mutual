#!/bin/bash

CLIENTSTORE_PASS=client1234
SERVERSTORE_PASS=server1234
TRUSTSTORE_PASS=trust1234

cd $(dirname "$0")

rm -fR certs
mkdir certs
cd certs
openssl genrsa -out clientprivatekey.key 2048
openssl req -new -nodes -x509 -key clientprivatekey.key -out clientcert.pem -days 36500 \
	-config ../openssl.config
#	-subj "/C=CH/ST=Zurich/L=Zurich/O=Example Company/OU=Department/CN=example.com"
openssl pkcs12 -export -name client -passout pass:$CLIENTSTORE_PASS \
               -in clientcert.pem -inkey clientprivatekey.key \
               -out clientkeystore.p12
keytool -noprompt -importkeystore -destkeystore client.jks -deststoretype pkcs12 \
        -srckeystore clientkeystore.p12 -srcstoretype pkcs12 -srcstorepass $CLIENTSTORE_PASS \
        -alias client -deststorepass $CLIENTSTORE_PASS

openssl genrsa -out serverprivatekey.key 2048
openssl req -new -nodes -x509 -key serverprivatekey.key -out servercert.pem -days 36500 \
	-config ../openssl.config
openssl pkcs12 -export -name localhost -passout pass:$SERVERSTORE_PASS \
        -in servercert.pem -inkey serverprivatekey.key \
        -out serverkeystore.p12
keytool -noprompt -importkeystore -destkeystore server.jks -deststoretype pkcs12 \
        -srckeystore serverkeystore.p12 -srcstoretype pkcs12 -srcstorepass $SERVERSTORE_PASS \
        -alias localhost -deststorepass $SERVERSTORE_PASS

keytool -noprompt -import -alias client -srcstorepass $CLIENTSTORE_PASS \
        -file clientcert.pem -deststorepass $TRUSTSTORE_PASS -keystore trust.jks
keytool -noprompt -import -alias localhost -srcstorepass $SERVERSTORE_PASS \
        -file servercert.pem -deststorepass $TRUSTSTORE_PASS -keystore trust.jks

echo -n | openssl s_client -showcerts -servername postman-echo.com \
	-connect postman-echo.com:443 </dev/null \
	| sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' \
	| tee "postman-echo.com.crt"

# 2) Import the server certificate into Truststore
keytool -noprompt -import -alias postman-echo.com -file postman-echo.com.crt -keystore trust.jks \
	-trustcacerts -deststorepass $TRUSTSTORE_PASS

#
#openssl pkcs12 -export -out serverkeystore.pkcs12 -in servercert.pem -inkey serverprivatekey.key \
#	-name localhost -passout pass:$SERVERSTORE_PASS
#openssl x509 -inform pem -in servercert.pem -out servercert.crt
#
#openssl pkcs12 -export -out clientkeystore.pkcs12 -in clientcert.pem -inkey clientprivatekey.key \
#	-name client -passout pass:$CLIENTSTORE_PASS
#openssl x509 -inform pem -in clientcert.pem -out clientcert.crt
#
#keytool -importkeystore -srcstorepass $CLIENTSTORE_PASS -srckeystore clientkeystore.pkcs12 -srcstoretype PKCS12 \
#	-destkeystore client.jks -deststoretype PKCS12 -deststorepass $CLIENTSTORE_PASS
#keytool -importkeystore -srcstorepass $SERVERSTORE_PASS -srckeystore serverkeystore.pkcs12 -srcstoretype PKCS12 \
#	-destkeystore server.jks -deststoretype PKCS12 -deststorepass $SERVERSTORE_PASS
#
#keytool -import -file clientcert.pem -alias client -keystore trust.jks -storepass $TRUSTSTORE_PASS
#keytool -import -file servercert.pem -alias localhost -keystore trust.jks -storepass $TRUSTSTORE_PASS

cd -