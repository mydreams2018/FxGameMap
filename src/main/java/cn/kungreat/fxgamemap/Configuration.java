package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.TreeArea;
import cn.kungreat.fxgamemap.custom.TreeGameMap;
import cn.kungreat.fxgamemap.custom.TreeWorld;
import cn.kungreat.fxgamemap.util.LogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.scene.control.TreeItem;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class Configuration {
    public static volatile String currentProject;
    public static List<String> historyProject = new ArrayList<>();
    public static String logDirectory;
    public static String errorPrint;

    static {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("fxgamemap.properties");
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        in.lines().forEach(new Consumer<>() {
            @Override
            public void accept(String s) {
                if (!s.isBlank() && s.contains("=")) {
                    String[] split = s.split("=");
                    if (split.length == 2 && split[0].equals("currentProject")) {
                        currentProject = split[1];
                    } else if (split.length == 2 && split[0].equals("historyProject")) {
                        split[1] = split[1].substring(1, split[1].length() - 1);
                        for (String history : split[1].split(",")) {
                            historyProject.add(history);
                        }
                    } else if (split.length == 2 && split[0].equals("logDirectory")) {
                        logDirectory = split[1];
                    } else if (split.length == 2 && split[0].equals("errorPrint")) {
                        errorPrint = split[1];
                    }
                }
            }
        });
    }

    public static void addHistoryProject(String project) {
        if (!historyProject.contains(project)) {
            historyProject.addFirst(project);
        }
    }

    public static void writerProperties() throws Exception {
        URI writerStream = ClassLoader.getSystemResource("fxgamemap.properties").toURI();
        Path path = Paths.get(writerStream);
        String stringBuilder = "currentProject=" + currentProject + System.lineSeparator() +
                "historyProject=" + historyProject + System.lineSeparator();
        Files.write(path, stringBuilder.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /*
     * 加载当前的项目文件
     * */
    public static void loadTreeMenu() {
        if (currentProject != null) {
            try (InputStream inputStream = new URI(currentProject).toURL().openStream()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                RootController controller = RootApplication.mainFXMLLoader.getController();
                TreeItem<Object> root = controller.getTreeView().getRoot();
                root.getChildren().clear();
                in.lines().forEach(s -> {
                    try {
                        TreeWorld treeWorld = RootApplication.MAP_JSON.readValue(s, TreeWorld.class);
                        TreeItem<Object> treeItem = new TreeItem<>(treeWorld);
                        treeItem.setGraphic(new FontIcon("fas-globe"));
                        root.getChildren().add(treeItem);
                        List<TreeArea> childrenArea = treeWorld.getChildrenArea();
                        if (childrenArea != null && !childrenArea.isEmpty()) {
                            for (TreeArea treeArea : childrenArea) {
                                TreeItem<Object> areaTreeItem = new TreeItem<>(treeArea);
                                areaTreeItem.setGraphic(new FontIcon("fas-chart-area"));
                                treeItem.getChildren().add(areaTreeItem);
                                List<TreeGameMap> childrenMap = treeArea.getChildrenMap();
                                if (childrenMap != null && !childrenMap.isEmpty()) {
                                    for (TreeGameMap treeGameMap : childrenMap) {
                                        TreeItem<Object> gameMapTreeItem = new TreeItem<>(treeGameMap);
                                        gameMapTreeItem.setGraphic(new FontIcon("fas-map"));
                                        areaTreeItem.getChildren().add(gameMapTreeItem);
                                    }
                                }
                            }
                        }
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                LogService.printLog(LogService.LogLevel.ERROR, Configuration.class, "加载当前的树项目文件", e);
            }
        }
    }

}
