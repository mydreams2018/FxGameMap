package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.util.PropertyListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;

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

    public SegmentResourceTab(String id, String name, String filePath, Integer segmentWidth,Integer segmentHeight,
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
        });
        scrollPane.setCursor(Cursor.HAND);
        scrollPane.setFitToWidth(true);
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(3);
        flowPane.setVgap(3);
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
        Rectangle2D viewportRect = new Rectangle2D(minX, minY, segmentWidth - segmentPadding, segmentHeight - segmentPadding);
        ImageView iv = new ImageView();
        iv.setImage(image);
        iv.setViewport(viewportRect);
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
