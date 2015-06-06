package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.CourseAttributes;

public class CourseTable {
    private String courseId;
    private String courseName;
    private List<CourseTableLink> buttons;
    private List<CourseTableSessionRow> rows;
    
    public CourseTable(CourseAttributes course, List<CourseTableLink> buttons, List<CourseTableSessionRow> rows) {
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
    
    public List<CourseTableLink> getButtons() {
        return buttons;
    }
    
    public List<CourseTableSessionRow> getRows() {
        return rows;
    }
}