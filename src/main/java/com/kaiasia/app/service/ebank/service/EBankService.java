package com.kaiasia.app.service.ebank.service;

import com.kaiasia.app.core.model.ApiBody;
import com.kaiasia.app.core.model.ApiError;
import com.kaiasia.app.core.model.ApiRequest;
import com.kaiasia.app.core.model.ApiResponse;
import com.kaiasia.app.core.utils.GetErrorUtils;
import com.kaiasia.app.register.KaiMethod;
import com.kaiasia.app.register.KaiService;
import com.kaiasia.app.register.Register;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@KaiService
@Slf4j
public class EBankService {

    @Autowired
    GetErrorUtils apiErrorUtils;


    @KaiMethod(name = "EBankService", type = Register.VALIDATE)
    public ApiError validate(ApiRequest req){
        ApiBody body = req.getBody();
//        Object field = body.get("enquiry");
        Map<String , Object> field = (Map<String, Object>) body.get("enquiry");
        String sessionId = (String) field.get("sessionId");
        if(sessionId == null && sessionId.isEmpty()){

        }
        return new ApiError(ApiError.OK_CODE, ApiError.OK_DESC);
    }

    @KaiMethod(name = "EBankService")
    public ApiResponse process(ApiRequest req){
        ApiResponse apiResponse = new ApiResponse();
        ApiBody apiBody = new ApiBody();
        try {
            Map<String, Object> bodyEnq = new HashMap<>();
            apiResponse.setBody(apiBody);
            return apiResponse;
        } catch (Exception e){
            log.error("Exception", e);
            ApiError apiError = apiErrorUtils.getError("999", new String[]{e.getMessage()});
            apiResponse.setError(apiError);
            return apiResponse;
        }
    }
}
