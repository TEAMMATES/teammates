package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackQuestionSubmissionEditPageAction extends FeedbackQuestionSubmissionEditPageAction {

    @Override
    protected boolean isSpecificUserJoinedCourse() {
        // Instructor is always already joined
        return true;
    }

    @Override
    protected void verifyAccesibleForSpecificUser() {
        new GateKeeper().verifyAccessible(logic.getInstructorForGoogleId(courseId, account.googleId),
                                          logic.getFeedbackSession(feedbackSessionName, courseId),
                                          false);
    }

    @Override
    protected String getUserEmailForCourse() {
        return logic.getInstructorForGoogleId(courseId, account.googleId).email;
    }

    @Override
    protected FeedbackSessionQuestionsBundle getDataBundle(
            String userEmailForCourse) throws EntityDoesNotExistException {
        return logic.getFeedbackSessionQuestionsBundleForInstructor(
                feedbackSessionName, courseId, feedbackQuestionId, userEmailForCourse);
    }

    @Override
    protected boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes fs) {
        return fs.isOpened() || fs.isPrivateSession();
    }

    @Override
    protected void setStatusToAdmin() {
        statusToAdmin = "Show instructor feedback question submission edit page<br>" +
                        "Question ID: " + feedbackQuestionId + "<br>" +
                        "Session Name: " + feedbackSessionName + "<br>" +
                        "Course ID: " + courseId;
    }

    @Override
    protected ShowPageResult createSpecificShowPageResult() {
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT, data);
    }
}
