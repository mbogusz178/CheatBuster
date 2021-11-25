package schumi178.javaprograms.cheatbuster.code.base.languages;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import schumi178.javaprograms.cheatbuster.code.AssessmentResult;
import schumi178.javaprograms.cheatbuster.code.CLexer;
import schumi178.javaprograms.cheatbuster.code.CParser;
import schumi178.javaprograms.cheatbuster.code.base.CompileReadyParser;
import schumi178.javaprograms.cheatbuster.code.base.ProgrammingLanguage;
import schumi178.javaprograms.cheatbuster.code.base.Assessable;
import schumi178.javaprograms.cheatbuster.code.exception.DoesNotCompileException;
import schumi178.javaprograms.cheatbuster.code.listeners.*;
import schumi178.javaprograms.cheatbuster.util.Result;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

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
        return List.of(new MethodCountDetector(), new MethodVariableTypesAndCountDetector(), new MethodVariableNamesDetector(), new JavaDiffUtilsDetector());
    }

    private void processDefine(String line, StringBuilder result, List<String> defs) throws DoesNotCompileException {
//        int bracketLevel = 0;
//        boolean nameDefined = false;
//        StringBuilder name = new StringBuilder();
//        for(int i = 0; i < line.length(); i++) {
//            if(line.charAt(i) == '(') {
//                nameDefined = true;
//                bracketLevel++;
//            } else if(line.charAt(i) == ')') {
//                bracketLevel--;
//                if(bracketLevel < 0) throw new DoesNotCompileException();
//            } else if(line.charAt(i) == ' ') {
//                nameDefined = true;
//            } else if(bracketLevel == 0 && !nameDefined) {
//                name.append(line.charAt(i));
//            }
//        }
    }

    private static String swapStringLine(String originalString, String lineToBeSwapped, String stringToBeInserted) {

        StringTokenizer tokenizer = new StringTokenizer(originalString, "\n");
        StringBuilder newString = new StringBuilder();
        while(tokenizer.hasMoreTokens()) {
            String nextString = tokenizer.nextToken();
            if(nextString.trim().equals(lineToBeSwapped)) {
                newString.append(stringToBeInserted).append("\n");
            } else {
                newString.append(nextString).append("\n");
            }
        }

        return newString.toString();
    }

    private static boolean isInclude(String line) {
        if(line.startsWith("#") && line.length() > 1) {
            for(int i = 1; i < line.length(); i++) {
                if(!Character.isWhitespace(line.charAt(i))) {
                    return line.substring(i).startsWith("include ");
                }
            }
        }
        return false;
    }

    private String processInclude(String fileName, List<String> includePaths, List<String> defines, List<String> typedefs, List<String> filesIncluded) throws DoesNotCompileException {
        boolean fileFound = false;
        File file = null;
        for(String path: includePaths) {
            file = new File(path + (path.endsWith("/") ? "" : "/") + fileName);
            if(file.exists()) {
                fileFound = true;
                break;
            }
        }
        if(!fileFound) {
            throw new DoesNotCompileException("Nieznany plik: " + fileName);
        }
        if(filesIncluded.contains(fileName)) {
            return "";
        }
        filesIncluded.add(fileName);
        StringBuilder builder = new StringBuilder();
        try {
            Scanner sc = new Scanner(file);
            boolean parsingTypedef = false;
            int bracketLevel = 0;
            int count = 0;
            while(sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if(parsingTypedef) {
                    long openingBracketCount = line.chars().filter(c -> c == '{').count();
                    bracketLevel += openingBracketCount;
                    long closingBracketCount = line.chars().filter(c -> c == '}').count();
                    bracketLevel -= closingBracketCount;
                    typedefs.add(line);
                    if(line.endsWith(";") && bracketLevel < 1) {
                        parsingTypedef = false;
                    }
                    continue;
                } else if(line.startsWith("typedef ")) {
                    long openingBracketCount = line.chars().filter(c -> c == '{').count();
                    bracketLevel += openingBracketCount;
                    long closingBracketCount = line.chars().filter(c -> c == '}').count();
                    bracketLevel -= closingBracketCount;
                    parsingTypedef = true;
                    typedefs.add(line);
                    if(line.endsWith(";") && bracketLevel < 1) {
                        parsingTypedef = false;
                    }
                } else if(isInclude(line)) {
                    String includedDefinesAndTypedefs;
                    if(line.contains("<") && line.contains(">")) {
                        includedDefinesAndTypedefs = processInclude(line.substring(line.indexOf('<') + 1, line.indexOf('>')), includePaths, defines, typedefs, filesIncluded);
                    } else if(line.chars().filter(c -> c == '\"').count() == 2) {
                        includedDefinesAndTypedefs = processInclude(line.substring(line.indexOf('\"') + 1, line.substring(line.indexOf('\"') + 1).indexOf('\"') + line.indexOf('\"') + 1), includePaths, defines, typedefs, filesIncluded);
                    } else throw new DoesNotCompileException("Błąd składni w dyrektywie #include (plik " + fileName + ", wiersz " + count + ")");
                    builder.append(includedDefinesAndTypedefs);
                }
                count++;
            }
        } catch (FileNotFoundException e) {
            throw new DoesNotCompileException("Nieznany plik: " + fileName);
        }
        for(String typedef: typedefs) {
            builder.append(typedef).append("\n");
        }
        return builder.toString();
    }

    @Override
    public String preprocess(String code, List<String> includePaths) throws DoesNotCompileException {
        List<String> defs = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        List<String> filesIncluded = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(code, "\n");
        int count = 0;
        while(tokenizer.hasMoreTokens()) {
            String line = tokenizer.nextToken().trim();
            if(isInclude(line)) {
                String includedDefinesAndTypedefs;
                if(line.contains("<") && line.contains(">")) {
                    includedDefinesAndTypedefs = processInclude(line.substring(line.indexOf('<') + 1, line.indexOf('>')), includePaths, new ArrayList<>(), new ArrayList<>(), filesIncluded);
                } else if(line.chars().filter(c -> c == '\"').count() == 2) {
                    String fileName = line.substring(line.indexOf('\"') + 1);
                    fileName = fileName.substring(0, fileName.indexOf('\"'));
                    includedDefinesAndTypedefs = processInclude(fileName, includePaths, new ArrayList<>(), new ArrayList<>(), filesIncluded);
                } else throw new DoesNotCompileException("Błąd składni w dyrektywie #include (wiersz " + count + ")");
                code = swapStringLine(code, line, includedDefinesAndTypedefs);
            }
            count++;
        }
        if(filesIncluded.contains("sys/cdefs.h") || filesIncluded.contains("cdefs.h")) {
            code = code.replaceAll("__END_DECLS", "");
        }

        return code;
    }

    @Override
    public AssessmentResult assess(List<ParseTreeListener> filledListenersFirst, List<ParseTreeListener> filledListenersSecond) {
        List<Assessable> resultListeners = new ArrayList<>();
        List<Double> results = new ArrayList<>();
        List<Result> resultListFirst = new ArrayList<>();
        for(ParseTreeListener listener: filledListenersFirst) {
            if(listener instanceof Assessable) {
                Assessable provider = (Assessable) listener;
                resultListFirst.add(provider.getResult());
            }
        }
        int count = 0;
        int finalResult = 0;
        int weightSum = 0;
        for(ParseTreeListener listener: filledListenersSecond) {
            if(listener instanceof Assessable) {
                Assessable provider = (Assessable) listener;
                double currentAssessment = provider.assess(resultListFirst.get(count));
                finalResult += currentAssessment * provider.getWeight();
                weightSum += provider.getWeight();
                resultListeners.add(provider);
                results.add(currentAssessment);
                count++;
            }
        }
        return new AssessmentResult(resultListeners, results,finalResult / weightSum);
    }
}
