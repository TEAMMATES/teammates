package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.StudentAttributes;

public class EnrollResultPanel {
    private String panelClass;
    private String messageForEnrollmentStatus;
    private List<StudentAttributes> studentList;
    
    public EnrollResultPanel(final String panelClass, final String messageForEnrollmentStatus,
                                    final List<StudentAttributes> studentList) {
        this.panelClass = panelClass;
        this.messageForEnrollmentStatus = messageForEnrollmentStatus;
        this.studentList = studentList;
    }
    
    public String getPanelClass() {
        return panelClass;
    }
    
    public String getMessageForEnrollmentStatus() {
        return messageForEnrollmentStatus;
    }
    
    public List<StudentAttributes> getStudentList() {
        return studentList;
    }
}
