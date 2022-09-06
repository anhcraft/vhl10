package dev.anhcraft.vhvl10.views;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuView implements Initializable {
    @FXML
    private ImageView review;
    @FXML
    private ImageView lab;
    @FXML
    private ImageView wiki;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        review.setOnMouseClicked(mouseEvent -> View.REVIEW_MENU.load());
        lab.setOnMouseClicked(mouseEvent -> View.LAB_MENU.load());
        wiki.setOnMouseClicked(mouseEvent -> View.WIKI.load());
    }
}
