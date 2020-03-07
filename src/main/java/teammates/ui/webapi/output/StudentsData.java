package teammates.ui.webapi.output;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * The API output format of a list of {@link StudentAttributes}.
 */
public class StudentsData extends ApiOutput {

    private final List<StudentData> students;

    public StudentsData(List<StudentAttributes> students) {
        this.students = new ArrayList<>();
        for (StudentAttributes s : students) {
            StudentData data = new StudentData(s);
            data.setGoogleId(s.googleId);
            this.students.add(data);
        }
    }

    public List<StudentData> getStudents() {
        return students;
    }
}
