package teammates.common.util;

import java.util.List;

/**
 * Formats list of error messages inorder to parse them in the front-end.
 */
public final class ErrorMessageFormatter {
    private static final String DELIMITER = Character.valueOf((char) Const.ERROR_MESSAGE_DELIMITER).toString();

    private ErrorMessageFormatter() {
    }

    /**
     * Returns a string of messages joined using a delimiter.
     */
    public static String format(List<String> messages) {
        return String.join(DELIMITER, messages);
    }
}
