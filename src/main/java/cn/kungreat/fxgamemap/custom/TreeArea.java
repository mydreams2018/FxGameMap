package cn.kungreat.fxgamemap.custom;

import cn.kungreat.fxgamemap.BaseDialog;
import cn.kungreat.fxgamemap.Configuration;
import cn.kungreat.fxgamemap.RootApplication;
import cn.kungreat.fxgamemap.util.LogService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class TreeArea {
    private String title;
    private String id;
    /*
     * xNumber x坐标有几个地图
     * yNumber y坐标有几个地图
     * */
    private Integer xNumber;
    private Integer yNumber;
    private Integer width;
    private Integer height;
    private List<TreeGameMap> childrenMap;
    private String imageDirectory;
    private String[][] childrenPointName;
    private String bgAudioClip;
    /*
     * 区域角色初始的坐标
     * */
    private Integer roleStartX;
    private Integer roleStartY;
    @JsonIgnore
    private GridPane gridPane;
    @JsonIgnore
    private String switchTypeName;
    @JsonIgnore
    private AreaMapShow areaMapShow;
    @JsonIgnore
    private boolean[][] basePointLockList;
    @JsonIgnore
    private List<PointLock> openPointLockList;

    public static final ObservableList<String> STRING_OBSERVABLE_LIST = FXCollections.observableArrayList();
    public static final Dialog<String> STRING_OBSERVABLE_DIALOG = BaseDialog.getChildrenPointDialog();

    public TreeArea(String title, String id, Integer xNumber, Integer yNumber, List<TreeGameMap> childrenMap, String imageDirectory,
                    Integer width, Integer height, String bgAudioClip, Integer roleStartX, Integer roleStartY) {
        this.title = title;
        this.id = id;
        this.xNumber = xNumber;
        this.yNumber = yNumber;
        this.childrenMap = childrenMap;
        this.imageDirectory = imageDirectory;
        this.width = width;
        this.height = height;
        this.bgAudioClip = bgAudioClip;
        this.roleStartX = roleStartX;
        this.roleStartY = roleStartY;
    }

    public void initGridPane() {
        if (gridPane == null) {
            gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setMaxHeight(Control.USE_PREF_SIZE);
            if (childrenPointName == null) {
                childrenPointName = new String[xNumber][yNumber];
            }
            for (int y = 0; y < yNumber; y++) {
                for (int x = 0; x < xNumber; x++) {
                    HBox hBoxTwo = new HBox(6);
                    hBoxTwo.getChildren().addAll(new Label("名称:"), new Label(childrenPointName[x][y]));
                    hBoxTwo.setMinWidth(Control.USE_PREF_SIZE);
                    hBoxTwo.setAlignment(Pos.CENTER);
                    hBoxTwo.setPadding(new Insets(10, 10, 10, 10));
                    hBoxTwo.setBorder(Border.stroke(Color.GRAY));
                    hBoxTwo.setOnMouseClicked(new PointEventHandler(x, y));
                    gridPane.add(hBoxTwo, x, y);
                }
            }
        }
        switchTypeName = "gridPane";
    }

    public static void addChildrenPointDialogEvent() {
        Button apply = (Button) STRING_OBSERVABLE_DIALOG.getDialogPane().lookupButton(BaseDialog.AREA_LINK_APPLY);
        Button cancel = (Button) STRING_OBSERVABLE_DIALOG.getDialogPane().lookupButton(BaseDialog.AREA_LINK_CANCEL);
        apply.setOnAction(event -> STRING_OBSERVABLE_DIALOG.setResult("OK"));
        cancel.setOnAction(event -> STRING_OBSERVABLE_DIALOG.setResult("CANCEL"));
    }

    @Setter
    @Getter
    public final class PointEventHandler implements EventHandler<MouseEvent> {
        private int x;
        private int y;

        public PointEventHandler(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void handle(MouseEvent event) {
            STRING_OBSERVABLE_LIST.clear();
            if (childrenMap != null) {
                childrenMap.forEach(treeGameMap -> {
                    for (String[] outArray : childrenPointName) {
                        for (String titleName : outArray) {
                            if (treeGameMap.getTitle().equals(titleName)) {
                                return;
                            }
                        }
                    }
                    STRING_OBSERVABLE_LIST.add(treeGameMap.getTitle());
                });
            }
            Optional<String> re = STRING_OBSERVABLE_DIALOG.showAndWait();
            if (re.isPresent() && "OK".equals(re.get())) {
                ChoiceBox<String> choiceBox = (ChoiceBox<String>) STRING_OBSERVABLE_DIALOG.getGraphic();
                if (choiceBox.getValue() != null) {
                    TreeArea.this.childrenPointName[x][y] = choiceBox.getValue();
                    HBox hBox = (HBox) event.getSource();
                    Label label = (Label) hBox.getChildren().getLast();
                    label.setText(choiceBox.getValue());
                    STRING_OBSERVABLE_DIALOG.setResult("CANCEL");
                }
            }
        }
    }

    //自动填充区域数据
    public void autoFillChildData(TreeItem<Object> treeAreaItem) {
        if (this.childrenPointName == null) {
            this.childrenPointName = new String[xNumber][yNumber];
        }
        for (int y = 0; y < this.yNumber; y++) {
            for (int x = 0; x < this.xNumber; x++) {
                TreeGameMap treeGameMap = new TreeGameMap(UUID.randomUUID().toString(), "x" + x + "y" + y,
                        this.getWidth(), this.getHeight(), this.getImageDirectory());
                this.getChildrenMap().add(treeGameMap);
                TreeItem<Object> treeItem = new TreeItem<>(treeGameMap);
                treeItem.setGraphic(new FontIcon("fas-map"));
                treeAreaItem.getChildren().add(treeItem);
                this.childrenPointName[x][y] = treeGameMap.getTitle();
            }
        }
    }

    public void initPointLockListData() {
        try {
            File baseDirectory = new File(new File(new URI(Configuration.currentProject).getPath()).getParentFile(), this.imageDirectory);
            File lockFile = new File(baseDirectory, "base_point_lock.json");
            if (lockFile.exists()) {
                BufferedReader readerLPointLock = new BufferedReader(new InputStreamReader(new FileInputStream(lockFile)));
                String readLine = readerLPointLock.readLine();
                if (readLine != null && !readLine.isEmpty()) {
                    this.basePointLockList = RootApplication.MAP_JSON.readValue(readLine, boolean[][].class);
                }
                readerLPointLock.close();
            }
            File openFile = new File(baseDirectory, "open_point_lock.json");
            if (openFile.exists()) {
                BufferedReader openPointLock = new BufferedReader(new InputStreamReader(new FileInputStream(openFile)));
                String openLine = openPointLock.readLine();
                if (openLine != null && !openLine.isEmpty()) {
                    this.openPointLockList = RootApplication.MAP_JSON.readValue(openLine, new TypeReference<List<PointLock>>() {
                    });
                }
                //替换数据
                if (this.openPointLockList != null && !this.openPointLockList.isEmpty()) {
                    for (PointLock tempPointLock : this.openPointLockList) {
                        this.basePointLockList[tempPointLock.getX()][tempPointLock.getY()] = tempPointLock.isB();
                    }
                }
                openPointLock.close();
            }
        } catch (Exception e) {
            LogService.printLog(LogService.LogLevel.ERROR, TreeArea.class, "initPointLockListData", e);
        }
    }

    public void writeJsonData() {
        try {
            File areaJson = new File(new File(new URI(Configuration.currentProject).getPath()).getParentFile(), this.imageDirectory);
            if (!areaJson.exists()) {
                areaJson.mkdir();
            }
            File areaJsonFile = new File(areaJson, "area.json");
            if (!areaJsonFile.exists()) {
                areaJsonFile.createNewFile();
            }
            Files.write(areaJsonFile.toPath(), RootApplication.MAP_JSON.writeValueAsString(this).getBytes(),
                    StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Exception e) {
            LogService.printLog(LogService.LogLevel.ERROR, TreeArea.class, "writeJsonData", e);
        }
    }
}
