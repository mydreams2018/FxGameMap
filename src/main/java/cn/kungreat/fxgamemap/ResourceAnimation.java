package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.util.PatternUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
public class ResourceAnimation {

    public static final DirectoryChooser DIRECTORY_CHOOSER = new DirectoryChooser();

    static {
        DIRECTORY_CHOOSER.setTitle("选择目录");
    }

    private String id;
    private String tabName;
    private String directoryFullPath;

    public ResourceAnimation(String id, String tabName, String directoryFullPath) {
        this.id = id;
        this.tabName = tabName;
        this.directoryFullPath = directoryFullPath;
    }

    @JsonIgnore
    private Tab tab;
    @JsonIgnore
    private final TextField moveIntervalMilliView = new TextField();
    @JsonIgnore
    private final TextField jumpIntervalMilliView = new TextField();
    @JsonIgnore
    private final TextField attackIntervalMilliView = new TextField();
    @JsonIgnore
    private final TextField highAttackIntervalMilliView = new TextField();
    @JsonIgnore
    private final TextArea imagePropertiesArea = new TextArea();

    private Integer moveIntervalMilli;
    private Integer jumpIntervalMilli;
    private Integer attackIntervalMilli;
    private Integer highAttackIntervalMilli;
    private Map<String, String> imageProperties;

    public void initTab() {
        tab = new Tab();
        tab.setClosable(true);
        tab.setText(tabName);
        tab.setGraphic(new FontIcon("fas-map"));
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        tab.setOnClosed(event -> RootApplication.RESOURCES.getResourceAnimations().remove(ResourceAnimation.this));
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        if (this.moveIntervalMilli != null) {
            this.moveIntervalMilliView.setText(this.moveIntervalMilli.toString());
        }
        this.moveIntervalMilliView.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ResourceAnimation.this.moveIntervalMilli = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("移动间隔"), 0, 0);
        gridPane.add(this.moveIntervalMilliView, 1, 0);
        if (this.jumpIntervalMilli != null) {
            this.jumpIntervalMilliView.setText(this.jumpIntervalMilli.toString());
        }
        this.jumpIntervalMilliView.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ResourceAnimation.this.jumpIntervalMilli = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("跳跃间隔"), 0, 1);
        gridPane.add(this.jumpIntervalMilliView, 1, 1);
        if (this.attackIntervalMilli != null) {
            this.attackIntervalMilliView.setText(this.attackIntervalMilli.toString());
        }
        this.attackIntervalMilliView.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ResourceAnimation.this.attackIntervalMilli = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("功击间隔"), 0, 2);
        gridPane.add(this.attackIntervalMilliView, 1, 2);
        if (this.highAttackIntervalMilli != null) {
            this.highAttackIntervalMilliView.setText(this.highAttackIntervalMilli.toString());
        }
        this.highAttackIntervalMilliView.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ResourceAnimation.this.highAttackIntervalMilli = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("重击间隔"), 0, 3);
        gridPane.add(this.highAttackIntervalMilliView, 1, 3);
        this.imagePropertiesArea.setWrapText(false);
        if (this.imageProperties != null) {
            StringBuilder propertiesBuild = new StringBuilder();
            for (Map.Entry<String, String> propertiesEntry : imageProperties.entrySet()) {
                propertiesBuild.append(propertiesEntry.getKey()).append("=").append(propertiesEntry.getValue()).append("\n");
            }
            this.imagePropertiesArea.setText(propertiesBuild.toString());
        } else {
            this.imageProperties = new HashMap<>();
        }
        this.imagePropertiesArea.textProperty().addListener((observable, oldValue, newValue) -> {
            ResourceAnimation.this.imageProperties.clear();
            String[] splitProperties = newValue.split("\n", 5);
            for (String property : splitProperties) {
                if (property.contains("=")) {
                    String[] splitKV = property.split("=", 5);
                    ResourceAnimation.this.imageProperties.put(splitKV[0], splitKV[1]);
                }
            }
        });
        gridPane.add(new Label("图片边距属性"), 0, 4);
        gridPane.add(this.imagePropertiesArea, 1, 4);
        scrollPane.setContent(gridPane);
        tab.setContent(scrollPane);
    }
}
