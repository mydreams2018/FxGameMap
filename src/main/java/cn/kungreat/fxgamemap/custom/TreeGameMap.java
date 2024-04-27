package cn.kungreat.fxgamemap.custom;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TreeGameMap {
    private String title;
    private String id;
    private Integer width;
    private Integer height;
    private TreeGameMap left;
    private TreeGameMap top;
    private TreeGameMap right;
    private TreeGameMap bottom;
    private TreeGameMap leftTop;
    private TreeGameMap leftBottom;
    private TreeGameMap rightTop;
    private TreeGameMap rightBottom;

    public TreeGameMap(String id, String title, Integer width, Integer height) {
        this.id = id;
        this.title = title;
        this.width = width;
        this.height = height;
    }

}
