package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.TreeArea;
import cn.kungreat.fxgamemap.custom.TreeGameMap;
import cn.kungreat.fxgamemap.custom.TreeWorld;
import cn.kungreat.fxgamemap.util.PatternUtils;
import cn.kungreat.fxgamemap.util.PropertyListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.regex.Pattern;
@Setter
@Getter
public class RootController implements Initializable {

    private Dialog<String> worldDialog = BaseDialog.getDialog("世界地图", "请输入世界地图名称:", "是否需要添加世界地图层级"
            , BaseDialog.TEXT_WORLD, BaseDialog.APPLY_WORLD, BaseDialog.CANCEL_WORLD);

    private Dialog<String> areaDialog = BaseDialog.getDialog("区域地图", "请输入区域地图信息:", "是否需要添加区域地图层级"
            , BaseDialog.getAreaRectangular(), BaseDialog.APPLY_AREA, BaseDialog.CANCEL_AREA);

    private Dialog<String> mapDialog = BaseDialog.getDialog("分块地图", "请输入分块地图信息:", "是否需要添加分块地图"
            , BaseDialog.getMapRectangular(), BaseDialog.APPLY_MAP, BaseDialog.CANCEL_MAP);

    private Dialog<String> linkMapBookDialog = BaseDialog.getDialog("使用说明", "地图完整的使用说明",
            """
                    1.世界地图是一个完整游戏地图的概念
                    2.区域地图是世界地图的子级.是一个区域的概念
                    3.分块地图是区域地图的子级,分块地图是一个个地图的概念
                        游戏过大时不会把全部地图信息存在一个图片里
                    """
            , null, ButtonType.CLOSE);
    @FXML
    private HBox hBox;
    @FXML
    private SplitPane splitPane;
    @FXML
    private StackPane stackPaneLeft;
    @FXML
    private Text stackPaneLeftText;
    @FXML
    private StackPane stackPaneCenter;
    @FXML
    private StackPane stackPaneRight;
    @FXML
    private TreeView<Object> treeView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        hBox.getChildren().add(new MainMenuBar());
        treeView.setEditable(true);
        treeView.setCellFactory(TextFieldTreeCell.forTreeView(TreeWorld.treeConverter()));
        treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        treeView.prefHeightProperty().bind(stackPaneLeft.heightProperty().subtract(stackPaneLeftText.prefHeight(-1)));
        treeView.setContextMenu(getTreeContextMenu());
        worldDialog.setOnShowing(event -> BaseDialog.TEXT_WORLD.clear());
        areaDialog.setOnShowing(event -> BaseDialog.TEXT_AREA.clear());
        mapDialog.setOnShowing(event -> BaseDialog.TEXT_MAP.clear());
        addTreeEvent();
    }

    public ContextMenu getTreeContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuWorld = new MenuItem("add world");
        menuWorld.setGraphic(new FontIcon("fas-globe"));
        menuWorld.setOnAction(event -> worldDialog.showAndWait());
        MenuItem menuArea = new MenuItem("add Area");
        menuArea.setGraphic(new FontIcon("fas-chart-area"));
        menuArea.setOnAction(event -> areaDialog.showAndWait());
        MenuItem menuMap = new MenuItem("add Map");
        menuMap.setGraphic(new FontIcon("fas-map"));
        menuMap.setOnAction(event -> mapDialog.showAndWait());
        contextMenu.getItems().addAll(menuWorld, new SeparatorMenuItem(), menuArea, new SeparatorMenuItem(), menuMap);
        return contextMenu;
    }

    public void addTreeEvent() {
        //世界地图事件添加
        Button applyWorld = (Button) worldDialog.getDialogPane().lookupButton(BaseDialog.APPLY_WORLD);
        applyWorld.setOnAction(event -> {
            String newWorld = BaseDialog.TEXT_WORLD.getText();
            if (!newWorld.isBlank()) {
                TreeItem<Object> root = treeView.getRoot();
                TreeWorld treeWorld = new TreeWorld(newWorld, UUID.randomUUID().toString(), new ArrayList<>());
                TreeItem<Object> treeItem = new TreeItem<>(treeWorld);
                treeItem.setGraphic(new FontIcon("fas-globe"));
                root.getChildren().add(treeItem);
                PropertyListener.changeIsSaved(false);
            }
        });
        //区域地图事件添加
        Button applyArea = (Button) areaDialog.getDialogPane().lookupButton(BaseDialog.APPLY_AREA);
        applyArea.setOnAction(event -> {
            String newArea = BaseDialog.TEXT_AREA.getText();
            String areaXText = BaseDialog.TEXT_AREAX.getText();
            String areaYText = BaseDialog.TEXT_AREAY.getText();
            if (!newArea.isBlank() && !areaXText.isBlank() && !areaYText.isBlank()
                    && Pattern.matches(PatternUtils.NumberRegex, areaXText) && Pattern.matches(PatternUtils.NumberRegex, areaYText)) {
                TreeItem<Object> item = treeView.getFocusModel().getFocusedItem();
                if (item != null && item.getValue() instanceof TreeWorld treeWorld) {
                    TreeArea treeArea = new TreeArea(newArea, UUID.randomUUID().toString(),
                            Integer.parseInt(areaXText), Integer.parseInt(areaYText), new ArrayList<>());
                    treeWorld.getChildrenArea().add(treeArea);
                    TreeItem<Object> treeItem = new TreeItem<>(treeArea);
                    treeItem.setGraphic(new FontIcon("fas-chart-area"));
                    item.getChildren().add(treeItem);
                    item.setExpanded(true);
                    PropertyListener.changeIsSaved(false);
                }
            }
        });
        //地图事件添加
        Button applyMap = (Button) mapDialog.getDialogPane().lookupButton(BaseDialog.APPLY_MAP);
        applyMap.setOnAction(event -> {
            String title = BaseDialog.TEXT_MAP.getText();
            String mapWidth = BaseDialog.TEXT_MAP_WIDTH.getText();
            String mapHeight = BaseDialog.TEXT_MAP_HEIGHT.getText();
            if (!title.isBlank() && !mapWidth.isBlank() && !mapHeight.isBlank()
                    && Pattern.matches(PatternUtils.NumberRegex, mapWidth) && Pattern.matches(PatternUtils.NumberRegex, mapHeight)) {
                TreeItem<Object> item = treeView.getFocusModel().getFocusedItem();
                if (item != null && item.getValue() instanceof TreeArea treeArea) {
                    TreeGameMap treeGameMap = new TreeGameMap(UUID.randomUUID().toString(), title,
                            Integer.parseInt(mapWidth), Integer.parseInt(mapHeight));
                    treeArea.getChildrenMap().add(treeGameMap);
                    TreeItem<Object> treeItem = new TreeItem<>(treeGameMap);
                    treeItem.setGraphic(new FontIcon("fas-map"));
                    item.getChildren().add(treeItem);
                    item.setExpanded(true);
                    PropertyListener.changeIsSaved(false);
                }
            }
        });
    }

    public void showTreeBookDialog(MouseEvent mouseEvent) {
        linkMapBookDialog.showAndWait();
    }
}