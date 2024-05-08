package cn.kungreat.fxgamemap.custom;

import cn.kungreat.fxgamemap.Configuration;
import cn.kungreat.fxgamemap.ImageObject;
import cn.kungreat.fxgamemap.RootApplication;
import cn.kungreat.fxgamemap.RootController;
import cn.kungreat.fxgamemap.util.LogService;
import cn.kungreat.fxgamemap.util.PropertyListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.net.URI;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class TreeGameMap {
    private String title;
    private String id;
    private Integer width;
    private Integer height;
    private TreeGameMap left;
    private TreeGameMap top;
    private TreeGameMap right;
    private TreeGameMap bottom;
    private TreeGameMap leftTop;
    private TreeGameMap leftBottom;
    private TreeGameMap rightTop;
    private TreeGameMap rightBottom;

    public static final WritablePixelFormat<IntBuffer> PIXEL_INT_FORMAT = PixelFormat.getIntArgbPreInstance();
    public static final Color CANVAS_DEFAULT_COLOR = Color.LIGHTBLUE;
    public static final SnapshotParameters CANVAS_SNAPSHOT_PARAMETERS = new SnapshotParameters();

    @JsonIgnore
    private Canvas canvas;
    @JsonIgnore
    private GraphicsContext graphicsContext;

    private List<BackgroundImageData> backgroundImages = new ArrayList<>();
    private Set<String> saveImgPaths = new HashSet<>();

    private String backgroundImagePath;
    private List<ImageObject> imageObjectList;

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
            graphicsContext.setFill(CANVAS_DEFAULT_COLOR);
            CANVAS_SNAPSHOT_PARAMETERS.setFill(Color.color(0, 0, 0, 0));
            if (!backgroundImages.isEmpty()) {
                backgroundImages.forEach(backgroundImageData -> backgroundImageData.initImage(backgroundImagePath));
            }
            clearAndDraw();
            canvasEvent();
        }
    }

    public void canvasEvent() {
        canvas.setOnMouseMoved(event -> {
            RootController controller = RootApplication.mainFXMLLoader.getController();
            ImageView chooseResourceImage = PropertyListener.getChooseResourceImage();
            if (controller.getTopPaintingMode().isSelected() && chooseResourceImage != null) {
                if (!controller.getRadioButtonIsObject().isSelected()) {
                    clearAndDraw();
                    Image image = chooseResourceImage.getImage();
                    graphicsContext.drawImage(image, event.getX() - (image.getWidth() / 2), event.getY() - (image.getHeight() / 2));
                }
            }
        });
        canvas.setOnMouseExited(event -> clearAndDraw());
        canvas.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                RootController controller = RootApplication.mainFXMLLoader.getController();
                ImageView chooseResourceImage = PropertyListener.getChooseResourceImage();
                if (controller.getTopPaintingMode().isSelected() && chooseResourceImage != null) {
                    if (!controller.getRadioButtonIsObject().isSelected()) {
                        Image image = chooseResourceImage.getImage();
                        double startX = event.getX() - (image.getWidth() / 2);
                        double startY = event.getY() - (image.getHeight() / 2);
                        String imagePath;
                        if (image.getUrl() != null) {
                            saveImgPaths.add(image.getUrl());
                            imagePath = image.getUrl();
                        } else {
                            saveImgPaths.add(chooseResourceImage.getId());
                            imagePath = chooseResourceImage.getId();
                        }
                        backgroundImages.add(new BackgroundImageData(image, startX, startY, imagePath, chooseResourceImage));
                        PropertyListener.changeIsSaved(false);
                    }
                } else if (!controller.getTopPaintingMode().isSelected()) {
                    if (!controller.getRadioButtonIsObject().isSelected()) {
                        getBackgroundImageData(event);
                    }
                }
            }
        });
        PropertyListener.initChooseCanvasImageListener();
    }

    //拿到当前选中的对象
    private void getBackgroundImageData(MouseEvent event) {
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
        PropertyListener.setChooseCanvasImage(chooseBackgroundImageData);
    }

    //全部内容刷新
    public void clearAndDraw() {
        graphicsContext.fillRect(0, 0, width, height);
        backgroundImages.forEach(image -> graphicsContext.drawImage(image.getImage(), image.getStartX(), image.getStartY()));
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
    public static final class BackgroundImageData {
        @JsonIgnore
        private Image image;
        @JsonIgnore
        private ImageView imageView;
        private String imagePath;
        private double startX;
        private double startY;

        public BackgroundImageData(Image image, double startX, double startY, String imagePath, ImageView imageView) {
            this.image = image;
            this.startX = startX;
            this.startY = startY;
            this.imagePath = imagePath;
            this.imageView = imageView;
        }

        public void initImage(String backgroundImagePath) {
            try {
                String[] split = imagePath.split("/");
                File outFile = new File(new File(new URI(Configuration.currentProject).getPath()).getParentFile(), backgroundImagePath);
                if (outFile.exists() && outFile.isDirectory()) {
                    for (File file : outFile.listFiles()) {
                        if (file.getName().equals(split[split.length - 1])) {
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
