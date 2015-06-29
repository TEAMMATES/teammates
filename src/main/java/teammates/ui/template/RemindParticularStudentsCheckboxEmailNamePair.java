package teammates.ui.template;

public class RemindParticularStudentsCheckboxEmailNamePair {
    private String studentEmail;
    private String studentName;
    
    public RemindParticularStudentsCheckboxEmailNamePair(String email, String name) {
        studentEmail = email;
        studentName = name;
    }
    
    public String getStudentEmail() {
        return studentEmail;
    }
    
    public String getStudentName() {
        return studentName;
    }
}