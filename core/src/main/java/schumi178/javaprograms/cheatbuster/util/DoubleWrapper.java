package schumi178.javaprograms.cheatbuster.util;

public class DoubleWrapper implements Result {

    private final double value;

    public DoubleWrapper(double value) {
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }
}
