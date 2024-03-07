package me.frankv.staaaaaaaaaaaack.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.EnumConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import lombok.Getter;
import me.frankv.staaaaaaaaaaaack.Staaaaaaaaaaaack;


public class StxckFiberClientConfig implements StxckClientConfig {
    public static final String fileName = Staaaaaaaaaaaack.MODID + "-client.json5";

    @Getter
    private final ConfigTree configTree;

    private final EnumConfigType<OverlayDisplayMode> OVERLAY_DISPLAY_MODE_TYPE = ConfigTypes.makeEnum(OverlayDisplayMode.class);

    private final PropertyMirror<Integer> minItemCountRenderDistance = PropertyMirror.create(ConfigTypes.INTEGER);
    private final PropertyMirror<Double> overlaySizeMultiplier = PropertyMirror.create(ConfigTypes.DOUBLE);
    private final PropertyMirror<Boolean> alwaysShowItemCount = PropertyMirror.create(ConfigTypes.BOOLEAN);
    private final PropertyMirror<OverlayDisplayMode> overlayDisplayMode = PropertyMirror.create(OVERLAY_DISPLAY_MODE_TYPE);

    public StxckFiberClientConfig() {
        var builder = ConfigTree.builder();
        builder.beginValue("minItemCountRenderDistance", ConfigTypes.INTEGER.withMaximum(128).withMinimum(0), 8)
                .withComment("""
                                                
                        The maximum distance between you and the drops to display its count.
                        Range: 0 ~ 128, Default: 8
                        """)
                .finishValue(minItemCountRenderDistance::mirror);

        builder.beginValue("overlaySizeMultiplier", ConfigTypes.DOUBLE.withMaximum(2d).withMinimum(.1d), 0.8d)
                .withComment("""
                                                
                        Range: 0.1 ~ 2.0, Default: 0.8
                        """)
                .finishValue(overlaySizeMultiplier::mirror);

        builder.beginValue("alwaysShowItemCount", ConfigTypes.BOOLEAN, false)
                .withComment("""
                                                
                        Show item count overlay even if the item count is lower than default maximum stack size.
                        Default: false
                        """)
                .finishValue(alwaysShowItemCount::mirror);

        builder.beginValue("overlayDisplayMode", OVERLAY_DISPLAY_MODE_TYPE, OverlayDisplayMode.STACK_COUNT)
                .withComment("""
                                                
                        Options: STACK_COUNT, ITEM_COUNT
                        Default: STACK_COUNT
                        """)
                .finishValue(overlayDisplayMode::mirror);

        configTree = builder.build();
    }

    @Override
    public int getMinItemCountRenderDistance() {
        return minItemCountRenderDistance.getValue();
    }

    @Override
    public double getOverlaySizeMultiplier() {
        return overlaySizeMultiplier.getValue();
    }

    @Override
    public boolean isAlwaysShowItemCount() {
        return alwaysShowItemCount.getValue();
    }

    @Override
    public OverlayDisplayMode getOverlayDisplayMode() {
        return overlayDisplayMode.getValue();
    }
}
