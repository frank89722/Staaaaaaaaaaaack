package me.frankv.staaaaaaaaaaaack.config;


public class StxckConfig implements StxckCommonConfig, StxckClientConfig {
    private int minItemCountRenderDistance = 8;

    private float maxMergeDistanceHorizontal = 1.25f;

    private float maxMergeDistanceVertical = 0f;

    @Override
    public int getMinItemCountRenderDistance() {
        return minItemCountRenderDistance * minItemCountRenderDistance;
    }

    @Override
    public float getMaxMergeDistanceHorizontal() {
        return maxMergeDistanceHorizontal;
    }

    @Override
    public float getMaxMergeDistanceVertical() {
        return maxMergeDistanceVertical;
    }
}
