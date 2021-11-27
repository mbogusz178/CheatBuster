package schumi178.javaprograms.cheatbuster;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;
import schumi178.javaprograms.cheatbuster.gui.MainScreen;

import java.util.Objects;

public class CheatBuster extends Application {

    private static HostServices hostServices;

    public static void startApp(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        hostServices = getHostServices();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainScreen.fxml"));
        primaryStage = loader.load();
        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setScene(primaryStage.getScene());
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/logo.png"))));
        MainScreen controller = loader.getController();
        controller.getBorderPane().getStyleClass().add(JMetroStyleClass.BACKGROUND);
        primaryStage.show();
    }

    public static void showWebsite(String url) {
        hostServices.showDocument(url);
    }
}
