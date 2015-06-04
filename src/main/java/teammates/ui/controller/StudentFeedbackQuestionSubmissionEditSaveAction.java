package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class StudentFeedbackQuestionSubmissionEditSaveAction extends FeedbackQuestionSubmissionEditSaveAction {
    @Override
    protected void verifyAccesibleForSpecificUser() {
        new GateKeeper().verifyAccessible(getStudent(), logic.getFeedbackSession(feedbackSessionName, courseId));
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
    protected FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse) throws EntityDoesNotExistException {
        return logic.getFeedbackSessionQuestionsBundleForStudent(
                feedbackSessionName, courseId, feedbackQuestionId, userEmailForCourse);
    }

    @Override
    protected void setStatusToAdmin() {
        statusToAdmin = "Save question feedback and show student feedback question submission edit page<br>"
                        + "Question ID: " + feedbackQuestionId + "<br>"
                        + "Session Name: " + feedbackSessionName + "<br>" 
                        + "Course ID: " + courseId;
    }

    @Override
    protected boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes fs) {
        return fs.isOpened() || fs.isInGracePeriod();
    }

    protected StudentAttributes getStudent() {
        if (student != null) {
            // Not covered in tests as it is not easily producible but acts as a safety net
            return student;
        } else {
            return logic.getStudentForGoogleId(courseId, account.googleId);
        }
    }

    @Override
    protected RedirectResult createSpecificRedirectResult() {
        RedirectResult result = createRedirectResult(Const.ActionURIs.STUDENT_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE);
        result.responseParams.put(Const.ParamsNames.COURSE_ID, getStudent().course);
        result.responseParams.put(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        result.responseParams.put(Const.ParamsNames.FEEDBACK_QUESTION_ID, feedbackQuestionId);

        return result;
    }
}
