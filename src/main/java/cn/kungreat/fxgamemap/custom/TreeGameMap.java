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
import javafx.scene.input.MouseEvent;
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
                        Optional<String> optional = IMAGE_OBJECT_DIALOG.showAndWait();
                        if (optional.isPresent() && optional.get().equals("OK")) {
                            String objectNameText = BaseDialog.IMAGE_OBJECT_NAME.getText();
                            if (objectNameText != null && !objectNameText.isBlank()) {
                                ImageObject changeImageObject = new ImageObject(UUID.randomUUID().toString(), image, startX, startY, imagePath);
                                changeImageObject.setTitle(objectNameText);
                                changeImageObject.initTitledPane();
                                controller.getRightTopScrollPaneAccordion().getPanes().add(changeImageObject.getTitledPane());
                                imageObjectList.add(changeImageObject);
                                PropertyListener.changeIsSaved(false);
                            }
                        }
                    } else {
                        backgroundImages.add(new BackgroundImageData(image, startX, startY, imagePath));
                        PropertyListener.changeIsSaved(false);
                    }
                } else if (controller.getTopMovingMode().isSelected()) {
                    if (controller.getRadioButtonIsObject().isSelected()) {
                        ImageObject imageObject = getImageObjectData(event);
                        PropertyListener.setChooseCanvasImage(imageObject);
                        if (imageObject != null) {
                            controller.getRightTopScrollPaneAccordion().setExpandedPane(imageObject.getTitledPane());
                        }
                    } else {
                        PropertyListener.setChooseCanvasImage(getBackgroundImageData(event));
                    }
                } else if (controller.getTopDeletingMode().isSelected()) {
                    if (controller.getRadioButtonIsObject().isSelected()) {
                        ImageObject removeImageObject = getImageObjectData(event);
                        if (removeImageObject != null) {
                            imageObjectList.remove(removeImageObject);
                            controller.getRightTopScrollPaneAccordion().getPanes().remove(removeImageObject.getTitledPane());
                            clearAndDraw();
                            graphicsContext.drawImage(DELETE_IMAGE, event.getX() - (DELETE_IMAGE.getWidth() / 2),
                                    event.getY() - (DELETE_IMAGE.getHeight() / 2));
                        }
                    } else {
                        BackgroundImageData backgroundImageData = getBackgroundImageData(event);
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
        PropertyListener.initChooseCanvasImageListener();
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

    //拿到当前选中的对象
    private BackgroundImageData getBackgroundImageData(MouseEvent event) {
        double currentX = event.getX();
        double currentY = event.getY();
        BackgroundImageData chooseBackgroundImageData = null;
        for (BackgroundImageData backgroundImage : backgroundImages) {
            if (backgroundImage.getStartX() < currentX && backgroundImage.getStartY() < currentY &&
                    backgroundImage.getStartX() + backgroundImage.getImage().getWidth() > currentX &&
                    backgroundImage.getStartY() + backgroundImage.getImage().getHeight() > currentY) {
                chooseBackgroundImageData = backgroundImage;
            }
        }
        return chooseBackgroundImageData;
    }

    private ImageObject getImageObjectData(MouseEvent event) {
        double currentX = event.getX();
        double currentY = event.getY();
        ImageObject chooseImageObject = null;
        for (ImageObject imageObject : imageObjectList) {
            if (imageObject.getStartX() < currentX && imageObject.getStartY() < currentY &&
                    imageObject.getStartX() + imageObject.getImage().getWidth() > currentX &&
                    imageObject.getStartY() + imageObject.getImage().getHeight() > currentY) {
                chooseImageObject = imageObject;
            }
        }
        return chooseImageObject;
    }

    //全部内容刷新
    public void clearAndDraw() {
        graphicsContext.fillRect(0, 0, width, height);
        drawMarkLine();
        backgroundImages.forEach(image -> graphicsContext.drawImage(image.getImage(), image.getStartX(), image.getStartY()));
        imageObjectList.forEach(image -> graphicsContext.drawImage(image.getImage(), image.getStartX(), image.getStartY()));
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
        private double startX;
        private double startY;

        public BackgroundImageData(Image image, double startX, double startY, String imagePath) {
            this.image = image;
            this.startX = startX;
            this.startY = startY;
            String[] split = imagePath.split("/");
            this.imagePath = split[split.length - 1];
            this.imageView = new ImageView(image);
        }

        public void initImage(String backgroundImagePath) {
            if (this.imageView == null) {
                try {
                    File outFile = new File(new File(new URI(Configuration.currentProject).getPath()).getParentFile(), backgroundImagePath);
                    if (outFile.exists() && outFile.isDirectory()) {
                        for (File file : outFile.listFiles()) {
                            if (file.getName().equals(this.imagePath)) {
                                this.image = new Image(file.toURI().toString());
                                this.imageView = new ImageView(image);
                            }
                        }
                    }
                } catch (Exception e) {
                    LogService.printLog(LogService.LogLevel.ERROR, TreeGameMap.class, "读取图片资源文件", e);
                }
            }
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
