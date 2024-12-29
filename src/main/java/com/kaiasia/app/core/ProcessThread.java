package com.kaiasia.app.core;

import com.kaiasia.app.core.async.IProcess;
import com.kaiasia.app.core.dao.IAPIDaoManager;
import com.kaiasia.app.core.model.*;
import com.kaiasia.app.core.utils.AppConfigPropertiesUtils;
import com.kaiasia.app.core.utils.GetErrorUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
@Scope("prototype")
@Data
public class ProcessThread {

    @Autowired
    private IProcess process;
    @Autowired
    ProcessService processService;
    @Autowired
    private GetErrorUtils apiErrUtils;
    @Autowired
    AppConfigPropertiesUtils appConfigUtils;
    private boolean start = true;
    int threadIndex;
    private final Logger logger = LoggerFactory.getLogger(ProcessThread.class);

    public void process(ApiRequestBean apiRequestBean) {
        ApiRequest apiReq = this.processService.getRequest(apiRequestBean);
        if (apiReq == null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException var11) {
                var11.printStackTrace();
            }
        }

        ApiResponse res = null;

        long b = System.currentTimeMillis();
        int checkTimeInQueue = this.processService.checkReqTimeInQueue(apiRequestBean);
        this.logger.info("checkTimeInQueue: " + (System.currentTimeMillis() - b));
        if (1 == checkTimeInQueue) {
            res.setHeader(apiReq.getHeader());
            ApiError err = this.apiErrUtils.getNotProcessErr();
            err.setDesc(err.getDesc() + ". " + "Request wait in queue too long");
            ApiBody body = new ApiBody();
            body.put("status", "FAILE");
            res.setBody(body);
            res.setError(err);
        } else {
            res = this.process.process(apiReq);
            if (apiRequestBean.getTimeout() > 0L && System.currentTimeMillis() - apiRequestBean.getReceiveTime().getTime() >= apiRequestBean.getTimeout() * 1000L) {
                res.setHeader(apiReq.getHeader());
                ApiError err = this.apiErrUtils.getTimeoutErr();
                res.setError(err);
                ApiBody body = new ApiBody();
                body.put("status", "FAILE");
                res.setBody(body);
            }

        }

        long duration = System.currentTimeMillis() - apiRequestBean.getReceiveTime().getTime();
        res.getHeader().setDuration(duration);
        res.getHeader().setReqType("RESPONSE");

        try {
            long e = System.currentTimeMillis();
            this.processService.updateResAfterProcessed(apiRequestBean, res);
            this.logger.info(apiReq.get_reqId() + "#saveResponse#Duration: " + (System.currentTimeMillis() - e));
        } catch (Exception var12) {
            this.logger.error("saveResponseToRedis:{}", var12);
        }
    }

}
