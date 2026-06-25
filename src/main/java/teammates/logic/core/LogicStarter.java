package teammates.logic.core;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import teammates.common.util.Logger;
import teammates.logic.email.AccountVerificationEmailsLogic;
import teammates.logic.email.CourseJoinEmailsLogic;
import teammates.logic.email.DeadlineExtensionEmailsLogic;
import teammates.logic.email.EmailQueueService;
import teammates.logic.email.FeedbackSessionEmailsLogic;
import teammates.storage.api.AccountVerificationRequestsDb;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.DeadlineExtensionsDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionLogsDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.InstitutesDb;
import teammates.storage.api.InstructorPermissionsDb;
import teammates.storage.api.NotificationsDb;
import teammates.storage.api.ResponseInstructorCommentsDb;
import teammates.storage.api.UsersDb;

/**
 * Setup in web.xml to register logic classes at application startup.
 */
public class LogicStarter implements ServletContextListener {

    private static final Logger log = Logger.getLogger();

    /**
     * Registers dependencies between different logic classes.
     */
    public static void initializeDependencies() {
        AuthLogic authLogic = AuthLogic.inst();
        AccountsLogic accountsLogic = AccountsLogic.inst();
        AccountVerificationsLogic accountVerificationsLogic = AccountVerificationsLogic.inst();
        CoursesLogic coursesLogic = CoursesLogic.inst();
        EnrollmentLogic enrollmentLogic = EnrollmentLogic.inst();
        InstitutesLogic institutesLogic = InstitutesLogic.inst();
        DataBundleLogic dataBundleLogic = DataBundleLogic.inst();
        DeadlineExtensionsLogic deadlineExtensionsLogic = DeadlineExtensionsLogic.inst();
        DemoCourseLogic demoCourseLogic = DemoCourseLogic.inst();
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        FeedbackSessionLogsLogic fslLogic = FeedbackSessionLogsLogic.inst();
        FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
        ResponseInstructorCommentsLogic frcLogic = ResponseInstructorCommentsLogic.inst();
        FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
        NotificationsLogic notificationsLogic = NotificationsLogic.inst();
        UsageStatisticsLogic usageStatisticsLogic = UsageStatisticsLogic.inst();
        UsersLogic usersLogic = UsersLogic.inst();
        InstructorPermissionsLogic instructorPermissionsLogic = InstructorPermissionsLogic.inst();

        CourseJoinEmailsLogic courseJoinEmailsLogic = CourseJoinEmailsLogic.inst();
        DeadlineExtensionEmailsLogic deadlineExtensionEmailsLogic = DeadlineExtensionEmailsLogic.inst();
        FeedbackSessionEmailsLogic feedbackSessionEmailsLogic = FeedbackSessionEmailsLogic.inst();
        AccountVerificationEmailsLogic accountVerificationEmailsLogic = AccountVerificationEmailsLogic.inst();
        EmailQueueService emailQueueService = EmailQueueService.inst();

        courseJoinEmailsLogic.init(emailQueueService);
        deadlineExtensionEmailsLogic.init(emailQueueService);
        feedbackSessionEmailsLogic.init(emailQueueService);
        accountVerificationEmailsLogic.init(emailQueueService);

        authLogic.initLogicDependencies(usersLogic, accountsLogic);
        institutesLogic.initLogicDependencies(InstitutesDb.inst());
        accountVerificationsLogic.initLogicDependencies(
                AccountVerificationRequestsDb.inst(), accountsLogic, institutesLogic, accountVerificationEmailsLogic);
        accountsLogic.initLogicDependencies(AccountsDb.inst(), usersLogic);
        coursesLogic.initLogicDependencies(
                CoursesDb.inst(), usersLogic, institutesLogic, accountVerificationsLogic, instructorPermissionsLogic);
        dataBundleLogic.initLogicDependencies(accountsLogic, notificationsLogic, institutesLogic);
        deadlineExtensionsLogic.initLogicDependencies(DeadlineExtensionsDb.inst(), fsLogic, coursesLogic, usersLogic,
                deadlineExtensionEmailsLogic);
        fsLogic.initLogicDependencies(FeedbackSessionsDb.inst(), frLogic, fqLogic, usersLogic, coursesLogic,
                deadlineExtensionsLogic,
                feedbackSessionEmailsLogic);
        fslLogic.initLogicDependencies(FeedbackSessionLogsDb.inst());
        frLogic.initLogicDependencies(FeedbackResponsesDb.inst(), usersLogic, fqLogic, fsLogic, frcLogic,
                instructorPermissionsLogic);
        frcLogic.initLogicDependencies(ResponseInstructorCommentsDb.inst(), frLogic);
        fqLogic.initLogicDependencies(FeedbackQuestionsDb.inst(), coursesLogic, frLogic, usersLogic, fsLogic,
                instructorPermissionsLogic);
        notificationsLogic.initLogicDependencies(NotificationsDb.inst(), accountsLogic);
        usageStatisticsLogic.initLogicDependencies(frLogic, coursesLogic, usersLogic, accountVerificationsLogic);
        enrollmentLogic.initLogicDependencies(usersLogic, coursesLogic, fsLogic);
        usersLogic.initLogicDependencies(UsersDb.inst(), coursesLogic, courseJoinEmailsLogic,
                fsLogic, frLogic, instructorPermissionsLogic);
        instructorPermissionsLogic.initLogicDependencies(InstructorPermissionsDb.inst());
        demoCourseLogic.initLogicDependencies(
                accountVerificationsLogic, accountsLogic, coursesLogic, dataBundleLogic);
        log.info("Initialized dependencies between logic classes");
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Invoked by Jetty at application startup.
        initializeDependencies();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Nothing to do
    }

}
