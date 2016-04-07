package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

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
        String moderatedQuestionNumber = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_QUESTION);

        Assumption.assertNotNull(String.format(Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE, 
                                               Const.ParamsNames.COURSE_ID), 
                                 courseId);
        Assumption.assertNotNull(String.format(Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE, 
                                               Const.ParamsNames.FEEDBACK_SESSION_NAME), 
                                 feedbackSessionName);
        Assumption.assertNotNull(String.format(Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE, 
                                               Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON), 
                                 instructorUnderModerationEmail);

        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId), 
                logic.getFeedbackSession(feedbackSessionName, courseId),
                false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        InstructorAttributes instructorUnderModeration = logic.getInstructorForEmail(courseId, instructorUnderModerationEmail);

        // If the instructor doesn't exist
        if (instructorUnderModeration == null) {
            throw new EntityDoesNotExistException("Instructor Email "
                    + instructorUnderModerationEmail + " does not exist in " + courseId
                    + ".");
        }

        FeedbackSubmissionEditPageData data = new FeedbackSubmissionEditPageData(account, student);

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

        if (moderatedQuestionNumber != null) {
            data.setModeratedQuestion(moderatedQuestionNumber);
        }

        statusToAdmin = "Moderating feedback session for instructor (" + instructorUnderModeration.email + ")<br>" +
                        "Session Name: " + feedbackSessionName + "<br>" +
                        "Course ID: " + courseId;
        
        data.bundle.hideQuestionsWithAnonymousResponses();
        data.init(courseId);
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT, data);
    }
}
