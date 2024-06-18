package fr.yronusa.llmcraft;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class Logger {

    public static java.util.logging.Logger logger;

    public static void log(Level level, String s){
        logger.log(new LogRecord(level, s));
    }
}
