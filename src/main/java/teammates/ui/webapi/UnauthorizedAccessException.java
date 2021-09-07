package teammates.ui.webapi;

/**
 * Exception thrown when an entity is attempting to request for resources it does not have permission for.
 *
 * <p>This corresponds to HTTP 403 error.
 */
public class UnauthorizedAccessException extends Exception {

    /**
     * Indicates whether the error message will be shown to the user (as the API response).
     * If this flag is set to "false", a generic error message will be displayed instead.
     * The actual error message is always displayed to admin in the logs.
     *
     * <p>As this exception involves access control, the error message need to be redacted
     * in most cases because the full error message can give clues as to what needs to be done
     * in order to get the access (which should be unauthorized).
     * While it is true that there is little that user can do to elevate his/her privilege
     * even with that knowledge, we still need to do our part.
     *
     * <p>There are, however, some cases whereby the error message would be legitimately helpful
     * to users, e.g. when submitting responses to a feedback session that has not opened yet.
     * For such cases, it is appropriate to set this flag as "true".
     */
    private final boolean showErrorMessage;

    public UnauthorizedAccessException(String message) {
        this(message, false);
    }

    public UnauthorizedAccessException(String message, boolean showErrorMessage) {
        super(message);
        this.showErrorMessage = showErrorMessage;
    }

    public boolean isShowErrorMessage() {
        return showErrorMessage;
    }

}
