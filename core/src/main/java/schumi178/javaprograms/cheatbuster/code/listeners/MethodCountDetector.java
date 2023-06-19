package schumi178.javaprograms.cheatbuster.code.listeners;

import schumi178.javaprograms.cheatbuster.code.CBaseListener;
import schumi178.javaprograms.cheatbuster.code.base.Assessable;
import schumi178.javaprograms.cheatbuster.util.DoubleWrapper;
import schumi178.javaprograms.cheatbuster.util.Result;
import schumi178.javaprograms.cheatbuster.code.CParser.*;

public class MethodCountDetector extends CBaseListener {

    private int methodCount = 0;
    private int methodCountOther = 0;

    @Override
    public void enterFunctionDefinition(FunctionDefinitionContext ctx) {
        methodCount++;
    }

    public int getMethodCount() {
        return methodCount;
    }

    @Override
    public String getName() {
        return "Liczba metod";
    }

    @Override
    public String resultToString() {
        return "Metod w kodzie po lewej: " + methodCountOther + "\nMetod w kodzie po prawej: " + methodCount;
    }

    @Override
    public Result getResult() {
        return new DoubleWrapper(methodCount);
    }

    @Override
    public double getWeight() {
        return 1;
    }

    @Override
    public double assess(Result otherResult) {
        double methodCountOtherDouble = (double)otherResult.getValue();
        methodCountOther = (int)methodCountOtherDouble;
        double diff = Math.abs((double)getResult().getValue() - (double)otherResult.getValue());
        double factor = Math.pow(2, diff);
        return 100 - (100 * ((factor - 1) / factor));
    }
}
