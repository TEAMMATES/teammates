package teammates.common.util;

import java.io.Serializable;

import teammates.common.util.Const.StatusMessageColor;

/**
 * The {@code StatusMessage} class encapsulates the text of status message 
 * and its level of seriousness of the status message (the color of the message).
 */
public class StatusMessage implements Serializable {
    private String text;
    private String color;
    
    public StatusMessage(String text, StatusMessageColor color) {
        this.text = text;
        
        switch (color) {
            case SUCCESS:
                this.color = "success";
                break;
            case WARNING:
                this.color = "warning";
                break;
            case DANGER:
                this.color = "danger";
                break;
            case INFO:
            default:
                this.color = "info";
        }
    }
    
    public String getText() {
        return text;
    }
    
    public String getColor() {
        return color;
    }
}
