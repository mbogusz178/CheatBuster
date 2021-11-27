package schumi178.javaprograms.cheatbuster.code.base;

import schumi178.javaprograms.cheatbuster.code.base.languages.CLang;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ProgrammingLanguageFactory {
    private static final Map<String, Class<? extends ProgrammingLanguage>> registeredLanguages = new HashMap<>();

    public void registerLanguage(ProgrammingLanguage lang) {
        registeredLanguages.put(lang.getFileExtension(), lang.getClass());
    }

    public ProgrammingLanguage getLanguage(String fileName) {
        for(String extension: registeredLanguages.keySet()) {
            if(fileName.endsWith(extension)) {
                try {
                    return registeredLanguages.get(extension).getDeclaredConstructor().newInstance();
                } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }
}
