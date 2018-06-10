# Running the application
```
mvn spring-boot:run
```

# curl commands  
http://venkateshragi.blogspot.com/2013/04/two-way-ssl-using-curl.html
```
cd src/main/resources/jsse
curl -v -d '{"info":"from client","details":{"some":"more"}}' --insecure --cert client.pem \
    --key client1234 https://user:password@localhost:8443/api/post | jq
```

# JSSE Key Stores

## Create
Only required if you make changes to the script
```
src/main/resources/jsse/gen-certs.sh
```

## Inspect 
```
keytool -list -v -keystore jsse/certs/client.jks
keytool -list -v -keystore jsse/certs/trust.jks -alias localhost
```

# Check SSL
```
openssl s_client -host server -port 8443
openssl s_client -showcerts -connect server:8443
openssl s_client -state -nbio -connect localhost:8443 2>&1 | grep "^SSL"
```

# SSL debugging JVM arguments
```
-Djavax.net.debug=ssl:handshake:verbose:keymanager:trustmanager
-Djava.security.debug=access:stack
```
