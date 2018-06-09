#!/bin/bash

CLIENTSTORE_PASS=client1234
TRUSTSTORE_PASS=trust1234
SERVERSTORE_PASS=server1234

cd $(dirname "$0")

rm -fR certs
mkdir certs
cd certs
openssl genrsa -out clientprivatekey.pem 2048
openssl req -new -x509 -key clientprivatekey.pem -out clientcert.pem -days 36500 \
	-config ../openssl.config
#	-subj "/C=CH/ST=Zurich/L=Zurich/O=Example Company/OU=Department/CN=example.com"
openssl genrsa -out serverprivatekey.pem 2048
openssl req -new -x509 -key serverprivatekey.pem -out servercert.pem -days 36500 \
	-config ../openssl.config

openssl pkcs12 -export -out serverkeystore.pkcs12 -in servercert.pem -inkey serverprivatekey.pem \
	-name localhost -passout pass:$SERVERSTORE_PASS
openssl x509 -inform pem -in servercert.pem -out servercert.crt

openssl pkcs12 -export -out clientkeystore.pkcs12 -in clientcert.pem -inkey clientprivatekey.pem \
	-name client -passout pass:$CLIENTSTORE_PASS

keytool -importkeystore -srcstorepass $CLIENTSTORE_PASS -srckeystore clientkeystore.pkcs12 -srcstoretype PKCS12 \
	-destkeystore client.jks -deststoretype PKCS12 -deststorepass $CLIENTSTORE_PASS
keytool -importkeystore -srcstorepass $SERVERSTORE_PASS -srckeystore serverkeystore.pkcs12 -srcstoretype PKCS12 \
	-destkeystore server.jks -deststoretype PKCS12 -deststorepass $SERVERSTORE_PASS

keytool -importkeystore -srcstorepass $CLIENTSTORE_PASS -srckeystore clientkeystore.pkcs12 -srcstoretype PKCS12 \
	-destkeystore trust.jks -deststoretype PKCS12 -deststorepass $TRUSTSTORE_PASS -destkeypass $TRUSTSTORE_PASS
keytool -importkeystore -srcstorepass $SERVERSTORE_PASS -srckeystore serverkeystore.pkcs12 -srcstoretype PKCS12 \
	-destkeystore trust.jks -deststoretype PKCS12 -deststorepass $TRUSTSTORE_PASS -destkeypass $TRUSTSTORE_PASS \
	-trustcacerts #-srcalias localhost -destalias localhost

cd -