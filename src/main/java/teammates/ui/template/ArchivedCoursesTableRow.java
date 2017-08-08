package teammates.ui.template;

import java.util.List;

public class ArchivedCoursesTableRow {
    private String courseId;
    private String courseName;
    private String createdAtDateString;
    private List<ElementTag> actions;

    public ArchivedCoursesTableRow(String courseIdParam, String courseNameParam,
            String createdAtDateStringParam, List<ElementTag> actionsParam) {
        this.courseId = courseIdParam;
        this.courseName = courseNameParam;
        this.createdAtDateString = createdAtDateStringParam;
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

    public List<ElementTag> getActions() {
        return actions;
    }
}
