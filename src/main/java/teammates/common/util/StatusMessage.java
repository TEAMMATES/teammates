package teammates.common.util;

import java.io.Serializable;

/**
 * The {@code StatusMessage} class encapsulates the text of status message
 * and its level of seriousness of the status message (the color of the message).
 */
@SuppressWarnings("serial")
public class StatusMessage implements Serializable {
    private String text;
    private StatusMessageColor color;

    public StatusMessage(String text, StatusMessageColor color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public String getColor() {
        return color.name().toLowerCase();
    }
}
