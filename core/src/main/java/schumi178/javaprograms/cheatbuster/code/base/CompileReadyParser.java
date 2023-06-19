package schumi178.javaprograms.cheatbuster.code.base;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public abstract class CompileReadyParser extends Parser {
    public CompileReadyParser(TokenStream input) {
        super(input);
    }

    public abstract ParseTree walkTree();
}
