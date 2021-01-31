package teammates.ui.output;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * The API output format of a list of {@link StudentAttributes}.
 */
public class StudentsData extends ApiOutput {

    private List<StudentData> students;
    private List<EnrollError> failToEnrollStudents;

    public StudentsData() {
        this.students = new ArrayList<>();
    }

    public StudentsData(List<StudentAttributes> students) {
        this.students = students.stream().map(StudentData::new).collect(Collectors.toList());
    }

    public StudentsData(List<StudentAttributes> students, List<EnrollError> failToEnrollStudents) {
        this.students = students.stream().map(StudentData::new).collect(Collectors.toList());
        this.failToEnrollStudents = failToEnrollStudents;
    }

    public List<StudentData> getStudents() {
        return students;
    }

    public void setStudents(List<StudentData> students) {
        this.students = students;
    }

    public List<EnrollError> getFailToEnrollStudents() {
        return failToEnrollStudents;
    }

    public void setFailToEnrollStudents(List<EnrollError> failToEnrollStudents) {
        this.failToEnrollStudents = failToEnrollStudents;
    }
}
