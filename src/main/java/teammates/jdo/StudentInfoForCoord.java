package teammates.jdo;

import teammates.datatransfer.StudentData;

import com.google.appengine.api.datastore.Text;

/**
 * 
 * @deprecated Use StudentData instead
 *
 */
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
		this.updateStatus = StudentData.UpdateStatus.UNKNOWN;
	}
	
	public StudentInfoForCoord(StudentData student) {
		this.id = student.id;
		this.email = student.email;
		this.courseId = student.course;
		this.name = student.name;
		this.comments = student.comments;
		this.teamName = student.team;
		this.courseArchived = false;
		this.profileSummary = null;
		this.profileDetail = student.profile;
		this.updateStatus = student.updateStatus;
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
	
	public StudentData.UpdateStatus updateStatus;



}
