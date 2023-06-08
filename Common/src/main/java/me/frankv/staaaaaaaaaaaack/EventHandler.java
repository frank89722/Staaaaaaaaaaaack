package me.frankv.staaaaaaaaaaaack;

import me.frankv.staaaaaaaaaaaack.config.StxckCommonConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

import static me.frankv.staaaaaaaaaaaack.StxckUtil.*;

public class EventHandler {
    private static final StxckCommonConfig config = Staaaaaaaaaaaack.commonConfig;


    public static void onEntityCreate(Entity entity, Runnable eventCanceller) {
        if (!(entity instanceof ItemEntity itemEntity && isMergable(itemEntity))
                || isBlackListItem(itemEntity.getItem())) return;

        var h = config.getMaxMergeDistanceHorizontal();
        var v = config.getMaxMergeDistanceVertical();

        var nearByEntities = itemEntity.level().getEntitiesOfClass(
                ItemEntity.class,
                itemEntity.getBoundingBox().inflate(h, Math.max(v, .5f), h),
                (she) -> itemEntity != she && isMergable(she)
        );

        for(var nearByEntity : nearByEntities) {
            tryToMerge(nearByEntity, itemEntity);
            if (itemEntity.isRemoved()) {
                eventCanceller.run();
                break;
            }
        }
    }
}
