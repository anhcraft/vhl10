package dev.anhcraft.vhvl10.views;

import dev.anhcraft.neep.struct.primitive.NeepString;
import dev.anhcraft.vhvl10.Main;
import dev.anhcraft.vhvl10.resources.Config;
import dev.anhcraft.vhvl10.resources.Storage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class Wiki implements Initializable {
    private List<String> images = new ArrayList<>();

    @FXML
    public ImageView image;
    @FXML
    public Slider slider;

    @FXML
    public void quit() {
        View.MAIN_MENU.load();
    }

    private void changeImage(int index) {
        image.setImage(new Image(Main.class.getResourceAsStream("/images/" + images.get(index))));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (Object obj : Objects.requireNonNull(Config.WIKI.getList("images"))) {
            images.add(((NeepString) obj).getValue());
        }
        changeImage(Storage.userData.getSliderIndex());
        slider.setBlockIncrement(1);
        slider.setMin(0);
        slider.setMax(images.size() - 1);
        slider.setValue(Storage.userData.getSliderIndex());
        slider.valueProperty().addListener((ov, v, t1) -> {
            int i = (int) slider.getValue();
            Storage.userData.setSliderIndex(i);
            changeImage(i);
        });
    }
}
