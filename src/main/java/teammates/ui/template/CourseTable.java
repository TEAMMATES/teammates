package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.util.Sanitizer;

public class CourseTable {
    private String courseId;
    private String courseName;
    private List<ElementTag> buttons;
    private List<HomeFeedbackSessionRow> rows;
    
    public CourseTable(CourseAttributes course, List<ElementTag> buttons, List<HomeFeedbackSessionRow> rows) {
        this.courseId = course.id;
        this.courseName = course.name;
        this.buttons = buttons;
        this.rows = rows;
    }

    public String getCourseId() {
        return courseId;
    }
    
    public String getCourseName() {
        return Sanitizer.sanitizeForHtml(courseName);
    }
    
    public List<ElementTag> getButtons() {
        return buttons;
    }
    
    public List<HomeFeedbackSessionRow> getRows() {
        return rows;
    }
}