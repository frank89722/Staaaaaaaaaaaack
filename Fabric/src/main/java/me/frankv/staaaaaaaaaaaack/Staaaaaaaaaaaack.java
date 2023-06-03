package me.frankv.staaaaaaaaaaaack;

import lombok.extern.slf4j.Slf4j;
import me.frankv.staaaaaaaaaaaack.config.FiberUtils;
import me.frankv.staaaaaaaaaaaack.config.StxckFiberClientConfig;
import me.frankv.staaaaaaaaaaaack.config.StxckFiberCommonConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class Staaaaaaaaaaaack implements ModInitializer, ClientModInitializer {

    @Override
    public void onInitialize() {
        var config = new StxckFiberCommonConfig();
        StxckCommon.commonConfig = config;
        FiberUtils.setup(config.getConfigTree(), StxckFiberCommonConfig.fileName);
    }

    @Override
    public void onInitializeClient() {
        var config = new StxckFiberClientConfig();
        StxckCommon.clientConfig = config;
        FiberUtils.setup(config.getConfigTree(), StxckFiberClientConfig.fileName);
    }


}
