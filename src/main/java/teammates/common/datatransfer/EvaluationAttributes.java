package teammates.common.datatransfer;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import teammates.common.Common;
import teammates.common.FieldValidator;
import teammates.common.FieldValidator.FieldType;
import teammates.storage.entity.Evaluation;

public class EvaluationAttributes extends EntityAttributes {
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

	private static Logger log = Common.getLogger();

	public static final int EVALUATION_NAME_MAX_LENGTH = 38;

	public static final String ERROR_FIELD_COURSE = "Evaluation must belong to a valid course";
	public static final String ERROR_FIELD_NAME = "Evaluation name cannot be null or empty\n";
	public static final String ERROR_NAME_TOOLONG = "Evaluation name cannot be more than "
			+ EVALUATION_NAME_MAX_LENGTH + " characters\n";
	public static final String ERROR_FIELD_STARTTIME = "Evaluation start time cannot be null";
	public static final String ERROR_FIELD_ENDTIME = "Evaluation end time cannot be null";
	public static final String ERROR_END_BEFORE_START = "Evaluation end time cannot be earlier than start time";
	public static final String ERROR_PUBLISHED_BEFORE_END = "Evaluation cannot be published before end time";
	public static final String ERROR_ACTIVATED_BEFORE_START = "Evaluation cannot be activated before start time";

	public enum EvalStatus {
		AWAITING, OPEN, CLOSED, PUBLISHED, DOES_NOT_EXIST
	}

	public EvaluationAttributes() {
		// This constructor should take in String params so we can trim them at
		// construction time
		// However, this constructor is already being used in more than 10
		// places
		// Refactoring it will take a very long time. Maybe much later
		// For now, the trimming will be done everytime isValid is called.
	}

	public EvaluationAttributes(Evaluation e) {
		this.course = e.getCourseId();
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

	@Override
	public boolean isValid() {
		course = Common.trimIfNotNull(course);
		name = Common.trimIfNotNull(name);
		return getInvalidStateInfo().isEmpty();
	}

	public String getInvalidStateInfo() {
		FieldValidator validator = new FieldValidator();
		String errorMessage = validator.getInvalidStateInfo(FieldType.EVALUATION_NAME, name);

		if (!Common.isValidCourseId(course)) {
			errorMessage += ERROR_FIELD_COURSE;
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

			if (Common.isCurrentTimeInUsersTimezoneEarlierThan(endTime,
					timeZone) && published) {
				errorMessage += ERROR_PUBLISHED_BEFORE_END;
			}

			if (Common.isCurrentTimeInUsersTimezoneEarlierThan(startTime,
					timeZone) && activated) {
				errorMessage += ERROR_ACTIVATED_BEFORE_START;
			}
		}

		return errorMessage.trim();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("course:" + course + ", name:" + name + Common.EOL);
		return sb.toString();
	}

}
