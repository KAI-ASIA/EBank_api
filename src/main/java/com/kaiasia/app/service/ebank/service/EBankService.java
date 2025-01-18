package com.kaiasia.app.service.ebank.service;

import com.kaiasia.app.core.utils.GetErrorUtils;
import com.kaiasia.app.register.KaiMethod;
import com.kaiasia.app.register.KaiService;
import com.kaiasia.app.register.Register;
import com.kaiasia.app.service.ebank.model.EBankReq;
import com.kaiasia.app.service.ebank.utils.Mapper;
import com.kaiasia.app.service.ebank.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import ms.apiclient.authen.AuthRequest;
import ms.apiclient.authen.AuthTakeSessionResponse;
import ms.apiclient.authen.AuthenClient;
import ms.apiclient.model.*;
import ms.apiclient.t24util.T24CustomerInfoResponse;
import ms.apiclient.t24util.T24Request;
import ms.apiclient.t24util.T24UserInfoResponse;
import ms.apiclient.t24util.T24UtilClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestClientException;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@KaiService
@Slf4j
public class EBankService {

    @Value("${spring.redis.ttl}")
    private long ttl;

    @Autowired
    private GetErrorUtils apiErrorUtils;

    @Autowired
    private T24UtilClient t24UtilClient;

    @Autowired
    private AuthenClient authenClient;

    @Autowired
    private RedisUtils redisUtils;

    @KaiMethod(name = "getUSER_PROFILE", type = Register.VALIDATE)
    public ApiError validate(ApiRequest req) throws Exception  {

        ApiBody body = req.getBody();

        if (body == null) {
            return  apiErrorUtils.getError("804", new String[]{"Body"});
        }

        EBankReq eBankReq = Mapper.fromObject(req.getBody().get("enquiry"),EBankReq.class);

        if (StringUtils.isBlank(eBankReq.getSessionId())) {
            return apiErrorUtils.getError("804", new String[]{"sessionId"});
        }

        if (StringUtils.isBlank(eBankReq.getUserID())) {
            return apiErrorUtils.getError("804", new String[]{"userID"});
        }

        return new ApiError(ApiError.OK_CODE, ApiError.OK_DESC);
    }

    @KaiMethod(name = "getUSER_PROFILE")
    public ApiResponse process(ApiRequest req) throws Exception {
        ApiResponse apiResponse = new ApiResponse();
        ApiBody body = new ApiBody();
        ApiHeader header = req.getHeader();

        EBankReq eBankReq = Mapper.fromObject(req.getBody().get("enquiry"),EBankReq.class);

        String chanel = header.getChannel();
        long time = System.currentTimeMillis();

        apiResponse.setHeader(header);

        String location = time + "-" + chanel + "-" + eBankReq.getUserID();

        String key = "EBank" + eBankReq.getSessionId() + "-" + eBankReq.getUserID();

        log.info(location + "#BEGIN GET CACHE");
        // get cache
        ApiResponse cache = redisUtils.getCache(key);
        if (cache != null){
            log.info(location + "#GET CACHE SUCCESSFULLY");
            return cache;
        }
        log.info(key + "#CACHE MISSING");

        log.info(location + "#BEGIN CALL SESSION");
        //take session
        AuthTakeSessionResponse authTakeSessionResponse = authenClient.takeSession(
                    location,
                    AuthRequest
                            .builder()
                            .sessionId(eBankReq.getSessionId())
                            .build(),
                    req.getHeader()
             );

        if(authTakeSessionResponse.getError() != null && !ApiError.OK_CODE.equals(authTakeSessionResponse.getError().getCode())){
            apiResponse.setError(authTakeSessionResponse.getError());
            log.info(location + "#END CALL SESSION" + (System.currentTimeMillis() - time) + "ERROR - {}",authTakeSessionResponse.getError());
            return apiResponse;
        }

        // call t24 ebank
        log.info(location + "#BEGIN CALL USER INFO");

        T24UserInfoResponse t24UserInfoResponse =  t24UtilClient.getUserInfo(
                location,
                T24Request
                        .builder()
                        .username(eBankReq.getUserID())
                        .build(),
                req.getHeader()
        );

        if(t24UserInfoResponse.getError() != null){
            ApiError apiError = new ApiError(t24UserInfoResponse.getError().getCode(),t24UserInfoResponse.getError().getDesc());
            apiResponse.setError(apiError);
            log.info(location + "#END CALL USER INFO" + (System.currentTimeMillis() - time));
            return apiResponse;
        }


        log.info(location + "#BEGIN CALL CUSTOMER INFO");
        T24CustomerInfoResponse t24CustomerInfoResponse = t24UtilClient.getCustomerInfo(
                location,
                T24Request
                        .builder()
                        .customerId(t24UserInfoResponse.getCustomerId())
                        .build(),
                req.getHeader()
        );

        if(t24CustomerInfoResponse.getError() != null){
            ApiError apiError = new ApiError(t24CustomerInfoResponse.getError().getCode(),t24CustomerInfoResponse.getError().getDesc());
            apiResponse.setError(apiError);
            log.info(location + "#END CALL CUSTOMER INFO" + (System.currentTimeMillis() - time));
            return apiResponse;
        }



        HashMap<String , Object> field = new HashMap<>();
        field.put("customerID",t24UserInfoResponse.getCustomerId());
        field.put("responseCode","00");
        field.put("customerType",t24UserInfoResponse.getCustomerType());
        field.put("company",t24UserInfoResponse.getCompany());
        field.put("nationality",t24CustomerInfoResponse.getCountry());
        field.put("phone",t24UserInfoResponse.getPhone());
        field.put("email",t24UserInfoResponse.getEmail());
        field.put("mainAccount",t24UserInfoResponse.getMainAccount());
        field.put("name",t24UserInfoResponse.getName());
        field.put("trustedType","---");
        field.put("lang",t24UserInfoResponse.getLanguage());
        field.put("startDate","----");
        field.put("endDate","----");
        field.put("pwDate",t24UserInfoResponse.getPwDate());
        field.put("userLock","---");
        field.put("packAge","---");
        field.put("userStatus",t24UserInfoResponse.getUserStatus());

        header.setReqType("RESPONE");
        body.put("enquiry",field);
        apiResponse.setBody(body);

        redisUtils.setTimeToLive(key,apiResponse,ttl);

        return apiResponse;
    }
}
