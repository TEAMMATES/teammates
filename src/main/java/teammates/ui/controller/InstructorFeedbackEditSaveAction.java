package teammates.ui.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.ui.pagedata.InstructorFeedbackEditPageData;

public class InstructorFeedbackEditSaveAction extends InstructorFeedbackAbstractAction {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getFeedbackSession(feedbackSessionName, courseId),
                false,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        InstructorFeedbackEditPageData data = new InstructorFeedbackEditPageData(account, sessionToken);

        // This is only for validation to pass; it will be overridden with its existing value at the logic layer
        String dummyCreatorEmail = "dummy@example.com";

        FeedbackSessionAttributes feedbackSession =
                extractFeedbackSessionData(feedbackSessionName, logic.getCourse(courseId), dummyCreatorEmail);

        try {
            validateTimeData(feedbackSession);
            addResolvedTimeFieldsToDataIfRequired(feedbackSession, data);
            String feedbackEditSaveStatusMessages = addResetTimeFieldsToDataIfRequired(feedbackSession, data,
                    feedbackSessionName, courseId);
            logic.updateFeedbackSession(feedbackSession);
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_EDITED, StatusMessageColor.SUCCESS));
            if (!feedbackEditSaveStatusMessages.isEmpty()) {
                statusToUser.add(new StatusMessage("The following values were found to be invalid and "
                        + "the original values have been retained:<br>"
                        + feedbackEditSaveStatusMessages, StatusMessageColor.WARNING));
            }
            statusToAdmin =
                    "Updated Feedback Session "
                    + "<span class=\"bold\">(" + feedbackSession.getFeedbackSessionName() + ")</span> for Course "
                    + "<span class=\"bold\">[" + feedbackSession.getCourseId() + "]</span> created.<br>"
                    + "<span class=\"bold\">From:</span> " + feedbackSession.getStartTime()
                    + "<span class=\"bold\"> to</span> " + feedbackSession.getEndTime()
                    + "<br><span class=\"bold\">Session visible from:</span> " + feedbackSession.getSessionVisibleFromTime()
                    + "<br><span class=\"bold\">Results visible from:</span> " + feedbackSession.getResultsVisibleFromTime()
                    + "<br><br><span class=\"bold\">Instructions:</span> " + feedbackSession.getInstructions();
            data.setHasError(false);
        } catch (InvalidParametersException e) {
            setStatusForException(e);
            data.setHasError(true);
        }
        data.setStatusMessagesToUser(statusToUser);
        return createAjaxResult(data);
    }

    private void addResolvedTimeFieldsToDataIfRequired(
            FeedbackSessionAttributes session, InstructorFeedbackEditPageData data) {
        addResolvedTimeFieldToDataIfRequired(inputStartTimeLocal, session.getStartTimeLocal(), data,
                Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, Const.ParamsNames.FEEDBACK_SESSION_STARTTIME);

        addResolvedTimeFieldToDataIfRequired(inputEndTimeLocal, session.getEndTimeLocal(), data,
                Const.ParamsNames.FEEDBACK_SESSION_ENDDATE, Const.ParamsNames.FEEDBACK_SESSION_ENDTIME);

        addResolvedTimeFieldToDataIfRequired(inputVisibleTimeLocal, session.getSessionVisibleFromTimeLocal(), data,
                Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE, Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME);

        addResolvedTimeFieldToDataIfRequired(inputPublishTimeLocal, session.getResultsVisibleFromTimeLocal(), data,
                Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME);
    }

    /**
     * This function checks if the input datetime is invalid and updates
     * the corresponding status messages to be shown to the user.
     */
    private String addResetTimeFieldsToDataIfRequired(
            FeedbackSessionAttributes session, InstructorFeedbackEditPageData data,
            String feedbackSessionName, String courseId) {

        FeedbackSessionAttributes oldSession = loadOldFeedbackSession(feedbackSessionName, courseId);
        List<String> statusMessages = new ArrayList<>();

        if (inputStartTimeLocal == null) {
            session.setStartTime(oldSession.getStartTime());
            statusMessages.add("\"Submission opening time\": "
                    + getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_STARTDATE));
            addResetTimeFieldToDataIfRequired(session.getStartTimeLocal(), data,
                    Const.ParamsNames.FEEDBACK_SESSION_STARTDATE, Const.ParamsNames.FEEDBACK_SESSION_STARTTIME,
                    session.getTimeZone());
        }

        if (inputEndTimeLocal == null) {
            session.setEndTime(oldSession.getEndTime());
            statusMessages.add("\"Submission closing time\": "
                    + getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ENDDATE));
            addResetTimeFieldToDataIfRequired(session.getEndTimeLocal(), data,
                    Const.ParamsNames.FEEDBACK_SESSION_ENDDATE, Const.ParamsNames.FEEDBACK_SESSION_ENDTIME,
                    session.getTimeZone());
        }

        if (inputVisibleTimeLocal == null
                && !getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON).equals("atopen")) {
            session.setSessionVisibleFromTime(oldSession.getSessionVisibleFromTime());
            statusMessages.add("\"Session visibility period\": "
                    + getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE));

            addResetTimeFieldToDataIfRequired(session.getSessionVisibleFromTimeLocal(), data,
                    Const.ParamsNames.FEEDBACK_SESSION_VISIBLEDATE, Const.ParamsNames.FEEDBACK_SESSION_VISIBLETIME,
                    session.getTimeZone());
        }

        if (inputPublishTimeLocal == null
                && !(getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON).equals("atvisible")
                || getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON).equals("later"))) {
            session.setResultsVisibleFromTime(oldSession.getResultsVisibleFromTime());
            statusMessages.add("\"Responses visibility period\": "
                    + getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE));
            addResetTimeFieldToDataIfRequired(session.getResultsVisibleFromTimeLocal(), data,
                    Const.ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, Const.ParamsNames.FEEDBACK_SESSION_PUBLISHTIME,
                    session.getTimeZone());
        }

        return statusMessages.isEmpty() ? StringHelper.toString(statusMessages)
                : StringHelper.toString(statusMessages, "<br>");
    }

    private void addResolvedTimeFieldToDataIfRequired(LocalDateTime input, LocalDateTime resolved,
            InstructorFeedbackEditPageData data, String dateInputId, String timeInputId) {
        if (input == null || input.isEqual(resolved)) {
            return;
        }
        data.putResolvedTimeField(dateInputId, TimeHelper.formatDateForSessionsForm(resolved));
        data.putResolvedTimeField(timeInputId, String.valueOf(resolved.getMinute() == 59 ? 23 : resolved.getHour()));
    }

    /**
     * This function adds the expected datetime to a map if the input datetime is invalid.
     */
    private void addResetTimeFieldToDataIfRequired(LocalDateTime reset,
                                                   InstructorFeedbackEditPageData data,
                                                   String dateInputId, String timeInputId,
                                                   ZoneId sessionTimezone) {
        Instant sessionInstant = TimeHelper.convertLocalDateTimeToInstant(reset, sessionTimezone);

        // This covers the case of users transitioning from a non custom option and typed
        // an invalid datetime in the custom datetime option
        if (sessionInstant.equals(Const.TIME_REPRESENTS_FOLLOW_OPENING)
                || sessionInstant.equals(Const.TIME_REPRESENTS_FOLLOW_VISIBLE)
                || sessionInstant.equals(Const.TIME_REPRESENTS_LATER)) {
            data.putResetTimeField(dateInputId, "");
            data.putResetTimeField(timeInputId, String.valueOf(24));
            return;
        }
        data.putResetTimeField(dateInputId, TimeHelper.formatDateForSessionsForm(reset));
        data.putResetTimeField(timeInputId, String.valueOf(reset.getMinute() == 59 ? 23 : reset.getHour()));
    }
}
