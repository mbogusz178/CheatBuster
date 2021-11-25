package schumi178.javaprograms.cheatbuster.gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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
    @FXML
    private VBox vBox;
    @FXML
    private VBox styleVBox;

    @FXML
    private VBox resultVBox;

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
            styleVBox.getStylesheets().removeAll("fxml/progressBarRed.css", "fxml/progressBarYellow.css");
            styleVBox.getStylesheets().add("fxml/progressBarGreen.css");
            resultVBox.getStylesheets().removeAll("fxml/finalRatingBad.css", "fxml/finalRatingAverage.css");
            resultVBox.getStylesheets().add("fxml/finalResultGood.css");
        } else if(rating <= 75) {
            finalRatingLabel.setTextFill(Color.GOLD);
            styleVBox.getStylesheets().removeAll("fxml/progressBarRed.css", "fxml/progressBarGreen.css");
            styleVBox.getStylesheets().add("fxml/progressBarYellow.css");
            resultVBox.getStylesheets().removeAll("fxml/finalRatingBad.css", "fxml/finalRatingGood.css");
            resultVBox.getStylesheets().add("fxml/finalResultAverage.css");
        } else {
            finalRatingLabel.setTextFill(Color.RED);
            styleVBox.getStylesheets().removeAll("fxml/progressBarYellow.css", "fxml/progressBarGreen.css");
            styleVBox.getStylesheets().add("fxml/progressBarRed.css");
            resultVBox.getStylesheets().removeAll("fxml/finalRatingAverage.css", "fxml/finalRatingGood.css");
            resultVBox.getStylesheets().add("fxml/finalResultBad.css");
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

    public VBox getVBox() {
        return vBox;
    }

    public VBox getResultVBox() {
        return resultVBox;
    }
}
