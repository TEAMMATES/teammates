package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import teammates.common.Common;
import teammates.storage.entity.Evaluation;

public class EvaluationData {
	public String course;
	public String name;
	public String instructions = "";
	public Date startTime;
	public Date endTime;
	public double timeZone;
	public int gracePeriod;
	public boolean p2pEnabled;
	public boolean published = false;
	public boolean activated = false;

	// marked transient to avoid converting to json
	public transient int submittedTotal = Common.UNINITIALIZED_INT;
	public transient int expectedTotal = Common.UNINITIALIZED_INT;

	public transient ArrayList<TeamData> teams = new ArrayList<TeamData>();

	private static Logger log = Common.getLogger();
	
	public static final int EVALUATION_NAME_MAX_LENGTH = 38;
	
	public static final String ERROR_FIELD_COURSE = "Evaluation must belong to a valid course\n";
	public static final String ERROR_FIELD_NAME = "Evaluation name cannot be null or empty\n";
	public static final String ERROR_NAME_TOOLONG = "Evaluation name cannot be more than " + EVALUATION_NAME_MAX_LENGTH + " characters\n";
	public static final String ERROR_FIELD_STARTTIME = "Evaluation start time cannot be null\n";
	public static final String ERROR_FIELD_ENDTIME = "Evaluation end time cannot be null\n";
	public static final String ERROR_END_BEFORE_START = "Evaluation end time cannot be earlier than start time\n";
	public static final String ERROR_PUBLISHED_BEFORE_END = "Evaluation cannot be published before end time\n";
	public static final String ERROR_ACTIVATED_BEFORE_START = "Evaluation cannot be activated before start time\n";

	public enum EvalStatus {
		AWAITING, OPEN, CLOSED, PUBLISHED, DOES_NOT_EXIST
	}

	public EvaluationData() {
		// This constructor should take in String params so we can trim them at construction time
		// However, this constructor is already being used in more than 10 places
		// Refactoring it will take a very long time. Maybe much later
		// For now, the trimming will be done everytime isValid is called.
	}

	public EvaluationData(Evaluation e) {
		this.course = e.getCourseID();
		this.name = e.getName();
		this.instructions = e.getInstructions();
		this.startTime = e.getStart();
		this.endTime = e.getDeadline();
		this.timeZone = e.getTimeZone();
		this.gracePeriod = e.getGracePeriod();
		this.p2pEnabled = e.isCommentsEnabled();
		this.published = e.isPublished();
		this.activated = e.isActivated();
	}

	public Evaluation toEntity() {
		Evaluation evaluation = new Evaluation(course, name, instructions,
				p2pEnabled, startTime, endTime, timeZone, gracePeriod);
		evaluation.setActivated(activated);
		evaluation.setPublished(published);
		return evaluation;
	}

	public EvalStatus getStatus() {
		Calendar now = Calendar.getInstance();
		Common.convertToUserTimeZone(now, timeZone);

		Calendar start = Calendar.getInstance();
		start.setTime(startTime);

		Calendar end = Calendar.getInstance();
		end.setTime(endTime);
		end.add(Calendar.MINUTE, gracePeriod);

		log.fine(Common.EOL + "Now  : " + Common.calendarToString(now)
				+ Common.EOL + "Start: " + Common.calendarToString(start)
				+ Common.EOL + "End  : " + Common.calendarToString(end));

		if (published) {
			return EvalStatus.PUBLISHED;
		} else if (now.after(end)) {
			return EvalStatus.CLOSED;
		} else if (now.after(start)) {
			return EvalStatus.OPEN;
		} else {
			return EvalStatus.AWAITING;
		}
	}

	public TeamData getTeamData(String teamName) {
		for (TeamData team : teams) {
			if (team.name.equals(teamName)) {
				return team;
			}
		}
		return null;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("course:" + course + ", name:" + name + Common.EOL);
		for (TeamData team : teams) {
			sb.append(team.toString(1));
		}
		return sb.toString();
	}

	public boolean isValid() {
		course = course == null ? null : course.trim();
		name = name == null ? null : name.trim();
		
		return getInvalidStateInfo() == "";
	}
	
	public String getInvalidStateInfo() {
		String errorMessage = "";
		
		if (!Common.isValidCourseId(course)){
			errorMessage += ERROR_FIELD_COURSE;
		}
		
		if (!Common.isValidName(name)) {
			errorMessage += ERROR_FIELD_NAME;
		} else if (name.length() > EVALUATION_NAME_MAX_LENGTH) {
			errorMessage += ERROR_NAME_TOOLONG;
		}
		
		if (this.startTime == null) {
			errorMessage += ERROR_FIELD_STARTTIME;
		}
		
		if (this.endTime == null) {
			errorMessage += ERROR_FIELD_ENDTIME;
		}
		
		// Check time values are valid
		if (this.startTime != null && this.endTime != null) {
			if (endTime.before(startTime)) {
				errorMessage += ERROR_END_BEFORE_START;
			}
			
			if (isCurrentTimeZoneEarlierThan(endTime) && published) {
				errorMessage += ERROR_PUBLISHED_BEFORE_END;
			}
			
			if (isCurrentTimeZoneEarlierThan(startTime) && activated) {
				errorMessage += ERROR_ACTIVATED_BEFORE_START;
			}
		}
		
		return errorMessage;
	}

	private boolean isCurrentTimeZoneEarlierThan(Date time) {
		Date nowInUserTimeZone = Common.convertToUserTimeZone(
				Calendar.getInstance(), timeZone).getTime();
		return nowInUserTimeZone.before(time);
	}
}
