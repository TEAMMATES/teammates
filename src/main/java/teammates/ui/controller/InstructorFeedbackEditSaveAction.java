package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.Logger;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.TimeHelper;
import teammates.ui.pagedata.InstructorFeedbackEditPageData;

public class InstructorFeedbackEditSaveAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getFeedbackSession(feedbackSessionName, courseId),
                false,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        InstructorFeedbackEditPageData data = new InstructorFeedbackEditPageData(account);
        FeedbackSessionAttributes feedbackSession = extractFeedbackSessionData();

        // A session opening reminder email is always sent as students
        // without accounts need to receive the email to be able to respond
        feedbackSession.setOpeningEmailEnabled(true);

        try {
            logic.updateFeedbackSession(feedbackSession);
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_EDITED, StatusMessageColor.SUCCESS));
            statusToAdmin =
                    "Updated Feedback Session "
                    + "<span class=\"bold\">(" + feedbackSession.getFeedbackSessionName() + ")</span> for Course "
                    + "<span class=\"bold\">[" + feedbackSession.getCourseId() + "]</span> created.<br>"
                    + "<span class=\"bold\">From:</span> " + feedbackSession.getStartTime()
                    + "<span class=\"bold\"> to</span> " + feedbackSession.getEndTime()
                    + "<br><span class=\"bold\">Session visible from:</span> " + feedbackSession.getSessionVisibleFromTime()
                    + "<br><span class=\"bold\">Results visible from:</span> " + feedbackSession.getResultsVisibleFromTime()
                    + "<br><br><span class=\"bold\">Instructions:</span> " + feedbackSession.getInstructions();
            data.setStatusForAjax(Const.StatusMessages.FEEDBACK_SESSION_EDITED);
            data.setHasError(false);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            data.setStatusForAjax(e.getMessage());
            data.setHasError(true);
        }
        return createAjaxResult(data);
    }

    private FeedbackSessionAttributes extractFeedbackSessionData() {
        //TODO make this method stateless

        // null checks for parameters not done as null values do not affect data integrity

        FeedbackSessionAttributes newSession = new FeedbackSessionAttributes();
        newSession.setCourseId(getRequestParamValue(Const.ParamsNames.COURSE_ID));
        newSession.setFeedbackSessionName(getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME));
        newSession.setCreatorEmail(getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_CREATOR));

        newSession.setStartTime(TimeHelper.combineDateTime(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE),
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTTIME)));
        newSession.setEndTime(TimeHelper.combineDateTime(
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE),
                getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDTIME)));
        String paramTimeZone = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_TIMEZONE);
        if (paramTimeZone != null) {
            try {
                newSession.setTimeZone(Double.parseDouble(paramTimeZone));
            } catch (NumberFormatException nfe) {
                log.warning("Failed to parse time zone parameter: " + paramTimeZone);
            }
        }

        String paramGracePeriod = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_GRACEPERIOD);
        try {
            newSession.setGracePeriod(Integer.parseInt(paramGracePeriod));
        } catch (NumberFormatException nfe) {
            log.warning("Failed to parse graced period parameter: " + paramGracePeriod);
        }

        newSession.setFeedbackSessionType(FeedbackSessionType.STANDARD);
        newSession.setInstructions(new Text(getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_INSTRUCTIONS)));

        String type = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON);
        switch (type) {
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_CUSTOM:
            newSession.setResultsVisibleFromTime(TimeHelper.combineDateTime(
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE),
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME)));
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_ATVISIBLE:
            newSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_VISIBLE);
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_LATER:
            newSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_LATER);
            break;
        case Const.INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_NEVER:
            newSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_NEVER);
            break;
        default:
            log.severe("Invalid resultsVisibleFrom setting editing " + newSession.getIdentificationString());
            break;
        }

        // handle session visible after results visible to avoid having a
        // results visible date when session is private (session not visible)
        type = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON);
        switch (type) {
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_CUSTOM:
            newSession.setSessionVisibleFromTime(TimeHelper.combineDateTime(
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE),
                    getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME)));
            break;
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_ATOPEN:
            newSession.setSessionVisibleFromTime(Const.TIME_REPRESENTS_FOLLOW_OPENING);
            break;
        case Const.INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_NEVER:
            newSession.setSessionVisibleFromTime(Const.TIME_REPRESENTS_NEVER);
            // overwrite if private
            newSession.setResultsVisibleFromTime(Const.TIME_REPRESENTS_NEVER);
            newSession.setEndTime(null);
            newSession.setFeedbackSessionType(FeedbackSessionType.PRIVATE);
            break;
        default:
            log.severe("Invalid sessionVisibleFrom setting editing " + newSession.getIdentificationString());
            break;
        }

        String[] sendReminderEmailsArray = getRequestParamValues(Const.ParamsNames.FEEDBACK_SESSION_SENDREMINDEREMAIL);
        List<String> sendReminderEmailsList = sendReminderEmailsArray == null ? new ArrayList<String>()
                                                                              : Arrays.asList(sendReminderEmailsArray);
        newSession.setOpeningEmailEnabled(sendReminderEmailsList.contains(EmailType.FEEDBACK_OPENING.toString()));
        newSession.setClosingEmailEnabled(sendReminderEmailsList.contains(EmailType.FEEDBACK_CLOSING.toString()));
        newSession.setPublishedEmailEnabled(sendReminderEmailsList.contains(EmailType.FEEDBACK_PUBLISHED.toString()));

        return newSession;
    }
}
