package me.frankv.staaaaaaaaaaaack;

import me.frankv.staaaaaaaaaaaack.config.StxckForgeClientConfig;
import me.frankv.staaaaaaaaaaaack.config.StxckForgeCommonConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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
        initConfigs();
    }

    private void initConfigs() {
        var clientConfig = new ForgeConfigSpec.Builder().configure(StxckForgeClientConfig::new);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientConfig.getRight());
        StxckCommon.clientConfig = clientConfig.getLeft();

        var commonConfig = new ForgeConfigSpec.Builder().configure(StxckForgeCommonConfig::new);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, commonConfig.getRight());
        StxckCommon.commonConfig = commonConfig.getLeft();
    }

    private void onEntityJoinLevel(EntityJoinWorldEvent event) {
        EventHandler.onEntityCreate(event.getEntity(), () -> event.setCanceled(true));
    }
}
