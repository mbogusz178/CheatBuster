package schumi178.javaprograms.cheatbuster.code.base.languages;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import schumi178.javaprograms.cheatbuster.code.CLexer;
import schumi178.javaprograms.cheatbuster.code.CParser;
import schumi178.javaprograms.cheatbuster.code.base.CompileReadyParser;
import schumi178.javaprograms.cheatbuster.code.base.ProgrammingLanguage;
import schumi178.javaprograms.cheatbuster.code.listeners.MethodVariableTypesAndCountDetector;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CLang implements ProgrammingLanguage {
    @Override
    public Lexer getLexer(CharStream code) {
        return new CLexer(code);
    }

    @Override
    public CompileReadyParser getParser(TokenStream tokenStream) {
        return new CParser(tokenStream);
    }

    @Override
    public List<ParseTreeListener> getListeners() {
        return List.of(new MethodVariableTypesAndCountDetector());
    }

    @Override
    public int assess(List<ParseTreeListener> filledListenersFirst, List<ParseTreeListener> filledListenersSecond) {
        for(ParseTreeListener listener: filledListenersFirst) {
            if(listener instanceof MethodVariableTypesAndCountDetector) {
                MethodVariableTypesAndCountDetector castListener = (MethodVariableTypesAndCountDetector) listener;
                Map<String, Set<String>> variableTypes = castListener.getVariableTypes();
                String typedefs = castListener.getTypedefList();
                for(String string: variableTypes.keySet()) {
                    System.out.println(string);
                }
                System.out.println(typedefs);
            }
        }
        return 100;
    }
}
