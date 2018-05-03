package exercice.monopoly.util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 *
 */
public class VerySimpleFormatter extends Formatter {

    //// Public Functions ////

    /**
     * @param record
     * @return
     */
    @Override
    public String format(LogRecord record) {
        return record.getMessage() + "\n";
    }
}
