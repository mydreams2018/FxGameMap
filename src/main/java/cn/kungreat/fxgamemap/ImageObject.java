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
    ChoiceBox<String> textType = new ChoiceBox<>();
    @JsonIgnore
    TextField textLevel = new TextField();
    @JsonIgnore
    ChoiceBox<String> textPhysical = new ChoiceBox<>();
    @JsonIgnore
    TextField maxActivityScopeText = new TextField();
    @JsonIgnore
    TextField moveSpeedText = new TextField();

    private String id;
    private String title;
    private ImageObjectType type = ImageObjectType.FIXED_BODY;
    private String level;
    private boolean physical = false;
    private String maxActivityScope;
    private Integer moveSpeed;

    public ImageObject(String id, Image image, double startX, double startY, String imagePath) {
        super(image, startX, startY, imagePath);
        this.id = id;
    }

    public void initTitledPane() {
        titledPane = new TitledPane();
        titledPane.setText(title);
        VBox outVBox = new VBox(10);
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        for (ImageObjectType value : ImageObjectType.values()) {
            textType.getItems().add(value.name());
        }
        textType.getSelectionModel().clearAndSelect(type.ordinal());
        textType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> ImageObject.this.type = ImageObjectType.valueOf(newValue));
        gridPane.add(new Label("类型"), 0, 0);
        gridPane.add(textType, 1, 0);
        if (level != null && !level.isBlank()) {
            textLevel.setText(level);
        }
        textLevel.textProperty().addListener((observable, oldValue, newValue) -> level = newValue);
        gridPane.add(new Label("层级"), 0, 1);
        gridPane.add(textLevel, 1, 1);
        textPhysical.getItems().addAll("是", "否");
        textPhysical.getSelectionModel().select(physical ? "是" : "否");
        textPhysical.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> physical = "是".equals(newValue));
        gridPane.add(new Label("物理物体"), 0, 2);
        gridPane.add(textPhysical, 1, 2);
        if (this.maxActivityScope != null && !this.maxActivityScope.isBlank()) {
            maxActivityScopeText.setText(this.maxActivityScope);
        }
        maxActivityScopeText.textProperty().addListener((observable, oldValue, newValue) -> ImageObject.this.maxActivityScope = newValue);
        gridPane.add(new Label("最大追杀范围"), 0, 3);
        gridPane.add(maxActivityScopeText, 1, 3);
        if (this.moveSpeed != null) {
            moveSpeedText.setText(this.moveSpeed.toString());
        }
        moveSpeedText.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && PatternUtils.NumberRegex.matcher(newValue).matches()) {
                ImageObject.this.moveSpeed = Integer.parseInt(newValue);
            }
        });
        gridPane.add(new Label("移动速度"), 0, 4);
        gridPane.add(this.moveSpeedText, 1, 4);
        outVBox.getChildren().add(gridPane);
        titledPane.setContent(outVBox);
    }

    /*
     * 批量修改时刷新数据
     * */
    public void refresh() {
        textType.getSelectionModel().select(type.name());
        textLevel.setText(level);
        textPhysical.getSelectionModel().select(physical ? "是" : "否");
        maxActivityScopeText.setText(this.maxActivityScope);
        moveSpeedText.setText(this.moveSpeed.toString());
    }
}
