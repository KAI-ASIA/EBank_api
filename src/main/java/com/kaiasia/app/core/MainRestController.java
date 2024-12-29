package com.kaiasia.app.core;

import com.kaiasia.app.core.async.IProcess;
import com.kaiasia.app.core.async.IValidate;
import com.kaiasia.app.core.model.ApiBody;
import com.kaiasia.app.core.model.ApiError;
import com.kaiasia.app.core.model.ApiRequest;
import com.kaiasia.app.core.model.ApiResponse;
import com.kaiasia.app.core.utils.ReqIDAutoCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;

@RestController
public class MainRestController {

    private final Logger logger = LoggerFactory.getLogger(MainRestController.class);

    @Autowired
    IProcess process;
    @Autowired
    IValidate validate;
    @Autowired
    ReqIDAutoCreate apiReqIdCreator;
    @Autowired
    ProcessService processService;

    @RequestMapping(
            value = {"/process"},
            method = {RequestMethod.POST}
    )
    @CrossOrigin(
            origins = {"*"},
            allowedHeaders = {"*"}
    )
    @ResponseBody
    public ResponseEntity<ApiResponse> process(HttpServletRequest httpRequest, @RequestBody ApiRequest request) {
        long a = System.currentTimeMillis();
        String LOCATION = "";

        if ("REQUEST".equals(request.getHeader().getReqType())) {
            logger.info("MsgFromClient_Receive:{}", request);
        }

        ApiError error = this.processService.validate(request);
        ApiResponse response = new ApiResponse();
        if (ApiError.OK_CODE.equals(error.getCode())) {
            error = this.validate.validate(request);
        }

        if (!ApiError.OK_CODE.equals(error.getCode())) {
            response = new ApiResponse();
            response.setHeader(request.getHeader());
            response.setError(error);
            ApiBody body = new ApiBody();
            body.put("status", "FAILE");
            response.setBody(body);
        } else {
            request.set_sendTime(System.currentTimeMillis());
            request.set_command((String)request.getBody().get("command"));
            LinkedHashMap enquiry = (LinkedHashMap)request.getBody().get("enquiry");
            if (enquiry == null) {
                enquiry = (LinkedHashMap)request.getBody().get("transaction");
            }
            request.set_authenType((String)enquiry.get("authenType"));

            long timeout = 0L;

            request.set_timeOut(timeout);
            String reqId = this.apiReqIdCreator.createReqId(request.get_sendTime(), timeout);
            LOCATION = reqId + "#";
            request.set_reqId(reqId);
            this.logger.info("{}[API-BEGIN]", LOCATION);
            response = this.processService.pushApiReqToDB(request, LOCATION);
            if (response.getError() != null) {
                return new ResponseEntity(response, HttpStatus.OK);
            }

            if (response.getBody() != null && "OK".equals(response.getBody().get("status"))) {
                while(true) {
                    response = this.processService.getResponse(LOCATION, request, reqId);
                    if (response != null && "FAILE".equals(response.getBody().get("status")) || "ERROR".equals(response.getBody().get("status")) || "OK".equals(response.getBody().get("status"))) {
                        break;
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException var20) {
//                        var20.printStackTrace();
                    }
                }
            }
            this.logger.info(LOCATION + "#END");
            this.logger.info("{}MsgFromClient_Response:{}", LOCATION, response + "#Total time: " + (System.currentTimeMillis() - a));
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }

}
