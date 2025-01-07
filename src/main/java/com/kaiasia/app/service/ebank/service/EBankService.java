package com.kaiasia.app.service.ebank.service;

import com.kaiasia.app.core.utils.GetErrorUtils;
import com.kaiasia.app.register.KaiMethod;
import com.kaiasia.app.register.KaiService;
import com.kaiasia.app.register.Register;
import com.kaiasia.app.service.ebank.config.DepAipConfig;
import com.kaiasia.app.service.ebank.model.EBankReq;
import com.kaiasia.app.service.ebank.utils.Mapper;
import lombok.extern.slf4j.Slf4j;
import ms.apiclient.authen.AuthenClient;
import ms.apiclient.model.*;
import ms.apiclient.t24util.T24Request;
import ms.apiclient.t24util.T24UserInfoResponse;
import ms.apiclient.t24util.T24UtilClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


@KaiService
@Slf4j
public class EBankService {

    @Autowired
    private GetErrorUtils apiErrorUtils;

    @Autowired
    private DepAipConfig depAipConfig;

    @Autowired
    private T24UtilClient t24UtilClient;
    @Autowired
    private AuthenClient authenClient;

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

//        Map<String, Object> bodyEnq = (Map<String, Object>) body.get("enquiry");
//        String userID = (String) bodyEnq.get("userID");
        String chanel = header.getChannel();
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMddHHmmss");
        String location = date+"-"+chanel+"-"+eBankReq.getUserID();




        apiResponse.setHeader(header);

        //take session
//        AuthTakeSessionResponse authTakeSessionResponse = null;
//        try{
//             authTakeSessionResponse = authenClient.takeSession(
//                    location,
//                    AuthRequest
//                            .builder()
//                            .sessionId(eBankReq.getSessionId())
//                            .build(),
//                    req.getHeader()
//            );
//
//        }catch (Exception e){
//            throw new RestClientException(location, e);
//        }
//
//        if(authTakeSessionResponse.getError() != null){
//            log.error("Error processing at " + location,authTakeSessionResponse.getError().toString());
//            apiResponse.setError(authTakeSessionResponse.getError());
//            return apiResponse;
//        }



        // call t24
        T24UserInfoResponse t24UserInfoResponse =  null;
        try{
            t24UserInfoResponse =  t24UtilClient.getUserInfo(
                    location,
                    T24Request
                            .builder()
//                            .username(authTakeSessionResponse.getUsername())
                            .username("28169200")
                            .build(),
                    req.getHeader()
            );
        }catch (Exception e){
            throw new Exception();
        }

        if(t24UserInfoResponse.getError() != null){
            log.error("Error processing at " + location,t24UserInfoResponse.getError().toString());
            apiResponse.setError(t24UserInfoResponse.getError());
            return apiResponse;
        }



        HashMap<String , Object> field = new HashMap<>();
        field.put("customerID",t24UserInfoResponse.getCustomerId());
        field.put("responseCode","00");
        field.put("customerType",t24UserInfoResponse.getCustomerType());
        field.put("company",t24UserInfoResponse.getCompany());
        field.put("nationality","VN");
        field.put("phone",t24UserInfoResponse.getPhone());
        field.put("email",t24UserInfoResponse.getEmail());
        field.put("mainAccount",t24UserInfoResponse.getMainAccount());
        field.put("name",t24UserInfoResponse.getName());
        field.put("trustedType","SMS");
        field.put("lang",t24UserInfoResponse.getLanguage());
        field.put("startDate","----");
        field.put("endDate","----");
        field.put("pwDate",t24UserInfoResponse.getPwDate());
        field.put("userLock","NO");
        field.put("packAge","SUPPER");
        field.put("userStatus",t24UserInfoResponse.getUserStatus());

        header.setReqType("RESPONE");
        body.put("enquiry",field);
        apiResponse.setBody(body);
        return apiResponse;

//        DepApiPropeties AuthProperties = depAipConfig.getApiProperties("authApi");
//        ApiRequest auth1Req = new ApiRequest();
//        ApiHeader auth1HeadReq = new ApiHeader();
//        auth1HeadReq.setReqType("REQUEST");
//        auth1HeadReq.setApi(AuthProperties.getApiName());
//        auth1HeadReq.setApiKey(AuthProperties.getApiKey());
//        auth1HeadReq.setPriority(1);
//        auth1HeadReq.setChannel(header.getChannel());
//        auth1HeadReq.setLocation(header.getLocation());
//        auth1HeadReq.setRequestAPI(header.getRequestAPI());
//        auth1Req.setHeader(auth1HeadReq);
//        ApiBody auth1bodyReq = new ApiBody();
//        auth1bodyReq.put("command","GET_ENQUIRY");
//        Map<String,Object> auth1enquiry = new HashMap<>();
//        auth1enquiry.put("authenType","takeSession");
//        auth1enquiry.put("sessionId",bodyEnq.get("sessionId"));
//        auth1bodyReq.put("enquiry",auth1enquiry);
//        auth1Req.setBody(auth1bodyReq);
//
//        try {
//            ApiResponse auth1Response = ApiCallHelper.call(AuthProperties.getUrl(), HttpMethod.POST, Mapper.toJson(auth1Req), ApiResponse.class);
//            error = auth1Response.getError();
//            if (error != null && !"OK".equals(apiResponse.getBody().get("status"))){
//                log.error("Error processing at " + location,error);
//                apiResponse.setError(error);
//                return apiResponse;
//            }
//
//        } catch (Exception e){
//            log.error("Error processing at " + location,e);
//            ApiError apiError = apiErrorUtils.getError("999", new String[]{e.getMessage()});
//            apiResponse.setError(apiError);
//            return apiResponse;
//        }
//
////        DepApiPropeties T24Properties = depAipConfig.getT24utilsApi();
//        ApiRequest T24Request = new ApiRequest();
//        ApiHeader T24RequestHeader = new ApiHeader();
//        T24RequestHeader.setReqType("REQUEST");
//        T24RequestHeader.setApi("T24_UTIL_API");
//        T24RequestHeader.setApiKey(T24Properties.getApiKey());
//        T24RequestHeader.setPriority(1);
//        T24RequestHeader.setChannel(header.getChannel());
//        T24RequestHeader.setLocation(header.getLocation());
//        T24RequestHeader.setRequestNode("node 01");
//        T24Request.setHeader(T24RequestHeader);
//        ApiBody T24ReqBody = new ApiBody();



//        return apiResponse;
    }
}
