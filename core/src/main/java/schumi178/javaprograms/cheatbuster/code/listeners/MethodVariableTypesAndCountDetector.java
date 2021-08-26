package schumi178.javaprograms.cheatbuster.code.listeners;

import org.antlr.v4.runtime.tree.TerminalNode;
import schumi178.javaprograms.cheatbuster.code.CBaseListener;
import schumi178.javaprograms.cheatbuster.code.CParser.*;
import schumi178.javaprograms.cheatbuster.kotlin.GetTypedefKt;

import java.util.*;

public class MethodVariableTypesAndCountDetector extends CBaseListener {

    private Map<String, Set<String>> variableTypes = new HashMap<>();
    private Map<String, Integer> variableCounts = new HashMap<>();

    private List<String> funcNameList = new ArrayList<>();
    private List<String> typedefList = new ArrayList<>();

    private String currentFuncName = "Outside function";

    @Override
    public void enterFunctionDefinition(FunctionDefinitionContext ctx) {
        DirectDeclaratorContext directDeclarator = ctx.declarator().directDeclarator();

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
            return;
        }

        while(identifier == null) {
            identifier = directDeclarator.Identifier();
            if (identifier == null) {
                directDeclarator = directDeclarator.directDeclarator();
            }
        }

        currentFuncName = identifier.getText();
        funcNameList.add(currentFuncName);
        variableTypes.put(currentFuncName, new HashSet<>());
    }

    public Map<String, Set<String>> getVariableTypes() {
        return variableTypes;
    }

    @Override
    public void exitFunctionDefinition(FunctionDefinitionContext ctx) {
        currentFuncName = "Outside function";
    }

    public String getTypedefList() {
        StringBuilder builder = new StringBuilder();
        for(String s: typedefList) {
            builder.append(s).append("\n");
        }
        return builder.toString();
    }

    public String getVariableTypesList() {
        StringBuilder builder = new StringBuilder();
        for(var entry: variableTypes.entrySet()) {
            builder.append(entry.getKey()).append(": ");
            for(String type: entry.getValue()) {
                builder.append(type).append(", ");
            }
            builder = new StringBuilder(builder.substring(0, builder.length() - 2));
            builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public void enterTypeSpecifier(TypeSpecifierContext ctx) {
        Set<String> currentFuncTypes = variableTypes.get(currentFuncName);
        if (currentFuncTypes == null) return;

        currentFuncTypes.add(ctx.getText());
    }

    @Override
    public void enterDeclaration(DeclarationContext ctx) {
        // wykrywanie typedefów

        String typedef = GetTypedefKt.getTypedef(ctx);

        if(!typedef.equals("")) {
            typedefList.add(typedef);
        }
    }
}
