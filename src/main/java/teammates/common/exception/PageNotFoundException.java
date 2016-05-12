package teammates.common.exception;

@SuppressWarnings("serial")
public class PageNotFoundException extends RuntimeException {
    public PageNotFoundException(final String message) {
        super(message);
    }
}
