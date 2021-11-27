module cheatbuster.core {
    requires antlr4.runtime;
    requires kotlin.stdlib;
    requires io.github.javadiffutils;
    requires java.logging;
    exports schumi178.javaprograms.cheatbuster.code to cheatbuster.frontend;
    exports schumi178.javaprograms.cheatbuster.file to cheatbuster.frontend;
    exports schumi178.javaprograms.cheatbuster.code.listeners to cheatbuster.frontend;
    exports schumi178.javaprograms.cheatbuster.code.base to cheatbuster.frontend;
    exports schumi178.javaprograms.cheatbuster.code.base.languages to cheatbuster.frontend;
    exports schumi178.javaprograms.cheatbuster.code.exception to cheatbuster.frontend;
    exports schumi178.javaprograms.cheatbuster.util to cheatbuster.frontend;
}