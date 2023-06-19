package schumi178.javaprograms.cheatbuster.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import schumi178.javaprograms.cheatbuster.CheatBuster;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class AboutScreen implements Initializable {

    @FXML
    public ImageView logo;
    @FXML
    private VBox vBox;

    @FXML
    public void openAttributeSite1() {
        CheatBuster.showWebsite("https://www.freepik.com/");
    }

    @FXML
    public void openAttributeSite2() { CheatBuster.showWebsite("https://www.flaticon.com/authors/smashicons");}

    @FXML
    public void openFlaticonSite() {
        CheatBuster.showWebsite("https://www.flaticon.com/");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/logo.png")));
        logo.setImage(image);
    }

    public VBox getVBox() {
        return vBox;
    }
}
