package cn.kungreat.fxgamemap;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class MapMovePointLockUtils {

    /*
     * 保存区域配置文件 此配置表示xy坐标是否可移动
     * base_point_lock.json 默认读取的mir地图的占位数据
     * */
    private static final boolean[][] basePointLocks = new boolean[300][300];
    private static final String FILE_NAME = "IceValley\\";

    public static void main(String[] args) throws Exception {
        File readBackLimit = new File("F:\\mir-map-history\\" + FILE_NAME + "backLimit\\point.txt");
        File readFrontMask = new File("F:\\mir-map-history\\" + FILE_NAME + "frontMask\\point.txt");
        File outPointLocks = new File("F:\\mir\\mirBrother\\" + FILE_NAME + "base_point_lock.json");
        if (!outPointLocks.exists()) {
            outPointLocks.createNewFile();
        }
        readFileTxt(readBackLimit);
        readFileTxt(readFrontMask);
        Files.write(outPointLocks.toPath(), RootApplication.MAP_JSON.writeValueAsString(basePointLocks).getBytes(),
                StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println(FILE_NAME + " down");
    }

    public static void readFileTxt(File fileLimit) throws Exception {
        BufferedReader openBackLimit = new BufferedReader(new InputStreamReader(new FileInputStream(fileLimit)));
        String backLine = openBackLimit.readLine();
        if (backLine != null && !backLine.isEmpty()) {
            String[] split = backLine.split(",");
            for (String pointXY : split) {
                int[] realPoint = explainDataXY(pointXY);
                if (realPoint != null) {
                    basePointLocks[realPoint[0]][realPoint[1]] = true;
                }
            }
        }
    }

    public static int[] explainDataXY(String pointXY) {
        if (!pointXY.contains("max")) {
            String[] split = pointXY.split("=")[0].split("_");
            return new int[]{Integer.parseInt(split[0]), Integer.parseInt(split[1])};
        }
        return null;
    }
}
