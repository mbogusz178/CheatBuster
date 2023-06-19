package schumi178.javaprograms.cheatbuster.gui.options;

public class OptionsCategory {
    private final String categoryName;
    private final String fxmlName;

    public OptionsCategory(String categoryName, String fxmlName) {
        this.categoryName = categoryName;
        this.fxmlName = fxmlName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getFxmlName() {
        return fxmlName;
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
