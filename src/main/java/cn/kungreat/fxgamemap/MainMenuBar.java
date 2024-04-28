package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.TreeWorld;
import cn.kungreat.fxgamemap.util.PatternUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;


public class MainMenuBar extends MenuBar {
    private final FileChooser fileSave = new FileChooser();

    {
        Menu file = new Menu("文件");
        MenuItem open = new MenuItem("打开", new FontIcon("far-folder-open"));
        MenuItem save = new MenuItem("保存", new FontIcon("far-save"));
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        save.setOnAction(event -> {
            File saveFile = fileSave.showSaveDialog(RootApplication.mainStage);
            if (saveFile != null && saveFile.toString().endsWith(".json")) {
                try {
                    StringBuilder stringBuilder = getStringBuilder();
                    Files.write(saveFile.toPath(), stringBuilder.toString().getBytes(),
                            StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    Configuration.currentProject = saveFile.toURI().toString();
                    Configuration.addHistoryProject(saveFile.toURI().toString());
                    Configuration.writerProperties();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Menu history = new Menu("历史记录", new FontIcon("fas-history"));
        if (Configuration.historyProject != null && !Configuration.historyProject.isEmpty()) {
            Configuration.historyProject.forEach(s -> {
                Matcher matcher = PatternUtils.findFileName.matcher(s);
                if (matcher.find()) {
                    history.getItems().add(new MenuItem(matcher.group().substring(1, matcher.group().length() - 5)));
                }
            });
        }
        file.getItems().addAll(open, save, history);
        this.getMenus().add(file);
        this.getStyleClass().add("customBgColor");
        initFileSave();
    }

    private static StringBuilder getStringBuilder() throws JsonProcessingException {
        RootController controller = RootApplication.mainFXMLLoader.getController();
        ObservableList<TreeItem<Object>> children = controller.getTreeView().getRoot().getChildren();
        StringBuilder stringBuilder = new StringBuilder();
        for (TreeItem<Object> child : children) {
            TreeWorld treeWorld = (TreeWorld) child.getValue();
            stringBuilder.append(RootApplication.MAP_JSON.writeValueAsString(treeWorld));
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder;
    }

    public void initFileSave() {
        fileSave.setTitle("Save File");
        //默认的文件名称
        fileSave.setInitialFileName("badeDemo");
        fileSave.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Save Files", "*.json"));
    }

}
