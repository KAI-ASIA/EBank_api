package com.kaiasia.app.core.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@ConditionalOnExpression("${spring.database.postgres:true}")
public class PosgrestDAOHelper {

    @Autowired
    @Qualifier("namedParameterJdbcTemplatePostgres")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplatePostgres;

    public PosgrestDAOHelper() {
    }

    public int update(String sql, Map paramMap) throws Exception {
        try {
            paramMap = paramMap == null ? new HashMap() : paramMap;
            int rs = this.namedParameterJdbcTemplatePostgres.update(sql, (Map)paramMap);
            return rs;
        } catch (Exception var4) {
            var4.printStackTrace();
            throw var4;
        }
    }

    public int query4Int(String sql, Map paramMap) throws Exception {
        try {
            Number result = (Number)this.namedParameterJdbcTemplatePostgres.queryForObject(sql, (Map)(paramMap == null ? new HashMap() : paramMap), Integer.class);
            return result != null ? result.intValue() : 0;
        } catch (Exception var4) {
            throw var4;
        }
    }

    public <T> T querySingle(String sql, Map paramMap, BeanPropertyRowMapper rowMapper) throws Exception {
        try {
            paramMap = paramMap == null ? new HashMap() : paramMap;
            List<T> list = this.namedParameterJdbcTemplatePostgres.query(sql, (Map)paramMap, rowMapper);
            T rsObj = list != null && list.size() != 0 ? list.get(0) : null;
            return rsObj;
        } catch (Exception var6) {
            throw var6;
        }
    }

    public <T> List<T> query(String sql, Map paramMap, BeanPropertyRowMapper rowMapper) throws Exception {
        try {
            paramMap = paramMap == null ? new HashMap() : paramMap;
            List<T> list = this.namedParameterJdbcTemplatePostgres.query(sql, (Map)paramMap, rowMapper);
            return list;
        } catch (Exception var5) {
            throw var5;
        }
    }

}
