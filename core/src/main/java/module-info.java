module cheatbuster.core {
    requires antlr4.runtime;
    requires kotlin.stdlib;
    exports schumi178.javaprograms.cheatbuster.code to cheatbuster.frontend;
    exports schumi178.javaprograms.cheatbuster.file to cheatbuster.frontend;
    exports schumi178.javaprograms.cheatbuster.code.listeners to cheatbuster.frontend;
    exports schumi178.javaprograms.cheatbuster.code.base to cheatbuster.frontend;
    exports schumi178.javaprograms.cheatbuster.code.base.languages to cheatbuster.frontend;
}