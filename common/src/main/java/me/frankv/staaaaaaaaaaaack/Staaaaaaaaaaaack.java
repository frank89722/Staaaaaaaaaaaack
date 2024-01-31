package me.frankv.staaaaaaaaaaaack;


import me.frankv.staaaaaaaaaaaack.config.StxckClientConfig;
import me.frankv.staaaaaaaaaaaack.config.StxckCommonConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Staaaaaaaaaaaack {
    public static final String MODID = "staaaaaaaaaaaack";

    public static final TagKey<Item> BLACK_LIST_TAG = TagKey.create(Registries.ITEM, new ResourceLocation(MODID, "blacklist"));
    private static Set<Item> itemBlackList;

    public static StxckCommonConfig commonConfig;
    public static StxckClientConfig clientConfig;


    public static Set<Item> getItemBlackList() {
        if (itemBlackList == null) {
            itemBlackList = commonConfig.getItemBlackList().stream()
                    .map(ResourceLocation::new)
                    .map(BuiltInRegistries.ITEM::get)
                    .collect(Collectors.toUnmodifiableSet());
        }
        return itemBlackList;
    }
}
