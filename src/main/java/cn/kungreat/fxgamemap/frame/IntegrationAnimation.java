package cn.kungreat.fxgamemap.frame;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class IntegrationAnimation {

    //空闲动画
    private Timeline idleTimeline;
    //功击动画
    private Timeline attackTimeline;
    private VariableAnimation attackVariableAnimation;
    //跑步重击
    private Timeline highAttackTimeline;
    private VariableAnimation highAttackVariableAnimation;
    //攀登动画
    private Timeline climbTimeline;
    //挂掉动画
    private Timeline deathTimeline;
    private VariableAnimation deathVariableAnimation;
    //受伤动画
    private Timeline hurtTimeline;
    private VariableAnimation hurtVariableAnimation;
    //跳动画
    private Timeline jumpTimeline;
    private VariableAnimation jumpVariableAnimation;
    //跑动画
    private Timeline runTimeline;
    //走动画
    private Timeline walkTimeline;
    private VariableAnimation walkVariableAnimation;
    //魔法移动
    private Timeline magicMoveTimeline;
    private VariableAnimation magicMoveVariableAnimation;
    //魔法销毁
    private Timeline magicDestructionTimeline;
    private VariableAnimation magicDestructionVariableAnimation;
    //当前播放的动画类型标识
    private AnimationType animationType;

    private final ThreadLocal<OperationHistory> operationHistoryThreadLocal = new ThreadLocal<>();
    private DurationControl moveDurationControl;
    private DurationControl jumpDurationControl;
    private DurationControl attackDurationControl;
    private DurationControl highAttackDurationControl;

    /* 空闲动画
     * imageView 动画应用的图像
     * images 动画帧集合
     * durationMillis 动画的间隔时间
     * delayMillis 动画启动的延迟时间
     * */
    public void addIdleTimeline(ImageView imageView, List<Image> images, int durationMillis, int delayMillis) {
        if (this.idleTimeline == null) {
            this.idleTimeline = new Timeline();
            this.idleTimeline.setCycleCount(Animation.INDEFINITE);
            this.idleTimeline.setAutoReverse(false);
        }
        this.idleTimeline.getKeyFrames().clear();
        imageView.setImage(images.getFirst());
        for (int i = 0; i < images.size(); i++) {
            KeyFrame keyFrame = new KeyFrame(Duration.millis(i * durationMillis), String.valueOf(i), event -> {
                KeyFrame source = (KeyFrame) event.getSource();
                int indexImage = Integer.parseInt(source.getName());
                imageView.setImage(images.get(indexImage));
            });
            this.idleTimeline.getKeyFrames().add(keyFrame);
        }
        this.idleTimeline.setDelay(Duration.millis(delayMillis));
    }

    /* 步行动画
     * imageView 动画应用的图像
     * imagesRight 多个动画帧集合 右操作方向
     * imagesLeft 多个动画帧集合 左操作方向
     * durationMillis 动画的间隔时间
     * delayMillis 动画启动的延迟时间
     * moveDistance 每一步走的像素
     * operationHistoryDistance 左 <--> 右 转换时的一个间隔像素
     * */
    public void addWalkTimeline(ImageView imageView, List<Image> imagesRight, int durationMillis,
                                int delayMillis, int moveDistance, int operationHistoryDistance) {
        if (this.walkTimeline == null) {
            this.walkTimeline = new Timeline();
            this.walkTimeline.setCycleCount(1);
            this.walkTimeline.setAutoReverse(false);
            this.walkVariableAnimation = new VariableAnimation(imageView, imagesRight, durationMillis, moveDistance,
                    this.operationHistoryThreadLocal, this.walkTimeline, operationHistoryDistance);
            this.walkTimeline.setDelay(Duration.millis(delayMillis));
        } else if (imagesRight != null) {
            this.walkVariableAnimation.setImagesRight(imagesRight);
        }
    }

    /* 功击动画
     * imageView 动画应用的图像
     * imagesRight 多个动画帧集合 右操作方向
     * imagesLeft 多个动画帧集合 左操作方向
     * durationMillis 动画的间隔时间
     * delayMillis 动画启动的延迟时间
     * moveDistance 每一步走的像素
     * */
    public void addAttackTimeline(ImageView imageView, List<Image> imagesRight, int durationMillis,
                                  int delayMillis, int moveDistance) {
        if (this.attackTimeline == null) {
            this.attackTimeline = new Timeline();
            this.attackTimeline.setCycleCount(1);
            this.attackTimeline.setAutoReverse(false);
            this.attackVariableAnimation = new VariableAnimation(imageView, imagesRight, durationMillis, moveDistance,
                    this.operationHistoryThreadLocal, this.attackTimeline, 0);
            this.attackTimeline.setDelay(Duration.millis(delayMillis));
        } else if (imagesRight != null) {
            this.attackVariableAnimation.setImagesRight(imagesRight);
        }
    }

    /* 跑步功击动画
     * imageView 动画应用的图像
     * imagesRight 多个动画帧集合 右操作方向
     * imagesLeft 多个动画帧集合 左操作方向
     * durationMillis 动画的间隔时间
     * delayMillis 动画启动的延迟时间
     * moveDistance 每一步走的像素
     * */
    public void addHighAttackTimeline(ImageView imageView, List<Image> imagesRight, int durationMillis,
                                      int delayMillis, int moveDistance) {
        if (this.highAttackTimeline == null) {
            this.highAttackTimeline = new Timeline();
            this.highAttackTimeline.setCycleCount(1);
            this.highAttackTimeline.setAutoReverse(false);
            this.highAttackVariableAnimation = new VariableAnimation(imageView, imagesRight, durationMillis, moveDistance,
                    this.operationHistoryThreadLocal, this.highAttackTimeline, 0);
            this.highAttackTimeline.setDelay(Duration.millis(delayMillis));
        }
    }

    public void addHurtTimeline(ImageView imageView, List<Image> imagesRight, int durationMillis,
                                int delayMillis, int moveDistance) {
        if (this.hurtTimeline == null) {
            this.hurtTimeline = new Timeline();
            this.hurtTimeline.setCycleCount(1);
            this.hurtTimeline.setAutoReverse(false);
            this.hurtVariableAnimation = new VariableAnimation(imageView, imagesRight, durationMillis, moveDistance,
                    this.operationHistoryThreadLocal, this.hurtTimeline, 0);
            this.hurtTimeline.setDelay(Duration.millis(delayMillis));
        }
    }

    public void addDeathTimeline(ImageView imageView, List<Image> imagesRight, int durationMillis, int delayMillis) {
        if (this.deathTimeline == null) {
            this.deathTimeline = new Timeline();
            this.deathTimeline.setCycleCount(1);
            this.deathTimeline.setAutoReverse(false);
            this.deathVariableAnimation = new VariableAnimation(imageView, imagesRight, durationMillis, 0,
                    this.operationHistoryThreadLocal, this.deathTimeline, 0);
            this.deathTimeline.setDelay(Duration.millis(delayMillis));
        }
    }

    public void addMagicMoveTimeline(ImageView imageView, List<Image> imagesRight, int durationMillis, int delayMillis) {
        if (this.magicMoveTimeline == null) {
            this.magicMoveTimeline = new Timeline();
            this.magicMoveTimeline.setCycleCount(1);
            this.magicMoveTimeline.setAutoReverse(false);
            this.magicMoveVariableAnimation = new VariableAnimation(imageView, imagesRight, durationMillis, 0,
                    this.operationHistoryThreadLocal, this.magicMoveTimeline, 0);
            this.magicMoveTimeline.setDelay(Duration.millis(delayMillis));
        }
    }

    public void addMagicDestructionTimeline(ImageView imageView, List<Image> imagesRight, int durationMillis, int delayMillis) {
        if (this.magicDestructionTimeline == null) {
            this.magicDestructionTimeline = new Timeline();
            this.magicDestructionTimeline.setCycleCount(1);
            this.magicDestructionTimeline.setAutoReverse(false);
            this.magicDestructionVariableAnimation = new VariableAnimation(imageView, imagesRight, durationMillis, 0,
                    this.operationHistoryThreadLocal, this.magicDestructionTimeline, 0);
            this.magicDestructionTimeline.setDelay(Duration.millis(delayMillis));
        }
    }

    /* 功击动画
     * imageView 动画应用的图像
     * imagesRight 多个动画帧集合 右操作方向
     * imagesLeft 多个动画帧集合 左操作方向
     * durationMillis 动画的间隔时间
     * delayMillis 动画启动的延迟时间
     * moveDistance 每一步走的像素
     * */
    public void addJumpTimeline(ImageView imageView, List<Image> imagesRight, int durationMillis,
                                int delayMillis, int moveDistance) {
        if (this.jumpTimeline == null) {
            this.jumpTimeline = new Timeline();
            this.jumpTimeline.setCycleCount(1);
            this.jumpTimeline.setAutoReverse(false);
            this.jumpVariableAnimation = new VariableAnimation(imageView, imagesRight, durationMillis, moveDistance,
                    this.operationHistoryThreadLocal, this.jumpTimeline, 0);
            this.jumpTimeline.setDelay(Duration.millis(delayMillis));
        } else if (imagesRight != null) {
            this.jumpVariableAnimation.setImagesRight(imagesRight);
        }
    }

    /*
     * 保存数据  关闭旧的动画 开始新的动画
     * 游戏引擎处需要额外的处理逻辑
     * */
    public void startAnimation(AnimationType animationType) {
        if (this.animationType != null) {
            switch (this.animationType) {
                case IDLE -> {
                    if (this.idleTimeline != null) {
                        this.idleTimeline.stop();
                    }
                }
                case ATTACK -> {
                    if (this.attackTimeline != null) {
                        this.attackTimeline.stop();
                    }
                }
                case HIGH_ATTACK -> {
                    if (this.highAttackTimeline != null) {
                        this.highAttackTimeline.stop();
                    }
                }
                case RUN -> {
                    if (this.runTimeline != null) {
                        this.runTimeline.stop();
                    }
                }
                case HURT -> {
                    if (this.hurtTimeline != null) {
                        this.hurtTimeline.stop();
                    }
                }
                case JUMP -> {
                    if (this.jumpTimeline != null) {
                        this.jumpTimeline.stop();
                    }
                }
                case WALK -> {
                    if (this.walkTimeline != null) {
                        this.walkTimeline.stop();
                    }
                }
                case CLIMB -> {
                    if (this.climbTimeline != null) {
                        this.climbTimeline.stop();
                    }
                }
                case DEATH -> {
                    if (this.deathTimeline != null) {
                        this.deathTimeline.stop();
                    }
                }
                case MAGIC_MOVE -> {
                    if (this.magicMoveTimeline != null) {
                        this.magicMoveTimeline.stop();
                    }
                }
                case MAGIC_D -> {
                    if (this.magicDestructionTimeline != null) {
                        this.magicDestructionTimeline.stop();
                    }
                }
            }
        }
        this.animationType = animationType;
        switch (this.animationType) {
            case IDLE -> {
                if (this.idleTimeline != null) {
                    this.idleTimeline.playFromStart();
                }
            }
            case ATTACK -> {
                if (this.attackTimeline != null) {
                    this.attackVariableAnimation.startBaseVariableAnimation();
                    this.attackTimeline.playFromStart();
                }
            }
            case HIGH_ATTACK -> {
                if (this.highAttackTimeline != null) {
                    this.highAttackVariableAnimation.startBaseVariableAnimation();
                    this.highAttackTimeline.playFromStart();
                }
            }
            case RUN -> {
                if (this.runTimeline != null) {
                    this.runTimeline.playFromStart();
                }
            }
            case HURT -> {
                if (this.hurtTimeline != null) {
                    this.hurtVariableAnimation.startBaseVariableAnimation();
                    this.hurtTimeline.playFromStart();
                }
            }
            case JUMP -> {
                if (this.jumpTimeline != null) {
                    //只显示动画的过程,别的处理细节由引擎完成,这里只做演示
                    this.jumpVariableAnimation.startBaseVariableAnimation();
                    this.jumpTimeline.playFromStart();
                }
            }
            case WALK -> {
                if (this.walkTimeline != null) {
                    this.walkVariableAnimation.startBaseVariableAnimation();
                    this.walkTimeline.playFromStart();
                }
            }
            case CLIMB -> {
                if (this.climbTimeline != null) {
                    this.climbTimeline.playFromStart();
                }
            }
            case DEATH -> {
                if (this.deathTimeline != null) {
                    this.deathVariableAnimation.startBaseVariableAnimation();
                    this.deathTimeline.playFromStart();
                }
            }
            case MAGIC_MOVE -> {
                if (this.magicMoveTimeline != null) {
                    this.magicMoveVariableAnimation.startBaseVariableAnimation();
                    this.magicMoveTimeline.playFromStart();
                }
            }
            case MAGIC_D -> {
                if (this.magicDestructionTimeline != null) {
                    this.magicDestructionVariableAnimation.startBaseVariableAnimation();
                    this.magicDestructionTimeline.playFromStart();
                }
            }
        }
    }

    @Setter
    private static final class VariableAnimation {

        private final ImageView imageView;
        private List<Image> imagesRight;
        private final int durationMillis;
        private final int moveDistance;
        private final ThreadLocal<OperationHistory> operationHistoryThreadLocal;
        private final Timeline animationTimeline;
        private final int operationHistoryDistance;
        //操作历史记录
        private OperationHistory operationHistoryCache;

        private VariableAnimation(ImageView imageView, List<Image> imagesRight,
                                  int durationMillis,
                                  int moveDistance,
                                  ThreadLocal<OperationHistory> operationHistoryThreadLocal,
                                  Timeline animationTimeline, int operationHistoryDistance) {
            this.imageView = imageView;
            this.imagesRight = imagesRight;
            this.durationMillis = durationMillis;
            this.moveDistance = moveDistance;
            this.operationHistoryThreadLocal = operationHistoryThreadLocal;
            this.animationTimeline = animationTimeline;
            this.operationHistoryDistance = operationHistoryDistance;
        }

 /*       public void startMoveVariableAnimation() {
            this.animationTimeline.getKeyFrames().clear();
            for (int i = 0; i < this.imagesRight.size(); i++) {
                KeyValue keyValue = new KeyValue(imageView.layoutXProperty(), imageView.layoutXProperty().get() + (i * moveDistance));
                KeyFrame keyFrame = new KeyFrame(Duration.millis(i * durationMillis), String.valueOf(i), event -> {
                    KeyFrame source = (KeyFrame) event.getSource();
                    int indexImage = Integer.parseInt(source.getName());
                    imageView.setImage(imagesRight.get(indexImage));
                }, keyValue);
                this.animationTimeline.getKeyFrames().add(keyFrame);
            }
        }*/

        public void startBaseVariableAnimation() {
            this.animationTimeline.getKeyFrames().clear();
            for (int i = 1; i <= this.imagesRight.size(); i++) {
                KeyFrame keyFrame = new KeyFrame(Duration.millis(i * durationMillis), String.valueOf(i), event -> {
                    KeyFrame source = (KeyFrame) event.getSource();
                    int indexImage = Integer.parseInt(source.getName());
                    imageView.setImage(imagesRight.get(indexImage - 1));
                });
                this.animationTimeline.getKeyFrames().add(keyFrame);
            }
        }
    }

    public static enum AnimationType {
        IDLE, ATTACK, HIGH_ATTACK, CLIMB, DEATH, HURT, JUMP, RUN, WALK,MAGIC_MOVE,MAGIC_D;
    }

    public static enum OperationHistory {
        TOP, BOTTOM, LEFT, RIGHT;
    }

}
