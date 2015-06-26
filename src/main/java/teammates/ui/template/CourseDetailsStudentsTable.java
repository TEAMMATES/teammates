package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

public class CourseDetailsStudentsTable {
    private List<CourseDetailsStudentsTableRow> rows;
    
    public CourseDetailsStudentsTable() {
        rows = new ArrayList<CourseDetailsStudentsTableRow>();
    }
    
    public List<CourseDetailsStudentsTableRow> getRows() {
        return rows;
    }
}
