package schumi178.javaprograms.cheatbuster.code.listeners;

import schumi178.javaprograms.cheatbuster.code.CBaseListener;
import schumi178.javaprograms.cheatbuster.code.base.Assessable;
import schumi178.javaprograms.cheatbuster.code.base.FileProcessable;
import schumi178.javaprograms.cheatbuster.util.DoubleWrapper;
import schumi178.javaprograms.cheatbuster.util.Result;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JavaDiffUtilsDetector extends CBaseListener implements Assessable, FileProcessable {

    private final List<String> sameRows = new ArrayList<>();
    private int linesCount1;

    @Override
    public String getName() {
        return "Różnice w liniach kodu";
    }

    @Override
    public String resultToString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Podobne wiersze:\n");
        for(String sameRow: sameRows) {
            builder.append(sameRow).append("\n");
        }
        return builder.toString();
    }

    @Override
    public Result getResult() {
        return new DoubleWrapper(sameRows.size());
    }

    @Override
    public double getWeight() {
        return 8;
    }

    @Override
    public double assess(Result otherResult) {
        double thisResult = (double)getResult().getValue();
        double relative;
        if(thisResult == 0) {
            return 0;
        }
        else
            relative = thisResult < linesCount1 ? thisResult / linesCount1 : linesCount1 / thisResult;
        return relative * 100;
    }

    private static boolean hasOnlyCurlyBraces(String s) {
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(!Character.isWhitespace(c) && c != '{' && c != '}') {
                return false;
            }
        }
        return true;
    }

    @Override
    public void process(String fileContent, String otherFileContent) {
        List<String> lines1 = fileContent.lines().collect(Collectors.toList());
        List<String> lines2 = otherFileContent.lines().collect(Collectors.toList());
        linesCount1 = lines1.size();
        for(String line1: lines1) {
            if(lines2.contains(line1)) {
                if(!hasOnlyCurlyBraces(line1)) {
                    sameRows.add(line1);
                } else {
                    linesCount1--;
                }
                lines2.remove(line1);
            }
        }
    }
}
