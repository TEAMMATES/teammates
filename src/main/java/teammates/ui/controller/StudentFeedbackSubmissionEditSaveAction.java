package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;

public class StudentFeedbackSubmissionEditSaveAction extends FeedbackSubmissionEditSaveAction {

    private static final Logger log = Logger.getLogger();

    @Override
    protected void verifyAccessibleForSpecificUser() {
        gateKeeper.verifyAccessible(getStudent(), logic.getFeedbackSession(feedbackSessionName, courseId));
    }

    @Override
    protected void appendRespondent() {
        try {
            logic.addStudentRespondent(getUserEmailForCourse(), feedbackSessionName, courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            log.severe("Fail to append student respondent");
        }
    }

    @Override
    protected void removeRespondent() {
        try {
            logic.deleteStudentRespondent(getUserEmailForCourse(), feedbackSessionName, courseId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            log.severe("Fail to remove student respondent");
        }
    }

    @Override
    protected String getUserEmailForCourse() {
        return getStudent().email;
    }

    @Override
    protected String getUserTeamForCourse() {
        return SanitizationHelper.desanitizeFromHtml(getStudent().team);
    }

    @Override
    protected String getUserSectionForCourse() {
        return getStudent().section;
    }

    @Override
    protected FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse) throws EntityDoesNotExistException {
        return logic.getFeedbackSessionQuestionsBundleForStudent(feedbackSessionName, courseId,
                                                                 userEmailForCourse);
    }

    @Override
    protected void setStatusToAdmin() {
        statusToAdmin = "Show student feedback edit result page<br>" + "Session Name: "
                        + feedbackSessionName + "<br>" + "Course ID: " + courseId;
    }

    @Override
    protected boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes session) {
        return session.isOpened() || session.isInGracePeriod();
    }

    @Override
    protected RedirectResult createSpecificRedirectResult() {
        if (!isRegisteredStudent()) {
            // Always remains at student feedback submission edit page if user is unregistered
            // Link given to unregistered student already contains course id & session name
            return createRedirectResult(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE);
        }
        if (isError) {
            // Return to student feedback submission edit page if there is an error and user is registered
            RedirectResult result = createRedirectResult(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE);

            // Provide course id and session name for the redirected page
            result.responseParams.put(Const.ParamsNames.COURSE_ID, student.course);
            result.responseParams.put(Const.ParamsNames.FEEDBACK_SESSION_NAME,
                                      getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME));

            return result;
        }

        // Return to student home page if there is no error and user is registered
        return createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
    }

    private StudentAttributes getStudent() {
        if (student == null) {
            student = logic.getStudentForGoogleId(courseId, account.googleId);
        }

        return student;
    }

    private boolean isRegisteredStudent() {
        // a registered student must have an associated google Id, therefore 2 branches are missed here
        // and not covered, if they happen, it signifies a much larger problem.
        // i.e. that student.googleId cannot be empty or null if student != null
        return student != null && student.googleId != null && !student.googleId.isEmpty();
    }

    @Override
    protected void setAdditionalParameters() {
        isSendSubmissionEmail = true;
    }

    @Override
    protected void checkAdditionalConstraints() {
        // no additional constraints to check for the standard student submit page
    }
}
