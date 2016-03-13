package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

public class InstructorEditInstructorFeedbackSaveAction extends FeedbackSubmissionEditSaveAction {
    InstructorAttributes moderatedInstructor;
    
    @Override
    protected void verifyAccesibleForSpecificUser() {
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);

        new GateKeeper().verifyAccessible(instructor,
                session,
                false, 
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
    }
    
    @Override
    protected void setAdditionalParameters() {
        String moderatedInstructorEmail = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_INSTRUCTOR);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_INSTRUCTOR, moderatedInstructorEmail);

        moderatedInstructor = logic.getInstructorForEmail(courseId, moderatedInstructorEmail);
    }
    
    @Override
    protected void checkAdditionalConstraints() {
        // check the instructor did not submit responses to questions that he/she should not be able when moderating
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        int numOfQuestionsToGet = data.bundle.questionResponseBundle.size();

        for (int questionIndx = 1; questionIndx <= numOfQuestionsToGet; questionIndx++) {
            String questionId = HttpRequestHelper.getValueFromParamMap(
                    requestParameters, 
                    Const.ParamsNames.FEEDBACK_QUESTION_ID + "-" + questionIndx);
            
            if (questionId == null) {
                // we do not throw an error if the question was not present on the page for instructors to edit
                continue;
            }
            
            FeedbackQuestionAttributes questionAttributes = data.bundle.getQuestionAttributes(questionId);
            
            if (questionAttributes == null) {
                statusToUser.add(new StatusMessage("The feedback session or questions may have changed while you were submitting. "
                                                + "Please check your responses to make sure they are saved correctly.", StatusMessageColor.WARNING));
                isError = true;
                log.warning("Question not found. (deleted or invalid id passed?) id: "+ questionId + " index: " + questionIndx);
                continue;
            }
            
            boolean isGiverVisibleToInstructors = questionAttributes.showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS);
            boolean isRecipientVisibleToInstructors = questionAttributes.showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS);
            boolean isResponseVisibleToInstructors = questionAttributes.showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS);
            
            if (!isResponseVisibleToInstructors || !isGiverVisibleToInstructors || !isRecipientVisibleToInstructors) {
                isError = true;
                throw new UnauthorizedAccessException(
                        "Feedback session [" + feedbackSessionName + 
                        "] question [" + questionAttributes.getId() + "] is not accessible to instructor ["+ instructor.email + "]");
            }
        }
    }
    
    @Override
    protected void appendRespondant() {
        try {
            logic.addInstructorRespondant(getUserEmailForCourse(), feedbackSessionName, courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            log.severe("Fail to append instructor respondant");
        }
    }
    
    @Override
    protected void removeRespondant() {
        try {
            logic.deleteInstructorRespondant(getUserEmailForCourse(), feedbackSessionName, courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            log.severe("Fail to remove instructor respondant");
        }
    }

    @Override
    protected String getUserEmailForCourse() {
        return moderatedInstructor.email;
    }
    
    @Override
    protected String getUserTeamForCourse() {
        return Const.USER_TEAM_FOR_INSTRUCTOR;
    }
    
    @Override
    protected String getUserSectionForCourse() {
        return Const.DEFAULT_SECTION;
    }

    @Override
    protected FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse)
            throws EntityDoesNotExistException {
        return logic.getFeedbackSessionQuestionsBundleForInstructor(
                feedbackSessionName, courseId, userEmailForCourse);
    }

    @Override
    protected void setStatusToAdmin() {
        statusToAdmin = "Instructor moderated instructor session<br>" +
                        "Instructor: " + account.email + "<br>" + 
                        "Moderated Instructor: " + moderatedInstructor + "<br>" +
                        "Session Name: " + feedbackSessionName + "<br>" +
                        "Course ID: " + courseId;
    }

    @Override
    protected boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes session) {
        // Feedback session closing date does not matter. Instructors can moderate at any time
        return true; 
    }

    @Override
    protected RedirectResult createSpecificRedirectResult() {
        RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_EDIT_INSTRUCTOR_FEEDBACK_PAGE);
        
        result.responseParams.put(Const.ParamsNames.COURSE_ID, moderatedInstructor.courseId);
        result.responseParams.put(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        result.responseParams.put(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedInstructor.email);
        
        return result;
    }
}
