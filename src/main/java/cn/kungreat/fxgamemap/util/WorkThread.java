package cn.kungreat.fxgamemap.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WorkThread {

    public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(2, 6, 3600,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(999, false), new WorkThreadFactory(), new DiscardPolicy());

    static class DiscardPolicy implements RejectedExecutionHandler {

        public DiscardPolicy() {

        }

        /**
         * 记录日志
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         */
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            LogService.writerLog(LogService.LogLevel.WARNING, WorkThread.class, "线程池拒绝处理");
        }
    }
}
