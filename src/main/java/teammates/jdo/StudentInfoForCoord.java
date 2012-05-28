package teammates.jdo;

import com.google.appengine.api.datastore.Text;

public class StudentInfoForCoord {
	
	public StudentInfoForCoord(Student student) {
		this.id = student.getID();
		this.email = student.getEmail();
		this.courseId = student.getCourseID();
		this.name = student.getName();
		this.comments = student.getComments();
		this.registrationKey = student.getRegistrationKey();
		this.teamName = student.getTeamName();
		this.courseArchived = student.isCourseArchived();
		this.profileSummary = student.getProfileSummary();
		this.profileDetail = student.getProfileDetail();
		this.updateStatus = UpdateStatus.UNKNOWN;
	}

	public enum UpdateStatus{
		MODIFIED, UNMODIFIED, NEW, UNKNOWN;
	}
	
	public String id;

	public String email;

	public String courseId;

	public String name;

	public String comments;

	public transient Long registrationKey;

	public String teamName;

	public transient boolean courseArchived;
	
	public String profileSummary;
	
	public Text profileDetail;
	
	public UpdateStatus updateStatus;

}
