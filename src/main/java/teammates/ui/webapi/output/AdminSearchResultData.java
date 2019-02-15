package teammates.ui.webapi.output;

import java.util.List;

import teammates.common.datatransfer.InstructorBundle;
import teammates.common.datatransfer.StudentBundle;

/**
 * Output format for admin search result.
 */
public class AdminSearchResultData extends ApiOutput {
    private final List<StudentBundle> students;
    private final List<InstructorBundle> instructors;

    public AdminSearchResultData(List<StudentBundle> students, List<InstructorBundle> instructors) {
        this.students = students;
        this.instructors = instructors;
    }

    public List<StudentBundle> getStudents() {
        return students;
    }

    public List<InstructorBundle> getInstructors() {
        return instructors;
    }
}
