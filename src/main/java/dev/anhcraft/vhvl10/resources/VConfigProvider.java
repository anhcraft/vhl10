package dev.anhcraft.vhvl10.resources;

import dev.anhcraft.config.ConfigDeserializer;
import dev.anhcraft.config.ConfigProvider;
import dev.anhcraft.config.ConfigSerializer;
import dev.anhcraft.config.NeepConfigSection;
import dev.anhcraft.config.struct.ConfigSection;
import org.jetbrains.annotations.NotNull;

public class VConfigProvider implements ConfigProvider {
    @Override
    public @NotNull ConfigSection createSection() {
        return new NeepConfigSection();
    }

    @Override
    public @NotNull ConfigSerializer createSerializer() {
        return new VConfigSerializer(this);
    }

    @Override
    public @NotNull ConfigDeserializer createDeserializer() {
        return new VConfigDeserializer(this);
    }
}
