# Running the application
```
mvn spring-boot:run
```

# curl commands
```
cd src/main/resources/jsse
curl -v -d '{"info":"from client","details":{"some":"more"}}' --insecure --cert client.pem \
    --key client1234 https://user:password@localhost:8443/api/post | jq
```

# Creating JSSE Key Stores
Only reqired if you make changes
```
src/main/resources/jsse/gen-certs.sh
```
