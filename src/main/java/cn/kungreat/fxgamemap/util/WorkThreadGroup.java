package cn.kungreat.fxgamemap.util;


public class WorkThreadGroup extends ThreadGroup {

    public WorkThreadGroup(String name) {
        super(name);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LogService.printLog(LogService.LogLevel.ERROR, getClass(), "线程组异常处理器" + t.getName(), e);
    }
}
