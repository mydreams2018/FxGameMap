package cn.kungreat.fxgamemap;

/*
* CLOSE_COMBAT 近战
* LONG_RANGE 远程
* */
public enum ActionType {
    CLOSE_COMBAT(10), LONG_RANGE(800);

    private final int actionDistance;

    ActionType(int actionDistance) {
        this.actionDistance = actionDistance;
    }

    public int getActionDistance() {
        return actionDistance;
    }
}
