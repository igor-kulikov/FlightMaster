package ua.ikulikov.flightmaster.skyscannerservice.utils;

import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class Utils {
    private static Logger logger = Logger.getLogger(Utils.class);

    private Utils() {
        // this is utility class, no instantiation is allowed
    }

    public static void sleep(int sleepDurationMs, String message) {
        try {
            if (!message.isEmpty())
                logger.info(String.format("%s - [%ds]", message, TimeUnit.MILLISECONDS.toSeconds(sleepDurationMs)));
            Thread.sleep(sleepDurationMs);
        }
        catch (InterruptedException e) {
            // interrupted from outside - restore interrupted state...
            Thread.currentThread().interrupt();
        }
    }
}