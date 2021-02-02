package teammates.ui.output;

import java.util.List;

/**
 * The API output wrapper for enroll results.
 */
public class EnrollStudentsData extends ApiOutput {

    private StudentsData studentsData;
    private List<EnrollErrorResults> failToEnrollStudents;

    public EnrollStudentsData(StudentsData studentsData, List<EnrollErrorResults> failToEnrollStudents) {
        this.studentsData = studentsData;
        this.failToEnrollStudents = failToEnrollStudents;
    }

    public List<EnrollErrorResults> getFailToEnrollStudents() {
        return failToEnrollStudents;
    }

    public void setFailToEnrollStudents(List<EnrollErrorResults> failToEnrollStudents) {
        this.failToEnrollStudents = failToEnrollStudents;
    }

    public StudentsData getStudentsData() {
        return studentsData;
    }

    public void setStudentsData(StudentsData studentsData) {
        this.studentsData = studentsData;
    }

    /**
     * Wrapper class for fail to enroll students.
     */
    public static class EnrollErrorResults {
        private String studentEmail;
        private String errorMessage;

        public EnrollErrorResults(String studentEmail, String errorMessage) {
            this.studentEmail = studentEmail;
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getStudentEmail() {
            return studentEmail;
        }

        public void setStudentEmail(String studentEmail) {
            this.studentEmail = studentEmail;
        }
    }
}
