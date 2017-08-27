package teammates.ui.template;

import java.util.List;

public class ActiveCoursesTableRow {
    private String courseId;
    private String courseName;
    private String createdAtDateString;
    private String createdAtDateStamp;
    private String createdAtFullDateTimeString;
    private String href;
    private List<ElementTag> actions;

    public ActiveCoursesTableRow(String courseIdParam, String courseNameParam,
                                 String createdAtDateStringParam, String createdAtDateStampParam,
                                 String createdAtFullDateTimeStringParam, String href, List<ElementTag> actionsParam) {
        this.courseId = courseIdParam;
        this.courseName = courseNameParam;
        this.createdAtDateString = createdAtDateStringParam;
        this.createdAtDateStamp = createdAtDateStampParam;
        this.createdAtFullDateTimeString = createdAtFullDateTimeStringParam;
        this.href = href;
        this.actions = actionsParam;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCreatedAtDateString() {
        return createdAtDateString;
    }

    public String getCreatedAtDateStamp() {
        return createdAtDateStamp;
    }

    public String getCreatedAtFullDateTimeString() {
        return createdAtFullDateTimeString;
    }

    public String getHref() {
        return href;
    }

    public List<ElementTag> getActions() {
        return actions;
    }

}
