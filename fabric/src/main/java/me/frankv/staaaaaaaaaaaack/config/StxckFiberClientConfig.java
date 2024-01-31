package me.frankv.staaaaaaaaaaaack.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import lombok.Getter;
import me.frankv.staaaaaaaaaaaack.Staaaaaaaaaaaack;


public class StxckFiberClientConfig implements StxckClientConfig {
    public static final String fileName = Staaaaaaaaaaaack.MODID + "-client.json5";

    @Getter
    private final ConfigTree configTree;

    private final PropertyMirror<Integer> minItemCountRenderDistance = PropertyMirror.create(ConfigTypes.INTEGER);
    private final PropertyMirror<Double> overlaySizeMultiplier = PropertyMirror.create(ConfigTypes.DOUBLE);
    private final PropertyMirror<Boolean> alwaysShowItemCount = PropertyMirror.create(ConfigTypes.BOOLEAN);

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
}
