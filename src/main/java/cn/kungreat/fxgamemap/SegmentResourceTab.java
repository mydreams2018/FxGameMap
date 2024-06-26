package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.util.LogService;
import cn.kungreat.fxgamemap.util.PropertyListener;
import cn.kungreat.fxgamemap.util.WorkThread;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Setter
@Getter
@NoArgsConstructor
public class SegmentResourceTab {
    @JsonIgnore
    private Tab tab;

    private String id;
    private String tabName;
    private String filePath;
    private Integer segmentWidth;
    private Integer segmentHeight;
    private Integer segmentMargin;
    private Integer segmentPadding;

    public SegmentResourceTab(String id, String name, String filePath, Integer segmentWidth, Integer segmentHeight,
                              Integer segmentMargin, Integer segmentPadding) {
        this.id = id;
        this.tabName = name;
        this.filePath = filePath;
        this.segmentHeight = segmentHeight;
        this.segmentMargin = segmentMargin;
        this.segmentPadding = segmentPadding;
        this.segmentWidth = segmentWidth;
    }

    public void initTab() {
        tab = new Tab();
        tab.setClosable(true);
        tab.setText(tabName);
        tab.setGraphic(new FontIcon("fas-map"));
        ScrollPane scrollPane = new ScrollPane();
        tab.setContent(scrollPane);
        tab.setOnClosed(event -> {
            ImageView chooseResourceImage = PropertyListener.getChooseResourceImage();
            if (chooseResourceImage != null) {
                Object bindFile = chooseResourceImage.getUserData();
                if (filePath.equals(bindFile)) {
                    PropertyListener.changeChooseResourceImage(null);
                }
            }
            PropertyListener.changeIsSaved(false);
            RootApplication.RESOURCES.getSegmentResourceTabList().remove(SegmentResourceTab.this);
        });
        scrollPane.setCursor(Cursor.HAND);
        scrollPane.setFitToWidth(true);
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(1);
        flowPane.setVgap(1);
        flowPane.setColumnHalignment(HPos.LEFT);
        flowPane.setAlignment(Pos.CENTER_LEFT);
        flowPane.setRowValignment(VPos.CENTER);
        Image image = new Image(new File(filePath).toURI().toString());
        int imageWidth = (int) image.getWidth();
        int imageHeight = (int) image.getHeight();
        int loopX = imageWidth / (segmentWidth + segmentMargin);
        int loopY = imageHeight / (segmentHeight + segmentMargin);
        for (int y = 0; y < loopY; y++) {
            for (int x = 0; x < loopX; x++) {
                ImageView iv = getImageView(x, y, image);
                Tooltip.install(iv, new Tooltip("宽" + iv.prefWidth(-1) + "高" + iv.prefHeight(-1)));
                flowPane.getChildren().add(iv);
            }
        }
        scrollPane.setContent(flowPane);
    }

    private ImageView getImageView(int x, int y, Image image) {
        double minX = x * (segmentWidth + segmentMargin);
        double minY = y * (segmentHeight + segmentMargin);
        WritableImage targetImage = new WritableImage(image.getPixelReader(), (int) minX, (int) minY,
                segmentWidth - segmentPadding, segmentHeight - segmentPadding);
        ImageView iv = new ImageView();
        try {
            File targetFile = File.createTempFile("SRT", ".png");
            iv.setId(targetFile.toURI().toString());
            WorkThread.THREAD_POOL_EXECUTOR.execute(() -> {
                try {
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(targetImage, null);
                    ImageIO.write(bufferedImage, "png", targetFile);
                    targetFile.deleteOnExit();
                } catch (IOException e) {
                    LogService.printLog(LogService.LogLevel.ERROR, SegmentResourceTab.class, "分割图片资源文件", e);
                }
            });
        } catch (IOException e) {
            LogService.printLog(LogService.LogLevel.ERROR, SegmentResourceTab.class, "创建临时图片资源文件", e);
        }
        iv.setImage(targetImage);
        iv.setUserData(filePath);
        iv.setOnMouseClicked(event -> {
            MouseButton button = event.getButton();
            if (button == MouseButton.PRIMARY) {
                ImageView source = (ImageView) event.getSource();
                PropertyListener.changeChooseResourceImage(source);
            }
        });
        return iv;
    }
}
