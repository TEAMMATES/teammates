package teammates.ui.webapi;

import java.time.Instant;

import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * The basic action for feedback submission.
 */
abstract class BasicFeedbackSubmissionAction extends Action {
    /**
     * Checks whether instructors can see the question.
     */
    boolean canInstructorSeeQuestion(FeedbackQuestion feedbackQuestion) {
        boolean isGiverVisibleToInstructor =
                feedbackQuestion.getShowGiverNameTo().contains(ViewerType.INSTRUCTORS);
        boolean isRecipientVisibleToInstructor =
                feedbackQuestion.getShowRecipientNameTo().contains(ViewerType.INSTRUCTORS);
        boolean isResponseVisibleToInstructor =
                feedbackQuestion.getShowResponsesTo().contains(ViewerType.INSTRUCTORS);
        return isResponseVisibleToInstructor && isGiverVisibleToInstructor && isRecipientVisibleToInstructor;
    }

    /**
     * Verifies that instructor can see the moderated question in moderation request.
     */
    void verifyInstructorCanSeeQuestionIfInModeration(FeedbackQuestion feedbackQuestion)
            throws UnauthorizedAccessException {
        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);

        if (!StringHelper.isEmpty(moderatedPerson) && !canInstructorSeeQuestion(feedbackQuestion)) {
            // should not moderate question which instructors cannot see
            throw new UnauthorizedAccessException("The question is not applicable for moderation", true);
        }
    }

    /**
     * Gets the student involved in the submission process.
     */
    Student getStudentOfCourseFromRequest(String courseId) {
        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (!StringHelper.isEmpty(moderatedPerson)) {
            return logic.getStudentForEmail(courseId, moderatedPerson);
        } else if (!StringHelper.isEmpty(previewAsPerson)) {
            return logic.getStudentForEmail(courseId, previewAsPerson);
        } else {
            return getPossiblyUnregisteredStudent(courseId);
        }
    }

    /**
     * Checks the access control for student feedback submission.
     */
    void checkAccessControlForStudentFeedbackSubmission(Student student, FeedbackSession feedbackSession)
            throws UnauthorizedAccessException {
        if (student == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent student entity");
        }

        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (!StringHelper.isEmpty(moderatedPerson)) {
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            gateKeeper.verifyAccessible(
                    logic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId()), feedbackSession,
                    student.getSectionName(),
                    Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        } else if (!StringHelper.isEmpty(previewAsPerson)) {
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            gateKeeper.verifyAccessible(
                    logic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId()), feedbackSession,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION);
        } else {
            gateKeeper.verifyAccessible(student, feedbackSession);
            if (student.getAccount() != null) {
                if (userInfo == null) {
                    // Student is associated with an account; even if registration key is passed, do not allow access
                    throw new UnauthorizedAccessException("Login is required to access this feedback session");
                } else if (!userInfo.id.equals(student.getAccount().getGoogleId())) {
                    // Logged in student is not the same as the student registered for the given key, do not allow access
                    throw new UnauthorizedAccessException("You are not authorized to access this feedback session");
                }
            }
        }
    }

    /**
     * Checks the access control for student feedback result.
     */
    void checkAccessControlForStudentFeedbackResult(
            Student student, FeedbackSession feedbackSession) throws UnauthorizedAccessException {
        if (student == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent student entity");
        }

        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (StringHelper.isEmpty(previewAsPerson)) {
            gateKeeper.verifyAccessible(student, feedbackSession);
            verifyMatchingGoogleId(student.getGoogleId());
        } else {
            checkAccessControlForPreview(feedbackSession, false);
        }
    }

    /**
     * Gets the instructor involved in the submission process.
     */
    Instructor getInstructorOfCourseFromRequest(String courseId) {
        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (!StringHelper.isEmpty(moderatedPerson)) {
            return logic.getInstructorForEmail(courseId, moderatedPerson);
        } else if (!StringHelper.isEmpty(previewAsPerson)) {
            return logic.getInstructorForEmail(courseId, previewAsPerson);
        } else {
            return getPossiblyUnregisteredInstructor(courseId);
        }
    }

    /**
     * Checks the access control for instructor feedback submission.
     */
    void checkAccessControlForInstructorFeedbackSubmission(
            Instructor instructor, FeedbackSession feedbackSession) throws UnauthorizedAccessException {
        if (instructor == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
        }

        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (!StringHelper.isEmpty(moderatedPerson)) {
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            gateKeeper.verifyAccessible(
                    logic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId()),
                    feedbackSession, Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        } else if (!StringHelper.isEmpty(previewAsPerson)) {
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            gateKeeper.verifyAccessible(
                    logic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId()),
                    feedbackSession, Const.InstructorPermissions.CAN_MODIFY_SESSION);
        } else {
            gateKeeper.verifySessionSubmissionPrivilegeForInstructor(feedbackSession, instructor);
            if (instructor.getAccount() != null) {
                if (userInfo == null) {
                    // Instructor is associated to an account; even if registration key is passed, do not allow access
                    throw new UnauthorizedAccessException("Login is required to access this feedback session");
                } else if (!userInfo.id.equals(instructor.getAccount().getGoogleId())) {
                    // Logged in instructor is not the same as the instructor registered for the given key,
                    // do not allow access
                    throw new UnauthorizedAccessException("You are not authorized to access this feedback session");
                }
            }
        }
    }

    /**
     * Checks the access control for instructor feedback result.
     */
    void checkAccessControlForInstructorFeedbackResult(
            Instructor instructor, FeedbackSession feedbackSession) throws UnauthorizedAccessException {
        if (instructor == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
        }

        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (StringHelper.isEmpty(previewAsPerson)) {
            gateKeeper.verifyAccessible(instructor, feedbackSession,
                    Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
            verifyMatchingGoogleId(instructor.getGoogleId());
        } else {
            checkAccessControlForPreview(feedbackSession, true);
        }
    }

    private void verifyMatchingGoogleId(String googleId) throws UnauthorizedAccessException {
        if (!StringHelper.isEmpty(googleId)) {
            if (userInfo == null) {
                // Student/Instructor is associated to a google ID; even if registration key is passed, do not allow access
                throw new UnauthorizedAccessException("Login is required to access this feedback session");
            } else if (!userInfo.id.equals(googleId)) {
                // Logged in student/instructor is not the same as the student/instructor registered for the given key,
                // do not allow access
                throw new UnauthorizedAccessException("You are not authorized to access this feedback session");
            }
        }
    }

    @SuppressWarnings("PMD.IdenticalConditionalBranches") // TODO find out why!
    private void checkAccessControlForPreview(FeedbackSession feedbackSession, boolean isInstructor)
            throws UnauthorizedAccessException {
        gateKeeper.verifyLoggedInUserPrivileges(userInfo);
        if (isInstructor) {
            gateKeeper.verifyAccessible(
                    logic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId()), feedbackSession,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION);
        } else {
            gateKeeper.verifyAccessible(
                    logic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId()), feedbackSession,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION);
        }
    }

    /**
     * Verifies that it is not a preview request.
     */
    void verifyNotPreview() throws UnauthorizedAccessException {
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);
        if (!StringHelper.isEmpty(previewAsPerson)) {
            // should not view response under preview mode
            throw new UnauthorizedAccessException("You are not allowed to see responses when previewing", true);
        }
    }

    /**
     * Verifies that the session is open for submission.
     *
     * <p>If it is moderation request, omit the check.
     */
    void verifySessionOpenExceptForModeration(FeedbackSession feedbackSession, User user)
            throws UnauthorizedAccessException {
        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        Instant deadlineExtension = logic.getDeadlineForUser(feedbackSession, user);

        if (StringHelper.isEmpty(moderatedPerson) && !(feedbackSession.isOpenedGivenExtendedDeadline(deadlineExtension)
                || feedbackSession.isInGracePeriodGivenExtendedDeadline(deadlineExtension))) {
            throw new UnauthorizedAccessException("The feedback session is not available for submission", true);
        }
    }

    /**
     * Gets the section of a recipient.
     */
    @SuppressWarnings("PMD.ImplicitSwitchFallThrough") // false positive
    Section getRecipientSection(
            String courseId, QuestionGiverType giverType, QuestionRecipientType recipientType,
            String recipientIdentifier) {

        switch (recipientType) {
        case SELF:
            switch (giverType) {
            case INSTRUCTORS:
            case SELF:
                return logic.getDefaultSectionOrCreate(courseId);
            case TEAMS:
            case TEAMS_IN_SAME_SECTION:
                Section section = logic.getSectionByCourseIdAndTeam(courseId, recipientIdentifier);
                return section == null ? logic.getDefaultSectionOrCreate(courseId) : section;
            case STUDENTS:
            case STUDENTS_IN_SAME_SECTION:
                Student student = logic.getStudentForEmail(courseId, recipientIdentifier);
                return student == null ? logic.getDefaultSectionOrCreate(courseId) : student.getSection();
            default:
                assert false : "Invalid giver type " + giverType + " for recipient type " + recipientType;
                return null;
            }
        case INSTRUCTORS:
        case NONE:
            return logic.getDefaultSectionOrCreate(courseId);
        case TEAMS:
        case TEAMS_EXCLUDING_SELF:
        case TEAMS_IN_SAME_SECTION:
        case OWN_TEAM:
            Section section = logic.getSectionByCourseIdAndTeam(courseId, recipientIdentifier);
            return section == null ? logic.getDefaultSectionOrCreate(courseId) : section;
        case STUDENTS:
        case STUDENTS_EXCLUDING_SELF:
        case STUDENTS_IN_SAME_SECTION:
        case OWN_TEAM_MEMBERS:
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            Student student = logic.getStudentForEmail(courseId, recipientIdentifier);
            return student == null ? logic.getDefaultSectionOrCreate(courseId) : student.getSection();
        default:
            assert false : "Unknown recipient type " + recipientType;
            return null;
        }
    }
}
