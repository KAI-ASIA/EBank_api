package com.kaiasia.app.core.async;

public abstract class KaiThreadMain<T> {
    private KaiThread<T> KaiThread;
    private KaiThreadQueue<T> queue;
    private boolean active = true;
    private int size;
    private String threadName;

    public abstract KaiTask<T> getKaiTask(T t);

    public abstract boolean checkToRun(T t);

    public String getThreadName() {
        return this.threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public KaiThreadMain(String threadName, int size) {
        this.size = size;
    }

    public void init() {
        this.queue = new KaiThreadQueue();
        this.KaiThread = new KaiThread<T>(this.threadName, this.size, this.queue) {
            public KaiTask<T> getTask(T t) {
                return KaiThreadMain.this.getKaiTask(t);
            }

            public boolean check4Run(T t) {
                return KaiThreadMain.this.checkToRun(t);
            }
        };
        Thread thread = new Thread(this.KaiThread);
        thread.start();
    }

    public void addQueue(T t) {
        this.queue.add(t);
    }

    public int getTaskInQueue() {
        return this.queue.size();
    }

    public boolean isAllTaskFinish() {
        return this.KaiThread.isAllTaskFinish();
    }

    public String getInfo() {
        return this.KaiThread.getInfo();
    }

    public void shutdown() {
        this.KaiThread.stop();
    }

    public int getActive() {
        return this.KaiThread.taskRunning();
    }
}
