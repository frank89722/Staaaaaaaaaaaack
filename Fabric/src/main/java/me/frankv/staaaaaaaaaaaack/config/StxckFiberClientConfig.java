package me.frankv.staaaaaaaaaaaack.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import lombok.Getter;
import me.frankv.staaaaaaaaaaaack.StxckCommon;


public class StxckFiberClientConfig implements StxckClientConfig {
    public static final String fileName = StxckCommon.MODID + "-client.json5";

    @Getter
    private final ConfigTree configTree;

    private final PropertyMirror<Integer> minItemCountRenderDistance = PropertyMirror.create(ConfigTypes.INTEGER);

    public StxckFiberClientConfig() {
        var builder = ConfigTree.builder();
        builder.beginValue("minItemCountRenderDistance", ConfigTypes.INTEGER.withMaximum(128).withMinimum(0), 8)
                .withComment("""
                        
                        The maximum distance between you and the drops to display its count.
                        Range: 0 ~ 128
                        """)
                .finishValue(minItemCountRenderDistance::mirror);

        configTree = builder.build();
    }

    @Override
    public int getMinItemCountRenderDistance() {
        return minItemCountRenderDistance.getValue();
    }
}
