package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.TreeWorld;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.UUID;

public class RootController implements Initializable {

    private Dialog<String> worldDialog = BaseDialog.getDialog("世界地图", "请输入世界地图名称:", "是否需要添加世界地图层级"
            , BaseDialog.TEXT_FIELD, BaseDialog.APPLY_BUTTON, BaseDialog.CANCEL_BUTTON);

    @FXML
    private HBox hBox;
    @FXML
    private SplitPane splitPane;
    @FXML
    private StackPane stackPaneLeft;
    @FXML
    private Text stackPaneLeftText;
    @FXML
    private StackPane stackPaneCenter;
    @FXML
    private StackPane stackPaneRight;
    @FXML
    private TreeView<Object> treeView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        treeView.setEditable(false);
        treeView.setCellFactory(TextFieldTreeCell.forTreeView(TreeWorld.treeConverter()));
//        treeView.setCellFactory(TreeWorld.treeCallback());
        treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        treeView.prefHeightProperty().bind(stackPaneLeft.heightProperty().subtract(stackPaneLeftText.prefHeight(-1)));
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("add world");
        menuItem.setGraphic(new FontIcon("fas-globe"));
        menuItem.setOnAction(event -> worldDialog.showAndWait());
        contextMenu.getItems().addAll(menuItem);
        treeView.setContextMenu(contextMenu);
        worldDialog.setOnShowing(event -> BaseDialog.TEXT_FIELD.clear());

        //树节点数据添加
        Button applyButton = (Button) worldDialog.getDialogPane().lookupButton(BaseDialog.APPLY_BUTTON);
        applyButton.setOnAction(event -> {
            String newWorld = BaseDialog.TEXT_FIELD.getText();
            if (!newWorld.isBlank()) {
                TreeItem<Object> root = treeView.getRoot();
                TreeWorld treeWorld = new TreeWorld(newWorld, UUID.randomUUID().toString(), null);
                TreeItem<Object> treeItem = new TreeItem<>(treeWorld);
                treeItem.setGraphic(new FontIcon("fas-globe"));
                root.getChildren().add(treeItem);
            }
        });

    }
}