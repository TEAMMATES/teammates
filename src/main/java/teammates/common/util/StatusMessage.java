package teammates.common.util;

import teammates.common.util.Const.StatusMessageColor;

public class StatusMessage {
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
