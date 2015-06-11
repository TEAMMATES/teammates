package teammates.ui.template;

import java.util.List;

/**
 * A table contains details of students in a course whose student details contain the search
 * keyword entered by the instructor.
 */
public class SearchStudentsTable {
    private String courseId;
    private List<StudentRow> studentRows;
    
    public SearchStudentsTable(String courseId, List<StudentRow> studentRows) {
        this.courseId = courseId;
        this.studentRows = studentRows;
    }
    
    public String getCourseId() {
        return courseId;
    }
    
    public List<StudentRow> getStudentRows() {
        return studentRows;
    }
}
