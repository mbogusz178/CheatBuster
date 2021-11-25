module cheatbuster.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires cheatbuster.core;
    requires antlr4.runtime;
    requires org.jfxtras.styles.jmetro;

    exports schumi178.javaprograms.cheatbuster.gui to javafx.graphics, cheatbuster.core;
}