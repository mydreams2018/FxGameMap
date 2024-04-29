package cn.kungreat.fxgamemap.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WorkThread {

    public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(1, 3, 3600,
            TimeUnit.SECONDS, new ArrayBlockingQueue<>(99, false), new WorkThreadFactory(), new DiscardPolicy());

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

        }
    }
}
