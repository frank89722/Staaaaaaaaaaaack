package me.frankv.staaaaaaaaaaaack.platform;


import lombok.extern.slf4j.Slf4j;
import me.frankv.staaaaaaaaaaaack.platform.services.PlatformHelper;

import java.util.ServiceLoader;

@Slf4j
public class Services {

    public static final PlatformHelper PLATFORM = load(PlatformHelper.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        log.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
