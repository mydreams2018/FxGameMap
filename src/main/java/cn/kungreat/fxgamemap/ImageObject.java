package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.TreeGameMap;
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

    private String id;
    private String title;
    private ImageObjectType type = ImageObjectType.FIXED_BODY;
    private String level;
    private boolean physical = false;
    private String maxActivityScope;

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
        ChoiceBox<String> textType = new ChoiceBox<>();
        textType.getItems().addAll("FIXED_BODY", "BOSS", "ELITE", "MONSTER");
        textType.getSelectionModel().clearAndSelect(type.ordinal());
        textType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> type = ImageObjectType.valueOf(newValue));
        gridPane.add(new Label("类型"), 0, 0);
        gridPane.add(textType, 1, 0);
        TextField textLevel = new TextField();
        textLevel.textProperty().addListener((observable, oldValue, newValue) -> level = newValue);
        if (level != null && !level.isBlank()) {
            textLevel.setText(level);
        }
        gridPane.add(new Label("层级"), 0, 1);
        gridPane.add(textLevel, 1, 1);
        ChoiceBox<String> textPhysical = new ChoiceBox<>();
        textPhysical.getItems().addAll("是", "否");
        textPhysical.getSelectionModel().select(physical ? "是" : "否");
        textPhysical.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> physical = "是".equals(newValue));
        gridPane.add(new Label("物理物体"), 0, 2);
        gridPane.add(textPhysical, 1, 2);
        TextField maxActivityScope = new TextField();
        maxActivityScope.textProperty().addListener((observable, oldValue, newValue) -> ImageObject.this.maxActivityScope = newValue);
        if (this.maxActivityScope != null && !this.maxActivityScope.isBlank()) {
            maxActivityScope.setText(this.maxActivityScope);
        }
        gridPane.add(new Label("最大追杀范围"), 0, 3);
        gridPane.add(maxActivityScope, 1, 3);
        outVBox.getChildren().add(gridPane);
        titledPane.setContent(outVBox);
    }
}
