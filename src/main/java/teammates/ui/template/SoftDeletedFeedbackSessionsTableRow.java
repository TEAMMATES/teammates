package teammates.ui.template;

import java.util.List;

public class SoftDeletedFeedbackSessionsTableRow {
    private String courseId;
    private String sessionName;
    private String createdTimeDateString;
    private String createdTimeDateStamp;
    private String createdTimeFullDateTimeString;
    private String deletedTimeDateString;
    private String deletedTimeDateStamp;
    private String deletedTimeFullDateTimeString;
    private String href;
    private List<ElementTag> actions;

    public SoftDeletedFeedbackSessionsTableRow(String courseIdParam, String sessionNameParam,
            String createdTimeDateStringParam, String createdTimeDateStampParam, String createdTimeFullDateTimeStringParam,
            String deletedTimeDateStringParam, String deletedTimeDateStampParam, String deletedTimeFullDateTimeStringParam,
            String href, List<ElementTag> actionsParam) {
        this.courseId = courseIdParam;
        this.sessionName = sessionNameParam;
        this.createdTimeDateString = createdTimeDateStringParam;
        this.createdTimeDateStamp = createdTimeDateStampParam;
        this.createdTimeFullDateTimeString = createdTimeFullDateTimeStringParam;
        this.deletedTimeDateString = deletedTimeDateStringParam;
        this.deletedTimeDateStamp = deletedTimeDateStampParam;
        this.deletedTimeFullDateTimeString = deletedTimeFullDateTimeStringParam;
        this.href = href;
        this.actions = actionsParam;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public String getCreatedTimeDateString() {
        return createdTimeDateString;
    }

    public String getCreatedTimeDateStamp() {
        return createdTimeDateStamp;
    }

    public String getCreatedTimeFullDateTimeString() {
        return createdTimeFullDateTimeString;
    }

    public String getDeletedTimeDateString() {
        return deletedTimeDateString;
    }

    public String getDeletedTimeDateStamp() {
        return deletedTimeDateStamp;
    }

    public String getDeletedTimeFullDateTimeString() {
        return deletedTimeFullDateTimeString;
    }

    public String getHref() {
        return href;
    }

    public List<ElementTag> getActions() {
        return actions;
    }

}
