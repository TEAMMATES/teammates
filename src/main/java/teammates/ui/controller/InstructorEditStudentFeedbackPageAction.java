package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorEditStudentFeedbackPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String moderatedStudentEmail = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT);

        Assumption.assertNotNull(String.format(Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE, 
                                               Const.ParamsNames.COURSE_ID), 
                                 courseId);
        Assumption.assertNotNull(String.format(Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE, 
                                               Const.ParamsNames.FEEDBACK_SESSION_NAME),
                                 feedbackSessionName);
        Assumption.assertNotNull(String.format(Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE, 
                                               Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT),
                                 moderatedStudentEmail);

        StudentAttributes studentUnderModeration = logic.getStudentForEmail(courseId, moderatedStudentEmail); 
        
        if (studentUnderModeration == null) {
            throw new EntityDoesNotExistException("Student Email "
                    + moderatedStudentEmail + " does not exist in " + courseId
                    + ".");
        }
        
        new GateKeeper().verifyAccessible(logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getFeedbackSession(feedbackSessionName, courseId),
                false, studentUnderModeration.section, 
                feedbackSessionName, 
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        
        
        FeedbackSubmissionEditPageData data = new FeedbackSubmissionEditPageData(account, student);
        
        data.bundle = logic.getFeedbackSessionQuestionsBundleForStudent(
                feedbackSessionName, courseId, studentUnderModeration.email);
        
        Assumption.assertNotNull(data.bundle);
        
        data.isSessionOpenForSubmission = true;
        data.isModeration = true;
        data.studentToViewPageAs = studentUnderModeration;
        hideQuestionsWithAnonymousResponses(data.bundle);

        
        statusToAdmin = "Moderating feedback session for student (" + studentUnderModeration.email + ")<br>" +
                "Session Name: " + feedbackSessionName + "<br>" +
                "Course ID: " + courseId;
        
        return createShowPageResult(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT, data);
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

            if (!isGiverVisibleToInstructor || !isRecipientVisibleToInstructor || !isResponseVisibleToInstructor) {
                questionsToHide.add(question);
                bundle.questionResponseBundle.put(question, new ArrayList<FeedbackResponseAttributes>());
            }
        }
        
        bundle.questionResponseBundle.keySet().removeAll(questionsToHide);
        return !questionsToHide.isEmpty();
    }
    
}
