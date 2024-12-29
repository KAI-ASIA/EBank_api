package com.kaiasia.app.core.dao;

import com.kaiasia.app.core.model.ApiRequestBean;
import com.kaiasia.app.core.model.ApiResponse;
import com.kaiasia.app.core.model.ApiResponseBean;

import java.util.List;

public interface IAPIDaoManager {
    void insertNewApiRequest(ApiRequestBean var1, ApiResponseBean var2) throws Exception;
    List<ApiRequestBean> fetchAPIReqs(int var1) throws Exception;
    void updateResAfterProcessed(ApiResponseBean var1) throws Exception;
    ApiResponseBean getResponse(String reqId) throws Exception;
}
