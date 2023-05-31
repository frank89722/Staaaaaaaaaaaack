package me.frankv.staaaaaaaaaaaack;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;

@Mod(StxckCommon.MODID)
public class Staaaaaaaaaaaack {
    public Staaaaaaaaaaaack() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onEntityJoinLevel);
    }

    private void onEntityJoinLevel(EntityJoinLevelEvent event) {
        EventHandler.onEntityCreate(event.getEntity(), () -> event.setCanceled(true));
    }
}
