package teammates.ui.output;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Generic output format for message-producing endpoint.
 */
public class MessageOutput extends ApiOutput {

    private final String message;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public MessageOutput(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
