package schumi178.javaprograms.cheatbuster.code.base;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgrammingLanguageFactory {
    private static final Map<String, Class<? extends ProgrammingLanguage>> registeredLanguages = new HashMap<>();

    public void registerLanguage(ProgrammingLanguage lang) {
        registeredLanguages.put(lang.getFileExtension(), lang.getClass());
    }

    public List<ProgrammingLanguage> getAllLanguages() {
        List<ProgrammingLanguage> languages = new ArrayList<>();
        for(Class<? extends ProgrammingLanguage> clazz: registeredLanguages.values()) {
            try {
                languages.add(clazz.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return languages;
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
