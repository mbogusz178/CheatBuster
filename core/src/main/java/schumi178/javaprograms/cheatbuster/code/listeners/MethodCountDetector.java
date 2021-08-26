package schumi178.javaprograms.cheatbuster.code.listeners;

import schumi178.javaprograms.cheatbuster.code.CBaseListener;
import schumi178.javaprograms.cheatbuster.code.CParser.FunctionDefinitionContext;

public class MethodCountDetector extends CBaseListener {

    private int methodCount = 0;

    @Override
    public void enterFunctionDefinition(FunctionDefinitionContext ctx) {
        methodCount++;
    }

    public int getMethodCount() {
        return methodCount;
    }
}
