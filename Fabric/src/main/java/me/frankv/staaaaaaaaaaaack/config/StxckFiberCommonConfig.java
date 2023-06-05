package me.frankv.staaaaaaaaaaaack.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ListConfigType;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import lombok.Getter;
import me.frankv.staaaaaaaaaaaack.Staaaaaaaaaaaack;

import java.util.List;

public class StxckFiberCommonConfig implements StxckCommonConfig {
    public static final String fileName = Staaaaaaaaaaaack.MODID + "-common.json5";

    private static final ListConfigType<List<String>, String> stringListConfigType = ConfigTypes.makeList(ConfigTypes.STRING);

    @Getter
    private final ConfigTree configTree;

    private final PropertyMirror<Double> maxMergeDistanceHorizontal = PropertyMirror.create(ConfigTypes.DOUBLE);
    private final PropertyMirror<Double> maxMergeDistanceVertical = PropertyMirror.create(ConfigTypes.DOUBLE);
    private final PropertyMirror<Integer> maxSize = PropertyMirror.create(ConfigTypes.INTEGER);
    private final PropertyMirror<Boolean> enableForUnstackableItem = PropertyMirror.create(ConfigTypes.BOOLEAN);
    private final PropertyMirror<List<String>> itemBlackList = PropertyMirror.create(stringListConfigType);

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

        builder.beginValue("itemBlackList", stringListConfigType, List.of())
                .withComment("""
                        
                        The list of items that should not exceed their original max stack size.
                        You can achieve the same feature by using the item tag "#staaaaaaaaaaaack:blacklist" as well.
                        e.g., ["minecraft:diamond_block", "minecraft:coal"]
                        """)
                .finishValue(itemBlackList::mirror);

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
    public List<? extends String> getItemBlackList() {
        return itemBlackList.getValue();
    }
}
