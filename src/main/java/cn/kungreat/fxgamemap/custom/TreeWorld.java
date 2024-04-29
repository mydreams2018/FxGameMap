package cn.kungreat.fxgamemap.custom;

import cn.kungreat.fxgamemap.RootApplication;
import cn.kungreat.fxgamemap.RootController;
import cn.kungreat.fxgamemap.util.PropertyListener;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
                PropertyListener.changeIsSaved(false);
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
}
