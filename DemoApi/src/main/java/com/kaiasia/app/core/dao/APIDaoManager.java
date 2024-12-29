package com.kaiasia.app.core.dao;

import com.kaiasia.app.core.model.ApiRequestBean;
import com.kaiasia.app.core.model.ApiResponseBean;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Data
public class APIDaoManager implements IAPIDaoManager{

    private final Logger logger = LoggerFactory.getLogger(APIDaoManager.class);

    private IApiRequestDAO apiRequestDao;
    private IApiResponseDAO apiResponseDAO;


    @Override
    @Transactional
    public void insertNewApiRequest(ApiRequestBean apiReq, ApiResponseBean apiRes) throws Exception {
        String LOCATION = "InsertAPIReqs()" + apiReq.getReqId();
        logger.info(LOCATION + "#BEGIN");
        this.apiRequestDao.insert(apiReq);
        this.apiResponseDAO.insert(apiRes);
        logger.info(LOCATION + "#END");
    }

    @Override
    public List<ApiRequestBean> fetchAPIReqs(int limit) throws Exception {
        List<ApiRequestBean> listReqs = this.apiRequestDao.getReqs(limit);
        List<ApiRequestBean> result = new ArrayList();
        if (listReqs != null && listReqs.size() > 0) {
            List<String> ids = new ArrayList();
            List<String> ids_sla_error = new ArrayList();
            Date currentDate = new Date();
            long currentMs = System.currentTimeMillis();
            Iterator var10 = listReqs.iterator();

            while(var10.hasNext()) {
                ApiRequestBean apiRequestBean = (ApiRequestBean)var10.next();
                if (currentMs - apiRequestBean.getReceiveTime().getTime() >= (long)(3000 * apiRequestBean.getTimeout())) {
                    ids_sla_error.add(apiRequestBean.getReqId());
                } else {
                    apiRequestBean.setStatus("PROCESSING");
                    ids.add(apiRequestBean.getReqId());
                    result.add(apiRequestBean);
                }
            }

            if (ids.size() > 0) {
                this.apiRequestDao.updateReqList(ids, "PROCESSING");
                this.apiResponseDAO.updateReq2Process(ids, currentDate);
            }

            if (ids_sla_error.size() > 0) {
                this.apiRequestDao.updateReqList(ids_sla_error, "ERROR");
                this.apiResponseDAO.updateReq2Reject(ids_sla_error);
            }
        }

        return result;
    }

    @Override
    public void updateResAfterProcessed(ApiResponseBean apiRes) throws Exception {
        this.apiResponseDAO.updateResAfterProcessed(apiRes);
        this.apiRequestDao.updateReq(apiRes.getReqId(), apiRes.getStatus());
    }

    @Override
    public ApiResponseBean getResponse(String reqId) throws Exception{
        return this.apiResponseDAO.getRes(reqId);
    }
}
