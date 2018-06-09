#!/bin/bash

cd $(dirname "$0")
rm -fR certs
mkdir certs
cd certs

# Creating JSSE Key Stores
# https://www.naschenweng.info/2018/02/01/java-mutual-ssl-authentication-2-way-ssl-authentication/
# https://www.calazan.com/how-to-convert-a-java-keystore-jks-to-pem-format/

CLIENTSTORE_PASS=client1234
TRUSTSTRORE_PASS=trust1234
SERVERSTORE_PASS=server1234

## Create client and trust jks 

# > client.jks
keytool -genkey -alias client -keyalg RSA -keystore client.jks -keysize 2048 \
	-deststoretype pkcs12 -storepass $CLIENTSTORE_PASS \
	-dname "CN=localhost, OU=unknown, O=unknown, L=unknown, S=unknown, C=unknown"
# > client.cer 
keytool -export -alias client -file client.cer -keystore client.jks -storepass $CLIENTSTORE_PASS
# > client.crt
keytool -export -alias client -file client.crt -keystore client.jks -storepass $CLIENTSTORE_PASS
# > clienttrust.jks
keytool -noprompt -import -v -trustcacerts -alias client -file client.crt -keystore trust.jks \
	-deststorepass $TRUSTSTRORE_PASS

## Create server jks 
# > server.jks
keytool -genkey -alias localhost -keyalg RSA -keystore server.jks -keysize 2048 \
	-deststoretype pkcs12 -storepass $SERVERSTORE_PASS \
	-dname "CN=localhost, OU=unknown, O=unknown, L=unknown, S=unknown, C=unknown" \
	-ext SAN=dns:localhost,dns:server,ip:127.0.0.1
# > server.crt
keytool -export -alias localhost -file server.crt -keystore server.jks -storepass $SERVERSTORE_PASS
keytool -noprompt -import -v -trustcacerts -alias localhost -file server.crt -keystore trust.jks \
	-deststorepass $TRUSTSTRORE_PASS


## Import postman-echo.com certificate into trust store
# 1) Create crt > postman-echo.com.crt
echo -n | openssl s_client -showcerts -servername postman-echo.com \
	-connect postman-echo.com:443 </dev/null \
	| sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' \
	| tee "postman-echo.com.crt"
 
# 2) Import the server certificate into Truststore
keytool -noprompt -import -alias postman-echo.com -file postman-echo.com.crt -keystore trust.jks \
	-deststorepass $TRUSTSTRORE_PASS

## Convert JKS to PEM for curl etc.
# 1) create p12 for openssl > client.p12
keytool -noprompt -importkeystore -srcstorepass $CLIENTSTORE_PASS -srckeystore client.jks \
	-srcalias client -srcstoretype jks \
	-deststorepass $CLIENTSTORE_PASS -destkeystore client.p12 -deststoretype pkcs12
# 2) create pem
openssl pkcs12 -in client.p12 -out client.pem -passin pass:$CLIENTSTORE_PASS -passout pass:$CLIENTSTORE_PASS

cd -
