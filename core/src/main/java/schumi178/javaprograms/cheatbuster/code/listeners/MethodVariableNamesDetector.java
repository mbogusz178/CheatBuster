package schumi178.javaprograms.cheatbuster.code.listeners;

import org.antlr.v4.runtime.tree.TerminalNode;
import schumi178.javaprograms.cheatbuster.code.CBaseListener;
import schumi178.javaprograms.cheatbuster.code.CParser;
import schumi178.javaprograms.cheatbuster.code.base.Assessable;
import schumi178.javaprograms.cheatbuster.kotlin.UtilKt;
import schumi178.javaprograms.cheatbuster.util.ListStringResult;
import schumi178.javaprograms.cheatbuster.util.ListStringWrapper;
import schumi178.javaprograms.cheatbuster.util.Result;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MethodVariableNamesDetector extends CBaseListener implements Assessable {

    private final Set<String> typedefList = new HashSet<>();
    private final List<String> variableNames = new ArrayList<>();
    private final List<String> funcNames = new ArrayList<>();

    private List<String> totalNames = new ArrayList<>();
    private List<String> totalNamesOther = new ArrayList<>();

    @Override
    public void enterVariableName(CParser.VariableNameContext ctx) {
        variableNames.add(ctx.getText());
        System.out.println(ctx.getText() + " (zmienna)");
    }

    @Override
    public void enterFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        CParser.DirectDeclaratorContext directDeclarator = ctx.declarator().directDeclarator();
        if(directDeclarator == null) return;

        TerminalNode identifier = directDeclarator.Identifier();

        String currentFuncName;
        // poniższy if ze względu na błąd w ANTLR

        if(ctx.declarator().getText().startsWith("(")) {
            currentFuncName = ctx.declarationSpecifiers()
                    .declarationSpecifier(1)
                    .typeSpecifier()
                    .typedefName()
                    .getText();
            funcNames.add(currentFuncName);
            System.out.println(currentFuncName + " (funkcja)");
            return;
        }

        while(identifier == null) {
            identifier = directDeclarator.Identifier();
            if (identifier == null) {
                directDeclarator = directDeclarator.directDeclarator();
                if(directDeclarator == null) {
                    return;
                }
            }
        }

        currentFuncName = identifier.getText();
        funcNames.add(currentFuncName);
        System.out.println(currentFuncName + " (funkcja)");
    }

    @Override
    public void enterTypedefDeclaration(CParser.TypedefDeclarationContext ctx) {
        typedefList.add(ctx.typedefName().getText());
    }

    @Override
    public void enterMultiplicativeExpression(CParser.MultiplicativeExpressionContext ctx) {
        String typeName = ctx.castExpression(0).getText();
//        System.out.println(typeName);
        if(typedefList.contains(typeName)) {
            CParser.CastExpressionContext cast = ctx.castExpression(1);
            if(cast != null) {
                String secondOperand = UtilKt.getSecondOperand(cast);
                if (!secondOperand.isEmpty()) {
                    System.out.println(secondOperand + " (zmienna)");
                }
            }
        }
    }

    int levenshteinDistance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] d = new int[m + 1][n + 1];
        for(int i = 0; i <= m; i++) {
            d[i][0] = i;
        }
        for(int j = 1; j <= n; j++) {
            d[0][j] = j;
        }
        for(int i = 1; i <= m; i++) {
            for(int j = 1; j <= n; j++) {
                int cost;
                if(s1.charAt(i-1) == s2.charAt(j-1))
                    cost = 0;
                else cost = 1;
                d[i][j] = Math.min(Math.min(d[i-1][j] + 1, d[i][j-1] + 1), d[i-1][j-1] + cost);
            }
        }
        return d[m][n];
    }

    @Override
    public String getName() {
        return "Nazwy zmiennych i funkcji";
    }

    @Override
    public String resultToString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Nazwy po lewej:\n");
        for(String name: totalNamesOther) {
            builder.append(name).append("\n");
        }
        builder.append("Nazwy po prawej:\n");
        for(String name: totalNames) {
            builder.append(name).append("\n");
        }
        return builder.toString();
    }

    @Override
    public Result getResult() {
        List<String> totalString = new ArrayList<>(List.copyOf(funcNames));
        totalString.addAll(variableNames);
        totalNames = totalString;
        return new ListStringWrapper(totalString);
    }

    @Override
    public double getWeight() {
        return 2;
    }

    @Override
    public double assess(Result otherResult) {
        Result thisResult = getResult();
        assert thisResult instanceof ListStringResult;
        List<String> thisList = ((ListStringResult) thisResult).getResult();
        assert otherResult instanceof ListStringResult;
        List<String> otherList = ((ListStringResult)otherResult).getResult();
        totalNamesOther = otherList;
        double distanceCurrent = 0;
        double distanceMax = 0;
        for(String s1: thisList) {
            double distanceSingle = 0;
            for(String s2: otherList) {
                int distance = levenshteinDistance(s1, s2);
                int maxLength = Math.max(s1.length(), s2.length());
                if(distance <= 1) {
                    System.out.println("Podobne: " + s1 + ", " + s2);
                    distanceSingle = 0;
                    distanceMax += maxLength;
                    break;
                }
                distanceSingle += distance;
                distanceMax += maxLength;
            }
            distanceCurrent += distanceSingle;
        }
        double finalResult = distanceCurrent / distanceMax;
        System.out.println(100 - finalResult * 100);
        return 100 - finalResult * 100;
    }
}
