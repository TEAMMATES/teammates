package teammates.common.datatransfer;

import teammates.common.datatransfer.StudentAttributes.UpdateStatus;

public class StudentEnrollDetails {
	public StudentAttributes.UpdateStatus updateStatus;
	public String course;
	public String email;
	public String oldTeam;
	public String newTeam;
	
	public StudentEnrollDetails() {
		updateStatus = UpdateStatus.UNKNOWN;
		course = null;
		email = null;
		oldTeam = null;
		newTeam = null;
	}
	
	public StudentEnrollDetails(StudentAttributes.UpdateStatus updateStatus, String course,
			String email, String oldTeam, String newTeam) {
		this.updateStatus = updateStatus;
		this.course = course;
		this.email = email;
		this.oldTeam = oldTeam;
		this.newTeam = newTeam;
	}
}
