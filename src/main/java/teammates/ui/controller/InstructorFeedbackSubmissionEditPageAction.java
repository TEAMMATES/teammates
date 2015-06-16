package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackSubmissionEditPageAction extends FeedbackSubmissionEditPageAction {

    @Override
    protected boolean isSpecificUserJoinedCourse() {
        // Instructor is always already joined
        return true;
    }
    
    @Override
    protected void verifyAccesibleForSpecificUser(FeedbackSessionAttributes session) {
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        boolean creatorOnly = false;
        new GateKeeper().verifyAccessible(instructor, session, creatorOnly);
        boolean shouldEnableSubmit = 
                    instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
        List<String> sectionsInCourse;

        try {
            sectionsInCourse = logic.getSectionNamesForCourse(instructor.courseId);
        } catch (EntityDoesNotExistException e) {
            // This part is not tested because it is not normally reproducible, it will already be
            // redirected the moment something does not exist but we can still catch it for safety purposes
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
    protected String getUserEmailForCourse() {
        return logic.getInstructorForGoogleId(courseId, account.googleId).email;
    }

    @Override
    protected FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse) throws EntityDoesNotExistException {
        return logic.getFeedbackSessionQuestionsBundleForInstructor(
                             feedbackSessionName, courseId, userEmailForCourse);
    }
    
    @Override
    protected boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes session) {
        return session.isOpened() || session.isPrivateSession();
    }

    @Override
    protected void setStatusToAdmin() {
        statusToAdmin = "Show instructor feedback submission edit page<br>"
                        + "Session Name: " + feedbackSessionName + "<br>"
                        + "Course ID: " + courseId;
    }

    @Override
    protected ShowPageResult createSpecificShowPageResult() {
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT, data);
    }

    @Override
    protected RedirectResult createSpecificRedirectResult() {
        return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
    }
}
