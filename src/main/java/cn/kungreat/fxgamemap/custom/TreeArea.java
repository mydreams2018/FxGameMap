package cn.kungreat.fxgamemap.custom;

import cn.kungreat.fxgamemap.BaseDialog;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Setter
@Getter
@NoArgsConstructor
public class TreeArea {
    private String title;
    private String id;
    /*
     * xNumber x坐标有几个地图
     * yNumber y坐标有几个地图
     * */
    private Integer xNumber;
    private Integer yNumber;
    private List<TreeGameMap> childrenMap;
    private String imageDirectory;
    private String[][] childrenPointName;
    @JsonIgnore
    private GridPane gridPane;

    public static final ObservableList<String> STRING_OBSERVABLE_LIST = FXCollections.observableArrayList();
    public static final Dialog<String> STRING_OBSERVABLE_DIALOG = BaseDialog.getChildrenPointDialog();

    public TreeArea(String title, String id, Integer xNumber, Integer yNumber, List<TreeGameMap> childrenMap, String imageDirectory) {
        this.title = title;
        this.id = id;
        this.xNumber = xNumber;
        this.yNumber = yNumber;
        this.childrenMap = childrenMap;
        this.imageDirectory = imageDirectory;
    }

    public void initGridPane() {
        if (gridPane == null) {
            gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setMaxHeight(Control.USE_PREF_SIZE);
            if (childrenPointName == null) {
                childrenPointName = new String[xNumber][yNumber];
            }
            for (int y = 0; y < yNumber; y++) {
                for (int x = 0; x < xNumber; x++) {
                    HBox hBoxTwo = new HBox(6);
                    hBoxTwo.getChildren().addAll(new Label("名称:"), new Label(childrenPointName[x][y]));
                    hBoxTwo.setMinWidth(Control.USE_PREF_SIZE);
                    hBoxTwo.setAlignment(Pos.CENTER);
                    hBoxTwo.setPadding(new Insets(10, 10, 10, 10));
                    hBoxTwo.setBorder(Border.stroke(Color.GRAY));
                    hBoxTwo.setOnMouseClicked(new PointEventHandler(x, y));
                    gridPane.add(hBoxTwo, x, y);
                }
            }
        }
    }

    public static void addChildrenPointDialogEvent() {
        Button apply = (Button) STRING_OBSERVABLE_DIALOG.getDialogPane().lookupButton(BaseDialog.AREA_LINK_APPLY);
        Button cancel = (Button) STRING_OBSERVABLE_DIALOG.getDialogPane().lookupButton(BaseDialog.AREA_LINK_CANCEL);
        apply.setOnAction(event -> STRING_OBSERVABLE_DIALOG.setResult("OK"));
        cancel.setOnAction(event -> STRING_OBSERVABLE_DIALOG.setResult("CANCEL"));
    }

    @Setter
    @Getter
    public final class PointEventHandler implements EventHandler<MouseEvent> {
        private int x;
        private int y;

        public PointEventHandler(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void handle(MouseEvent event) {
            STRING_OBSERVABLE_LIST.clear();
            if (childrenMap != null) {
                childrenMap.forEach(treeGameMap -> STRING_OBSERVABLE_LIST.add(treeGameMap.getTitle()));
            }
            Optional<String> re = STRING_OBSERVABLE_DIALOG.showAndWait();
            if (re.isPresent() && "OK".equals(re.get())) {
                ChoiceBox<String> choiceBox = (ChoiceBox<String>) STRING_OBSERVABLE_DIALOG.getGraphic();
                if (choiceBox.getValue() != null) {
                    TreeArea.this.childrenPointName[x][y] = choiceBox.getValue();
                    HBox hBox = (HBox) event.getSource();
                    Label label = (Label) hBox.getChildren().getLast();
                    label.setText(choiceBox.getValue());
                    STRING_OBSERVABLE_DIALOG.setResult("CANCEL");
                }
            }
        }
    }

}
