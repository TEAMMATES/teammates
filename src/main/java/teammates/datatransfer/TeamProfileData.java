package teammates.datatransfer;

import teammates.storage.entity.TeamProfile;

import com.google.appengine.api.datastore.Text;

public class TeamProfileData {

	/**course id*/
	public String course;
	/**team name*/
	public String team;
	public Text profile;
	
	public TeamProfileData(){
	}

	public TeamProfileData(String course, String team, Text profile) {
		super();
		this.course = course;
		this.team = team;
		this.profile = profile;
	}
	
	public TeamProfileData(TeamProfile tf) {
		this.course = tf.getCourseID();
		this.team = tf.getTeamName();
		this.profile = tf.getTeamProfile();
	}
	
	public TeamProfile toTeamProfile(){
		return new TeamProfile(course, "", team,profile);
	}
	
	

}
