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
        String moderatedEntityIdentifier = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT);
        String moderatedQuestionNumber = getRequestParamValue("moderatedquestion");

        Assumption.assertNotNull(String.format(Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE, 
                                               Const.ParamsNames.COURSE_ID), 
                                 courseId);
        Assumption.assertNotNull(String.format(Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE, 
                                               Const.ParamsNames.FEEDBACK_SESSION_NAME), 
                                 feedbackSessionName);
        Assumption.assertNotNull(String.format(Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE, 
                                               Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT), 
                                 moderatedEntityIdentifier);

        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId), 
                logic.getFeedbackSession(feedbackSessionName, courseId),
                false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        
        InstructorAttributes instructorUnderModeration = logic.getInstructorForEmail(courseId, moderatedEntityIdentifier);

        // If the instructor doesn't exist
        if (instructorUnderModeration == null) {
            throw new EntityDoesNotExistException("Instructor Email "
                    + moderatedEntityIdentifier + " does not exist in " + courseId
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

        hideQuestionsWithAnonymousResponses(data.bundle);

        statusToAdmin = "Moderating feedback session for instructor (" + instructorUnderModeration.email + ")<br>" +
                        "Session Name: " + feedbackSessionName + "<br>" +
                        "Course ID: " + courseId;
        
        data.init("", "", courseId);
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT, data);
    }
    
    /**
     * Removes question from the bundle if the question has givers, recipients or responses that are anonymous to instructors.
     * @param bundle
     */
    private boolean hideQuestionsWithAnonymousResponses(FeedbackSessionQuestionsBundle bundle) {
        List<FeedbackQuestionAttributes> questionsToHide = new ArrayList<FeedbackQuestionAttributes>();
        
        for (FeedbackQuestionAttributes question : bundle.questionResponseBundle.keySet()) {
            boolean isGiverVisibleToInstructor = question.showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS);
            boolean isRecipientVisibleToInstructor = question.showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS);
            boolean isResponseVisibleToInstructor = question.showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS);

            if (!isResponseVisibleToInstructor || !isGiverVisibleToInstructor || !isRecipientVisibleToInstructor) {
                questionsToHide.add(question);
                bundle.questionResponseBundle.put(question, new ArrayList<FeedbackResponseAttributes>());
            }
        }
        
        bundle.questionResponseBundle.keySet().removeAll(questionsToHide);
        
        return !questionsToHide.isEmpty();
    }
}
