package teammates.datatransfer;

import teammates.exception.InvalidParametersException;
import teammates.jdo.Student;
import teammates.jdo.StudentInfoForCoord;
import teammates.jdo.StudentInfoForCoord.UpdateStatus;

import com.google.appengine.api.datastore.Text;

public class StudentData {
	public String id = "";
	public String email;
	public String courseId;
	public String name;
	public String comments = "";
	public String team = "";
	public Text profile = new Text("");
	
	public StudentInfoForCoord.UpdateStatus updateStatus = UpdateStatus.UNKNOWN ;
	
	
	public StudentData(String email, String name, String comments,
			String courseId, String team) {
		this.email = email;
		this.courseId = courseId;
		this.name = name;
		this.comments = comments;
		this.team = team;
	}

	public StudentData() {

	}

	public StudentData(String enrollLine, String courseId)
			throws InvalidParametersException {
		StudentData s = new Student(enrollLine, courseId).toStudentData();
		this.email = s.email;
		this.courseId = s.courseId;
		this.name = s.name;
		this.comments = s.comments;
		this.team = s.team;
	}

	public boolean isEnrollInfoSameAs(StudentData otherStudent) {
		return (otherStudent != null) && otherStudent.email.equals(this.email)
				&& otherStudent.courseId.equals(this.courseId)
				&& otherStudent.name.equals(this.name)
				&& otherStudent.comments.equals(this.comments)
				&& otherStudent.team.equals(this.team);
	}
}
