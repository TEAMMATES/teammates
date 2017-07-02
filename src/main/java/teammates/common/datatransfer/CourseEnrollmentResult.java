package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * Represents the result of an enrollment of students to a course.
 */
public class CourseEnrollmentResult {

    public List<StudentAttributes> studentList = new ArrayList<>();
    public List<StudentEnrollDetails> enrollmentList = new ArrayList<>();

    public CourseEnrollmentResult(List<StudentAttributes> studentList, List<StudentEnrollDetails> enrollmentList) {
        this.studentList = studentList;
        this.enrollmentList = enrollmentList;
    }

}
