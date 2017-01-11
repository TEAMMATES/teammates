package teammates.logic.api;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.FeedbackSessionNotVisibleException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.StudentsLogic;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class GateKeeper {
    private static UserService userService = UserServiceFactory.getUserService();

    private static GateKeeper instance;

    public static GateKeeper inst() {
        if (instance == null) {
            instance = new GateKeeper();
        }
        return instance;
    }

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

    /** Verifies the user is logged in */
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

        if (!student.course.equals(feedbacksession.getCourseId()) || feedbacksession.isPrivateSession()) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbacksession.getFeedbackSessionName()
                                                  + "] is not accessible to student [" + student.email + "]");
        }

        if (!feedbacksession.isVisible()) {
            throw new FeedbackSessionNotVisibleException(
                                            "This feedback session is not yet visible.",
                                            feedbacksession.getStartTimeString());
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
     * verify the instructor and course are not null, the instructor belongs to
     * the course and the instructor has the privilege specified by
     * privilegeName
     *
     * @param instructor
     * @param course
     * @param privilegeName
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
     * verify the instructor and course are not null, the instructor belongs to
     * the course and the instructor has the privilege specified by
     * privilegeName for sectionName
     *
     * @param instructor
     * @param course
     * @param sectionName
     * @param privilegeName
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
     * verify the instructor and course are not null, the instructor belongs to
     * the course and the instructor has the privilege specified by
     * privilegeName for feedbackSession
     *
     * @param instructor
     * @param feedbacksession
     * @param creatorOnly
     * @param privilegeName
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
        return AccountsLogic.inst().isAccountAnInstructor(user.getNickname());
    }

    private boolean isStudent() {
        User user = userService.getCurrentUser();
        Assumption.assertNotNull(user);

        return StudentsLogic.inst().isStudentInAnyCourse(user.getNickname());
    }

    public void verifyAccessibleForCurrentUserAsInstructorOrTeamMember(AccountAttributes account, String courseId,
            String section, String email) {
        InstructorAttributes instructor = InstructorsLogic.inst().getInstructorForGoogleId(courseId, account.googleId);
        if (instructor != null) {
            if (!instructor.isAllowedForPrivilege(section,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS)) {
                throw new UnauthorizedAccessException("Instructor does not have enough privileges to view the photo");
            }
            return;
        }
        
        StudentAttributes student = StudentsLogic.inst().getStudentForCourseIdAndGoogleId(courseId, account.googleId);
        if (student != null) {
            if (!StudentsLogic.inst().isStudentsInSameTeam(courseId, email, student.email)) {
                throw new UnauthorizedAccessException("Student does not have enough privileges to view the photo");
            }
            return;
        }
        
        throw new UnauthorizedAccessException("User is not in the course that student belongs to");
    }
}
