package com.kaiasia.app.core.model;

import lombok.Data;

import java.util.Date;

@Data
public class ApiRequestBean {
    private String api;
    private String reqId;
    private int priority;
    private Date receiveTime;
    private String requestMsg;
    private String requestNode;
    private String requestAPI;
    private String status;
    private String clientIp;
    private int timeout;
    private String authenType;
}
