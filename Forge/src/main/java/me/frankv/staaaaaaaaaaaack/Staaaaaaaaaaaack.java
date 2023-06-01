package me.frankv.staaaaaaaaaaaack;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;

@Mod(StxckCommon.MODID)
public class Staaaaaaaaaaaack {
    public Staaaaaaaaaaaack() {
        StxckUtil.itemStackMergablePredicate = (a, b) -> {
            if (!a.is(b.getItem())) {
                return false;
            } else if (a.hasTag() ^ b.hasTag()) {
                return false;
            } else if (!b.areCapsCompatible(a)) {
                return false;
            } else {
                return !a.hasTag() || a.getTag().equals(b.getTag());
            }
        };

        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onEntityJoinLevel);
    }

    private void onEntityJoinLevel(EntityJoinWorldEvent event) {
        EventHandler.onEntityCreate(event.getEntity(), () -> event.setCanceled(true));
    }
}
