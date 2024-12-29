package com.kaiasia.app.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(
        ignoreUnknown = true
)
@Data
public class ApiHeader {
    private String reqType;
    private String api;
    private String apiKey;
    private String channel;
    private String location;
    private String requestAPI;
    private String requestNode;
    private Long duration;
    private int priority;
    private String context;
    public String synasyn = "false";

    public int getPriority() {
        if (this.priority == 0) {
            this.priority = 3;
        }

        return this.priority;
    }
}