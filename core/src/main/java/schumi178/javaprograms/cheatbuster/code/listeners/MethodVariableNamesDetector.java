package schumi178.javaprograms.cheatbuster.code.listeners;

import org.antlr.v4.runtime.tree.TerminalNode;
import schumi178.javaprograms.cheatbuster.code.CBaseListener;
import schumi178.javaprograms.cheatbuster.code.base.Assessable;
import schumi178.javaprograms.cheatbuster.code.exception.DoesNotCompileException;
import schumi178.javaprograms.cheatbuster.kotlin.UtilKt;
import schumi178.javaprograms.cheatbuster.util.ListStringResult;
import schumi178.javaprograms.cheatbuster.util.ListStringWrapper;
import schumi178.javaprograms.cheatbuster.util.Result;
import schumi178.javaprograms.cheatbuster.code.CParser;
import schumi178.javaprograms.cheatbuster.util.TypedefParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class MethodVariableNamesDetector extends CBaseListener {

    private final Set<String> typedefList = new HashSet<>();
    private final List<String> variableNames = new ArrayList<>();
    private final List<String> funcNames = new ArrayList<>();

    private List<String> totalNames = new ArrayList<>();
    private List<String> totalNamesOther = new ArrayList<>();

    public MethodVariableNamesDetector(String file, String includedTypesPath) throws DoesNotCompileException {
        List<String> lines = file.lines().collect(Collectors.toList());
        int count = 1;
        for(String line: lines) {
            if(TypedefParser.isInclude(line)) {
                if(line.contains("<") && line.contains(">")) {
                    String childFileName = line.substring(line.indexOf('<') + 1, line.indexOf('>'));
                    try {
                        Scanner sc = new Scanner(new File(includedTypesPath + "/" + childFileName + ".cfg"));
                        while(sc.hasNextLine()) {
                            typedefList.add(sc.nextLine());
                        }
                        sc.close();
                    } catch (FileNotFoundException ignored) {

                    }
                } else if(line.chars().filter(c -> c == '\"').count() == 2) {
                    String childFileName = line.substring(line.indexOf('\"') + 1);
                    childFileName = childFileName.substring(0, childFileName.indexOf('\"'));
                    try {
                        Scanner sc = new Scanner(new File("cache/c/includedTypes/" + childFileName + ".cfg"));
                        while(sc.hasNextLine()) {
                            typedefList.add(sc.nextLine());
                        }
                        sc.close();
                    } catch (FileNotFoundException ignored) {

                    }
                } else throw new DoesNotCompileException("Błąd składni w dyrektywie #include (wiersz " + count + ")");
            }
            count++;
        }
    }

    @Override
    public void enterVariableName(CParser.VariableNameContext ctx) {
        variableNames.add(ctx.getText());
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
    }

    @Override
    public void enterTypedefDeclarationNames(CParser.TypedefDeclarationNamesContext ctx) {
        for(CParser.TypedefDeclarationNameContext typedef: ctx.typedefDeclarationName()) {
            typedefList.add(typedef.typedefName().getText());
        }
    }

    @Override
    public void enterMultiplicativeExpression(CParser.MultiplicativeExpressionContext ctx) {
        String typeName = ctx.castExpression(0).getText();
        if(typedefList.contains(typeName)) {
            CParser.CastExpressionContext cast = ctx.castExpression(1);
            if(cast != null) {
                String secondOperand = UtilKt.getSecondOperand(cast);
                if (!secondOperand.isEmpty()) {
                    variableNames.add(secondOperand);
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

    private double[] levenshteinLoop(List<String> thisList, List<String> otherList) {
        double distanceCurrent = 0;
        double distanceMax = 0;
        for(String s1: thisList) {
            double distanceSingle = 0;
            boolean similarDetected = false;
            for(String s2: otherList) {
                int distance = levenshteinDistance(s1, s2);
                int maxLength = Math.max(s1.length(), s2.length());
                if(distance <= 1) {
                    distanceSingle = 0;
                    similarDetected = true;
                }
                if(!similarDetected)
                    distanceSingle += distance;
                distanceMax += maxLength;
            }
            distanceCurrent += distanceSingle;
        }
        return new double[] {distanceCurrent, distanceMax};
    }

    @Override
    public double assess(Result otherResult) {
        Result thisResult = getResult();
        assert thisResult instanceof ListStringResult;
        List<String> thisList = ((ListStringResult) thisResult).getResult();
        assert otherResult instanceof ListStringResult;
        List<String> otherList = ((ListStringResult)otherResult).getResult();
        totalNamesOther = otherList;
        double[] result1 = levenshteinLoop(thisList, otherList);
        double[] result2 = levenshteinLoop(otherList, thisList);
        double distanceCurrent = (result1[0] + result2[0]) / 2.0;
        double distanceMax = result1[1];
        double finalResult = distanceCurrent / distanceMax;
        return 100 - finalResult * 100;
    }
}
