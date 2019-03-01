package teammates.ui.webapi.output;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * The API output format of a list of {@link StudentAttributes}.
 */
public class StudentsData extends ApiOutput {

    private final List<StudentData> students;

    public StudentsData(List<StudentAttributes> students) {
        this.students = students.stream().map(StudentData::new).collect(Collectors.toList());
    }

    public List<StudentData> getStudents() {
        return students;
    }
}
