package com.kaiasia.app.service.ebank.config;

import javafx.beans.DefaultProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dep-api")
@Data
public class DepAipConfig {
    @Autowired
    Environment env;

    DepApiPropeties authApi;
    DepApiPropeties t24utilsApi;

    public DepApiPropeties getApiProperties(String name) {
        String prefix = "dep-api." + name;
        return DepApiPropeties
                .builder()
                .url(env.getProperty(prefix + ".url"))
                .apiKey(env.getProperty(prefix + ".apiKey"))
                .apiName(env.getProperty(prefix + ".apiName"))
                .timeout(Long.parseLong(StringUtils.defaultString(env.getProperty(prefix + ".timeout"))))
                .build();
    }
}
