package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.FieldValidator;
import teammates.common.FieldValidator.FieldType;
import teammates.common.Sanitizer;
import teammates.storage.entity.Evaluation;

/**
 * Represents a data transfer object for Evaluation entities.
 */
public class EvaluationAttributes extends EntityAttributes {
	
	public enum EvalStatus {
		AWAITING, OPEN, CLOSED, PUBLISHED, DOES_NOT_EXIST
	}
		
	//Note: be careful when changing these variables as their names are used in *.json files.
	public String courseId;
	public String name;
	public String instructions = ""; // TODO: Change to Text?
	public Date startTime;
	public Date endTime;
	public double timeZone;
	public int gracePeriod;
	public boolean p2pEnabled;
	public boolean published = false;
	public boolean activated = false;

	private static Logger log = Common.getLogger();

	public EvaluationAttributes() {

	}
	
	public EvaluationAttributes(String courseId, String name, String instructions,
			Date startTime, Date endTime, double timeZone, int gracePeriod,
			boolean p2pEnabled, boolean published, boolean activated) {
		this.courseId = Sanitizer.sanitizeTitle(courseId);
		this.name = Sanitizer.sanitizeName(name);
		this.instructions = Sanitizer.sanitizeTextField(instructions);
		this.startTime = startTime;
		this.endTime = endTime;
		this.timeZone = timeZone;
		this.gracePeriod = gracePeriod;
		this.p2pEnabled = p2pEnabled;
		this.published = published;
		this.activated = activated;
	}

	public EvaluationAttributes(Evaluation e) {
		this.courseId = e.getCourseId();
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
		Evaluation evaluation = new Evaluation(courseId, name, instructions,
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
	
	/**
	 * @return True if the current time is between start time and deadline, but
	 *         the evaluation has not been activated yet.
	 */
	public boolean isReadyToActivate() {
		Calendar currentTimeInUserTimeZone = Common.convertToUserTimeZone(
				Calendar.getInstance(), timeZone);

		Calendar evalStartTime = Calendar.getInstance();
		evalStartTime.setTime(startTime);

		log.fine("current:"
				+ Common.calendarToString(currentTimeInUserTimeZone)
				+ "|start:" + Common.calendarToString(evalStartTime));

		if (currentTimeInUserTimeZone.before(evalStartTime)) {
			return false;
		} else {
			return (!activated);
		}
	}
	
	//TODO: unit test this
	public boolean isClosingWithinTimeLimit(int hours) {

		Calendar now = Calendar.getInstance();
		// Fix the time zone accordingly
		now.add(Calendar.MILLISECOND,
				(int) (60 * 60 * 1000 * timeZone));
		
		Calendar start = Calendar.getInstance();
		start.setTime(startTime);
		
		Calendar deadline = Calendar.getInstance();
		deadline.setTime(endTime);

		long nowMillis = now.getTimeInMillis();
		long deadlineMillis = deadline.getTimeInMillis();
		long differenceBetweenDeadlineAndNow = (deadlineMillis - nowMillis)
				/ (60 * 60 * 1000);

		// If now and start are almost similar, it means the evaluation 
		// is open for only 24 hours.
		// Hence we do not send a reminder e-mail for the evaluation.
		return now.after(start)
				&& (differenceBetweenDeadlineAndNow >= hours - 1 
				&& differenceBetweenDeadlineAndNow < hours);
	}

	@Override
	public boolean isValid() {
		return getInvalidityInfo().isEmpty();
	}

	public List<String> getInvalidityInfo() {
		
		Assumption.assertTrue(startTime!=null);
		Assumption.assertTrue(endTime!=null);
		
		FieldValidator validator = new FieldValidator();
		List<String> errors = new ArrayList<String>();
		String error;
		
		error= validator.getInvalidityInfo(FieldType.COURSE_ID, courseId);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getInvalidityInfo(FieldType.EVALUATION_NAME, name);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getInvalidityInfo(FieldType.EVALUATION_INSTRUCTIONS, instructions);
		if(!error.isEmpty()) { errors.add(error); }		
		
		error= validator.getValidityInfoForTimeFrame(FieldType.EVALUATION_TIME_FRAME,
				FieldType.START_TIME, FieldType.END_TIME, startTime, endTime);
		if(!error.isEmpty()) { errors.add(error); }	
		
		error= validator.getValidityInfoForEvalStartTime(startTime, timeZone, activated);
		if(!error.isEmpty()) { errors.add(error); }	
		
		error= validator.getValidityInfoForEvalEndTime(endTime, timeZone, published);
		if(!error.isEmpty()) { errors.add(error); }	

		return errors;
	}

	@Override
	public String toString() {
		return Common.getTeammatesGson()
				.toJson(this, EvaluationAttributes.class);
	}

	public static List<EvaluationAttributes> toAttributes(
			List<Evaluation> evaluationEntities) {
		
		List<EvaluationAttributes> attributesList = new ArrayList<EvaluationAttributes>();
		for(Evaluation e: evaluationEntities){
			attributesList.add(new EvaluationAttributes(e));
		}
		return attributesList;
	}

	@Override
	public String getIdentificationString() {
		return this.courseId + " | " + this.name;
	}

	@Override
	public String getEntityTypeAsString() {
		return "Evaluation";
	}

}
