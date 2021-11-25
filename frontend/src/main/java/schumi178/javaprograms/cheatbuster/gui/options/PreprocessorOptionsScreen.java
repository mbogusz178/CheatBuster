package schumi178.javaprograms.cheatbuster.gui.options;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import schumi178.javaprograms.cheatbuster.gui.StageController;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class PreprocessorOptionsScreen implements Initializable, StageController {

    @FXML
    private ListView<String> includeList;

    private Stage stage;
    private final ObservableList<String> includeItems = FXCollections.observableArrayList();

    @FXML
    private void browseIncludeDirectories() {
        DirectoryChooser chooser = new DirectoryChooser();
        File dir = chooser.showDialog(stage);
        if(dir != null) {
            includeItems.add(dir.getAbsolutePath());
        }
    }

    @FXML
    private void onKeyPressed(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.DELETE) {
            String toDelete = includeList.getSelectionModel().getSelectedItem();
            includeItems.remove(toDelete);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        includeList.setItems(includeItems);
        try {
            Scanner scanner = new Scanner(new File("config/include.cfg"));
            while(scanner.hasNextLine()) {
                includeItems.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException ignored) {

        }
        includeItems.addListener((ListChangeListener<String>) change -> {
            File file = new File("config/include.cfg");
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();
            try {
                PrintWriter writer = new PrintWriter(file);
                for(String include: includeItems) {
                    writer.write(include + "\n");
                }
                writer.close();
            } catch (FileNotFoundException ignored) {

            }
        });
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
