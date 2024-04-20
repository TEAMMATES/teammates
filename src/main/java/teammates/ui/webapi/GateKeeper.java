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
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

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
    void verifyAccessible(InstructorAttributes instructor, CourseAttributes course, String sectionName, String privilegeName)
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

        if (!instructor.isAllowedForPrivilege(privilegeName)
                && !instructor.isAllowedForPrivilegeAnySection(feedbacksession.getFeedbackSessionName(), privilegeName)) {
            throw new UnauthorizedAccessException("Feedback session [" + feedbacksession.getFeedbackSessionName()
                                                  + "] is not accessible to instructor [" + instructor.getEmail()
                                                  + "] for privilege [" + privilegeName + "]");
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
     * Verifies that the feedback question is for student to answer.
     */
    void verifyAnswerableForStudent(FeedbackQuestion feedbackQuestion)
            throws UnauthorizedAccessException {
        verifyNotNull(feedbackQuestion, "feedback question");

        if (feedbackQuestion.getGiverType() != FeedbackParticipantType.STUDENTS
                && feedbackQuestion.getGiverType() != FeedbackParticipantType.TEAMS) {
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
     * Verifies that the feedback question is for instructor to answer.
     */
    void verifyAnswerableForInstructor(FeedbackQuestion feedbackQuestion)
            throws UnauthorizedAccessException {
        verifyNotNull(feedbackQuestion, "feedback question");

        if (feedbackQuestion.getGiverType() != FeedbackParticipantType.INSTRUCTORS
                && feedbackQuestion.getGiverType() != FeedbackParticipantType.SELF) {
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

    /**
     * Verifies that comment is created by feedback participant.
     *
     * @param frc comment to be accessed
     * @param feedbackParticipant email or team of feedback participant
     */
    void verifyOwnership(FeedbackResponseComment frc, String feedbackParticipant)
            throws UnauthorizedAccessException {
        verifyNotNull(frc, "feedback response comment");
        verifyNotNull(frc.getGiver(), "feedback response comment giver");
        verifyNotNull(feedbackParticipant, "comment giver");

        if (!frc.getGiver().equals(feedbackParticipant)) {
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

}
