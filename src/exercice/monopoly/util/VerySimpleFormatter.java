package exercice.monopoly.util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * VerySimpleFormatter class a very simple formatter for Java Logging that just prints the message on a new line
 */
public class VerySimpleFormatter extends Formatter {

    //// Public Functions ////

    /**
     * {@inheritDoc}
     */
    @Override
    public String format(LogRecord record) {
        return record.getMessage() + "\n";
    }
}
