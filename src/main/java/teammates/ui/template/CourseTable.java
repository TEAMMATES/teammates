package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.CourseAttributes;

public class CourseTable {
    private String courseId;
    private String courseName;
    private List<ElementTag> buttons;
    private List<CourseTableSessionRow> rows;
    
    public CourseTable(CourseAttributes course, List<ElementTag> buttons, List<CourseTableSessionRow> rows) {
        this.courseId = course.id;
        this.courseName = course.name;
        this.buttons = buttons;
        this.rows = rows;
    }

    public String getCourseId() {
        return courseId;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public List<ElementTag> getButtons() {
        return buttons;
    }
    
    public List<CourseTableSessionRow> getRows() {
        return rows;
    }
}