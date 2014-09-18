package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackQuestionBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class StudentFeedbackQuestionSubmissionEditSaveAction extends
        FeedbackQuestionSubmissionEditSaveAction {

    @Override
    protected void verifyAccesibleForSpecificUser() {
        new GateKeeper().verifyAccessible(
                getStudent(),
                logic.getFeedbackSession(feedbackSessionName, courseId));
    }

    @Override
    protected void appendRespondant() {
        try {
            logic.addStudentRespondant(account.googleId, feedbackSessionName, courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            log.severe("Fail to append student respondant");
        }
    }

    @Override
    protected void removeRespondant() {
        try {
            logic.deleteStudentRespondant(account.googleId, feedbackSessionName, courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            log.severe("Fail to remove student respondant");
        }
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
    protected FeedbackQuestionBundle getDataBundle(String userEmailForCourse)
            throws EntityDoesNotExistException {
        return logic.getFeedbackQuestionBundleForStudent(
                feedbackSessionName, courseId, feedbackQuestionId, userEmailForCourse);
    }

    @Override
    protected void setStatusToAdmin() {
        statusToAdmin = "Save question feedback and show student feedback question submission edit page<br>" +
                "Question ID: " + feedbackQuestionId + "<br>" +
                "Session Name: " + feedbackSessionName + "<br>" + 
                "Course ID: " + courseId;
    }

    @Override
    protected boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes fs) {
        return fs.isOpened() || fs.isInGracePeriod();
    }

    @Override
    protected ShowPageResult createSpecificShowPageResult() {
        return createShowPageResult(Const.ViewURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT, data);
    }
    
    protected StudentAttributes getStudent() {
        if (student != null) {
            return student;
        } else {
            return logic.getStudentForGoogleId(courseId, account.googleId);
        }
    }

}
