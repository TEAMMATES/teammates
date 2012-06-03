package teammates.datatransfer;

import java.util.Date;

import teammates.persistent.TeamFormingLog;

import com.google.appengine.api.datastore.Text;

public class StudentActionData {
	/** course id */
	public String course;
	/** student name*/
	public String name;
	/** student email*/
	public String email;
	public Date time;
	/**description of the student action*/
	public Text action;
	
	public StudentActionData(){
		
	}

	public StudentActionData(TeamFormingLog tfl) {
		super();
		this.course = tfl.getCourseID();
		this.name = tfl.getStudentName();
		this.email = tfl.getStudentEmail();
		this.time = tfl.getTime();
		this.action = tfl.getMessage();
	}
	
	public TeamFormingLog toTeamFormingLog(){
		return new TeamFormingLog(course, time, name, email, action);
	}
	
	
}
