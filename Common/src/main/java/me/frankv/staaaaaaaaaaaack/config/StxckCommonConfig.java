package me.frankv.staaaaaaaaaaaack.config;

import java.util.List;
import java.util.Set;

public interface StxckCommonConfig {
    double getMaxMergeDistanceHorizontal();
    double getMaxMergeDistanceVertical();
    boolean isEnableForUnstackableItem();
    List<? extends String> getItemBlackList();
}
