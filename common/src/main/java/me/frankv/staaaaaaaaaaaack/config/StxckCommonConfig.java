package me.frankv.staaaaaaaaaaaack.config;

import java.util.List;

public interface StxckCommonConfig {
    enum CleanDropsScheduler {
        DISABLED,
        INTERVAL,
    }


    double getMaxMergeDistanceHorizontal();
    double getMaxMergeDistanceVertical();
    int getMaxSize();
    boolean isEnableForUnstackableItem();
    boolean isItemListAsWhitelist();
    List<? extends String> getItemList();
    boolean isDimensionListAsWhitelist();
    List<? extends String> getDimensionList();

    CleanDropsScheduler getCleanDropsScheduler();
    int getCleanDropsIntervalSeconds();
    int getCleanDropsItemCountThreshold();
    boolean onlyCleanSingleItemType();
    boolean isCleanDropsItemListAsWhitelist();
    List<? extends String> getCleanDropsItemList();
    boolean isCleanDropsDimensionListAsWhitelist();
    List<? extends String> getCleanDropsDimensionList();

}
