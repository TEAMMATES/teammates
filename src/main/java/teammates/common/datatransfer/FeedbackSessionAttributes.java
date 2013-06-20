package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.Common;
import teammates.common.FieldValidator;
import teammates.common.Sanitizer;
import teammates.common.FieldValidator.FieldType;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.FeedbackSession.FeedbackSessionType;

public class FeedbackSessionAttributes extends EntityAttributes {
	
	public String feedbackSessionName;
	public String courseId;
	public String creatorEmail;
	public Text instructions;
	public Date createdTime;
	public Date startTime;
	public Date endTime;
	public Date sessionVisibleFromTime;
	public Date resultsVisibleFromTime;
	public int timeZone;
	public int gracePeriod;
	public FeedbackSessionType feedbackSessionType;
	public boolean sentOpenEmail;
	public boolean sentPublishedEmail;
	
	public FeedbackSessionAttributes() {
		
	}
	
	public FeedbackSessionAttributes(FeedbackSession fs) {
		this.feedbackSessionName = fs.getFeedbackSessionName();
		this.courseId = fs.getCourseId();
		this.creatorEmail = fs.getCreatorEmail();
		this.instructions = fs.getInstructions();
		this.createdTime = fs.getCreatedTime();
		this.startTime = fs.getStartTime();
		this.endTime = fs.getEndTime();
		this.sessionVisibleFromTime = fs.getSessionVisibleFromTime();
		this.resultsVisibleFromTime = fs.getResultsVisibleFromTime();
		this.timeZone = fs.getTimeZone();
		this.gracePeriod = fs.getGracePeriod();
		this.feedbackSessionType = fs.getFeedbackSessionType();
		this.sentOpenEmail = fs.isSentOpenEmail();
		this.sentPublishedEmail = fs.isSentPublishedEmail();
	}
		
	public FeedbackSessionAttributes(String feedbackSessionName,
			String courseId, String creatorId, Text instructions,
			Date createdTime, Date startTime, Date endTime,
			Date sessionVisibleFromTime, Date resultsVisibleFromTime,
			int timeZone, int gracePeriod, FeedbackSessionType feedbackSessionType,
			boolean sentOpenEmail, boolean sentPublishedEmail) {
		this.feedbackSessionName = Sanitizer.sanitizeTitle(feedbackSessionName);
		this.courseId = Sanitizer.sanitizeTitle(courseId);
		this.creatorEmail = Sanitizer.sanitizeGoogleId(creatorId);
		this.instructions = Sanitizer.sanitizeTextField(instructions);
		this.createdTime = createdTime;
		this.startTime = startTime;
		this.endTime = endTime;
		this.sessionVisibleFromTime = sessionVisibleFromTime;
		this.resultsVisibleFromTime = resultsVisibleFromTime;
		this.timeZone = timeZone;
		this.gracePeriod = gracePeriod;
		this.feedbackSessionType = feedbackSessionType;
		this.sentOpenEmail = sentOpenEmail;
		this.sentPublishedEmail = sentPublishedEmail;
	}
	
	public List<String> getInvalidityInfo() {
		
		FieldValidator validator = new FieldValidator();
		List<String> errors = new ArrayList<String>();
		String error;
		
		error= validator.getInvalidityInfo(FieldType.FEEDBACK_SESSION_NAME, feedbackSessionName);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getInvalidityInfo(FieldType.COURSE_ID, courseId);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getInvalidityInfo(FieldType.EMAIL, "creator's email", creatorEmail);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getValidityInfoForTimeFrame(FieldType.FEEDBACK_SESSION_TIME_FRAME,
				FieldType.START_TIME, FieldType.END_TIME, startTime, endTime);
		if(!error.isEmpty()) { errors.add(error); }	
		
		error= validator.getValidityInfoForTimeFrame(FieldType.FEEDBACK_SESSION_TIME_FRAME,
				FieldType.START_TIME, FieldType.RESULTS_VISIBLE_TIME, startTime, resultsVisibleFromTime);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getValidityInfoForTimeFrame(FieldType.FEEDBACK_SESSION_TIME_FRAME,
				FieldType.SESSION_VISIBLE_TIME, FieldType.START_TIME, sessionVisibleFromTime, startTime);
		if(!error.isEmpty()) { errors.add(error); }	
		
		return errors;
	}

	public FeedbackSession toEntity() {
		return new FeedbackSession(feedbackSessionName,
				courseId, creatorEmail, instructions,
				createdTime, startTime, endTime,
				sessionVisibleFromTime, resultsVisibleFromTime,
				timeZone, gracePeriod,
				feedbackSessionType, sentOpenEmail, sentPublishedEmail);
	}
	
	@Override
	public boolean isValid() {
		return getInvalidityInfo().isEmpty();
	}

	@Override
	public String getIdentificationString() {
		return this.feedbackSessionName + "/" + this.courseId;
	}

	@Override
	public String getEntityTypeAsString() {
		return "Feedback Session";
	}

	/**
	 * @return {@code true} if it is after the closing time of this feedback session; {@code false} if not.
	 */
	public boolean isClosed() {
		Calendar now = Calendar.getInstance();
		Common.convertToUserTimeZone(now, timeZone);

		Calendar end = Common.dateToCalendar(endTime);
		end.add(Calendar.MINUTE, gracePeriod);

		return (now.after(end));
	}
	
	/**
	 * @return {@code true} is currently open and accepting responses.
	 */
	public boolean isOpened() {
		Calendar now = Calendar.getInstance();
		Common.convertToUserTimeZone(now, timeZone);

		Calendar start = Common.dateToCalendar(startTime);

		return (now.after(start) && !isClosed());
	}
	
	/**
	 * @return {@code true} has not opened before and is waiting to open.<br> 
	 * {@code false} if session has opened before.
	 */
	public boolean isWaitingToOpen() {
		return (!isOpened() && !isClosed());
	}
	
	/**
	 * @return {@code true} if the session is visible; {@code false} if not.
	 * Does not care if the session has started or not.
	 */
	public boolean isVisible() {
		Date now = Common.convertToUserTimeZone(Calendar.getInstance(), timeZone).getTime();
		Date visibleTime = this.sessionVisibleFromTime;
		
		if (visibleTime.equals(Common.TIME_REPRESENTS_FOLLOW_OPENING)) {
			visibleTime = this.startTime;
		} else if (visibleTime.equals(Common.TIME_REPRESENTS_NEVER)) {
			return false;
		} 
		return (visibleTime.before(now));
	}
	
	/**
	 * @return {@code true} if the results of the feedback session is visible; {@code false} if not.
	 * Does not care if the session has ended or not.
	 */
	public boolean isPublished() {
		Date now = Common.convertToUserTimeZone(Calendar.getInstance(), timeZone).getTime();
		Date publishTime = this.resultsVisibleFromTime;
		
		if (publishTime.equals(Common.TIME_REPRESENTS_FOLLOW_VISIBLE)) {
			return isVisible();
		} else if (publishTime.equals(Common.TIME_REPRESENTS_LATER)) {
			return false;
		} else if (publishTime.equals(Common.TIME_REPRESENTS_NEVER)) {
			return false;
		}  else {
			return (publishTime.before(now));
		}
	}
	
	@Override
	public String toString() {
		return "FeedbackSessionAttributes [feedbackSessionName="
				+ feedbackSessionName + ", courseId=" + courseId
				+ ", creatorEmail=" + creatorEmail + ", instructions=" + instructions
				+ ", createdTime=" + createdTime + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", sessionVisibleFromTime="
				+ sessionVisibleFromTime + ", resultsVisibleFromTime="
				+ resultsVisibleFromTime + ", timeZone=" + timeZone
				+ ", gracePeriod=" + gracePeriod + ", feedbackSessionType="
				+ feedbackSessionType + ", sentOpenEmail=" + sentOpenEmail
				+ ", sentPublishedEmail=" + sentPublishedEmail + "]";
	}
}
