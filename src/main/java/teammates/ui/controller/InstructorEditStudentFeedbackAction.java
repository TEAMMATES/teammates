package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorEditStudentFeedbackAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String previewStudentEmail = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        Assumption.assertNotNull(String.format(Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE, 
                                               Const.ParamsNames.COURSE_ID), 
                                 courseId);
        Assumption.assertNotNull(String.format(Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE, 
                                               Const.ParamsNames.FEEDBACK_SESSION_NAME),
                                 feedbackSessionName);
        Assumption.assertNotNull(String.format(Const.StatusMessages.NULL_POST_PARAMETER_MESSAGE, 
                                               Const.ParamsNames.PREVIEWAS),
                                 previewStudentEmail);

        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId), 
                logic.getFeedbackSession(feedbackSessionName, courseId),
                false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS); // double check later
        
        StudentAttributes previewStudent = logic.getStudentForEmail(courseId, previewStudentEmail); //rename preview students later
        
        if (previewStudent == null) {
            throw new EntityDoesNotExistException("Student Email "
                    + previewStudentEmail + " does not exist in " + courseId
                    + ".");
        }
        
        FeedbackSubmissionEditPageData data = new FeedbackSubmissionEditPageData(account, student);
        
        data.bundle = logic.getFeedbackSessionQuestionsBundleForStudent(
                feedbackSessionName, courseId, previewStudent.email);
        
        Assumption.assertNotNull(data.bundle);
        
        data.isSessionOpenForSubmission = true;
        data.isModeration = true;
        data.previewStudent = previewStudent;
        List<String> questionsToHide = hideAnonymousResponses(data.bundle);

        statusToAdmin = "Moderating feedback session for student (" + previewStudent.email + ")<br>" +
                "Session Name: " + feedbackSessionName + "<br>" +
                "Course ID: " + courseId;
        
        return createShowPageResult(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT, data);
    }
    
    private List<String> hideAnonymousResponses(FeedbackSessionQuestionsBundle bundle) {
        List<String> questionsToHide = new ArrayList<String>();
        
        for(FeedbackQuestionAttributes question : bundle.questionResponseBundle.keySet()) {
            boolean instructorsCanSeeGiver = question.showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS);
            if (!instructorsCanSeeGiver) {
                questionsToHide.add(question.getId());
                bundle.questionResponseBundle.put(question, new ArrayList<FeedbackResponseAttributes>());
            }
        }
        return questionsToHide;
    }
    
}
