package me.frankv.staaaaaaaaaaaack.config;

public interface StxckClientConfig {
    int getMinItemCountRenderDistance();
    double getOverlaySizeMultiplier();
    boolean isAlwaysShowItemCount();
    OverlayDisplayMode getOverlayDisplayMode();

    enum OverlayDisplayMode {
        ITEM_COUNT,
        STACK_COUNT
    }
}
