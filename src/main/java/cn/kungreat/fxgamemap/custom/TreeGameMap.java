package cn.kungreat.fxgamemap.custom;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TreeGameMap {
    private String title;
    private Integer id;
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
}
