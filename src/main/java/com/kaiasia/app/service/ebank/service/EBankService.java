package com.kaiasia.app.service.ebank.service;

import com.kaiasia.app.core.model.*;
import com.kaiasia.app.core.utils.GetErrorUtils;
import com.kaiasia.app.register.KaiMethod;
import com.kaiasia.app.register.KaiService;
import com.kaiasia.app.register.Register;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@KaiService
@Slf4j
public class EBankService {

    @Autowired
    GetErrorUtils apiErrorUtils;


    @KaiMethod(name = "getUSER_PROFILE", type = Register.VALIDATE)
    public ApiError validate(ApiRequest req){
        ApiBody body = req.getBody();
//        Object field = body.get("enquiry");
        Map<String , Object> field = (Map<String, Object>) body.get("enquiry");

        String sessionId = (String) field.get("sessionId");
        String userID = (String) field.get("userID");

        if(StringUtils.isBlank(sessionId)){
            return apiErrorUtils.getError("804", new String[]{"sessionId"});
        }else if(StringUtils.isBlank(userID)){
            return apiErrorUtils.getError("804", new String[]{"userID"});
        }else {
            return new ApiError(ApiError.OK_CODE,ApiError.OK_DESC);
        }
    }

    @KaiMethod(name = "getUSER_PROFILE")
    public ApiResponse process(ApiRequest req){
        ApiResponse apiResponse = new ApiResponse();

        ApiHeader header = req.getHeader();
        ApiBody body = req.getBody();
        Map<String, Object> bodyEnq = (Map<String, Object>) body.get("enquiry");

        String chanel = header.getChannel();
        String userID = (String) bodyEnq.get("userID");

        String location = System.currentTimeMillis()+"-"+chanel+"-"+userID;

        try {
//            Map<String, Object> bodyEnq = new HashMap<>();
            apiResponse.setBody(body);
            return apiResponse;
        } catch (Exception e){
            log.error(location,e);
            ApiError apiError = apiErrorUtils.getError("999", new String[]{e.getMessage()});
            apiResponse.setError(apiError);
            return apiResponse;
        }
    }
}
