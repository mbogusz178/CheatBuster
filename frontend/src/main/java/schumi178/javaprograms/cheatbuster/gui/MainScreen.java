package schumi178.javaprograms.cheatbuster.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
import schumi178.javaprograms.cheatbuster.code.base.*;
import schumi178.javaprograms.cheatbuster.code.base.languages.CLang;
import schumi178.javaprograms.cheatbuster.code.exception.DoesNotCompileException;
import schumi178.javaprograms.cheatbuster.file.StringParser;
import schumi178.javaprograms.cheatbuster.filechooser.CFileChooserProvider;
import schumi178.javaprograms.cheatbuster.filechooser.FileChooserProvider;
import schumi178.javaprograms.cheatbuster.gui.util.ErrorIconProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MainScreen implements Initializable {

    @FXML
    private Stage stage;

    @FXML
    private TextArea leftTextArea;

    private final StringProperty leftFileName = new SimpleStringProperty("");

    @FXML
    private TextArea rightTextArea;

    private final StringProperty rightFileName = new SimpleStringProperty("");

    @FXML
    private BorderPane borderPane;
    @FXML
    private ScrollPane statusTextPane;
    @FXML
    private ImageView refreshIcon;
    @FXML
    private ImageView statusIcon;
    @FXML
    private HBox statusBar;
    @FXML
    private HBox refreshIconBackground;
    @FXML
    private Text statusText;

    private boolean refreshButtonPressed = false;

    @FXML
    private Menu languageActions;

    private final ErrorIconProvider provider = ErrorIconProvider.getProvider();
    private final ProgrammingLanguageFactory factory = new ProgrammingLanguageFactory();

    private class LanguageActionsListener implements ChangeListener<Boolean> {

        private final TextArea textArea;
        private final CodeTextField field;

        public LanguageActionsListener(CodeTextField field) {
            this.textArea = field == CodeTextField.LEFT ? leftTextArea : rightTextArea;
            this.field = field;
        }

        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
            if(t1) {
                String fileName = field == CodeTextField.LEFT ? leftFileName.get() : rightFileName.get();
                if(fileName.isEmpty()) {
                    languageActions.setVisible(false);
                } else {
                    ProgrammingLanguage lang = factory.getLanguage(fileName);
                    languageActions.setText(lang.getName());
                    languageActions.getItems().clear();
                    for(LanguageAction action: lang.getActions()) {
                        MenuItem item = new MenuItem(action.getName());
                        item.setOnAction(event -> {
                            List<String> includePaths = new ArrayList<>();
                            try {
                                Scanner sc = new Scanner(new File("config/include.cfg"));
                                while(sc.hasNextLine()) {
                                    includePaths.add(sc.nextLine());
                                }
                                sc.close();
                            } catch (FileNotFoundException ignored) {

                            }
                            String error = action.getAction().apply(textArea.getText(), includePaths);
                            if(!error.isEmpty()) {
                                Alert alert = new Alert(Alert.AlertType.ERROR, error, ButtonType.OK);
                                alert.setTitle("Błąd");
                                alert.setHeaderText("Błąd akcji");
                                alert.showAndWait();
                            }
                        });
                        languageActions.getItems().add(item);
                    }
                    languageActions.setVisible(true);
                }
            }
        }
    }

    private void updateWindowTitle(String newLeft, String newRight) {
        String optionalComma = newLeft.equals("") || newRight.equals("") ? "" : ", ";
        stage.setTitle(newLeft + optionalComma + newRight + " - CheatBuster");
    }

    private void refreshStatusBar(String leftCode, String rightCode) {
        ProgrammingLanguage leftLanguage = factory.getLanguage(leftFileName.get());
        String errorsLeft = null;
        if(leftLanguage != null)
            errorsLeft = leftLanguage.getErrors(leftCode);
        ProgrammingLanguage rightLanguage = factory.getLanguage(rightFileName.get());
        String errorsRight = null;
        if(rightLanguage != null)
            errorsRight = rightLanguage.getErrors(rightCode);
        StringBuilder message = new StringBuilder();
        if(errorsLeft != null) {
            message.append("Plik po lewej zawiera błędy:\n").append(errorsLeft).append("\n\n");
        }
        if(errorsRight != null) {
            message.append("Plik po prawej zawiera błędy:\n").append(errorsRight).append("\n\n");
        }
        if(message.toString().equals("")) {
            statusIcon.setImage(provider.getSuccessIcon());
            statusText.setText("Gotowy");
        } else {
            statusIcon.setImage(provider.getErrorIcon());
            String result = message.toString();
            statusText.setText(result.substring(0, result.length() - 2));
        }
    }

    @FXML
    private void refreshButtonPressed() {
        refreshButtonPressed = true;
    }

    @FXML
    private void refreshButtonReleased(MouseEvent event) {
        boolean hasMovedAway = event.getX() < 0 || event.getY() < 0 || event.getX() >= 32 || event.getY() >= 32;
        if(refreshButtonPressed && !hasMovedAway) {
            refreshStatusBar(leftTextArea.getText(), rightTextArea.getText());
        }
        refreshButtonPressed = false;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statusText.wrappingWidthProperty().bind(statusTextPane.widthProperty());
        statusBar.getStylesheets().add("fxml/statusBar.css");
        statusBar.getStyleClass().add("hboxOverridden");
        refreshIconBackground.getStyleClass().add("barButton");

        Tooltip.install(refreshIcon, new Tooltip("Odśwież"));
        refreshIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/repeat-1.png"))));
        statusIcon.setImage(provider.getSuccessIcon());

        factory.registerLanguage(new CLang());
        HBox.setHgrow(statusTextPane, Priority.ALWAYS);

        leftTextArea.focusedProperty().addListener(new LanguageActionsListener(CodeTextField.LEFT));
        rightTextArea.focusedProperty().addListener(new LanguageActionsListener(CodeTextField.RIGHT));

        leftTextArea.textProperty().addListener((observable, oldValue, newValue) -> refreshStatusBar(newValue, rightTextArea.getText()));
        rightTextArea.textProperty().addListener((observable, oldValue, newValue) -> refreshStatusBar(leftTextArea.getText(), newValue));

        leftFileName.addListener((observable, oldValue, newValue) -> updateWindowTitle(newValue, rightFileName.get()));
        rightFileName.addListener((observable, oldValue, newValue) -> updateWindowTitle(leftFileName.get(), newValue));
    }

    private enum CodeTextField {
        LEFT, RIGHT
    }

    private void openFile(CodeTextField field) {
        TextArea area = field == CodeTextField.LEFT ? leftTextArea : rightTextArea;
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
            if(field == CodeTextField.LEFT) {
                leftFileName.set(file.getName());
            } else {
                rightFileName.set(file.getName());
            }
        } catch (IOException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Nie znaleziono pliku albo plik jest uszkodzony.", ButtonType.OK);
            alert.showAndWait();
        }
        refreshStatusBar(leftTextArea.getText(), rightTextArea.getText());
    }

    @FXML
    public void openLeftFile() {
        openFile(CodeTextField.LEFT);
    }

    @FXML
    public void openRightFile() {
        openFile(CodeTextField.RIGHT);
    }

    @FXML
    public void openAboutWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/aboutScreen.fxml"));
        Stage aboutScreen = loader.load();
        aboutScreen.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/logo.png"))));
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
    public void openHowToAlert() {
        List<ProgrammingLanguage> languages = factory.getAllLanguages();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.setHeaderText("Jak używać programu?");
        alert.setTitle("Pomoc");

        StringBuilder contentTextBuilder = new StringBuilder();

        contentTextBuilder.append("Załaduj pliki, które chcesz porównać, poprzez opcję Plik > Otwórz plik 1 i Plik > Otwórz plik 2. Następnie postępuj zgodnie z wytycznymi dla poszczególnych języków programowania:");

        for(ProgrammingLanguage language: languages) {
            contentTextBuilder.append("\n\n").append(language.getName()).append(":\n").append(language.getHelp());
        }

        alert.setContentText(contentTextBuilder.toString());
        alert.showAndWait();
    }

    @FXML
    private void onOptionsClick() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/optionsScreen.fxml"));
        try {
            Stage stage = loader.load();
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/logo.png"))));
            JMetro jMetro = new JMetro(Style.DARK);
            jMetro.setScene(stage.getScene());
            OptionsScreen controller = loader.getController();
            controller.getVBox().getStyleClass().add(JMetroStyleClass.BACKGROUND);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void compare() throws IOException {
        FXMLLoader waitingWindowLoader = new FXMLLoader(getClass().getResource("/fxml/assessmentInProgress.fxml"));
        Stage waitingWindow = waitingWindowLoader.load();
        AssessmentInProgressWindow waitingWindowController = waitingWindowLoader.getController();
        waitingWindow.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/logo.png"))));
        JMetro jMetro = new JMetro(Style.DARK);
        jMetro.setScene(waitingWindow.getScene());
        waitingWindowController.getVBox().getStyleClass().add(JMetroStyleClass.BACKGROUND);

        ProgressBar progressBar = waitingWindowController.getProgressBar();
        progressBar.setProgress(0);

        Label currentStageLabel = waitingWindowController.getCurrentStageLabel();
        currentStageLabel.setText("Przygotowywanie oceniania");

        Label currentHalfLabel = waitingWindowController.getCurrentHalfLabel();
        currentHalfLabel.setText("Kod pierwszy");

        FXMLLoader stageLoader = new FXMLLoader(getClass().getResource("/fxml/resultScreen.fxml"));
        Stage resultStage = stageLoader.load();

        waitingWindow.show();

        CompletableFuture.runAsync(() -> {
            ProgrammingLanguage lang = factory.getLanguage(leftFileName.get());
            if (lang == null || factory.getLanguage(rightFileName.get()) == null) {
                Platform.runLater(() -> {
                    waitingWindow.close();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Nie rozpoznano języka w jednym z pól. Załaduj plik z kodem, aby kontynuować.", ButtonType.OK);
                    alert.setTitle("Błąd");
                    alert.setHeaderText("Błąd programu");
                    alert.showAndWait();
                });
                return;
            }
            if (lang.getClass() != factory.getLanguage(rightFileName.get()).getClass()) {
                Platform.runLater(() -> {
                    waitingWindow.close();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Języki poszczególnych kodów różnią się między sobą.", ButtonType.OK);
                    alert.setTitle("Błąd");
                    alert.setHeaderText("Błąd programu");
                    alert.showAndWait();
                });
                return;
            }

            List<String> includePaths = new ArrayList<>();
            try {
                Scanner sc = new Scanner(new File("config/include.cfg"));
                while (sc.hasNextLine()) {
                    includePaths.add(sc.nextLine());
                }
                sc.close();
            } catch (FileNotFoundException ignored) {

            }
            String preprocessedLeftText;
            try {
                preprocessedLeftText = lang.preprocess(leftTextArea.getText(), includePaths);
            } catch (DoesNotCompileException e) {
                Platform.runLater(() -> {
                    waitingWindow.close();
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                    alert.setTitle("Błąd");
                    alert.setHeaderText("Błąd preprocesora");
                    alert.showAndWait();
                });
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
            List<ParseTreeListener> listeners;
            try {
                listeners = lang.getListeners(preprocessedLeftText, "cache/" + lang.getName() + "/includedTypes");
            } catch (DoesNotCompileException e) {
                Platform.runLater(() -> {
                    waitingWindow.close();
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                    alert.setTitle("Błąd");
                    alert.setHeaderText("Błąd preprocesora");
                    alert.showAndWait();
                });
                return;
            }
            int listenersSize = listeners.size();
            for(int i = 0; i < listenersSize; i++) {
                ParseTreeListener listener = listeners.get(i);
                int finalI = i;
                Platform.runLater(() -> {
                    progressBar.setProgress((double) finalI / listenersSize);
                    currentStageLabel.setText(((NamedListener) listener).getName() + " (przetwarzanie drzewa wywodu)");
                });
                walker.walk(listener, tree);
                if(waitingWindowController.willInterrupt()) {
                    return;
                }
            }
            List<ParseTreeListener> fileProcessableListeners = listeners.stream().filter(listener -> listener instanceof FileProcessable).collect(Collectors.toList());
            for(int i = 0; i < fileProcessableListeners.size(); i++) {
                ParseTreeListener listener = fileProcessableListeners.get(i);
                if (listener instanceof FileProcessable) {
                    int finalI = i;
                    Platform.runLater(() -> {
                        progressBar.setProgress((double) finalI / listenersSize);
                        currentStageLabel.setText(((NamedListener) listener).getName() + " (przetwarzanie pliku kodu)");
                    });
                    ((FileProcessable) listener).process(leftTextArea.getText(), rightTextArea.getText());
                    if(waitingWindowController.willInterrupt()) {
                        return;
                    }
                }
            }

            Platform.runLater(() -> {
                currentHalfLabel.setText("Kod drugi");
                currentStageLabel.setText("Przygotowywanie oceniania");
                progressBar.setProgress(0);
            });

            if(waitingWindowController.willInterrupt()) {
                return;
            }

            String preprocessedRightText;
            try {
                preprocessedRightText = lang.preprocess(rightTextArea.getText(), includePaths);
            } catch (DoesNotCompileException e) {
                Platform.runLater(() -> {
                    waitingWindow.close();
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                    alert.setTitle("Błąd");
                    alert.setHeaderText("Błąd preprocesora");
                    alert.showAndWait();
                });
                return;
            }

            CharStream charStream1 = CharStreams.fromString(preprocessedRightText);
            Lexer lexer1 = lang.getLexer(charStream1);
            lexer1.setTokenFactory(new CommonTokenFactory(true));
            TokenStream tokenStream1 = new UnbufferedTokenStream<CommonToken>(lexer1);
            CompileReadyParser parser1 = lang.getParser(tokenStream1);
            ParseTree tree1 = parser1.walkTree();
            ParseTreeWalker walker1 = new ParseTreeWalker();
            List<ParseTreeListener> listeners1;
            try {
                listeners1 = lang.getListeners(preprocessedRightText, "cache/" + lang.getName() + "/includedTypes");
            } catch (DoesNotCompileException e) {
                Platform.runLater(() -> {
                    waitingWindow.close();
                    Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                    alert.setTitle("Błąd");
                    alert.setHeaderText("Błąd preprocesora");
                    alert.showAndWait();
                });
                return;
            }
            int listenersSize1 = listeners1.size();
            for(int i = 0; i < listenersSize1; i++) {
                ParseTreeListener listener = listeners1.get(i);
                int finalI = i;
                Platform.runLater(() -> {
                    progressBar.setProgress((double) finalI / listenersSize1);
                    currentStageLabel.setText(((NamedListener) listener).getName() + " (przetwarzanie drzewa wywodu)");
                });
                walker1.walk(listener, tree1);
                if(waitingWindowController.willInterrupt()) {
                    return;
                }
            }
            List<ParseTreeListener> fileProcessableListeners1 = listeners1.stream().filter(listener -> listener instanceof FileProcessable).collect(Collectors.toList());
            for(int i = 0; i < fileProcessableListeners1.size(); i++) {
                ParseTreeListener listener = fileProcessableListeners1.get(i);
                if (listener instanceof FileProcessable) {
                    int finalI = i;
                    Platform.runLater(() -> {
                        progressBar.setProgress((double) finalI / listenersSize1);
                        currentStageLabel.setText(((NamedListener) listener).getName() + " (przetwarzanie pliku kodu)");
                    });
                    ((FileProcessable) listener).process(rightTextArea.getText(), leftTextArea.getText());
                    if(waitingWindowController.willInterrupt()) {
                        return;
                    }
                }
            }

            AssessmentResult result = lang.assess(listeners, listeners1);

            if(waitingWindowController.willInterrupt()) {
                return;
            }

            Platform.runLater(() -> {
                resultStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/logo.png"))));
                ResultScreen resultScreen = stageLoader.getController();
                resultStage.setTitle("Wynik");
                HBox hBox = resultScreen.getResultSpace();
                Iterator<Assessable> resultListenerIterator = result.resultIterator();
                Iterator<Double> resultIterator = result.resultDoubleIterator();
                while(resultListenerIterator.hasNext() && resultIterator.hasNext()) {
                    Assessable current = resultListenerIterator.next();
                    double currentResult = resultIterator.next();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/singleResult.fxml"));
                    VBox resultVBox;
                    try {
                        resultVBox = loader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    hBox.getChildren().add(resultVBox);
                    SingleResult resultController = loader.getController();
                    resultController.setRating((int)currentResult);
                    resultController.setListenerName(current.getName());
                    resultController.setResultText(current.resultToString());
                    resultScreen.addController(resultController);
                    resultStage.addEventHandler(WindowEvent.WINDOW_SHOWN, event -> resultController.onShown());
                }
                JMetro jMetro2 = new JMetro(Style.DARK);
                jMetro2.setScene(resultStage.getScene());
                resultScreen.getVBox().getStyleClass().add(JMetroStyleClass.BACKGROUND);
                resultScreen.getResultVBox().getStyleClass().add(JMetroStyleClass.BACKGROUND);
                resultScreen.setFinalRating(result.getFinalResult());
                resultScreen.setCodeInfo(leftTextArea.getText(), leftFileName.get(), rightTextArea.getText(), rightFileName.get());
                if(waitingWindowController.willInterrupt()) {
                    return;
                }
                resultStage.show();
                waitingWindow.close();
            });
        });
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
