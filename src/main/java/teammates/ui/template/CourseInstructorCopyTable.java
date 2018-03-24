package teammates.ui.template;

import java.util.List;

/**
 * Data model for the copy-instructor-table in InstructorCourseInstructorCopyModal.jsp
 * which is then inserted and displayed in copyInstructorModal.tag.
 */
public class CourseInstructorCopyTable {
    private List<CourseInstructorCopyTableRow> instructorRows;

    public CourseInstructorCopyTable(List<CourseInstructorCopyTableRow> instructorRows) {
        this.instructorRows = instructorRows;
    }

    public List<CourseInstructorCopyTableRow> getInstructorRows() {
        return instructorRows;
    }
}
