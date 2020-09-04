package nix.edu.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FinanceLogger {

    private static final Logger logger = LoggerFactory.getLogger(FinanceLogger.class);

    public static Logger getLogger() {
        return logger;
    }
}
