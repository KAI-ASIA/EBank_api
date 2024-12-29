package com.kaiasia.app.core.job;

import com.kaiasia.app.core.ProcessThread;
import com.kaiasia.app.core.async.KaiTask;
import com.kaiasia.app.core.model.ApiRequestBean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResTask extends KaiTask<ApiRequestBean> {

    private final Logger logger = LoggerFactory.getLogger(ApiResTask.class);

    private ProcessThread process;
    private ApiRequestBean apiRequestBean;

    @Override
    public void execute() {
        try{
            process.process(apiRequestBean);
        }catch (Exception ex){
            logger.error("Exception process {}#{}", apiRequestBean, ex);
        }
    }

    @Override
    public void sleep(int threads) {

    }

    @Override
    public String executeResult() {
        return null;
    }
}
