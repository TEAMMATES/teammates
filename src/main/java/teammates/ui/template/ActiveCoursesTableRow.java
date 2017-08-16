package teammates.ui.template;

import java.util.List;

public class ActiveCoursesTableRow {
    private String courseId;
    private String courseName;
    private String createdAtDateString;
    private String createdAtDateTimeString;
    private String href;
    private List<ElementTag> actions;

    public ActiveCoursesTableRow(String courseIdParam, String courseNameParam,
                                 String createdAtDateStringParam, String createdAtDateTimeStringParam,
                                 String href, List<ElementTag> actionsParam) {
        this.courseId = courseIdParam;
        this.courseName = courseNameParam;
        this.createdAtDateString = createdAtDateStringParam;
        this.createdAtDateTimeString = createdAtDateTimeStringParam;
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

    public String getCreatedAtDateTimeString() {
        return createdAtDateTimeString;
    }

    public String getHref() {
        return href;
    }

    public List<ElementTag> getActions() {
        return actions;
    }

}
