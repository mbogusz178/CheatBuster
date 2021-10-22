package schumi178.javaprograms.cheatbuster.code.base;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import schumi178.javaprograms.cheatbuster.code.base.languages.CLang;
import schumi178.javaprograms.cheatbuster.code.exception.DoesNotCompileException;

import java.util.List;

public interface ProgrammingLanguage {
    Lexer getLexer(CharStream code);
    CompileReadyParser getParser(TokenStream tokenStream);
    List<ParseTreeListener> getListeners();
    default String preprocess(String code, List<String> includePaths) throws DoesNotCompileException {
        return code;
    }
    int assess(List<ParseTreeListener> filledListenersFirst, List<ParseTreeListener> filledListenersSecond);
    static ProgrammingLanguage getLanguage() {
        return new CLang();
    }
}
