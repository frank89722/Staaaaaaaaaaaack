package me.frankv.staaaaaaaaaaaack.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class StxckForgeClientConfig implements StxckClientConfig {
    private final ForgeConfigSpec.IntValue minItemCountRenderDistance;
    private final ForgeConfigSpec.DoubleValue overlaySizeMultiplier;


    public StxckForgeClientConfig(ForgeConfigSpec.Builder builder) {
        builder.push("Item count overlay");

        minItemCountRenderDistance = builder
                .comment("The maximum distance between you and the drops to display its count.")
                .comment("Default: 8")
                .defineInRange("minItemCountRenderDistance", 8, 0, 128);

        overlaySizeMultiplier = builder
                .comment("Default: 0.8")
                .defineInRange("overlaySizeMultiplier", 0.8d, 0.1d, 2d);

        builder.pop();
    }

    @Override
    public int getMinItemCountRenderDistance() {
        return minItemCountRenderDistance.get();
    }

    @Override
    public double getOverlaySizeMultiplier() {
        return overlaySizeMultiplier.get();
    }
}
