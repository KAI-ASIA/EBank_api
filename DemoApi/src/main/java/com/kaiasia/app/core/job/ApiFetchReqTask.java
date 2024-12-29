package com.kaiasia.app.core.job;

import com.kaiasia.app.core.dao.IAPIDaoManager;
import com.kaiasia.app.core.model.ApiRequestBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApiFetchReqTask implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(ApiFetchReqTask.class);
    @Autowired
    private IAPIDaoManager daoManager;
    @Autowired
    private ProcessThreadMain processThreadMain;
    @Value("${kai.pollSize}")
    private int maxTaskInQueue;

    private boolean start = true;

    @Override
    public void run() {
        boolean pause = false;
        while (start) {
            do {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (pause);

            if (processThreadMain.getTaskInQueue() < maxTaskInQueue) {
                List<ApiRequestBean> listReq = null;
                try{
                    listReq = daoManager.fetchAPIReqs(maxTaskInQueue - processThreadMain.getTaskInQueue());
                }catch (Exception e){
                    logger.error("EXCEPTION FETCH DB: {}", e);
                }
                if (listReq == null || listReq.isEmpty()) continue;
                System.out.println("FETCH_DATA_API" + ": " + listReq);
                for (ApiRequestBean apiRequestBean : listReq) {
                    processThreadMain.addQueue(apiRequestBean);
                }
            }
        }
    }
}

