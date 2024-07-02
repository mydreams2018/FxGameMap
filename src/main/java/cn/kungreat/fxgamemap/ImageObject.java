package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.TreeGameMap;
import cn.kungreat.fxgamemap.util.PatternUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private ChoiceBox<String> actionTypeCheckBox = new ChoiceBox<>();
    @JsonIgnore
    private TextField animationNameText = new TextField();

    private String id;
    private String title;
    private ImageObjectType type = ImageObjectType.FIXED_BODY;
    private LevelType level = LevelType.DEFAULT_LEVEL;
    private boolean physical = false;
    private String maxActivityScope;
    private Integer moveSpeed;
    private ActionType actionType;
    private String animationName;

    public ImageObject(String id, Image image, double startX, double startY, String imagePath) {
        super(image, startX, startY, imagePath);
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
        this.actionTypeCheckBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> ImageObject.this.actionType = ActionType.valueOf(newValue));
        gridPane.add(new Label("功击类型"), 0, 5);
        gridPane.add(this.actionTypeCheckBox, 1, 5);
        if (this.animationName != null && !this.animationName.isBlank()) {
            this.animationNameText.setText(this.animationName);
        }
        this.animationNameText.textProperty().addListener((observable, oldValue, newValue) -> ImageObject.this.animationName = newValue);
        gridPane.add(new Label("动画名称"), 0, 6);
        gridPane.add(this.animationNameText, 1, 6);
        outVBox.getChildren().add(gridPane);
        titledPane.setContent(outVBox);
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
        if (this.actionType != null) {
            this.actionTypeCheckBox.getSelectionModel().select(this.actionType.name());
        }
        if (this.animationName != null) {
            this.animationNameText.setText(this.animationName);
        }
    }
}
