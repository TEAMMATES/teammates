package teammates.ui.webapi;

import teammates.common.datatransfer.AuthContext;
import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.storage.entity.Student;
import teammates.ui.exception.UnauthorizedAccessException;

/**
 * Provides access control mechanisms.
 */
final class GateKeeper {

    private static final GateKeeper instance = new GateKeeper();

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
     * Verifies that the specified account has student privileges in any course.
     */
    void verifyStudentInAnyCourse(Account account) throws UnauthorizedAccessException {
        if (account != null && account.getStudents() != null && !account.getStudents().isEmpty()) {
            return;
        }

        throw new UnauthorizedAccessException("Student privilege is required to access this resource.");
    }

    /**
     * Verifies that the specified account has instructor privileges in any course.
     */
    void verifyInstructorInAnyCourse(Account account) throws UnauthorizedAccessException {
        if (account != null && account.getInstructors() != null && !account.getInstructors().isEmpty()) {
            return;
        }

        throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
    }

    /**
     * Verifies that the specified student can access the specified course.
     */
    void verifyAccessible(Student student, Course course) throws UnauthorizedAccessException {
        verifyNotNull(student, "student");
        verifyNotNull(course, "course");

        if (!course.equals(student.getCourse())) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to student ["
                    + student.getEmail() + "]");
        }
    }

    /**
     * Verifies that the specified student can access the specified feedback session.
     */
    void verifyAccessible(Student student, FeedbackSession feedbackSession)
            throws UnauthorizedAccessException {
        verifyNotNull(student, "student");
        verifyNotNull(student.getCourse(), "student's course");
        verifyNotNull(feedbackSession, "feedback session");
        verifyNotNull(feedbackSession.getCourse(), "feedback session's course");

        if (!student.getCourse().equals(feedbackSession.getCourse())) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbackSession.getName()
                                                  + "] is not accessible to student [" + student.getEmail() + "]");
        }

        if (!feedbackSession.isVisible()) {
            throw new UnauthorizedAccessException("This feedback session is not yet visible.", true);
        }
    }

    /**
     * Verifies that the specified instructor can access the specified course.
     */
    void verifyAccessible(Instructor instructor, Course course)
            throws UnauthorizedAccessException {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.getCourse(), "instructor's course");
        verifyNotNull(course, "course");

        if (!course.equals(instructor.getCourse())) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to instructor ["
                    + instructor.getEmail() + "]");
        }
    }

    /**
     * Verifies the instructor and course are not null, the instructor belongs to
     * the course and the instructor has the privilege specified by
     * privilegeName.
     */
    void verifyAccessible(Instructor instructor, Course course, String privilegeName)
            throws UnauthorizedAccessException {
        verifyAccessible(instructor, course);

        boolean instructorIsAllowedCoursePrivilege = instructor.isAllowedForPrivilege(privilegeName);
        boolean instructorIsAllowedSectionPrivilege = !instructor.getSectionsWithPrivilege(privilegeName).isEmpty();
        if (!instructorIsAllowedCoursePrivilege && !instructorIsAllowedSectionPrivilege) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to instructor ["
                                                  + instructor.getEmail() + "] for privilege [" + privilegeName + "]");
        }
    }

    /**
     * Verifies the instructor and course are not null, the instructor belongs to
     * the course and the instructor has the privilege specified by
     * privilegeName for sectionName.
     */
    void verifyAccessible(Instructor instructor, Course course, String sectionName, String privilegeName)
            throws UnauthorizedAccessException {
        verifyAccessible(instructor, course);

        verifyNotNull(sectionName, "section name");

        if (!instructor.isAllowedForPrivilege(sectionName, privilegeName)) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to instructor ["
                                                  + instructor.getEmail() + "] for privilege [" + privilegeName
                                                  + "] on section [" + sectionName + "]");
        }
    }

    /**
     * Verifies that the specified instructor can access the specified feedback session.
     */
    void verifyAccessible(Instructor instructor, FeedbackSession feedbackSession)
            throws UnauthorizedAccessException {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.getCourse(), "instructor's course");
        verifyNotNull(feedbackSession, "feedback session");
        verifyNotNull(feedbackSession.getCourse(), "feedback session's course");

        if (!instructor.getCourse().equals(feedbackSession.getCourse())) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbackSession.getName()
                                                  + "] is not accessible to instructor [" + instructor.getEmail() + "]");
        }
    }

    /**
     * Verifies the instructor and course are not null, the instructor belongs to
     * the course and the instructor has the privilege specified by
     * privilegeName for feedbackSession.
     */
    void verifyAccessible(Instructor instructor, FeedbackSession feedbacksession, String privilegeName)
            throws UnauthorizedAccessException {
        verifyAccessible(instructor, feedbacksession);

        if (!instructor.isAllowedForPrivilege(privilegeName)
                && !instructor.isAllowedForPrivilegeAnySection(feedbacksession.getName(), privilegeName)) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbacksession.getName()
                                                  + "] is not accessible to instructor [" + instructor.getEmail()
                                                  + "] for privilege [" + privilegeName + "]");
        }
    }

    /**
     * Verifies that the specified instructor has specified privilege for a section in the specified feedback session.
     */
    void verifyAccessible(Instructor instructor, FeedbackSession feedbackSession, String sectionName, String privilegeName)
            throws UnauthorizedAccessException {
        verifyAccessible(instructor, feedbackSession);

        if (!instructor.isAllowedForPrivilege(sectionName, feedbackSession.getName(), privilegeName)) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbackSession.getName()
                                                  + "] is not accessible to instructor [" + instructor.getEmail()
                                                  + "] for privilege [" + privilegeName + "] on section ["
                                                  + sectionName + "]");
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

    /**
     * Verifies that comment is created by instructor.
     *
     * @param frc comment to be accessed
     * @param instructor the instructor who is trying to access the comment
     */
    void verifyOwnership(ResponseInstructorComment frc, Instructor instructor)
            throws UnauthorizedAccessException {
        verifyNotNull(frc, "feedback response comment");
        verifyNotNull(frc.getGiver(), "feedback response comment giver");
        verifyNotNull(instructor, "comment giver");

        if (!frc.getGiver().equals(instructor)) {
            throw new UnauthorizedAccessException("Comment [" + frc.getId() + "] is not accessible to "
                    + instructor);
        }
    }

    // These methods ensures that the nominal user specified can perform the specified action on a given entity.

    private void verifyNotNull(Object object, String typeName)
            throws UnauthorizedAccessException {
        if (object == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent " + typeName + " entity");
        }
    }

}
