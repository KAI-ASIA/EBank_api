package com.kaiasia.app.core.dao;

import com.kaiasia.app.core.model.ApiResponseBean;

import java.util.Date;
import java.util.List;

public interface IApiResponseDAO {
    int insert(ApiResponseBean var1) throws Exception;

    ApiResponseBean getRes(String var1) throws Exception;

    List<ApiResponseBean> getApiResponseByStatus(String var1, int var2) throws Exception;

    int delete(String var1) throws Exception;

    void updateReq2Process(List<String> var1, Date var2) throws Exception;

    void updateReq2Reject(List<String> var1) throws Exception;

    void updateResAfterProcessed(ApiResponseBean var1) throws Exception;
}
