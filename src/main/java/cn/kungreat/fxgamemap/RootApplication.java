package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.Resources;
import cn.kungreat.fxgamemap.custom.TreeArea;
import cn.kungreat.fxgamemap.custom.TreeGameMap;
import cn.kungreat.fxgamemap.frame.FrameTimer;
import cn.kungreat.fxgamemap.util.LogService;
import cn.kungreat.fxgamemap.util.PropertyListener;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RootApplication extends Application {

    public static Stage mainStage;
    public static final FXMLLoader mainFXMLLoader = new FXMLLoader(RootApplication.class.getResource("FxGameMap.fxml"));
    public static final ObjectMapper MAP_JSON = new ObjectMapper();
    public static final Resources RESOURCES = new Resources();
    public static final Map<String, List<String>> AREA_MONSTER_FILL = new HashMap<>();

    static {
        //序列化时过滤掉为null的对象
        MAP_JSON.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAP_JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        AREA_MONSTER_FILL.put("PeachGarden", List.of("003", "004", "005", "006", "007", "008", "009", "010", "011", "012", "013", "014", "015", "016", "017", "018"));
        AREA_MONSTER_FILL.put("Zhongzhou", List.of("003", "004", "005", "006", "007", "008", "009", "010", "011", "012", "013", "014", "015", "016", "017", "018"));
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(mainFXMLLoader.load());
        scene.getStylesheets().add(RootApplication.class.getResource("FxGameMap.css").toExternalForm());
        stage.setTitle("FxGameMap");
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.getIcons().add(new Image(RootApplication.class.getResourceAsStream("favicon.ico")));
        stage.setWidth(1366);
        stage.setHeight(768);
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setMaxWidth(bounds.getWidth());
        stage.setMaxHeight(bounds.getHeight());
        stage.show();
        mainStage = stage;
        Configuration.loadTreeMenu();
        PropertyListener.initIsSavedListener(stage);
        PropertyListener.initChooseResourceImageListener();
        RootController rootController = mainFXMLLoader.getController();
        PropertyListener.initMainMenuHistoryListener(rootController.getMainMenuBar().getHistoryMenu());
        TreeGameMap.addImageObjectEvent();
        TreeArea.addChildrenPointDialogEvent();
        PropertyListener.initSwitchTreeAreaListener();
        PropertyListener.initChooseCanvasImageListener();
        RootController.addBatchChangeImageObjectEvent();
        new FrameTimer().start();
        LogService.writerLog(LogService.LogLevel.INFO, getClass(), "项目启动完成");
    }

    public static void main(String[] args) {
        launch(args);
    }
}