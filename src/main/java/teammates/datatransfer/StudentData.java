package teammates.datatransfer;

import teammates.api.InvalidParametersException;
import teammates.persistent.Student;

import com.google.appengine.api.datastore.Text;

public class StudentData extends UserData{
	public enum UpdateStatus{
		MODIFIED, 
		UNMODIFIED, 
		NEW, 
		UNKNOWN, 
		ERROR, 
		NOT_IN_ENROLL_LIST;
	}
	
	public String name;
	public String email;
	public String course = null;
	public String comments = null;
	public String team = null;
	public Text profile = null;
	
	
	public UpdateStatus updateStatus = UpdateStatus.UNKNOWN ;
	
	public transient EvalResultData result;
	
	
	public StudentData(String email, String name, String comments,
			String courseId, String team) {
		this();
		this.email = email;
		this.course = courseId;
		this.name = name;
		this.comments = comments;
		this.team = team;
	}

	public StudentData() {
		isStudent=true;
	}

	public StudentData(String enrollLine, String courseId)
			throws InvalidParametersException {
		this(new Student(enrollLine, courseId));
	}
	
	public StudentData(Student student){
		this();
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
