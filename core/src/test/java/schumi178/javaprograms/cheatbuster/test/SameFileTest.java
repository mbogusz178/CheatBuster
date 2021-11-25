package schumi178.javaprograms.cheatbuster.test;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import schumi178.javaprograms.cheatbuster.code.AssessmentResult;
import schumi178.javaprograms.cheatbuster.code.base.CompileReadyParser;
import schumi178.javaprograms.cheatbuster.code.base.FileProcessable;
import schumi178.javaprograms.cheatbuster.code.base.ProgrammingLanguage;
import schumi178.javaprograms.cheatbuster.code.exception.DoesNotCompileException;
import schumi178.javaprograms.cheatbuster.file.StringParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.*;

public class SameFileTest {

    private void compareFiles(File file1, File file2) throws IOException, DoesNotCompileException {
        ProgrammingLanguage lang = ProgrammingLanguage.getLanguage();

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
        List<ParseTreeListener> listeners = lang.getListeners();
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
        List<ParseTreeListener> listeners1 = lang.getListeners();
        listeners1.forEach(listener -> walker1.walk(listener, tree1));
        listeners1.forEach(listener -> {
            if(listener instanceof FileProcessable) {
                ((FileProcessable)listener).process(file2String, file1String);
            }
        });

        AssessmentResult result = lang.assess(listeners, listeners1);
        assert result.getFinalResult() == 100;
        Iterator<Double> it = result.resultDoubleIterator();
        while(it.hasNext()) {
            assert(it.next() == 100);
        }
    }

    @Test
    public void file1() throws URISyntaxException, IOException, DoesNotCompileException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/PP KJ-projekt z dokumentacja-15461_1/Małgorzata Kulik_233576_assignsubmission_file_/main.c")).toURI());
        compareFiles(file, file);
    }

    @Test
    public void file2() throws URISyntaxException, IOException, DoesNotCompileException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/PP KJ-projekt z dokumentacja-15461_1/Michał Leończuk_233577_assignsubmission_file_/projekt szachy - Michał Leończuk/ruchy.c")).toURI());
        compareFiles(file, file);
    }
}
