package com.kaiasia.app.core.job;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Data
@Component
public class ApiFetchJob {

    @Autowired
    private ApiFetchReqTask apiFetchReqTask;

    @PostConstruct
    public void init(){
        System.out.println("START APIFetchJob");
        Thread fetch = new Thread(apiFetchReqTask);
        fetch.start();
    }

}
