package cn.kungreat.fxgamemap.custom;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TreeArea {
    private String title;
    private Integer id;
    private List<TreeGameMap> childrenMap;
}
