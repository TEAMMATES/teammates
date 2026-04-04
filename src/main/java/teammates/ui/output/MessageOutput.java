package teammates.ui.output;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Generic output format for message-producing endpoint.
 */
public class MessageOutput extends ApiOutput {

    private final String message;

    @JsonCreator
    public MessageOutput(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
