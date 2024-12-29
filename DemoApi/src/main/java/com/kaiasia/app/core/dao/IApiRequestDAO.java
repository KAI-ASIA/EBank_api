package com.kaiasia.app.core.dao;

import com.kaiasia.app.core.model.ApiRequestBean;

import java.util.List;

public interface IApiRequestDAO {
    ApiRequestBean getByReqID(String var1) throws Exception;

    int insert(ApiRequestBean var1) throws Exception;

    List<ApiRequestBean> getReqs(int var1) throws Exception;

    int updateReq(String var1, String var2) throws Exception;

    int updateReqList(List<String> var1, String var2) throws Exception;

    int delete(String var1) throws Exception;
}
