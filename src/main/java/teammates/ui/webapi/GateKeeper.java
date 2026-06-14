package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.datatransfer.RequestContext;
import teammates.common.util.Const;
import teammates.logic.api.Logic;
import teammates.logic.core.AuthLogic;
import teammates.logic.core.UsersLogic;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Provides access control mechanisms.
 */
final class GateKeeper {

    private static final GateKeeper instance = new GateKeeper();

    // TODO: refactor to remove direct dependency on logic classes and instead call the logic facade.
    private final UsersLogic usersLogic = UsersLogic.inst();
    private final AuthLogic authLogic = AuthLogic.inst();
    private final Logic logic = Logic.inst();

    private GateKeeper() {
        // prevent initialization
    }

    public static GateKeeper inst() {
        return instance;
    }

    /**
     * Verifies the user is logged in.
     */
    void verifyLoggedInUserPrivileges(RequestContext requestContext) throws UnauthorizedAccessException {
        if (requestContext.getAccount() != null) {
            return;
        }

        throw new UnauthorizedAccessException("User is not logged in");
    }

    /**
     * Verifies that the user has admin privileges.
     */
    void verifyAdminPrivileges(RequestContext requestContext) throws UnauthorizedAccessException {
        if (requestContext.isAdmin()) {
            return;
        }

        throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
    }

    /**
     * Verifies that the user has student privileges in any course.
     */
    void verifyStudentInAnyCourse(RequestContext requestContext) throws UnauthorizedAccessException {
        if (requestContext.getAccount() != null
                && !usersLogic.getStudentsByAccountId(requestContext.getAccount().getId()).isEmpty()) {
            return;
        }

        throw new UnauthorizedAccessException("Student privilege is required to access this resource.");
    }

    /**
     * Verifies that the user has instructor privileges in any course.
     */
    void verifyInstructorInAnyCourse(RequestContext requestContext) throws UnauthorizedAccessException {
        if (requestContext.getAccount() != null
                && !usersLogic.getInstructorsByAccountId(requestContext.getAccount().getId()).isEmpty()) {
            return;
        }

        throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
    }

    /**
     * Verifies that the user has student privileges in the specified course.
     */
    void verifyStudentInCourse(RequestContext requestContext, String courseId) throws UnauthorizedAccessException {
        Student student = requestContext.getStudentForCourse(courseId, authLogic::getStudentFromAuthContext);
        verifyNotNull(student, "student");

        if (!student.getCourseId().equals(courseId)) {
            throw new UnauthorizedAccessException("Course [" + courseId + "] is not accessible to student ["
                    + student.getEmail() + "]");
        }
    }

    /**
     * Verifies that the user is a member (student or instructor) of the specified course.
     */
    void verifyUserInCourse(RequestContext requestContext, String courseId)
            throws UnauthorizedAccessException {
        Student student = requestContext.getStudentForCourse(courseId, authLogic::getStudentFromAuthContext);
        if (student != null && student.getCourseId().equals(courseId)) {
            return;
        }
        Instructor instructor = requestContext.getInstructorForCourse(courseId, authLogic::getInstructorFromAuthContext);
        if (instructor != null && instructor.getCourseId().equals(courseId)) {
            return;
        }
        throw new UnauthorizedAccessException("User is not a member of course [" + courseId + "]");
    }

    /**
     * Verifies that the user has instructor privileges in the specified course.
     */
    void verifyInstructorInCourse(RequestContext requestContext, String courseId)
            throws UnauthorizedAccessException {
        Instructor instructor = requestContext.getInstructorForCourse(courseId, authLogic::getInstructorFromAuthContext);
        verifyNotNull(instructor, "instructor");

        if (!instructor.getCourseId().equals(courseId)) {
            throw new UnauthorizedAccessException("Course [" + courseId + "] is not accessible to instructor ["
                    + instructor.getEmail() + "]");
        }
    }

    /**
     * Verifies that the user has instructor privileges in the same course as the specified instructor.
     */
    void verifyInstructorInSameCourseAsInstructor(RequestContext requestContext, UUID userId)
            throws UnauthorizedAccessException {
        Instructor instructor = logic.getInstructor(userId);
        verifyNotNull(instructor, "instructor");
        verifyInstructorInCourse(requestContext, instructor.getCourseId());
    }

    /**
     * Verifies that the user has instructor privileges to view the specified student.
     */
    void verifyInstructorCanViewStudent(RequestContext requestContext, UUID userId)
            throws UnauthorizedAccessException {
        Student student = logic.getStudent(userId);
        verifyNotNull(student, "student");
        verifyInstructorHasPrivilegeForSection(requestContext, student.getCourseId(), student.getSectionId(),
                Const.InstructorPermissions.CAN_VIEW_STUDENT);
    }

    /**
     * Verifies that the user can view the deadline extension for the given user in the given feedback session.
     *
     * <p>Admins, the user themselves (via regkey or account), and instructors with CAN_MODIFY_SESSION
     * are allowed to access.
     */
    void verifyCanViewDeadlineExtension(RequestContext requestContext, UUID feedbackSessionId, UUID userId)
            throws UnauthorizedAccessException {
        if (requestContext.isAdmin()) {
            return;
        }

        User regKeyUser = requestContext.getRegKeyUser();
        if (regKeyUser != null && regKeyUser.getId().equals(userId)) {
            return;
        }

        if (requestContext.getAccount() != null) {
            User targetUser = logic.getUser(userId);
            if (targetUser != null && requestContext.getAccount().getId().equals(targetUser.getAccountId())) {
                return;
            }

            FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
            verifyNotNull(feedbackSession, "feedback session");
            Instructor instructor = requestContext.getInstructorForCourse(feedbackSession.getCourseId(),
                    authLogic::getInstructorFromAuthContext);
            if (instructor != null && logic.hasInstructorPermissions(instructor,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION)) {
                return;
            }
        }

        throw new UnauthorizedAccessException("Not authorized to view this deadline extension.");
    }

    /**
     * Verifies that the user can view the specified account request.
     *
     * <p>Admins can view all account requests. Non-admins can only view account requests that they own.
     */
    void verifyCanViewAccountRequest(RequestContext requestContext, UUID accountRequestId)
            throws UnauthorizedAccessException {
        if (requestContext.isAdmin()) {
            return;
        }

        AccountRequest accountRequest = logic.getAccountRequest(accountRequestId);
        verifyNotNull(accountRequest, "account request");

        if (requestContext.getAccount() == null
                || !requestContext.getAccount().getId().equals(accountRequest.getAccountId())) {
            throw new UnauthorizedAccessException("Account request [" + accountRequestId + "] is not accessible to user");
        }
    }

    /**
     * Verifies that the user has student privileges in the course of the specified feedback session.
     */
    void verifyStudentInFeedbackSession(RequestContext requestContext, UUID feedbackSessionId)
            throws UnauthorizedAccessException {
        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        verifyNotNull(feedbackSession, "feedback session");
        verifyStudentInCourse(requestContext, feedbackSession.getCourseId());
    }

    /**
     * Verifies that the user has instructor privileges in the course of the specified feedback session.
     */
    void verifyInstructorInFeedbackSession(RequestContext requestContext, UUID feedbackSessionId)
            throws UnauthorizedAccessException {
        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        verifyNotNull(feedbackSession, "feedback session");
        verifyInstructorInCourse(requestContext, feedbackSession.getCourseId());
    }

    /**
     * Verifies that the user has instructor privileges in the course of the specified feedback question.
     */
    void verifyInstructorInFeedbackQuestion(RequestContext requestContext, UUID feedbackQuestionId)
            throws UnauthorizedAccessException {
        FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);
        verifyNotNull(feedbackQuestion, "feedback question");
        verifyInstructorInCourse(requestContext, feedbackQuestion.getCourseId());
    }

    /**
     * Verifies that the user can access the specified feedback session.
     *
     * <p>The user must be a course member (student or instructor). Instructors with CAN_VIEW_SESSION
     * privilege may access regardless of session visibility; all others require the session to be visible.
     */
    void verifyFeedbackSessionAccessible(RequestContext requestContext, UUID feedbackSessionId)
            throws UnauthorizedAccessException {
        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        verifyNotNull(feedbackSession, "feedback session");
        String courseId = feedbackSession.getCourseId();

        verifyUserInCourse(requestContext, courseId);

        Instructor instructor = requestContext.getInstructorForCourse(courseId, authLogic::getInstructorFromAuthContext);
        if (instructor != null) {
            boolean canViewSession = logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_VIEW_SESSION)
                    || !logic.getSectionsWithInstructorPermission(instructor,
                            Const.InstructorPermissions.CAN_VIEW_SESSION).isEmpty();
            if (canViewSession) {
                return;
            }
        }

        if (!feedbackSession.isVisible()) {
            throw new UnauthorizedAccessException("This feedback session is not yet visible.", true);
        }
    }

    /**
     * Verifies the instructor has the privileges specified by privilegeNames for the course
     * of the specified feedback session.
     */
    void verifyInstructorHasPrivilegeInFeedbackSession(RequestContext requestContext, UUID feedbackSessionId,
            String... privilegeNames) throws UnauthorizedAccessException {
        FeedbackSession feedbackSession = logic.getFeedbackSession(feedbackSessionId);
        verifyNotNull(feedbackSession, "feedback session");
        verifyInstructorHasPrivilege(requestContext, feedbackSession.getCourseId(), privilegeNames);
    }

    /**
     * Verifies the instructor has the privileges specified by privilegeNames for the course
     * of the specified feedback question.
     */
    void verifyInstructorHasPrivilegeInFeedbackQuestion(RequestContext requestContext, UUID feedbackQuestionId,
            String... privilegeNames) throws UnauthorizedAccessException {
        FeedbackQuestion feedbackQuestion = logic.getFeedbackQuestion(feedbackQuestionId);
        verifyNotNull(feedbackQuestion, "feedback question");
        verifyInstructorHasPrivilege(requestContext, feedbackQuestion.getCourseId(), privilegeNames);
    }

    /**
     * Verifies the instructor for the specified course has the privileges specified by privilegeNames.
     */
    void verifyInstructorHasPrivilege(RequestContext requestContext, String courseId, String... privilegeNames)
            throws UnauthorizedAccessException {
        Instructor instructor = requestContext.getInstructorForCourse(courseId, authLogic::getInstructorFromAuthContext);
        verifyInstructorHasPrivilege(instructor, privilegeNames);
    }

    /**
     * Verifies the instructor has the privileges specified by privilegeNames.
     */
    void verifyInstructorHasPrivilege(Instructor instructor, String... privilegeNames)
            throws UnauthorizedAccessException {
        for (String privilegeName : privilegeNames) {
            boolean instructorIsAllowedCoursePrivilege =
                    instructor != null && logic.hasInstructorPermissions(instructor, privilegeName);
            boolean instructorIsAllowedSectionPrivilege =
                    instructor != null
                            && !logic.getSectionsWithInstructorPermission(instructor, privilegeName).isEmpty();
            if (!instructorIsAllowedCoursePrivilege && !instructorIsAllowedSectionPrivilege) {
                throw new UnauthorizedAccessException("Instructor does not have privilege [" + privilegeName + "]");
            }
        }
    }

    /**
     * Verifies the instructor for the specified course has the privileges specified by privilegeNames for sectionId.
     */
    void verifyInstructorHasPrivilegeForSection(RequestContext requestContext, String courseId, UUID sectionId,
            String... privilegeNames) throws UnauthorizedAccessException {
        Instructor instructor = requestContext.getInstructorForCourse(courseId, authLogic::getInstructorFromAuthContext);
        verifyInstructorHasPrivilegeForSection(instructor, sectionId, privilegeNames);
    }

    /**
     * Verifies the instructor has the privileges specified by privilegeNames for sectionId.
     */
    void verifyInstructorHasPrivilegeForSection(Instructor instructor, UUID sectionId, String... privilegeNames)
            throws UnauthorizedAccessException {
        verifyNotNull(sectionId, "section ID");

        for (String privilegeName : privilegeNames) {
            if (instructor == null
                    || !logic.hasInstructorPermissionsForSection(instructor, sectionId, privilegeName)) {
                throw new UnauthorizedAccessException("Instructor does not have privilege [" + privilegeName
                                                      + "] on section [" + sectionId + "]");
            }
        }
    }

    private void verifyNotNull(Object object, String typeName)
            throws UnauthorizedAccessException {
        if (object == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent " + typeName + " entity");
        }
    }

}
