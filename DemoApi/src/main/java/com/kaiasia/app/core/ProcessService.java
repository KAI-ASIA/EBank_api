package com.kaiasia.app.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaiasia.app.core.dao.IAPIDaoManager;
import com.kaiasia.app.core.model.*;
import com.kaiasia.app.core.utils.ApiConstant;
import com.kaiasia.app.core.utils.AppConfigPropertiesUtils;
import com.kaiasia.app.core.utils.GetErrorUtils;
import com.kaiasia.app.core.utils.ReqIDAutoCreate;
import com.kaiasia.app.utils.ApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;

@Service
@Slf4j
public class ProcessService {
    @Autowired
    IAPIDaoManager apiDaoManager;
    @Autowired
    AppConfigPropertiesUtils appConfig;
    @Autowired
    GetErrorUtils apiErrUtils;
    @Autowired
    ReqIDAutoCreate apiReqIDCreator;
    @Autowired
    private ObjectMapper mapper;

    private final Logger logger = LoggerFactory.getLogger(ProcessService.class);

    @Value("${kai.name}")
    private String api;

    public ProcessService() {
    }

    public ApiError validate(ApiRequest apiReq) {
        ApiError error = new ApiError();
        error.setCode(ApiError.OK_CODE);
        String apiName = this.appConfig.getApiName();
        String apiKey = this.appConfig.getApiKey();
        if (StringUtils.isEmpty(apiReq.getHeader().getApi())) {
            error = this.apiErrUtils.getError("701");
            return error;
        } else {
            if (!apiName.equals(apiReq.getHeader().getApi())) {
                error = this.apiErrUtils.getError("701");
                return error;
            }

            if (!apiKey.equals(apiReq.getHeader().getApiKey())) {
                error = this.apiErrUtils.getError("700");
                return error;
            }
            if (!"REQUEST".equals(apiReq.getHeader().getReqType()) && !"RESPONSE".equals(apiReq.getHeader().getReqType())) {
                error = this.apiErrUtils.getError("801");
                return error;
            } else if (StringUtils.isEmpty(apiReq.getHeader().getChannel())) {
                error = this.apiErrUtils.getError("804", new String[]{"channel"});
                return error;
            }  else if (StringUtils.isEmpty(apiReq.getHeader().getRequestAPI())) {
                error = this.apiErrUtils.getError("804", new String[]{"requestAPI"});
                return error;
            } else if (StringUtils.isEmpty(apiReq.getHeader().getRequestNode())) {
                error = this.apiErrUtils.getError("804", new String[]{"requestNode"});
                return error;
            } else if (apiReq.getBody() == null) {
                error = this.apiErrUtils.getError("804", new String[]{"body"});
                return error;
            } else {
                if ("REQUEST".equals(apiReq.getHeader().getReqType())) {
                    String command = (String)apiReq.getBody().get("command");
                    if (StringUtils.isEmpty(command)) {
                        error = this.apiErrUtils.getError("805", new String[]{"command"});
                        return error;
                    }

                    if (!"GET_ENQUIRY".equals(command) && !"GET_TRANSACTION".equals(command)) {
                        error = this.apiErrUtils.getError("805", new String[]{command});
                        return error;
                    }

                    LinkedHashMap enquiryOrTransaction = (LinkedHashMap)apiReq.getBody().get("enquiry");
                    if (enquiryOrTransaction == null) {
                        enquiryOrTransaction = (LinkedHashMap)apiReq.getBody().get("transaction");
                    }

                    if (enquiryOrTransaction != null) {
                        String authenType = (String)enquiryOrTransaction.get("authenType");
                        if (StringUtils.isEmpty(authenType)) {
                            error = this.apiErrUtils.getError("804", new String[]{"authenType"});
                            return error;
                        }
                    }
                }

                if ("RESPONSE".equals(apiReq.getHeader().getReqType())) {
                    ApiBody body = apiReq.getBody();
                    String reqId = null;
                    if (body != null) {
                        reqId = (String)body.get("reqID");
                    }

                    if (StringUtils.isEmpty(reqId)) {
                        error = this.apiErrUtils.getError("803");
                        return error;
                    }

                    long sendTime = this.apiReqIDCreator.getSendTimeFromReqId(reqId);
                    long timeout = this.apiReqIDCreator.getTimeoutFromReqId(reqId);
                    if (sendTime <= 0L || timeout <= 0L) {
                        return this.apiErrUtils.getError("802", new String[]{"reqID"});
                    }
                }

                return error;
            }
        }
    }

    public ApiResponse pushApiReqToDB(ApiRequest apiReq, final String LOCATION) {
        long a = System.currentTimeMillis();
        ApiResponse response = new ApiResponse();
        response.setHeader(apiReq.getHeader());

        try {
            ApiRequestBean requestBean = new ApiRequestBean();
            ApiResponseBean responseBean = new ApiResponseBean();
            createRequestBean(apiReq, requestBean, responseBean);
            this.apiDaoManager.insertNewApiRequest(requestBean, responseBean);
            ApiBody body = new ApiBody();
            body.put("reqID", apiReq.get_reqId());
            body.put("status", "OK");
            response.setBody(body);
        } catch (Exception var8) {
            this.logger.error("{}{}", "Exception", var8);
            ApiError err = this.apiErrUtils.getError("996", new String[]{var8.getMessage()});
            response.setError(err);
        }

        this.logger.info(LOCATION + "#pushApiReqToQueue#Duration:" + (System.currentTimeMillis() - a));
        return response;
    }

    public void createRequestBean(ApiRequest apiReq, ApiRequestBean bean, ApiResponseBean apiRes2Insert) throws Exception {
        int priorityMax = 1;
        if (apiReq.getHeader().getPriority() < priorityMax) {
            apiReq.getHeader().setPriority(priorityMax);
        }

        String reqMsg = mapper.writeValueAsString(apiReq);

        //create request_in
        bean.setReqId(apiReq.get_reqId());
        bean.setPriority(apiReq.getHeader().getPriority());
        bean.setReceiveTime(new Date());
        bean.setRequestMsg(reqMsg);
        bean.setRequestAPI(apiReq.getHeader().getRequestAPI());
        bean.setRequestNode(apiReq.getHeader().getRequestNode());
        bean.setStatus(ApiConstant.STATUS.RECEIVE);
        bean.setTimeout(60);

        apiRes2Insert.setStatus(ApiConstant.STATUS.WAITING);
        apiRes2Insert.setReqId(apiReq.get_reqId());
        apiRes2Insert.setReceiveTime(bean.getReceiveTime());
        apiRes2Insert.setRequestMsg(reqMsg);
        apiRes2Insert.setRequestApi(apiReq.getHeader().getRequestAPI());
        apiRes2Insert.setRequestNode(apiReq.getHeader().getRequestNode());
        apiRes2Insert.setReceiveNode(ApiUtils.getCurrentHostName());
    }

    public ApiRequest getRequest(ApiRequestBean apiRequestBean) {
        try {
            ApiRequest rq = mapper.readValue(apiRequestBean.getRequestMsg(), ApiRequest.class);
            rq.set_reqId(apiRequestBean.getReqId());
            return rq;
        } catch (Exception var4) {
            this.logger.error(var4.toString());

            try {
                Thread.sleep(10000L);
            } catch (Exception var3) {
            }

            return null;
        }
    }

    public int checkReqTimeInQueue(ApiRequestBean request) {
        long t = System.currentTimeMillis() - request.getReceiveTime().getTime();
        return request.getTimeout() > 0L && (double)t > 0.6666666666666666D * (double)request.getTimeout() * 1000.0D ? 1 : 0;
    }

    public ApiResponse getResponse(String prefix, ApiRequest apiRequest, String reqId) {
        ApiResponse res = null;
        try{
            ApiResponseBean apiResponseBean = this.apiDaoManager.getResponse(reqId);
            if(apiResponseBean != null && apiResponseBean.getResponseMsg() != null){
                res = mapper.readValue(apiResponseBean.getResponseMsg(), ApiResponse.class);
            }
        }catch (Exception var1){
//            System.out.println(var1);
        }

        if (res != null) {
            return res;
        } else {
            long sendTime = this.apiReqIDCreator.getSendTimeFromReqId(reqId);
            long timeout = 60l;
            if (sendTime > 0L && timeout >= 0L) {
                long current = System.currentTimeMillis();
                long duration = current - sendTime;
                if (timeout > 0L && duration >= timeout * 1000L) {
                    this.logger.error("{}{}", prefix, "Return timeout, because client get response at time over timeout value");
                    ApiResponse rs = new ApiResponse();
                    rs.setHeader(apiRequest.getHeader());
                    ApiError err = this.apiErrUtils.getTimeoutErr();
                    rs.setError(err);
                    ApiBody body = new ApiBody();
                    body.put("status", "FAILE");
                    rs.setBody(body);
                    return rs;
                } else {
                    ApiResponse rs = new ApiResponse();
                    rs.setHeader(apiRequest.getHeader());
                    ApiBody body = new ApiBody();
                    body.put("status", "PROCESS");
                    rs.setBody(body);
                    return rs;
                }
            } else {
                this.logger.error("{}{}", prefix, "ReqID not in validate format");
                ApiResponse rs = new ApiResponse();
                rs.setHeader(apiRequest.getHeader());
                rs.setError(apiErrUtils.getError("803"));
                ApiBody body = new ApiBody();
                body.put("status", "FAILE");
                rs.setBody(body);
                return rs;
            }
        }
    }

    public void updateResAfterProcessed(ApiRequestBean requestBean, ApiResponse res) throws Exception {
        ApiResponseBean apiResponseBean = new ApiResponseBean();
        apiResponseBean.setReqId(requestBean.getReqId());
        apiResponseBean.setStatus(ApiConstant.STATUS.DONE);
        apiResponseBean.setResponseMsg(mapper.writeValueAsString(res));
        apiResponseBean.setEndProcessTime(new Date());
        this.apiDaoManager.updateResAfterProcessed(apiResponseBean);
    }
}
