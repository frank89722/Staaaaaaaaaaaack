package me.frankv.staaaaaaaaaaaack;

import me.frankv.staaaaaaaaaaaack.config.StxckCommonConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;

public class EventHandler {
    private static final StxckCommonConfig config = Stxck.commonConfig;


    public static void onEntityCreate(Entity entity, Runnable eventCanceller) {
        if (!(entity instanceof ItemEntity itemEntity && Stxck.isMergable(itemEntity))
                || Stxck.isBlackListItem(itemEntity.getItem())) return;

        var h = config.getMaxMergeDistanceHorizontal();
        var v = config.getMaxMergeDistanceVertical();

        var nearByEntities = itemEntity.level().getEntitiesOfClass(
                ItemEntity.class,
                itemEntity.getBoundingBox().inflate(h, Math.max(v, .5f), h),
                (she) -> itemEntity != she && Stxck.isMergable(she)
        );

        for(var nearByEntity : nearByEntities) {
            Stxck.tryToMerge(nearByEntity, itemEntity, true);
            if (itemEntity.isRemoved()) {
                eventCanceller.run();
                break;
            }
        }
    }
    
}
