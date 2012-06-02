package teammates.datatransfer;

import java.util.Date;


import teammates.jdo.TeamFormingSession;


public class TfsData {
	public String course;
	public Date startTime;
	public Date endTime;
	public double timeZone;
	public int gracePeriod;
	public String instructions;
	public String profileTemplate;
	public boolean activated;

	public TfsData(){
	}
	
	public TfsData(TeamFormingSession tfs) {
		this.course = tfs.getCourseID();
		this.startTime = tfs.getStart();
		this.endTime = tfs.getDeadline();
		this.timeZone = tfs.getTimeZone();
		this.gracePeriod = tfs.getGracePeriod();
		this.instructions = tfs.getInstructions();
		this.profileTemplate = tfs.getProfileTemplate();
		this.activated = tfs.isActivated();
	}

	public TeamFormingSession toTfs() {
		TeamFormingSession tfs = new TeamFormingSession(course, startTime, endTime, timeZone, gracePeriod, instructions, profileTemplate);
		tfs.setActivated(activated);
		return tfs;
	}
	
}
