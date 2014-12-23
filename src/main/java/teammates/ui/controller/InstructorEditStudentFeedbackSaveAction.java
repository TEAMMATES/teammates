package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;


public class InstructorEditStudentFeedbackSaveAction extends FeedbackSubmissionEditSaveAction {
    
    StudentAttributes moderatedStudent;
    
    @Override
    protected void verifyAccesibleForSpecificUser() {
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
                
        new GateKeeper().verifyAccessible(instructor,
                session,
                false, moderatedStudent.section, 
                session.feedbackSessionName, 
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        
    }
    
    @Override
    protected void setAdditionalParameters() {
        String moderatedStudentEmail = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT);
        moderatedStudent = logic.getStudentForEmail(courseId, moderatedStudentEmail);
    }

    @Override
    protected void appendRespondant() {
        try {
            logic.addStudentRespondant(getUserEmailForCourse(), feedbackSessionName, courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            log.severe("Fail to append instructor respondant");
        }
    }

    @Override
    protected void removeRespondant() {
        try {
            logic.deleteStudentRespondant(getUserEmailForCourse(), feedbackSessionName, courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            log.severe("Fail to remove instructor respondant");
        }
    }

    @Override
    protected String getUserEmailForCourse() {
        return moderatedStudent.email;
    }
    
    @Override
    protected String getUserSectionForCourse() {
        return moderatedStudent.section;
    }

    @Override
    protected FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse)
            throws EntityDoesNotExistException {
        return logic.getFeedbackSessionQuestionsBundleForStudent(
                feedbackSessionName, courseId, userEmailForCourse);
    }

    @Override
    protected void setStatusToAdmin() {
        statusToAdmin = "Instructor moderated student session<br>" +
                        "Instructor: " + account.email + "<br>" + 
                        "Moderated Student: " + moderatedStudent + "<br>" +
                        "Session Name: " + feedbackSessionName + "<br>" +
                        "Course ID: " + courseId;
    }

    @Override
    protected boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes session) {
        // Feedback session closing date does not matter; instructor can moderate at any time
        return true; 
    }

    @Override
    protected RedirectResult createSpecificRedirectResult() {
        return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
    }
}