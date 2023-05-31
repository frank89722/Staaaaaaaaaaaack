package me.frankv.staaaaaaaaaaaack;

import me.frankv.staaaaaaaaaaaack.config.StxckCommonConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.function.Function;

import static me.frankv.staaaaaaaaaaaack.StxckUtil.isMergable;
import static me.frankv.staaaaaaaaaaaack.StxckUtil.tryToMerge;

public class EventHandler {
    private static final StxckCommonConfig config = StxckCommon.commonConfig;

    public static void onEntityCreate(Entity entity, Runnable eventCanceller) {
        if (!(entity instanceof ItemEntity itemEntity && isMergable(itemEntity))) return;

        var h = config.getMaxMergeDistanceHorizontal();
        var v = config.getMaxMergeDistanceVertical();

        var nearByEntities = itemEntity.level.getEntitiesOfClass(
                ItemEntity.class,
                itemEntity.getBoundingBox().inflate(h, v == 0 ? .5f : v, h),
                (she) -> itemEntity != she && isMergable(she)
        );

        for(ItemEntity itementity : nearByEntities) {
            tryToMerge(itementity, itemEntity);
            if (itemEntity.isRemoved()) {
                eventCanceller.run();
                break;
            }
        }
    }
}
