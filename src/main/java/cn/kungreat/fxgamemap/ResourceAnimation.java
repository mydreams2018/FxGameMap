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
import java.util.List;

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
    private final TextField hurtIntervalMilliView = new TextField();
    @JsonIgnore
    private final TextField attackRangeView = new TextField();
    @JsonIgnore
    private final TextField imagePropertiesView = new TextField();
    @JsonIgnore
    private final IntegrationAnimation integrationAnimation = new IntegrationAnimation();
    @JsonIgnore
    private final ImageView idleImageView = new ImageView();
    @JsonIgnore
    private final ImageView walkImageView = new ImageView();
    @JsonIgnore
    private final ImageView attackImageView = new ImageView();
    @JsonIgnore
    private final ImageView jumpImageView = new ImageView();
    @JsonIgnore
    private final ImageView highAttackImageView = new ImageView();
    @JsonIgnore
    private final ImageView hurtImageView = new ImageView();
    /*
     * 只在初始化的时候更新,修改的时候不同步更新
     * */
    private Integer operationHistoryDistance;
    private Integer moveIntervalMilli;
    private Integer jumpIntervalMilli;
    private Integer attackIntervalMilli;
    private Integer highAttackIntervalMilli;
    private Integer hurtIntervalMilli;
    private Integer attackRange;
    private String imageProperties;
    private List<String> idleImagesName;
    private List<String> walkRightImagesName;
    private List<String> attackRightImagesName;
    private List<String> jumpRightImagesName;
    private List<String> highAttackRightImagesName;
    private List<String> hurtRightImagesName;

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
        if (this.attackRange != null) {
            this.attackRangeView.setText(this.attackRange.toString());
        }
        this.attackRangeView.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ResourceAnimation.this.attackRange = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("功击范围"), 0, 4);
        gridPane.add(this.attackRangeView, 1, 4);
        if (this.hurtIntervalMilli != null) {
            this.hurtIntervalMilliView.setText(this.hurtIntervalMilli.toString());
        }
        this.hurtIntervalMilliView.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ResourceAnimation.this.hurtIntervalMilli = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("受伤间隔"), 0, 5);
        gridPane.add(this.hurtIntervalMilliView, 1, 5);
        if (this.operationHistoryDistance != null) {
            this.operationHistoryDistanceView.setText(this.operationHistoryDistance.toString());
        }
        this.operationHistoryDistanceView.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ResourceAnimation.this.operationHistoryDistance = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("左右转换时的一个间隔像素"), 0, 6);
        gridPane.add(this.operationHistoryDistanceView, 1, 6);
        if (this.imageProperties != null && !this.imageProperties.isEmpty()) {
            this.imagePropertiesView.setText(this.imageProperties);
        }
        this.imagePropertiesView.textProperty().addListener((observable, oldValue, newValue) -> ResourceAnimation.this.imageProperties = newValue);
        gridPane.add(new Label("图片边距属性"), 0, 7);
        gridPane.add(this.imagePropertiesView, 1, 7);
        gridPane.add(this.initIdleImages(), 0, 8);
        gridPane.add(this.idleImageView, 1, 8);
        gridPane.add(this.initWalkImages(), 0, 9);
        gridPane.add(this.walkImageView, 1, 9);
        gridPane.add(this.initAttackImages(), 0, 10);
        gridPane.add(this.attackImageView, 1, 10);
        gridPane.add(this.initJumpImages(), 0, 11);
        gridPane.add(this.jumpImageView, 1, 11);
        gridPane.add(this.initHighAttackImages(), 0, 12);
        gridPane.add(this.highAttackImageView, 1, 12);
        gridPane.add(this.initHurtImages(), 0, 13);
        gridPane.add(this.hurtImageView, 1, 13);
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
        if (this.walkRightImagesName != null) {
            addWalkRightTimeline();
        }
        VBox walkVBox = new VBox(10);
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
        walkVBox.getChildren().addAll(walkRightButtonAdd, walkRightButtonShow);
        return walkVBox;
    }

    private VBox initAttackImages() {
        if (this.attackRightImagesName != null) {
            addAttackRightTimeline();
        }
        VBox attackVBox = new VBox(10);
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
        attackVBox.getChildren().addAll(attackRightButtonAdd, attackRightButtonShow);
        return attackVBox;
    }

    private VBox initJumpImages() {
        VBox jumpVBox = new VBox(10);
        if (this.jumpRightImagesName != null) {
            addJumpRightTimeline();
        }
        Button jumpRightButtonAdd = new Button("添加右跳动画");
        Button jumpRightButtonShow = new Button("播放右跳动画");
        jumpRightButtonShow.setOnAction(event -> {
            if (this.jumpRightImagesName != null) {
                ResourceAnimation.this.integrationAnimation.getOperationHistoryThreadLocal().set(IntegrationAnimation.OperationHistory.RIGHT);
                ResourceAnimation.this.integrationAnimation.startAnimation(IntegrationAnimation.AnimationType.JUMP);
            }
        });
        jumpRightButtonAdd.setOnAction(event -> {
            List<File> selectedFiles = ResourceTab.FILE_CHOOSER.showOpenMultipleDialog(RootApplication.mainStage);
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                if (ResourceAnimation.this.jumpRightImagesName == null) {
                    ResourceAnimation.this.jumpRightImagesName = new ArrayList<>();
                } else {
                    ResourceAnimation.this.jumpRightImagesName.clear();
                }
                for (File selectedFile : selectedFiles) {
                    try {
                        File idleDirectory = Path.of(ResourceAnimation.this.directoryFullPath, ResourceAnimation.this.tabName, "jumpRight", selectedFile.getName()).toFile();
                        if (!idleDirectory.getParentFile().exists()) {
                            idleDirectory.mkdirs();
                        }
                        Files.copy(selectedFile.toPath(), idleDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                        ResourceAnimation.this.jumpRightImagesName.add(selectedFile.getName());
                    } catch (IOException e) {
                        LogService.printLog(LogService.LogLevel.ERROR, ResourceAnimation.class, "保存动画资源文件", e);
                    }
                }
                ResourceAnimation.this.addJumpRightTimeline();
            }
        });
        jumpVBox.getChildren().addAll(jumpRightButtonAdd, jumpRightButtonShow);
        return jumpVBox;
    }

    private VBox initHighAttackImages() {
        if (this.highAttackRightImagesName != null) {
            addHighAttackRightTimeline();
        }
        VBox highAttackVBox = new VBox(10);
        Button highAttackRightButtonAdd = new Button("添加右大功动画");
        Button highAttackRightButtonShow = new Button("播放右大功动画");
        highAttackRightButtonShow.setOnAction(event -> {
            if (this.highAttackRightImagesName != null) {
                ResourceAnimation.this.integrationAnimation.startAnimation(IntegrationAnimation.AnimationType.HIGH_ATTACK);
            }
        });
        highAttackRightButtonAdd.setOnAction(event -> {
            List<File> selectedFiles = ResourceTab.FILE_CHOOSER.showOpenMultipleDialog(RootApplication.mainStage);
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                if (ResourceAnimation.this.highAttackRightImagesName == null) {
                    ResourceAnimation.this.highAttackRightImagesName = new ArrayList<>();
                } else {
                    ResourceAnimation.this.highAttackRightImagesName.clear();
                }
                for (File selectedFile : selectedFiles) {
                    try {
                        File idleDirectory = Path.of(ResourceAnimation.this.directoryFullPath, ResourceAnimation.this.tabName, "highAttackRight", selectedFile.getName()).toFile();
                        if (!idleDirectory.getParentFile().exists()) {
                            idleDirectory.mkdirs();
                        }
                        Files.copy(selectedFile.toPath(), idleDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                        ResourceAnimation.this.highAttackRightImagesName.add(selectedFile.getName());
                    } catch (IOException e) {
                        LogService.printLog(LogService.LogLevel.ERROR, ResourceAnimation.class, "保存动画资源文件", e);
                    }
                }
                ResourceAnimation.this.addHighAttackRightTimeline();
            }
        });
        highAttackVBox.getChildren().addAll(highAttackRightButtonAdd, highAttackRightButtonShow);
        return highAttackVBox;
    }

    private VBox initHurtImages() {
        if (this.hurtRightImagesName != null) {
            addHurtRightTimeline();
        }
        VBox hurtVBox = new VBox(10);
        Button hurtRightButtonAdd = new Button("添加受伤动画");
        Button hurtRightButtonShow = new Button("播放受伤动画");
        hurtRightButtonShow.setOnAction(event -> {
            if (this.hurtRightImagesName != null) {
                ResourceAnimation.this.integrationAnimation.startAnimation(IntegrationAnimation.AnimationType.HURT);
            }
        });
        hurtRightButtonAdd.setOnAction(event -> {
            List<File> selectedFiles = ResourceTab.FILE_CHOOSER.showOpenMultipleDialog(RootApplication.mainStage);
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                if (ResourceAnimation.this.hurtRightImagesName == null) {
                    ResourceAnimation.this.hurtRightImagesName = new ArrayList<>();
                } else {
                    ResourceAnimation.this.hurtRightImagesName.clear();
                }
                for (File selectedFile : selectedFiles) {
                    try {
                        File idleDirectory = Path.of(ResourceAnimation.this.directoryFullPath, ResourceAnimation.this.tabName, "hurtRight", selectedFile.getName()).toFile();
                        if (!idleDirectory.getParentFile().exists()) {
                            idleDirectory.mkdirs();
                        }
                        Files.copy(selectedFile.toPath(), idleDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                        ResourceAnimation.this.hurtRightImagesName.add(selectedFile.getName());
                    } catch (IOException e) {
                        LogService.printLog(LogService.LogLevel.ERROR, ResourceAnimation.class, "保存动画资源文件", e);
                    }
                }
                ResourceAnimation.this.addHurtRightTimeline();
            }
        });
        hurtVBox.getChildren().addAll(hurtRightButtonAdd, hurtRightButtonShow);
        return hurtVBox;
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

    private void addWalkRightTimeline() {
        if (!this.walkRightImagesName.isEmpty()) {
            List<Image> walkRightImg = new ArrayList<>();
            for (String imageName : this.walkRightImagesName) {
                Path file = Path.of(this.directoryFullPath, this.tabName, "walkRight", imageName);
                walkRightImg.add(new Image(file.toUri().toString()));
            }
            this.integrationAnimation.addWalkTimeline(this.walkImageView, walkRightImg, 100, 0, 0, 0);
        }
    }

    private void addAttackRightTimeline() {
        if (!this.attackRightImagesName.isEmpty()) {
            List<Image> attackRightImg = new ArrayList<>();
            for (String imageName : this.attackRightImagesName) {
                Path file = Path.of(this.directoryFullPath, this.tabName, "attackRight", imageName);
                attackRightImg.add(new Image(file.toUri().toString()));
            }
            this.integrationAnimation.addAttackTimeline(this.attackImageView, attackRightImg, 100, 0, 0);
        }
    }

    private void addHighAttackRightTimeline() {
        if (!this.highAttackRightImagesName.isEmpty()) {
            List<Image> highAttackRightImg = new ArrayList<>();
            for (String imageName : this.highAttackRightImagesName) {
                Path file = Path.of(this.directoryFullPath, this.tabName, "highAttackRight", imageName);
                highAttackRightImg.add(new Image(file.toUri().toString()));
            }
            this.integrationAnimation.addHighAttackTimeline(this.highAttackImageView, highAttackRightImg, 100, 0, 0);
        }
    }

    private void addHurtRightTimeline() {
        if (!this.hurtRightImagesName.isEmpty()) {
            List<Image> hurtRightImg = new ArrayList<>();
            for (String imageName : this.hurtRightImagesName) {
                Path file = Path.of(this.directoryFullPath, this.tabName, "hurtRight", imageName);
                hurtRightImg.add(new Image(file.toUri().toString()));
            }
            this.integrationAnimation.addHurtTimeline(this.hurtImageView, hurtRightImg, 100, 0, 0);
        }
    }

    private void addJumpRightTimeline() {
        if (!this.jumpRightImagesName.isEmpty()) {
            List<Image> jumpRightImg = new ArrayList<>();
            for (String imageName : this.jumpRightImagesName) {
                Path file = Path.of(this.directoryFullPath, this.tabName, "jumpRight", imageName);
                jumpRightImg.add(new Image(file.toUri().toString()));
            }
            this.integrationAnimation.addJumpTimeline(this.jumpImageView, jumpRightImg, 100, 0, 0);
        }
    }
}
