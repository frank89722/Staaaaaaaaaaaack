package me.frankv.staaaaaaaaaaaack.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class StxckForgeCommonConfig implements StxckCommonConfig {
    private final ForgeConfigSpec.DoubleValue maxMergeDistanceHorizontal;
    private final ForgeConfigSpec.DoubleValue maxMergeDistanceVertical;


    public StxckForgeCommonConfig(ForgeConfigSpec.Builder builder) {
        builder.push("Item count overlay");

        maxMergeDistanceHorizontal = builder
                .comment("The maximum horizontal block distance over which dropped items attempt to merge with each other.")
                .defineInRange("maxMergeDistanceHorizontal", 1.25d, 0.5d, 10d);

        maxMergeDistanceVertical = builder
                .comment("The maximum vertical block distance over which dropped items attempt to merge with each other.")
                .defineInRange("maxMergeDistanceVertical", 0d, 0d, 10d);

        builder.pop();
    }
    @Override
    public double getMaxMergeDistanceHorizontal() {
        return maxMergeDistanceHorizontal.get();
    }

    @Override
    public double getMaxMergeDistanceVertical() {
        return maxMergeDistanceVertical.get();
    }
}
