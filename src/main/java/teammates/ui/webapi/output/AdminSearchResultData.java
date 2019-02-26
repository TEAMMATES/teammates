package teammates.ui.webapi.output;

import java.util.List;

import teammates.common.datatransfer.InstructorAccountSearchResult;
import teammates.common.datatransfer.StudentAccountSearchResult;

/**
 * Output format for admin search result.
 */
public class AdminSearchResultData extends ApiOutput {
    private final List<StudentAccountSearchResult> students;
    private final List<InstructorAccountSearchResult> instructors;

    public AdminSearchResultData(List<StudentAccountSearchResult> students,
                                 List<InstructorAccountSearchResult> instructors) {
        this.students = students;
        this.instructors = instructors;
    }

    public List<StudentAccountSearchResult> getStudents() {
        return students;
    }

    public List<InstructorAccountSearchResult> getInstructors() {
        return instructors;
    }
}
