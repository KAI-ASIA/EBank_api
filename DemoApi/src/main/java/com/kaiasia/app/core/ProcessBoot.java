//package com.kaiasia.app.core;
//
//import com.kaiasia.app.core.utils.AppConfigPropertiesUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.HashMap;
//
//@Component
//public class ProcessBoot {
//    @Autowired
//    private ApplicationContext context;
//    @Autowired
//    AppConfigPropertiesUtils appConfig;
//    private HashMap<Integer, Boolean> stopTheadsStatus;
//    private final Logger logger = LoggerFactory.getLogger(ProcessBoot.class);
//
//
//    @PostConstruct
//    public void init() {
//        int threadNo = Integer.parseInt(this.appConfig.getProp("kai.processThreadNumber"));
//        this.stopTheadsStatus = new HashMap();
//
//        for(int i = 0; i < threadNo; ++i) {
//            this.stopTheadsStatus.put(i, false);
//            ProcessThread pt = (ProcessThread)this.context.getBean(ProcessThread.class);
//            pt.setThreadIndex(i);
//            Thread t = new Thread(pt);
//            t.start();
//        }
//        this.logger.info("Service start with {} process threads. ", threadNo);
//    }
//
//    public void processThreadStop(int index) {
//        this.stopTheadsStatus.put(index, true);
//    }
//
//}
