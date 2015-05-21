package teammates.common.exception;

@SuppressWarnings("serial")
public class PageNotFoundException extends RuntimeException {
    public PageNotFoundException(String message) {
        super(message);
    }
}
