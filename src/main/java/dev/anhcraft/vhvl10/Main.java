package dev.anhcraft.vhvl10;

import dev.anhcraft.vhvl10.gui.GUIApp;
import dev.anhcraft.vhvl10.resources.Storage;
import dev.anhcraft.vhvl10.tasks.TaskManager;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Storage.loadUserData();
        TaskManager.runRepeatAsync(Storage::saveUserData, 0, 10, TimeUnit.SECONDS);
        GUIApp.start(args);
        Runtime.getRuntime().addShutdownHook(new Thread(Main::destroy));
    }

    public static void destroy() {
        TaskManager.destroy();
    }
}
