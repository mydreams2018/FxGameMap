package cn.kungreat.fxgamemap;

import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/* 泛型影响
 * final ObjectProperty<Callback<ButtonType,R>> resultConverter
 * final ObjectProperty<R> result
 * */
public class BaseDialog {

    public static final ButtonType APPLY_WORLD = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
    public static final ButtonType CANCEL_WORLD = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    public static final ButtonType APPLY_AREA = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
    public static final ButtonType CANCEL_AREA = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    public static final ButtonType APPLY_MAP = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
    public static final ButtonType CANCEL_MAP = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    public static final TextField TEXT_WORLD = BaseDialog.getTextField("请输入名称");

    public static final TextField TEXT_AREA = BaseDialog.getTextField("请输入名称");
    public static final TextField TEXT_AREAX = BaseDialog.getTextField("请输入X坐标地图有几个");
    public static final TextField TEXT_AREAY = BaseDialog.getTextField("请输入Y坐标地图有几个");

    public static final TextField TEXT_MAP = BaseDialog.getTextField("请输入名称");
    public static final TextField TEXT_MAP_WIDTH = BaseDialog.getTextField("请输入宽度");
    public static final TextField TEXT_MAP_HEIGHT = BaseDialog.getTextField("请输入高度");

    public static Dialog<String> getDialog(String title, String header, String content, Node graphic, ButtonType... buttonType) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        dialog.setGraphic(graphic);
        dialog.getDialogPane().getButtonTypes().addAll(buttonType);
        dialog.setResizable(false);
        return dialog;
    }

    public static TextField getTextField(String promptText) {
        TextField tf = new TextField();
        tf.setPromptText(promptText);
        return tf;
    }

    public static VBox getMapRectangular() {
        VBox vb = new VBox();
        vb.getChildren().addAll(TEXT_MAP, TEXT_MAP_WIDTH, TEXT_MAP_HEIGHT);
        return vb;
    }

    public static VBox getAreaRectangular() {
        VBox vb = new VBox();
        vb.getChildren().addAll(TEXT_AREA, TEXT_AREAX, TEXT_AREAY);
        return vb;
    }
}
