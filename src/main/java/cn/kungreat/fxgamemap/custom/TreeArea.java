package cn.kungreat.fxgamemap.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class TreeArea {
    private String title;
    private String id;
    private List<TreeGameMap> childrenMap;
}
