package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class StudentFeedbackSubmissionEditSaveAction extends FeedbackSubmissionEditSaveAction {
    
    @Override
    protected void verifyAccesibleForSpecificUser() {
        new GateKeeper().verifyAccessible(
                getStudent(),
                logic.getFeedbackSession(feedbackSessionName, courseId));
    }

    @Override
    protected String getUserEmailForCourse() {
        return getStudent().email;
    }
    
    @Override 
    protected String getUserSectionForCourse() {
        return getStudent().section;
    }

    @Override
    protected FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse)
            throws EntityDoesNotExistException {
        return logic.getFeedbackSessionQuestionsBundleForStudent(
                feedbackSessionName, courseId, userEmailForCourse);
    }

    @Override
    protected void setStatusToAdmin() {
        statusToAdmin = "Show student feedback edit result page<br>" +
                "Session Name: " + feedbackSessionName + "<br>" +
                "Course ID: " + courseId;
    }

    @Override
    protected boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes session) {
        return session.isOpened() || session.isInGracePeriod();
    }

    @Override
    protected RedirectResult createSpecificRedirectResult() {
        if (regkey == null) {
            return createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
        } else {
            return createRedirectResult(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE);
        }
    }

    protected StudentAttributes getStudent() {
        if (student == null) {
            student = logic.getStudentForGoogleId(courseId, account.googleId);
        }
        
        return student;
    }
}