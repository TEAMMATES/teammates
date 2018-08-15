package teammates.ui.template;

import java.util.List;

public class SoftDeletedCoursesTableRow {
    private String courseId;
    private String courseName;
    private String createdAtDateString;
    private String createdAtDateStamp;
    private String createdAtFullDateTimeString;
    private String deletedAtDateString;
    private String deletedAtDateStamp;
    private String deletedAtFullDateTimeString;
    private String href;
    private List<ElementTag> actions;

    public SoftDeletedCoursesTableRow(String courseIdParam, String courseNameParam,
            String createdAtDateStringParam, String createdAtDateStampParam, String createdAtFullDateTimeStringParam,
            String deletedAtDateStringParam, String deletedAtDateStampParam, String deletedAtFullDateTimeStringParam,
            String href, List<ElementTag> actionsParam) {
        this.courseId = courseIdParam;
        this.courseName = courseNameParam;
        this.createdAtDateString = createdAtDateStringParam;
        this.createdAtDateStamp = createdAtDateStampParam;
        this.createdAtFullDateTimeString = createdAtFullDateTimeStringParam;
        this.deletedAtDateString = deletedAtDateStringParam;
        this.deletedAtDateStamp = deletedAtDateStampParam;
        this.deletedAtFullDateTimeString = deletedAtFullDateTimeStringParam;
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

    public String getDeletedAtDateString() {
        return deletedAtDateString;
    }

    public String getDeletedAtDateStamp() {
        return deletedAtDateStamp;
    }

    public String getDeletedAtFullDateTimeString() {
        return deletedAtFullDateTimeString;
    }

    public String getHref() {
        return href;
    }

    public List<ElementTag> getActions() {
        return actions;
    }

}
