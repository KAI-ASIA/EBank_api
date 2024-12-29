package com.kaiasia.app.utils;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;

public class ApiUtils {

    private static String currentHostName;

    public static String getCurrentHostName(){
        try {
            if(StringUtils.isBlank(currentHostName)){
                currentHostName = InetAddress.getLocalHost().getHostName();
            }
            return currentHostName;
        } catch (Exception e){
            try {
                currentHostName = InetAddress.getLocalHost().getHostAddress();
                return currentHostName;
            } catch (Exception ex){
                return "Unknown host";
            }
        }
    }
}
