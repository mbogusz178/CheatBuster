package schumi178.javaprograms.cheatbuster;

import com.sun.javafx.application.HostServicesDelegate;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class CheatBuster extends Application {

    private static HostServicesDelegate hostServices;

    public static void startApp(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        hostServices = HostServicesDelegate.getInstance(this);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainScreen.fxml"));
        primaryStage = loader.load();
        primaryStage.show();
    }

    public static void showWebsite(String url) {
        hostServices.showDocument(url);
    }
}
