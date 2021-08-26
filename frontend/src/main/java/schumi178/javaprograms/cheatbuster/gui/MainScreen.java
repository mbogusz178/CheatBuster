package schumi178.javaprograms.cheatbuster.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import schumi178.javaprograms.cheatbuster.code.base.CompileReadyParser;
import schumi178.javaprograms.cheatbuster.code.base.ProgrammingLanguage;
import schumi178.javaprograms.cheatbuster.file.StringParser;
import schumi178.javaprograms.cheatbuster.filechooser.CFileChooserProvider;
import schumi178.javaprograms.cheatbuster.filechooser.FileChooserProvider;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MainScreen {

    @FXML
    private Stage stage;

    @FXML
    private TextArea leftTextArea;

    @FXML
    private TextArea rightTextArea;

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
        aboutScreen.show();
    }

    @FXML
    public void close() {
        stage.close();
    }

    @FXML
    public void compare() {
        ProgrammingLanguage lang = ProgrammingLanguage.getLanguage();
        CharStream charStream = CharStreams.fromString(leftTextArea.getText());
        Lexer lexer = lang.getLexer(charStream);
        TokenStream tokenStream = new CommonTokenStream(lexer);
        CompileReadyParser parser = lang.getParser(tokenStream);
        if(!(parser instanceof Parser)) {
            throw new ClassCastException("The parser must be an instance of Parser");
        }
        ParseTree tree = parser.walkTree();
        ParseTreeWalker walker = new ParseTreeWalker();
        List<ParseTreeListener> listeners = lang.getListeners();
        listeners.forEach(listener -> walker.walk(listener, tree));
        int result = lang.assess(listeners);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Twój wynik to " + result + "!", ButtonType.OK);
        alert.showAndWait();
    }
}
