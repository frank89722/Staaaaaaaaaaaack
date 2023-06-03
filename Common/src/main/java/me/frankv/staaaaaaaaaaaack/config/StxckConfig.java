package me.frankv.staaaaaaaaaaaack.config;


public class StxckConfig implements StxckCommonConfig, StxckClientConfig {

    @Override
    public int getMinItemCountRenderDistance() {
        return 8;
    }

    @Override
    public double getMaxMergeDistanceHorizontal() {
        return 1.25d;
    }

    @Override
    public double getMaxMergeDistanceVertical() {
        return 0d;
    }
}
