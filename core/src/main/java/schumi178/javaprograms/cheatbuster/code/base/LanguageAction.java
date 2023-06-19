package schumi178.javaprograms.cheatbuster.code.base;

import java.util.List;
import java.util.function.BiFunction;

public class LanguageAction {
    private final String name;
    private final BiFunction<String, List<String>, String> action;

    public LanguageAction(String name, BiFunction<String, List<String>, String> action) {
        this.name = name;
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public BiFunction<String, List<String>, String> getAction() {
        return action;
    }
}
