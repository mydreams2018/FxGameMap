package cn.kungreat.fxgamemap;

import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;

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
    public static final TextField TEXT_MAP = BaseDialog.getTextField("请输入名称");

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
}
