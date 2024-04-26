module cn.kungreat.fxgamemap {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;

    opens cn.kungreat.fxgamemap to javafx.fxml;
    exports cn.kungreat.fxgamemap;
}