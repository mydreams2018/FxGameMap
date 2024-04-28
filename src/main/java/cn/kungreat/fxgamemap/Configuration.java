package cn.kungreat.fxgamemap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class Configuration {
    public static String currentProject;
    public static List<String> historyProject = new ArrayList<>();

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

}
