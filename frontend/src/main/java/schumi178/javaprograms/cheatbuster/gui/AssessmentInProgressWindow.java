package schumi178.javaprograms.cheatbuster.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class AssessmentInProgressWindow implements Initializable {

    @FXML
    private Stage stage;
    @FXML
    private Label currentStageLabel;
    @FXML
    private Label currentHalfLabel;
    @FXML
    private ProgressBar assessmentProgress;
    @FXML
    private VBox vBox;

    private boolean doInterrupt = false;

    public void onCancel() {
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    public ProgressBar getProgressBar() {
        return assessmentProgress;
    }

    public Label getCurrentStageLabel() {
        return currentStageLabel;
    }

    private void closeEvent(WindowEvent event) {
        doInterrupt = true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeEvent);
    }

    public VBox getVBox() {
        return vBox;
    }

    public Label getCurrentHalfLabel() {
        return currentHalfLabel;
    }

    public boolean willInterrupt() {
        return doInterrupt;
    }
}
