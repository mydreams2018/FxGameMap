package cn.kungreat.fxgamemap.util;

import cn.kungreat.fxgamemap.Configuration;
import cn.kungreat.fxgamemap.RootApplication;
import cn.kungreat.fxgamemap.RootController;
import cn.kungreat.fxgamemap.custom.AreaMapShow;
import cn.kungreat.fxgamemap.custom.TreeArea;
import cn.kungreat.fxgamemap.custom.TreeGameMap;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.regex.Matcher;

public class PropertyListener {

    private static final SimpleBooleanProperty IS_SAVED = new SimpleBooleanProperty(true);

    private static final SimpleObjectProperty<ImageView> CHOOSE_RESOURCE_IMAGE = new SimpleObjectProperty<>();

    private static final Timeline CHOOSE_RESOURCE_TIME = new Timeline();

    private static final SimpleStringProperty MAIN_MENU_HISTORY = new SimpleStringProperty();

    private static final SimpleObjectProperty<TreeGameMap.BackgroundImageData> CHOOSE_CANVAS_IMAGE = new SimpleObjectProperty<>();
    private static final Timeline CHOOSE_CANVAS_TIME = new Timeline();

    private static final SimpleBooleanProperty SWITCH_TREE_AREA = new SimpleBooleanProperty(true);

    /*
     * 主程序入口调一次
     * */
    public static void initIsSavedListener(Stage stage) {
        IS_SAVED.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                stage.setTitle("FxGameMap");
            } else {
                stage.setTitle("需要保存信息");
            }
        });
    }

    public static void initChooseResourceImageListener() {
        CHOOSE_RESOURCE_TIME.setCycleCount(Timeline.INDEFINITE);
        CHOOSE_RESOURCE_TIME.setAutoReverse(true);
        CHOOSE_RESOURCE_IMAGE.addListener((observable, oldValue, newValue) -> {
            CHOOSE_RESOURCE_TIME.stop();
            CHOOSE_RESOURCE_TIME.getKeyFrames().clear();
            if (newValue != null) {
                CHOOSE_RESOURCE_TIME.getKeyFrames().add(new KeyFrame(Duration.millis(0), new KeyValue(newValue.opacityProperty(), 1)));
                CHOOSE_RESOURCE_TIME.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(newValue.opacityProperty(), 0)));
                CHOOSE_RESOURCE_TIME.playFromStart();
            }
            if (oldValue != null) {
                oldValue.setOpacity(1);
            }
        });
    }

    public static void initMainMenuHistoryListener(Menu historyMenu) {
        if (Configuration.historyProject != null && !Configuration.historyProject.isEmpty()) {
            Configuration.historyProject.forEach(s -> {
                addMainMenuHistory(s, historyMenu);
            });
        }
        MAIN_MENU_HISTORY.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isBlank()) {
                addMainMenuHistory(newValue, historyMenu);
            }
        });
    }

    /*
     * 主菜单中的历史记录子菜单 选择事件
     * */
    private static void addMainMenuHistory(String newValue, Menu historyMenu) {
        Matcher matcher = PatternUtils.findFileName.matcher(newValue);
        if (matcher.find()) {
            MenuItem historyItem = new MenuItem(matcher.group().substring(1, matcher.group().length() - 5));
            historyItem.setUserData(newValue);
            //历史记录菜单事件
            historyItem.setOnAction(event -> {
                MenuItem historyItemHandler = (MenuItem) event.getSource();
                if (!Configuration.currentProject.equals(historyItemHandler.getUserData())) {
                    Configuration.changeCurrentProject((String) historyItemHandler.getUserData());
                    Configuration.loadTreeMenu();
                }
            });
            historyMenu.getItems().add(historyItem);
        }
    }

    public static void changeIsSaved(boolean value) {
        IS_SAVED.set(value);
    }

    public static void changeChooseResourceImage(ImageView imageView) {
        CHOOSE_RESOURCE_IMAGE.set(imageView);
    }

    public static ImageView getChooseResourceImage() {
        return CHOOSE_RESOURCE_IMAGE.get();
    }

    public static void setMainMenuHistory(String newValue) {
        MAIN_MENU_HISTORY.set(newValue);
    }

    public static void initChooseCanvasImageListener() {
        CHOOSE_CANVAS_TIME.setCycleCount(Timeline.INDEFINITE);
        CHOOSE_CANVAS_TIME.setAutoReverse(true);
        CHOOSE_CANVAS_IMAGE.addListener((observable, oldValue, newValue) -> {
            CHOOSE_CANVAS_TIME.stop();
            CHOOSE_CANVAS_TIME.getKeyFrames().clear();
            if (newValue != null) {
                CHOOSE_CANVAS_TIME.getKeyFrames().add(new KeyFrame(Duration.millis(0), new KeyValue(newValue.getImageView().opacityProperty(), 1)));
                CHOOSE_CANVAS_TIME.getKeyFrames().add(new KeyFrame(Duration.millis(100), new KeyValue(newValue.getImageView().opacityProperty(), 0.1)));
                CHOOSE_CANVAS_TIME.playFromStart();
            }
            if (oldValue != null) {
                oldValue.getImageView().setOpacity(1);
                oldValue.setImage(oldValue.getImageView().snapshot(TreeGameMap.CANVAS_SNAPSHOT_PARAMETERS, null));
            }
        });
    }

    public static void setChooseCanvasImage(TreeGameMap.BackgroundImageData backgroundImageData) {
        CHOOSE_CANVAS_IMAGE.setValue(backgroundImageData);
    }

    public static TreeGameMap.BackgroundImageData getChooseCanvasImage() {
        return CHOOSE_CANVAS_IMAGE.get();
    }

    public static void initSwitchTreeAreaListener() {
        RootController controller = RootApplication.mainFXMLLoader.getController();
        SWITCH_TREE_AREA.addListener((observable, oldValue, newValue) -> {
            TreeItem<Object> item = controller.getTreeView().getFocusModel().getFocusedItem();
            if (item != null && item.getValue() instanceof TreeArea treeArea) {
                if(treeArea.getSwitchTypeName().equals("gridPane")){
                    AreaMapShow areaMapShow = treeArea.getAreaMapShow();
                    if (areaMapShow == null) {
                        areaMapShow = new AreaMapShow();
                        treeArea.setAreaMapShow(areaMapShow);
                    }
                    areaMapShow.initAreaMapShow(treeArea);
                    areaMapShow.clearAndDraw(treeArea);
                    controller.getScrollPaneCenterInHBox().getChildren().clear();
                    controller.getScrollPaneCenterInHBox().getChildren().add(areaMapShow.getOutVBox());
                }else {
                    controller.getScrollPaneCenterInHBox().getChildren().clear();
                    treeArea.initGridPane();
                    controller.getScrollPaneCenterInHBox().getChildren().add(treeArea.getGridPane());
                }
            }
        });
    }

    public static void setSwitchTreeArea() {
        SWITCH_TREE_AREA.set(!SWITCH_TREE_AREA.get());
    }

}
