package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.Resources;
import cn.kungreat.fxgamemap.custom.TreeArea;
import cn.kungreat.fxgamemap.custom.TreeGameMap;
import cn.kungreat.fxgamemap.util.LogService;
import cn.kungreat.fxgamemap.util.PropertyListener;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
        MAP_JSON.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        MAP_JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAP_JSON.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        AREA_MONSTER_FILL.put("PeachGarden", List.of("003", "004", "005", "006", "007", "008", "009", "010", "011", "012", "013", "014", "015", "016", "017", "018"));
        AREA_MONSTER_FILL.put("Zhongzhou", List.of("003", "004", "005", "006", "007", "008", "009", "010", "011", "012", "013", "014", "015", "016", "017", "018"));
        AREA_MONSTER_FILL.put("Mengzhong", List.of("003", "004", "005", "006", "007", "008", "009", "010", "011", "012", "013", "014", "015", "016", "017", "018"));
        AREA_MONSTER_FILL.put("Shacheng", List.of("081", "082", "083", "084", "085"));
        AREA_MONSTER_FILL.put("ChiyueValley", List.of("052", "053", "054", "055", "056"));
        AREA_MONSTER_FILL.put("BlackCave1", List.of("019", "020", "021", "022", "023", "025", "026", "027", "024", "028"));
        AREA_MONSTER_FILL.put("BlackCave2", List.of("019", "020", "021", "022", "023", "025", "026", "027", "024", "028"));
        AREA_MONSTER_FILL.put("BlackCave3", List.of("019", "020", "021", "022", "023", "025", "026", "027", "024", "028"));
        AREA_MONSTER_FILL.put("BlackCave4", List.of("019", "020", "021", "022", "023", "025", "026", "027", "024", "028"));
        AREA_MONSTER_FILL.put("BoneDemonCave1", List.of("089", "090", "091", "092"));
        AREA_MONSTER_FILL.put("BoneDemonCave2", List.of("089", "090", "091", "092"));
        AREA_MONSTER_FILL.put("BoneDemonCave3", List.of("089", "090", "091", "092"));
        AREA_MONSTER_FILL.put("BoneDemonCave4", List.of("089", "090", "091", "092"));
        AREA_MONSTER_FILL.put("BoneDemonCave5", List.of("089", "090", "091", "092"));
        AREA_MONSTER_FILL.put("BoneDemonCave6", List.of("089", "090", "091", "092"));
        AREA_MONSTER_FILL.put("BullDemonCave1", List.of("029", "030", "031", "032"));
        AREA_MONSTER_FILL.put("BullDemonCave2", List.of("029", "030", "031", "032"));
        AREA_MONSTER_FILL.put("BullDemonCave3", List.of("029", "030", "031", "032"));
        AREA_MONSTER_FILL.put("BullDemonCave4", List.of("029", "030", "031", "032"));
        AREA_MONSTER_FILL.put("CentipedeCave1", List.of("035", "036", "037", "038", "039"));
        AREA_MONSTER_FILL.put("CentipedeCave2", List.of("035", "036", "037", "038", "039"));
        AREA_MONSTER_FILL.put("CentipedeCave3", List.of("035", "036", "037", "038", "039"));
        AREA_MONSTER_FILL.put("CentipedeCave5", List.of("035", "036", "037", "038", "039"));
        AREA_MONSTER_FILL.put("CentipedeCave6", List.of("035", "036", "037", "038", "039"));
        AREA_MONSTER_FILL.put("MineHole1", List.of("069", "070", "071", "072", "073"));
        AREA_MONSTER_FILL.put("MineHole2", List.of("069", "070", "071", "072", "073"));
        AREA_MONSTER_FILL.put("MineHole3", List.of("069", "070", "071", "072", "073"));
        AREA_MONSTER_FILL.put("MineHole4", List.of("069", "070", "071", "072", "073"));
        AREA_MONSTER_FILL.put("MineHole5", List.of("069", "070", "071", "072", "073"));
        AREA_MONSTER_FILL.put("MineHole6", List.of("069", "070", "071", "072", "073"));
        AREA_MONSTER_FILL.put("MineHole7", List.of("069", "070", "071", "072", "073"));
        AREA_MONSTER_FILL.put("MineHole8", List.of("069", "070", "071", "072", "073"));
        AREA_MONSTER_FILL.put("Pighole1", List.of("042", "043", "044", "045", "046", "047"));
        AREA_MONSTER_FILL.put("Pighole2", List.of("042", "043", "044", "045", "046", "047"));
        AREA_MONSTER_FILL.put("Pighole3", List.of("042", "043", "044", "045", "046", "047"));
        AREA_MONSTER_FILL.put("Pighole4", List.of("042", "043", "044", "045", "046", "047"));
        AREA_MONSTER_FILL.put("Pighole5", List.of("042", "043", "044", "045", "046", "047"));
        AREA_MONSTER_FILL.put("Pighole6", List.of("042", "043", "044", "045", "046", "047"));
        AREA_MONSTER_FILL.put("WomaTemple1", List.of("094", "095", "096", "097", "098", "099", "100"));
        AREA_MONSTER_FILL.put("WomaTemple2", List.of("094", "095", "096", "097", "098", "099", "100"));
        AREA_MONSTER_FILL.put("WomaTemple3", List.of("094", "095", "096", "097", "098", "099", "100"));
        AREA_MONSTER_FILL.put("WomaTemple4", List.of("094", "095", "096", "097", "098", "099", "100"));
        AREA_MONSTER_FILL.put("ZumaTemple2", List.of("063", "065", "066",  "064"));
        AREA_MONSTER_FILL.put("ZumaTemple3", List.of("063", "065", "066",  "064"));
        AREA_MONSTER_FILL.put("ZumaTemple4", List.of("063", "065", "066",  "064"));
        AREA_MONSTER_FILL.put("ZumaTemple5", List.of("063", "065", "066",  "064"));
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
        LogService.writerLog(LogService.LogLevel.INFO, getClass(), "项目启动完成");
    }

    public static void main(String[] args) {
        launch(args);
    }
}