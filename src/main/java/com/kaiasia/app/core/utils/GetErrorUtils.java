package com.kaiasia.app.core.utils;

import ms.apiclient.model.ApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
public class GetErrorUtils {

    @Autowired
    AppConfigPropertiesUtils appConfig;

    public ApiError getError(String code) {
        String desc = this.appConfig.getProp("kai.error.code" + code);
        return new ApiError(code, desc);
    }

    public ApiError getError(String code, String[] params) {
        String desc = this.appConfig.getProp("kai.error.code" + code);
        if (params != null) {
            MessageFormat formater = new MessageFormat(desc);
            desc = formater.format(params);
        }

        return new ApiError(code, desc);
    }

    public ApiError getNotProcessErr() {
        return this.getError("997");
    }

    public ApiError getTimeoutErr() {
        return this.getError("998");
    }

}
