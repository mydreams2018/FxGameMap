package cn.kungreat.fxgamemap.frame;

import cn.kungreat.fxgamemap.RootApplication;
import cn.kungreat.fxgamemap.RootController;
import cn.kungreat.fxgamemap.custom.TreeGameMap;
import cn.kungreat.fxgamemap.util.PropertyListener;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

/*
 * 帧率刷新器 UI线程回调
 * */
public class FrameTimer extends AnimationTimer {

    private static final int INTERVAL_TIME = 10;
    private long lastStartTime = System.currentTimeMillis();

    @Override
    public void handle(long now) {
        RootController controller = RootApplication.mainFXMLLoader.getController();
        if (changeOpacity() && !controller.getTopPaintingMode().isSelected()) {
            TreeGameMap.BackgroundImageData chooseCanvasImage = PropertyListener.getChooseCanvasImage();
            if (chooseCanvasImage != null) {
                TreeItem<Object> item = controller.getTreeView().getFocusModel().getFocusedItem();
                if (item != null && item.getValue() instanceof TreeGameMap treeGameMap) {
                    GraphicsContext graphicsContext = treeGameMap.getGraphicsContext();
                    if (graphicsContext != null) {
                        changeOpacity(chooseCanvasImage, treeGameMap);
                    }
                }
            }
        }
    }

    private boolean changeOpacity() {
        if (System.currentTimeMillis() > lastStartTime + INTERVAL_TIME) {
            lastStartTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    private void changeOpacity(TreeGameMap.BackgroundImageData backgroundImageData, TreeGameMap treeGameMap) {
        ImageView imageView = backgroundImageData.getImageView();
        backgroundImageData.setImage(imageView.snapshot(TreeGameMap.CANVAS_SNAPSHOT_PARAMETERS, null));
        treeGameMap.clearAndDraw();
    }
}
