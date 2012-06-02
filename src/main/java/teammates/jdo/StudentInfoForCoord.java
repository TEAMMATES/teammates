package teammates.jdo;

import teammates.datatransfer.StudentData;

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
	
	public StudentInfoForCoord(StudentData student) {
		this.id = student.id;
		this.email = student.email;
		this.courseId = student.courseId;
		this.name = student.name;
		this.comments = student.comments;
		this.teamName = student.team;
		this.courseArchived = false;
		this.profileSummary = null;
		this.profileDetail = student.profile;
		this.updateStatus = student.updateStatus;
	}

	public enum UpdateStatus{
		MODIFIED, 
		UNMODIFIED, 
		NEW, 
		UNKNOWN, 
		ERROR, 
		NOT_IN_ENROLL_LIST;
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

	public boolean isEnrollmentInfoMatchingTo(StudentData student) {
		return (this.email.equals(student.email)) &&
				(this.courseId.equals(student.courseId)) &&
				(this.name.equals(student.name)) &&
				(this.comments.equals(student.comments)) &&
				(this.teamName.equals(student.team)) &&
				(this.updateStatus == student.updateStatus);
	}

}
