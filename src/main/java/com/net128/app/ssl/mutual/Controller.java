package com.net128.app.ssl.mutual;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.Charsets;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Date;

@RestController
@RequestMapping("/api")
public class Controller {
    private final static Logger logger= LoggerFactory.getLogger(Controller.class);
    private ObjectMapper mapper = new ObjectMapper();;

    @PostMapping("post")
    public ResponseEntity<?> post(@RequestBody String postBody) throws IOException {
        String requestJson=URLDecoder.decode(postBody, Charsets.UTF_8.name()).replaceAll("=$","");
        logger.info(requestJson);
        return new ResponseEntity<>(new Object() {
            public String response="hello";
            public Date ts=new Date();
            public Object request=mapper.readValue(requestJson, Object.class);
        }, HttpStatus.CREATED);
    }
}
