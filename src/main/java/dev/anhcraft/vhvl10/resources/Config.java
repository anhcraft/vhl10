package dev.anhcraft.vhvl10.resources;

import dev.anhcraft.neep.NeepConfig;
import dev.anhcraft.neep.errors.NeepReaderException;
import dev.anhcraft.vhvl10.Main;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class Config {
    public static final NeepConfig GENERAL = load("general");
    public static final NeepConfig REVIEW = load("review");
    public static final NeepConfig WIKI = load("wiki");

    @NotNull
    private static NeepConfig load(String fileName) {
        try {
            return NeepConfig.fromInputStream(Main.class.getResourceAsStream("/configs/" + fileName + ".neep"));
        } catch (IOException | NeepReaderException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }
}
