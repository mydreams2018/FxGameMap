package cn.kungreat.fxgamemap.custom;

import cn.kungreat.fxgamemap.Configuration;
import cn.kungreat.fxgamemap.ImageObject;
import cn.kungreat.fxgamemap.RootApplication;
import cn.kungreat.fxgamemap.RootController;
import cn.kungreat.fxgamemap.util.CutoverBytes;
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
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;

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

    @JsonIgnore
    private Canvas canvas;
    @JsonIgnore
    private GraphicsContext graphicsContext;
    @JsonIgnore
    private WritableImage writableImage;
    @JsonIgnore
    private IntBuffer intBuffer;

    public static final WritablePixelFormat<IntBuffer> PIXEL_INT_FORMAT = PixelFormat.getIntArgbPreInstance();
    public static final String SUFFIX_SYMBOL = ".png";
    public static final Color CANVAS_DEFAULT_COLOR = Color.LIGHTBLUE;

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
            //TODO 根据相关信息恢复画板 先做保存信息
            File file = new File(new File(Configuration.currentProject).getParent(), backgroundImagePath + SUFFIX_SYMBOL);
            if (file.exists()) {

            } else {
                intBuffer = IntBuffer.allocate(width * height);
                //初始背影色
                Arrays.fill(intBuffer.array(), CutoverBytes.readInt((byte) (CANVAS_DEFAULT_COLOR.getOpacity() * 255), (byte) (CANVAS_DEFAULT_COLOR.getRed() * 255)
                        , (byte) (CANVAS_DEFAULT_COLOR.getGreen() * 255), (byte) (CANVAS_DEFAULT_COLOR.getBlue() * 255)));
                writableImage = new WritableImage(width, height);
                graphicsContext.getPixelWriter().setPixels(0, 0, width, height, PIXEL_INT_FORMAT, intBuffer, width);
            }
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
                    //全部内容刷新
                    graphicsContext.getPixelWriter().setPixels(0, 0, width, height, PIXEL_INT_FORMAT, intBuffer, width);
                    Image image = chooseResourceImage.getImage();
                    graphicsContext.drawImage(image, event.getX() - (image.getWidth() / 2), event.getY() - (image.getHeight() / 2));
                }
            }
        });
        canvas.setOnMouseExited(event -> graphicsContext.getPixelWriter().setPixels(0, 0, width, height, PIXEL_INT_FORMAT, intBuffer, width));
        canvas.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                ImageView chooseResourceImage = PropertyListener.getChooseResourceImage();
                if (chooseResourceImage != null) {
                    RootController controller = RootApplication.mainFXMLLoader.getController();
                    RadioButton radioButtonIsObject = controller.getRadioButtonIsObject();
                    if (!radioButtonIsObject.isSelected()) {
                        PixelReader pixelReader = chooseResourceImage.getImage().getPixelReader();
                        int forX = (int) chooseResourceImage.getImage().getWidth();
                        int forY = (int) chooseResourceImage.getImage().getHeight();
                        int startX = (int) event.getX() - (forX / 2);
                        int startY = (int) event.getY() - (forY / 2);
                        for (int y = 0; y < forY; y++) {
                            for (int x = 0; x < forX; x++) {
                                int argb = pixelReader.getArgb(x, y);
                                int currentPoint = ((startY + y) * width) + (startX + x);
                                if (argb != 0 && currentPoint >= 0 && currentPoint < intBuffer.capacity()) {
                                    intBuffer.put(currentPoint, argb);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

}
