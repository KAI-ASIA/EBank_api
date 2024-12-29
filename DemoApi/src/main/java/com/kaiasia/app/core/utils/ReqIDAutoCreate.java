package com.kaiasia.app.core.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.UUID;

@Component
public class ReqIDAutoCreate {
    @Autowired
    AppConfigPropertiesUtils appConfig;
    private String prefix;

    @PostConstruct
    public void init() throws Exception {
        String ip = InetAddress.getLocalHost().getHostAddress();
        String api = this.appConfig.getApiName();
        this.prefix = api + "_" + ip;
    }

    public String createIReqId(long sendTime, long timeout) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // Combine sendTime, timeout, and UUID
        return sendTime + "000" + "_" + timeout + "000" + "_" + uuid;
    }


    public String createReqId(long sendTime, long timeout) {
        String uuid = UUID.randomUUID().toString().replace("-", "");

        // Combine sendTime, timeout, and UUID
        return sendTime + "000" + "_" + timeout + "000" + "_" + uuid;
    }

    public long getTimeoutFromReqId(String reqId) {
        try {
            String[] items = reqId.split("_");
            String tail = items[1];
            String timeout = tail.substring(tail.length() - 3);
            return Long.parseLong(timeout);
        } catch (Exception var5) {
            return -1L;
        }
    }

    public long getSendTimeFromReqId(String reqId) {
        try {
            String[] items = reqId.split("_");
            String tail = items[0];
            String sendTime = tail.substring(0, tail.length() - 3);
            return Long.parseLong(sendTime);
        } catch (Exception var5) {
            return -1L;
        }
    }

}
