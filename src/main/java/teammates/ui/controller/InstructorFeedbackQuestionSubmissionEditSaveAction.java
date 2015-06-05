package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackQuestionSubmissionEditSaveAction extends FeedbackQuestionSubmissionEditSaveAction {
    @Override
    protected void verifyAccesibleForSpecificUser() {
        new GateKeeper().verifyAccessible(logic.getInstructorForGoogleId(courseId, account.googleId),
                                          logic.getFeedbackSession(feedbackSessionName, courseId),
                                          false);
    }

    @Override
    protected void appendRespondant() {
        try {
            logic.addInstructorRespondant(account.googleId, feedbackSessionName, courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            log.severe("Fail to append instructor respondant for session");
        }
    }

    @Override
    protected void removeRespondant() {
        try {
            logic.deleteInstructorRespondant(account.googleId, feedbackSessionName, courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            log.severe("Fail to remove instructor respondant for session");
        }
    }

    @Override
    protected String getUserEmailForCourse() {
        return logic.getInstructorForGoogleId(courseId, account.googleId).email;
    }

    @Override
    protected String getUserSectionForCourse() {
        return Const.DEFAULT_SECTION;
    }

    @Override
    protected FeedbackSessionQuestionsBundle getDataBundle(
            String userEmailForCourse) throws EntityDoesNotExistException {
        return logic.getFeedbackSessionQuestionsBundleForInstructor(
                feedbackSessionName, courseId, userEmailForCourse);
    }

    @Override
    protected void setStatusToAdmin() {
        statusToAdmin = "Save question feedback and show instructor feedback question submission edit page<br>" +
                        "Question ID: " + feedbackQuestionId + "<br>" +
                        "Session Name: " + feedbackSessionName + "<br>" +
                        "Course ID: " + courseId;
    }

    @Override
    protected boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes fs) {
        return fs.isOpened() || fs.isPrivateSession() || fs.isInGracePeriod();
    }

    @Override
    protected RedirectResult createSpecificRedirectResult() {
        RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT_PAGE);
        result.responseParams.put(Const.ParamsNames.COURSE_ID, courseId);
        result.responseParams.put(Const.ParamsNames.FEEDBACK_SESSION_NAME,
                feedbackSessionName);
       
        result.responseParams.put(Const.ParamsNames.FEEDBACK_QUESTION_ID,
                feedbackQuestionId);
        return result;
    }
}
