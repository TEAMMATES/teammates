package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

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
        List<String> sectionsInCourse;

        try {
            sectionsInCourse = logic.getSectionNamesForCourse(instructor.courseId);
        } catch(EntityDoesNotExistException e) {
            sectionsInCourse = new ArrayList<String>();
        }
        
        for (String section : sectionsInCourse) {
            if (instructor.isAllowedForPrivilege(section, session.feedbackSessionName, 
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)) {
                shouldEnableSubmit = true;
                break;
            }
        }
        
        if (!shouldEnableSubmit) {
            throw new UnauthorizedAccessException("Feedback session [" + session.feedbackSessionName
                                                  + "] is not accessible to instructor ["
                                                  + instructor.email + "] for this purpose");
        }
    }

    @Override
    protected void appendRespondant() {
        try {
            logic.addInstructorRespondant(getUserEmailForCourse(), feedbackSessionName, courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            log.severe("Fail to append instructor respondant");
        }
    }

    @Override
    protected void removeRespondant() {
        try {
            logic.deleteInstructorRespondant(getUserEmailForCourse(), feedbackSessionName, courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            log.severe("Fail to remove instructor respondant");
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
}
