package teammates.common.datatransfer;

import teammates.common.datatransfer.attributes.StudentAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of an enrollment of students to a course.
 */
public class CourseEnrollmentResult {

    public List<StudentAttributes> studentList = new ArrayList<StudentAttributes>();
    public List<StudentEnrollDetails> enrollmentList = new ArrayList<StudentEnrollDetails>();

    public CourseEnrollmentResult(List<StudentAttributes> studentList, List<StudentEnrollDetails> enrollmentList) {
        this.studentList = studentList;
        this.enrollmentList = enrollmentList;
    }

}
