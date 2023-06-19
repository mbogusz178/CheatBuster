package schumi178.javaprograms.cheatbuster.filechooser;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class CFileChooserProvider implements FileChooserProvider {
    @Override
    public FileChooser getFileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Otwórz plik");
        chooser.getExtensionFilters().setAll(
                new ExtensionFilter("Kod w języku C (*.c)", "*.c"),
                new ExtensionFilter("Nagłówek w języku C (*.h)", "*.h")
        );
        return chooser;
    }
}
