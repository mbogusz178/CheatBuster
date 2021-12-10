package schumi178.javaprograms.cheatbuster.code.listeners;

import org.antlr.v4.runtime.tree.TerminalNode;
import schumi178.javaprograms.cheatbuster.code.CBaseListener;
import schumi178.javaprograms.cheatbuster.code.CParser.*;
import schumi178.javaprograms.cheatbuster.code.base.Assessable;
import schumi178.javaprograms.cheatbuster.code.exception.DoesNotCompileException;
import schumi178.javaprograms.cheatbuster.kotlin.FunctionKt;
import schumi178.javaprograms.cheatbuster.kotlin.UtilKt;
import schumi178.javaprograms.cheatbuster.util.DoubleWrapper;
import schumi178.javaprograms.cheatbuster.util.Result;
import schumi178.javaprograms.cheatbuster.util.TypedefParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MethodVariableTypesAndCountDetector extends CBaseListener {

    private final Map<String, Set<String>> variableTypes = new HashMap<>();
    private final Map<String, Integer> variableCounts = new HashMap<>();

    private final List<String> funcNameList = new ArrayList<>();
    private final Set<String> typedefList = new HashSet<>();

    private double result;
    private double resultOther;
    private double typeWeight;
    private double variableWeight;

    private String currentFuncName = "Outside function";

    public MethodVariableTypesAndCountDetector(String file, String includedTypesPath) throws DoesNotCompileException {
        funcNameList.add("Outside function");
        variableTypes.put("Outside function", new HashSet<>());
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
        try {
            Scanner scanner = new Scanner(new File("config/weights.cfg"));
            Locale locale = Locale.getDefault();
            scanner.useLocale(locale);
            variableWeight = scanner.nextDouble();
            typeWeight = scanner.nextDouble();
            scanner.close();
        } catch (FileNotFoundException e) {
            typeWeight = 1.5;
            variableWeight = 3.0;
        }
    }

    @Override
    public void enterIncludeDeclaration(IncludeDeclarationContext ctx) {
        System.out.println(ctx.IncludeBlock().getText());
    }

    public Map<String, Integer> getVariableCounts() {
        return variableCounts;
    }

    private void detectTypedefVariables(BlockItemListContext ctx) {
        for(BlockItemContext item: ctx.blockItem()) {
            BlockItemListContext innerBlock = FunctionKt.getFunctionBody(item);
            if(innerBlock != null) {
                detectTypedefVariables(innerBlock);
                return;
            }
            String statementText = item.getText();
//            System.out.println(statementText);
            int index = statementText.lastIndexOf('*');
            if(index > 0) {

            }
        }
    }

    private void detectTypedefVariables(FunctionDefinitionContext ctx) {
        BlockItemListContext functionBody = FunctionKt.getFunctionBody(ctx);
        if(functionBody == null) { // prototyp funkcji
            return;
        }
        detectTypedefVariables(functionBody);
    }

    @Override
    public void enterTypedefDeclarationNames(TypedefDeclarationNamesContext ctx) {
        for(TypedefDeclarationNameContext typedef: ctx.typedefDeclarationName()) {
            typedefList.add(typedef.typedefName().getText());
        }
    }

    @Override
    public void enterMultiplicativeExpression(MultiplicativeExpressionContext ctx) {
        String typeName = ctx.castExpression(0).getText();
//        System.out.println(typeName);
        if(typedefList.contains(typeName)) {
            CastExpressionContext cast = ctx.castExpression(1);
            if(cast != null) {
                String secondOperand = UtilKt.getSecondOperand(cast);
                if (!secondOperand.isEmpty()) {
                    Set<String> currentFuncTypes = variableTypes.get(currentFuncName);
                    if (currentFuncTypes != null) {
                        String type = typeName + "*";
                        variableTypes.get(currentFuncName).add(type);
                        if(!variableCounts.containsKey(type)) {
                            variableCounts.put(type, 0);
                        }
                        int count = variableCounts.get(type);
                        variableCounts.remove(type);
                        variableCounts.put(type, count + 1);
//                        System.out.println(currentFuncName + ": " + typeName + " *" + secondOperand);
                    }
                }
            }
        }
    }

    @Override
    public void enterFunctionDefinition(FunctionDefinitionContext ctx) {
        DirectDeclaratorContext directDeclarator = ctx.declarator().directDeclarator();
        if(directDeclarator == null) return;

        TerminalNode identifier = directDeclarator.Identifier();

        // poniższy if ze względu na błąd w ANTLR

        if(ctx.declarator().getText().startsWith("(")) {
            currentFuncName = ctx.declarationSpecifiers()
                                 .declarationSpecifier(1)
                                 .typeSpecifier()
                                 .typedefName()
                                 .getText();
            funcNameList.add(currentFuncName);
            variableTypes.put(currentFuncName, new HashSet<>());
            detectTypedefVariables(ctx);
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
        funcNameList.add(currentFuncName);
        variableTypes.put(currentFuncName, new HashSet<>());
        detectTypedefVariables(ctx);
    }

    public Map<String, Set<String>> getVariableTypes() {
        return variableTypes;
    }

    public List<String> getFuncNameList() {
        return funcNameList;
    }

    @Override
    public void exitFunctionDefinition(FunctionDefinitionContext ctx) {
        currentFuncName = "Outside function";
    }

    @Override
    public void enterVarInitDeclarationList(VarInitDeclarationListContext ctx) {
        for(VarInitDeclarationContext varInitDeclaration: ctx.varInitDeclaration()) {
            String type = ctx.varType().getText() + UtilKt.getPointer(varInitDeclaration);
            if(varInitDeclaration.arrayDimension() != null) {
                type += "[]".repeat(varInitDeclaration.arrayDimension().size());
            }
            variableTypes.get(currentFuncName).add(type);
            if(!variableCounts.containsKey(type)) {
                variableCounts.put(type, 0);
            }
            int count = variableCounts.get(type);
            variableCounts.remove(type);
            variableCounts.put(type, count + 1);
        }
    }

    public String getTypedefList() {
        StringBuilder builder = new StringBuilder();
        for(String s: typedefList) {
            builder.append(s).append("\n");
        }
        return builder.toString();
    }

    @Override
    public String getName() {
        return "Typy i liczba zmiennych";
    }

    @Override
    public String resultToString() {
        return "Wynik obliczany jest, mnożąc liczbę typów w kodzie przez " + typeWeight + ", a następnie dodając do wyniku całkowitą liczbę zmiennych pomnożoną przez " + variableWeight + ".\n" +
                "Wynik po lewej: " + resultOther + "\n" +
                "Wynik po prawej: " + result + "\n";
    }

    @Override
    public Result getResult() {
        int typeCount = variableCounts.size();
        int totalVariableCount = 0;
        for(int typeVariables: variableCounts.values()) {
            totalVariableCount += typeVariables;
        }
        result = typeCount * typeWeight + totalVariableCount * variableWeight;
        return new DoubleWrapper(result);
    }

    @Override
    public double getWeight() {
        return 4;
    }

    @Override
    public double assess(Result otherResult) {
        double thisResult = (double)getResult().getValue();
        double otherResultCast = (double)otherResult.getValue();
        if(thisResult == 0 && otherResultCast == 0) {
            return 100;
        }
        double diff;
        if(thisResult < otherResultCast) {
            diff = thisResult * 0.9;
        } else {
            diff = otherResultCast * 0.9;
        }
        resultOther = otherResultCast;
        thisResult -= diff;
        otherResultCast -= diff;
        double relative = thisResult < otherResultCast ? thisResult / otherResultCast : otherResultCast / thisResult;
        return relative * 100;
    }
}
