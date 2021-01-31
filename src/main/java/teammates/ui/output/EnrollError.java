package teammates.ui.output;

/**
 * Wrapper class for students who failed to be enrolled.
 */
public class EnrollError {
    private String studentEmail;
    private String errorMessage;

    public EnrollError(String studentEmail, String errorMessage) {
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
