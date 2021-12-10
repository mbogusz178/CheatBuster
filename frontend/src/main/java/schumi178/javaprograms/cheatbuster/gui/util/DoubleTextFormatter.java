package schumi178.javaprograms.cheatbuster.gui.util;

import javafx.scene.control.TextFormatter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DoubleTextFormatter extends TextFormatter<Double> {

    public DoubleTextFormatter() {
        super(t -> {
            Locale locale = Locale.getDefault();
            DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance(locale);
            DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
            char sep = symbols.getDecimalSeparator();
            if (t.isReplaced())
                if (t.getText().matches("[^0-9]"))
                    t.setText(t.getControlText().substring(t.getRangeStart(), t.getRangeEnd()));


            if (t.isAdded()) {
                if (t.getControlText().contains("" + sep)) {
                    if (t.getText().matches("[^0-9]")) {
                        t.setText("");
                    }
                } else if (t.getText().matches("[^0-9" + sep + "]")) {
                    t.setText("");
                }
            }

            return t;
        });
    }
}
