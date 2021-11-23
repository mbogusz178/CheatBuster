package schumi178.javaprograms.cheatbuster.util;

import java.util.List;

public class ListStringWrapper implements ListStringResult {
    private final List<String> list;

    public ListStringWrapper(List<String> list) {
        this.list = list;
    }

    @Override
    public Object getValue() {
        return list;
    }

    @Override
    public List<String> getResult() {
        return list;
    }
}
