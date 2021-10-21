package schumi178.javaprograms.cheatbuster.gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import schumi178.javaprograms.cheatbuster.gui.options.OptionsCategory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OptionsScreen implements Initializable {

    @FXML
    private Stage stage;

    @FXML
    private ListView<OptionsCategory> categoryList;

    @FXML
    private ScrollPane optionsContent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryList.setItems(FXCollections.observableArrayList(new OptionsCategory("Preprocesor", "preprocessor")));
        categoryList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/options/" + newValue.getFxmlName() + ".fxml"));
            try {
                Pane content = loader.load();
                StageController controller = loader.getController();
                controller.setStage(stage);
                optionsContent.setContent(content);
                stage.setWidth(content.getPrefWidth() + categoryList.getPrefWidth());
                stage.setHeight(content.getPrefHeight());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        categoryList.getSelectionModel().select(0);
    }
}
