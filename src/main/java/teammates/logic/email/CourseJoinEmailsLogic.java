package teammates.logic.email;

import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.logic.email.model.CourseEmailContext;
import teammates.logic.email.model.InstructorCourseJoinEmailContext;
import teammates.logic.email.model.InstructorCourseRejoinAfterUnlinkEmailContext;
import teammates.logic.email.model.RenderedEmail;
import teammates.logic.email.model.StudentCourseJoinEmailContext;
import teammates.logic.email.model.StudentCourseRejoinAfterUnlinkEmailContext;
import teammates.logic.email.model.UserCourseRegisteredEmailContext;

/**
 * Handles email-specific orchestration for course join-related use cases.
 */
public class CourseJoinEmailsLogic {

    private static final CourseJoinEmailsLogic instance = new CourseJoinEmailsLogic(EmailQueueService.inst());

    private final EmailQueueService emailQueueService;

    CourseJoinEmailsLogic(EmailQueueService emailQueueService) {
        this.emailQueueService = emailQueueService;
    }

    public static CourseJoinEmailsLogic inst() {
        return instance;
    }

    /**
     * Enqueues the post-join course registration confirmation email.
     */
    public void enqueueUserCourseRegisteredEmail(
            CourseEmailContext courseContext, UserCourseRegisteredEmailContext userContext) {
        RenderedEmail renderedEmail = EmailRenderer.renderUserCourseRegisteredEmail(courseContext, userContext);
        EmailWrapper email = EmailWrapperBuilder.build(
                userContext.recipientEmailAddress(),
                EmailType.USER_COURSE_REGISTER,
                renderedEmail,
                courseContext.courseName(),
                courseContext.courseId());
        emailQueueService.enqueuePriority(email);
    }

    /**
     * Enqueues the student course join invitation email.
     */
    public void enqueueStudentCourseJoinEmail(
            CourseEmailContext courseContext, StudentCourseJoinEmailContext studentContext) {
        RenderedEmail renderedEmail = EmailRenderer.renderStudentCourseJoinEmail(courseContext, studentContext);
        EmailWrapper email = EmailWrapperBuilder.build(
                studentContext.recipientEmailAddress(),
                EmailType.STUDENT_COURSE_JOIN,
                renderedEmail,
                courseContext.courseName(),
                courseContext.courseId());
        emailQueueService.enqueuePriority(email);
    }

    /**
     * Enqueues the student course join invitation emails.
     */
    public void enqueueStudentCourseJoinEmails(
            CourseEmailContext courseContext, Iterable<StudentCourseJoinEmailContext> studentContexts) {
        for (StudentCourseJoinEmailContext studentContext : studentContexts) {
            enqueueStudentCourseJoinEmail(courseContext, studentContext);
        }
    }

    /**
     * Enqueues the student course rejoin email after account unlink.
     */
    public void enqueueStudentCourseRejoinAfterUnlinkAccountEmail(
            CourseEmailContext courseContext, StudentCourseRejoinAfterUnlinkEmailContext studentContext) {
        RenderedEmail renderedEmail = EmailRenderer.renderStudentCourseRejoinAfterUnlinkAccountEmail(
                courseContext, studentContext);
        EmailWrapper email = EmailWrapperBuilder.build(
                studentContext.recipientEmailAddress(),
                EmailType.STUDENT_COURSE_REJOIN_AFTER_UNLINK_ACCOUNT,
                renderedEmail,
                courseContext.courseName(),
                courseContext.courseId());
        emailQueueService.enqueuePriority(email);
    }

    /**
     * Enqueues the instructor course join invitation email.
     */
    public void enqueueInstructorCourseJoinEmail(
            CourseEmailContext courseContext, InstructorCourseJoinEmailContext instructorContext) {
        RenderedEmail renderedEmail = EmailRenderer.renderInstructorCourseJoinEmail(courseContext, instructorContext);
        EmailWrapper email = EmailWrapperBuilder.build(
                instructorContext.recipientEmailAddress(),
                EmailType.INSTRUCTOR_COURSE_JOIN,
                renderedEmail,
                courseContext.courseName(),
                courseContext.courseId());
        emailQueueService.enqueuePriority(email);
    }

    /**
     * Enqueues the instructor course rejoin email after account unlink.
     */
    public void enqueueInstructorCourseRejoinAfterUnlinkAccountEmail(
            CourseEmailContext courseContext, InstructorCourseRejoinAfterUnlinkEmailContext instructorContext) {
        RenderedEmail renderedEmail = EmailRenderer.renderInstructorCourseRejoinAfterUnlinkAccountEmail(
                courseContext, instructorContext);
        EmailWrapper email = EmailWrapperBuilder.build(
                instructorContext.recipientEmailAddress(),
                EmailType.INSTRUCTOR_COURSE_REJOIN_AFTER_UNLINK_ACCOUNT,
                renderedEmail,
                courseContext.courseName(),
                courseContext.courseId());
        emailQueueService.enqueuePriority(email);
    }
}
