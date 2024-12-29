package com.kaiasia.app.core.async;

public abstract class KaiTask<T> implements Runnable {
    public KaiTask() {
    }

    public abstract void execute();

    public abstract void sleep(int threads);

    public void run() {
        KaiThreadPool.beginTask();

        try {
            this.execute();
        } catch (Exception var5) {
            var5.printStackTrace();
        } finally {
            KaiThreadPool.endTask();
            this.sleep(200);
        }

    }

    public abstract String executeResult();
}
