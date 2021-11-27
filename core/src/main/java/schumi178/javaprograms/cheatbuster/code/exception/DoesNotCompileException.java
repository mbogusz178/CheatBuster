package schumi178.javaprograms.cheatbuster.code.exception;

import java.io.File;

public class DoesNotCompileException extends Exception {

    public DoesNotCompileException(String detailedError) {
        super("Kod się nie skompiluje." + detailedError);
    }
}
