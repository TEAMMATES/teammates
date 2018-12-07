package teammates.ui.controller;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.FeedbackSubmissionEditPageData;

/**
 * The {@code InstructorEditInstructorFeedbackPageAction} class handles incoming requests to the page.
 * {@code FeedbackSubmissionEditPageData} will be generated and the page requested will be loaded.
 */
public class InstructorEditInstructorFeedbackPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String instructorUnderModerationEmail = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);

        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(courseId, account.googleId),
                                    logic.getFeedbackSession(feedbackSessionName, courseId),
                                    false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        InstructorAttributes instructorUnderModeration =
                logic.getInstructorForEmail(courseId, instructorUnderModerationEmail);

        // If the instructor doesn't exist
        if (instructorUnderModeration == null) {
            throw new EntityDoesNotExistException("Instructor Email "
                    + instructorUnderModerationEmail + " does not exist in " + courseId
                    + ".");
        }

        String moderatedQuestionId = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_QUESTION_ID);

        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON,
                instructorUnderModerationEmail);

        FeedbackSubmissionEditPageData data = new FeedbackSubmissionEditPageData(account, student, sessionToken);

        data.bundle = logic.getFeedbackSessionQuestionsBundleForInstructor(
                feedbackSessionName,
                courseId,
                instructorUnderModeration.email);

        Assumption.assertNotNull(data.bundle);

        data.setSessionOpenForSubmission(true);
        data.setModeration(true);
        data.setHeaderHidden(true);
        data.setPreviewInstructor(instructorUnderModeration);
        data.setSubmitAction(Const.ActionURIs.INSTRUCTOR_EDIT_INSTRUCTOR_FEEDBACK_SAVE);

        if (moderatedQuestionId != null) {
            data.setModeratedQuestionId(moderatedQuestionId);
        }

        statusToAdmin = "Moderating feedback session for instructor (" + instructorUnderModeration.email + ")<br>"
                      + "Session Name: " + feedbackSessionName + "<br>"
                      + "Course ID: " + courseId;

        data.bundle.hideUnmoderatableQuestions();
        data.init(courseId);

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT, data);
    }
}
