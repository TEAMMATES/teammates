package teammates.ui.webapi.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * Contains Students search results.
 */
public class SearchStudentsResult extends ApiOutput {
    private final List<SearchStudentData> students;

    public SearchStudentsResult(List<StudentAttributes> students) {
        this.students = students.stream().map(SearchStudentData::new).collect(Collectors.toList());
    }

    public List<SearchStudentData> getStudents() {
        return students;
    }
}
