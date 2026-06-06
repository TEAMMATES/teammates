package teammates.ui.webapi;

import teammates.common.datatransfer.RequestContext;
import teammates.common.util.Const;
import teammates.logic.core.AuthLogic;
import teammates.logic.core.UsersLogic;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Provides access control mechanisms.
 */
final class GateKeeper {

    private static final GateKeeper instance = new GateKeeper();

    private final UsersLogic usersLogic = UsersLogic.inst();
    private final AuthLogic authLogic = AuthLogic.inst();

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
     * Verifies the instructor is not null and has the privilege specified by privilegeName.
     */
    void verifyInstructorHasPrivilege(Instructor instructor, String privilegeName)
            throws UnauthorizedAccessException {
        boolean instructorIsAllowedCoursePrivilege = instructor.isAllowedForPrivilege(privilegeName);
        boolean instructorIsAllowedSectionPrivilege = !instructor.getSectionsWithPrivilege(privilegeName).isEmpty();
        if (!instructorIsAllowedCoursePrivilege && !instructorIsAllowedSectionPrivilege) {
            throw new UnauthorizedAccessException("Instructor [" + instructor.getEmail()
                                                  + "] does not have privilege [" + privilegeName + "]");
        }
    }

    /**
     * Verifies the instructor is not null and has the privilege specified by privilegeName for sectionName.
     */
    void verifyInstructorHasPrivilege(Instructor instructor, String sectionName, String privilegeName)
            throws UnauthorizedAccessException {
        verifyNotNull(sectionName, "section name");

        if (!instructor.isAllowedForPrivilege(sectionName, privilegeName)) {
            throw new UnauthorizedAccessException("Instructor [" + instructor.getEmail()
                                                  + "] does not have privilege [" + privilegeName
                                                  + "] on section [" + sectionName + "]");
        }
    }

    /**
     * Verifies that an instructor has submission privilege for a feedback session.
     */
    void verifySessionSubmissionPrivilegeForInstructor(FeedbackSession session, Instructor instructor)
            throws UnauthorizedAccessException {
        verifyNotNull(session, "feedback session");
        verifyNotNull(instructor, "instructor");

        boolean shouldEnableSubmit =
                instructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);

        if (!shouldEnableSubmit && instructor.isAllowedForPrivilegeAnySection(session.getName(),
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS)) {
            shouldEnableSubmit = true;
        }

        if (!shouldEnableSubmit) {
            throw new UnauthorizedAccessException("You don't have submission privilege");
        }
    }

    private void verifyNotNull(Object object, String typeName)
            throws UnauthorizedAccessException {
        if (object == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent " + typeName + " entity");
        }
    }

}
