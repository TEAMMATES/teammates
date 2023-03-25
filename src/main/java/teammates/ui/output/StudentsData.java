package teammates.ui.output;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import teammates.storage.sqlentity.Student;

/**
 * The API output format of a list of {@link StudentData}.
 */
public class StudentsData extends ApiOutput {

    private List<StudentData> students;

    public StudentsData() {
        this.students = new ArrayList<>();
    }

    public StudentsData(List<Student> students) {
        this.students = students.stream().map(StudentData::new).collect(Collectors.toList());
    }

    public List<StudentData> getStudents() {
        return students;
    }

    public void setStudents(List<StudentData> students) {
        this.students = students;
    }
}
