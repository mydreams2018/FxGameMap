package cn.kungreat.fxgamemap.custom;

import cn.kungreat.fxgamemap.ImageObject;
import cn.kungreat.fxgamemap.ImageObjectType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/*
 * 图层层级
 * 1.底图色
 * 2.背景图片
 * 3.对象图片
 * 5.顶层图片 用作图像重叠
 * */

@Setter
@Getter
public class AreaMapShow {

    /*
     * SUBSTRATE_PANE 底图层
     * FIXED_BODY_PANE 固定图像层
     * TOP_PANE 顶图层
     * */
    private static final Pane SUBSTRATE_PANE = new Pane();
    private static final Pane SUBSTRATE_IMAGE_PANE = new Pane();
    private static final Pane FIXED_BODY_PANE = new Pane();
    private static final Pane TOP_PANE = new Pane();

    /*
     * 存放每次需要处理的图片
     * */
    private static final List<TreeGameMap.BackgroundImageData> SHOW_BACKGROUND_COLOR = new LinkedList<>();
    private static final List<TreeGameMap.BackgroundImageData> SHOW_BACKGROUND_IMAGES = new LinkedList<>();
    private static final List<ImageObject> FIXED_OBJECT_LIST = new LinkedList<>();
    private static final List<ImageObject> SHOW_TOP_IMAGE = new LinkedList<>();

    /*
     * 此区域地图下的所有图片信息 坐标转换成全局的
     * allImageObject 当前还没有使用到
     * */
    private final List<TreeGameMap.BackgroundImageData> allBackgroundColor = new ArrayList<>();
    private final List<TreeGameMap.BackgroundImageData> allBackgroundImages = new ArrayList<>();
    private final List<ImageObject> allImageObject = new ArrayList<>();
    private final List<ImageObject> allTopImageObject = new ArrayList<>();
    private final List<ImageObject> allFixedImageObject = new ArrayList<>();


    private Integer groupWidth;
    private Integer groupHeight;
    private Integer currentX;
    private Integer currentY;

    private StackPane mainPane;
    private VBox outVBox;
    private HBox innerHBox;
    private ScrollBar scrollBarY;
    private ScrollBar scrollBarX;
    private WritableImage writableImage;

    public void initAreaMapShow(TreeArea treeArea) {
        if (mainPane == null) {
            groupWidth = treeArea.getWidth();
            groupHeight = treeArea.getHeight();
            mainPane = new StackPane();
            mainPane.setPrefSize(groupWidth, groupHeight);
            Rectangle clipRect = new Rectangle();
            clipRect.setWidth(groupWidth);
            clipRect.setHeight(groupHeight);
            clipRect.setSmooth(true);
            mainPane.setClip(clipRect);//超出的子无素修剪掉
            mainPane.getChildren().addAll(SUBSTRATE_PANE,SUBSTRATE_IMAGE_PANE, FIXED_BODY_PANE, TOP_PANE);
            currentX = 0;
            currentY = 0;
            initView(treeArea);
        }
        allBackgroundColor.clear();
        allBackgroundImages.clear();
        allImageObject.clear();
        allTopImageObject.clear();
        allFixedImageObject.clear();
        String[][] childrenPointName = treeArea.getChildrenPointName();
        for (int y = 0; y < treeArea.getYNumber(); y++) {
            for (int x = 0; x < treeArea.getXNumber(); x++) {
                String pointName = childrenPointName[x][y];
                if (pointName != null && !pointName.isBlank()) {
                    TreeGameMap treeGameMap = findTreeGameMap(pointName, treeArea);
                    if (treeGameMap != null) {
                        this.allBackgroundColor.add(initShowBackgroundColor(treeGameMap.getCanvasFillColor(), x, y));
                        loadTreeGameMap(treeGameMap, x, y);
                    }
                }
            }
        }
        treeArea.setSwitchTypeName("areaMapShow");
    }

    private void initView(TreeArea treeArea) {
        outVBox = new VBox();
        innerHBox = new HBox();
        scrollBarY = new ScrollBar();
        scrollBarY.setOrientation(Orientation.VERTICAL);
        scrollBarY.setMin(0);
        scrollBarY.setMax(treeArea.getYNumber() * groupHeight - groupHeight);
        scrollBarY.setBlockIncrement(groupHeight);
        innerHBox.getChildren().addAll(mainPane, scrollBarY);
        scrollBarX = new ScrollBar();
        scrollBarX.setOrientation(Orientation.HORIZONTAL);
        scrollBarX.setMin(0);
        scrollBarX.setMax(treeArea.getXNumber() * groupWidth - groupWidth);
        scrollBarX.setBlockIncrement(groupWidth);
        outVBox.getChildren().addAll(innerHBox, scrollBarX);
        outVBox.setMaxWidth(Region.USE_PREF_SIZE);
        outVBox.setMaxHeight(Region.USE_PREF_SIZE);
        scrollBarY.valueProperty().addListener((observable, oldValue, newValue) -> {
            currentY = newValue.intValue();
            clearAndDraw();
        });
        scrollBarX.valueProperty().addListener((observable, oldValue, newValue) -> {
            currentX = newValue.intValue();
            clearAndDraw();
        });
    }

    public void clearAndDraw() {
        filterShowSubstrateImages();
        filterShowMesosphereImages();
        filterShowTopImages();
        clearCacheImageView();
        startDrawSubstratePane();
        startDrawMesospherePane();
        startDrawTopPane();
    }

    private void startDrawSubstratePane() {
        //外面的一个大矩形绘制背景色
        for (TreeGameMap.BackgroundImageData backgroundColor : SHOW_BACKGROUND_COLOR) {
            ImageView view = backgroundColor.getImageView();
            view.setLayoutX(backgroundColor.getChangeX());
            view.setLayoutY(backgroundColor.getChangeY());
            if (!SUBSTRATE_PANE.getChildren().contains(view)) {
                SUBSTRATE_PANE.getChildren().add(view);
            }
        }
        //背景图层
        for (TreeGameMap.BackgroundImageData showBackgroundImage : SHOW_BACKGROUND_IMAGES) {
            ImageView view = showBackgroundImage.getImageView();
            view.setLayoutX(showBackgroundImage.getChangeX());
            view.setLayoutY(showBackgroundImage.getChangeY());
            if (!SUBSTRATE_IMAGE_PANE.getChildren().contains(view)) {
                SUBSTRATE_IMAGE_PANE.getChildren().add(view);
            }
        }
    }

    private void startDrawTopPane() {
        for (ImageObject topimageObject : SHOW_TOP_IMAGE) {
            ImageView view = topimageObject.getImageView();
            view.setLayoutX(topimageObject.getChangeX());
            view.setLayoutY(topimageObject.getChangeY());
            if (!TOP_PANE.getChildren().contains(view)) {
                TOP_PANE.getChildren().add(view);
            }
        }
    }

    private void filterShowSubstrateImages() {
        SHOW_BACKGROUND_COLOR.clear();
        SHOW_BACKGROUND_IMAGES.clear();
        List<TreeGameMap.BackgroundImageData> allBackgroundColor = this.allBackgroundColor;
        if (!allBackgroundColor.isEmpty()) {
            for (TreeGameMap.BackgroundImageData backgroundColor : allBackgroundColor) {
                if (addShowImages(backgroundColor)) {
                    SHOW_BACKGROUND_COLOR.add(backgroundColor);
                }
            }
        }
        List<TreeGameMap.BackgroundImageData> allBackgroundImages = this.allBackgroundImages;
        if (!allBackgroundImages.isEmpty()) {
            for (TreeGameMap.BackgroundImageData backgroundImage : allBackgroundImages) {
                if (addShowImages(backgroundImage)) {
                    SHOW_BACKGROUND_IMAGES.add(backgroundImage);
                }
            }
        }
    }

    private void filterShowTopImages() {
        SHOW_TOP_IMAGE.clear();
        List<ImageObject> allTopImageObject = this.allTopImageObject;
        if (!allTopImageObject.isEmpty()) {
            for (ImageObject imageObject : allTopImageObject) {
                if (addShowImages(imageObject)) {
                    SHOW_TOP_IMAGE.add(imageObject);
                }
            }
        }
    }

    private void filterShowMesosphereImages() {
        FIXED_OBJECT_LIST.clear();
        for (ImageObject internetObject : this.allFixedImageObject) {
            if (addShowImages(internetObject)) {
                FIXED_OBJECT_LIST.add(internetObject);
            }
        }
    }

    private void startDrawMesospherePane() {
        for (TreeGameMap.BackgroundImageData interObject : FIXED_OBJECT_LIST) {
            ImageView view = interObject.getImageView();
            view.setLayoutX(interObject.getChangeX());
            view.setLayoutY(interObject.getChangeY());
            if (!FIXED_BODY_PANE.getChildren().contains(view)) {
                FIXED_BODY_PANE.getChildren().add(view);
            }
        }
    }

    private boolean addShowImages(TreeGameMap.BackgroundImageData backgroundImage) {
        Image image = backgroundImage.getImageView().getImage();
        double startX = backgroundImage.getTempStartX();
        double startY = backgroundImage.getTempStartY();
        if (startY + image.getHeight() > this.currentY && startX + image.getWidth() > this.currentX &&
                startY < this.currentY + this.groupHeight && startX < this.currentX + this.groupWidth) {
            backgroundImage.setChangeX(startX - this.currentX);
            backgroundImage.setChangeY(startY - this.currentY);
            return true;
        }
        return false;
    }

    private void clearCacheImageView() {
        final ObservableList<Node> removeNode = FXCollections.observableArrayList();
        for (Node child : SUBSTRATE_PANE.getChildren()) {
            boolean isContains = false;
            for (TreeGameMap.BackgroundImageData backgroundImageData : SHOW_BACKGROUND_COLOR) {
                if (backgroundImageData.getImageView() == child) {
                    isContains = true;
                    break;
                }
            }
            if (!isContains) {
                removeNode.add(child);
            }
        }
        SUBSTRATE_PANE.getChildren().removeAll(removeNode);
        removeNode.clear();
        for (Node child : SUBSTRATE_IMAGE_PANE.getChildren()) {
            boolean isContains = false;
            for (TreeGameMap.BackgroundImageData backgroundImageData : SHOW_BACKGROUND_IMAGES) {
                if (backgroundImageData.getImageView() == child) {
                    isContains = true;
                    break;
                }
            }
            if (!isContains) {
                removeNode.add(child);
            }
        }
        SUBSTRATE_IMAGE_PANE.getChildren().removeAll(removeNode);
        removeNode.clear();
        for (Node child : FIXED_BODY_PANE.getChildren()) {
            boolean isContains = false;
            for (TreeGameMap.BackgroundImageData imageData : FIXED_OBJECT_LIST) {
                if (imageData.getImageView() == child) {
                    isContains = true;
                    break;
                }
            }
            if (!isContains) {
                removeNode.add(child);
            }
        }
        FIXED_BODY_PANE.getChildren().removeAll(removeNode);
        removeNode.clear();
        for (Node child : TOP_PANE.getChildren()) {
            boolean isContains = false;
            for (TreeGameMap.BackgroundImageData imageData : SHOW_TOP_IMAGE) {
                if (imageData.getImageView() == child) {
                    isContains = true;
                    break;
                }
            }
            if (!isContains) {
                removeNode.add(child);
            }
        }
        TOP_PANE.getChildren().removeAll(removeNode);
    }

    private void loadTreeGameMap(TreeGameMap gameMap, int pointX, int pointY) {
        int globalX = pointX * groupWidth;
        int globalY = pointY * groupHeight;
        List<TreeGameMap.BackgroundImageData> backgroundImages = gameMap.getBackgroundImages();
        if (backgroundImages != null && !backgroundImages.isEmpty()) {
            for (TreeGameMap.BackgroundImageData backgroundImage : backgroundImages) {
                backgroundImage.initImage(gameMap.getBackgroundImagePath());
                backgroundImage.setTempStartX(globalX + backgroundImage.getStartX());
                backgroundImage.setTempStartY(globalY + backgroundImage.getStartY());
                this.allBackgroundImages.add(backgroundImage);
            }
        }
        List<ImageObject> imageObjectList = gameMap.getImageObjectList();
        if (imageObjectList != null && !imageObjectList.isEmpty()) {
            for (ImageObject imageObject : imageObjectList) {
                imageObject.initImage(gameMap.getBackgroundImagePath());
                imageObject.setTempStartX(globalX + imageObject.getStartX());
                imageObject.setTempStartY(globalY + imageObject.getStartY());
                if (imageObject.getType() != null && imageObject.getType() == ImageObjectType.FIXED_BODY_TOP) {
                    this.allTopImageObject.add(imageObject);
                } else if (imageObject.getType() != null && imageObject.getType() == ImageObjectType.FIXED_BODY) {
                    this.allFixedImageObject.add(imageObject);
                } else {
                    this.allImageObject.add(imageObject);
                }
            }
        }
    }

    private TreeGameMap.BackgroundImageData initShowBackgroundColor(String colorName, int globalX, int globalY) {
        TreeGameMap.BackgroundImageData backgroundColor = new TreeGameMap.BackgroundImageData();
        WritableImage writableImage = new WritableImage(groupWidth, groupHeight);
        Color color;
        if (colorName == null) {
            color = Color.LIGHTBLUE;
        } else {
            color = Color.web(colorName);
        }
        for (int y = 0; y < groupHeight; y++) {
            for (int x = 0; x < groupWidth; x++) {
                writableImage.getPixelWriter().setColor(x, y, color);
            }
        }
        backgroundColor.setImageView(new ImageView(writableImage));
        backgroundColor.setTempStartX(globalX * groupWidth);
        backgroundColor.setTempStartY(globalY * groupHeight);
        return backgroundColor;
    }

    private TreeGameMap findTreeGameMap(String title, TreeArea treeArea) {
        List<TreeGameMap> childrenMap = treeArea.getChildrenMap();
        if (childrenMap != null && !childrenMap.isEmpty()) {
            for (TreeGameMap treeGameMap : childrenMap) {
                if (treeGameMap.getTitle().equals(title)) {
                    return treeGameMap;
                }
            }
        }
        return null;
    }
}
