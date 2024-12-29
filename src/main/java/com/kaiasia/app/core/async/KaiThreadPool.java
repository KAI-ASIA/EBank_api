package com.kaiasia.app.core.async;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KaiThreadPool {
    private ThreadPoolExecutor threadPoolExecutor;
    private String threadpoolName;
    private int poolSize;
    private static int active;

    public KaiThreadPool(String threadpoolName, int poolSize) {
        this.threadPoolExecutor = new ThreadPoolExecutor(poolSize, poolSize, 500L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue(poolSize * 3));
        this.poolSize = poolSize;
        this.threadpoolName = threadpoolName;
    }

    public void submit(KaiTask runable) throws KaiThreadException {
        try {
            this.threadPoolExecutor.execute(runable);
        } catch (RejectedExecutionException var3) {
            throw new KaiThreadException(this.threadpoolName, var3);
        }
    }

    public boolean checkAllTaskFinish() {
        return this.threadPoolExecutor.getQueue().size() == 0 && this.threadPoolExecutor.getActiveCount() == 0 && active == 0;
    }

    public boolean checkSleep() {
        return this.threadPoolExecutor.getQueue().size() >= this.poolSize * 2 || this.threadPoolExecutor.getActiveCount() == this.poolSize;
    }

    public boolean check2Sleep() {
        return this.threadPoolExecutor.getActiveCount() == this.poolSize;
    }

    public String getInfo() {
        StringBuilder builder = (new StringBuilder("\n******************************************")).append("\n*\tName: ").append(this.threadpoolName).append("\n*\tQueueSize: ").append(this.threadPoolExecutor.getQueue().size()).append("\n*\tActiveCount: ").append(this.threadPoolExecutor.getActiveCount()).append("\n*\tCompletedTaskCount: ").append(this.threadPoolExecutor.getCompletedTaskCount()).append("\n*\tTaskCount: ").append(this.threadPoolExecutor.getTaskCount()).append("\n*\tLargestPoolSize: ").append(this.threadPoolExecutor.getLargestPoolSize()).append("\n*\tIsShutdown: ").append(this.threadPoolExecutor.isShutdown()).append("\n*\tPoolSize: ").append(this.threadPoolExecutor.getPoolSize()).append("\n*\tCorePoolSize: ").append(this.threadPoolExecutor.getCorePoolSize()).append("\n*\tactive: ").append(active).append("\n******************************************");
        return builder.toString();
    }

    public int taskRunning() {
        return active;
    }

    public String getHTMLInfo() {
        StringBuilder builder = (new StringBuilder("<br/>******************************************")).append("<br/>*\tName: ").append(this.threadpoolName).append("<br/>*\tQueueSize: ").append(this.threadPoolExecutor.getQueue().size()).append("<br/>*\tActiveCount: ").append(this.threadPoolExecutor.getActiveCount()).append("<br/>*\tCompletedTaskCount: ").append(this.threadPoolExecutor.getCompletedTaskCount()).append("<br/>*\tTaskCount: ").append(this.threadPoolExecutor.getTaskCount()).append("<br/>*\tLargestPoolSize: ").append(this.threadPoolExecutor.getLargestPoolSize()).append("<br/>*\tIsShutdown: ").append(this.threadPoolExecutor.isShutdown()).append("<br/>*\tactive: ").append(active).append("<br/>******************************************");
        return builder.toString();
    }

    public void shutdown() {
        try {
            this.threadPoolExecutor.shutdown();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    protected static synchronized void beginTask() {
        ++active;
    }

    protected static synchronized int endTask() {
        --active;
        return active;
    }
}
