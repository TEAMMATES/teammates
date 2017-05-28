package teammates.ui.controller;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.FeedbackSubmissionEditPageData;

public class InstructorFeedbackPreviewAsInstructorAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String previewInstructorEmail = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        Assumption.assertPostParamNotNull(Const.ParamsNames.PREVIEWAS, previewInstructorEmail);

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getFeedbackSession(feedbackSessionName, courseId),
                false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        InstructorAttributes previewInstructor = logic.getInstructorForEmail(courseId, previewInstructorEmail);

        if (previewInstructor == null) {
            throw new EntityDoesNotExistException(
                    "Instructor Email " + previewInstructorEmail + " does not exist in " + courseId + ".");
        }

        FeedbackSubmissionEditPageData data = new FeedbackSubmissionEditPageData(account, student, sessionToken);

        data.bundle = logic.getFeedbackSessionQuestionsBundleForInstructor(
                feedbackSessionName,
                courseId,
                previewInstructor.email);

        data.setSessionOpenForSubmission(true);
        data.setPreview(true);
        data.setHeaderHidden(true);
        data.setPreviewInstructor(previewInstructor);
        data.setSubmitAction(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_SAVE);
        data.bundle.resetAllResponses();

        statusToAdmin = "Preview feedback session as instructor (" + previewInstructor.email + ")<br>"
                      + "Session Name: " + feedbackSessionName + "<br>"
                      + "Course ID: " + courseId;

        data.init("", "", courseId);

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT, data);
    }
}
