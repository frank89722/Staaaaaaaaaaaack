package me.frankv.staaaaaaaaaaaack.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ListConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import lombok.Getter;
import me.frankv.staaaaaaaaaaaack.Stxck;

import java.util.List;

public class StxckFiberCommonConfig implements StxckCommonConfig {
    public static final String fileName = Stxck.MODID + "-common.json5";

    private static final ListConfigType<List<String>, String> stringListConfigType = ConfigTypes.makeList(ConfigTypes.STRING);

    @Getter
    private final ConfigTree configTree;

    private final PropertyMirror<Double> maxMergeDistanceHorizontal = PropertyMirror.create(ConfigTypes.DOUBLE);
    private final PropertyMirror<Double> maxMergeDistanceVertical = PropertyMirror.create(ConfigTypes.DOUBLE);
    private final PropertyMirror<Integer> maxSize = PropertyMirror.create(ConfigTypes.INTEGER);
    private final PropertyMirror<Boolean> enableForUnstackableItem = PropertyMirror.create(ConfigTypes.BOOLEAN);
    private final PropertyMirror<Boolean> itemListAsWhiteList = PropertyMirror.create(ConfigTypes.BOOLEAN);
    private final PropertyMirror<List<String>> itemList = PropertyMirror.create(stringListConfigType);
    private final PropertyMirror<Boolean> dimensionListAsWhiteList = PropertyMirror.create(ConfigTypes.BOOLEAN);
    private final PropertyMirror<List<String>> dimensionList = PropertyMirror.create(stringListConfigType);

    public StxckFiberCommonConfig() {
        var builder = ConfigTree.builder();
        builder.beginValue("maxMergeDistanceHorizontal", ConfigTypes.DOUBLE.withMaximum(10d).withMinimum(.5d), 1.25d)
                .withComment("""
                        
                        The maximum horizontal block distance over which dropped items attempt to merge with each other.
                        Range: 0.5 ~ 10.0, Default: 1.25, Minecraft default: 0.5
                        """)
                .finishValue(maxMergeDistanceHorizontal::mirror);

        builder.beginValue("maxMergeDistanceVertical", ConfigTypes.DOUBLE.withMaximum(10d).withMinimum(0d), 0d)
                .withComment("""
                        
                        The minimum vertical block distance over which dropped items attempt to merge with each other.
                        Range: 0.0 ~ 10.0, Default: 0.0, Minecraft default: 0.0
                        """)
                .finishValue(maxMergeDistanceVertical::mirror);

        builder.beginValue("maxSize", ConfigTypes.INTEGER.withMaximum(Integer.MAX_VALUE).withMinimum(1), Integer.MAX_VALUE)
                .withComment(String.format("""
                        
                        The maximum number of extra items that an item entity can hold.
                        Range: 0 ~ %d, Default: %d
                        """, Integer.MAX_VALUE, Integer.MAX_VALUE))
                .finishValue(maxSize::mirror);

        builder.beginValue("enableForUnstackableItem", ConfigTypes.BOOLEAN, false)
                .withComment("""
                        
                        Enable for merging non-stackable item.
                        Should be used with caution while playing with other mods.
                        Default: false
                        """)
                .finishValue(enableForUnstackableItem::mirror);

        builder.beginValue("itemListAsWhitelist", ConfigTypes.BOOLEAN, false)
                .withComment("""
                        
                        Set this "true" to make `itemList` a whitelist, it's a blacklist when set to "false".
                        Default: false
                        """)
                .finishValue(itemListAsWhiteList::mirror);

        builder.beginValue("itemList", stringListConfigType, List.of())
                .withComment("""
                        
                        The list of items that should or should not exceed their original max stack size.
                        Or You can by using the item tag "#staaaaaaaaaaaack:blacklist" to blacklist the item.
                        e.g., ["minecraft:diamond_block", "minecraft:coal"]
                        """)
                .finishValue(itemList::mirror);

        builder.beginValue("dimensionListAsWhitelist", ConfigTypes.BOOLEAN, false)
                .withComment("""
                        
                        Set this "true" to make `dimensionList` a whitelist, it's a blacklist when set to "false".
                        Default: false
                        """)
                .finishValue(dimensionListAsWhiteList::mirror);

        builder.beginValue("dimensionList", stringListConfigType, List.of())
                .withComment("""
                        
                        The list of dimension that should or should not make item exceed their original max stack size.
                        e.g., ["minecraft:overworld", "minecraft:the_nether"]
                        """
                )
                .finishValue(dimensionList::mirror);

        configTree = builder.build();
    }

    @Override
    public double getMaxMergeDistanceHorizontal() {
        return maxMergeDistanceHorizontal.getValue();
    }

    @Override
    public double getMaxMergeDistanceVertical() {
        return maxMergeDistanceVertical.getValue();
    }

    @Override
    public int getMaxSize() {
        return maxSize.getValue();
    }

    @Override
    public boolean isEnableForUnstackableItem() {
        return enableForUnstackableItem.getValue();
    }

    @Override
    public boolean isItemListAsWhitelist() {
        return itemListAsWhiteList.getValue();
    }

    @Override
    public List<? extends String> getItemList() {
        return itemList.getValue();
    }

    @Override
    public boolean isDimensionListAsWhitelist() {
        return dimensionListAsWhiteList.getValue();
    }

    @Override
    public List<? extends String> getDimensionList() {
        return dimensionList.getValue();
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
