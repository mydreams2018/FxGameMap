package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.TreeGameMap;
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
    private TextField targetXYText;
    @JsonIgnore
    private ChoiceBox<String> monsterTypeCheckBox;
    @JsonIgnore
    private TextField animationIndexText;
    @JsonIgnore
    private TextField baseAnimationNameText;
    @JsonIgnore
    private Label monsterAnimationLabel;
    @JsonIgnore
    private int amIndex;//显示实时动画效果用的

    private String id;
    private String title;
    private ImageObjectType type = ImageObjectType.FIXED_ANIMATION;
    private boolean physical = true;
    private String maxActivityScope;
    private String targetXY;
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
        if (this.type != ImageObjectType.MONSTER && this.type != ImageObjectType.NPC) {
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
        targetXYText = new TextField();
        monsterTypeCheckBox = new ChoiceBox<>();
        animationIndexText = new TextField();
        baseAnimationNameText = new TextField();
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
            this.refreshAnimationNameText();
        }
        this.baseAnimationNameText.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (newValue == null || newValue.isBlank()) {
                    this.baseAnimationNameText.setText("");
                    this.baseAnimationName = null;
                } else {
                    this.baseAnimationName = RootApplication.MAP_JSON.readValue(newValue, new TypeReference<List<String>>() {
                    });
                }
            } catch (JsonProcessingException e) {
            }
        });
        gridPane.add(new Label("动画图片数据"), 0, 5);
        gridPane.add(this.baseAnimationNameText, 1, 5);
        gridPane.add(new Label("id"), 0, 6);
        gridPane.add(new TextField(this.id), 1, 6);
        if (this.targetXY != null && !this.targetXY.isBlank()) {
            this.targetXYText.setText(this.targetXY);
        }
        this.targetXYText.textProperty().addListener((observable, oldValue, newValue) -> ImageObject.this.targetXY = newValue);
        gridPane.add(new Label("目标XY"), 0, 7);
        gridPane.add(this.targetXYText, 1, 7);
        outVBox.getChildren().add(gridPane);
        titledPane.setContent(outVBox);
    }

    public void refreshAnimationNameText() {
        try {
            this.baseAnimationNameText.setText(RootApplication.MAP_JSON.writeValueAsString(this.baseAnimationName));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
