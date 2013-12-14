package teammates.ui.controller;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackEditSaveAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId), 
				logic.getFeedbackSession(feedbackSessionName, courseId),
				true);
		
		InstructorFeedbackEditPageData data = new InstructorFeedbackEditPageData(account);
		data.session = extractFeedbackSessionData();
		
		try {
			logic.updateFeedbackSession(data.session);			
			statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_EDITED);
			statusToAdmin = "Updated Feedback Session <span class=\"bold\">(" + data.session.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" + data.session.courseId + "]</span> created.<br>" +
					"<span class=\"bold\">From:</span> " + data.session.startTime + "<span class=\"bold\"> to</span> " + data.session.endTime + "<br>" +
					"<span class=\"bold\">Session visible from:</span> " + data.session.sessionVisibleFromTime + "<br>" +
					"<span class=\"bold\">Results visible from:</span> " + data.session.resultsVisibleFromTime + "<br><br>" +
					"<span class=\"bold\">Instructions:</span> " + data.session.instructions;
		} catch (InvalidParametersException e) {
			setStatusForException(e);
		}
		
		return createRedirectResult(data.getInstructorFeedbackSessionEditLink(courseId, feedbackSessionName));
	}

	private FeedbackSessionAttributes extractFeedbackSessionData() {
		FeedbackSessionAttributes newSession = new FeedbackSessionAttributes();
		newSession.courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		newSession.feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		newSession.creatorEmail = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_CREATOR);
		newSession.startTime = TimeHelper.combineDateTime(
				getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE),
				getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTTIME));
		newSession.endTime = TimeHelper.combineDateTime(
				getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE),
				getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDTIME));		
		String paramTimeZone = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE);
		if (paramTimeZone != null) {
			newSession.timeZone = Integer.parseInt(paramTimeZone);
		}
		String paramGracePeriod = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD);
		if (paramGracePeriod != null) {
			newSession.gracePeriod = Integer.parseInt(paramGracePeriod);
		}
		newSession.feedbackSessionType = FeedbackSessionType.STANDARD;
		newSession.instructions = new Text(getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS));

		String type = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON);
		switch (type) {
		case "custom":
			newSession.resultsVisibleFromTime = TimeHelper.combineDateTime(
					getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE),
					getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME));
			break;
		case "atvisible":
			newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_VISIBLE;
			break;
		case "later":
			newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
			break;
		case "never":
			newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
			break;
		}
		
		type = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON);
		switch (type) {
		case "custom": //Magic strings. Use enums to prevent potentila bugs caused by typos.
			newSession.sessionVisibleFromTime = TimeHelper.combineDateTime(
					getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE),
					getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME));
			break;
		case "atopen":
			newSession.sessionVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_OPENING;
			break;
		case "never":
			newSession.sessionVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
			// overwrite if private
			newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
			newSession.feedbackSessionType = FeedbackSessionType.PRIVATE;
			break;
		}
		
		return newSession;
	}
}
