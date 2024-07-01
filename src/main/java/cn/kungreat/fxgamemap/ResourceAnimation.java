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
    private final TextField operationHistoryDistanceView = new TextField();
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
    @JsonIgnore
    private final ImageView walkImageView = new ImageView();
    @JsonIgnore
    private final ImageView attackImageView = new ImageView();
    /*
     * 只在初始化的时候更新,修改的时候不同步更新
     * */
    private Integer operationHistoryDistance;
    private Integer moveIntervalMilli;
    private Integer jumpIntervalMilli;
    private Integer attackIntervalMilli;
    private Integer highAttackIntervalMilli;
    private Map<String, String> imageProperties;
    private List<String> idleImagesName;
    private List<String> walkLeftImagesName;
    private List<String> walkRightImagesName;
    private List<String> attackLeftImagesName;
    private List<String> attackRightImagesName;

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
        if (this.operationHistoryDistance != null) {
            this.operationHistoryDistanceView.setText(this.operationHistoryDistance.toString());
        }
        this.operationHistoryDistanceView.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ResourceAnimation.this.operationHistoryDistance = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("左右转换时的一个间隔像素"), 0, 4);
        gridPane.add(this.operationHistoryDistanceView, 1, 4);
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
        gridPane.add(new Label("图片边距属性"), 0, 5);
        gridPane.add(this.imagePropertiesArea, 1, 5);
        gridPane.add(initIdleImages(), 0, 6);
        gridPane.add(this.idleImageView, 1, 6);
        gridPane.add(initWalkImages(), 0, 7);
        gridPane.add(this.walkImageView, 1, 7);
        gridPane.add(initAttackImages(), 0, 8);
        gridPane.add(this.attackImageView, 1, 8);
        scrollPane.setContent(gridPane);
        tab.setContent(scrollPane);
    }

    private VBox initIdleImages() {
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
        return idleVBox;
    }

    private VBox initWalkImages() {
        if (this.walkLeftImagesName != null) {
            addWalkLeftTimeline();
        }
        if (this.walkRightImagesName != null) {
            addWalkRightTimeline();
        }
        VBox walkLeftVBox = new VBox(10);
        Button walkLeftButtonAdd = new Button("添加左移动画");
        Button walkLeftButtonShow = new Button("播放左移动画");
        walkLeftButtonShow.setOnAction(event -> {
            if (this.walkLeftImagesName != null) {
                ResourceAnimation.this.integrationAnimation.getOperationHistoryThreadLocal().set(IntegrationAnimation.OperationHistory.LEFT);
                ResourceAnimation.this.integrationAnimation.startAnimation(IntegrationAnimation.AnimationType.WALK);
            }
        });
        walkLeftButtonAdd.setOnAction(event -> {
            List<File> selectedFiles = ResourceTab.FILE_CHOOSER.showOpenMultipleDialog(RootApplication.mainStage);
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                if (ResourceAnimation.this.walkLeftImagesName == null) {
                    ResourceAnimation.this.walkLeftImagesName = new ArrayList<>();
                } else {
                    ResourceAnimation.this.walkLeftImagesName.clear();
                }
                for (File selectedFile : selectedFiles) {
                    try {
                        File idleDirectory = Path.of(ResourceAnimation.this.directoryFullPath, ResourceAnimation.this.tabName, "walkLeft", selectedFile.getName()).toFile();
                        if (!idleDirectory.getParentFile().exists()) {
                            idleDirectory.mkdirs();
                        }
                        Files.copy(selectedFile.toPath(), idleDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                        ResourceAnimation.this.walkLeftImagesName.add(selectedFile.getName());
                    } catch (IOException e) {
                        LogService.printLog(LogService.LogLevel.ERROR, ResourceAnimation.class, "保存动画资源文件", e);
                    }
                }
                ResourceAnimation.this.addWalkLeftTimeline();
            }
        });
        Button walkRightButtonAdd = new Button("添加右移动画");
        Button walkRightButtonShow = new Button("播放右移动画");
        walkRightButtonShow.setOnAction(event -> {
            if (this.walkRightImagesName != null) {
                ResourceAnimation.this.integrationAnimation.getOperationHistoryThreadLocal().set(IntegrationAnimation.OperationHistory.RIGHT);
                ResourceAnimation.this.integrationAnimation.startAnimation(IntegrationAnimation.AnimationType.WALK);
            }
        });
        walkRightButtonAdd.setOnAction(event -> {
            List<File> selectedFiles = ResourceTab.FILE_CHOOSER.showOpenMultipleDialog(RootApplication.mainStage);
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                if (ResourceAnimation.this.walkRightImagesName == null) {
                    ResourceAnimation.this.walkRightImagesName = new ArrayList<>();
                } else {
                    ResourceAnimation.this.walkRightImagesName.clear();
                }
                for (File selectedFile : selectedFiles) {
                    try {
                        File idleDirectory = Path.of(ResourceAnimation.this.directoryFullPath, ResourceAnimation.this.tabName, "walkRight", selectedFile.getName()).toFile();
                        if (!idleDirectory.getParentFile().exists()) {
                            idleDirectory.mkdirs();
                        }
                        Files.copy(selectedFile.toPath(), idleDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                        ResourceAnimation.this.walkRightImagesName.add(selectedFile.getName());
                    } catch (IOException e) {
                        LogService.printLog(LogService.LogLevel.ERROR, ResourceAnimation.class, "保存动画资源文件", e);
                    }
                }
                ResourceAnimation.this.addWalkRightTimeline();
            }
        });
        walkLeftVBox.getChildren().addAll(walkLeftButtonAdd, walkLeftButtonShow, walkRightButtonAdd, walkRightButtonShow);
        return walkLeftVBox;
    }

    private VBox initAttackImages() {
        if (this.attackLeftImagesName != null) {
            addAttackLeftTimeline();
        }
        if (this.attackRightImagesName != null) {
            addAttackRightTimeline();
        }
        VBox attackLeftVBox = new VBox(10);
        Button attackLeftButtonAdd = new Button("添加左功动画");
        Button attackLeftButtonShow = new Button("播放左功动画");
        attackLeftButtonShow.setOnAction(event -> {
            if (this.attackLeftImagesName != null) {
                ResourceAnimation.this.integrationAnimation.getOperationHistoryThreadLocal().set(IntegrationAnimation.OperationHistory.LEFT);
                ResourceAnimation.this.integrationAnimation.startAnimation(IntegrationAnimation.AnimationType.ATTACK);
            }
        });
        attackLeftButtonAdd.setOnAction(event -> {
            List<File> selectedFiles = ResourceTab.FILE_CHOOSER.showOpenMultipleDialog(RootApplication.mainStage);
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                if (ResourceAnimation.this.attackLeftImagesName == null) {
                    ResourceAnimation.this.attackLeftImagesName = new ArrayList<>();
                } else {
                    ResourceAnimation.this.attackLeftImagesName.clear();
                }
                for (File selectedFile : selectedFiles) {
                    try {
                        File idleDirectory = Path.of(ResourceAnimation.this.directoryFullPath, ResourceAnimation.this.tabName, "attackLeft", selectedFile.getName()).toFile();
                        if (!idleDirectory.getParentFile().exists()) {
                            idleDirectory.mkdirs();
                        }
                        Files.copy(selectedFile.toPath(), idleDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                        ResourceAnimation.this.attackLeftImagesName.add(selectedFile.getName());
                    } catch (IOException e) {
                        LogService.printLog(LogService.LogLevel.ERROR, ResourceAnimation.class, "保存动画资源文件", e);
                    }
                }
                ResourceAnimation.this.addAttackLeftTimeline();
            }
        });
        Button attackRightButtonAdd = new Button("添加右功动画");
        Button attackRightButtonShow = new Button("播放右功动画");
        attackRightButtonShow.setOnAction(event -> {
            if (this.attackRightImagesName != null) {
                ResourceAnimation.this.integrationAnimation.getOperationHistoryThreadLocal().set(IntegrationAnimation.OperationHistory.RIGHT);
                ResourceAnimation.this.integrationAnimation.startAnimation(IntegrationAnimation.AnimationType.ATTACK);
            }
        });
        attackRightButtonAdd.setOnAction(event -> {
            List<File> selectedFiles = ResourceTab.FILE_CHOOSER.showOpenMultipleDialog(RootApplication.mainStage);
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                if (ResourceAnimation.this.attackRightImagesName == null) {
                    ResourceAnimation.this.attackRightImagesName = new ArrayList<>();
                } else {
                    ResourceAnimation.this.attackRightImagesName.clear();
                }
                for (File selectedFile : selectedFiles) {
                    try {
                        File idleDirectory = Path.of(ResourceAnimation.this.directoryFullPath, ResourceAnimation.this.tabName, "attackRight", selectedFile.getName()).toFile();
                        if (!idleDirectory.getParentFile().exists()) {
                            idleDirectory.mkdirs();
                        }
                        Files.copy(selectedFile.toPath(), idleDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                        ResourceAnimation.this.attackRightImagesName.add(selectedFile.getName());
                    } catch (IOException e) {
                        LogService.printLog(LogService.LogLevel.ERROR, ResourceAnimation.class, "保存动画资源文件", e);
                    }
                }
                ResourceAnimation.this.addAttackRightTimeline();
            }
        });
        attackLeftVBox.getChildren().addAll(attackLeftButtonAdd, attackLeftButtonShow, attackRightButtonAdd, attackRightButtonShow);
        return attackLeftVBox;
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

    private void addWalkLeftTimeline() {
        if (!this.walkLeftImagesName.isEmpty()) {
            List<Image> walkLeftImg = new ArrayList<>();
            for (String imageName : this.walkLeftImagesName) {
                Path file = Path.of(this.directoryFullPath, this.tabName, "walkLeft", imageName);
                walkLeftImg.add(new Image(file.toUri().toString()));
            }
            this.integrationAnimation.addWalkTimeline(this.walkImageView, null, walkLeftImg, 100, 0, 0, 0);
        }
    }

    private void addWalkRightTimeline() {
        if (!this.walkRightImagesName.isEmpty()) {
            List<Image> walkRightImg = new ArrayList<>();
            for (String imageName : this.walkRightImagesName) {
                Path file = Path.of(this.directoryFullPath, this.tabName, "walkRight", imageName);
                walkRightImg.add(new Image(file.toUri().toString()));
            }
            this.integrationAnimation.addWalkTimeline(this.walkImageView, walkRightImg, null, 100, 0, 0, 0);
        }
    }

    private void addAttackLeftTimeline() {
        if (!this.attackLeftImagesName.isEmpty()) {
            List<Image> attackLeftImg = new ArrayList<>();
            for (String imageName : this.attackLeftImagesName) {
                Path file = Path.of(this.directoryFullPath, this.tabName, "attackLeft", imageName);
                attackLeftImg.add(new Image(file.toUri().toString()));
            }
            this.integrationAnimation.addAttackTimeline(this.attackImageView, null, attackLeftImg, 100, 0, 0);
        }
    }

    private void addAttackRightTimeline() {
        if (!this.attackRightImagesName.isEmpty()) {
            List<Image> attackRightImg = new ArrayList<>();
            for (String imageName : this.attackRightImagesName) {
                Path file = Path.of(this.directoryFullPath, this.tabName, "attackRight", imageName);
                attackRightImg.add(new Image(file.toUri().toString()));
            }
            this.integrationAnimation.addAttackTimeline(this.attackImageView, attackRightImg, null, 100, 0, 0);
        }
    }

}
