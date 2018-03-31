package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

public class InstructorEditStudentFeedbackSaveAction extends FeedbackSubmissionEditSaveAction {

    private static final Logger log = Logger.getLogger();

    private StudentAttributes moderatedStudent;

    @Override
    protected void verifyAccessibleForSpecificUser() {
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);

        gateKeeper.verifyAccessible(instructor, session, false, moderatedStudent.section,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
    }

    @Override
    protected void setAdditionalParameters() {
        String moderatedStudentEmail = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudentEmail);

        moderatedStudent = logic.getStudentForEmail(courseId, moderatedStudentEmail);
        isSendSubmissionEmail = false;
    }

    @Override
    protected void checkAdditionalConstraints() {
        // check the instructor did not submit responses to questions that he/she should not be able
        // to view during moderation

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);

        int numOfQuestionsToGet = data.bundle.questionResponseBundle.size();

        for (int questionIndx = 1; questionIndx <= numOfQuestionsToGet; questionIndx++) {
            String questionId = getRequestParamValue(
                    Const.ParamsNames.FEEDBACK_QUESTION_ID + "-" + questionIndx);

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
                log.warning("Question not found. (deleted or invalid id passed?) id: "
                            + questionId + " index: " + questionIndx);
                continue;
            }

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
    }

    @Override
    protected void appendRespondent() {
        taskQueuer.scheduleUpdateRespondentForSession(courseId, feedbackSessionName, getUserEmailForCourse(), false, false);
    }

    @Override
    protected void removeRespondent() {
        taskQueuer.scheduleUpdateRespondentForSession(courseId, feedbackSessionName, getUserEmailForCourse(), false, true);
    }

    @Override
    protected String getUserEmailForCourse() {
        return moderatedStudent.email;
    }

    @Override
    protected String getUserTeamForCourse() {
        return SanitizationHelper.desanitizeFromHtml(moderatedStudent.team);
    }

    @Override
    protected String getUserSectionForCourse() {
        return moderatedStudent.section;
    }

    @Override
    protected FeedbackSessionQuestionsBundle getDataBundle(String userEmailForCourse)
            throws EntityDoesNotExistException {
        return logic.getFeedbackSessionQuestionsBundleForStudent(
                feedbackSessionName, courseId, userEmailForCourse);
    }

    @Override
    protected void setStatusToAdmin() {
        statusToAdmin = "Instructor moderated student session<br>"
                      + "Instructor: " + account.email + "<br>"
                      + "Moderated Student: " + moderatedStudent + "<br>"
                      + "Session Name: " + feedbackSessionName + "<br>"
                      + "Course ID: " + courseId;
    }

    @Override
    protected boolean isSessionOpenForSpecificUser(FeedbackSessionAttributes session) {
        // Feedback session closing date does not matter. Instructors can moderate at any time
        return true;
    }

    @Override
    protected RedirectResult createSpecificRedirectResult() {
        RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE);

        result.responseParams.put(Const.ParamsNames.COURSE_ID, moderatedStudent.course);
        result.responseParams.put(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        result.responseParams.put(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedStudent.email);

        return result;
    }
}
