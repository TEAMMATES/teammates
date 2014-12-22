package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

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
        
        boolean shouldEnableSubmit = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        
        if (!shouldEnableSubmit) {
            throw new UnauthorizedAccessException(
                    "Feedback session [" + session.feedbackSessionName + 
                    "] is not accessible to instructor ["+ instructor.email + "] for this purpose");
        }
    }
    
    @Override
    protected void setOptionalParameters() {
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
                        "Instructor: " + 
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