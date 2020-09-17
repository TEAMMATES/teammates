package teammates.ui.output;

/**
 * Generic output format for message-producing endpoint.
 */
public class MessageOutput extends ApiOutput {

    private final String message;

    public MessageOutput(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
