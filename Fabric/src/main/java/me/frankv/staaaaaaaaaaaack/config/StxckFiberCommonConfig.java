package me.frankv.staaaaaaaaaaaack.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import lombok.Getter;
import me.frankv.staaaaaaaaaaaack.StxckCommon;

public class StxckFiberCommonConfig implements StxckCommonConfig {
    public static final String fileName = StxckCommon.MODID + "-common.json5";

    @Getter
    private final ConfigTree configTree;

    private final PropertyMirror<Double> maxMergeDistanceHorizontal = PropertyMirror.create(ConfigTypes.DOUBLE);
    private final PropertyMirror<Double> maxMergeDistanceVertical = PropertyMirror.create(ConfigTypes.DOUBLE);

    public StxckFiberCommonConfig() {
        var builder = ConfigTree.builder();
        builder.beginValue("maxMergeDistanceHorizontal", ConfigTypes.DOUBLE.withMaximum(10d).withMinimum(.5d), 1.25d)
                .withComment("""
                        
                        The maximum horizontal block distance over which dropped items attempt to merge with each other.
                        Range: 0.5 ~ 10.0
                        """)
                .finishValue(maxMergeDistanceHorizontal::mirror);

        builder.beginValue("maxMergeDistanceVertical", ConfigTypes.DOUBLE.withMaximum(10d).withMinimum(0d), 0d)
                .withComment("""
                            
                        The minimum vertical block distance over which dropped items attempt to merge with each other.
                        Range: 0.0 ~ 10.0
                        """)
                .finishValue(maxMergeDistanceVertical::mirror);

        configTree = builder.build();
    }

    @Override
    public double getMaxMergeDistanceHorizontal() {
        return maxMergeDistanceHorizontal.getValue();
    }

    @Override
    public double getMaxMergeDistanceVertical() {
        return maxMergeDistanceVertical.getValue();
    }
}
