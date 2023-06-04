package me.frankv.staaaaaaaaaaaack.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class StxckForgeClientConfig implements StxckClientConfig {
    private final ForgeConfigSpec.IntValue minItemCountRenderDistance;


    public StxckForgeClientConfig(ForgeConfigSpec.Builder builder) {
        builder.push("Item count overlay");

        minItemCountRenderDistance = builder
                .comment("The maximum distance between you and the drops to display its count.")
                .comment("Default: 8")
                .defineInRange("minItemCountRenderDistance", 8, 0, 128);

        builder.pop();
    }

    @Override
    public int getMinItemCountRenderDistance() {
        return minItemCountRenderDistance.get();
    }
}
