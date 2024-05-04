package cn.kungreat.fxgamemap.custom;

import cn.kungreat.fxgamemap.ImageObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
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

    @JsonIgnore
    private Canvas canvas;
    @JsonIgnore
    private GraphicsContext gc;
    private String backgroundImagePath;
    private List<ImageObject> imageObjectList;

    public TreeGameMap(String id, String title, Integer width, Integer height, String backgroundImagePath) {
        this.id = id;
        this.title = title;
        this.width = width;
        this.height = height;
        this.backgroundImagePath = backgroundImagePath;
    }

    public void initCanvas() {
        if (canvas == null) {
            canvas = new Canvas(width, height);
            gc = canvas.getGraphicsContext2D();
            gc.setStroke(Color.GREENYELLOW);
            gc.setLineWidth(3);
            gc.strokePolyline(new double[]{0, width, width, 0, 0}, new double[]{0, 0, height, height, 0}, 5);
            //TODO 根据相关信息恢复画板 先做保存信息
        }
    }

}
