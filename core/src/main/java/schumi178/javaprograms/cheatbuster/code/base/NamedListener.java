package schumi178.javaprograms.cheatbuster.code.base;

import org.antlr.v4.runtime.tree.ParseTreeListener;

public interface NamedListener extends ParseTreeListener {
    String getName();
}
