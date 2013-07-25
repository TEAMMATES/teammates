package teammates.ui.controller;

import java.util.Date;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackAddAction extends InstructorFeedbacksPageAction {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		
		String courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		
		Assumption.assertNotNull(courseId);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId), 
				logic.getCourse(courseId));
				
		InstructorFeedbacksPageData data = new InstructorFeedbacksPageData(account);

		FeedbackSessionAttributes fs = extractFeedbackSessionData();

		// Set creator email as instructors' email
		InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, data.account.googleId);		
		if (instructor == null) {
			//TODO: can reuse the instructor retrieved previously
			Assumption.fail("Could not find instructor after passing through gatekeeper.");
		}
		fs.creatorEmail = instructor.email;
		
		data.newFeedbackSession = fs;
		
		try {
			logic.createFeedbackSession(fs);
			
			statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_ADDED);
			statusToAdmin = "New Feedback Session <span class=\"bold\">(" + fs.feedbackSessionName + ")</span> for Course <span class=\"bold\">[" + fs.courseId + "]</span> created.<br>" +
					"<span class=\"bold\">From:</span> " + fs.startTime + "<span class=\"bold\"> to</span> " + fs.endTime + "<br>" +
					"<span class=\"bold\">Session visible from:</span> " + fs.sessionVisibleFromTime + "<br>" +
					"<span class=\"bold\">Results visible from:</span> " + fs.resultsVisibleFromTime + "<br><br>" +
					"<span class=\"bold\">Instructions:</span> " + fs.instructions;
			
			return createRedirectResult(new PageData(account).getInstructorFeedbackSessionEditLink(fs.courseId,fs.feedbackSessionName));
			
		} catch (EntityAlreadyExistsException e) {
			statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_EXISTS);
			statusToAdmin = e.getMessage();
			isError = true;
			
		} catch (InvalidParametersException e) {
			setStatusForException(e);
		} 
		
		// Reload same page if fail. 
		//TODO: is the above comment correct?
		data.courses = loadCoursesList(account.googleId);
		data.existingEvalSessions = loadEvaluationsList(account.googleId);
		data.existingFeedbackSessions = loadFeedbackSessionsList(account.googleId);
		if (data.existingFeedbackSessions.size() == 0) {
			statusToUser.add(Const.StatusMessages.FEEDBACK_SESSION_EMPTY);
		}
		
		return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACKS, data);
	}
	
	private FeedbackSessionAttributes extractFeedbackSessionData() {
		FeedbackSessionAttributes newSession = new FeedbackSessionAttributes();
		newSession.courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		newSession.feedbackSessionName = getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_NAME);
		newSession.createdTime = new Date();
		newSession.startTime = TimeHelper.combineDateTime(
				getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE),
				getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_STARTTIME));
		newSession.endTime = TimeHelper.combineDateTime(
				getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE),
				getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_ENDTIME));		
		String paramTimeZone = getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE);
		if (paramTimeZone != null) {
			newSession.timeZone = Integer.parseInt(paramTimeZone);
		}
		String paramGracePeriod = getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD);
		if (paramGracePeriod != null) {
			newSession.gracePeriod = Integer.parseInt(paramGracePeriod);
		}
		newSession.sentOpenEmail = false; 
		newSession.sentPublishedEmail = false;
		newSession.feedbackSessionType = FeedbackSessionType.STANDARD;
		newSession.instructions = new Text(getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS));
		
		String type = getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON);
		switch (type) {
		case "custom":
			newSession.resultsVisibleFromTime = TimeHelper.combineDateTime(
					getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE),
					getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME));
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
		
		type = getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON);
		switch (type) {
		case "custom": //Magic strings. Use enums to prevent potentila bugs caused by typos.
			newSession.sessionVisibleFromTime = TimeHelper.combineDateTime(
					getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE),
					getRequestParam(Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME));
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
