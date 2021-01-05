package teammates.logic.api;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
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

    private boolean isUserLoggedOn() {
        return userService.getCurrentUser() != null;
    }

    /**
     * Gets the information of the current logged in user.
     */
    public UserInfo getCurrentUser() {
        User user = getCurrentGoogleUser();

        if (user == null) {
            return null;
        }

        UserInfo userInfo = new UserInfo(user);

        if (isAdministrator()) {
            userInfo.isAdmin = true;
        }

        if (isInstructor()) {
            userInfo.isInstructor = true;
        }

        if (isStudent()) {
            userInfo.isStudent = true;
        }

        return userInfo;
    }

    /**
     * Gets the information of the current masqueraded user.
     *
     * <p>Note that this assumes that the privilege to masquerade as another user is present.
     */
    public UserInfo getMasqueradeUser(String googleId) {
        UserInfo userInfo = new UserInfo(googleId);
        userInfo.isAdmin = false;
        userInfo.isInstructor = accountsLogic.isAccountAnInstructor(googleId);
        userInfo.isStudent = studentsLogic.isStudentInAnyCourse(googleId);
        return userInfo;
    }

    /**
     * Gets the login URL with the specified page as the redirect after logging in (if successful).
     */
    public String getLoginUrl(String redirectPage) {
        User user = userService.getCurrentUser();

        if (user == null) {
            return userService.createLoginURL(redirectPage);
        }
        return redirectPage;
    }

    /**
     * Gets the logout URL with the specified page as the redirect after logging out.
     */
    public String getLogoutUrl(String redirectPage) {
        return userService.createLogoutURL(redirectPage);
    }

    // These methods ensures the logged in user is of a particular type.

    /**
     * Verifies the user is logged in.
     */
    public void verifyLoggedInUserPrivileges() {
        if (isUserLoggedOn()) {
            return;
        }

        throw new UnauthorizedAccessException("User is not logged in");
    }

    // These methods ensures that the nominal user specified has access to a given entity

    /**
     * Verifies that the specified student can access the specified course.
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

    /**
     * Verifies that the specified student can access the specified feedback session.
     */
    public void verifyAccessible(StudentAttributes student, FeedbackSessionAttributes feedbackSession) {
        verifyNotNull(student, "student");
        verifyNotNull(student.course, "student's course ID");
        verifyNotNull(feedbackSession, "feedback session");
        verifyNotNull(feedbackSession.getCourseId(), "feedback session's course ID");

        if (!student.course.equals(feedbackSession.getCourseId())) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbackSession.getFeedbackSessionName()
                                                  + "] is not accessible to student [" + student.email + "]");
        }

        if (!feedbackSession.isVisible()) {
            throw new UnauthorizedAccessException("This feedback session is not yet visible.", true);
        }
    }

    /**
     * Verifies that the specified instructor can access the specified course.
     */
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

    /**
     * Verifies that the specified instructor can access the specified feedback session.
     */
    public void verifyAccessible(InstructorAttributes instructor, FeedbackSessionAttributes feedbackSession) {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(feedbackSession, "feedback session");
        verifyNotNull(feedbackSession.getCourseId(), "feedback session's course ID");

        if (!instructor.courseId.equals(feedbackSession.getCourseId())) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbackSession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.email + "]");
        }
    }

    /**
     * Verifies the instructor and course are not null, the instructor belongs to
     * the course and the instructor has the privilege specified by
     * privilegeName for feedbackSession.
     */
    public void verifyAccessible(InstructorAttributes instructor, FeedbackSessionAttributes feedbacksession,
                                 String privilegeName) {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(feedbacksession, "feedback session");
        verifyNotNull(feedbacksession.getCourseId(), "feedback session's course ID");

        if (!instructor.courseId.equals(feedbacksession.getCourseId())) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbacksession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.email + "]");
        }

        if (!instructor.isAllowedForPrivilege(privilegeName)) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbacksession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.email
                                                  + "] for privilege [" + privilegeName + "]");
        }
    }

    /**
     * Verifies that the specified instructor has specified privilege for a section in the specified feedback session.
     */
    public void verifyAccessible(InstructorAttributes instructor, FeedbackSessionAttributes feedbackSession,
                                 String sectionName, String privilegeName) {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.courseId, "instructor's course ID");
        verifyNotNull(feedbackSession, "feedback session");
        verifyNotNull(feedbackSession.getCourseId(), "feedback session's course ID");

        if (!instructor.courseId.equals(feedbackSession.getCourseId())) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbackSession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.email + "]");
        }

        if (!instructor.isAllowedForPrivilege(sectionName, feedbackSession.getFeedbackSessionName(), privilegeName)) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbackSession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.email
                                                  + "] for privilege [" + privilegeName + "] on section ["
                                                  + sectionName + "]");
        }
    }

    /**
     * Verifies that the feedback question is for student to answer.
     */
    public void verifyAnswerableForStudent(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        verifyNotNull(feedbackQuestionAttributes, "feedback question");

        if (feedbackQuestionAttributes.getGiverType() != FeedbackParticipantType.STUDENTS
                && feedbackQuestionAttributes.getGiverType() != FeedbackParticipantType.TEAMS) {
            throw new UnauthorizedAccessException("Feedback question is not answerable for students", true);
        }
    }

    /**
     * Verifies that the feedback question is for instructor to answer.
     */
    public void verifyAnswerableForInstructor(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        verifyNotNull(feedbackQuestionAttributes, "feedback question");

        if (feedbackQuestionAttributes.getGiverType() != FeedbackParticipantType.INSTRUCTORS
                && feedbackQuestionAttributes.getGiverType() != FeedbackParticipantType.SELF) {
            throw new UnauthorizedAccessException("Feedback question is not answerable for instructors", true);
        }
    }

    /**
     * Verifies that an instructor has submission privilege of a feedback session.
     */
    public void verifySessionSubmissionPrivilegeForInstructor(
            FeedbackSessionAttributes session, InstructorAttributes instructor) {
        verifyNotNull(session, "feedback session");
        verifyNotNull(instructor, "instructor");

        boolean shouldEnableSubmit =
                instructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS);

        if (!shouldEnableSubmit && instructor.isAllowedForPrivilegeAnySection(session.getFeedbackSessionName(),
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS)) {
            shouldEnableSubmit = true;
        }

        if (!shouldEnableSubmit) {
            throw new UnauthorizedAccessException("You don't have submission privilege");
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

    // These methods ensures that the nominal user specified can perform the specified action on a given entity.

    private void verifyNotNull(Object object, String typeName) {
        if (object == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent " + typeName + " entity");
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

    /**
     * Verifies that the action is accessible when the user is either an instructor of the course, a student of the course
     * or his/her team member, or an admin.
     */
    public void verifyAccessibleForCurrentUserAsInstructorOrTeamMemberOrAdmin(String googleId, String courseId,
            String section, String email) {
        if (isAdministrator()) {
            return;
        }

        InstructorAttributes instructor = instructorsLogic.getInstructorForGoogleId(courseId, googleId);
        if (instructor != null) {
            verifyInstructorCanViewProfile(instructor, section);
            return;
        }

        StudentAttributes student = studentsLogic.getStudentForCourseIdAndGoogleId(courseId, googleId);
        if (student != null) {
            verifyStudentCanViewProfile(student, courseId, email);
            return;
        }

        throw new UnauthorizedAccessException("User is not in the course that student belongs to");
    }

    private void verifyInstructorCanViewProfile(InstructorAttributes instructor, String section) {
        if (!instructor.isAllowedForPrivilege(section, Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS)) {
            throw new UnauthorizedAccessException("Instructor does not have enough privileges to view the profile.");
        }
    }

    private void verifyStudentCanViewProfile(StudentAttributes student, String courseId, String email) {
        if (!studentsLogic.isStudentsInSameTeam(courseId, email, student.email)) {
            throw new UnauthorizedAccessException("Student does not have enough privileges to view the profile.");
        }
    }
}
