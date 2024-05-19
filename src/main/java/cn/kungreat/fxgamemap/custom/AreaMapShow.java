package cn.kungreat.fxgamemap.custom;

import cn.kungreat.fxgamemap.ImageObject;
import cn.kungreat.fxgamemap.RootController;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.geometry.Orientation;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Setter
@Getter
public class AreaMapShow {

    /*
     * 存放每次需要处理的图片
     * */
    private List<TreeGameMap.BackgroundImageData> showBackgroundImages = new LinkedList<>();
    private List<ImageObject> showImageObject = new LinkedList<>();

    private Integer width;
    private Integer height;

    private Integer canvasWidth;
    private Integer canvasHeight;
    private Integer currentX;
    private Integer currentY;

    private Color[][] backgroundColors;
    @JsonIgnore
    private Canvas canvas;
    @JsonIgnore
    private GraphicsContext graphicsContext;
    @JsonIgnore
    private VBox outVBox;
    @JsonIgnore
    private HBox innerHBox;
    @JsonIgnore
    private ScrollBar scrollBarY;
    @JsonIgnore
    private ScrollBar scrollBarX;
    @JsonIgnore
    private WritableImage writableImage;

    public void initAreaMapShow(TreeArea treeArea) {
        if (canvas == null) {
            canvasWidth = treeArea.getWidth();
            canvasHeight = treeArea.getHeight();
            canvas = new Canvas(canvasWidth, canvasHeight);
            graphicsContext = canvas.getGraphicsContext2D();
            width = treeArea.getWidth();
            height = treeArea.getHeight();
            backgroundColors = new Color[treeArea.getXNumber()][treeArea.getYNumber()];
            currentX = 0;
            currentY = 0;
            initView(treeArea);
        }
        String[][] childrenPointName = treeArea.getChildrenPointName();
        for (int y = 0; y < treeArea.getYNumber(); y++) {
            for (int x = 0; x < treeArea.getXNumber(); x++) {
                String pointName = childrenPointName[x][y];
                if (pointName != null && !pointName.isBlank()) {
                    TreeGameMap treeGameMap = findTreeGameMap(pointName, treeArea);
                    if (treeGameMap != null && treeGameMap.getCanvasFillColor() != null) {
                        backgroundColors[x][y] = Color.web(treeGameMap.getCanvasFillColor());
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
        scrollBarY.setMax(treeArea.getYNumber() * height - canvasHeight);
        scrollBarY.setBlockIncrement(height);
        innerHBox.getChildren().addAll(canvas, scrollBarY);
        scrollBarX = new ScrollBar();
        scrollBarX.setOrientation(Orientation.HORIZONTAL);
        scrollBarX.setMin(0);
        scrollBarX.setMax(treeArea.getXNumber() * width - canvasWidth);
        scrollBarX.setBlockIncrement(width);
        outVBox.getChildren().addAll(innerHBox, scrollBarX);
        outVBox.setMaxWidth(Region.USE_PREF_SIZE);
        outVBox.setMaxHeight(Region.USE_PREF_SIZE);
        scrollBarY.valueProperty().addListener((observable, oldValue, newValue) -> {
            currentY = newValue.intValue();
            clearAndDraw(treeArea);
        });
        scrollBarX.valueProperty().addListener((observable, oldValue, newValue) -> {
            currentX = newValue.intValue();
            clearAndDraw(treeArea);
        });
    }

    public void clearAndDraw(TreeArea treeArea) {
        graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
        showBackgroundImages.clear();
        showImageObject.clear();
        drawAllImage(treeArea);
    }

    private void drawAllImage(TreeArea treeArea) {
        int startX = 0, startY = 0;
        int centerPointX = 0, centerPointY = 0;
        for (int x = 0; x < treeArea.getXNumber(); x++) {
            if (x * width + width > currentX) {
                centerPointX = x;
                startX = width - ((x * width) + width - currentX);
                break;
            }
        }
        for (int y = 0; y < treeArea.getYNumber(); y++) {
            if (y * height + height > currentY) {
                centerPointY = y;
                startY = height - ((y * height) + height - currentY);
                break;
            }
        }
        int startWidthIndex = centerPointX, endWidthIndex = centerPointX;
        if (canvasWidth - (width - startX) > 0) {
            int needRightWidth = canvasWidth - (width - startX);
            for (; needRightWidth > 0; endWidthIndex++) {
                needRightWidth = needRightWidth - width;
            }
        }
        int startHeightIndex = centerPointY;
        int endHeightIndex = centerPointY;
        if (canvasHeight - (height - startY) > 0) {
            int needBottomHeight = canvasHeight - (height - startY);
            for (; needBottomHeight > 0; endHeightIndex++) {
                needBottomHeight = needBottomHeight - height;
            }
        }
        if (writableImage == null || writableImage.getWidth() < ((endWidthIndex - startWidthIndex) + 1) * width || writableImage.getHeight() < ((endHeightIndex - startHeightIndex) + 1) * height) {
            writableImage = new WritableImage(((endWidthIndex - startWidthIndex) + 1) * width, ((endHeightIndex - startHeightIndex) + 1) * height);
        }
        int tempX = 0, tempY = 0;
        for (int y = startHeightIndex; y <= endHeightIndex; y++, tempY++) {
            for (int x = startWidthIndex; x <= endWidthIndex; x++, tempX++) {
                Color chooseColor = backgroundColors[x][y];
                Color color = chooseColor == null ? RootController.CANVAS_DEFAULT_COLOR : chooseColor;
                writerColor(tempX, tempY, color, writableImage.getPixelWriter());
                filterShowImages(treeArea, x, y, startX, startY, tempX, tempY);
            }
            tempX = 0;
        }
        graphicsContext.drawImage(writableImage, -startX, -startY);
        for (TreeGameMap.BackgroundImageData showBackgroundImage : showBackgroundImages) {
            graphicsContext.drawImage(showBackgroundImage.getImage(), showBackgroundImage.getStartX() - startX, showBackgroundImage.getStartY() - startY);
        }
        for (ImageObject imageObject : showImageObject) {
            graphicsContext.drawImage(imageObject.getImage(), imageObject.getStartX() - startX, imageObject.getStartY() - startY);
        }
    }

    /*
     * 过滤不必要的图片数据
     * */
    private void filterShowImages(TreeArea treeArea, int indexX, int indexY, int startX, int startY, int tempX, int tempY) {
        String[][] childrenPointName = treeArea.getChildrenPointName();
        String pointName = childrenPointName[indexX][indexY];
        if (pointName != null && !pointName.isBlank()) {
            TreeGameMap treeGameMap = findTreeGameMap(pointName, treeArea);
            if (treeGameMap != null) {
                List<TreeGameMap.BackgroundImageData> backgroundImages = treeGameMap.getBackgroundImages();
                if (backgroundImages != null && !backgroundImages.isEmpty()) {
                    for (TreeGameMap.BackgroundImageData backgroundImage : backgroundImages) {
                        backgroundImage.initImage(treeGameMap.getBackgroundImagePath());
                        Image image = backgroundImage.getImage();
                        if (tempY * height + backgroundImage.getStartY() + image.getHeight() >= startY && tempX * width + backgroundImage.getStartX() + image.getWidth() >= startX
                                && tempY * height + backgroundImage.getStartY() <= startY + canvasHeight && tempX * width + backgroundImage.getStartX() <= startX + canvasWidth) {
                            TreeGameMap.BackgroundImageData areaShowBackgroundImage = new TreeGameMap.BackgroundImageData
                                    (image, tempX * width + backgroundImage.getStartX()
                                            , tempY * height + backgroundImage.getStartY(), backgroundImage.getImagePath());
                            showBackgroundImages.add(areaShowBackgroundImage);
                        }
                    }
                }
                List<ImageObject> imageObjectList = treeGameMap.getImageObjectList();
                if (imageObjectList != null && !imageObjectList.isEmpty()) {
                    for (ImageObject imageObject : imageObjectList) {
                        imageObject.initImage(treeGameMap.getBackgroundImagePath());
                        Image image = imageObject.getImage();
                        if (tempY * height + imageObject.getStartY() + image.getHeight() >= startY && tempX * width + imageObject.getStartX() + image.getWidth() >= startX
                                && tempY * height + imageObject.getStartY() <= startY + canvasHeight && tempX * width + imageObject.getStartX() <= startX + canvasWidth) {
                            ImageObject areaShowImageObject = new ImageObject(imageObject.getId(), imageObject.getImage(),
                                    tempX * width + imageObject.getStartX(),
                                    tempY * height + imageObject.getStartY(), imageObject.getImagePath());
                            showImageObject.add(areaShowImageObject);
                        }
                    }
                }
            }
        }
    }

    private void writerColor(int tempX, int tempY, Color color, PixelWriter pixelWriter) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixelWriter.setColor(tempX * width + x, tempY * height + y, color);
            }
        }
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
