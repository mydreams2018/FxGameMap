package cn.kungreat.fxgamemap.custom;

import cn.kungreat.fxgamemap.ResourceAnimation;
import cn.kungreat.fxgamemap.ResourceTab;
import cn.kungreat.fxgamemap.SegmentResourceTab;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Resources {
    private List<ResourceTab> resourceTabList = new ArrayList<>();
    private List<SegmentResourceTab> segmentResourceTabList = new ArrayList<>();
    private List<ResourceAnimation> resourceAnimations = new ArrayList<>();
}
