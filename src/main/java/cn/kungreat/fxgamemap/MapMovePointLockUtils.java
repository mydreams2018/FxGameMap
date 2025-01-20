package cn.kungreat.fxgamemap;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class MapMovePointLockUtils {

    /*
     * 保存区域配置文件 此配置表示xy坐标是否可移动
     * base_point_lock.json 默认读取的mir地图的占位数据
     * open_point_lock.json 自已手动修改地图的占位数据
     * */
    private static final boolean[][] basePointLocks = new boolean[800][800];

    public static void main(String[] args) throws Exception {
        File readBackLimit = new File("F:\\mir-map-export\\backLimit");
        File readFrontMask = new File("F:\\mir-map-export\\frontMask");
        File outPointLocks = new File("F:\\mir2-sources\\mirBrother\\PeachGarden\\base_point_lock.json");
        for (File file : readBackLimit.listFiles()) {
            String[] split = file.getName().split("\\.")[0].split("_");
            basePointLocks[Integer.parseInt(split[0])][Integer.parseInt(split[1])] = true;
        }
        for (File file : readFrontMask.listFiles()) {
            String[] split = file.getName().split("\\.")[0].split("_");
            basePointLocks[Integer.parseInt(split[0])][Integer.parseInt(split[1])] = true;
        }
        Files.write(outPointLocks.toPath(), RootApplication.MAP_JSON.writeValueAsString(basePointLocks).getBytes(),
                StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("down");
    }
}
