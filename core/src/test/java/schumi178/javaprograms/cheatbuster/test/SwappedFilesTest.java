package schumi178.javaprograms.cheatbuster.test;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import schumi178.javaprograms.cheatbuster.code.AssessmentResult;
import schumi178.javaprograms.cheatbuster.code.base.CompileReadyParser;
import schumi178.javaprograms.cheatbuster.code.base.FileProcessable;
import schumi178.javaprograms.cheatbuster.code.base.ProgrammingLanguage;
import schumi178.javaprograms.cheatbuster.code.base.ProgrammingLanguageFactory;
import schumi178.javaprograms.cheatbuster.code.base.languages.CLang;
import schumi178.javaprograms.cheatbuster.code.exception.DoesNotCompileException;
import schumi178.javaprograms.cheatbuster.file.StringParser;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class SwappedFilesTest {

    private static final Logger log = Logger.getLogger(SwappedFilesTest.class.getName());
    private static final ProgrammingLanguageFactory factory = new ProgrammingLanguageFactory();

    @BeforeAll
    static void init() {
        factory.registerLanguage(new CLang());
    }

    private AssessmentResult compareFiles(File file1, File file2) throws IOException, DoesNotCompileException, URISyntaxException {
        ProgrammingLanguage lang = factory.getLanguage(file1.getName());

        URL resource = getClass().getResource("/includedTypes");
        String includedTypesPath = Paths.get(Objects.requireNonNull(resource).toURI()).toAbsolutePath().toString();

        List<String> includePaths = new ArrayList<>();
        Scanner sc = new Scanner(Objects.requireNonNull(getClass().getResourceAsStream("/config/include.cfg")));
        while(sc.hasNextLine()) {
            includePaths.add(sc.nextLine());
        }

        String file1String = StringParser.getStringFromFile(file1);
        String file2String = StringParser.getStringFromFile(file2);
        String preprocessedLeftText;

        preprocessedLeftText = lang.preprocess(file1String, includePaths);

        CharStream charStream = CharStreams.fromString(preprocessedLeftText);
        Lexer lexer = lang.getLexer(charStream);
        lexer.setTokenFactory(new CommonTokenFactory(true));
        TokenStream tokenStream = new UnbufferedTokenStream<CommonToken>(lexer);
        CompileReadyParser parser = lang.getParser(tokenStream);
        ParseTree tree = parser.walkTree();
        ParseTreeWalker walker = new ParseTreeWalker();
        List<ParseTreeListener> listeners = lang.getListeners(preprocessedLeftText, includedTypesPath);
        listeners.forEach(listener -> walker.walk(listener, tree));
        listeners.forEach(listener -> {
            if(listener instanceof FileProcessable) {
                ((FileProcessable)listener).process(file1String, file2String);
            }
        });

        String preprocessedRightText;

        preprocessedRightText = lang.preprocess(file2String, includePaths);

        CharStream charStream1 = CharStreams.fromString(preprocessedRightText);
        Lexer lexer1 = lang.getLexer(charStream1);
        lexer1.setTokenFactory(new CommonTokenFactory(true));
        TokenStream tokenStream1 = new UnbufferedTokenStream<CommonToken>(lexer1);
        CompileReadyParser parser1 = lang.getParser(tokenStream1);
        ParseTree tree1 = parser1.walkTree();
        ParseTreeWalker walker1 = new ParseTreeWalker();
        List<ParseTreeListener> listeners1 = lang.getListeners(preprocessedRightText, includedTypesPath);
        listeners1.forEach(listener -> walker1.walk(listener, tree1));
        listeners1.forEach(listener -> {
            if(listener instanceof FileProcessable) {
                ((FileProcessable)listener).process(file2String, file1String);
            }
        });

        return lang.assess(listeners, listeners1);
    }

    private void compareSwappedFiles(File file1, File file2) throws IOException, DoesNotCompileException, URISyntaxException {
        AssessmentResult result1 = compareFiles(file1, file2);
        AssessmentResult result2 = compareFiles(file2, file1);
        assert result1.getFinalResult() == result2.getFinalResult();
        Iterator<Double> it = result1.resultDoubleIterator();
        Iterator<Double> it2 = result2.resultDoubleIterator();
        while (it.hasNext() && it2.hasNext()) {
            assert (double) it.next() == it2.next();
        }
    }

    @Test
    public void fileSet1() throws URISyntaxException, IOException, DoesNotCompileException {
        File file1 = new File(Objects.requireNonNull(getClass().getResource("/testCode/PP KJ-projekt z dokumentacja-15461_1/Małgorzata Kulik_233576_assignsubmission_file_/main.c")).toURI());
        File file2 = new File(Objects.requireNonNull(getClass().getResource("/testCode/PP KJ-projekt z dokumentacja-15461_1/Michał Leończuk_233577_assignsubmission_file_/projekt szachy - Michał Leończuk/ruchy.c")).toURI());
        compareSwappedFiles(file1, file2);
    }

    private List<File> getCFiles(File parent, List<File> totalFiles) {
        File[] files = parent.listFiles();
        if(files != null) {
            for(File file: files) {
                if(file.isFile() && file.getName().endsWith(".c")) {
                    totalFiles.add(file);
                } else if(file.isDirectory()) {
                    getCFiles(file, totalFiles);
                }
            }
        }
        return totalFiles;
    }

    @Test
    public void everyCombination() throws URISyntaxException, IOException, DoesNotCompileException {
        URL resource = getClass().getResource("/testCode");

        File file = Paths.get(Objects.requireNonNull(resource).toURI()).toFile();
        List<File> cCode = getCFiles(file, new ArrayList<>());
        for (int i = 0; i < cCode.size(); i++) {
            for(int j = i + 1; j < cCode.size(); j++) {
                File file1 = cCode.get(i);
                File file2 = cCode.get(j);
                log.info("Przetwarzanie pliku " + file1.getName() + " (" + file1.getAbsolutePath() + ") i " + file2.getName() + " (" + file2.getAbsolutePath() + ")");
                compareSwappedFiles(file1, file2);
            }
        }
    }
}
