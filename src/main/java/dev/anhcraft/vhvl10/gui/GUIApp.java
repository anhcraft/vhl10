package dev.anhcraft.vhvl10.gui;

import dev.anhcraft.vhvl10.Main;
import dev.anhcraft.vhvl10.resources.Config;
import dev.anhcraft.vhvl10.views.View;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIApp extends Application {
    public static boolean shift;
    public static boolean control;
    public static View view;
    private static Scene scene;

    public static void setView(View v) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/" + v.name().toLowerCase() + ".fxml"));
            GUIApp.scene.setRoot(loader.load());
            view = v;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void start(String[] args) {
        GUIApp.launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle(Config.GENERAL.getString("scene.title"));

        scene = new Scene(
                new Group(),
                Config.GENERAL.getInt("scene.width"),
                Config.GENERAL.getInt("scene.height")
        );
        scene.addEventFilter(KeyEvent.ANY, keyEvent -> {
            shift = keyEvent.isShiftDown();
            control = keyEvent.isControlDown();
        });
        View.MAIN_MENU.load();
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            Main.destroy();
            System.exit(0);
        });
    }
}
