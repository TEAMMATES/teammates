package teammates.datatransfer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import teammates.api.Common;
import teammates.api.InvalidParametersException;
import teammates.persistent.Evaluation;

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

	public enum EvalStatus {
		AWAITING, OPEN, CLOSED, PUBLISHED
	}

	public EvaluationData() {

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

	public Evaluation toEvaluation() throws InvalidParametersException {
		Evaluation evaluation = new Evaluation(course, name, instructions,
				p2pEnabled, startTime, endTime, timeZone, gracePeriod);
		evaluation.setActivated(activated);
		evaluation.setPublished(published);
		return evaluation;
	}

	public EvalStatus getStatus() {
		Calendar now = Calendar.getInstance();
		Evaluation.convertToUserTimeZone(now, timeZone);
		long nowInMilliSec = now.getTimeInMillis();

		Calendar start = Calendar.getInstance();
		start.setTime(startTime);
		long startInMilliSec = start.getTimeInMillis();

		Calendar end = Calendar.getInstance();
		end.setTime(endTime);
		end.add(Calendar.MILLISECOND, gracePeriod * 60 * 1000);
		long endInMilliSec = end.getTimeInMillis();

		log.finer(Common.EOL + "Now  : " + Common.calendarToString(now)
				+ Common.EOL + "Start: " + Common.calendarToString(start)
				+ Common.EOL + "End  : " + Common.calendarToString(end));

		if (published) {
			return EvalStatus.PUBLISHED;
		} else if ((startInMilliSec <= nowInMilliSec)
				&& (nowInMilliSec <= endInMilliSec)) {
			return EvalStatus.OPEN;
		} else if (nowInMilliSec > endInMilliSec) {
			return EvalStatus.CLOSED;
		}
		return EvalStatus.AWAITING;
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

	public void validate() throws InvalidParametersException {
		Common.verifyNotNull(this.course, "course ID");
		Common.verifyNotNull(this.name, "evaluation name");
		Common.verifyNotNull(this.startTime, "start time");
		Common.verifyNotNull(this.endTime, "end time");
		if (endTime.before(startTime)) {
			throw new InvalidParametersException(
					Common.ERRORCODE_END_BEFORE_START,
					"End time cannot be before start time");
		}
		
		if ((!beforeTime(endTime)) && published) {
			throw new InvalidParametersException(
					Common.ERRORCODE_PUBLISHED_BEFORE_CLOSING,
					"Cannot be published before the evaluation is CLOSED");
		}
		
		if ((!beforeTime(startTime)) && activated) {
			throw new InvalidParametersException(
					Common.ERRORCODE_ACTIVATED_BEFORE_START,
					"Cannot be activated before the evaluation is OPEN");
		}
	}

	private boolean beforeTime(Date time) {
		Date nowInUserTimeZone = Evaluation.convertToUserTimeZone(Calendar.getInstance(),timeZone).getTime();
		return time.before(nowInUserTimeZone);
	}

	
}
