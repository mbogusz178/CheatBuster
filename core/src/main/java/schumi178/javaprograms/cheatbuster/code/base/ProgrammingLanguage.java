package schumi178.javaprograms.cheatbuster.code.base;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import schumi178.javaprograms.cheatbuster.code.AssessmentResult;
import schumi178.javaprograms.cheatbuster.code.base.languages.CLang;
import schumi178.javaprograms.cheatbuster.code.exception.DoesNotCompileException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface ProgrammingLanguage {
    Lexer getLexer(CharStream code);
    String getName();
    String getHelp();
    List<LanguageAction> getActions();
    String getFileExtension();
    CompileReadyParser getParser(TokenStream tokenStream);
    List<ParseTreeListener> getListeners(String file, String includedTypesPath) throws DoesNotCompileException;
    default String preprocess(String code, List<String> includePaths) throws DoesNotCompileException {
        return code;
    }
    String getErrors(String code);
    AssessmentResult assess(List<ParseTreeListener> filledListenersFirst, List<ParseTreeListener> filledListenersSecond);
}
