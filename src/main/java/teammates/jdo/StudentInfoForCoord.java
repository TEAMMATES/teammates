package teammates.jdo;

import com.google.appengine.api.datastore.Text;

public class StudentInfoForCoord {
	
	public enum UpdateStatus{
		MODIFIED, UNMODIFIED, NEW;
	}
	
	public String ID;

	public String email;

	public String courseID;

	public String name;

	public String comments;

	public transient Long registrationKey;

	public String teamName;

	public transient boolean courseArchived;
	
	public String profileSummary;
	
	public Text profileDetail;
	
	public UpdateStatus updateStatus;

}
