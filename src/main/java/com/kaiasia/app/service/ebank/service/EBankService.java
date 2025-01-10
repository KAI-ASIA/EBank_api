package com.kaiasia.app.service.ebank.service;

import com.kaiasia.app.core.utils.GetErrorUtils;
import com.kaiasia.app.register.KaiMethod;
import com.kaiasia.app.register.KaiService;
import com.kaiasia.app.register.Register;
import com.kaiasia.app.service.ebank.model.EBankReq;
import com.kaiasia.app.service.ebank.utils.Mapper;
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
    private RedisTemplate<String , Object> redisTemplate;

    @KaiMethod(name = "getUSER_PROFILE", type = Register.VALIDATE)
    public ApiError validate(ApiRequest req)  {
        ApiBody body = req.getBody();

        if (body == null) {
            return  apiErrorUtils.getError("804", new String[]{"Body"});
        }

        Map<String ,Object> field = (Map<String, Object>) req.getBody().get("enquiry");
        String sessionId = (String) field.get("sessionId");
        String userID = (String) field.get("userID");

        if (StringUtils.isBlank(sessionId)) {
            return apiErrorUtils.getError("804", new String[]{"sessionId"});
        }

        if (StringUtils.isBlank(userID)) {
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
        ApiResponse cache = getCache(key);
        if (cache != null){
            log.info(location + "#GET CACHE SUCCESSFULLY");
            return cache;
        }
        log.info(key + "#CACHE MISSING");

//        log.info(location + "#BEGIN CALL SESSION");
//        //take session
//        AuthTakeSessionResponse authTakeSessionResponse = null;
//        try{
//             authTakeSessionResponse = authenClient.takeSession(
//                    location,
//                    AuthRequest
//                            .builder()
////                            .sessionId(eBankReq.getSessionId())
//                            .sessionId("158963500-20170110135803-1484031483542")
//                            .build(),
//                    req.getHeader()
//            );
//        }catch (RestClientException e) {
//            ApiError error = apiErrorUtils.getError("505",new String[]{e.getMessage()});
//            apiResponse.setError(error);
//            return apiResponse;
//        }
//


        // call t24
        log.info(location + "#BEGIN CALL USER INFO");

        T24UserInfoResponse t24UserInfoResponse =  t24UtilClient.getUserInfo(
                location,
                T24Request
                        .builder()
//                            .username(authTakeSessionResponse.getUsername())
                        .username("28169200")
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

        setTimeToLive(key,apiResponse,ttl);

        return apiResponse;
    }

    public ApiResponse getCache(String key){
        if(key == null || key.trim().isEmpty()){
            log.info("invalid key :{}",key);
            return null;
        }

        try{
            Object cache = redisTemplate.opsForValue().get(key);
            if(cache != null){
                log.info("cache get User info :{}", key);
                return (ApiResponse) cache;
            }else {
                log.info("Cache miss for key: {}", key);
            }
        }catch (Exception e){
            log.info("Redis error :{}",e.getMessage());
        }

        return  null;
    }

    public void setTimeToLive(String key ,ApiResponse apiResponse, long time){
        redisTemplate.opsForValue().set(key,apiResponse,time,TimeUnit.SECONDS);
        log.info("Save {} in cache }",key);
    }
}
