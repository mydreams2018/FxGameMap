package cn.kungreat.fxgamemap.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TreeArea {
    private String title;
    private String id;
    /*
    * xNumber x坐标有几个地图
    * yNumber y坐标有几个地图
    * */
    private Integer xNumber;
    private Integer yNumber;
    private List<TreeGameMap> childrenMap;
    private String imageDirectory;
}
