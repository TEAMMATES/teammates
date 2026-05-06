package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.storage.entity.Student;

/**
 * Represents the results of enrolling students in a course.
 */
public class EnrollResults {
    List<Student> enrolledStudents;
    // key: student email, value: error message
    Map<String, String> unsuccessfulEnrolls;

    public EnrollResults() {
        this.enrolledStudents = new ArrayList<>();
        this.unsuccessfulEnrolls = new HashMap<>();
    }

    public EnrollResults(List<Student> enrolledStudents, Map<String, String> unsuccessfulEnrolls) {
        this.enrolledStudents = enrolledStudents;
        this.unsuccessfulEnrolls = unsuccessfulEnrolls;
    }

    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public Map<String, String> getUnsuccessfulEnrolls() {
        return unsuccessfulEnrolls;
    }

    /**
     * Adds a successfully enrolled student to the results.
     * This could be a new enrollment or an existing student that is already enrolled in the course.
     */
    public void addEnrolledStudent(Student student) {
        enrolledStudents.add(student);
    }

    /**
     * Adds an error message for a student that failed to enroll.
     */
    public void addUnsuccessfulEnroll(String studentEmail, String errorMessage) {
        unsuccessfulEnrolls.put(studentEmail, errorMessage);
    }
}
