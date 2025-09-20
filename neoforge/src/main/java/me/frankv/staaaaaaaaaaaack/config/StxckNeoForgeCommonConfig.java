package me.frankv.staaaaaaaaaaaack.config;


import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.*;

public class StxckNeoForgeCommonConfig implements StxckCommonConfig {
    private final ModConfigSpec.DoubleValue maxMergeDistanceHorizontal;
    private final ModConfigSpec.DoubleValue maxMergeDistanceVertical;
    private final ModConfigSpec.IntValue maxSize;
    private final ModConfigSpec.BooleanValue enableForUnstackableItem;
    private final ModConfigSpec.BooleanValue itemListAsWhitelist;
    private final ModConfigSpec.ConfigValue<List<? extends String>> itemList;
    private final ModConfigSpec.BooleanValue dimensionListAsWhitelist;
    private final ModConfigSpec.ConfigValue<List<? extends String>> dimensionList;


    public StxckNeoForgeCommonConfig(ModConfigSpec.Builder builder) {
        builder.push("Item merge settings");

        maxMergeDistanceHorizontal = builder
                .comment("The maximum horizontal block distance over which dropped items attempt to merge with each other.")
                .comment("Default: 1.25, Minecraft default: 0.5")
                .defineInRange("maxMergeDistanceHorizontal", 1.25d, 0.5d, 10d);

        maxMergeDistanceVertical = builder
                .comment("The maximum vertical block distance over which dropped items attempt to merge with each other.")
                .comment("Default: 0.0, Minecraft default: 0.0")
                .defineInRange("maxMergeDistanceVertical", 0d, 0d, 10d);

        maxSize = builder
                .comment("The maximum number of extra items that an item entity can hold.")
                .comment(String.format("Default: %d", Integer.MAX_VALUE))
                .defineInRange("maxSize", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);

        enableForUnstackableItem = builder
                .comment("Enable for merging non-stackable item.")
                .comment("Should be used with caution while playing with other mods.")
                .comment("Default: false")
                .define("enableForUnstackableItem", false);

        itemListAsWhitelist = builder
                .comment("Set this \"true\" to make `itemList` a whitelist, it's a blacklist when set to \"false\".")
                .comment("Default: false")
                .define("itemListAsWhitelist", false);

        itemList = builder
                .comment("The list of items that should or should not exceed their original max stack size.")
                .comment("Or You can by using the item tag \"#staaaaaaaaaaaack:blacklist\" to blacklist the item.")
                .comment("e.g., [\"minecraft:diamond_block\", \"minecraft:coal\"]")
                .defineList("itemList", ArrayList::new, () -> "",o -> o instanceof String);

        dimensionListAsWhitelist = builder
                .comment("Set this \"true\" to make `dimensionList` a whitelist, it's a blacklist when set to " +
                        "\"false\".")
                .comment("Default: false")
                .define("dimensionListAsWhitelist", false);

        dimensionList = builder
                .comment("The list of dimension that should or should not make item exceed their original max stack " +
                        "size.")
                .comment("e.g., [\"minecraft:overworld\", \"minecraft:the_nether\"]")
                .defineList("dimensionList", ArrayList::new, () -> "",o -> o instanceof String);

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

    @Override
    public int getMaxSize() {
        return maxSize.get();
    }

    @Override
    public boolean isEnableForUnstackableItem() {
        return enableForUnstackableItem.get();
    }

    @Override
    public boolean isItemListAsWhitelist() {
        return itemListAsWhitelist.get();
    }

    @Override
    public List<? extends String> getItemList() {
        return itemList.get();
    }

    @Override
    public boolean isDimensionListAsWhitelist() {
        return dimensionListAsWhitelist.get();
    }

    @Override
    public List<? extends String> getDimensionList() {
        return dimensionList.get();
    }

    @Override
    public CleanDropsScheduler getCleanDropsScheduler() {
        return null;
    }

    @Override
    public int getCleanDropsIntervalSeconds() {
        return 0;
    }

    @Override
    public int getCleanDropsItemCountThreshold() {
        return 0;
    }

    @Override
    public boolean onlyCleanSingleItemType() {
        return false;
    }

    @Override
    public boolean isCleanDropsItemListAsWhitelist() {
        return false;
    }

    @Override
    public List<? extends String> getCleanDropsItemList() {
        return List.of();
    }

    @Override
    public boolean isCleanDropsDimensionListAsWhitelist() {
        return false;
    }

    @Override
    public List<? extends String> getCleanDropsDimensionList() {
        return List.of();
    }
}
