package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.logic.api.GateKeeper;
import teammates.logic.core.Emails.EmailType;

import com.google.appengine.api.datastore.Text;

public class InstructorFeedbackEditSaveAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);
        
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId), 
                logic.getFeedbackSession(feedbackSessionName, courseId),
                false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        InstructorFeedbackEditPageData data = new InstructorFeedbackEditPageData(account);
        data.session = extractFeedbackSessionData();
        
        // A session opening reminder email is always sent
        // as students without accounts need to receive the email to be able to respond
        data.session.isOpeningEmailEnabled = true;
        
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
        //TODO make this method stateless
        
        // null checks for parameters not done as null values do not affect data integrity
        
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
        try {
            newSession.timeZone = Double.parseDouble(paramTimeZone);
        } catch (NumberFormatException nfe) {
            // do nothing
        } catch (NullPointerException npe) {
            //do nothing
        }
        String paramGracePeriod = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD);
        try {
            newSession.gracePeriod = Integer.parseInt(paramGracePeriod);
        } catch (NumberFormatException nfe) {
            //do nothing
        }
        newSession.feedbackSessionType = FeedbackSessionType.STANDARD;
        newSession.instructions = new Text(getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS));

        String type = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON);
        switch (type) {
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_CUSTOM:
            newSession.resultsVisibleFromTime = TimeHelper.combineDateTime(
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE),
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME));
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_ATVISIBLE:
            newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_VISIBLE;
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_LATER:
            newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_NEVER:
            newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
            break;
        }
        
        // handle session visible after results visible to avoid having a 
        // results visible date when session is private (session not visible)
        type = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON);
        switch (type) {
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_CUSTOM:
            newSession.sessionVisibleFromTime = TimeHelper.combineDateTime(
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE),
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME));
            break;
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_ATOPEN:
            newSession.sessionVisibleFromTime = Const.TIME_REPRESENTS_FOLLOW_OPENING;
            break;
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_NEVER:
            newSession.sessionVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
            // overwrite if private
            newSession.resultsVisibleFromTime = Const.TIME_REPRESENTS_NEVER;
            newSession.endTime = null;
            newSession.feedbackSessionType = FeedbackSessionType.PRIVATE;
            break;
        }
        
        String[] sendReminderEmailsArray = getRequestParamValues(Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL);
        List<String> sendReminderEmailsList = sendReminderEmailsArray == null ? new ArrayList<String>() : Arrays.asList(sendReminderEmailsArray);
        newSession.isOpeningEmailEnabled = sendReminderEmailsList.contains(EmailType.FEEDBACK_OPENING.toString());
        newSession.isClosingEmailEnabled = sendReminderEmailsList.contains(EmailType.FEEDBACK_CLOSING.toString());
        newSession.isPublishedEmailEnabled = sendReminderEmailsList.contains(EmailType.FEEDBACK_PUBLISHED.toString());
        
        return newSession;
    }
}
