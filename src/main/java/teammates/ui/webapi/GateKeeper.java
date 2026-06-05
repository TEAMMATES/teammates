package teammates.ui.webapi;

import teammates.common.datatransfer.AuthContext;
import teammates.common.util.Const;
import teammates.logic.core.UsersLogic;
import teammates.storage.entity.Course;
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

    private GateKeeper() {
        // prevent initialization
    }

    public static GateKeeper inst() {
        return instance;
    }

    /**
     * Verifies the user is logged in.
     */
    void verifyLoggedInUserPrivileges(AuthContext authContext) throws UnauthorizedAccessException {
        if (authContext.account() != null) {
            return;
        }

        throw new UnauthorizedAccessException("User is not logged in");
    }

    void verifyAdminPrivileges(AuthContext authContext) throws UnauthorizedAccessException {
        if (authContext.isAdmin()) {
            return;
        }

        throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
    }

    /**
     * Verifies that the specified auth context has student privileges in any course.
     */
    void verifyStudentInAnyCourse(AuthContext authContext) throws UnauthorizedAccessException {
        if (authContext.account() != null
                && !usersLogic.getStudentsByAccountId(authContext.account().getId()).isEmpty()) {
            return;
        }

        throw new UnauthorizedAccessException("Student privilege is required to access this resource.");
    }

    /**
     * Verifies that the specified auth context has instructor privileges in any course.
     */
    void verifyInstructorInAnyCourse(AuthContext authContext) throws UnauthorizedAccessException {
        if (authContext.account() != null
                && !usersLogic.getInstructorsByAccountId(authContext.account().getId()).isEmpty()) {
            return;
        }

        throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
    }

    /**
     * Verifies that the specified student can access the specified course.
     */
    void verifyStudentInCourse(Student student, Course course) throws UnauthorizedAccessException {
        verifyNotNull(student, "student");
        verifyNotNull(course, "course");

        if (!course.equals(student.getCourse())) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to student ["
                    + student.getEmail() + "]");
        }
    }

    /**
     * Verifies that the specified instructor can access the specified course.
     */
    void verifyInstructorInCourse(Instructor instructor, Course course)
            throws UnauthorizedAccessException {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(course, "course");

        if (!course.equals(instructor.getCourse())) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to instructor ["
                    + instructor.getEmail() + "]");
        }
    }

    /**
     * Verifies that the specified student can access the specified feedback session.
     */
    void verifyStudentCanAccessSession(Student student, FeedbackSession feedbackSession)
            throws UnauthorizedAccessException {
        verifyNotNull(student, "student");
        verifyNotNull(feedbackSession, "feedback session");

        if (!student.getCourse().equals(feedbackSession.getCourse())) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbackSession.getName()
                                                  + "] is not accessible to student [" + student.getEmail() + "]");
        }

        if (!feedbackSession.isVisible()) {
            throw new UnauthorizedAccessException("This feedback session is not yet visible.", true);
        }
    }

    /**
     * Verifies that the specified instructor can access the specified feedback session.
     */
    void verifyInstructorCanAccessSession(Instructor instructor, FeedbackSession feedbackSession)
            throws UnauthorizedAccessException {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(feedbackSession, "feedback session");

        if (!instructor.getCourse().equals(feedbackSession.getCourse())) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbackSession.getName()
                                                  + "] is not accessible to instructor [" + instructor.getEmail() + "]");
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
