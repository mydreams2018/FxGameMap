package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.TreeGameMap;
import cn.kungreat.fxgamemap.util.LogService;
import cn.kungreat.fxgamemap.util.PatternUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * 图片对象的描述信息
 * */
@Setter
@Getter
@NoArgsConstructor
public class ImageObject extends TreeGameMap.BackgroundImageData {
    @JsonIgnore
    private TitledPane titledPane;
    @JsonIgnore
    private ChoiceBox<String> textType = new ChoiceBox<>();
    @JsonIgnore
    private ChoiceBox<String> textLevel = new ChoiceBox<>();
    @JsonIgnore
    private ChoiceBox<String> textPhysical = new ChoiceBox<>();
    @JsonIgnore
    private TextField maxActivityScopeText = new TextField();
    @JsonIgnore
    private TextField moveSpeedText = new TextField();
    @JsonIgnore
    private TextField runSpeedText = new TextField();
    @JsonIgnore
    private ChoiceBox<String> actionTypeCheckBox = new ChoiceBox<>();
    @JsonIgnore
    private TextField animationNameText = new TextField();
    @JsonIgnore
    private TextField bloodVolumeText = new TextField();
    @JsonIgnore
    private TextField baseAttackValueText = new TextField();
    @JsonIgnore
    private TextField baseAnimationNameText = new TextField();
    @JsonIgnore
    private Button baseAnimationButton = new Button("添加单独动画");
    @JsonIgnore
    public static final String FIXED_ANIMATION_DIRECTORY = "fixed_animation";
    @JsonIgnore
    private List<File> FixedAnimationFileSrc;
    /*
     * physical 是否物理物体,是否需要检测碰撞
     * level
     * */
    private String id;
    private String title;
    private ImageObjectType type = ImageObjectType.FIXED_BODY;
    private LevelType level = LevelType.DEFAULT_LEVEL;
    private boolean physical = true;
    private String maxActivityScope;
    private Integer moveSpeed;
    private Integer runSpeed;
    private ActionType actionType;
    private String animationName;
    private Integer bloodVolume;
    private Integer baseAttackValue;
    private List<String> baseAnimationName;

    public ImageObject(String id, Image image, double startX, double startY, String imagePath, int locatorX, int locatorY) {
        super(image, startX, startY, imagePath, locatorX, locatorY);
        this.id = id;
    }

    public void initTitledPane() {
        this.titledPane = new TitledPane();
        this.titledPane.setText(this.title);
        VBox outVBox = new VBox(10);
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        for (ImageObjectType value : ImageObjectType.values()) {
            this.textType.getItems().add(value.name());
        }
        this.textType.getSelectionModel().clearAndSelect(type.ordinal());
        this.textType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> ImageObject.this.type = ImageObjectType.valueOf(newValue));
        gridPane.add(new Label("类型"), 0, 0);
        gridPane.add(this.textType, 1, 0);
        for (LevelType value : LevelType.values()) {
            this.textLevel.getItems().add(value.name());
        }
        this.textLevel.getSelectionModel().clearAndSelect(level.ordinal());
        this.textLevel.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> ImageObject.this.level = LevelType.valueOf(newValue));
        gridPane.add(new Label("层级"), 0, 1);
        gridPane.add(this.textLevel, 1, 1);
        this.textPhysical.getItems().addAll("是", "否");
        this.textPhysical.getSelectionModel().select(this.physical ? "是" : "否");
        this.textPhysical.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> this.physical = "是".equals(newValue));
        gridPane.add(new Label("物理物体"), 0, 2);
        gridPane.add(textPhysical, 1, 2);
        if (this.maxActivityScope != null && !this.maxActivityScope.isBlank()) {
            this.maxActivityScopeText.setText(this.maxActivityScope);
        }
        this.maxActivityScopeText.textProperty().addListener((observable, oldValue, newValue) -> ImageObject.this.maxActivityScope = newValue);
        gridPane.add(new Label("最大追杀范围"), 0, 3);
        gridPane.add(this.maxActivityScopeText, 1, 3);
        if (this.moveSpeed != null) {
            this.moveSpeedText.setText(this.moveSpeed.toString());
        }
        this.moveSpeedText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ImageObject.this.moveSpeed = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("移动速度"), 0, 4);
        gridPane.add(this.moveSpeedText, 1, 4);
        for (ActionType value : ActionType.values()) {
            this.actionTypeCheckBox.getItems().add(value.name());
        }
        if (this.actionType != null) {
            this.actionTypeCheckBox.getSelectionModel().clearAndSelect(actionType.ordinal());
        }
        this.actionTypeCheckBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> ImageObject.this.actionType = ActionType.valueOf(newValue));
        gridPane.add(new Label("功击类型"), 0, 5);
        gridPane.add(this.actionTypeCheckBox, 1, 5);
        if (this.animationName != null && !this.animationName.isBlank()) {
            this.animationNameText.setText(this.animationName);
        }
        this.animationNameText.textProperty().addListener((observable, oldValue, newValue) -> ImageObject.this.animationName = newValue);
        gridPane.add(new Label("动画名称"), 0, 6);
        gridPane.add(this.animationNameText, 1, 6);
        if (this.bloodVolume != null) {
            this.bloodVolumeText.setText(this.bloodVolume.toString());
        }
        this.bloodVolumeText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ImageObject.this.bloodVolume = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("怪物血量"), 0, 7);
        gridPane.add(this.bloodVolumeText, 1, 7);
        if (this.baseAttackValue != null) {
            this.baseAttackValueText.setText(this.baseAttackValue.toString());
        }
        this.baseAttackValueText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ImageObject.this.baseAttackValue = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("基础功击"), 0, 8);
        gridPane.add(this.baseAttackValueText, 1, 8);
        if (this.runSpeed != null) {
            this.runSpeedText.setText(this.runSpeed.toString());
        }
        this.runSpeedText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ImageObject.this.runSpeed = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("跑动速度"), 0, 9);
        gridPane.add(this.runSpeedText, 1, 9);
        if (this.baseAnimationName != null) {
            this.baseAnimationNameText.setText(Arrays.toString(this.baseAnimationName.toArray()));
        }
        this.baseAnimationNameText.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                this.baseAnimationName = RootApplication.MAP_JSON.readValue(newValue, new TypeReference<List<String>>() {
                });
            } catch (JsonProcessingException e) {
            }
        });
        this.baseAnimationButton.setOnAction(event -> {
            List<File> selectedFiles = ResourceTab.FILE_CHOOSER.showOpenMultipleDialog(RootApplication.mainStage);
            if (selectedFiles == null || selectedFiles.isEmpty()) {
                this.FixedAnimationFileSrc = null;
                this.baseAnimationNameText.setText("");
                this.baseAnimationName = null;
            } else {
                this.FixedAnimationFileSrc = selectedFiles;
                if (this.baseAnimationName == null) {
                    this.baseAnimationName = new ArrayList<>();
                } else {
                    this.baseAnimationName.clear();
                }
                for (File selectedFile : selectedFiles) {
                    this.baseAnimationName.add(selectedFile.getName());
                }
                this.baseAnimationNameText.setText(Arrays.toString(this.baseAnimationName.toArray()));
            }
        });
        gridPane.add(this.baseAnimationButton, 0, 10);
        gridPane.add(this.baseAnimationNameText, 1, 10);
        outVBox.getChildren().add(gridPane);
        titledPane.setContent(outVBox);
    }

    public List<String> getBaseAnimationName() {
        if (this.FixedAnimationFileSrc != null) {
            this.FixedAnimationFileSrc.forEach(imageSrcPath -> {
                try {
                    File outFile = new File(new File(new URI(Configuration.currentProject).getPath()).getParentFile(), FIXED_ANIMATION_DIRECTORY);
                    outFile.mkdirs();
                    Files.copy(imageSrcPath.toPath(), Path.of(outFile.toString(), imageSrcPath.getName()),
                            StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                } catch (Exception e) {
                    LogService.printLog(LogService.LogLevel.ERROR, ImageObject.class, "保存图片资源文件", e);
                }
            });
        }
        return this.baseAnimationName;
    }

    /*
     * 批量修改时刷新数据
     * */
    public void refresh() {
        this.textType.getSelectionModel().select(this.type.name());
        this.textLevel.getSelectionModel().select(this.level.name());
        this.textPhysical.getSelectionModel().select(this.physical ? "是" : "否");
        this.maxActivityScopeText.setText(this.maxActivityScope);
        if (this.moveSpeed != null) {
            this.moveSpeedText.setText(this.moveSpeed.toString());
        }
        if (this.runSpeed != null) {
            this.runSpeedText.setText(this.runSpeed.toString());
        }
        if (this.actionType != null) {
            this.actionTypeCheckBox.getSelectionModel().select(this.actionType.name());
        }
        if (this.animationName != null) {
            this.animationNameText.setText(this.animationName);
        }
        if (this.baseAnimationName != null) {
            this.baseAnimationNameText.setText(Arrays.toString(this.baseAnimationName.toArray()));
        }
    }
}
