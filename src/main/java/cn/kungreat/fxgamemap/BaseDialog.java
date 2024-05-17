package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.TreeArea;
import javafx.geometry.Insets;
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

    public static final TextField TEXT_MAP = BaseDialog.getTextField("请输入名称");

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
    //对象图片的批量修改
    public static final ButtonType BATCH_IMAGE_APPLY = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
    public static final ButtonType BATCH_IMAGE_CANCEL = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    public static final ImageObject BATCH_IMAGE_OBJECT = new ImageObject();
    public static final VBox BATCH_IMAGE_VBOX = new VBox(10);

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

    public static VBox getMapRectangular() {
        VBox vb = new VBox();
        vb.getChildren().addAll(TEXT_MAP);
        return vb;
    }

    public static VBox getAreaRectangular() {
        VBox vb = new VBox();
        vb.getChildren().addAll(TEXT_AREA, TEXT_AREAX, TEXT_AREAY, TEXT_AREA_WIDTH, TEXT_AREA_HEIGHT);
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

    public static Dialog<Boolean> getBatchChangeImageObjectDialog() {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("批量修改");
        dialog.setHeaderText("请选择名称");
        dialog.setContentText("是否接受修改");
        dialog.setResizable(false);
        HBox hBox = new HBox(10);
        ScrollPane scrollLeft = new ScrollPane();
        scrollLeft.setContent(BATCH_IMAGE_VBOX);
        scrollLeft.setPrefHeight(300);
        BATCH_IMAGE_VBOX.setPadding(new Insets(10, 10, 10, 10));
        scrollLeft.prefViewportWidthProperty().bind(BATCH_IMAGE_VBOX.widthProperty());
        BATCH_IMAGE_OBJECT.setTitle("公共的对象属性");
        BATCH_IMAGE_OBJECT.setId("BATCH_IMAGE_OBJECT");
        BATCH_IMAGE_OBJECT.initTitledPane();
        BATCH_IMAGE_OBJECT.getTitledPane().setExpanded(true);
        hBox.getChildren().addAll(scrollLeft, BATCH_IMAGE_OBJECT.getTitledPane());
        dialog.setGraphic(hBox);
        dialog.getDialogPane().getButtonTypes().addAll(BATCH_IMAGE_APPLY, BATCH_IMAGE_CANCEL);
        dialog.setResult(false);
        return dialog;
    }
}
