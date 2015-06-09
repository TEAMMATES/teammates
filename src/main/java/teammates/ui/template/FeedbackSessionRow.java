package teammates.ui.template;


public class FeedbackSessionRow {
    
    
    
    String courseId;
    String name;
    String tooltip;
    
    String href;
    String recent;
    
    String status;  // Awaiting, Open, Closed..
    String actions;
    
    
    ElementTag rowAttributes;

    /**
     * Constructs a session row for a course table.
     * @param courseId
     * @param name name of the session
     * @param tooltip tooltip displayed when hovering over status
     * @param status status of the session
     * @param href link for the session under response rate
     * @param recent if the session is considered recent (calculate response rate on load if true)
     * @param actions possible actions to do on the session, a block of HTML representing the formatted actions
     */
    public FeedbackSessionRow(String courseId, String name, String tooltip, String status, 
                                  String href, String actions, ElementTag attributes) {
        this.courseId = courseId;
        this.name = name;
        this.status = status;
        this.actions = actions;
        this.rowAttributes = attributes;
    }
    

    public String getCourseId() {
        return courseId;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }


    
    public String getTooltip() {
        return tooltip;
    }


    public String getHref() {
        return href;
    }


    public String getRecent() {
        return recent;
    }

    
    public String getActions() {
        return actions;
    }
    
    public ElementTag getRowAttributes() {
        return rowAttributes;
    }
    
    
}
