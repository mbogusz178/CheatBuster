package schumi178.javaprograms.cheatbuster.code;

public class MethodCountDetector extends CBaseListener {

    private int methodCount = 0;

    @Override
    public void enterFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        methodCount++;
    }

    public int getMethodCount() {
        return methodCount;
    }
}
