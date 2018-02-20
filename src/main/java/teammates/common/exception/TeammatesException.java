package teammates.common.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

@SuppressWarnings("serial")
public class TeammatesException extends Exception {
    public String errorCode;

    public TeammatesException() {
        super();
    }

    public TeammatesException(String message) {
        super(message);
    }

    public TeammatesException(String errorcode, String message) {
        super(message);
        errorCode = errorcode;
    }

    public TeammatesException(Throwable cause) {
        super(cause);
    }

    public static String toStringWithStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
            return System.lineSeparator() + sw.toString();
        }
    }
}
