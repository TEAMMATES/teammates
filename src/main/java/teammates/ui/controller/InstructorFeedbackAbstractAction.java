package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.common.util.EmailType;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;

public abstract class InstructorFeedbackAbstractAction extends Action {

    private static final Logger log = Logger.getLogger();

    /**
     * extractFeedbackSessionData sets the attributes for specified session.
     * @param isCreatingSession is a boolean, true for newly creating session and false for already created session.
     * @return session attributes
     */
    protected FeedbackSessionAttributes extractFeedbackSessionData(boolean isCreatingSession) {
        //TODO make this method stateless

        // null checks for parameters not done as null values do not affect data integrity

        FeedbackSessionAttributes newSession = new FeedbackSessionAttributes();
        newSession.setCourseId(getRequestParamValue(Const.ParamsNames.COURSE_ID));

        //Default title.. If the class is InstructorFeedbackAddAction then sanitze the title
        String title = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        if (isCreatingSession) {
            title = SanitizationHelper.sanitizeTitle(title);
        }

        newSession.setFeedbackSessionName(title);
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
        //Only run if its not editing
        if (isCreatingSession) {
            newSession.setCreatedTime(new Date());
            newSession.setSentOpenEmail(false);
            newSession.setSentPublishedEmail(false);
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
            log.severe("Invalid sessionVisibleFrom setting " + newSession.getIdentificationString());
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
            newSession.setFeedbackSessionType(FeedbackSessionType.PRIVATE);
            if (!isCreatingSession) {
                newSession.setEndTime(null);
            }
            break;
        default:
            log.severe("Invalid sessionVisibleFrom setting " + newSession.getIdentificationString());
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
