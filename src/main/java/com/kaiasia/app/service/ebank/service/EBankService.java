package com.kaiasia.app.service.ebank.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaiasia.app.core.model.*;
import com.kaiasia.app.core.utils.GetErrorUtils;
import com.kaiasia.app.register.KaiMethod;
import com.kaiasia.app.register.KaiService;
import com.kaiasia.app.register.Register;
import com.kaiasia.app.service.ebank.model.EBankReq;
import com.kaiasia.app.service.ebank.utils.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@KaiService
@Slf4j
public class EBankService {

    @Autowired
    GetErrorUtils apiErrorUtils;



    @KaiMethod(name = "getUSER_PROFILE", type = Register.VALIDATE)
    public ApiError validate(ApiRequest req)  {
        ApiBody body = req.getBody();

        if (body == null) {
            return apiErrorUtils.getError("804", new String[]{"Body is required"});
        }

        ApiHeader header = req.getHeader();
        String chanel = header.getChannel();
        String location = "";
        EBankReq eBankReq = null;
        String sessionId = "";
        String userID = "";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            eBankReq = objectMapper.readValue((String) body.get("enquiry"), EBankReq.class);
            sessionId = eBankReq != null ? eBankReq.getSessionId() : null;
            userID = eBankReq != null ? eBankReq.getUserID() : null;
            location = System.currentTimeMillis() + "-" + chanel + "-" + userID;
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON at " + location, e);
            return apiErrorUtils.getError("600", new String[]{"Invalid JSON format"});
        }

        if (StringUtils.isBlank(sessionId)) {
            return apiErrorUtils.getError("804", new String[]{"sessionId is required"});
        }

        if (StringUtils.isBlank(userID)) {
            return apiErrorUtils.getError("804", new String[]{"userID is required"});
        }

        return new ApiError(ApiError.OK_CODE, ApiError.OK_DESC);
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
            log.error("Error processing at " + location,e);
            ApiError apiError = apiErrorUtils.getError("999", new String[]{e.getMessage()});
            apiResponse.setError(apiError);
            return apiResponse;
        }
    }
}
