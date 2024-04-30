package cn.kungreat.fxgamemap.util;

import cn.kungreat.fxgamemap.RootApplication;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class PropertyListener {

    private static final SimpleBooleanProperty IS_SAVED = new SimpleBooleanProperty(true);

    private static final SimpleObjectProperty<ImageView> CHOOSE_RESOURCE_IMAGE = new SimpleObjectProperty<>();

    private static final Timeline CHOOSE_RESOURCE_TIME = new Timeline();

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

    public static void initChooseResourceImageListener() {
        CHOOSE_RESOURCE_TIME.setCycleCount(Timeline.INDEFINITE);
        CHOOSE_RESOURCE_TIME.setAutoReverse(true);
        CHOOSE_RESOURCE_IMAGE.addListener((observable, oldValue, newValue) -> {
            CHOOSE_RESOURCE_TIME.stop();
            CHOOSE_RESOURCE_TIME.getKeyFrames().clear();
            CHOOSE_RESOURCE_TIME.getKeyFrames().add(new KeyFrame(Duration.millis(0), new KeyValue(newValue.opacityProperty(), 1)));
            CHOOSE_RESOURCE_TIME.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(newValue.opacityProperty(), 0)));
            CHOOSE_RESOURCE_TIME.playFromStart();
            if (oldValue != null){
                oldValue.setOpacity(1);
            }
        });
    }

    public static void changeIsSaved(boolean value) {
        IS_SAVED.set(value);
    }

    public static void changeChooseResourceImage(ImageView imageView) {
        CHOOSE_RESOURCE_IMAGE.set(imageView);
    }
}
