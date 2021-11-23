package schumi178.javaprograms.cheatbuster.gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class SingleResult implements Initializable {
    @FXML
    private Label listenerName;
    @FXML
    private Label ratingLabel;
    @FXML
    private ProgressBar ratingBar;
    @FXML
    private Text resultText;
    @FXML
    private ScrollPane scrollPane;

    private int rating;

    public void setListenerName(String listenerName) {
        this.listenerName.setText(listenerName);
    }

    public String getListenerName() {
        return listenerName.getText();
    }

    public int getRating() {
        return rating;
    }

    public String getResultText() {
        return resultText.getText();
    }

    public void setRating(int rating) {
        if(rating < 0)
            this.rating = 0;
        else this.rating = Math.min(rating, 100);
        ratingLabel.setText(rating + "%");
        if(rating <= 25) {
            ratingLabel.setTextFill(Color.GREEN);
            ratingBar.getStyleClass().removeAll("red-bar", "yellow-bar");
            ratingBar.getStyleClass().add("green-bar");
        } else if(rating <= 75) {
            ratingLabel.setTextFill(Color.GOLD);
            ratingBar.getStyleClass().removeAll("red-bar", "green-bar");
            ratingBar.getStyleClass().add("yellow-bar");
        } else {
            ratingLabel.setTextFill(Color.RED);
            ratingBar.getStyleClass().removeAll("yellow-bar", "green-bar");
            ratingBar.getStyleClass().add("red-bar");
        }
    }

    public void setResultText(String text) {
        resultText.setText(text);
    }

    public void onShown() {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000), new KeyValue(ratingBar.progressProperty(), rating / 100.0)));
        timeline.play();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resultText.wrappingWidthProperty().bind(scrollPane.widthProperty());
    }
}
