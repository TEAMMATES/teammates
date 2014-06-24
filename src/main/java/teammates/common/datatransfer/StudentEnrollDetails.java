package teammates.common.datatransfer;

import teammates.common.datatransfer.StudentAttributes.UpdateStatus;

public class StudentEnrollDetails {
    public StudentAttributes.UpdateStatus updateStatus;
    public String course;
    public String email;
    public String oldTeam;
    public String newTeam;
    public String oldSection;
    public String newSection;
    
    public StudentEnrollDetails() {
        updateStatus = UpdateStatus.UNKNOWN;
        course = null;
        email = null;
        oldTeam = null;
        newTeam = null;
        oldSection = null;
        newSection = null;
    }
    
    public StudentEnrollDetails(StudentAttributes.UpdateStatus updateStatus, String course,
            String email, String oldTeam, String newTeam, String oldSection, String newSection) {
        this.updateStatus = updateStatus;
        this.course = course;
        this.email = email;
        this.oldTeam = oldTeam;
        this.newTeam = newTeam;
        this.oldSection = oldSection;
        this.newSection = newSection;
    }
}
