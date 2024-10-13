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
    private final TextField runIntervalMilliView = new TextField();
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
    private final TextField attackAudioView = new TextField();
    @JsonIgnore
    private final TextField highAttackAudioView = new TextField();
    @JsonIgnore
    private final TextField hurtAudioView = new TextField();
    @JsonIgnore
    private final TextField moveAudioView = new TextField();
    @JsonIgnore
    private final IntegrationAnimation integrationAnimation = new IntegrationAnimation();
    @JsonIgnore
    private final ImageView idleImageView = new ImageView();
    @JsonIgnore
    private final ImageView walkImageView = new ImageView();
    @JsonIgnore
    private final ImageView runImageView = new ImageView();
    @JsonIgnore
    private final ImageView attackImageView = new ImageView();
    @JsonIgnore
    private final ImageView jumpImageView = new ImageView();
    @JsonIgnore
    private final ImageView highAttackImageView = new ImageView();
    @JsonIgnore
    private final ImageView hurtImageView = new ImageView();
    @JsonIgnore
    private final ImageView deathImageView = new ImageView();
    @JsonIgnore
    private final ImageView magicMoveRightImageView = new ImageView();
    @JsonIgnore
    private final ImageView magicDestructionRightImageView = new ImageView();

    /*
     * 只在初始化的时候更新,修改的时候不同步更新
     * */
    private Integer operationHistoryDistance;
    private Integer moveIntervalMilli;
    private Integer runIntervalMilli;
    private Integer jumpIntervalMilli;
    private Integer attackIntervalMilli;
    private Integer highAttackIntervalMilli;
    private Integer hurtIntervalMilli;
    private Integer attackRange;
    private String imageProperties;
    private String attackAudio;
    private String highAttackAudio;
    private String hurtAudio;
    private String moveAudio;
    private List<String> idleImagesName;
    private List<String> walkRightImagesName;
    private List<String> runRightImagesName;
    private List<String> attackRightImagesName;
    private List<String> jumpRightImagesName;
    private List<String> highAttackRightImagesName;
    private List<String> hurtRightImagesName;
    private List<String> deathRightImagesName;
    private List<String> magicMoveRightImagesName;
    private List<String> magicDestructionRightImagesName;

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
        if (this.runIntervalMilli != null) {
            this.runIntervalMilliView.setText(this.runIntervalMilli.toString());
        }
        this.runIntervalMilliView.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ResourceAnimation.this.runIntervalMilli = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("跑动间隔"), 0, 1);
        gridPane.add(this.runIntervalMilliView, 1, 1);
        if (this.jumpIntervalMilli != null) {
            this.jumpIntervalMilliView.setText(this.jumpIntervalMilli.toString());
            this.integrationAnimation.setJumpDurationControl(new DurationControl(this.jumpIntervalMilli));
        }
        this.jumpIntervalMilliView.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ResourceAnimation.this.jumpIntervalMilli = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("跳跃间隔"), 0, 2);
        gridPane.add(this.jumpIntervalMilliView, 1, 2);
        if (this.attackIntervalMilli != null) {
            this.attackIntervalMilliView.setText(this.attackIntervalMilli.toString());
            this.integrationAnimation.setAttackDurationControl(new DurationControl(this.attackIntervalMilli));
        }
        this.attackIntervalMilliView.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ResourceAnimation.this.attackIntervalMilli = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("功击间隔"), 0, 3);
        gridPane.add(this.attackIntervalMilliView, 1, 3);
        if (this.highAttackIntervalMilli != null) {
            this.highAttackIntervalMilliView.setText(this.highAttackIntervalMilli.toString());
            this.integrationAnimation.setHighAttackDurationControl(new DurationControl(this.highAttackIntervalMilli));
        }
        this.highAttackIntervalMilliView.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ResourceAnimation.this.highAttackIntervalMilli = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("重击间隔"), 0, 4);
        gridPane.add(this.highAttackIntervalMilliView, 1, 4);
        if (this.attackRange != null) {
            this.attackRangeView.setText(this.attackRange.toString());
        }
        this.attackRangeView.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ResourceAnimation.this.attackRange = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("功击范围"), 0, 5);
        gridPane.add(this.attackRangeView, 1, 5);
        if (this.hurtIntervalMilli != null) {
            this.hurtIntervalMilliView.setText(this.hurtIntervalMilli.toString());
        }
        this.hurtIntervalMilliView.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ResourceAnimation.this.hurtIntervalMilli = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("受伤间隔"), 0, 6);
        gridPane.add(this.hurtIntervalMilliView, 1, 6);
        if (this.operationHistoryDistance != null) {
            this.operationHistoryDistanceView.setText(this.operationHistoryDistance.toString());
        }
        this.operationHistoryDistanceView.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ResourceAnimation.this.operationHistoryDistance = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("左右转换时的一个间隔像素"), 0, 7);
        gridPane.add(this.operationHistoryDistanceView, 1, 7);
        if (this.imageProperties != null && !this.imageProperties.isEmpty()) {
            this.imagePropertiesView.setText(this.imageProperties);
        }
        this.imagePropertiesView.textProperty().addListener((observable, oldValue, newValue) -> ResourceAnimation.this.imageProperties = newValue);
        gridPane.add(new Label("图片边距属性"), 0, 8);
        gridPane.add(this.imagePropertiesView, 1, 8);
        if (this.attackAudio != null && !this.attackAudio.isEmpty()) {
            this.attackAudioView.setText(this.attackAudio);
        }
        this.attackAudioView.textProperty().addListener((observable, oldValue, newValue) -> ResourceAnimation.this.attackAudio = newValue);
        gridPane.add(new Label("功击音乐"), 0, 9);
        gridPane.add(this.attackAudioView, 1, 9);
        if (this.highAttackAudio != null && !this.highAttackAudio.isEmpty()) {
            this.highAttackAudioView.setText(this.highAttackAudio);
        }
        this.highAttackAudioView.textProperty().addListener((observable, oldValue, newValue) -> ResourceAnimation.this.highAttackAudio = newValue);
        gridPane.add(new Label("重击音乐"), 0, 10);
        gridPane.add(this.highAttackAudioView, 1, 10);
        if (this.hurtAudio != null && !this.hurtAudio.isEmpty()) {
            this.hurtAudioView.setText(this.hurtAudio);
        }
        this.hurtAudioView.textProperty().addListener((observable, oldValue, newValue) -> ResourceAnimation.this.hurtAudio = newValue);
        gridPane.add(new Label("受伤音乐"), 0, 11);
        gridPane.add(this.hurtAudioView, 1, 11);
        if (this.moveAudio != null && !this.moveAudio.isEmpty()) {
            this.moveAudioView.setText(this.moveAudio);
        }
        this.moveAudioView.textProperty().addListener((observable, oldValue, newValue) -> ResourceAnimation.this.moveAudio = newValue);
        gridPane.add(new Label("移动音乐"), 0, 12);
        gridPane.add(this.moveAudioView, 1, 12);
        this.gridPaneAuto(gridPane, 13);
        scrollPane.setContent(gridPane);
        tab.setContent(scrollPane);
    }

    private void gridPaneAuto(GridPane gridPane, int rowIndex) {
        gridPane.add(this.initIdleImages(), 0, rowIndex);
        gridPane.add(this.idleImageView, 1, rowIndex);
        rowIndex++;
        gridPane.add(this.initWalkImages(), 0, rowIndex);
        gridPane.add(this.walkImageView, 1, rowIndex);
        rowIndex++;
        gridPane.add(this.initRunImages(), 0, rowIndex);
        gridPane.add(this.runImageView, 1, rowIndex);
        rowIndex++;
        gridPane.add(this.initAttackImages(), 0, rowIndex);
        gridPane.add(this.attackImageView, 1, rowIndex);
        rowIndex++;
        gridPane.add(this.initJumpImages(), 0, rowIndex);
        gridPane.add(this.jumpImageView, 1, rowIndex);
        rowIndex++;
        gridPane.add(this.initHighAttackImages(), 0, rowIndex);
        gridPane.add(this.highAttackImageView, 1, rowIndex);
        rowIndex++;
        gridPane.add(this.initHurtImages(), 0, rowIndex);
        gridPane.add(this.hurtImageView, 1, rowIndex);
        rowIndex++;
        gridPane.add(this.initDeathImages(), 0, rowIndex);
        gridPane.add(this.deathImageView, 1, rowIndex);
        rowIndex++;
        gridPane.add(this.initMagicMoveImages(), 0, rowIndex);
        gridPane.add(this.magicMoveRightImageView, 1, rowIndex);
        rowIndex++;
        gridPane.add(this.initMagicDestructionImages(), 0, rowIndex);
        gridPane.add(this.magicDestructionRightImageView, 1, rowIndex);
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

    private VBox initRunImages() {
        if (this.runRightImagesName != null) {
            addRunRightTimeline();
        }
        VBox runVBox = new VBox(10);
        Button runRightButtonAdd = new Button("添加右跑动画");
        Button runRightButtonShow = new Button("播放右跑动画");
        runRightButtonShow.setOnAction(event -> {
            if (this.runRightImagesName != null) {
                ResourceAnimation.this.integrationAnimation.getOperationHistoryThreadLocal().set(IntegrationAnimation.OperationHistory.RIGHT);
                ResourceAnimation.this.integrationAnimation.startAnimation(IntegrationAnimation.AnimationType.RUN);
            }
        });
        runRightButtonAdd.setOnAction(event -> {
            List<File> selectedFiles = ResourceTab.FILE_CHOOSER.showOpenMultipleDialog(RootApplication.mainStage);
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                if (ResourceAnimation.this.runRightImagesName == null) {
                    ResourceAnimation.this.runRightImagesName = new ArrayList<>();
                } else {
                    ResourceAnimation.this.runRightImagesName.clear();
                }
                for (File selectedFile : selectedFiles) {
                    try {
                        File idleDirectory = Path.of(ResourceAnimation.this.directoryFullPath, ResourceAnimation.this.tabName, "runRight", selectedFile.getName()).toFile();
                        if (!idleDirectory.getParentFile().exists()) {
                            idleDirectory.mkdirs();
                        }
                        Files.copy(selectedFile.toPath(), idleDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                        ResourceAnimation.this.runRightImagesName.add(selectedFile.getName());
                    } catch (IOException e) {
                        LogService.printLog(LogService.LogLevel.ERROR, ResourceAnimation.class, "保存动画资源文件", e);
                    }
                }
                ResourceAnimation.this.addRunRightTimeline();
            }
        });
        runVBox.getChildren().addAll(runRightButtonAdd, runRightButtonShow);
        return runVBox;
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

    private VBox initDeathImages() {
        if (this.deathRightImagesName != null) {
            addDeathRightTimeline();
        }
        VBox deathVBox = new VBox(10);
        Button deathRightButtonAdd = new Button("添加死亡动画");
        Button deathRightButtonShow = new Button("播放死亡动画");
        deathRightButtonShow.setOnAction(event -> {
            if (this.deathRightImagesName != null) {
                ResourceAnimation.this.integrationAnimation.startAnimation(IntegrationAnimation.AnimationType.DEATH);
            }
        });
        deathRightButtonAdd.setOnAction(event -> {
            List<File> selectedFiles = ResourceTab.FILE_CHOOSER.showOpenMultipleDialog(RootApplication.mainStage);
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                if (ResourceAnimation.this.deathRightImagesName == null) {
                    ResourceAnimation.this.deathRightImagesName = new ArrayList<>();
                } else {
                    ResourceAnimation.this.deathRightImagesName.clear();
                }
                for (File selectedFile : selectedFiles) {
                    try {
                        File idleDirectory = Path.of(ResourceAnimation.this.directoryFullPath, ResourceAnimation.this.tabName, "deathRight", selectedFile.getName()).toFile();
                        if (!idleDirectory.getParentFile().exists()) {
                            idleDirectory.mkdirs();
                        }
                        Files.copy(selectedFile.toPath(), idleDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                        ResourceAnimation.this.deathRightImagesName.add(selectedFile.getName());
                    } catch (IOException e) {
                        LogService.printLog(LogService.LogLevel.ERROR, ResourceAnimation.class, "保存动画资源文件", e);
                    }
                }
                ResourceAnimation.this.addDeathRightTimeline();
            }
        });
        deathVBox.getChildren().addAll(deathRightButtonAdd, deathRightButtonShow);
        return deathVBox;
    }

    private VBox initMagicMoveImages() {
        if (this.magicMoveRightImagesName != null) {
            addMagicMoveRightTimeline();
        }
        VBox magicMoveVBox = new VBox(10);
        Button magicMoveRightButtonAdd = new Button("添加魔法移动动画");
        Button magicMoveRightButtonShow = new Button("播放魔法移动动画");
        magicMoveRightButtonShow.setOnAction(event -> {
            if (this.magicMoveRightImagesName != null) {
                ResourceAnimation.this.integrationAnimation.startAnimation(IntegrationAnimation.AnimationType.MAGIC_MOVE);
            }
        });
        magicMoveRightButtonAdd.setOnAction(event -> {
            List<File> selectedFiles = ResourceTab.FILE_CHOOSER.showOpenMultipleDialog(RootApplication.mainStage);
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                if (ResourceAnimation.this.magicMoveRightImagesName == null) {
                    ResourceAnimation.this.magicMoveRightImagesName = new ArrayList<>();
                } else {
                    ResourceAnimation.this.magicMoveRightImagesName.clear();
                }
                for (File selectedFile : selectedFiles) {
                    try {
                        File idleDirectory = Path.of(ResourceAnimation.this.directoryFullPath, ResourceAnimation.this.tabName, "magicMoveRight", selectedFile.getName()).toFile();
                        if (!idleDirectory.getParentFile().exists()) {
                            idleDirectory.mkdirs();
                        }
                        Files.copy(selectedFile.toPath(), idleDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                        ResourceAnimation.this.magicMoveRightImagesName.add(selectedFile.getName());
                    } catch (IOException e) {
                        LogService.printLog(LogService.LogLevel.ERROR, ResourceAnimation.class, "保存动画资源文件", e);
                    }
                }
                ResourceAnimation.this.addMagicMoveRightTimeline();
            }
        });
        magicMoveVBox.getChildren().addAll(magicMoveRightButtonAdd, magicMoveRightButtonShow);
        return magicMoveVBox;
    }

    private VBox initMagicDestructionImages() {
        if (this.magicDestructionRightImagesName != null) {
            addMagicDestructionRightTimeline();
        }
        VBox magicDestructionVBox = new VBox(10);
        Button magicDestructionRightButtonAdd = new Button("添加魔法销毁动画");
        Button magicDestructionRightButtonShow = new Button("播放魔法销毁动画");
        magicDestructionRightButtonShow.setOnAction(event -> {
            if (this.magicDestructionRightImagesName != null) {
                ResourceAnimation.this.integrationAnimation.startAnimation(IntegrationAnimation.AnimationType.MAGIC_D);
            }
        });
        magicDestructionRightButtonAdd.setOnAction(event -> {
            List<File> selectedFiles = ResourceTab.FILE_CHOOSER.showOpenMultipleDialog(RootApplication.mainStage);
            if (selectedFiles != null && !selectedFiles.isEmpty()) {
                if (ResourceAnimation.this.magicDestructionRightImagesName == null) {
                    ResourceAnimation.this.magicDestructionRightImagesName = new ArrayList<>();
                } else {
                    ResourceAnimation.this.magicDestructionRightImagesName.clear();
                }
                for (File selectedFile : selectedFiles) {
                    try {
                        File idleDirectory = Path.of(ResourceAnimation.this.directoryFullPath, ResourceAnimation.this.tabName, "magicDestructionRight", selectedFile.getName()).toFile();
                        if (!idleDirectory.getParentFile().exists()) {
                            idleDirectory.mkdirs();
                        }
                        Files.copy(selectedFile.toPath(), idleDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                        ResourceAnimation.this.magicDestructionRightImagesName.add(selectedFile.getName());
                    } catch (IOException e) {
                        LogService.printLog(LogService.LogLevel.ERROR, ResourceAnimation.class, "保存动画资源文件", e);
                    }
                }
                ResourceAnimation.this.addMagicDestructionRightTimeline();
            }
        });
        magicDestructionVBox.getChildren().addAll(magicDestructionRightButtonAdd, magicDestructionRightButtonShow);
        return magicDestructionVBox;
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

    private void addRunRightTimeline() {
        if (!this.runRightImagesName.isEmpty()) {
            List<Image> runRightImg = new ArrayList<>();
            for (String imageName : this.runRightImagesName) {
                Path file = Path.of(this.directoryFullPath, this.tabName, "runRight", imageName);
                runRightImg.add(new Image(file.toUri().toString()));
            }
            this.integrationAnimation.addRunTimeline(this.runImageView, runRightImg, 100, 0, 0, 0);
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

    private void addDeathRightTimeline() {
        if (!this.deathRightImagesName.isEmpty()) {
            List<Image> deathRightImg = new ArrayList<>();
            for (String imageName : this.deathRightImagesName) {
                Path file = Path.of(this.directoryFullPath, this.tabName, "deathRight", imageName);
                deathRightImg.add(new Image(file.toUri().toString()));
            }
            this.integrationAnimation.addDeathTimeline(this.deathImageView, deathRightImg, 100, 0);
        }
    }

    private void addMagicMoveRightTimeline() {
        if (!this.magicMoveRightImagesName.isEmpty()) {
            List<Image> magicMoveRightImg = new ArrayList<>();
            for (String imageName : this.magicMoveRightImagesName) {
                Path file = Path.of(this.directoryFullPath, this.tabName, "magicMoveRight", imageName);
                magicMoveRightImg.add(new Image(file.toUri().toString()));
            }
            this.integrationAnimation.addMagicMoveTimeline(this.magicMoveRightImageView, magicMoveRightImg, 100, 0);
        }
    }

    private void addMagicDestructionRightTimeline() {
        if (!this.magicDestructionRightImagesName.isEmpty()) {
            List<Image> magicDestructionRightImg = new ArrayList<>();
            for (String imageName : this.magicDestructionRightImagesName) {
                Path file = Path.of(this.directoryFullPath, this.tabName, "magicDestructionRight", imageName);
                magicDestructionRightImg.add(new Image(file.toUri().toString()));
            }
            this.integrationAnimation.addMagicDestructionTimeline(this.magicDestructionRightImageView, magicDestructionRightImg, 100, 0);
        }
    }
}
