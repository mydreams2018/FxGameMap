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
import javafx.scene.layout.VBox;
import lombok.Getter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

@Getter
public class RootController implements Initializable {

    private static final Dialog<String> WORLD_DIALOG = BaseDialog.getDialog("世界地图", "请输入世界地图名称:", "是否需要添加世界地图层级"
            , BaseDialog.TEXT_WORLD, BaseDialog.APPLY_WORLD, BaseDialog.CANCEL_WORLD);

    private static final Dialog<String> AREA_DIALOG = BaseDialog.getDialog("区域地图", "请输入区域地图信息:", "是否需要添加区域地图层级"
            , BaseDialog.getAreaRectangular(), BaseDialog.APPLY_AREA, BaseDialog.CANCEL_AREA);

    private static final Dialog<String> MAP_DIALOG = BaseDialog.getDialog("分块地图", "请输入分块地图信息:", "是否需要添加分块地图"
            , BaseDialog.getMapRectangular(), BaseDialog.APPLY_MAP, BaseDialog.CANCEL_MAP);

    private static final Dialog<String> LINK_MAP_BOOK_DIALOG = BaseDialog.getDialog("使用说明", "地图完整的使用说明",
            """
                    1.世界地图是一个完整游戏地图的概念
                    2.区域地图是世界地图的子级.是一个区域的概念
                    3.分块地图是区域地图的子级,分块地图是一个个地图的概念
                        游戏过大时不会把全部地图信息存在一个图片里
                    """
            , null, ButtonType.CLOSE);

    private static final Dialog<String> SEGMENT_RESOURCE_IMAGES_DIALOG = BaseDialog.getSegmentResourceImagesDialog();

    private static final Dialog<String> IMAGE_OBJECT_DIALOG = BaseDialog.getDialog("图片对象", "请输入图片对象信息", "确定添加此图片对象信息"
            , BaseDialog.IMAGE_OBJECT_NAME, BaseDialog.APPLY_IMAGE_OBJECT, BaseDialog.CANCEL_IMAGE_OBJECT);

    @FXML
    private HBox topHBox;
    @FXML
    private StackPane stackPaneLeft;
    @FXML
    private HBox stackPaneLeftHBox;
    @FXML
    private StackPane stackPaneCenter;
    @FXML
    private HBox scrollPaneCenterInHBox;
    @FXML
    private VBox rightTopOutVBox;
    @FXML
    private HBox rightTopInHbox;
    @FXML
    private RadioButton radioButtonIsObject;
    @FXML
    private ScrollPane rightTopScrollPane;
    @FXML
    private Accordion rightTopScrollPaneAccordion;
    @FXML
    private VBox tabPaneRightVbox;
    @FXML
    private HBox tabPaneRightHBox;
    @FXML
    private TabPane tabPaneRight;
    @FXML
    private TreeView<Object> treeView;

    private MainMenuBar mainMenuBar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainMenuBar = new MainMenuBar();
        topHBox.getChildren().add(mainMenuBar);
        treeView.setEditable(false);
        treeView.setCellFactory(TextFieldTreeCell.forTreeView(TreeWorld.treeConverter()));
        treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        treeView.prefHeightProperty().bind(stackPaneLeft.heightProperty().subtract(stackPaneLeftHBox.heightProperty()));
        treeView.setContextMenu(getTreeContextMenu());
        tabPaneRight.prefHeightProperty().bind(tabPaneRightVbox.heightProperty().subtract(tabPaneRightHBox.heightProperty()));
        rightTopScrollPane.prefHeightProperty().bind(rightTopOutVBox.heightProperty().subtract(rightTopInHbox.heightProperty()));
        rightTopScrollPaneAccordion.prefWidthProperty().bind(rightTopScrollPane.widthProperty().subtract(3));
        addTreeEvent();
        addSegmentResourceImgEvent();
        addImageObjectEvent();
    }

    public ContextMenu getTreeContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuWorld = new MenuItem("add world");
        menuWorld.setGraphic(new FontIcon("fas-globe"));
        menuWorld.setOnAction(event -> WORLD_DIALOG.showAndWait());
        MenuItem menuArea = new MenuItem("add Area");
        menuArea.setGraphic(new FontIcon("fas-chart-area"));
        menuArea.setOnAction(event -> AREA_DIALOG.showAndWait());
        MenuItem menuMap = new MenuItem("add Map");
        menuMap.setGraphic(new FontIcon("fas-map"));
        menuMap.setOnAction(event -> MAP_DIALOG.showAndWait());
        contextMenu.getItems().addAll(menuWorld, new SeparatorMenuItem(), menuArea, new SeparatorMenuItem(), menuMap);
        return contextMenu;
    }

    public void addTreeEvent() {
        WORLD_DIALOG.setOnShowing(event -> BaseDialog.TEXT_WORLD.clear());
        AREA_DIALOG.setOnShowing(event -> BaseDialog.TEXT_AREA.clear());
        MAP_DIALOG.setOnShowing(event -> BaseDialog.TEXT_MAP.clear());
        //世界地图事件添加
        Button applyWorld = (Button) WORLD_DIALOG.getDialogPane().lookupButton(BaseDialog.APPLY_WORLD);
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
        Button applyArea = (Button) AREA_DIALOG.getDialogPane().lookupButton(BaseDialog.APPLY_AREA);
        applyArea.setOnAction(event -> {
            String newArea = BaseDialog.TEXT_AREA.getText();
            String areaXText = BaseDialog.TEXT_AREAX.getText();
            String areaYText = BaseDialog.TEXT_AREAY.getText();
            if (!newArea.isBlank() && !areaXText.isBlank() && !areaYText.isBlank()
                    && Pattern.matches(PatternUtils.NumberRegex, areaXText) && Pattern.matches(PatternUtils.NumberRegex, areaYText)) {
                TreeItem<Object> item = treeView.getFocusModel().getFocusedItem();
                if (item != null && item.getValue() instanceof TreeWorld treeWorld) {
                    TreeArea treeArea = new TreeArea(newArea, UUID.randomUUID().toString(),
                            Integer.parseInt(areaXText), Integer.parseInt(areaYText), new ArrayList<>(),
                            treeWorld.getTitle() + File.separator + newArea);
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
        Button applyMap = (Button) MAP_DIALOG.getDialogPane().lookupButton(BaseDialog.APPLY_MAP);
        applyMap.setOnAction(event -> {
            String title = BaseDialog.TEXT_MAP.getText();
            String mapWidth = BaseDialog.TEXT_MAP_WIDTH.getText();
            String mapHeight = BaseDialog.TEXT_MAP_HEIGHT.getText();
            if (!title.isBlank() && !mapWidth.isBlank() && !mapHeight.isBlank()
                    && Pattern.matches(PatternUtils.NumberRegex, mapWidth) && Pattern.matches(PatternUtils.NumberRegex, mapHeight)) {
                TreeItem<Object> item = treeView.getFocusModel().getFocusedItem();
                if (item != null && item.getValue() instanceof TreeArea treeArea) {
                    TreeGameMap treeGameMap = new TreeGameMap(UUID.randomUUID().toString(), title,
                            Integer.parseInt(mapWidth), Integer.parseInt(mapHeight), treeArea.getImageDirectory() + File.separator + title);
                    treeArea.getChildrenMap().add(treeGameMap);
                    TreeItem<Object> treeItem = new TreeItem<>(treeGameMap);
                    treeItem.setGraphic(new FontIcon("fas-map"));
                    item.getChildren().add(treeItem);
                    item.setExpanded(true);
                    PropertyListener.changeIsSaved(false);
                }
            }
        });
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Object value = newValue.getValue();
                if (value instanceof TreeGameMap treeGameMap) {
                    treeGameMap.initCanvas();
                    scrollPaneCenterInHBox.getChildren().clear();
                    scrollPaneCenterInHBox.getChildren().add(treeGameMap.getCanvas());
                } else if (value instanceof TreeArea treeArea) {
                    System.out.println(treeArea.getImageDirectory());
                } else if (value instanceof TreeWorld treeWorld) {
                    System.out.println(treeWorld);
                }
            }
        });
    }

    @FXML
    public void showTreeBookDialog(MouseEvent mouseEvent) {
        LINK_MAP_BOOK_DIALOG.showAndWait();
    }

    @FXML
    public void addResourceImg() {
        List<File> selectedFiles = ResourceTab.FILE_CHOOSER.showOpenMultipleDialog(RootApplication.mainStage);
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            ResourceTab resourceTab = new ResourceTab(selectedFiles, UUID.randomUUID().toString());
            resourceTab.initTab();
            tabPaneRight.getTabs().add(resourceTab.getTab());
            RootApplication.RESOURCES.getResourceTabList().add(resourceTab);
            PropertyListener.changeIsSaved(false);
        }
    }

    @FXML
    public void addSegmentResourceImg() {
        SEGMENT_RESOURCE_IMAGES_DIALOG.showAndWait();
    }

    public void addSegmentResourceImgEvent() {
        SEGMENT_RESOURCE_IMAGES_DIALOG.setOnShowing(event -> {
            BaseDialog.SRID_NAME.clear();
            BaseDialog.SRID_PATH.clear();
        });
        //资源图片分割事件
        Button segmentResourceImgOk = (Button) SEGMENT_RESOURCE_IMAGES_DIALOG.getDialogPane().lookupButton(BaseDialog.APPLY_SRID);
        segmentResourceImgOk.setOnAction(event -> {
            String sridName = BaseDialog.SRID_NAME.getText();
            String sridPath = BaseDialog.SRID_PATH.getText();
            String sridWidth = BaseDialog.SRID_WIDTH.getText();
            String sridHeight = BaseDialog.SRID_HEIGHT.getText();
            String sridMargin = BaseDialog.SRID_MARGIN.getText();
            String sridPadding = BaseDialog.SRID_PADDING.getText();
            if (sridName != null && sridPath != null && sridWidth != null && sridHeight != null
                    && !sridName.isBlank() && !sridPath.isBlank() && !sridWidth.isBlank() && !sridHeight.isBlank()
                    && Pattern.matches(PatternUtils.NumberRegex, sridWidth) && Pattern.matches(PatternUtils.NumberRegex, sridHeight)) {
                Integer coverSridMargin = sridMargin == null ? 0 : Integer.parseInt(sridMargin);
                Integer coverSridPadding = sridPadding == null ? 0 : Integer.parseInt(sridPadding);
                SegmentResourceTab segmentResourceTab = new SegmentResourceTab(UUID.randomUUID().toString(),
                        sridName, sridPath, Integer.parseInt(sridWidth), Integer.parseInt(sridHeight), coverSridMargin, coverSridPadding);
                segmentResourceTab.initTab();
                tabPaneRight.getTabs().add(segmentResourceTab.getTab());
                RootApplication.RESOURCES.getSegmentResourceTabList().add(segmentResourceTab);
                PropertyListener.changeIsSaved(false);
            }
        });
    }

    @FXML
    public void addImageObject() {
        IMAGE_OBJECT_DIALOG.showAndWait();
    }

    public void addImageObjectEvent() {
        IMAGE_OBJECT_DIALOG.setOnShowing(event -> {
            BaseDialog.IMAGE_OBJECT_NAME.clear();
        });
        Button imageObjectOk = (Button) IMAGE_OBJECT_DIALOG.getDialogPane().lookupButton(BaseDialog.APPLY_IMAGE_OBJECT);
        imageObjectOk.setOnAction(event -> {
            String objectNameText = BaseDialog.IMAGE_OBJECT_NAME.getText();
            if (objectNameText != null && !objectNameText.isBlank()) {
                ImageObject imageObject = new ImageObject(UUID.randomUUID().toString(), objectNameText);
                imageObject.initTitledPane();
                rightTopScrollPaneAccordion.getPanes().add(imageObject.getTitledPane());
            }
        });
    }
}