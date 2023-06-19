package schumi178.javaprograms.cheatbuster.gui.options;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Map;

public interface OptionsChangeProvider {
    Stage getParentStage();
    void setParentStage(Stage stage);
    String getConfigFileContent();
    String getConfigFileName();
    Pane getContent();
}
