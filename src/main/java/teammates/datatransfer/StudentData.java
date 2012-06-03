package teammates.datatransfer;

import teammates.exception.InvalidParametersException;
import teammates.jdo.Student;

import com.google.appengine.api.datastore.Text;

public class StudentData {
	public enum UpdateStatus{
		MODIFIED, 
		UNMODIFIED, 
		NEW, 
		UNKNOWN, 
		ERROR, 
		NOT_IN_ENROLL_LIST;
	}
	
	public String id = "";
	public String email;
	public String course;
	public String name;
	public String comments = "";
	public String team = "";
	public Text profile = new Text("");
	
	public UpdateStatus updateStatus = UpdateStatus.UNKNOWN ;
	
	
	public StudentData(String email, String name, String comments,
			String courseId, String team) {
		this.email = email;
		this.course = courseId;
		this.name = name;
		this.comments = comments;
		this.team = team;
	}

	public StudentData() {

	}

	public StudentData(String enrollLine, String courseId)
			throws InvalidParametersException {
		this(new Student(enrollLine, courseId));
	}
	
	public StudentData(Student student){
		this.email = student.getEmail();
			this.course = student.getCourseID();
			this.name = student.getName();
			this.comments = student.getComments();
			this.team = student.getTeamName();
			this.profile = student.getProfileDetail();
			this.id = student.getID();
	}

	public boolean isEnrollInfoSameAs(StudentData otherStudent) {
		return (otherStudent != null) && otherStudent.email.equals(this.email)
				&& otherStudent.course.equals(this.course)
				&& otherStudent.name.equals(this.name)
				&& otherStudent.comments.equals(this.comments)
				&& otherStudent.team.equals(this.team);
	}
	
	public boolean isEnrollmentInfoMatchingTo(StudentData other) {
		return (this.email.equals(other.email)) &&
				(this.course.equals(other.course)) &&
				(this.name.equals(other.name)) &&
				(this.comments.equals(other.comments)) &&
				(this.team.equals(other.team)) &&
				(this.updateStatus == other.updateStatus);
	}
}
