package teammates.ui.controller;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.TimeHelper;
import teammates.logic.GateKeeper;
import teammates.storage.entity.FeedbackSession.FeedbackSessionType;

public class InstructorFeedbackEditSaveAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		//TODO: do we need this?
		String courseId = getRequestParam(Config.PARAM_COURSE_ID);
		String feedbackSessionName = getRequestParam(Config.PARAM_FEEDBACK_SESSION_NAME);
		
		Assumption.assertNotNull(courseId);
		Assumption.assertNotNull(feedbackSessionName);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId));
		
		InstructorFeedbackEditPageData data = new InstructorFeedbackEditPageData(account);
		data.session = extractFeedbackSessionData();
		
		try {
			logic.updateFeedbackSession(data.session);			
			statusToUser.add(Config.MESSAGE_FEEDBACK_SESSION_EDITED);
			statusToAdmin = "Updated Feedback Session <span class=\"bold\">(" + data.session.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" + data.session.courseId + "]</span> created.<br>" +
					"<span class=\"bold\">From:</span> " + data.session.startTime + "<span class=\"bold\"> to</span> " + data.session.endTime + "<br>" +
					"<span class=\"bold\">Session visible from:</span> " + data.session.sessionVisibleFromTime + "<br>" +
					"<span class=\"bold\">Results visible from:</span> " + data.session.resultsVisibleFromTime + "<br><br>" +
					"<span class=\"bold\">Instructions:</span> " + data.session.instructions;
		} catch (InvalidParametersException e) {
			statusToUser.add(e.getMessage());
			statusToAdmin = e.getMessage();
			isError = true;
		}
		
		// Get updated results and show same page
		data.session = logic.getFeedbackSession(feedbackSessionName, courseId);
		data.questions = logic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
		return createShowPageResult(Config.JSP_INSTRUCTOR_FEEDBACK_EDIT, data);
	}

	private FeedbackSessionAttributes extractFeedbackSessionData() {
		FeedbackSessionAttributes newSession = new FeedbackSessionAttributes();
		newSession.courseId = getRequestParam(Config.PARAM_COURSE_ID);
		newSession.feedbackSessionName = getRequestParam(Config.PARAM_FEEDBACK_SESSION_NAME);
		newSession.creatorEmail = getRequestParam(Config.PARAM_FEEDBACK_SESSION_CREATOR);
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
