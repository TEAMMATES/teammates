package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackSubmissionEditSaveAction extends FeedbackSubmissionEditSaveAction {

    @Override
    protected void verifyAccesibleForSpecificUser() {
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        boolean creatorOnly = false;
        new GateKeeper().verifyAccessible(instructor, session, creatorOnly);
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
        try {
            logic.addInstructorRespondent(getUserEmailForCourse(), feedbackSessionName, courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            log.severe("Fail to append instructor respondent");
        }
    }

    @Override
    protected void removeRespondent() {
        try {
            logic.deleteInstructorRespondent(getUserEmailForCourse(), feedbackSessionName, courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            log.severe("Fail to remove instructor respondent");
        }
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
        return session.isOpened() || session.isPrivateSession() || session.isInGracePeriod();
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
