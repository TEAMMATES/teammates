package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

/**
 * The {@code InstructorEditInstructorFeedbackSaveAction} class handles incoming requests to
 * save the data after moderating the instructor.
 */
public class InstructorEditInstructorFeedbackSaveAction extends FeedbackSubmissionEditSaveAction {

    private static final Logger log = Logger.getLogger();

    private InstructorAttributes moderatedInstructor;

    /**
     * Verifies if the user is allowed to carry out the action.
     */
    @Override
    protected void verifyAccessibleForSpecificUser() {
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);

        gateKeeper.verifyAccessible(
                instructor, session, false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
    }

    /**
     * Retrieves any additional parameters from request and set them accordingly.
     */
    @Override
    protected void setAdditionalParameters() throws EntityDoesNotExistException {
        String moderatedInstructorEmail = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedInstructorEmail);

        moderatedInstructor = logic.getInstructorForEmail(courseId, moderatedInstructorEmail);
        isSendSubmissionEmail = false;

        // If the instructor doesn't exist
        if (moderatedInstructor == null) {
            throw new EntityDoesNotExistException("Instructor Email "
                    + moderatedInstructorEmail + " does not exist in " + courseId
                    + ".");
        }
    }

    /**
     * Checks if the instructor only submitted responses that he/she should be submitting when moderating.
     */
    @Override
    protected void checkAdditionalConstraints() {
        // check the instructor did not submit responses to questions that he/she should not be able when moderating

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        int numOfQuestionsToGet = data.bundle.questionResponseBundle.size();

        for (int questionIndx = 1; questionIndx <= numOfQuestionsToGet; questionIndx++) {
            String paramMapKey = Const.ParamsNames.FEEDBACK_QUESTION_ID + "-" + questionIndx;
            String questionId = getRequestParamValue(paramMapKey);

            if (questionId == null) {
                // we do not throw an error if the question was not present on the page for instructors to edit
                continue;
            }

            FeedbackQuestionAttributes questionAttributes = data.bundle.getQuestionAttributes(questionId);

            if (questionAttributes == null) {
                statusToUser.add(new StatusMessage("The feedback session or questions may have changed "
                                                       + "while you were submitting. Please check your responses "
                                                       + "to make sure they are saved correctly.",
                                                   StatusMessageColor.WARNING));
                isError = true;
                log.warning("Question not found in Feedback Session [" + feedbackSessionName + "] "
                            + "of Course ID [" + courseId + "]."
                            + "(deleted or invalid id passed?) id: " + questionId + " index: " + questionIndx);
                continue;
            }

            checkSessionQuestionAccessPermission(instructor, questionAttributes);
        }
    }

    /**
     * Checks the instructor's access to a particular question in the feedback session.
     * @param instructor the instructor to be checked
     * @param questionAttributes the question to be checked against
     */
    private void checkSessionQuestionAccessPermission(InstructorAttributes instructor,
                                                      FeedbackQuestionAttributes questionAttributes) {
        boolean isGiverVisibleToInstructors =
                questionAttributes.showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS);
        boolean isRecipientVisibleToInstructors =
                questionAttributes.showRecipientNameTo.contains(FeedbackParticipantType.INSTRUCTORS);
        boolean isResponseVisibleToInstructors =
                questionAttributes.showResponsesTo.contains(FeedbackParticipantType.INSTRUCTORS);

        if (!isResponseVisibleToInstructors || !isGiverVisibleToInstructors || !isRecipientVisibleToInstructors) {
            isError = true;
            throw new UnauthorizedAccessException(
                    "Feedback session [" + feedbackSessionName
                    + "] question [" + questionAttributes.getId() + "] is not accessible "
                    + "to instructor [" + instructor.email + "]");
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

    /**
     * Retrieves the email of the user for the course.
     * @return the email of the user
     */
    @Override
    protected String getUserEmailForCourse() {
        return moderatedInstructor.email;
    }

    /**
     * Retrieves the user's team.
     * @return the name of the user's team
     */
    @Override
    protected String getUserTeamForCourse() {
        return Const.USER_TEAM_FOR_INSTRUCTOR;
    }

    /**
     * Retrieves the user's section for the course.
     * @return the name of the user's section
     */
    @Override
    protected String getUserSectionForCourse() {
        return Const.DEFAULT_SECTION;
    }

    /**
     * Gets data bundle for the course specified.
     * @param userEmailForCourse the email of the user
     * @return FeedbackSessionQuestionsBundle object
     */
    @Override
    protected FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse)
            throws EntityDoesNotExistException {
        return logic.getFeedbackSessionQuestionsBundleForInstructor(
                feedbackSessionName, courseId, userEmailForCourse);
    }

    /**
     * Sets the message to log.
     */
    @Override
    protected void setStatusToAdmin() {
        statusToAdmin = "Instructor moderated instructor session<br>"
                      + "Instructor: " + account.email + "<br>"
                      + "Moderated Instructor: " + moderatedInstructor + "<br>"
                      + "Session Name: " + feedbackSessionName + "<br>"
                      + "Course ID: " + courseId;
    }

    /**
     * Checks if the session is still open. However, since the instructor is moderating the session,
     * they can moderate it anytime. Therefore, it will be true forever.
     * @return true
     */
    @Override
    protected boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes session) {
        // Feedback session closing date does not matter. Instructors can moderate at any time
        return true;
    }

    /**
     * Creates the page to redirect.
     * @return RedirectResult object
     */
    @Override
    protected RedirectResult createSpecificRedirectResult() {
        RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_EDIT_INSTRUCTOR_FEEDBACK_PAGE);

        result.responseParams.put(Const.ParamsNames.COURSE_ID, moderatedInstructor.courseId);
        result.responseParams.put(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        result.responseParams.put(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedInstructor.email);

        return result;
    }
}
