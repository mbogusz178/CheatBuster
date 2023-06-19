package schumi178.javaprograms.cheatbuster.file;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class StringParser {

    public static String getStringFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        byte[] bytes = is.readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
