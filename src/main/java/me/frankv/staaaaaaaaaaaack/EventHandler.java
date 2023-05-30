package me.frankv.staaaaaaaaaaaack;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Predicate;

import static me.frankv.staaaaaaaaaaaack.StxckUtil.*;

@Mod.EventBusSubscriber
public class EventHandler {

    private static final StxckConfig config = Staaaaaaaaaaaack.config;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityCreate(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof ItemEntity entity && isMergable(entity))) return;

        var h = config.getMaxMergeDistanceHorizontal();
        var v = config.getMaxMergeDistanceVerital();
        Predicate<ItemEntity> isSheMergableWithMePredicate = (she) -> entity != she && isMergable(she);

        var nearByEntities = entity.level.getEntitiesOfClass(
                ItemEntity.class,
                entity.getBoundingBox().inflate(h, v == 0 ? .5f : v, h),
                isSheMergableWithMePredicate
        );

        for(ItemEntity itementity : nearByEntities) {
            tryToMerge(itementity, entity);
            if (entity.isRemoved()) {
                event.setCanceled(true);
                break;
            }
        }

    }
}
