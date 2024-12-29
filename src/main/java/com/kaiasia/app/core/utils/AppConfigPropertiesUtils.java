package com.kaiasia.app.core.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;

@Component
public class AppConfigPropertiesUtils {

    @Autowired
    Environment env;
    String ip;

    public AppConfigPropertiesUtils() {
    }

    public String getProp(String key) {
        return this.env.getProperty(key);
    }

    public String getApiName() {
        return this.env.getProperty("kai.name");
    }

    public String getApiKey() {
        return this.env.getProperty("kai.key");
    }

    public String getApiVersion() {
        return this.env.getProperty("kai.version");
    }

    @PostConstruct
    public void init() {
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            this.ip = ip;
        } catch (Exception var2) {
        }

    }

    public String getIp() {
        return this.ip;
    }

    public String getPort() {
        return this.env.getProperty("server.port");
    }
    
}
