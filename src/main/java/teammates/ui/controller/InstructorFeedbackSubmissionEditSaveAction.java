package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

public class InstructorFeedbackSubmissionEditSaveAction extends FeedbackSubmissionEditSaveAction {

    @Override
    protected void verifyAccessibleForSpecificUser() {
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        boolean isCreatorOnly = false;
        gateKeeper.verifyAccessible(instructor, session, isCreatorOnly);
        boolean shouldEnableSubmit =
                    instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);

        if (!shouldEnableSubmit && instructor.isAllowedForPrivilegeAnySection(session.getFeedbackSessionName(),
                                             Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)) {
            shouldEnableSubmit = true;
        }

        if (!shouldEnableSubmit) {
            throw new UnauthorizedAccessException("Feedback session [" + session.getFeedbackSessionName()
                                                  + "] is not accessible to instructor ["
                                                  + instructor.email + "] for this purpose");
        }
    }

    @Override
    protected void appendRespondent() {
        taskQueuer.scheduleUpdateRespondentForSession(courseId, feedbackSessionName, getUserEmailForCourse(), true, false);
    }

    @Override
    protected void removeRespondent() {
        taskQueuer.scheduleUpdateRespondentForSession(courseId, feedbackSessionName, getUserEmailForCourse(), true, true);
    }

    @Override
    protected String getUserEmailForCourse() {
        return logic.getInstructorForGoogleId(courseId, account.googleId).email;
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
    protected FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse) throws EntityDoesNotExistException {
        return logic.getFeedbackSessionQuestionsBundleForInstructor(
                             feedbackSessionName, courseId, userEmailForCourse);
    }

    @Override
    protected void setStatusToAdmin() {
        statusToAdmin = "Show instructor feedback submission edit&save page<br>"
                        + "Session Name: " + feedbackSessionName + "<br>"
                        + "Course ID: " + courseId;
    }

    @Override
    protected boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes session) {
        return session.isOpened() || session.isInGracePeriod();
    }

    @Override
    protected RedirectResult createSpecificRedirectResult() {
        return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
    }

    @Override
    protected void setAdditionalParameters() {
        isSendSubmissionEmail = true;
    }

    @Override
    protected void checkAdditionalConstraints() {
        // no additional constraints for the standard instructor submit page
    }
}
