package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.util.PropertyListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Setter
@Getter
public class ResourceTab {

    public static final FileChooser FILE_CHOOSER = new FileChooser();
    public static final String NAME_PREFIX = "ResourceTab";
    public static final AtomicInteger RESOURCE_TAB_NUMBER = new AtomicInteger(1);

    static {
        FILE_CHOOSER.setTitle("选择资源文件");
        FILE_CHOOSER.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp"));
    }
    @JsonIgnore
    private Tab tab;

    private List<File> resourceImg;
    private String tabName;
    private String id;

    public ResourceTab(List<File> resourceImg, String id) {
        this.resourceImg = resourceImg;
        this.tabName = NAME_PREFIX + RESOURCE_TAB_NUMBER.getAndIncrement();
        this.id = id;
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
                if (resourceImg.contains(bindFile)) {
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
        for (int x = 0; x < resourceImg.size(); x++) {
            ImageView iv = getImageView(x);
            flowPane.getChildren().add(iv);
        }
        scrollPane.setContent(flowPane);
    }

    private ImageView getImageView(int x) {
        Image image = new Image(resourceImg.get(x).toURI().toString(), false);
        ImageView iv = new ImageView();
        iv.setImage(image);
        iv.setSmooth(true);
        iv.setCache(true);
        iv.setUserData(resourceImg.get(x));
        Tooltip.install(iv, new Tooltip("宽" + iv.prefWidth(-1) + "高" + iv.prefHeight(-1)));
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
