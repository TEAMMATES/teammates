package teammates.ui.controller;

import java.util.Date;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.TimeHelper;
import teammates.logic.GateKeeper;
import teammates.storage.entity.FeedbackSession.FeedbackSessionType;

public class InstructorFeedbackAddAction extends InstructorFeedbackPageAction {

	@Override
	protected ActionResult execute() 
			throws EntityDoesNotExistException,	InvalidParametersException {
		
		String courseId = getRequestParam(Config.PARAM_COURSE_ID);
		
		Assumption.assertNotNull(courseId);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId), 
				logic.getCourse(courseId));
				
		InstructorFeedbackPageData data = new InstructorFeedbackPageData(account);

		FeedbackSessionAttributes fs = extractFeedbackSessionData();

		// Set creator email as instructors' email
		InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, data.account.googleId);		
		if (instructor == null) {
			Assumption.fail("Could not find instructor after passing through gatekeeper.");
		}
		fs.creatorEmail = instructor.email;
		
		data.newFeedbackSession = fs;
		
		try {
			logic.createFeedbackSession(fs);
			
			statusToUser.add(Config.MESSAGE_FEEDBACK_SESSION_ADDED);
			statusToAdmin = "New Feedback Session <span class=\"bold\">(" + fs.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" + fs.courseId + "]</span> created.<br>" +
					"<span class=\"bold\">From:</span> " + fs.startTime + "<span class=\"bold\"> to</span> " + fs.endTime + "<br>" +
					"<span class=\"bold\">Session visible from:</span> " + fs.sessionVisibleFromTime + "<br>" +
					"<span class=\"bold\">Results visible from:</span> " + fs.resultsVisibleFromTime + "<br><br>" +
					"<span class=\"bold\">Instructions:</span> " + fs.instructions;
			
			return createRedirectResult(new PageData(account).getInstructorFeedbackSessionEditLink(fs.courseId,fs.feedbackSessionName));
			
		} catch (EntityAlreadyExistsException e) {
			statusToUser.add(Config.MESSAGE_FEEDBACK_SESSION_EXISTS);
			statusToAdmin = e.getMessage();
			isError = true;
			
		} catch (InvalidParametersException e) {
			statusToUser.add(e.getMessage());
			statusToAdmin = e.getMessage();
			isError = true;
		} 
		
		// Reload same page if fail.
		data.courses = loadCoursesList(account.googleId);
		data.existingEvals = loadEvaluationsList(account.googleId);
		data.existingSessions = loadFeedbackSessionsList(account.googleId);
		if (data.existingSessions.size() == 0) {
			statusToUser.add(Config.MESSAGE_FEEDBACK_SESSION_EMPTY);
		}
		
		return createShowPageResult(Config.JSP_INSTRUCTOR_FEEDBACK, data);
	}
	
	private FeedbackSessionAttributes extractFeedbackSessionData() {
		FeedbackSessionAttributes newSession = new FeedbackSessionAttributes();
		newSession.courseId = getRequestParam(Config.PARAM_COURSE_ID);
		newSession.feedbackSessionName = getRequestParam(Config.PARAM_FEEDBACK_SESSION_NAME);
		newSession.createdTime = new Date();
		newSession.startTime = TimeHelper.combineDateTime(
				getRequestParam(Config.PARAM_FEEDBACK_SESSION_STARTDATE),
				getRequestParam(Config.PARAM_FEEDBACK_SESSION_STARTTIME));
		newSession.endTime = TimeHelper.combineDateTime(
				getRequestParam(Config.PARAM_FEEDBACK_SESSION_ENDDATE),
				getRequestParam(Config.PARAM_FEEDBACK_SESSION_ENDTIME));		
		String paramTimeZone = getRequestParam(Config.PARAM_FEEDBACK_SESSION_TIMEZONE);
		if (paramTimeZone != null) {
			newSession.timeZone = Integer.parseInt(paramTimeZone);
		}
		String paramGracePeriod = getRequestParam(Config.PARAM_FEEDBACK_SESSION_GRACEPERIOD);
		if (paramGracePeriod != null) {
			newSession.gracePeriod = Integer.parseInt(paramGracePeriod);
		}
		newSession.sentOpenEmail = false; 
		newSession.sentPublishedEmail = false;
		newSession.feedbackSessionType = FeedbackSessionType.STANDARD;
		newSession.instructions = new Text(getRequestParam(Config.PARAM_FEEDBACK_SESSION_INSTRUCTIONS));
		
		String type = getRequestParam(Config.PARAM_FEEDBACK_SESSION_SESSIONVISIBLEBUTTON);
		switch (type) {
		case "custom":
			newSession.sessionVisibleFromTime = TimeHelper.combineDateTime(
					getRequestParam(Config.PARAM_FEEDBACK_SESSION_VISIBLEDATE),
					getRequestParam(Config.PARAM_FEEDBACK_SESSION_VISIBLETIME));
			break;
		case "atopen":
			newSession.sessionVisibleFromTime = Config.TIME_REPRESENTS_FOLLOW_OPENING;
			break;
		case "never":
			newSession.sessionVisibleFromTime = Config.TIME_REPRESENTS_NEVER;
			newSession.feedbackSessionType = FeedbackSessionType.PRIVATE;
			break;
		}
		
		type = getRequestParam(Config.PARAM_FEEDBACK_SESSION_RESULTSVISIBLEBUTTON);
		switch (type) {
		case "custom":
			newSession.resultsVisibleFromTime = TimeHelper.combineDateTime(
					getRequestParam(Config.PARAM_FEEDBACK_SESSION_PUBLISHDATE),
					getRequestParam(Config.PARAM_FEEDBACK_SESSION_PUBLISHTIME));
			break;
		case "atvisible":
			newSession.resultsVisibleFromTime = Config.TIME_REPRESENTS_FOLLOW_VISIBLE;
			break;
		case "later":
			newSession.resultsVisibleFromTime = Config.TIME_REPRESENTS_LATER;
			break;
		case "never":
			newSession.resultsVisibleFromTime = Config.TIME_REPRESENTS_NEVER;
			break;
		}
		
		return newSession;
	}

}
