package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.TreeWorld;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class RootController implements Initializable {

    private Dialog<String> worldDialog = BaseDialog.getDialog("世界地图", "请输入世界地图名称:", "是否需要添加世界地图层级"
            , BaseDialog.TEXT_WORLD, BaseDialog.APPLY_WORLD, BaseDialog.CANCEL_WORLD);

    private Dialog<String> areaDialog = BaseDialog.getDialog("区域地图", "请输入区域地图名称:", "是否需要添加区域地图层级"
            , BaseDialog.TEXT_AREA, BaseDialog.APPLY_AREA, BaseDialog.CANCEL_AREA);

    private Dialog<String> mapDialog = BaseDialog.getDialog("分块地图", "请输入分块地图名称:", "是否需要添加分块地图"
            , BaseDialog.TEXT_MAP, BaseDialog.APPLY_MAP, BaseDialog.CANCEL_MAP);

    private Dialog<String> linkMapBookDialog = BaseDialog.getDialog("使用说明", "地图完整的使用说明",
            """
                    1.世界地图是一个完整游戏地图的概念
                    2.区域地图是世界地图的子级.是一个区域的概念
                    3.分块地图是区域地图的子级,分块地图是一个个地图的概念
                        游戏过大时不会把全部地图信息存在一个图片里
                    4.链接地图是把区域地图下的分块地图链接在一起
                        一个区域可能会有多个分块地图,组成一个完整的区域地图
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
        treeView.setEditable(false);
        treeView.setCellFactory(TextFieldTreeCell.forTreeView(TreeWorld.treeConverter()));
//        treeView.setCellFactory(TreeWorld.treeCallback());
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
        MenuItem linkMap = new MenuItem("link Map");
        linkMap.setGraphic(new FontIcon("fas-link"));
//        linkMap.setOnAction(event -> mapDialog.showAndWait());
        contextMenu.getItems().addAll(menuWorld, new SeparatorMenuItem(), menuArea, new SeparatorMenuItem(), menuMap, new SeparatorMenuItem(), linkMap);
        return contextMenu;
    }

    public void addTreeEvent() {
        //世界地图事件添加
        Button applyWorld = (Button) worldDialog.getDialogPane().lookupButton(BaseDialog.APPLY_WORLD);
        applyWorld.setOnAction(event -> {
            String newWorld = BaseDialog.TEXT_WORLD.getText();
            if (!newWorld.isBlank()) {
                TreeItem<Object> root = treeView.getRoot();
                TreeWorld treeWorld = new TreeWorld(newWorld, UUID.randomUUID().toString(), null);
                TreeItem<Object> treeItem = new TreeItem<>(treeWorld);
                treeItem.setGraphic(new FontIcon("fas-globe"));
                root.getChildren().add(treeItem);
            }
        });
        //区域地图事件添加
        Button applyArea = (Button) areaDialog.getDialogPane().lookupButton(BaseDialog.APPLY_AREA);
        applyArea.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String newArea = BaseDialog.TEXT_AREA.getText();
                if (!newArea.isBlank()) {
                    System.out.println("newArea=" + newArea);
                }
            }
        });
        //地图事件添加
        Button applyMap = (Button) mapDialog.getDialogPane().lookupButton(BaseDialog.APPLY_MAP);
        applyMap.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String newMap = BaseDialog.TEXT_MAP.getText();
                if (!newMap.isBlank()) {
                    System.out.println("newMap=" + newMap);
                }
            }
        });
    }

    public void showTreeBookDialog(MouseEvent mouseEvent) {
        linkMapBookDialog.showAndWait();
    }
}