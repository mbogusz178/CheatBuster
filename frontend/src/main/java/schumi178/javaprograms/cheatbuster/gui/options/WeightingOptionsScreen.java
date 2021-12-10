package schumi178.javaprograms.cheatbuster.gui.options;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import schumi178.javaprograms.cheatbuster.gui.util.DoubleTextFormatter;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;

public class WeightingOptionsScreen implements Initializable, OptionsChangeProvider {

    private Stage stage;

    @FXML
    private GridPane content;
    @FXML
    private TextField variableWeight;
    @FXML
    private TextField typeWeight;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        variableWeight.setTextFormatter(new DoubleTextFormatter());
        typeWeight.setTextFormatter(new DoubleTextFormatter());
        try {
            Scanner scanner = new Scanner(new File("config/weights.cfg"));
            Locale locale = Locale.getDefault();
            NumberFormat format = NumberFormat.getInstance(locale);
            variableWeight.setText(format.format(scanner.nextDouble()));
            typeWeight.setText(format.format(scanner.nextDouble()));
            scanner.close();
        } catch (Exception e) {
            Locale locale = Locale.getDefault();
            NumberFormat format = NumberFormat.getInstance(locale);
            variableWeight.setText(format.format(3.0));
            typeWeight.setText(format.format(1.5));
        }
    }

    @Override
    public Stage getParentStage() {
        return stage;
    }

    @Override
    public void setParentStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public String getConfigFileContent() {
        return variableWeight.getText() + "\n" + typeWeight.getText();
    }

    @Override
    public String getConfigFileName() {
        return "weights";
    }

    @Override
    public Pane getContent() {
        return content;
    }
}
