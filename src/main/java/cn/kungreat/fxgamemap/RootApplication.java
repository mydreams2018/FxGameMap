package cn.kungreat.fxgamemap;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class RootApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RootApplication.class.getResource("FxGameMap.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(RootApplication.class.getResource("FxGameMap.css").toExternalForm());
        stage.setTitle("FxGameMap");
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.getIcons().add(new Image(RootApplication.class.getResourceAsStream("favicon.ico")));
        stage.setWidth(1366);
        stage.setHeight(768);
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setMaxWidth(bounds.getWidth());
        stage.setMaxHeight(bounds.getHeight());
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}