package schumi178.javaprograms.cheatbuster.code.exception;

import java.io.File;

public class IncludeNotFoundException extends DoesNotCompileException {

    private final String filename;

    public IncludeNotFoundException(String filename) {
        super("Nie znaleziono pliku " + filename + ". Pozostałe pliki nagłówkowe zostaną przetworzone, jeśli zostaną znalezione.");
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
