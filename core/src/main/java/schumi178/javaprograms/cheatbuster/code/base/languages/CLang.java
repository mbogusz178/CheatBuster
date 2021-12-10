package schumi178.javaprograms.cheatbuster.code.base.languages;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import schumi178.javaprograms.cheatbuster.code.AssessmentResult;
import schumi178.javaprograms.cheatbuster.code.CLexer;
import schumi178.javaprograms.cheatbuster.code.CParser;
import schumi178.javaprograms.cheatbuster.code.base.Assessable;
import schumi178.javaprograms.cheatbuster.code.base.CompileReadyParser;
import schumi178.javaprograms.cheatbuster.code.base.LanguageAction;
import schumi178.javaprograms.cheatbuster.code.base.ProgrammingLanguage;
import schumi178.javaprograms.cheatbuster.code.exception.DoesNotCompileException;
import schumi178.javaprograms.cheatbuster.code.exception.IncludeNotFoundException;
import schumi178.javaprograms.cheatbuster.code.listeners.JavaDiffUtilsDetector;
import schumi178.javaprograms.cheatbuster.code.listeners.MethodCountDetector;
import schumi178.javaprograms.cheatbuster.code.listeners.MethodVariableNamesDetector;
import schumi178.javaprograms.cheatbuster.code.listeners.MethodVariableTypesAndCountDetector;
import schumi178.javaprograms.cheatbuster.util.Result;
import schumi178.javaprograms.cheatbuster.util.TypedefParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CLang implements ProgrammingLanguage {
    @Override
    public Lexer getLexer(CharStream code) {
        return new CLexer(code);
    }

    @Override
    public String getName() {
        return "C";
    }

    @Override
    public String getHelp() {
        return "Jeśli CheatBuster nie rozpoznał wszystkich plików nagłówkowych zaimportowanych w załadowanych plikach, w pasku statusu zostanie wyświetlone stosowne powiadomienie, zawierające nazwy nierozpoznanych plików. Aby CheatBuster poprawnie rozpoznał brakujące pliki, wskaż ścieżki do nich w menu Opcje > Opcje > Preprocesor, a następnie wybierz opcję C > Odśwież zależności. Jeśli chcesz odświeżyć pasek statusu, kliknij ikonę odświeżania w prawym dolnym rogu.\n" +
                "Ewentualnie, jeśli na Twoim komputerze nie ma wymaganych plików nagłówkowych, możesz kontynuować bez nich, jednak typy zdefiniowane w tych plikach przy pomocy polecenia typedef nie zostaną uwzględnione w procesie porównania, co może doprowadzić do błędnego rozpoznania deklaracji zmiennych wskaźnikowych, a co za tym idzie - mniej dokładnego wyniku podobieństwa kodu.";
    }

    private String refreshDependencies(String code, List<String> includePaths) {
        try {
            Map<String, List<String>> typedefStatements = TypedefParser.getTypedefStatements(code, includePaths);
            try {
                for(Map.Entry<String, List<String>> entry: typedefStatements.entrySet()) {
                    File file = new File("cache/" + getName() + "/includedTypes/" + entry.getKey() + ".cfg");
                    //noinspection ResultOfMethodCallIgnored
                    file.getParentFile().mkdirs();
                    PrintWriter writer = new PrintWriter(file);
                    for(String statement: entry.getValue()) {
                        List<String> types = TypedefParser.getDefinedTypes(statement);
                        for (String type : types) {
                            writer.write(type + "\n");
                        }
                    }
                    writer.close();
                }
            } catch (FileNotFoundException ignored) {

            }
        } catch (IncludeNotFoundException e) {
            String codeWithoutMissing = code.lines().filter(line -> !line.contains(e.getFilename())).collect(Collectors.joining());
            refreshDependencies(codeWithoutMissing, includePaths);
            return e.getMessage();
        } catch (DoesNotCompileException e) {
            return e.getMessage();
        }
        return "";
    }

    @Override
    public List<LanguageAction> getActions() {
        return List.of(new LanguageAction("Odśwież zależności", this::refreshDependencies));
    }

    @Override
    public String getFileExtension() {
        return ".c";
    }

    @Override
    public CompileReadyParser getParser(TokenStream tokenStream) {
        return new CParser(tokenStream);
    }

    @Override
    public List<ParseTreeListener> getListeners(String file, String includedTypesPath) throws DoesNotCompileException {
        return List.of(new MethodCountDetector(), new MethodVariableTypesAndCountDetector(file, includedTypesPath), new MethodVariableNamesDetector(file, includedTypesPath), new JavaDiffUtilsDetector());
    }

    @Override
    public String getErrors(String code) {
        List<String> missingFiles = new ArrayList<>();
        List<String> includedFiles = TypedefParser.getIncludedFiles(code);
        for(String include: includedFiles) {
            try {
                String path = "cache/" + getName() + "/includedTypes/" + include + ".cfg";
                Scanner ignored = new Scanner(new File(path));
                ignored.close();
            } catch (FileNotFoundException e) {
                missingFiles.add(include);
            }
        }
        if(missingFiles.isEmpty())
            return null;
        String commaSeparated = String.join(", ", missingFiles);
        return "Uwaga: Plik dołącza nierozpoznane przez CheatBuster pliki nagłówkowe: " + commaSeparated + ". Typy zdefiniowane w tych plikach nie zostaną uwzględnione w procesie porównywania.";
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
