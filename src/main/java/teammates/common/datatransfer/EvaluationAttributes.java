package teammates.common.datatransfer;

import static teammates.common.Common.EOL;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.FieldValidator;
import teammates.common.FieldValidator.FieldType;
import teammates.storage.entity.Evaluation;

/**
 * Represents a data transfer object for Evaluation entities.
 */
public class EvaluationAttributes extends EntityAttributes {
	
	public enum EvalStatus {
		AWAITING, OPEN, CLOSED, PUBLISHED, DOES_NOT_EXIST
	}
		
	//Note: be careful when changing these variables as their names are used in *.json files.
	public String course; //TODO: rename to courseId
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

	//TODO: move these to FieldValidator. All filed validation error messages should be in that class.
	public static final String ERROR_END_BEFORE_START = "Evaluation end time cannot be earlier than start time";
	public static final String ERROR_PUBLISHED_BEFORE_END = "Evaluation cannot be published before end time";
	public static final String ERROR_ACTIVATED_BEFORE_START = "Evaluation cannot be activated before start time";


	//TODO: add a constructor that takes all parameters. Provide sanitization.
	
	public EvaluationAttributes() {

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
		//TODO: remove these two lines. 
		course = Common.trimIfNotNull(course);
		name = Common.trimIfNotNull(name);
		
		return getInvalidStateInfo().isEmpty();
	}

	public String getInvalidStateInfo() {
		
		Assumption.assertTrue(course!=null);
		Assumption.assertTrue(name!=null);
		Assumption.assertTrue(instructions!=null);
		Assumption.assertTrue(startTime!=null);
		Assumption.assertTrue(endTime!=null);
		
		FieldValidator validator = new FieldValidator();
		String errorMessage = 
				validator.getValidityInfo(FieldType.COURSE_ID, course) + EOL+
				validator.getValidityInfo(FieldType.EVALUATION_NAME, name) + EOL+
				validator.getValidityInfo(FieldType.EVALUATION_INSTRUCTIONS, instructions) + EOL;

		// Verify that time values are valid
		if (this.startTime != null && this.endTime != null) {
			
			if (endTime.before(startTime)) {
				errorMessage += ERROR_END_BEFORE_START;
			}

			if (Common.isCurrentTimeInUsersTimezoneEarlierThan(endTime,	timeZone) && published) {
				errorMessage += ERROR_PUBLISHED_BEFORE_END;
			}

			if (Common.isCurrentTimeInUsersTimezoneEarlierThan(startTime, timeZone) && activated) {
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
