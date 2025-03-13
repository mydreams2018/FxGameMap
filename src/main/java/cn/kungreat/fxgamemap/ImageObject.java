package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.TreeGameMap;
import cn.kungreat.fxgamemap.util.LogService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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
    private ChoiceBox<String> textType;
    @JsonIgnore
    private ChoiceBox<String> textPhysical;
    @JsonIgnore
    private TextField maxActivityScopeText;
    @JsonIgnore
    private ChoiceBox<String> monsterTypeCheckBox;
    @JsonIgnore
    private TextField animationIndexText;
    @JsonIgnore
    private TextField baseAnimationNameText;
    @JsonIgnore
    private Button baseAnimationButton;
    @JsonIgnore
    public static final String FIXED_ANIMATION_DIRECTORY = "fixed_animation";
    @JsonIgnore
    private List<File> FixedAnimationFileSrc;
    @JsonIgnore
    private Label monsterAnimationLabel;

    private String id;
    private String title;
    private ImageObjectType type = ImageObjectType.FIXED_ANIMATION;
    private boolean physical = true;
    private String maxActivityScope;
    private MonsterType monsterType;
    private String animationIndex;
    private List<String> baseAnimationName;

    public ImageObject(String id, Image image, double startX, double startY, String imagePath, int locatorX, int locatorY) {
        super(image, startX, startY, imagePath, locatorX, locatorY);
        this.id = id;
    }

    public ImageObject(String id, double startX, double startY, int locatorX, int locatorY) {
        super(startX, startY, locatorX, locatorY);
        this.id = id;
    }

    @Override
    public void initImage(String backgroundImagePath) {
        if (this.type != ImageObjectType.MONSTER) {
            super.initImage(backgroundImagePath);
        } else {
            if (monsterAnimationLabel == null) {
                monsterAnimationLabel = new Label();
                monsterAnimationLabel.setText(this.animationIndex);
                monsterAnimationLabel.setPrefWidth(48);
                monsterAnimationLabel.setPrefHeight(32);
                monsterAnimationLabel.setTextFill(Color.BLUE);
                monsterAnimationLabel.setAlignment(Pos.CENTER);
            }
        }
    }

    public void initTitledPane() {
        if (this.titledPane != null) {
            return;
        }
        this.titledPane = new TitledPane();
        textType = new ChoiceBox<>();
        textPhysical = new ChoiceBox<>();
        maxActivityScopeText = new TextField();
        monsterTypeCheckBox = new ChoiceBox<>();
        animationIndexText = new TextField();
        baseAnimationNameText = new TextField();
        baseAnimationButton = new Button("添加单独动画");
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
        this.textPhysical.getItems().addAll("是", "否");
        this.textPhysical.getSelectionModel().select(this.physical ? "是" : "否");
        this.textPhysical.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> this.physical = "是".equals(newValue));
        gridPane.add(new Label("物理物体"), 0, 1);
        gridPane.add(textPhysical, 1, 1);
        if (this.maxActivityScope != null && !this.maxActivityScope.isBlank()) {
            this.maxActivityScopeText.setText(this.maxActivityScope);
        }
        this.maxActivityScopeText.textProperty().addListener((observable, oldValue, newValue) -> ImageObject.this.maxActivityScope = newValue);
        gridPane.add(new Label("最大追杀范围"), 0, 2);
        gridPane.add(this.maxActivityScopeText, 1, 2);
        for (MonsterType value : MonsterType.values()) {
            this.monsterTypeCheckBox.getItems().add(value.name());
        }
        if (this.monsterType != null) {
            this.monsterTypeCheckBox.getSelectionModel().clearAndSelect(monsterType.ordinal());
        }
        this.monsterTypeCheckBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> ImageObject.this.monsterType = MonsterType.valueOf(newValue));
        gridPane.add(new Label("功击类型"), 0, 3);
        gridPane.add(this.monsterTypeCheckBox, 1, 3);
        if (this.animationIndex != null && !this.animationIndex.isBlank()) {
            this.animationIndexText.setText(this.animationIndex);
        }
        this.animationIndexText.textProperty().addListener((observable, oldValue, newValue) -> ImageObject.this.animationIndex = newValue);
        gridPane.add(new Label("动画名称"), 0, 4);
        gridPane.add(this.animationIndexText, 1, 4);
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
        gridPane.add(this.baseAnimationButton, 0, 5);
        gridPane.add(this.baseAnimationNameText, 1, 5);
        gridPane.add(new Label("id"), 0, 6);
        gridPane.add(new TextField(this.id), 1, 6);
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
        this.textPhysical.getSelectionModel().select(this.physical ? "是" : "否");
        this.maxActivityScopeText.setText(this.maxActivityScope);
        if (this.monsterType != null) {
            this.monsterTypeCheckBox.getSelectionModel().select(this.monsterType.name());
        }
        if (this.animationIndex != null) {
            this.animationIndexText.setText(this.animationIndex);
        }
        if (this.baseAnimationName != null) {
            this.baseAnimationNameText.setText(Arrays.toString(this.baseAnimationName.toArray()));
        }
    }
}
