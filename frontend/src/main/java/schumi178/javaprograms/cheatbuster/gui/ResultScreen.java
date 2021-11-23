package schumi178.javaprograms.cheatbuster.gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ResultScreen {
    @FXML
    private Stage stage;
    @FXML
    private HBox resultSpace;
    @FXML
    private Label finalRatingLabel;
    @FXML
    private ProgressBar finalRatingBar;

    private final List<SingleResult> controllers = new ArrayList<>();

    private int rating;

    public HBox getResultSpace() {
        return resultSpace;
    }

    public void addController(SingleResult result) {
        controllers.add(result);
    }

    public void setFinalRating(int rating) {
        if(rating < 0)
            this.rating = 0;
        else this.rating = Math.min(rating, 100);
        finalRatingLabel.setText(rating + "%");
        if(rating <= 25) {
            finalRatingLabel.setTextFill(Color.GREEN);
            finalRatingBar.getStyleClass().removeAll("red-bar", "yellow-bar");
            finalRatingBar.getStyleClass().add("green-bar");
        } else if(rating <= 75) {
            finalRatingLabel.setTextFill(Color.GOLD);
            finalRatingBar.getStyleClass().removeAll("red-bar", "green-bar");
            finalRatingBar.getStyleClass().add("yellow-bar");
        } else {
            finalRatingLabel.setTextFill(Color.RED);
            finalRatingBar.getStyleClass().removeAll("yellow-bar", "green-bar");
            finalRatingBar.getStyleClass().add("red-bar");
        }
    }

    @FXML
    private void onShown() {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000), new KeyValue(finalRatingBar.progressProperty(), rating / 100.0)));
        timeline.play();
    }

    @FXML
    private void saveResult() throws FileNotFoundException {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new ExtensionFilter("Plik tekstowy", "*.txt"));
        File file = chooser.showSaveDialog(stage);
        if(file != null) {
            PrintWriter writer = new PrintWriter(file);
            for(SingleResult assessmentStage: controllers) {
                writer.write(assessmentStage.getListenerName() + ": " + assessmentStage.getRating() + "%\n");
                writer.write("Uwagi:\n");
                writer.write(assessmentStage.getResultText() + "\n\n");
            }

            writer.write("Końcowa ocena: " + rating + "%");
            writer.close();
        }
    }

    @FXML
    private void onClickOK() {
        stage.close();
    }
}
