package schumi178.javaprograms.cheatbuster.code.listeners;

import schumi178.javaprograms.cheatbuster.code.CBaseListener;
import schumi178.javaprograms.cheatbuster.code.base.Assessable;
import schumi178.javaprograms.cheatbuster.code.base.FileProcessable;
import schumi178.javaprograms.cheatbuster.util.DoubleWrapper;
import schumi178.javaprograms.cheatbuster.util.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JavaDiffUtilsDetector extends CBaseListener implements FileProcessable {

    private final List<String> sameRowsLeft = new ArrayList<>();
    private final List<String> sameRowsRight = new ArrayList<>();
    private int linesCount1;
    private int linesCount2;

    @Override
    public String getName() {
        return "Różnice w liniach kodu";
    }

    @Override
    public String resultToString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Podobne wiersze:\n");
        for(String sameRow: sameRowsLeft) {
            builder.append(sameRow).append("\n");
        }
        return builder.toString();
    }

    @Override
    public Result getResult() {
        return new DoubleWrapper((sameRowsLeft.size() + sameRowsRight.size()) / 2.0);
    }

    @Override
    public double getWeight() {
        return 8;
    }

    @Override
    public double assess(Result otherResult) {
        double thisResult = (double)getResult().getValue();
        double linesCountAvg = (linesCount1 + linesCount2) / 2.0;
        double relative;
        if(thisResult == 0) {
            return 0;
        }
        else
            relative = thisResult < linesCountAvg ? thisResult / linesCountAvg : linesCountAvg / thisResult;
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
        linesCount2 = lines2.size();
        List<String> lines2Copy = List.copyOf(lines2);
        for(String line1: lines1) {
            if(lines2.contains(line1)) {
                if(!hasOnlyCurlyBraces(line1)) {
                    sameRowsLeft.add(line1);
                } else {
                    linesCount1--;
                }
                lines2.remove(line1);
            }
        }
        for(String line2: lines2Copy) {
            if(lines1.contains(line2)) {
                if(!hasOnlyCurlyBraces(line2)) {
                    sameRowsRight.add(line2);
                } else {
                    linesCount2--;
                }
                lines1.remove(line2);
            }
        }
    }
}
