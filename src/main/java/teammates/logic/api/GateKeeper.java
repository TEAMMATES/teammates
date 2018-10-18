package teammates.logic.api;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import teammates.common.datatransfer.UserType;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.FeedbackSessionNotVisibleException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;

/**
 * Provides access control mechanisms.
 */
public class GateKeeper {

    private static UserService userService = UserServiceFactory.getUserService();

    private static final AccountsLogic accountsLogic = AccountsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    public boolean isUserLoggedOn() {
        return userService.getCurrentUser() != null;
    }

    public UserType getCurrentUser() {
        User user = getCurrentGoogleUser();

        if (user == null) {
            return null;
        }

        UserType userType = new UserType(user);

        if (isAdministrator()) {
            userType.isAdmin = true;
        }

        if (isInstructor()) {
            userType.isInstructor = true;
        }

        if (isStudent()) {
            userType.isStudent = true;
        }

        return userType;
    }

    public String getLoginUrl(String redirectPage) {
        User user = userService.getCurrentUser();

        if (user == null) {
            return userService.createLoginURL(redirectPage);
        }
        return redirectPage;
    }

    public String getLogoutUrl(String redirectPage) {
        return userService.createLogoutURL(redirectPage);
    }

    /**
     * These methods ensures the logged in user is of a particular type.
     */

    /**
     * Verifies the user is logged in.
     */
    public void verifyLoggedInUserPrivileges() {
        if (isUserLoggedOn()) {
            return;
        }

        throw new UnauthorizedAccessException("User is not logged in");
    }

    /**
     * Verifies that the logged in user is the admin and there is no
     * masquerading going on.
     */
    public void verifyAdminPrivileges(AccountAttributes account) {
        if (isUserLoggedOn() && userService.isUserAdmin()
                && getCurrentGoogleUser().getNickname().equals(account.googleId)) {
            return;
        }

        throw new UnauthorizedAccessException("User " + getCurrentGoogleUser().getNickname()
                                              + " does not have admin privilleges");
    }

    /**
     * Verifies that the nominal user has instructor privileges.
     */
    public void verifyInstructorPrivileges(AccountAttributes account) {
        if (account.isInstructor) {
            return;
        }
        throw new UnauthorizedAccessException("User " + account.googleId
                                              + " does not have instructor privilleges");
    }

    /**
     * Verifies that the nominal user has student privileges. Currently, all
     * logged in users as student privileges.
     */
    public void verifyStudentPrivileges(AccountAttributes account) {
        verifyLoggedInUserPrivileges();
    }

    /**
     * These methods ensures that the nominal user specified has access to a
     * given entity.
     */

    public void verifyAccessible(StudentAttributes student, CourseAttributes course) {
        verifyNotNull(student, "student");
        verifyNotNull(student.course, "student's course ID");
        verifyNotNull(course, "course");
        verifyNotNull(course.getId(), "course ID");

        if (!student.course.equals(course.getId())) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to student ["
                                                  + student.email + "]");
        }
    }

    public void verifyAccessible(StudentAttributes student, FeedbackSessionAttributes feedbacksession) {
        verifyNotNull(student, "student");
        verifyNotNull(student.course, "student's course ID");
        verifyNotNull(feedbacksession, "feedback session");
        verifyNotNull(feedbacksession.getCourseId(), "feedback session's course ID");

        if (!student.course.equals(feedbacksession.getCourseId())) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbacksession.getFeedbackSessionName()
                                                  + "] is not accessible to student [" + student.email + "]");
        }

        if (!feedbacksession.isVisible()) {
            throw new FeedbackSessionNotVisibleException(
                                            "This feedback session is not yet visible.",
                                            feedbacksession.getStartTimeString());
        }
    }

    /**
     * Verifies that comment is created by feedback participant.
     *
     * @param frc comment to be accessed
     * @param feedbackParticipant email or team of feedback participant
     */
    public void verifyOwnership(FeedbackResponseCommentAttributes frc, String feedbackParticipant) {
        verifyNotNull(frc, "feedback response comment");
        verifyNotNull(frc.commentGiver, "feedback response comment giver");
        verifyNotNull(feedbackParticipant, "comment giver");

        if (!frc.commentGiver.equals(feedbackParticipant)) {
            throw new UnauthorizedAccessException("Comment [" + frc.getId() + "] is not accessible to "
                    + feedbackParticipant);
        }
    }

    public void verifyAccessible(InstructorAttributes instructor, CourseAttributes course) {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(course, "course");
        verifyNotNull(course.getId(), "course ID");

        if (!instructor.courseId.equals(course.getId())) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to instructor ["
                                                  + instructor.email + "]");
        }
    }

    /**
     * Verifies the instructor and course are not null, the instructor belongs to
     * the course and the instructor has the privilege specified by
     * privilegeName.
     */
    public void verifyAccessible(InstructorAttributes instructor, CourseAttributes course, String privilegeName) {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(course, "course");
        verifyNotNull(course.getId(), "course ID");

        if (!instructor.courseId.equals(course.getId())) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to instructor ["
                                                  + instructor.email + "]");
        }

        if (!instructor.isAllowedForPrivilege(privilegeName)) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to instructor ["
                                                  + instructor.email + "] for privilege [" + privilegeName + "]");
        }
    }

    /**
     * Verifies the instructor and course are not null, the instructor belongs to
     * the course and the instructor has the privilege specified by
     * privilegeName for sectionName.
     */
    public void verifyAccessible(InstructorAttributes instructor, CourseAttributes course, String sectionName,
                                 String privilegeName) {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(course, "course");
        verifyNotNull(course.getId(), "course ID");
        verifyNotNull(sectionName, "section name");

        if (!instructor.courseId.equals(course.getId())) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to instructor ["
                                                  + instructor.email + "]");
        }

        if (!instructor.isAllowedForPrivilege(sectionName, privilegeName)) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to instructor ["
                                                  + instructor.email + "] for privilege [" + privilegeName
                                                  + "] on section [" + sectionName + "]");
        }
    }

    public void verifyAccessible(InstructorAttributes instructor, FeedbackSessionAttributes feedbacksession,
                                 boolean creatorOnly) {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(feedbacksession, "feedback session");
        verifyNotNull(feedbacksession.getCourseId(), "feedback session's course ID");

        if (!instructor.courseId.equals(feedbacksession.getCourseId())) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbacksession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.email + "]");
        }

        if (creatorOnly && !feedbacksession.getCreatorEmail().equals(instructor.email)) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbacksession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.email
                                                  + "] for this purpose");
        }
    }

    /**
     * Verifies the instructor and course are not null, the instructor belongs to
     * the course and the instructor has the privilege specified by
     * privilegeName for feedbackSession.
     */
    public void verifyAccessible(InstructorAttributes instructor, FeedbackSessionAttributes feedbacksession,
                                 boolean creatorOnly, String privilegeName) {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(feedbacksession, "feedback session");
        verifyNotNull(feedbacksession.getCourseId(), "feedback session's course ID");

        if (!instructor.courseId.equals(feedbacksession.getCourseId())) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbacksession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.email + "]");
        }

        if (creatorOnly && !feedbacksession.getCreatorEmail().equals(instructor.email)) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbacksession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.email
                                                  + "] for this purpose");
        }

        if (!instructor.isAllowedForPrivilege(privilegeName)) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbacksession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.email
                                                  + "] for privilege [" + privilegeName + "]");
        }
    }

    public void verifyAccessible(InstructorAttributes instructor, FeedbackSessionAttributes feedbacksession,
                                 boolean creatorOnly, String sectionName, String privilegeName) {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(feedbacksession, "feedback session");
        verifyNotNull(feedbacksession.getCourseId(), "feedback session's course ID");

        if (!instructor.courseId.equals(feedbacksession.getCourseId())) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbacksession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.email + "]");
        }

        if (creatorOnly && !feedbacksession.getCreatorEmail().equals(instructor.email)) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbacksession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.email
                                                  + "] for this purpose");
        }

        if (!instructor.isAllowedForPrivilege(sectionName, feedbacksession.getFeedbackSessionName(), privilegeName)) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbacksession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.email
                                                  + "] for privilege [" + privilegeName + "] on section ["
                                                  + sectionName + "]");
        }
    }

    /**
     * These methods ensures that the nominal user specified can perform the
     * specified action on a given entity.
     */

    // TODO: to be implemented when we adopt more finer-grain access control.
    private void verifyNotNull(Object object, String typeName) {
        if (object == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent " + typeName
                                                  + " entity");
        }

    }

    private User getCurrentGoogleUser() {
        return userService.getCurrentUser();
    }

    private boolean isAdministrator() {
        Assumption.assertTrue(isUserLoggedOn());
        return userService.isUserAdmin();
    }

    private boolean isInstructor() {
        User user = userService.getCurrentUser();
        Assumption.assertNotNull(user);
        return accountsLogic.isAccountAnInstructor(user.getNickname());
    }

    private boolean isStudent() {
        User user = userService.getCurrentUser();
        Assumption.assertNotNull(user);

        return studentsLogic.isStudentInAnyCourse(user.getNickname());
    }

    public void verifyAccessibleForCurrentUserAsInstructorOrTeamMemberOrAdmin(AccountAttributes account, String courseId,
            String section, String email) {
        if (isAdministrator()) {
            return;
        }

        InstructorAttributes instructor = instructorsLogic.getInstructorForGoogleId(courseId, account.googleId);
        if (instructor != null) {
            verifyInstructorCanViewPhoto(instructor, section);
            return;
        }

        StudentAttributes student = studentsLogic.getStudentForCourseIdAndGoogleId(courseId, account.googleId);
        if (student != null) {
            verifyStudentCanViewPhoto(student, courseId, email);
            return;
        }

        throw new UnauthorizedAccessException("User is not in the course that student belongs to");
    }

    private void verifyInstructorCanViewPhoto(InstructorAttributes instructor, String section) {
        if (!instructor.isAllowedForPrivilege(section, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS)) {
            throw new UnauthorizedAccessException("Instructor does not have enough privileges to view the photo");
        }
    }

    private void verifyStudentCanViewPhoto(StudentAttributes student, String courseId, String email) {
        if (!studentsLogic.isStudentsInSameTeam(courseId, email, student.email)) {
            throw new UnauthorizedAccessException("Student does not have enough privileges to view the photo");
        }
    }
}
