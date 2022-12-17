// Purpose: To act as a wrapper to System.out.println() and to provide testing utilities

public class Log {

    //TODO improve log functions so they are more user friendly and not just for developer use

    // print headers
    private static final String LOG = "[ LOG ] ";
    private static final String ERROR = "<ERROR> ";
    private static final String EXCEPTION_START = " | Error type";

    // print format
    private static final String FORMAT = "%-50s %s\n";

    // general print functions for logging

    // base print function
    private static void print(String logType, String message, Object value) {
        System.out.printf(FORMAT, logType + message + ":", value);
    }

    // overloaded print, add message to log, print message and value
    public static void print(String message, Object value) {
        print(LOG, message, value);
    }

    // overloaded print, make message empty, print value
    public static void print(Object value) {
        print("", value);
    }

    // print functions for logging errors

    public static void  error(String message, Object value) {
        System.out.printf("%-30s %s", ERROR + message, value);
    }

    // overloaded error, make message empty, print value
    public static void error(Object value) {
        System.out.println(ERROR + value);
    }

    public static void errorE(String message, Exception exception) {
        System.out.printf(FORMAT, ERROR, message + EXCEPTION_START + exception);
    }
}
