package dev.anhcraft.vhvl10.views;

import dev.anhcraft.vhvl10.gui.GUIApp;

public enum View {
    MAIN_MENU,
    REVIEW_MENU,
    REVIEW_MULTI_CHOICES,
    WIKI,
    LAB_MENU,
    LAB_LINEAR_MOTION,
    LAB_PROJECTILE_MOTION;

    public void load() {
        GUIApp.setView(this);
    }
}
