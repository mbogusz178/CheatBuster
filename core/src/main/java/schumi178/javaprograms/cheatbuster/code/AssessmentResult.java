package schumi178.javaprograms.cheatbuster.code;

import schumi178.javaprograms.cheatbuster.code.base.Assessable;

import java.util.Iterator;
import java.util.List;

public class AssessmentResult {
    private final List<Assessable> resultListeners;
    private final List<Double> results;
    private final int finalResult;

    public AssessmentResult(List<Assessable> resultListeners, List<Double> results, int finalResult) {
        this.resultListeners = resultListeners;
        this.results = results;
        this.finalResult = finalResult;
    }

    public Iterator<Assessable> resultIterator() {
        return resultListeners.iterator();
    }

    public int getFinalResult() {
        return finalResult;
    }

    public Iterator<Double> resultDoubleIterator() {
        return results.iterator();
    }
}
