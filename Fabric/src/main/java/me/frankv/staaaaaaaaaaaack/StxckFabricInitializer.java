package me.frankv.staaaaaaaaaaaack;

import me.frankv.staaaaaaaaaaaack.config.FiberUtils;
import me.frankv.staaaaaaaaaaaack.config.StxckFiberClientConfig;
import me.frankv.staaaaaaaaaaaack.config.StxckFiberCommonConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.registries.BuiltInRegistries;

public class StxckFabricInitializer implements ModInitializer, ClientModInitializer {

    @Override
    public void onInitialize() {
        var config = new StxckFiberCommonConfig();
        FiberUtils.setup(config.getConfigTree(), StxckFiberCommonConfig.fileName);

        Staaaaaaaaaaaack.commonConfig = config;
        Staaaaaaaaaaaack.registriesFetcher = BuiltInRegistries.ITEM::get;
    }

    @Override
    public void onInitializeClient() {
        var config = new StxckFiberClientConfig();
        Staaaaaaaaaaaack.clientConfig = config;
        FiberUtils.setup(config.getConfigTree(), StxckFiberClientConfig.fileName);
    }

}
