package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.TreeWorld;
import cn.kungreat.fxgamemap.util.LogService;
import cn.kungreat.fxgamemap.util.PatternUtils;
import cn.kungreat.fxgamemap.util.WorkThread;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;


public class MainMenuBar extends MenuBar {
    private final FileChooser fileSave = new FileChooser();
    private final FileChooser fileRead = new FileChooser();

    {
        Menu file = new Menu("文件");
        MenuItem open = new MenuItem("打开", new FontIcon("far-folder-open"));
        open.setOnAction(event -> {
            File readFile = fileRead.showOpenDialog(RootApplication.mainStage);
            if (readFile != null && readFile.toString().endsWith(".json")) {
                Configuration.currentProject = readFile.toURI().toString();
                Configuration.addHistoryProject(readFile.toURI().toString());
                Configuration.loadTreeMenu();
            }
        });
        MenuItem save = new MenuItem("保存", new FontIcon("far-save"));
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        save.setOnAction(event -> {
            if (Configuration.currentProject == null) {
                File saveFile = fileSave.showSaveDialog(RootApplication.mainStage);
                saveProjectFile(saveFile);
            } else {
                try {
                    URI resource = new URI(Configuration.currentProject);
                    File saveFile = new File(resource.getPath());
                    saveProjectFile(saveFile);
                } catch (URISyntaxException e) {
                    LogService.printLog(LogService.LogLevel.ERROR, getClass(), "保存数据事件", e);
                }
            }
        });

        Menu history = new Menu("历史记录", new FontIcon("fas-history"));
        if (Configuration.historyProject != null && !Configuration.historyProject.isEmpty()) {
            Configuration.historyProject.forEach(s -> {
                Matcher matcher = PatternUtils.findFileName.matcher(s);
                if (matcher.find()) {
                    MenuItem historyItem = new MenuItem(matcher.group().substring(1, matcher.group().length() - 5));
                    historyItem.setUserData(s);
                    historyItem.setOnAction(event -> {
                        MenuItem historyItemHandler = (MenuItem) event.getSource();
                        if (!Configuration.currentProject.equals(historyItemHandler.getUserData())) {
                            Configuration.currentProject = (String) historyItemHandler.getUserData();
                            Configuration.loadTreeMenu();
                        }
                    });
                    history.getItems().add(historyItem);
                }
            });
        }
        file.getItems().addAll(open, save, history);
        this.getMenus().add(file);
        this.getStyleClass().add("customBgColor");
        initFileSave();
        initFileRead();
    }

    public void initFileSave() {
        fileSave.setTitle("Save File");
        //默认的文件名称
        fileSave.setInitialFileName("badeDemo");
        fileSave.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Save File", "*.json"));
    }

    public void initFileRead() {
        fileRead.setTitle("Read File");
        fileRead.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Read File", "*.json"));
    }

    /*
     * Save File
     * */
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

    public void saveProjectFile(final File saveFile) {
        WorkThread.THREAD_POOL_EXECUTOR.execute(() -> {
            if (saveFile != null && saveFile.toString().endsWith(".json")) {
                try {
                    StringBuilder stringBuilder = getStringBuilder();
                    Files.write(saveFile.toPath(), stringBuilder.toString().getBytes(),
                            StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    Configuration.currentProject = saveFile.toURI().toString();
                    Configuration.addHistoryProject(saveFile.toURI().toString());
                    Configuration.writerProperties();
                } catch (Exception e) {
                    LogService.printLog(LogService.LogLevel.ERROR, getClass(), "写出文件数据出错", e);
                }
            }
        });
    }

}
