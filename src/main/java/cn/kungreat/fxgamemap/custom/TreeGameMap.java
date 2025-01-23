package cn.kungreat.fxgamemap.custom;

import cn.kungreat.fxgamemap.*;
import cn.kungreat.fxgamemap.util.LogService;
import cn.kungreat.fxgamemap.util.PropertyListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Setter
@Getter
@NoArgsConstructor
public class TreeGameMap {
    private String title;
    private String id;
    private Integer width;
    private Integer height;

    //从mir地图导出的所有图片资源目录
    public static final String MIR_SHARE_IMAGE_DIRECTORY = "mir2AllIMages\\";
    public static final SnapshotParameters CANVAS_SNAPSHOT_PARAMETERS = new SnapshotParameters();
    public static final Image DELETE_IMAGE = new Image(TreeGameMap.class.getResourceAsStream("hud_x.png"));
    public static final Dialog<String> IMAGE_OBJECT_DIALOG = BaseDialog.getDialog("图片对象", "请输入图片对象信息", "确定添加此图片对象信息"
            , BaseDialog.IMAGE_OBJECT_NAME, BaseDialog.APPLY_IMAGE_OBJECT, BaseDialog.CANCEL_IMAGE_OBJECT);

    @JsonIgnore
    private Canvas canvas;
    @JsonIgnore
    private GraphicsContext graphicsContext;
    @JsonIgnore
    private MarkLine markLine;

    private List<BackgroundImageData> backgroundImages = new ArrayList<>();
    private Set<String> saveImgPaths = new HashSet<>();

    private String backgroundImagePath;
    private List<ImageObject> imageObjectList = new ArrayList<>();
    private String canvasFillColor;

    public TreeGameMap(String id, String title, Integer width, Integer height, String backgroundImagePath) {
        this.id = id;
        this.title = title;
        this.width = width;
        this.height = height;
        this.backgroundImagePath = backgroundImagePath;
    }

    public void initCanvas() {
        if (canvas == null) {
            canvas = new Canvas(width, height);
            graphicsContext = canvas.getGraphicsContext2D();
            graphicsContext.setImageSmoothing(true);
            if (canvasFillColor != null) {
                graphicsContext.setFill(Color.web(canvasFillColor));
            } else {
                graphicsContext.setFill(RootController.CANVAS_DEFAULT_COLOR);
            }
            graphicsContext.setLineWidth(1);
            CANVAS_SNAPSHOT_PARAMETERS.setFill(Color.color(0, 0, 0, 0));
            backgroundImages.forEach(backgroundImageData -> backgroundImageData.initImage(backgroundImagePath));
            imageObjectList.forEach(imageObject -> {
                imageObject.initImage(backgroundImagePath);
                imageObject.initTitledPane();
            });
            clearAndDraw();
            canvasEvent();
        }
        imageObjectList.forEach(imageObject -> {
            RootController controller = RootApplication.mainFXMLLoader.getController();
            controller.getRightTopScrollPaneAccordion().getPanes().add(imageObject.getTitledPane());
        });
    }

    public void canvasEvent() {
        canvas.setOnMouseMoved(event -> {
            RootController controller = RootApplication.mainFXMLLoader.getController();
            ImageView chooseResourceImage = PropertyListener.getChooseResourceImage();
            clearAndDraw();
            if (controller.getTopPaintingMode().isSelected() && chooseResourceImage != null) {
                Image image = chooseResourceImage.getImage();
                if (markLine != null) {
                    graphicsContext.drawImage(image, event.getX() - (event.getX() % markLine.getMarkX()), event.getY() - (event.getY() % markLine.getMarkY()));
                } else {
                    graphicsContext.drawImage(image, event.getX() - (image.getWidth() / 2), event.getY() - (image.getHeight() / 2));
                }
            } else if (controller.getTopDeletingMode().isSelected()) {
                graphicsContext.drawImage(DELETE_IMAGE, event.getX() - (DELETE_IMAGE.getWidth() / 2), event.getY() - (DELETE_IMAGE.getHeight() / 2));
            }
        });
        canvas.setOnMouseExited(event -> clearAndDraw());
        canvas.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                RootController controller = RootApplication.mainFXMLLoader.getController();
                ImageView chooseResourceImage = PropertyListener.getChooseResourceImage();
                if (controller.getTopPaintingMode().isSelected() && chooseResourceImage != null) {
                    Image image = chooseResourceImage.getImage();
                    double startX;
                    double startY;
                    if (markLine != null) {
                        startX = event.getX() - (event.getX() % markLine.getMarkX());
                        startY = event.getY() - (event.getY() % markLine.getMarkY());
                    } else {
                        startX = event.getX() - (image.getWidth() / 2);
                        startY = event.getY() - (image.getHeight() / 2);
                    }
                    int locatorX = (int) ((startX + image.getWidth()) / 48);
                    int locatorY = (int) ((startY + image.getHeight()) / 32);
                    String imagePath;
                    if (image.getUrl() != null) {
                        saveImgPaths.add(image.getUrl());
                        imagePath = image.getUrl();
                    } else {
                        //分割图片时用Id暂存的图片路径
                        saveImgPaths.add(chooseResourceImage.getId());
                        imagePath = chooseResourceImage.getId();
                    }
                    if (controller.getRadioButtonIsObject().isSelected()) {
                        ImageObject changeImageObject = new ImageObject(UUID.randomUUID().toString(), image, startX, startY, imagePath, locatorX, locatorY);
                        changeImageObject.setTitle(changeImageObject.getImagePath());
                        changeImageObject.initTitledPane();
                        controller.getRightTopScrollPaneAccordion().getPanes().add(changeImageObject.getTitledPane());
                        imageObjectList.add(changeImageObject);
                        PropertyListener.changeIsSaved(false);
                    } else {
                        backgroundImages.add(new BackgroundImageData(image, startX, startY, imagePath, locatorX, locatorY));
                        PropertyListener.changeIsSaved(false);
                    }
                } else if (controller.getTopMovingMode().isSelected()) {
                    if (controller.getRadioButtonIsObject().isSelected()) {
                        ImageObject imageObject = getImageObjectData(event.getX(), event.getY());
                        PropertyListener.setChooseCanvasImage(imageObject);
                        if (imageObject != null) {
                            controller.getRightTopScrollPaneAccordion().setExpandedPane(imageObject.getTitledPane());
                        }
                    } else {
                        PropertyListener.setChooseCanvasImage(getBackgroundImageData(event.getX(), event.getY()));
                    }
                } else if (controller.getTopDeletingMode().isSelected()) {
                    if (controller.getRadioButtonIsObject().isSelected()) {
                        ImageObject removeImageObject = getImageObjectData(event.getX(), event.getY());
                        if (removeImageObject != null) {
                            imageObjectList.remove(removeImageObject);
                            controller.getRightTopScrollPaneAccordion().getPanes().remove(removeImageObject.getTitledPane());
                            clearAndDraw();
                            graphicsContext.drawImage(DELETE_IMAGE, event.getX() - (DELETE_IMAGE.getWidth() / 2),
                                    event.getY() - (DELETE_IMAGE.getHeight() / 2));
                        }
                    } else {
                        BackgroundImageData backgroundImageData = getBackgroundImageData(event.getX(), event.getY());
                        if (backgroundImageData != null) {
                            backgroundImages.remove(backgroundImageData);
                            clearAndDraw();
                            graphicsContext.drawImage(DELETE_IMAGE, event.getX() - (DELETE_IMAGE.getWidth() / 2),
                                    event.getY() - (DELETE_IMAGE.getHeight() / 2));
                        }
                    }
                }
            }
        });
    }

    public static void addImageObjectEvent() {
        IMAGE_OBJECT_DIALOG.setOnShowing(event -> {
            BaseDialog.IMAGE_OBJECT_NAME.clear();
        });
        Button imageObjectOk = (Button) IMAGE_OBJECT_DIALOG.getDialogPane().lookupButton(BaseDialog.APPLY_IMAGE_OBJECT);
        Button imageObjectCancel = (Button) IMAGE_OBJECT_DIALOG.getDialogPane().lookupButton(BaseDialog.CANCEL_IMAGE_OBJECT);
        imageObjectOk.setOnAction(event -> IMAGE_OBJECT_DIALOG.setResult("OK"));
        imageObjectCancel.setOnAction(event -> IMAGE_OBJECT_DIALOG.setResult("CANCEL"));
    }

    //拿到当前选中的对象[优先返回上层对象]
    public BackgroundImageData getBackgroundImageData(double currentX, double currentY) {
        BackgroundImageData resultBack = null;
        for (BackgroundImageData backgroundImage : backgroundImages) {
            if (backgroundImage.getStartX() < currentX && backgroundImage.getStartY() < currentY &&
                    backgroundImage.getStartX() + backgroundImage.getImage().getWidth() > currentX &&
                    backgroundImage.getStartY() + backgroundImage.getImage().getHeight() > currentY) {
                if (backgroundImage.getMirImageMark() == 2 || backgroundImage.getMirImageMark() == 3) {
                    return backgroundImage;
                } else {
                    resultBack = backgroundImage;
                }
            }
        }
        return resultBack;
    }

    private ImageObject getImageObjectData(double currentX, double currentY) {
        for (ImageObject imageObject : imageObjectList) {
            if (imageObject.getStartX() < currentX && imageObject.getStartY() < currentY &&
                    imageObject.getStartX() + imageObject.getImage().getWidth() > currentX &&
                    imageObject.getStartY() + imageObject.getImage().getHeight() > currentY) {
                return imageObject;
            }
        }
        return null;
    }

    //全部内容刷新
    public void clearAndDraw() {
        graphicsContext.fillRect(0, 0, width, height);
        backgroundImages.forEach(image -> {
            if (image.getMirImageMark() == 5) {
                graphicsContext.drawImage(image.getImage(), image.getStartX(), image.getStartY());
            }
        });
        backgroundImages.forEach(image -> {
            if (image.getMirImageMark() == 4) {
                graphicsContext.drawImage(image.getImage(), image.getStartX(), image.getStartY());
            }
        });
        backgroundImages.forEach(image -> {
            if (image.getMirImageMark() == 3) {
                graphicsContext.drawImage(image.getImage(), image.getStartX(), image.getStartY());
            }
        });
        backgroundImages.forEach(image -> {
            if (image.getMirImageMark() == 2) {
                graphicsContext.drawImage(image.getImage(), image.getStartX(), image.getStartY());
            }
        });
        imageObjectList.forEach(image -> graphicsContext.drawImage(image.getImage(), image.getStartX(), image.getStartY()));
        drawMarkLine();
    }

    private void drawMarkLine() {
        if (markLine != null) {
            int forX = width / markLine.getMarkX();
            int forY = height / markLine.getMarkY();
            for (int x = 1; x <= forX; x++) {
                graphicsContext.strokeLine(x * markLine.getMarkX(), 0, x * markLine.getMarkX(), height);
            }
            for (int y = 1; y <= forY; y++) {
                graphicsContext.strokeLine(0, y * markLine.getMarkY(), width, y * markLine.getMarkY());
            }
        }
    }

    /*
    如果有分割图片 需要等待图片分割保存完成
    序列化保存时回调 - > 线程池调用 - > 保存图片资源文件
    */
    public Set<String> getSaveImgPaths() {
        saveImgPaths.forEach(imageSrcPath -> {
            try {
                File outFile = new File(new File(new URI(Configuration.currentProject).getPath()).getParentFile(), backgroundImagePath);
                outFile.mkdirs();
                String[] split = imageSrcPath.split("/");
                Files.copy(Path.of(new URI(imageSrcPath)), Path.of(outFile.toString(), split[split.length - 1]),
                        StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            } catch (Exception e) {
                LogService.printLog(LogService.LogLevel.ERROR, TreeGameMap.class, "保存图片资源文件", e);
            }
        });
        saveImgPaths.clear();
        return saveImgPaths;
    }

    public void addSaveImgPaths(String getUrl) {
        saveImgPaths.add(getUrl);
    }

    /*
     * 画板中的 背景图片对象描述
     * */
    @Setter
    @Getter
    @NoArgsConstructor
    public static class BackgroundImageData {
        /*
         * 图片数据的中转层
         * */
        @JsonIgnore
        private Image image;
        @JsonIgnore
        private ImageView imageView;
        private String imagePath;
        /*
         * 用于数据交互的每一次坐标转换
         * */
        @JsonIgnore
        private double changeX;
        @JsonIgnore
        private double changeY;
        /*
         * 用于全局坐标转换的临时坐标
         * */
        @JsonIgnore
        private double tempStartX;
        @JsonIgnore
        private double tempStartY;

        private double startX;
        private double startY;
        //定位XY的索引坐标系
        private int locatorX;
        private int locatorY;
        /*  back = 5 middle = 4 front = 3 frontBlend = 2
            用来表示图层 在这里先存在一起 在游戏引擎中可以分开存储
            */
        private int mirImageMark = 5;

        public BackgroundImageData(Image image, double startX, double startY, String imagePath, int locatorX, int locatorY) {
            this.image = image;
            this.startX = startX;
            this.startY = startY;
            String[] split = imagePath.split("/");
            this.imagePath = split[split.length - 1];
            this.imageView = new ImageView(image);
            this.locatorX = locatorX;
            this.locatorY = locatorY;
        }

        //导入mir地图用
        public BackgroundImageData(double startX, double startY, String imagePath, int locatorX, int locatorY, int mirImageMark) {
            this.startX = startX;
            this.startY = startY;
            this.imagePath = imagePath;
            this.locatorX = locatorX;
            this.locatorY = locatorY;
            this.mirImageMark = mirImageMark;
        }

        public void initImage(String backgroundImagePath) {
            if (this.imageView == null) {
                try {
                    File outFile = new File(new File(new URI(Configuration.currentProject).getPath()).getParentFile(), backgroundImagePath);
                    if (outFile.exists() && outFile.isDirectory()) {
                        File imageSrcFile = new File(outFile, this.imagePath);
                        if (imageSrcFile.exists()) {
                            this.image = new Image(imageSrcFile.toURI().toString());
                            this.imageView = new ImageView(image);
                        } else {
                            //利用mir导出的图片缓存
                            Image mirCacheImage = Configuration.MIR_IMAGE_CACHE.get(this.imagePath);
                            if (mirCacheImage != null) {
                                this.image = mirCacheImage;
                                this.imageView = new ImageView(image);
                            } else {
                                LogService.writerLog(LogService.LogLevel.ERROR, TreeGameMap.class, "image not found=" + this.imagePath);
                            }
                        }
                    }
                } catch (Exception e) {
                    LogService.printLog(LogService.LogLevel.ERROR, TreeGameMap.class, "读取图片资源文件", e);
                }
            }
        }

        public double getStartX() {
            return Math.floor(startX);
        }

        public double getStartY() {
            return Math.floor(startY);
        }
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static final class MarkLine {
        private int markX;
        private int markY;
    }
}
