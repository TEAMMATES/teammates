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
    
    public StudentEnrollDetails(final StudentAttributes.UpdateStatus updateStatus, final String course,
            final String email, final String oldTeam, final String newTeam, final String oldSection, final String newSection) {
        this.updateStatus = updateStatus;
        this.course = course;
        this.email = email;
        this.oldTeam = oldTeam;
        this.newTeam = newTeam;
        this.oldSection = oldSection;
        this.newSection = newSection;
    }
}
