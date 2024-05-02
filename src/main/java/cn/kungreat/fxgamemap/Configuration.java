package cn.kungreat.fxgamemap;

import cn.kungreat.fxgamemap.custom.Resources;
import cn.kungreat.fxgamemap.custom.TreeArea;
import cn.kungreat.fxgamemap.custom.TreeGameMap;
import cn.kungreat.fxgamemap.custom.TreeWorld;
import cn.kungreat.fxgamemap.util.LogService;
import cn.kungreat.fxgamemap.util.PropertyListener;
import javafx.scene.control.TabPane;
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
    public static final String MAIN_PROPERTIES = "fxgamemap.properties";
    public static String currentProject;
    public static List<String> historyProject = new ArrayList<>();
    public static String logDirectory;
    public static String errorPrint;

    static {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(MAIN_PROPERTIES);
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        in.lines().forEach(new Consumer<>() {
            @Override
            public void accept(String s) {
                if (!s.isBlank() && s.contains("=")) {
                    String[] split = s.split("=");
                    if (split.length == 2 && split[0].equals("currentProject")) {
                        changeCurrentProject(split[1]);
                    } else if (split.length == 2 && split[0].equals("historyProject")) {
                        split[1] = split[1].substring(1, split[1].length() - 1);
                        for (String history : split[1].split(",")) {
                            addHistoryProject(history);
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
            PropertyListener.setMainMenuHistory(project);
        }
    }

    public static void changeCurrentProject(String cp) {
        currentProject = cp;
    }

    public static void writerProperties() throws Exception {
        URI writerStream = ClassLoader.getSystemResource(MAIN_PROPERTIES).toURI();
        Path path = Paths.get(writerStream);
        String stringBuilder = "currentProject=" + currentProject + System.lineSeparator() +
                "historyProject=" + historyProject + System.lineSeparator() +
                "logDirectory=" + logDirectory + System.lineSeparator() +
                "errorPrint=" + errorPrint + System.lineSeparator();
        Files.write(path, stringBuilder.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /*
     * 加载当前的项目文件资源
     * */
    public static void loadTreeMenu() {
        if (currentProject != null) {
            try (InputStream inputStream = new URI(currentProject).toURL().openStream()) {
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                RootController controller = RootApplication.mainFXMLLoader.getController();
                TreeItem<Object> root = controller.getTreeView().getRoot();
                root.getChildren().clear();
                TabPane tabPaneRight = controller.getTabPaneRight();
                tabPaneRight.getTabs().clear();
                in.lines().forEach(s -> {
                    try {
                        if (s.contains("resourceTabList")) {
                            loadResources(tabPaneRight, s);
                        } else {
                            loadTreeWorld(root, s);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                LogService.printLog(LogService.LogLevel.ERROR, Configuration.class, "加载当前的树项目文件", e);
            }
        }
    }

    private static void loadTreeWorld(TreeItem<Object> root, String s) throws Exception {
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
    }

    private static void loadResources(TabPane tabPaneRight, String s) throws Exception {
        Resources resources = RootApplication.MAP_JSON.readValue(s, Resources.class);
        RootApplication.RESOURCES.setResourceTabList(resources.getResourceTabList());
        RootApplication.RESOURCES.setSegmentResourceTabList(resources.getSegmentResourceTabList());
        if (!resources.getResourceTabList().isEmpty()) {
            resources.getResourceTabList().forEach(resourceTab -> {
                resourceTab.initTab();
                tabPaneRight.getTabs().add(resourceTab.getTab());
            });
        }
        if (!resources.getSegmentResourceTabList().isEmpty()) {
            resources.getSegmentResourceTabList().forEach(segmentResourceTab -> {
                segmentResourceTab.initTab();
                tabPaneRight.getTabs().add(segmentResourceTab.getTab());
            });
        }
    }
}
