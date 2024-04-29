package cn.kungreat.fxgamemap.util;

import cn.kungreat.fxgamemap.RootApplication;
import javafx.beans.property.SimpleBooleanProperty;

public class PropertyListener {

    private static final SimpleBooleanProperty IS_SAVED = new SimpleBooleanProperty(true);

    /*
     * 主程序入口调一次
     * */
    public static void initIsSavedListener() {
        IS_SAVED.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                RootApplication.mainStage.setTitle("FxGameMap");
            } else {
                RootApplication.mainStage.setTitle("需要保存信息");
            }
        });
    }

    public static void changeIsSaved(boolean value) {
        IS_SAVED.set(value);
    }
}
