package schumi178.javaprograms.cheatbuster.gui.util;

import javafx.scene.image.Image;

import java.util.Objects;

public class ErrorIconProvider {
    private Image errorIcon;
    private Image successIcon;
    private static ErrorIconProvider instance;

    private ErrorIconProvider() {

    }

    public static ErrorIconProvider getProvider() {
        if(instance == null)
            instance = new ErrorIconProvider();
        return instance;
    }

    public Image getErrorIcon() {
        if(errorIcon == null)
            errorIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/error.png")));
        return errorIcon;
    }

    public Image getSuccessIcon() {
        if(successIcon == null)
            successIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/success.png")));
        return successIcon;
    }
}
