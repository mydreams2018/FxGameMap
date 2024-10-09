package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.TreeArea;
import cn.kungreat.fxgamemap.custom.TreeGameMap;
import cn.kungreat.fxgamemap.custom.TreeWorld;
import cn.kungreat.fxgamemap.util.PatternUtils;
import cn.kungreat.fxgamemap.util.PropertyListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.net.URL;
import java.util.*;

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

    private static final Dialog<Boolean> BATCH_CHANGE_IMAGE_OBJECT = BaseDialog.getBatchChangeImageObjectDialog();

    private static final Dialog<Boolean> RESOURCE_ANIMATION_DIALOG = BaseDialog.getResourceAnimationDialog();

    public static final Color CANVAS_DEFAULT_COLOR = Color.LIGHTBLUE;

    @FXML
    private HBox topHBox;
    @FXML
    private RadioButton topPaintingMode;
    @FXML
    private RadioButton topMovingMode;
    @FXML
    private RadioButton topDeletingMode;
    @FXML
    private TextField canvasMarkLineWidth;
    @FXML
    private TextField canvasMarkLineHeight;
    @FXML
    private ColorPicker canvasColorPicker;
    @FXML
    private StackPane stackPaneLeft;
    @FXML
    private HBox stackPaneLeftHBox;
    @FXML
    private ScrollPane scrollPaneCenter;
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
        topHBox.getChildren().addFirst(mainMenuBar);
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
        addScrollPaneCenterEvent();
        addCanvasColorPickerEvent();
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
            String areaWidth = BaseDialog.TEXT_AREA_WIDTH.getText();
            String areaHeight = BaseDialog.TEXT_AREA_HEIGHT.getText();
            String bgAudio = BaseDialog.TEXT_AREA_BG_AUDIO.getText();
            if (!newArea.isBlank() && !areaXText.isBlank() && !areaYText.isBlank() && !areaWidth.isBlank() && !areaHeight.isBlank()
                    && PatternUtils.NumberRegex.matcher(areaXText).matches() && PatternUtils.NumberRegex.matcher(areaYText).matches()
                    && PatternUtils.NumberRegex.matcher(areaWidth).matches() && PatternUtils.NumberRegex.matcher(areaHeight).matches()) {
                TreeItem<Object> item = treeView.getFocusModel().getFocusedItem();
                if (item != null && item.getValue() instanceof TreeWorld treeWorld) {
                    TreeArea treeArea = new TreeArea(newArea, UUID.randomUUID().toString(),
                            Integer.parseInt(areaXText), Integer.parseInt(areaYText), new ArrayList<>(),
                            treeWorld.getTitle() + File.separator + newArea, Integer.parseInt(areaWidth), Integer.parseInt(areaHeight), bgAudio);
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
            if (!title.isBlank()) {
                TreeItem<Object> item = treeView.getFocusModel().getFocusedItem();
                if (item != null && item.getValue() instanceof TreeArea treeArea) {
                    TreeGameMap treeGameMap = new TreeGameMap(UUID.randomUUID().toString(), title,
                            treeArea.getWidth(), treeArea.getHeight(), treeArea.getImageDirectory() + File.separator + title);
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
                    rightTopScrollPaneAccordion.getPanes().clear();
                    scrollPaneCenterInHBox.getChildren().clear();
                    treeGameMap.initCanvas();
                    scrollPaneCenterInHBox.getChildren().add(treeGameMap.getCanvas());
                } else if (value instanceof TreeArea treeArea) {
                    scrollPaneCenterInHBox.getChildren().clear();
                    treeArea.initGridPane();
                    scrollPaneCenterInHBox.getChildren().add(treeArea.getGridPane());
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
            if (!sridName.isBlank() && !sridPath.isBlank() && !sridWidth.isBlank() && !sridHeight.isBlank()
                    && PatternUtils.NumberRegex.matcher(sridWidth).matches() && PatternUtils.NumberRegex.matcher(sridHeight).matches()) {
                Integer coverSridMargin = sridMargin.isBlank() ? 0 : Integer.parseInt(sridMargin);
                Integer coverSridPadding = sridPadding.isBlank() ? 0 : Integer.parseInt(sridPadding);
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
    public void addResourceAnimation() {
        Optional<Boolean> optionalB = RESOURCE_ANIMATION_DIALOG.showAndWait();
        if (optionalB.isPresent() && optionalB.get()) {
            String tabName = BaseDialog.RESOURCE_ANIMATION_NAME.getText();
            String tabPath = BaseDialog.RESOURCE_ANIMATION_PATH.getText();
            if (!tabName.isBlank() && !tabPath.isBlank()) {
                ResourceAnimation resourceAnimation = new ResourceAnimation(UUID.randomUUID().toString(), tabName, tabPath);
                resourceAnimation.initTab();
                tabPaneRight.getTabs().add(resourceAnimation.getTab());
                RootApplication.RESOURCES.getResourceAnimations().add(resourceAnimation);
                PropertyListener.changeIsSaved(false);
            }
        }
    }

    public void addScrollPaneCenterEvent() {
        scrollPaneCenter.setOnKeyPressed(event -> {
            TreeGameMap.BackgroundImageData chooseCanvasImage = PropertyListener.getChooseCanvasImage();
            if (topMovingMode.isSelected() && chooseCanvasImage != null) {
                if (event.getCode() == KeyCode.W) {
                    chooseCanvasImage.setStartY(chooseCanvasImage.getStartY() - 1);
                    canvasClearAndDraw();
                } else if (event.getCode() == KeyCode.S) {
                    chooseCanvasImage.setStartY(chooseCanvasImage.getStartY() + 1);
                    canvasClearAndDraw();
                } else if (event.getCode() == KeyCode.D) {
                    chooseCanvasImage.setStartX(chooseCanvasImage.getStartX() + 1);
                    canvasClearAndDraw();
                } else if (event.getCode() == KeyCode.A) {
                    chooseCanvasImage.setStartX(chooseCanvasImage.getStartX() - 1);
                    canvasClearAndDraw();
                }
            }
        });
    }

    private void canvasClearAndDraw() {
        TreeItem<Object> item = treeView.getFocusModel().getFocusedItem();
        if (item != null && item.getValue() instanceof TreeGameMap treeGameMap) {
            treeGameMap.clearAndDraw();
            PropertyListener.changeIsSaved(false);
        }
    }

    @FXML
    public void addCanvasMarkLine() {
        TreeItem<Object> item = treeView.getFocusModel().getFocusedItem();
        if (item != null && item.getValue() instanceof TreeGameMap treeGameMap) {
            String markLineWidth = canvasMarkLineWidth.getText();
            String markLineHeight = canvasMarkLineHeight.getText();
            if (PatternUtils.NumberRegex.matcher(markLineWidth).matches()
                    && PatternUtils.NumberRegex.matcher(markLineHeight).matches()) {
                treeGameMap.setMarkLine(new TreeGameMap.MarkLine(Integer.parseInt(markLineWidth), Integer.parseInt(markLineHeight)));
            } else {
                treeGameMap.setMarkLine(null);
            }
            treeGameMap.clearAndDraw();
        }
    }

    public void addCanvasColorPickerEvent() {
        canvasColorPicker.setValue(RootController.CANVAS_DEFAULT_COLOR);
        canvasColorPicker.setOnAction(event -> {
            TreeItem<Object> item = treeView.getFocusModel().getFocusedItem();
            if (item != null && item.getValue() instanceof TreeGameMap treeGameMap) {
                treeGameMap.getGraphicsContext().setFill(canvasColorPicker.getValue());
                treeGameMap.setCanvasFillColor(canvasColorPicker.getValue().toString());
                treeGameMap.clearAndDraw();
                PropertyListener.changeIsSaved(false);
            }
        });
    }

    @FXML
    public void switchTreeAreaView() {
        PropertyListener.setSwitchTreeArea();
    }

    @FXML
    public void batchChangeImageObject() {
        TreeItem<Object> item = treeView.getFocusModel().getFocusedItem();
        if (item != null && item.getValue() instanceof TreeGameMap treeGameMap) {
            if (!treeGameMap.getImageObjectList().isEmpty()) {
                ObservableList<Node> vboxChildren = BaseDialog.BATCH_IMAGE_VBOX.getChildren();
                vboxChildren.clear();
                for (ImageObject imageObject : treeGameMap.getImageObjectList()) {
                    RadioButton radioButton = new RadioButton(imageObject.getTitle());
                    vboxChildren.add(radioButton);
                }
                //填充多选数据数据
                Optional<Boolean> optional = BATCH_CHANGE_IMAGE_OBJECT.showAndWait();
                if (optional.isPresent() && optional.get()) {
                    for (Node vboxChild : vboxChildren) {
                        if (vboxChild instanceof RadioButton radioButton) {
                            if (radioButton.isSelected()) {
                                ImageObject findResult = findImageObjectByTitle(radioButton.getText(), treeGameMap.getImageObjectList());
                                if (findResult != null) {
                                    findResult.setType(BaseDialog.BATCH_IMAGE_OBJECT.getType());
                                    findResult.setLevel(BaseDialog.BATCH_IMAGE_OBJECT.getLevel());
                                    findResult.setPhysical(BaseDialog.BATCH_IMAGE_OBJECT.isPhysical());
                                    findResult.setMaxActivityScope(BaseDialog.BATCH_IMAGE_OBJECT.getMaxActivityScope());
                                    findResult.setMoveSpeed(BaseDialog.BATCH_IMAGE_OBJECT.getMoveSpeed());
                                    findResult.setActionType(BaseDialog.BATCH_IMAGE_OBJECT.getActionType());
                                    findResult.setAnimationName(BaseDialog.BATCH_IMAGE_OBJECT.getAnimationName());
                                    findResult.refresh();
                                }
                            }
                        }
                    }
                    PropertyListener.changeIsSaved(false);
                    BATCH_CHANGE_IMAGE_OBJECT.setResult(false);
                }
            }
        }
    }

    private static ImageObject findImageObjectByTitle(String title, List<ImageObject> imageObjectList) {
        for (ImageObject imageObject : imageObjectList) {
            if (imageObject.getTitle().equals(title)) {
                return imageObject;
            }
        }
        return null;
    }

    public static void addBatchChangeImageObjectEvent() {
        Button imageObjectBatchOk = (Button) BATCH_CHANGE_IMAGE_OBJECT.getDialogPane().lookupButton(BaseDialog.BATCH_IMAGE_APPLY);
        Button imageObjectBatchCancel = (Button) BATCH_CHANGE_IMAGE_OBJECT.getDialogPane().lookupButton(BaseDialog.BATCH_IMAGE_CANCEL);
        imageObjectBatchOk.setOnAction(event -> BATCH_CHANGE_IMAGE_OBJECT.setResult(true));
        imageObjectBatchCancel.setOnAction(event -> BATCH_CHANGE_IMAGE_OBJECT.setResult(false));
    }
}
