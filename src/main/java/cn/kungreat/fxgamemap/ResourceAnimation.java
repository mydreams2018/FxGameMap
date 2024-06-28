package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.frame.DurationControl;
import cn.kungreat.fxgamemap.frame.IntegrationAnimation;
import cn.kungreat.fxgamemap.util.LogService;
import cn.kungreat.fxgamemap.util.PatternUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    @JsonIgnore
    private final IntegrationAnimation integrationAnimation = new IntegrationAnimation();
    @JsonIgnore
    private final ImageView idleImageView = new ImageView();

    /*
     * 只在初始化的时候更新,修改的时候不同步更新
     * */
    private Integer moveIntervalMilli;
    private Integer jumpIntervalMilli;
    private Integer attackIntervalMilli;
    private Integer highAttackIntervalMilli;
    private Map<String, String> imageProperties;
    private List<String> idleImagesName;

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
            this.integrationAnimation.setMoveDurationControl(new DurationControl(this.moveIntervalMilli));
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
            this.integrationAnimation.setJumpDurationControl(new DurationControl(this.jumpIntervalMilli));
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
            this.integrationAnimation.setAttackDurationControl(new DurationControl(this.attackIntervalMilli));
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
            this.integrationAnimation.setHighAttackDurationControl(new DurationControl(this.highAttackIntervalMilli));
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
        if (this.idleImagesName != null) {
            addIdleTimeline();
        }
        VBox idleVBox = new VBox(10);
        Button idleButtonAdd = new Button("添加闲置动画");
        Button idleButtonShow = new Button("播放闲置动画");
        idleButtonShow.setOnAction(event -> ResourceAnimation.this.integrationAnimation.startAnimation(IntegrationAnimation.AnimationType.IDLE));
        idleButtonAdd.setOnAction(event -> {
            List<File> selectedFiles = ResourceTab.FILE_CHOOSER.showOpenMultipleDialog(RootApplication.mainStage);
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                if (ResourceAnimation.this.idleImagesName == null) {
                    ResourceAnimation.this.idleImagesName = new ArrayList<>();
                } else {
                    ResourceAnimation.this.idleImagesName.clear();
                }
                for (File selectedFile : selectedFiles) {
                    try {
                        File idleDirectory = Path.of(ResourceAnimation.this.directoryFullPath, ResourceAnimation.this.tabName, "idle", selectedFile.getName()).toFile();
                        if (!idleDirectory.getParentFile().exists()) {
                            idleDirectory.mkdirs();
                        }
                        Files.copy(selectedFile.toPath(), idleDirectory.toPath(),
                                StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                        ResourceAnimation.this.idleImagesName.add(selectedFile.getName());
                    } catch (IOException e) {
                        LogService.printLog(LogService.LogLevel.ERROR, ResourceAnimation.class, "保存动画资源文件", e);
                    }
                }
                ResourceAnimation.this.addIdleTimeline();
            }
        });
        idleVBox.getChildren().addAll(idleButtonAdd, idleButtonShow);
        gridPane.add(idleVBox, 0, 5);
        gridPane.add(this.idleImageView, 1, 5);
        scrollPane.setContent(gridPane);
        tab.setContent(scrollPane);
    }

    private void addIdleTimeline() {
        if (!this.idleImagesName.isEmpty()) {
            List<Image> idleImages = new ArrayList<>();
            for (String imageName : this.idleImagesName) {
                Path file = Path.of(this.directoryFullPath, this.tabName, "idle", imageName);
                idleImages.add(new Image(file.toUri().toString()));
            }
            this.integrationAnimation.addIdleTimeline(this.idleImageView, idleImages, 100, 0);
        }
    }

}
