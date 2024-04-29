package cn.kungreat.fxgamemap.custom;

import cn.kungreat.fxgamemap.RootApplication;
import cn.kungreat.fxgamemap.RootController;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TreeWorld {
    private String title;
    private String id;
    private List<TreeArea> childrenArea;

    public static StringConverter<Object> treeConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(Object item) {
                return switch (item) {
                    case String s -> s;
                    case TreeWorld treeWorld -> treeWorld.getTitle();
                    case TreeArea treeArea -> treeArea.getTitle();
                    case TreeGameMap treeGameApp -> treeGameApp.getTitle();
                    case null, default -> throw new RuntimeException("TreeWorld-treeConverter-类型错误");
                };
            }

            //可以修改才有用
            @Override
            public Object fromString(String title) {
                RootController controller = RootApplication.mainFXMLLoader.getController();
                TreeItem<Object> item = controller.getTreeView().getFocusModel().getFocusedItem();
                Object treeValue = item.getValue();
                switch (treeValue) {
                    case TreeWorld treeWorld -> treeWorld.setTitle(title);
                    case TreeArea treeArea -> treeArea.setTitle(title);
                    case TreeGameMap treeGameApp -> treeGameApp.setTitle(title);
                    case null, default -> throw new RuntimeException("TreeWorld-treeConverter-类型错误");
                }
                return treeValue;
            }
        };
    }
    /*
    *  根据树结构提供的功能,自定义组件显示太麻烦放弃
    * */
    public static Callback<TreeView<Object>, TreeCell<Object>> treeCallback() {
        return new Callback<>() {
            @Override
            public TreeCell<Object> call(TreeView<Object> param) {
                return new TreeCell<>() {
                    private final HBox hBox;
                    private final Text text;

                    {
                        setContentDisplay(ContentDisplay.RIGHT);
                        hBox = new HBox();
                        hBox.setAlignment(Pos.CENTER_LEFT);
                        text = new Text();
                        hBox.getChildren().addAll(new FontIcon("fas-globe"), text, new Text("添加子级"));
                    }

                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(null);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            switch (item) {
                                case String s -> text.setText(s);
                                case TreeWorld treeWorld -> text.setText(treeWorld.getTitle());
                                case TreeArea treeArea -> text.setText(treeArea.getTitle());
                                case TreeGameMap treeGameApp -> text.setText(treeGameApp.getTitle());
                                case null, default -> throw new RuntimeException("TreeWorld-treeCallback-类型错误");
                            }
                            setGraphic(hBox);
                        }
                    }
                };
            }
        };
    }
}
