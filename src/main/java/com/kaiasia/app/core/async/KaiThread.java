package com.kaiasia.app.core.async;

public abstract class KaiThread<T> implements Runnable {

    private KaiThreadPool threadpool;
    private KaiThreadQueue<T> queue;
    private boolean start = true;
    private boolean isFinish = true;

    public KaiThread(String threadName, int size, KaiThreadQueue<T> queue) {
        this.queue = queue;
        this.threadpool = new KaiThreadPool(threadName, size);
    }

    public void run() {
        System.out.println("START Kai_THREAD:" + this.threadpool.getInfo());

        while(true) {
            while(this.start) {
                if (this.queue.size() == 0) {
                    this.sleep(500L);
                } else {
                    this.isFinish = false;
                    T t = this.queue.poll();
                    if (!this.check4Run(t)) {
                        this.queue.add(t);
                        this.sleep((long)(this.queue.size() * 1000 + 2000));
                    } else {
                        try {
                            this.threadpool.submit(this.getTask(t));
                        } catch (KaiThreadException var3) {
                            var3.printStackTrace();
                        }
                    }

                    this.isFinish = true;

                    while(this.threadpool.check2Sleep()) {
                        this.sleep(500L);
                    }
                }
            }

            return;
        }
    }

    public abstract KaiTask<T> getTask(T t);

    public abstract boolean check4Run(T t);

    public boolean isAllTaskFinish() {
        return this.isFinish && this.queue.size() == 0 && this.threadpool.checkAllTaskFinish();
    }

    public void stop() {
        System.out.println("STOP Kai_THREAD:" + this.threadpool.getInfo());
        this.start = false;
        this.threadpool.shutdown();
    }

    public String getInfo() {
        return this.threadpool.getInfo();
    }

    public int taskRunning() {
        return this.threadpool.taskRunning();
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException var4) {
            var4.printStackTrace();
        }

    }
    
}
