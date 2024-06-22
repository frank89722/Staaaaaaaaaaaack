package me.frankv.staaaaaaaaaaaack;

import me.frankv.staaaaaaaaaaaack.config.StxckNeoForgeClientConfig;
import me.frankv.staaaaaaaaaaaack.config.StxckNeoForgeCommonConfig;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;


@Mod(Staaaaaaaaaaaack.MODID)
public class StxckNeoForgeInitializer {
    public StxckNeoForgeInitializer(ModContainer container) {
        var eventBus = NeoForge.EVENT_BUS;
        eventBus.addListener(EventPriority.LOWEST, this::onEntityJoinLevel);

        initConfigs(container);
    }

    private void initConfigs(ModContainer container) {
        var clientConfig = new ModConfigSpec.Builder().configure(StxckNeoForgeClientConfig::new);
        container.registerConfig(ModConfig.Type.CLIENT, clientConfig.getRight());
        Staaaaaaaaaaaack.clientConfig = clientConfig.getLeft();

        var commonConfig = new ModConfigSpec.Builder().configure(StxckNeoForgeCommonConfig::new);
        container.registerConfig(ModConfig.Type.COMMON, commonConfig.getRight());
        Staaaaaaaaaaaack.commonConfig = commonConfig.getLeft();
    }

    private void onEntityJoinLevel(EntityJoinLevelEvent event) {
        EventHandler.onEntityCreate(event.getEntity(), () -> event.setCanceled(true));
    }
}
