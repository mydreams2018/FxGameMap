package cn.kungreat.fxgamemap.custom;

import cn.kungreat.fxgamemap.Configuration;
import cn.kungreat.fxgamemap.ImageObject;
import cn.kungreat.fxgamemap.RootApplication;
import cn.kungreat.fxgamemap.RootController;
import cn.kungreat.fxgamemap.util.LogService;
import cn.kungreat.fxgamemap.util.PropertyListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.RadioButton;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
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
            if (!backgroundImages.isEmpty()) {
                backgroundImages.forEach(backgroundImageData -> backgroundImageData.initImage(backgroundImagePath));
            }
            clearAndDraw();
            canvasEvent();
        }
    }

    public void canvasEvent() {
        canvas.setOnMouseMoved(event -> {
            ImageView chooseResourceImage = PropertyListener.getChooseResourceImage();
            if (chooseResourceImage != null) {
                RootController controller = RootApplication.mainFXMLLoader.getController();
                RadioButton radioButtonIsObject = controller.getRadioButtonIsObject();
                if (!radioButtonIsObject.isSelected()) {
                    clearAndDraw();
                    Image image = chooseResourceImage.getImage();
                    graphicsContext.drawImage(image, event.getX() - (image.getWidth() / 2), event.getY() - (image.getHeight() / 2));
                }
            }
        });
        canvas.setOnMouseExited(event -> clearAndDraw());
        canvas.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                ImageView chooseResourceImage = PropertyListener.getChooseResourceImage();
                if (chooseResourceImage != null) {
                    RootController controller = RootApplication.mainFXMLLoader.getController();
                    RadioButton radioButtonIsObject = controller.getRadioButtonIsObject();
                    if (!radioButtonIsObject.isSelected()) {
                        Image image = chooseResourceImage.getImage();
                        double startX = event.getX() - (image.getWidth() / 2);
                        double startY = event.getY() - (image.getHeight() / 2);
                        backgroundImages.add(new BackgroundImageData(image, startX, startY, image.getUrl()));
                        saveImgPaths.add(image.getUrl());
                        PropertyListener.changeIsSaved(false);
                    }
                }
            }
        });
    }

    //全部内容刷新
    public void clearAndDraw() {
        graphicsContext.clearRect(0, 0, width, height);
        graphicsContext.fillRect(0, 0, width, height);
        backgroundImages.forEach(image -> graphicsContext.drawImage(image.getImage(), image.getStartX(), image.getStartY()));
    }

    //序列化保存时回调 - > 线程池调用 - > 保存图片资源文件
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

    @Setter
    @Getter
    @NoArgsConstructor
    private static final class BackgroundImageData {
        @JsonIgnore
        private Image image;
        private String imagePath;
        private double startX;
        private double startY;

        public BackgroundImageData(Image image, double startX, double startY, String imagePath) {
            this.image = image;
            this.startX = startX;
            this.startY = startY;
            this.imagePath = imagePath;
        }

        public void initImage(String backgroundImagePath) {
            try {
                String[] split = imagePath.split("/");
                File outFile = new File(new File(new URI(Configuration.currentProject).getPath()).getParentFile(), backgroundImagePath);
                if (outFile.exists() && outFile.isDirectory()) {
                    for (File file : outFile.listFiles()) {
                        if (file.getName().equals(split[split.length - 1])) {
                            this.image = new Image(file.toURI().toString());
                        }
                    }
                }
            } catch (Exception e) {
                LogService.printLog(LogService.LogLevel.ERROR, TreeGameMap.class, "读取图片资源文件", e);
            }
        }
    }
}
