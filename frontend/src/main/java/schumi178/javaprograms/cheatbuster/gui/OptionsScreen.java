package schumi178.javaprograms.cheatbuster.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import schumi178.javaprograms.cheatbuster.gui.options.OptionsCategory;
import schumi178.javaprograms.cheatbuster.gui.options.OptionsChangeProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;

public class OptionsScreen implements Initializable {

    @FXML
    private Stage stage;

    @FXML
    private VBox vBox;

    @FXML
    private ListView<OptionsCategory> categoryList;

    @FXML
    private ScrollPane optionsContent;

    private final Map<String, OptionsChangeProvider> controllers = new HashMap<>();
    private final List<String> ids = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryList.setItems(FXCollections.observableArrayList(new OptionsCategory("Preprocesor", "preprocessor"),
                new OptionsCategory("Wagi", "weighting")));
        categoryList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(ids.contains(newValue.getFxmlName())) {
                Pane content = controllers.get(newValue.getFxmlName()).getContent();
                optionsContent.setContent(content);
                if(content.getPrefWidth() == Region.USE_COMPUTED_SIZE) {
                    stage.setWidth(500 + categoryList.getPrefWidth());
                } else {
                    stage.setWidth(content.getPrefWidth() + categoryList.getPrefWidth());
                }
                stage.setHeight(content.getPrefHeight());
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/options/" + newValue.getFxmlName() + ".fxml"));
                try {
                    Pane content = loader.load();
                    OptionsChangeProvider controller = loader.getController();
                    controller.setParentStage(stage);
                    optionsContent.setContent(content);
                    if(content.getPrefWidth() == Region.USE_COMPUTED_SIZE) {
                        stage.setWidth(500 + categoryList.getPrefWidth());
                    } else {
                        stage.setWidth(content.getPrefWidth() + categoryList.getPrefWidth());
                    }
                    stage.setHeight(content.getPrefHeight());
                    ids.add(newValue.getFxmlName());
                    controllers.put(newValue.getFxmlName(), controller);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        categoryList.getSelectionModel().select(0);
    }

    private void saveChanges() {
        for(OptionsChangeProvider controller: controllers.values()) {
            try {
                File file = new File("config/" + controller.getConfigFileName() + ".cfg");
                //noinspection ResultOfMethodCallIgnored
                file.getParentFile().mkdirs();
                PrintWriter writer = new PrintWriter(file);
                writer.write(controller.getConfigFileContent());
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public VBox getVBox() {
        return vBox;
    }

    @FXML
    public void onOKClick() {
        saveChanges();
        stage.close();
    }

    @FXML
    public void onCancelClick() {
        stage.close();
    }

    @FXML
    public void onApplyClick() {
        saveChanges();
    }
}
