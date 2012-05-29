package teammates.jdo;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.google.appengine.api.datastore.Text;

/**
 * TeamFormingLog is a persistent data class that holds information pertaining to student
 * actions during team forming session on Teammates.
 * 
 * @author Kalpit Jain
 * 
 */
@PersistenceCapable
public class TeamFormingLog {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	public Long id;

	@Persistent
	@SerializedName("courseid")
	private String courseID;
	
	@Persistent
	@SerializedName("time")
	private Date time;
	
	@Persistent
	@SerializedName("studentname")
	private String studentName;
	
	@Persistent
	@SerializedName("studentemail")
	private String studentEmail;
	
	@Persistent
	@SerializedName("message")
	private Text message;
	
	/**
	 * Constructs an TeamFormingLog object.
	 * 
	 * @param courseID	
	 * @param time
	 * @param studentName
	 * @param studentEmail
	 * @param message
	 */
	public TeamFormingLog(String courseID, Date time, String studentName, 
			String studentEmail, Text message) {
		this.setCourseID(courseID);
		this.setTime(time);
		this.setStudentName(studentName);
		this.setStudentEmail(studentEmail);
		this.setMessage(message);		
	}
	
	public void setCourseID(String courseID) {
		this.courseID = courseID;
	}
	
	public String getCourseID() {
		return courseID;
	}
	
	public void setTime(Date time) {
		this.time = time;
	}

	public Date getTime() {
		return time;
	}
	
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	
	public String getStudentName() {
		return studentName;
	}
	
	public void setStudentEmail(String studentEmail) {
		this.studentEmail = studentEmail;
	}
	
	public String getStudentEmail() {
		return studentEmail;
	}
	
	public void setMessage(Text message) {
		this.message = message;
	}
	
	public Text getMessage() {
		return message;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("courseID: " + courseID);
		sb.append("\ntime: " + time);
		sb.append("\nstudentName: " + studentName);
		sb.append("\nstudentEmail: " + studentEmail);
		sb.append("\nmessage: " + message);
		return sb.toString();
	}
}