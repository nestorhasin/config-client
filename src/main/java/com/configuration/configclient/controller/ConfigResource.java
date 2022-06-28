package com.configuration.configclient.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.configuration.configclient.service.ConfigService;

import lombok.extern.slf4j.Slf4j;

@RefreshScope
@RestController
@RequestMapping("/config")
@Slf4j
public class ConfigResource {

    @Autowired
    private ConfigService configService;

    @GetMapping("/property/{propName}")
    public Object getProperty(@PathVariable("propName") String propertyName) {
        return configService.getPropertyAsString(propertyName);
    }

    @PostMapping(path="/reset")
    public ResponseEntity<?> doRefresh(@RequestBody String body){
        Integer responseCode = 0;
        
        try {
            final String POST_PARAMS = "";
            log.info("POST_PARAMS: {}", POST_PARAMS);
            
            URL obj = new URL("https://localhost:8888/actuator/bus-refresh");
            HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
                postConnection.setRequestMethod("POST");
                postConnection.setRequestProperty("Content-Type", "application/json");
                postConnection.setDoOutput(true);
            
            OutputStream os = postConnection.getOutputStream();
                os.write(POST_PARAMS.getBytes());
                os.flush();
                os.close();
            
            responseCode = postConnection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_CREATED) { // SUCCESS
                BufferedReader in = new BufferedReader
                    (new InputStreamReader(postConnection.getInputStream()));
            
                String inputLine;
                
                StringBuffer response = new StringBuffer();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
    
                log.info("response: {}", response.toString());
                log.info("— — — - Configuration Refreshed Successfully — — — -");
                
            } else {
                log.warn("— — — - Configuration Refreshed Failure — — — -");
            }
    
        } catch (Exception ex){
            log.error("Error: {}", ex.getMessage());
        }

    return new ResponseEntity<>("Configuration Refreshed Sucessfully", HttpStatus.OK);
    }
}