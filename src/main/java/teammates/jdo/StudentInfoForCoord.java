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
		MODIFIED, 
		UNMODIFIED, 
		NEW, 
		UNKNOWN, 
		ERROR;
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

	public boolean isEnrollmentInfoMatchingTo(Student student, UpdateStatus updateStatus) {
//		return (this.id.equals(student.getID())) &&
//		(this.email.equals(student.getEmail())) &&
//		(this.courseId.equals(student.getCourseID())) &&
//		(this.name.equals(student.getName())) &&
//		(this.comments.equals(student.getComments())) &&
//		(this.registrationKey.equals(student.getRegistrationKey())) &&
//		(this.teamName.equals(student.getTeamName())) &&
//		(this.courseArchived== student.isCourseArchived()) &&
//		(this.profileSummary.equals(student.getProfileSummary())) &&
//		(this.profileDetail.equals(student.getProfileDetail())) &&
//		(this.updateStatus == updateStatus);
		return (this.email.equals(student.getEmail())) &&
				(this.courseId.equals(student.getCourseID())) &&
				(this.name.equals(student.getName())) &&
				(this.comments.equals(student.getComments())) &&
				(this.teamName.equals(student.getTeamName())) &&
				(this.updateStatus == updateStatus);
	}

}
