package me.frankv.staaaaaaaaaaaack;

import me.frankv.staaaaaaaaaaaack.config.StxckForgeClientConfig;
import me.frankv.staaaaaaaaaaaack.config.StxckForgeCommonConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;


@Mod(Staaaaaaaaaaaack.MODID)
public class StxckForgeInitializer {
    public StxckForgeInitializer() {
        var eventBus = MinecraftForge.EVENT_BUS;
        eventBus.addListener(EventPriority.LOWEST, this::onEntityJoinLevel);

        initConfigs();
        replaceAreMergePredicate();
        Staaaaaaaaaaaack.registriesFetcher = ForgeRegistries.ITEMS::getValue;
    }

    private void initConfigs() {
        var clientConfig = new ForgeConfigSpec.Builder().configure(StxckForgeClientConfig::new);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientConfig.getRight());
        Staaaaaaaaaaaack.clientConfig = clientConfig.getLeft();

        var commonConfig = new ForgeConfigSpec.Builder().configure(StxckForgeCommonConfig::new);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, commonConfig.getRight());
        Staaaaaaaaaaaack.commonConfig = commonConfig.getLeft();
    }

    private void replaceAreMergePredicate() {
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
    }

    private void onEntityJoinLevel(EntityJoinWorldEvent event) {
        EventHandler.onEntityCreate(event.getEntity(), () -> event.setCanceled(true));
    }
}
