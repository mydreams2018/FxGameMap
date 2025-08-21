package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.TreeArea;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;

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
    public static final TextField TEXT_AREA_WIDTH = BaseDialog.getTextField("请输入宽度");
    public static final TextField TEXT_AREA_HEIGHT = BaseDialog.getTextField("请输入高度");
    public static final TextField TEXT_AREA_BG_AUDIO = BaseDialog.getTextField("请输入背景音乐");
    public static final TextField TEXT_AREA_ROLE_STARTX = BaseDialog.getTextField("角色初始坐标X");
    public static final TextField TEXT_AREA_ROLE_STARTY = BaseDialog.getTextField("角色初始坐标Y");

    //SegmentResourceImagesDialog
    public static final TextField SRID_NAME = BaseDialog.getTextField("请输入资源名称");
    public static final TextField SRID_PATH = BaseDialog.getTextField("请选择资源文件");
    public static final Button SRID_BTN_CHOOSE = new Button("点击选择文件");
    public static final TextField SRID_WIDTH = BaseDialog.getTextField("请输入分割宽度");
    public static final TextField SRID_HEIGHT = BaseDialog.getTextField("请输入分割高度");
    public static final TextField SRID_MARGIN = BaseDialog.getTextField("请输入外边距");
    public static final TextField SRID_PADDING = BaseDialog.getTextField("请输入内边距");
    public static final ButtonType APPLY_SRID = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
    public static final ButtonType CANCEL_SRID = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    //图片对象
    public static final TextField IMAGE_OBJECT_NAME = BaseDialog.getTextField("请输入图片对象名称");
    public static final ButtonType APPLY_IMAGE_OBJECT = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
    public static final ButtonType CANCEL_IMAGE_OBJECT = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    //区域的图片连接
    public static final ButtonType AREA_LINK_APPLY = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
    public static final ButtonType AREA_LINK_CANCEL = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    //动画资源
    public static final TextField RESOURCE_ANIMATION_NAME = BaseDialog.getTextField("请输入动画名称");
    public static final TextField RESOURCE_ANIMATION_PATH = BaseDialog.getTextField("动画目录");
    public static final ButtonType RESOURCE_ANIMATION_APPLY = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
    public static final ButtonType RESOURCE_ANIMATION_CANCEL = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

    static {
        SRID_BTN_CHOOSE.setOnAction(event -> {
            File selectedFile = ResourceTab.FILE_CHOOSER.showOpenDialog(RootApplication.mainStage);
            if (selectedFile != null) {
                SRID_PATH.setText(selectedFile.toString());
            }
        });
    }

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

    public static VBox getAreaRectangular() {
        VBox vb = new VBox();
        vb.getChildren().addAll(TEXT_AREA, TEXT_AREAX, TEXT_AREAY, TEXT_AREA_WIDTH, TEXT_AREA_HEIGHT, TEXT_AREA_BG_AUDIO, TEXT_AREA_ROLE_STARTX, TEXT_AREA_ROLE_STARTY);
        return vb;
    }

    /*
     * 打开单个大的资源图片 进行分割
     * */
    public static Dialog<String> getSegmentResourceImagesDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("分割图片");
        dialog.setHeaderText("属性设置");
        dialog.setContentText("确定分割图片吗");
        dialog.setResizable(false);
        VBox vBox = new VBox(10);
        //只读文本输入框
        SRID_PATH.setDisable(true);
//        SRID_PATH.setEditable(false);不可编辑
        HBox hBoxInnerOne = new HBox(10);
        hBoxInnerOne.getChildren().addAll(SRID_PATH, SRID_BTN_CHOOSE);
        HBox hBoxInnerTwo = new HBox(10);
        hBoxInnerTwo.getChildren().addAll(SRID_WIDTH, SRID_HEIGHT);
        HBox hBoxInnerThree = new HBox(10);
        SRID_MARGIN.setText("0");
        SRID_PADDING.setText("0");
        hBoxInnerThree.getChildren().addAll(SRID_MARGIN, SRID_PADDING);
        vBox.getChildren().addAll(SRID_NAME, hBoxInnerOne, hBoxInnerTwo, hBoxInnerThree);
        dialog.setGraphic(vBox);
        dialog.getDialogPane().getButtonTypes().addAll(APPLY_SRID, CANCEL_SRID);
        return dialog;
    }

    public static Dialog<String> getChildrenPointDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("选择图像");
        dialog.setHeaderText("请选择图像");
        dialog.setContentText("是否接受图像名称");
        dialog.setResizable(false);
        ChoiceBox<String> choiceBox = new ChoiceBox<>(TreeArea.STRING_OBSERVABLE_LIST);
        dialog.setGraphic(choiceBox);
        dialog.getDialogPane().getButtonTypes().addAll(AREA_LINK_APPLY, AREA_LINK_CANCEL);
        return dialog;
    }

    public static Dialog<Boolean> getResourceAnimationDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("添加动画资源");
        dialog.setHeaderText("请填写信息");
        dialog.setContentText("是否添加");
        dialog.setResizable(false);
        VBox vBox = new VBox(10);
        HBox hBox = new HBox(10);
        Button chooseDirectory = new Button("选择动画存储目录");
        RESOURCE_ANIMATION_PATH.setDisable(true);
        chooseDirectory.setOnAction(event -> {
            File file = ResourceAnimation.DIRECTORY_CHOOSER.showDialog(RootApplication.mainStage);
            if (file != null && file.exists()) {
                RESOURCE_ANIMATION_PATH.setText(file.toString());
            }
        });
        hBox.getChildren().addAll(RESOURCE_ANIMATION_PATH, chooseDirectory);
        vBox.getChildren().addAll(RESOURCE_ANIMATION_NAME, hBox);
        dialog.setGraphic(vBox);
        dialog.getDialogPane().getButtonTypes().addAll(RESOURCE_ANIMATION_APPLY, RESOURCE_ANIMATION_CANCEL);
        //按钮事件
        Button buttonOk = (Button) dialog.getDialogPane().lookupButton(RESOURCE_ANIMATION_APPLY);
        Button buttonCancel = (Button) dialog.getDialogPane().lookupButton(RESOURCE_ANIMATION_CANCEL);
        buttonOk.setOnAction(event -> dialog.setResult(true));
        buttonCancel.setOnAction(event -> dialog.setResult(false));
        //显示事件
        dialog.setOnShowing(event -> {
            dialog.setResult(false);
            RESOURCE_ANIMATION_PATH.clear();
            RESOURCE_ANIMATION_NAME.clear();
        });
        return dialog;
    }
}
