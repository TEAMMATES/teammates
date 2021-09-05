package teammates.ui.webapi;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.logic.api.Logic;

/**
 * Provides access control mechanisms.
 */
final class GateKeeper {

    private static final GateKeeper instance = new GateKeeper();
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
    void verifyLoggedInUserPrivileges(UserInfo userInfo) throws UnauthorizedAccessException {
        if (userInfo != null) {
            return;
        }

        throw new UnauthorizedAccessException("User is not logged in");
    }

    // These methods ensures that the nominal user specified has access to a given entity

    /**
     * Verifies that the specified student can access the specified course.
     */
    void verifyAccessible(StudentAttributes student, CourseAttributes course) throws UnauthorizedAccessException {
        verifyNotNull(student, "student");
        verifyNotNull(student.getCourse(), "student's course ID");
        verifyNotNull(course, "course");
        verifyNotNull(course.getId(), "course ID");

        if (!student.getCourse().equals(course.getId())) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to student ["
                                                  + student.getEmail() + "]");
        }
    }

    /**
     * Verifies that the specified student can access the specified feedback session.
     */
    void verifyAccessible(StudentAttributes student, FeedbackSessionAttributes feedbackSession)
            throws UnauthorizedAccessException {
        verifyNotNull(student, "student");
        verifyNotNull(student.getCourse(), "student's course ID");
        verifyNotNull(feedbackSession, "feedback session");
        verifyNotNull(feedbackSession.getCourseId(), "feedback session's course ID");

        if (!student.getCourse().equals(feedbackSession.getCourseId())) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbackSession.getFeedbackSessionName()
                                                  + "] is not accessible to student [" + student.getEmail() + "]");
        }

        if (!feedbackSession.isVisible()) {
            throw new UnauthorizedAccessException("This feedback session is not yet visible.", true);
        }
    }

    /**
     * Verifies that the specified instructor can access the specified course.
     */
    void verifyAccessible(InstructorAttributes instructor, CourseAttributes course)
            throws UnauthorizedAccessException {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.getCourseId(), "instructor's course ID");
        verifyNotNull(course, "course");
        verifyNotNull(course.getId(), "course ID");

        if (!instructor.getCourseId().equals(course.getId())) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to instructor ["
                                                  + instructor.getEmail() + "]");
        }
    }

    /**
     * Verifies the instructor and course are not null, the instructor belongs to
     * the course and the instructor has the privilege specified by
     * privilegeName.
     */
    void verifyAccessible(InstructorAttributes instructor, CourseAttributes course, String privilegeName)
            throws UnauthorizedAccessException {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.getCourseId(), "instructor's course ID");
        verifyNotNull(course, "course");
        verifyNotNull(course.getId(), "course ID");

        if (!instructor.getCourseId().equals(course.getId())) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to instructor ["
                                                  + instructor.getEmail() + "]");
        }

        if (!instructor.isAllowedForPrivilege(privilegeName)) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to instructor ["
                                                  + instructor.getEmail() + "] for privilege [" + privilegeName + "]");
        }
    }

    /**
     * Verifies the instructor and course are not null, the instructor belongs to
     * the course and the instructor has the privilege specified by
     * privilegeName for sectionName.
     */
    void verifyAccessible(InstructorAttributes instructor, CourseAttributes course, String sectionName,
                                 String privilegeName)
            throws UnauthorizedAccessException {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.getCourseId(), "instructor's course ID");
        verifyNotNull(course, "course");
        verifyNotNull(course.getId(), "course ID");
        verifyNotNull(sectionName, "section name");

        if (!instructor.getCourseId().equals(course.getId())) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to instructor ["
                                                  + instructor.getEmail() + "]");
        }

        if (!instructor.isAllowedForPrivilege(sectionName, privilegeName)) {
            throw new UnauthorizedAccessException("Course [" + course.getId() + "] is not accessible to instructor ["
                                                  + instructor.getEmail() + "] for privilege [" + privilegeName
                                                  + "] on section [" + sectionName + "]");
        }
    }

    /**
     * Verifies that the specified instructor can access the specified feedback session.
     */
    void verifyAccessible(InstructorAttributes instructor, FeedbackSessionAttributes feedbackSession)
            throws UnauthorizedAccessException {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.getCourseId(), "instructor's course ID");
        verifyNotNull(feedbackSession, "feedback session");
        verifyNotNull(feedbackSession.getCourseId(), "feedback session's course ID");

        if (!instructor.getCourseId().equals(feedbackSession.getCourseId())) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbackSession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.getEmail() + "]");
        }
    }

    /**
     * Verifies the instructor and course are not null, the instructor belongs to
     * the course and the instructor has the privilege specified by
     * privilegeName for feedbackSession.
     */
    void verifyAccessible(InstructorAttributes instructor, FeedbackSessionAttributes feedbacksession,
                                 String privilegeName)
            throws UnauthorizedAccessException {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.getCourseId(), "instructor's course ID");
        verifyNotNull(feedbacksession, "feedback session");
        verifyNotNull(feedbacksession.getCourseId(), "feedback session's course ID");

        if (!instructor.getCourseId().equals(feedbacksession.getCourseId())) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbacksession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.getEmail() + "]");
        }

        if (!instructor.isAllowedForPrivilege(privilegeName)) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbacksession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.getEmail()
                                                  + "] for privilege [" + privilegeName + "]");
        }
    }

    /**
     * Verifies that the specified instructor has specified privilege for a section in the specified feedback session.
     */
    void verifyAccessible(InstructorAttributes instructor, FeedbackSessionAttributes feedbackSession,
                                 String sectionName, String privilegeName)
            throws UnauthorizedAccessException {
        verifyNotNull(instructor, "instructor");
        verifyNotNull(instructor.getCourseId(), "instructor's course ID");
        verifyNotNull(feedbackSession, "feedback session");
        verifyNotNull(feedbackSession.getCourseId(), "feedback session's course ID");

        if (!instructor.getCourseId().equals(feedbackSession.getCourseId())) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbackSession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.getEmail() + "]");
        }

        if (!instructor.isAllowedForPrivilege(sectionName, feedbackSession.getFeedbackSessionName(), privilegeName)) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbackSession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.getEmail()
                                                  + "] for privilege [" + privilegeName + "] on section ["
                                                  + sectionName + "]");
        }
    }

    /**
     * Verifies that the feedback question is for student to answer.
     */
    void verifyAnswerableForStudent(FeedbackQuestionAttributes feedbackQuestionAttributes)
            throws UnauthorizedAccessException {
        verifyNotNull(feedbackQuestionAttributes, "feedback question");

        if (feedbackQuestionAttributes.getGiverType() != FeedbackParticipantType.STUDENTS
                && feedbackQuestionAttributes.getGiverType() != FeedbackParticipantType.TEAMS) {
            throw new UnauthorizedAccessException("Feedback question is not answerable for students", true);
        }
    }

    /**
     * Verifies that the feedback question is for instructor to answer.
     */
    void verifyAnswerableForInstructor(FeedbackQuestionAttributes feedbackQuestionAttributes)
            throws UnauthorizedAccessException {
        verifyNotNull(feedbackQuestionAttributes, "feedback question");

        if (feedbackQuestionAttributes.getGiverType() != FeedbackParticipantType.INSTRUCTORS
                && feedbackQuestionAttributes.getGiverType() != FeedbackParticipantType.SELF) {
            throw new UnauthorizedAccessException("Feedback question is not answerable for instructors", true);
        }
    }

    /**
     * Verifies that an instructor has submission privilege of a feedback session.
     */
    void verifySessionSubmissionPrivilegeForInstructor(
            FeedbackSessionAttributes session, InstructorAttributes instructor)
            throws UnauthorizedAccessException {
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
    void verifyOwnership(FeedbackResponseCommentAttributes frc, String feedbackParticipant)
            throws UnauthorizedAccessException {
        verifyNotNull(frc, "feedback response comment");
        verifyNotNull(frc.getCommentGiver(), "feedback response comment giver");
        verifyNotNull(feedbackParticipant, "comment giver");

        if (!frc.getCommentGiver().equals(feedbackParticipant)) {
            throw new UnauthorizedAccessException("Comment [" + frc.getId() + "] is not accessible to "
                    + feedbackParticipant);
        }
    }

    // These methods ensures that the nominal user specified can perform the specified action on a given entity.

    private void verifyNotNull(Object object, String typeName)
            throws UnauthorizedAccessException {
        if (object == null) {
            throw new UnauthorizedAccessException("Trying to access system using a non-existent " + typeName + " entity");
        }
    }

    /**
     * Verifies that the action is accessible when the user is either an instructor of the course, a student of the course
     * or his/her team member.
     */
    void verifyAccessibleForCurrentUserAsInstructorOrTeamMember(String googleId, String courseId,
            String section, String email)
            throws UnauthorizedAccessException {
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, googleId);
        if (instructor != null) {
            verifyInstructorCanViewProfile(instructor, section);
            return;
        }

        StudentAttributes student = logic.getStudentForGoogleId(courseId, googleId);
        if (student != null) {
            verifyStudentCanViewProfile(student, courseId, email);
            return;
        }

        throw new UnauthorizedAccessException("User is not in the course that student belongs to");
    }

    private void verifyInstructorCanViewProfile(InstructorAttributes instructor, String section)
            throws UnauthorizedAccessException {
        if (!instructor.isAllowedForPrivilege(section, Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS)) {
            throw new UnauthorizedAccessException("Instructor does not have enough privileges to view the profile.");
        }
    }

    private void verifyStudentCanViewProfile(StudentAttributes student, String courseId, String email)
            throws UnauthorizedAccessException {
        if (!logic.isStudentsInSameTeam(courseId, email, student.getEmail())) {
            throw new UnauthorizedAccessException("Student does not have enough privileges to view the profile.");
        }
    }
}
