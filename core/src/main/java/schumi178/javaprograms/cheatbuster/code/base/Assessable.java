package schumi178.javaprograms.cheatbuster.code.base;

import schumi178.javaprograms.cheatbuster.util.Result;

public interface Assessable extends NamedListener {
    String resultToString();
    Result getResult();
    double getWeight();
    double assess(Result otherResult);
}
