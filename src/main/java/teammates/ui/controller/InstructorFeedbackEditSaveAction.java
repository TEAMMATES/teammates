package teammates.ui.controller;

import java.time.LocalDateTime;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
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

    private void addResolvedTimeFieldToDataIfRequired(LocalDateTime input, LocalDateTime resolved,
            InstructorFeedbackEditPageData data, String dateInputId, String timeInputId) {
        if (input == null || input.isEqual(resolved)) {
            return;
        }
        data.putResolvedTimeField(dateInputId, TimeHelper.formatDateForSessionsForm(resolved));
        data.putResolvedTimeField(timeInputId, String.valueOf(resolved.getMinute() == 59 ? 23 : resolved.getHour()));
    }
}
