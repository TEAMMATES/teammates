package teammates.common.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

@SuppressWarnings("serial")
public class TeammatesException extends Exception {
    public String errorCode;

    public TeammatesException() {
        super();
    }

    public TeammatesException(final String message) {
        super(message);
    }

    public TeammatesException(final String errorcode, final String message) {
        super(message);
        errorCode = errorcode;
    }

    public static String toStringWithStackTrace(final Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return "\n" + sw.toString();
    }
}
