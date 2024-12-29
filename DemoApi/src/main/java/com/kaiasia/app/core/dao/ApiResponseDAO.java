package com.kaiasia.app.core.dao;

import com.kaiasia.app.core.model.ApiResponseBean;
import com.kaiasia.app.utils.ApiUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Data
public class ApiResponseDAO extends CommonDAO implements IApiResponseDAO{

    @Autowired
    private PosgrestDAOHelper posgrestDAOHelper;

    @Override
    public int insert(ApiResponseBean apiRes) throws Exception {
        String sql = "INSERT INTO " + this.getTableName() + "(req_id, receive_time, response_time, request_msg, response_msg, request_api, request_node, process_node,status, end_process_time ,start_process_time, response_node, receive_node) " +
                " VALUES(:REQ_ID, :RECEIVE_TIME, :RESPONSE_TIME, :REQUEST_MSG, :RESPONSE_MSG, :REQUEST_API, :REQUEST_NODE, :PROCESS_NODE,:STATUS, :END_PROCESS_TIME, :START_PROCESS_TIME, :RESPONSE_NODE, :RECEIVE_NODE )";
        HashMap<String, Object> param = new HashMap();
        param.put("REQ_ID", apiRes.getReqId());
        param.put("RECEIVE_TIME", apiRes.getReceiveTime());
        param.put("RESPONSE_TIME", apiRes.getResponseTime());
        param.put("REQUEST_MSG", apiRes.getRequestMsg());
        param.put("RESPONSE_MSG", apiRes.getResponseMsg());
        param.put("REQUEST_API", apiRes.getRequestApi());
        param.put("REQUEST_NODE", apiRes.getRequestNode());
        param.put("PROCESS_NODE", apiRes.getProcessNode());
        param.put("STATUS", apiRes.getStatus());
        param.put("END_PROCESS_TIME", apiRes.getEndProcessTime());
        param.put("START_PROCESS_TIME", apiRes.getStartProcessTime());
        param.put("RESPONSE_NODE", apiRes.getResponseNode());
        param.put("RECEIVE_NODE", apiRes.getReceiveNode());
        int result = posgrestDAOHelper.update(sql, param);
        return result;
    }

    @Override
    public ApiResponseBean getRes(String reqId) throws Exception {
        String sql = "SELECT * from " + this.getTableName() + " where req_id = :REQ_ID";
        HashMap<String, Object> param = new HashMap();
        param.put("REQ_ID", reqId);
        ApiResponseBean result = posgrestDAOHelper.querySingle(sql, param, new BeanPropertyRowMapper(ApiResponseBean.class));
        return result;
    }

    @Override
    public List<ApiResponseBean> getApiResponseByStatus(String status, int limit) throws Exception {
        String sql = "SELECT * FROM " + this.getTableName() + " WHERE status =:STATUS LIMIT " + limit;
        HashMap<String, Object> param = new HashMap();
        param.put("STATUS", status);
        List<ApiResponseBean> listOFSResponseError = posgrestDAOHelper.query(sql, param, new BeanPropertyRowMapper(ApiResponseBean.class));
        return listOFSResponseError;
    }

    @Override
    public int delete(String requestId) throws Exception {
        String sql = "DELETE FROM " + this.getTableName() + " WHERE req_id = :REQ_ID";
        HashMap paramMap = new HashMap();
        paramMap.put("REQ_ID", requestId);
        return posgrestDAOHelper.update(sql, paramMap);
    }

    @Override
    public void updateReq2Process(List<String> ids, Date startTime) throws Exception {
        String sql = "UPDATE " + this.getTableName() + " set status = :STATUS, start_process_time= :START_PROCESS_TIME, process_node = :PROCESS_NODE where req_id in (:IDS)";
        HashMap<String, Object> param = new HashMap();
        param.put("PROCESS_NODE", ApiUtils.getCurrentHostName());
        param.put("START_PROCESS_TIME", startTime);
        param.put("STATUS", "PROCESS");
        param.put("IDS", ids);
        posgrestDAOHelper.update(sql, param);
    }

    @Override
    public void updateReq2Reject(List<String> ids) throws Exception {
        String sql = "UPDATE " + this.getTableName() + " set status = :STATUS, process_node = :PROCESS_NODE where req_id in (:IDS)";
        HashMap<String, Object> param = new HashMap();
        param.put("PROCESS_NODE", ApiUtils.getCurrentHostName());
        param.put("STATUS", "REJECT");
        param.put("IDS", ids);
        posgrestDAOHelper.update(sql, param);
    }

    @Override
    public void updateResAfterProcessed(ApiResponseBean apiRes) throws Exception {
        String sql = "UPDATE " + this.getTableName() + " set status = :STATUS, response_msg= :RESPONSE_MSG, end_process_time=:END_PROCESS_TIME where req_id= :REQ_ID";
        HashMap<String, Object> param = new HashMap();
        param.put("RESPONSE_MSG", apiRes.getResponseMsg());
        param.put("STATUS", apiRes.getStatus());
        param.put("REQ_ID", apiRes.getReqId());
        param.put("END_PROCESS_TIME", new Date());
        posgrestDAOHelper.update(sql, param);
    }
}
