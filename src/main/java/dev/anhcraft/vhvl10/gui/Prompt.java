package dev.anhcraft.vhvl10.gui;

import dev.anhcraft.vhvl10.resources.Config;
import javafx.scene.control.Alert;

import java.util.function.Consumer;

public class Prompt {
    public static void ask(String message, Consumer<Boolean> cancelled) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, message);
        a.setHeaderText(null);
        a.setTitle(Config.GENERAL.getString("prompt.ask"));
        a.showAndWait().ifPresent(buttonType -> cancelled.accept(buttonType.getButtonData().isDefaultButton()));
    }

    public static void info(String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, message);
        a.setHeaderText(null);
        a.setTitle(Config.GENERAL.getString("prompt.info"));
        a.show();
    }

    public static void err(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR, message);
        a.setHeaderText(null);
        a.setTitle(Config.GENERAL.getString("prompt.error"));
        a.show();
    }
}
