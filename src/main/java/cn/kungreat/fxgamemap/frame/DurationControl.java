package cn.kungreat.fxgamemap.frame;

public class DurationControl {

    private long lastStartTime = System.currentTimeMillis();
    /*
    * 指定时间间隔
    * */
    private final int INTERVAL_TIME;

    public DurationControl(int intervalTime) {
        this.INTERVAL_TIME = intervalTime;
    }

    public boolean changeOpacity() {
        if (System.currentTimeMillis() > lastStartTime + INTERVAL_TIME) {
            lastStartTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public void updateLastStartTime() {
        this.lastStartTime = System.currentTimeMillis();
    }

}
