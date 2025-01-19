package cn.kungreat.fxgamemap.custom;

import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Setter
@Getter
public class AreaMapShow {

    /*
     * SUBSTRATE_PANE 底图层
     * FIXED_BODY_PANE 固定图像层
     * TOP_PANE 顶图层
     * */
    private static final Pane BACK_PANE = new Pane();
    private static final Pane MIDDLE_PANE = new Pane();
    private static final Pane FRONT_PANE = new Pane();

    /*
     * 存放每次需要处理的图片
     * */
    private static final List<TreeGameMap.BackgroundImageData> SHOW_BACK_IMAGE = new LinkedList<>();
    private static final List<TreeGameMap.BackgroundImageData> SHOW_MIDDLE_IMAGE = new LinkedList<>();
    private static final List<TreeGameMap.BackgroundImageData> SHOW_FRONT_IMAGE = new LinkedList<>();
    private Integer currentX;
    private Integer currentY;
    private TreeArea treeAreaShow;

    private StackPane mainPane;
    private VBox outVBox;
    private HBox innerHBox;
    private ScrollBar scrollBarY;
    private ScrollBar scrollBarX;

    public void initAreaMapShow(TreeArea treeArea) {
        if (mainPane == null) {
            mainPane = new StackPane();
            mainPane.setPrefSize(treeArea.getWidth(), treeArea.getHeight());
            Rectangle clipRect = new Rectangle();
            clipRect.setWidth(treeArea.getWidth());
            clipRect.setHeight(treeArea.getHeight());
            clipRect.setSmooth(true);
            mainPane.setClip(clipRect);//超出的子无素修剪掉
            mainPane.getChildren().addAll(BACK_PANE, MIDDLE_PANE, FRONT_PANE);
            currentX = 0;
            currentY = 0;
            treeAreaShow = treeArea;
            initView();
        }
        treeArea.setSwitchTypeName("areaMapShow");
        clearAndDraw();
    }

    private void initView() {
        outVBox = new VBox();
        innerHBox = new HBox();
        scrollBarY = new ScrollBar();
        scrollBarY.setOrientation(Orientation.VERTICAL);
        scrollBarY.setMin(0);
        scrollBarY.setMax(treeAreaShow.getYNumber() * treeAreaShow.getHeight() - treeAreaShow.getHeight());
        scrollBarY.setBlockIncrement(treeAreaShow.getHeight());
        innerHBox.getChildren().addAll(mainPane, scrollBarY);
        scrollBarX = new ScrollBar();
        scrollBarX.setOrientation(Orientation.HORIZONTAL);
        scrollBarX.setMin(0);
        scrollBarX.setMax(treeAreaShow.getXNumber() * treeAreaShow.getWidth() - treeAreaShow.getWidth());
        scrollBarX.setBlockIncrement(treeAreaShow.getWidth());
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

    //清空 过滤 添加 -> 数据
    private void findCurrentWindowData() {
        SHOW_BACK_IMAGE.clear();
        SHOW_MIDDLE_IMAGE.clear();
        SHOW_FRONT_IMAGE.clear();
        BACK_PANE.getChildren().clear();
        MIDDLE_PANE.getChildren().clear();
        FRONT_PANE.getChildren().clear();
        //start
        int xStartNumber = currentX / treeAreaShow.getWidth();
        int yStartNumber = currentY / treeAreaShow.getHeight();
        //end
        int xEndNumber = xStartNumber + 3 > treeAreaShow.getXNumber() ? treeAreaShow.getXNumber() : xStartNumber + 3;
        int yEndNumber = yStartNumber + 3 > treeAreaShow.getYNumber() ? treeAreaShow.getYNumber() : yStartNumber + 3;
        for (int y = yStartNumber; y < yEndNumber; y++) {
            for (int x = xStartNumber; x < xEndNumber; x++) {
                String[][] childrenPointName = treeAreaShow.getChildrenPointName();
                String pointName = childrenPointName[x][y];
                if (pointName != null && !pointName.isBlank()) {
                    TreeGameMap treeGameMap = findTreeGameMap(pointName, treeAreaShow);
                    if (treeGameMap != null) {
                        loadTreeGameMap(treeGameMap, x, y);
                    }
                }
            }
        }
    }

    public void clearAndDraw() {
        findCurrentWindowData();
        startDrawAllPane();
        System.out.println(currentX + "-" + currentY);
    }

    private void startDrawAllPane() {
        for (TreeGameMap.BackgroundImageData backgroundColor : SHOW_BACK_IMAGE) {
            ImageView view = backgroundColor.getImageView();
            view.setLayoutX(backgroundColor.getChangeX());
            view.setLayoutY(backgroundColor.getChangeY());
            BACK_PANE.getChildren().add(view);
        }
        for (TreeGameMap.BackgroundImageData showBackgroundImage : SHOW_MIDDLE_IMAGE) {
            ImageView view = showBackgroundImage.getImageView();
            view.setLayoutX(showBackgroundImage.getChangeX());
            view.setLayoutY(showBackgroundImage.getChangeY());
            MIDDLE_PANE.getChildren().add(view);
        }
        for (TreeGameMap.BackgroundImageData interObject : SHOW_FRONT_IMAGE) {
            ImageView view = interObject.getImageView();
            view.setLayoutX(interObject.getChangeX());
            view.setLayoutY(interObject.getChangeY());
            FRONT_PANE.getChildren().add(view);
        }
    }

    private boolean addShowImages(TreeGameMap.BackgroundImageData backgroundImage) {
        Image image = backgroundImage.getImageView().getImage();
        double startX = backgroundImage.getTempStartX();
        double startY = backgroundImage.getTempStartY();
        if (startY + image.getHeight() > this.currentY && startX + image.getWidth() > this.currentX &&
                startY < this.currentY + treeAreaShow.getHeight() && startX < this.currentX + treeAreaShow.getWidth()) {
            backgroundImage.setChangeX(startX - this.currentX);
            backgroundImage.setChangeY(startY - this.currentY);
            return true;
        }
        return false;
    }

    private void loadTreeGameMap(TreeGameMap gameMap, int pointX, int pointY) {
        int globalX = pointX * treeAreaShow.getWidth();
        int globalY = pointY * treeAreaShow.getHeight();
        List<TreeGameMap.BackgroundImageData> backgroundImages = gameMap.getBackgroundImages();
        if (backgroundImages != null && !backgroundImages.isEmpty()) {
            for (TreeGameMap.BackgroundImageData backgroundImage : backgroundImages) {
                backgroundImage.initImage(gameMap.getBackgroundImagePath());
                backgroundImage.setTempStartX(globalX + backgroundImage.getStartX());
                backgroundImage.setTempStartY(globalY + backgroundImage.getStartY());
                if (addShowImages(backgroundImage)) {
                    if (backgroundImage.getMirImageMark() == 4) {
                        SHOW_MIDDLE_IMAGE.add(backgroundImage);
                    } else if (backgroundImage.getMirImageMark() == 3 || backgroundImage.getMirImageMark() == 2) {
                        if (backgroundImage.getMirImageMark() == 3) {
                            backgroundImage.getImageView().setViewOrder(3);
                        } else {
                            backgroundImage.getImageView().setViewOrder(2);
                            backgroundImage.getImageView().setBlendMode(BlendMode.ADD);
                        }
                        SHOW_FRONT_IMAGE.add(backgroundImage);
                    } else {
                        SHOW_BACK_IMAGE.add(backgroundImage);
                    }
                }
            }
        }
    }

    public static TreeGameMap findTreeGameMap(String title, TreeArea treeArea) {
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
