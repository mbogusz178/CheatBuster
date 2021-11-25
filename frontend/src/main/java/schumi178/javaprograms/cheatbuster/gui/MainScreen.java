package schumi178.javaprograms.cheatbuster.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import schumi178.javaprograms.cheatbuster.code.AssessmentResult;
import schumi178.javaprograms.cheatbuster.code.base.Assessable;
import schumi178.javaprograms.cheatbuster.code.base.CompileReadyParser;
import schumi178.javaprograms.cheatbuster.code.base.FileProcessable;
import schumi178.javaprograms.cheatbuster.code.base.ProgrammingLanguage;
import schumi178.javaprograms.cheatbuster.code.exception.DoesNotCompileException;
import schumi178.javaprograms.cheatbuster.file.StringParser;
import schumi178.javaprograms.cheatbuster.filechooser.CFileChooserProvider;
import schumi178.javaprograms.cheatbuster.filechooser.FileChooserProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class MainScreen {

    @FXML
    private Stage stage;

    @FXML
    private TextArea leftTextArea;

    @FXML
    private TextArea rightTextArea;

    @FXML
    private BorderPane borderPane;

    private void openFile(TextArea area) {
        FileChooserProvider provider = new CFileChooserProvider();
        FileChooser chooser = provider.getFileChooser();
        Optional<File> selectedFile = Optional.ofNullable(chooser.showOpenDialog(stage));
        if(selectedFile.isEmpty()) {
            return;
        }
        File file = selectedFile.get();
        try {
            String text = StringParser.getStringFromFile(file);
            area.setText(text);
        } catch (IOException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Nie znaleziono pliku albo plik jest uszkodzony.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    @FXML
    public void openLeftFile() {
        openFile(leftTextArea);
    }

    @FXML
    public void openRightFile() {
        openFile(rightTextArea);
    }

    @FXML
    public void openAboutWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/aboutScreen.fxml"));
        Stage aboutScreen = loader.load();
        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setScene(aboutScreen.getScene());
        AboutScreen controller = loader.getController();
        controller.getVBox().getStyleClass().add(JMetroStyleClass.BACKGROUND);
        aboutScreen.show();
    }

    @FXML
    public void close() {
        stage.close();
    }

    @FXML
    private void onOptionsClick() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/optionsScreen.fxml"));
        try {
            Stage stage = loader.load();
            JMetro jMetro = new JMetro(Style.DARK);
            jMetro.setScene(stage.getScene());
            OptionsScreen controller = loader.getController();
            controller.getHBox().getStyleClass().add(JMetroStyleClass.BACKGROUND);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void compare() throws IOException {
        ProgrammingLanguage lang = ProgrammingLanguage.getLanguage();

        List<String> includePaths = new ArrayList<>();
        try {
            Scanner sc = new Scanner(new File("config/include.cfg"));
            while(sc.hasNextLine()) {
                includePaths.add(sc.nextLine());
            }
        } catch (FileNotFoundException ignored) {

        }
        String preprocessedLeftText;
        try {
            preprocessedLeftText = lang.preprocess(leftTextArea.getText(), includePaths);
        } catch (DoesNotCompileException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.setTitle("Błąd");
            alert.setHeaderText("Błąd preprocesora");
            alert.showAndWait();
            return;
        }
        try {
            PrintWriter test = new PrintWriter("preprocessed.c");
            test.write(preprocessedLeftText);
            test.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        File file = new File("test.c");
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.write(preprocessedLeftText);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        CharStream charStream = CharStreams.fromString(preprocessedLeftText);
        Lexer lexer = lang.getLexer(charStream);
        lexer.setTokenFactory(new CommonTokenFactory(true));
        TokenStream tokenStream = new UnbufferedTokenStream<CommonToken>(lexer);
        CompileReadyParser parser = lang.getParser(tokenStream);
        ParseTree tree = parser.walkTree();
        ParseTreeWalker walker = new ParseTreeWalker();
        List<ParseTreeListener> listeners = lang.getListeners();
        listeners.forEach(listener -> walker.walk(listener, tree));
        listeners.forEach(listener -> {
            if(listener instanceof FileProcessable) {
                ((FileProcessable)listener).process(leftTextArea.getText(), rightTextArea.getText());
            }
        });

        String preprocessedRightText;
        try {
            preprocessedRightText = lang.preprocess(rightTextArea.getText(), includePaths);
        } catch (DoesNotCompileException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.setTitle("Błąd");
            alert.setHeaderText("Błąd preprocesora");
            alert.showAndWait();
            return;
        }

        CharStream charStream1 = CharStreams.fromString(preprocessedRightText);
        Lexer lexer1 = lang.getLexer(charStream1);
        lexer1.setTokenFactory(new CommonTokenFactory(true));
        TokenStream tokenStream1 = new UnbufferedTokenStream<CommonToken>(lexer1);
        CompileReadyParser parser1 = lang.getParser(tokenStream1);
        ParseTree tree1 = parser1.walkTree();
        ParseTreeWalker walker1 = new ParseTreeWalker();
        List<ParseTreeListener> listeners1 = lang.getListeners();
        listeners1.forEach(listener -> walker1.walk(listener, tree1));
        listeners1.forEach(listener -> {
            if(listener instanceof FileProcessable) {
                ((FileProcessable)listener).process(rightTextArea.getText(), leftTextArea.getText());
            }
        });

        AssessmentResult result = lang.assess(listeners, listeners1);
        FXMLLoader stageLoader = new FXMLLoader(getClass().getResource("/fxml/resultScreen.fxml"));
        Stage stage = stageLoader.load();
        ResultScreen resultScreen = stageLoader.getController();
        stage.setTitle("Wynik");
        HBox hBox = resultScreen.getResultSpace();
        Iterator<Assessable> resultListenerIterator = result.resultIterator();
        Iterator<Double> resultIterator = result.resultDoubleIterator();
        while(resultListenerIterator.hasNext() && resultIterator.hasNext()) {
            Assessable current = resultListenerIterator.next();
            double currentResult = resultIterator.next();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/singleResult.fxml"));
            VBox resultVBox = loader.load();
            hBox.getChildren().add(resultVBox);
            SingleResult resultController = loader.getController();
            resultController.setRating((int)currentResult);
            resultController.setListenerName(current.getName());
            resultController.setResultText(current.resultToString());
            resultScreen.addController(resultController);
            stage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> resultController.onShown());
        }
        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setScene(stage.getScene());
        resultScreen.getVBox().getStyleClass().add(JMetroStyleClass.BACKGROUND);
        resultScreen.getResultVBox().getStyleClass().add(JMetroStyleClass.BACKGROUND);
        resultScreen.setFinalRating(result.getFinalResult());
        stage.show();
    }

    public BorderPane getBorderPane() {
        return borderPane;
    }

    public TextArea getLeftTextArea() {
        return leftTextArea;
    }

    public TextArea getRightTextArea() {
        return rightTextArea;
    }
}
