package cn.kungreat.fxgamemap.custom;

import cn.kungreat.fxgamemap.ImageObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class AreaMapShow {

    private List<TreeGameMap.BackgroundImageData> showBackgroundImages = new ArrayList<>();
    private List<ImageObject> showImageObject = new ArrayList<>();

    private Integer width;
    private Integer height;

    private Integer canvasWidth;
    private Integer canvasHeight;
    private Integer centerX;
    private Integer centerY;

    private Color[][] backgroundColors;
    private Color defaultBackgroundColor = Color.BLACK;
    @JsonIgnore
    private Canvas canvas;
    @JsonIgnore
    private GraphicsContext graphicsContext;

    public void initAreaMapShow(TreeArea treeArea) {
        if (canvas == null) {
            canvasWidth = 3840;
            canvasHeight = 2160;
            canvas = new Canvas(canvasWidth, canvasHeight);
            graphicsContext = canvas.getGraphicsContext2D();
            width = treeArea.getWidth();
            height = treeArea.getHeight();
            backgroundColors = new Color[treeArea.getXNumber()][treeArea.getYNumber()];
            centerX = 0;
            centerY = 0;
        }
        String[][] childrenPointName = treeArea.getChildrenPointName();
        for (int y = 0; y < treeArea.getYNumber(); y++) {
            for (int x = 0; x < treeArea.getXNumber(); x++) {
                String pointName = childrenPointName[x][y];
                if (pointName != null && !pointName.isBlank()) {
                    TreeGameMap treeGameMap = findTreeGameMap(pointName, treeArea);
                    if (treeGameMap != null) {
                        backgroundColors[x][y] = Color.web(treeGameMap.getCanvasFillColor());
                        List<TreeGameMap.BackgroundImageData> backgroundImages = treeGameMap.getBackgroundImages();
                        if (backgroundImages != null && !backgroundImages.isEmpty()) {
                            for (TreeGameMap.BackgroundImageData backgroundImage : backgroundImages) {
                                TreeGameMap.BackgroundImageData areaShowBackgroundImage = new TreeGameMap.BackgroundImageData
                                        (backgroundImage.getImage(), x * width + backgroundImage.getStartX()
                                                , y * height + backgroundImage.getStartY(), backgroundImage.getImagePath());
                                showBackgroundImages.add(areaShowBackgroundImage);
                            }
                        }
                        List<ImageObject> imageObjectList = treeGameMap.getImageObjectList();
                        if (imageObjectList != null && !imageObjectList.isEmpty()) {
                            for (ImageObject imageObject : imageObjectList) {
                                ImageObject areaShowImageObject = new ImageObject(imageObject.getId(), imageObject.getImage(),
                                        x * width + imageObject.getStartX(),
                                        y * height + imageObject.getStartY(), imageObject.getImagePath());
                                showImageObject.add(areaShowImageObject);
                            }
                        }
                    }
                }
            }
        }
        treeArea.setSwitchTypeName("areaMapShow");
    }

    public void clearAndDraw(TreeArea treeArea) {
        graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
        drawAllImage(treeArea);
    }

    private void drawAllImage(TreeArea treeArea) {
        int startX = 0, startY = 0;
        int centerPointX = 0, centerPointY = 0;
        for (int x = 0; x < treeArea.getXNumber(); x++) {
            if (x * width + width > centerX) {
                centerPointX = x;
                startX = width - ((x * width) + width - centerX);
                break;
            }
        }
        for (int y = 0; y < treeArea.getYNumber(); y++) {
            if (y * height + height > centerY) {
                centerPointY = y;
                startY = height - ((y * height) + height - centerY);
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
        WritableImage writableImage = new WritableImage(((endWidthIndex - startWidthIndex) + 1) * width, ((endHeightIndex - startHeightIndex) + 1) * height);
        int tempX = 0, tempY = 0;
        for (int y = startHeightIndex; y <= endHeightIndex; y++, tempY++) {
            for (int x = startWidthIndex; x <= endWidthIndex; x++, tempX++) {
                Color chooseColor = backgroundColors[x][y];
                Color color = chooseColor == null ? defaultBackgroundColor : chooseColor;
                writerColor(tempX, tempY, color, writableImage.getPixelWriter());
            }
            tempX = 0;
        }
        graphicsContext.drawImage(writableImage, -startX, -startY);
    }

    private void canvasMoveListener(TreeArea treeArea) {

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
