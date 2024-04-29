package cn.kungreat.fxgamemap.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkThreadFactory implements ThreadFactory {

    private static final WorkThreadGroup WORK_THREAD = new WorkThreadGroup("WorkThread");
    private static final AtomicInteger THREAD_NUMBER = new AtomicInteger(1);
    private static final String NAME_PREFIX = "WorkThread-";

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(WORK_THREAD, runnable, NAME_PREFIX + THREAD_NUMBER.getAndIncrement());
        //守护线程
        thread.setDaemon(true);
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
