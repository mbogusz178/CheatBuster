module cheatbuster.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.swing;
    requires java.desktop;
    requires cheatbuster.core;
    requires antlr4.runtime;
    requires org.jfxtras.styles.jmetro;
    requires com.github.librepdf.openpdf;

    exports schumi178.javaprograms.cheatbuster.gui to javafx.graphics, cheatbuster.core;
}