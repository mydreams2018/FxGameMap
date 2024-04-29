package cn.kungreat.fxgamemap.util;


public class WorkThreadGroup extends ThreadGroup {

    public WorkThreadGroup(String name) {
        super(name);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

    }
}
