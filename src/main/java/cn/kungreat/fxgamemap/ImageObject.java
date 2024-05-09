package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.TreeGameMap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
public class ImageObject extends TreeGameMap.BackgroundImageData implements EventHandler<ActionEvent>, ChangeListener<String> {
    @JsonIgnore
    private TitledPane titledPane;

    private String id;
    private String title;
    private ImageObjectType type = ImageObjectType.FIXED_BODY;
    private Integer level;
    private Boolean physical;
    private Integer maxActivityScope;

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
        textType.setOnAction(this);
        gridPane.add(new Label("类型"), 0, 0);
        gridPane.add(textType, 1, 0);
        TextField textLevel = new TextField();
        textLevel.textProperty().addListener(this);
        gridPane.add(new Label("层级"), 0, 1);
        gridPane.add(textLevel, 1, 1);
        ChoiceBox<String> textPhysical = new ChoiceBox<>();
        textPhysical.getItems().addAll("是", "否");
        textPhysical.setOnAction(this);
        gridPane.add(new Label("物理物体"), 0, 2);
        gridPane.add(textPhysical, 1, 2);
        TextField maxActivityScope = new TextField();
        maxActivityScope.textProperty().addListener(this);
        gridPane.add(new Label("最大追杀范围"), 0, 3);
        gridPane.add(maxActivityScope, 1, 3);
        outVBox.getChildren().add(gridPane);
        titledPane.setContent(outVBox);
    }

    @Override
    public void handle(ActionEvent event) {
        System.out.println(event);
    }

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        System.out.println(newValue);
    }
}
