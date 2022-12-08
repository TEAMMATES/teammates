package teammates.ui.webapi;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;

/**
 * The basic action for feedback submission.
 */
abstract class BasicFeedbackSubmissionAction extends Action {

    /**
     * Checks whether instructors can see the question.
     */
    boolean canInstructorSeeQuestion(FeedbackQuestionAttributes feedbackQuestion) {
        boolean isGiverVisibleToInstructor =
                feedbackQuestion.getShowGiverNameTo().contains(FeedbackParticipantType.INSTRUCTORS);
        boolean isRecipientVisibleToInstructor =
                feedbackQuestion.getShowRecipientNameTo().contains(FeedbackParticipantType.INSTRUCTORS);
        boolean isResponseVisibleToInstructor =
                feedbackQuestion.getShowResponsesTo().contains(FeedbackParticipantType.INSTRUCTORS);
        return isResponseVisibleToInstructor && isGiverVisibleToInstructor && isRecipientVisibleToInstructor;
    }

    /**
     * Verifies that instructor can see the moderated question in moderation request.
     */
    void verifyInstructorCanSeeQuestionIfInModeration(FeedbackQuestionAttributes feedbackQuestion)
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
    StudentAttributes getStudentOfCourseFromRequest(String courseId) {
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
    void checkAccessControlForStudentFeedbackSubmission(
            StudentAttributes student, FeedbackSessionAttributes feedbackSession) throws UnauthorizedAccessException {
        if (student == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent student entity");
        }

        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (!StringHelper.isEmpty(moderatedPerson)) {
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            gateKeeper.verifyAccessible(
                    logic.getInstructorForGoogleId(feedbackSession.getCourseId(), userInfo.getId()), feedbackSession,
                    student.getSection(),
                    Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        } else if (!StringHelper.isEmpty(previewAsPerson)) {
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            // TODO: How is CAN_MODIFY_SESSION related to preview submission? Why not CAN_VIEW_SESSION_IN_SECTIONS?
            gateKeeper.verifyAccessible(
                    logic.getInstructorForGoogleId(feedbackSession.getCourseId(), userInfo.getId()), feedbackSession,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION);
        } else {
            gateKeeper.verifyAccessible(student, feedbackSession);
            if (!StringHelper.isEmpty(student.getGoogleId())) {
                // TODO: Why are these checks not present in the original GetFeedbackQuestionsAction
                // TODO: for STUDENT_RESULT intent? View results is less strict than submit result?
                if (userInfo == null) {
                    // Student is associated to a google ID; even if registration key is passed, do not allow access
                    throw new UnauthorizedAccessException("Login is required to access this feedback session");
                } else if (!userInfo.id.equals(student.getGoogleId())) {
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
            StudentAttributes student, FeedbackSessionAttributes feedbackSession) throws UnauthorizedAccessException {
        if (student == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent student entity");
        }

        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);
        boolean isPreview = !StringHelper.isEmpty(previewAsPerson);

        if (isPreview) {
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);

            String courseId = feedbackSession.getCourseId();
            InstructorAttributes instructorPreviewer = logic.getInstructorForGoogleId(courseId, userInfo.id);
            // TODO: The key question to address here is instructors with what set of permissions
            // TODO: should be allowed to preview results (i.e., to view results as any student)
            // TODO: [or perform any action using this method?]

            // previewer should have permission to view the session
            gateKeeper.verifyAccessible(instructorPreviewer, feedbackSession, student.getSection(),
                    Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
            // previewer should have permission to view students' info [debatable!]
            // gateKeeper.verifyAccessible(instructorPreviewer, logic.getCourse(courseId),
            //        Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS);
        } else {
            gateKeeper.verifyAccessible(student, feedbackSession);
            // TODO: No extra (GoogleID) check following the original way of checking access control for STUDENT_RESULT!
        }
    }

    /**
     * Gets the instructor involved in the submission process.
     */
    InstructorAttributes getInstructorOfCourseFromRequest(String courseId) {
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
            InstructorAttributes instructor, FeedbackSessionAttributes feedbackSession) throws UnauthorizedAccessException {
        if (instructor == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
        }

        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);
        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);

        if (!StringHelper.isEmpty(moderatedPerson)) {
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(feedbackSession.getCourseId(), userInfo.getId()),
                    feedbackSession, Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        } else if (!StringHelper.isEmpty(previewAsPerson)) {
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(feedbackSession.getCourseId(), userInfo.getId()),
                    feedbackSession, Const.InstructorPermissions.CAN_MODIFY_SESSION);
        } else {
            gateKeeper.verifySessionSubmissionPrivilegeForInstructor(feedbackSession, instructor);
            if (!StringHelper.isEmpty(instructor.getGoogleId())) {
                if (userInfo == null) {
                    // Instructor is associated to a google ID; even if registration key is passed, do not allow access
                    throw new UnauthorizedAccessException("Login is required to access this feedback session");
                } else if (!userInfo.id.equals(instructor.getGoogleId())) {
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
            InstructorAttributes instructor, FeedbackSessionAttributes feedbackSession) throws UnauthorizedAccessException {
        if (instructor == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent instructor entity");
        }

        String previewAsPerson = getRequestParamValue(Const.ParamsNames.PREVIEWAS);
        boolean isPreview = !StringHelper.isEmpty(previewAsPerson);

        if (isPreview) {
            gateKeeper.verifyLoggedInUserPrivileges(userInfo);
            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(feedbackSession.getCourseId(), userInfo.getId()),
                    feedbackSession, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
            // TODO: It may be better to check whether previewAsPerson (instructor) has permission to view session
            // TODO: if not, return specific error message [i.e., when preview as an instructor without permission]
        } else {
            gateKeeper.verifyAccessible(instructor, feedbackSession,
                    Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
            // TODO: Original GetFeedbackQuestionsAction requires instructor to be logged in for INSTRUCTOR_RESULT
            // TODO: Original GetSessionResultsAction does not verify feedbackSession, just like for STUDENT_RESULT!
            // TODO: No extra (GoogleID) check following the original way of checking access control for INSTRUCTOR_RESULT!
            // TODO: Copy here from the method above if needed!
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
    void verifySessionOpenExceptForModeration(FeedbackSessionAttributes feedbackSession) throws UnauthorizedAccessException {
        String moderatedPerson = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON);

        if (StringHelper.isEmpty(moderatedPerson) && !(feedbackSession.isOpened() || feedbackSession.isInGracePeriod())) {
            throw new UnauthorizedAccessException("The feedback session is not available for submission", true);
        }
    }

    /**
     * Gets the section of a recipient.
     */
    String getRecipientSection(
            String courseId, FeedbackParticipantType giverType, FeedbackParticipantType recipientType,
            String recipientIdentifier) {
        switch (recipientType) {
        case SELF:
            switch (giverType) {
            case INSTRUCTORS:
            case SELF:
                return Const.DEFAULT_SECTION;
            case TEAMS:
            case TEAMS_IN_SAME_SECTION:
                return logic.getSectionForTeam(courseId, recipientIdentifier);
            case STUDENTS:
            case STUDENTS_IN_SAME_SECTION:
                StudentAttributes student = logic.getStudentForEmail(courseId, recipientIdentifier);
                return student == null ? Const.DEFAULT_SECTION : student.getSection();
            default:
                assert false : "Invalid giver type " + giverType + " for recipient type " + recipientType;
                return null;
            }
        case INSTRUCTORS:
        case NONE:
            return Const.DEFAULT_SECTION;
        case TEAMS:
        case TEAMS_EXCLUDING_SELF:
        case TEAMS_IN_SAME_SECTION:
        case OWN_TEAM:
            return logic.getSectionForTeam(courseId, recipientIdentifier);
        case STUDENTS:
        case STUDENTS_EXCLUDING_SELF:
        case STUDENTS_IN_SAME_SECTION:
        case OWN_TEAM_MEMBERS:
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            StudentAttributes student = logic.getStudentForEmail(courseId, recipientIdentifier);
            return student == null ? Const.DEFAULT_SECTION : student.getSection();
        default:
            assert false : "Unknown recipient type " + recipientType;
            return null;
        }
    }

}
