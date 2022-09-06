package dev.anhcraft.vhvl10.views;

import javafx.fxml.FXML;

public class LabMenuView {
    @FXML
    public void linearMotion() {
        View.LAB_LINEAR_MOTION.load();
    }

    @FXML
    public void projectileMotion() {
        View.LAB_PROJECTILE_MOTION.load();
    }

    @FXML
    public void quit() {
        View.MAIN_MENU.load();
    }
}
