package me.frankv.staaaaaaaaaaaack.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.*;

@Slf4j
public class FiberUtils {

    private static void writeDefaultConfig(ConfigTree config, Path path, JanksonValueSerializer serializer) {
        try (var outputStream = new BufferedOutputStream(Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW))) {
            FiberSerialization.serialize(config, outputStream, serializer);
        } catch (FileAlreadyExistsException ignored) {} catch (IOException e) {
            log.error("Error writing default config", e);
        }
    }

    private static void loadConfig(ConfigTree config, Path path, JanksonValueSerializer serializer) {
        writeDefaultConfig(config, path, serializer);

        try (var inputStream = new BufferedInputStream(Files.newInputStream(path, StandardOpenOption.READ, StandardOpenOption.CREATE))) {
            FiberSerialization.deserialize(config, inputStream, serializer);
        } catch (IOException | ValueDeserializationException e) {
            log.error("Error loading config from {}", path, e);
        }
    }

    public static void setup(ConfigTree config, String fileName) {
        try {
            Files.createDirectory(Paths.get("config"));
        } catch (FileAlreadyExistsException ignored) {} catch (IOException e) {
            log.warn("Failed to make config dir", e);
        }

        var serializer = new JanksonValueSerializer(false);
        loadConfig(config, Path.of("config", fileName), serializer);
    }
}
