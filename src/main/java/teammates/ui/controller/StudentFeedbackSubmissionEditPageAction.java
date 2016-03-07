package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class StudentFeedbackSubmissionEditPageAction extends FeedbackSubmissionEditPageAction {
    @Override
    protected boolean isSpecificUserJoinedCourse() {
        if (student != null) {
            return student.course.equals(courseId);
        } else {
            return isJoinedCourse(courseId, account.googleId);
        }
    }

    @Override
    protected void verifyAccesibleForSpecificUser(FeedbackSessionAttributes fsa) {
        new GateKeeper().verifyAccessible(getStudent(), fsa);
    }

    @Override
    protected String getUserEmailForCourse() {
        if (student != null) {
            return student.email;
        } else {
            // Not covered as this shouldn't happen since verifyAccesibleForSpecific user is always
            // called before this, calling getStudent() and making student not null in any case
            // This still acts as a safety net, however, and should stay
            return getStudent().email;
        }
    }

    @Override
    protected void setDataBundle(String userEmailForCourse) throws EntityDoesNotExistException {
        data.bundle = logic.getFeedbackSessionQuestionsBundleForStudent(feedbackSessionName, 
                                                                        courseId,
                                                                        userEmailForCourse);
        data.filterSessionQuestionBundle();
    }

    @Override
    protected boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes session) {
        return session.isOpened();
    }

    @Override
    protected void setStatusToAdmin() {
        statusToAdmin = "Show student feedback submission edit page<br>" + "Session Name: "
                        + feedbackSessionName + "<br>" + "Course ID: " + courseId;
    }

    @Override
    protected ShowPageResult createSpecificShowPageResult() {
        return createShowPageResult(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT, data);
    }

    @Override
    protected RedirectResult createSpecificRedirectResult() throws EntityDoesNotExistException {
        if (isRegisteredStudent()) {
            return createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
        } else {
            throw new EntityDoesNotExistException("unregistered student trying to access non-existent session");
        }
    }

    protected StudentAttributes getStudent() {
        if (student == null) {
            // branch of student != null is not covered since student is not set elsewhere, but this
            // helps to speed up the process of 'getting' a student so we should leave it here
            student = logic.getStudentForGoogleId(courseId, account.googleId);
        }

        return student;
    }

    protected boolean isRegisteredStudent(){
        return account.isUserRegistered();
    }
}
