package teammates.logic.email;

import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.logic.email.model.CourseEmailContext;
import teammates.logic.email.model.RenderedEmail;
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
}
