package com.kaiasia.app.core.job;

import com.kaiasia.app.core.ProcessThread;
import com.kaiasia.app.core.async.IProcess;
import com.kaiasia.app.core.async.KaiTask;
import com.kaiasia.app.core.async.KaiThreadMain;
import com.kaiasia.app.core.dao.IAPIDaoManager;
import com.kaiasia.app.core.model.ApiRequestBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class ProcessThreadMain extends KaiThreadMain<ApiRequestBean> {

    @PostConstruct
    public void initThreadPoll(){this.init();}

    public ProcessThreadMain(@Value("${kai.threadName}") String threadName, @Value("${kai.pollSize}") int size){
        super(threadName, size);
    }

    @Autowired
    private ProcessThread processThread;

    @Override
    public KaiTask<ApiRequestBean> getKaiTask(ApiRequestBean apiRequestBean) {
        return new ApiResTask(processThread, apiRequestBean);
    }

    @Override
    public boolean checkToRun(ApiRequestBean apiRequestBean) {
        return true;
    }
}
