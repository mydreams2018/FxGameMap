package cn.kungreat.fxgamemap.custom;

import cn.kungreat.fxgamemap.*;
import cn.kungreat.fxgamemap.util.PropertyListener;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Setter
@Getter
public class AreaMapShow {

    /*
     * 没有清理TreeGameMap内的Image ImageView数据 [在正式的游戏引擎要清理]
     * */
    private final Pane BACK_PANE = new Pane();
    private final Pane MIDDLE_PANE = new Pane();
    private final Pane FRONT_PANE = new Pane();

    /*
     * 存放每次需要处理的图片
     * */
    private static final List<TreeGameMap.BackgroundImageData> SHOW_BACK_IMAGE = new LinkedList<>();
    private static final List<TreeGameMap.BackgroundImageData> SHOW_MIDDLE_IMAGE = new LinkedList<>();
    private static final List<TreeGameMap.BackgroundImageData> SHOW_FRONT_IMAGE = new LinkedList<>();
    private int currentX;
    private int currentY;
    private TreeArea treeAreaShow;

    private BorderPane mapBorderPane;
    private Pane mainPane;
    private ScrollBar scrollBarY;
    private ScrollBar scrollBarX;

    private static final Image POINT_SHOW_IMAGE = new Image(RootApplication.class.getResourceAsStream("pointImage.png"));

    public void initAreaMapShow(TreeArea treeArea) {
        if (mainPane == null) {
            mainPane = new Pane();
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
            mainPaneMouseEvent();
        }
        treeArea.setSwitchTypeName("areaMapShow");
        clearAndDraw();
    }

    private void mainPaneMouseEvent() {
        mainPane.setOnMouseMoved(event -> {
            RootController controller = RootApplication.mainFXMLLoader.getController();
            ImageView chooseResourceImage = PropertyListener.getChooseResourceImage();
            clearAndDraw();
            if (controller.getTopPaintingMode().isSelected() && chooseResourceImage != null) {
                Image image = chooseResourceImage.getImage();
                ImageView imageView = new ImageView(image);
                imageView.setLayoutX(event.getX() - (image.getWidth() / 2));
                imageView.setLayoutY(event.getY() - (image.getHeight() / 2));
                FRONT_PANE.getChildren().add(imageView);
            } else if (controller.getTopDeletingMode().isSelected()) {
                ImageView imageView = new ImageView(TreeGameMap.DELETE_IMAGE);
                imageView.setLayoutX(event.getX() - (TreeGameMap.DELETE_IMAGE.getWidth() / 2));
                imageView.setLayoutY(event.getY() - (TreeGameMap.DELETE_IMAGE.getHeight() / 2));
                FRONT_PANE.getChildren().add(imageView);
            }
        });
        mainPane.setOnMouseExited(event -> clearAndDraw());
        mainPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                RootController controller = RootApplication.mainFXMLLoader.getController();
                ImageView chooseResourceImage = PropertyListener.getChooseResourceImage();
                //拿到当前的TreeGameMap
                int currentGlobalStartX = (int) (currentX + event.getX());
                int currentGlobalStartY = (int) (currentY + event.getY());
                Integer areaWidth = treeAreaShow.getWidth();
                Integer areaHeight = treeAreaShow.getHeight();
                int locatorX = (currentGlobalStartX % areaWidth) / 48;
                int locatorY = (currentGlobalStartY % areaHeight) / 32;
                TreeGameMap currentTreeGameMap = findTreeGameMap(treeAreaShow.getChildrenPointName()[currentGlobalStartX / areaWidth][currentGlobalStartY / areaHeight], treeAreaShow);
                if (currentTreeGameMap != null) {
                    if (controller.getTopPaintingMode().isSelected() && chooseResourceImage != null && !controller.getRadioButtonMonster().isSelected()
                            && !controller.getRadioButtonNpc().isSelected()) {
                        Image image = chooseResourceImage.getImage();
                        double startX = currentGlobalStartX % areaWidth - (image.getWidth() / 2);
                        double startY = currentGlobalStartY % areaHeight - (image.getHeight() / 2);
                        String imagePath;
                        if (image.getUrl() != null) {
                            currentTreeGameMap.addSaveImgPaths(image.getUrl());
                            imagePath = image.getUrl();
                        } else {
                            //分割图片时用Id暂存的图片路径
                            currentTreeGameMap.addSaveImgPaths(chooseResourceImage.getId());
                            imagePath = chooseResourceImage.getId();
                        }
                        if (controller.getRadioButtonIsObject().isSelected()) {
                            //默认是动画 FIXED_ANIMATION
                            ImageObject changeImageObject = new ImageObject(UUID.randomUUID().toString(), image, startX, startY, imagePath, locatorX, locatorY);
                            changeImageObject.setTitle(changeImageObject.getImagePath());
                            if (AnimationConfig.ANIMTATION_MAP.get(changeImageObject.getImagePath()) != null) {
                                changeImageObject.setBaseAnimationName(AnimationConfig.ANIMTATION_MAP.get(changeImageObject.getImagePath()));
                            }
                            currentTreeGameMap.getImageObjectList().add(changeImageObject);
                        } else {
                            currentTreeGameMap.getBackgroundImages().add(new TreeGameMap.BackgroundImageData(image, startX, startY, imagePath, null, null));
                        }
                        PropertyListener.changeIsSaved(false);
                    } else if (controller.getTopPaintingMode().isSelected() && controller.getRadioButtonMonster().isSelected()) {
                        //怪物标记模式
                        ImageObject monsterObject = new ImageObject(UUID.randomUUID().toString(), locatorX * 48, locatorY * 32, locatorX, locatorY);
                        monsterObject.setType(ImageObjectType.MONSTER);
                        monsterObject.setAnimationIndex(controller.getMonsterChoiceBox().getValue());
                        monsterObject.setTitle(controller.getMonsterChoiceBox().getValue());
                        currentTreeGameMap.getImageObjectList().add(monsterObject);
                    } else if (controller.getTopPaintingMode().isSelected() && controller.getRadioButtonNpc().isSelected()) {
                        //npc标记模式
                        ImageObject npcObject = new ImageObject(UUID.randomUUID().toString(), locatorX * 48, locatorY * 32, locatorX, locatorY);
                        npcObject.setType(ImageObjectType.NPC);
                        npcObject.setAnimationIndex(controller.getNpcChoiceBox().getValue());
                        npcObject.setTitle(controller.getNpcChoiceBox().getValue());
                        currentTreeGameMap.getImageObjectList().add(npcObject);
                    } else if (controller.getTopDeletingMode().isSelected()) {
                        if (controller.getRadioButtonIsObject().isSelected()) {
                            ImageObject removeImageObject = currentTreeGameMap.getImageObjectData(currentGlobalStartX % areaWidth, currentGlobalStartY % areaHeight);
                            if (removeImageObject != null) {
                                currentTreeGameMap.getImageObjectList().remove(removeImageObject);
                                clearAndDraw();
                            }
                        } else if (controller.getRadioButtonMonster().isSelected()) {
                            //怪物标记delete模式
                            ImageObject monsterObject = currentTreeGameMap.getMonsterObject(locatorX, locatorY);
                            if (monsterObject != null) {
                                currentTreeGameMap.getImageObjectList().remove(monsterObject);
                                clearAndDraw();
                            }
                        } else if (controller.getRadioButtonNpc().isSelected()) {
                            //npc标记 delete模式
                            ImageObject monsterObject = currentTreeGameMap.getNpcObject(locatorX, locatorY);
                            if (monsterObject != null) {
                                currentTreeGameMap.getImageObjectList().remove(monsterObject);
                                clearAndDraw();
                            }
                        } else {
                            TreeGameMap.BackgroundImageData backgroundImageData = currentTreeGameMap.getBackgroundImageData(currentGlobalStartX % areaWidth, currentGlobalStartY % areaHeight);
                            if (backgroundImageData != null) {
                                currentTreeGameMap.getBackgroundImages().remove(backgroundImageData);
                                clearAndDraw();
                            }
                        }
                    } else if (controller.getTopMovingMode().isSelected()) {
                        ImageObject chooserObject = null;
                        if (controller.getRadioButtonIsObject().isSelected()) {
                            chooserObject = currentTreeGameMap.getImageObjectData(currentGlobalStartX % areaWidth, currentGlobalStartY % areaHeight);
                            PropertyListener.setChooseCanvasImage(chooserObject);
                        } else if (controller.getRadioButtonMonster().isSelected()) {
                            chooserObject = currentTreeGameMap.getMonsterObject(locatorX, locatorY);
                        } else if (controller.getRadioButtonNpc().isSelected()) {
                            chooserObject = currentTreeGameMap.getNpcObject(locatorX, locatorY);
                        } else {
                            //背景图片
                            PropertyListener.setChooseCanvasImage(currentTreeGameMap.getBackgroundImageData(currentGlobalStartX % areaWidth, currentGlobalStartY % areaHeight));
                        }
                        if (chooserObject != null) {
                            chooserObject.initTitledPane();
                            controller.getRightTopScrollPaneAccordion().getPanes().clear();
                            controller.getRightTopScrollPaneAccordion().getPanes().add(chooserObject.getTitledPane());
                        }
                    } else if (controller.getMapLockEditMode().isSelected()) {
                        //切换当前锁
                        boolean[][] basePointLockList = treeAreaShow.getBasePointLockList();
                        boolean bl = basePointLockList[currentGlobalStartX / 48][currentGlobalStartY / 32];
                        basePointLockList[currentGlobalStartX / 48][currentGlobalStartY / 32] = !bl;
                    }
                }
            }
        });
    }

    private void initView() {
        mapBorderPane = new BorderPane();
        scrollBarY = new ScrollBar();
        scrollBarY.setOrientation(Orientation.VERTICAL);
        scrollBarY.setMin(0);
        scrollBarY.setMax(treeAreaShow.getYNumber() * treeAreaShow.getHeight() - treeAreaShow.getHeight());
        scrollBarY.setBlockIncrement(treeAreaShow.getHeight());
        scrollBarX = new ScrollBar();
        scrollBarX.setOrientation(Orientation.HORIZONTAL);
        scrollBarX.setMin(0);
        scrollBarX.setMax(treeAreaShow.getXNumber() * treeAreaShow.getWidth() - treeAreaShow.getWidth());
        scrollBarX.setBlockIncrement(treeAreaShow.getWidth());
        mapBorderPane.setBottom(scrollBarX);
        mapBorderPane.setRight(scrollBarY);
        mapBorderPane.setCenter(mainPane);
        //限制 VBox 的最大尺寸 防止它被父容器无限拉伸 让它的大小始终由它内部的内容来决定
//      setMaxWidth(Region.USE_PREF_SIZE);
//      setMaxHeight(Region.USE_PREF_SIZE);
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
        AreaMapShow.cleanCache();
        BACK_PANE.getChildren().clear();
        MIDDLE_PANE.getChildren().clear();
        FRONT_PANE.getChildren().clear();
        //start
        int xStartNumber = currentX / treeAreaShow.getWidth();
        int yStartNumber = currentY / treeAreaShow.getHeight();
        xStartNumber = xStartNumber > 0 ? xStartNumber - 1 : 0;
        yStartNumber = yStartNumber > 0 ? yStartNumber - 1 : 0;
        //end
        int xEndNumber = xStartNumber + 4 > treeAreaShow.getXNumber() ? treeAreaShow.getXNumber() : xStartNumber + 4;
        int yEndNumber = yStartNumber + 4 > treeAreaShow.getYNumber() ? treeAreaShow.getYNumber() : yStartNumber + 4;
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

    public static void cleanCache() {
        SHOW_BACK_IMAGE.clear();
        SHOW_MIDDLE_IMAGE.clear();
        SHOW_FRONT_IMAGE.clear();
    }

    public void clearAndDraw() {
        findCurrentWindowData();
        startDrawAllPane();
        startDrawPointLock();
    }

    private void startDrawAllPane() {
        for (TreeGameMap.BackgroundImageData backImage : SHOW_BACK_IMAGE) {
            ImageView view = backImage.getImageView();
            view.setLayoutX(backImage.getChangeX());
            view.setLayoutY(backImage.getChangeY());
            BACK_PANE.getChildren().add(view);
        }
        for (TreeGameMap.BackgroundImageData middleImage : SHOW_MIDDLE_IMAGE) {
            ImageView view = middleImage.getImageView();
            view.setLayoutX(middleImage.getChangeX());
            view.setLayoutY(middleImage.getChangeY());
            MIDDLE_PANE.getChildren().add(view);
        }
        for (TreeGameMap.BackgroundImageData frontImage : SHOW_FRONT_IMAGE) {
            if (frontImage instanceof ImageObject imageObject && (imageObject.getType() == ImageObjectType.MONSTER || imageObject.getType() == ImageObjectType.NPC)) {
                Label monsterLabel = imageObject.getMonsterAnimationLabel();
                monsterLabel.setLayoutX(frontImage.getChangeX());
                monsterLabel.setLayoutY(frontImage.getChangeY());
                if (imageObject.getType() == ImageObjectType.MONSTER) {
                    monsterLabel.setTextFill(Color.RED);
                } else {
                    monsterLabel.setTextFill(Color.GREEN);
                }
                FRONT_PANE.getChildren().add(monsterLabel);
            } else {
                ImageView view = frontImage.getImageView();
                view.setLayoutX(frontImage.getChangeX());
                view.setLayoutY(frontImage.getChangeY());
                FRONT_PANE.getChildren().add(view);
            }
        }
    }

    private void startDrawPointLock() {
        if (RootController.showPointLocks) {
            Path path = new Path();
            int blockWidthX = treeAreaShow.getWidth() / 48;
            int blockHeightY = treeAreaShow.getHeight() / 32;
            int remainderX = currentX % 48;
            int remainderY = currentY % 32;
            for (int i = 0; i < blockHeightY; i++) {
                int tempStartY = i * 32 - remainderY;
                MoveTo moveTo = new MoveTo(0, tempStartY);
                HLineTo hLineTo = new HLineTo(treeAreaShow.getWidth());
                path.getElements().addAll(moveTo, hLineTo);
            }
            for (int i = 0; i < blockWidthX; i++) {
                int tempStartX = i * 48 - remainderX;
                MoveTo moveTo = new MoveTo(tempStartX, 0);
                VLineTo hLineTo = new VLineTo(treeAreaShow.getHeight());
                path.getElements().addAll(moveTo, hLineTo);
            }
            path.getElements().add(new ClosePath());
            path.setFillRule(FillRule.NON_ZERO);
            path.setStroke(Color.RED);
            path.setStrokeWidth(1);
            FRONT_PANE.getChildren().add(path);
            addPointLockImage();
        }
    }

    //占位图显示
    private void addPointLockImage() {
        int singleLocatorXNumber = treeAreaShow.getWidth() / 48;
        int singleLocatorYNumber = treeAreaShow.getHeight() / 32;
        int currentLocatorXNumber = currentX / 48;
        int currentLocatorYNumber = currentY / 32;
        boolean[][] basePointLockList = treeAreaShow.getBasePointLockList();
        for (int y = 0; y < singleLocatorYNumber; y++) {
            for (int x = 0; x < singleLocatorXNumber; x++) {
                if (basePointLockList[currentLocatorXNumber + x][currentLocatorYNumber + y]) {
                    ImageView tempView = new ImageView(POINT_SHOW_IMAGE);
                    tempView.setLayoutX((currentLocatorXNumber + x) * 48 - currentX);
                    tempView.setLayoutY((currentLocatorYNumber + y) * 32 - currentY);
                    FRONT_PANE.getChildren().add(tempView);
                }
            }
        }
    }

    private boolean addShowImages(TreeGameMap.BackgroundImageData backgroundImage) {
        double imageWidth;
        double imageHeight;
        if (backgroundImage.getImageView() == null) {
            imageWidth = 48;
            imageHeight = 32;
        } else {
            imageWidth = backgroundImage.getImageView().getImage().getWidth();
            imageHeight = backgroundImage.getImageView().getImage().getHeight();
        }
        double startX = backgroundImage.getTempStartX();
        double startY = backgroundImage.getTempStartY();
        if (startY + imageHeight > this.currentY && startX + imageWidth > this.currentX &&
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
                    } else if (backgroundImage.getMirImageMark() == 9 || backgroundImage.getMirImageMark() == 3 || backgroundImage.getMirImageMark() == 2) {
                        if (backgroundImage.getMirImageMark() == 9) {
                            backgroundImage.getImageView().setViewOrder(9);
                        } else if (backgroundImage.getMirImageMark() == 3) {
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
        List<ImageObject> imageObjectList = gameMap.getImageObjectList();
        if (imageObjectList != null && !imageObjectList.isEmpty()) {
            for (ImageObject imageObject : imageObjectList) {
                imageObject.initImage(gameMap.getBackgroundImagePath());
                imageObject.setTempStartX(globalX + imageObject.getStartX());
                imageObject.setTempStartY(globalY + imageObject.getStartY());
                if (addShowImages(imageObject)) {
                    if (imageObject.getType() != ImageObjectType.MONSTER && imageObject.getType() != ImageObjectType.NPC) {
                        imageObject.getImageView().setViewOrder(2);
                        imageObject.getImageView().setBlendMode(BlendMode.ADD);
                    }
                    SHOW_FRONT_IMAGE.add(imageObject);
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

    public static void handle() {
        for (TreeGameMap.BackgroundImageData backgroundImageData : SHOW_FRONT_IMAGE) {
            if (backgroundImageData instanceof ImageObject imageObject) {
                if (imageObject.getType() == ImageObjectType.FIXED_ANIMATION && imageObject.getBaseAnimationName() != null && !imageObject.getBaseAnimationName().isEmpty()) {
                    int amIndex = imageObject.getAmIndex();
                    //利用mir导出的图片缓存
                    Image mirCacheImage = Configuration.useMirImageCache(imageObject.getBaseAnimationName().get(amIndex));
                    imageObject.getImageView().setImage(mirCacheImage);
                    ++amIndex;
                    if(amIndex == imageObject.getBaseAnimationName().size()) {
                        amIndex = 0;
                    }
                    imageObject.setAmIndex(amIndex);
                }
            }
        }
    }
}
