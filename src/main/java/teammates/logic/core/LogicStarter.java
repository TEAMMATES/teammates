package teammates.logic.core;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import teammates.common.util.Logger;
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
        InstitutesLogic institutesLogic = InstitutesLogic.inst();
        DataBundleLogic dataBundleLogic = DataBundleLogic.inst();
        DeadlineExtensionsLogic deadlineExtensionsLogic = DeadlineExtensionsLogic.inst();
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        FeedbackSessionLogsLogic fslLogic = FeedbackSessionLogsLogic.inst();
        FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
        ResponseInstructorCommentsLogic frcLogic = ResponseInstructorCommentsLogic.inst();
        FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
        NotificationsLogic notificationsLogic = NotificationsLogic.inst();
        UsageStatisticsLogic usageStatisticsLogic = UsageStatisticsLogic.inst();
        UsersLogic usersLogic = UsersLogic.inst();
        InstructorPermissionsLogic instructorPermissionsLogic = InstructorPermissionsLogic.inst();

        authLogic.initLogicDependencies(usersLogic);
        institutesLogic.initLogicDependencies(InstitutesDb.inst());
        accountVerificationsLogic.initLogicDependencies(
                AccountVerificationRequestsDb.inst(), accountsLogic, institutesLogic);
        accountsLogic.initLogicDependencies(AccountsDb.inst(), usersLogic);
        coursesLogic.initLogicDependencies(CoursesDb.inst(), usersLogic, institutesLogic);
        dataBundleLogic.initLogicDependencies(accountsLogic, notificationsLogic, institutesLogic);
        deadlineExtensionsLogic.initLogicDependencies(DeadlineExtensionsDb.inst(), fsLogic, usersLogic);
        fsLogic.initLogicDependencies(FeedbackSessionsDb.inst(), frLogic, fqLogic, usersLogic, coursesLogic);
        fslLogic.initLogicDependencies(FeedbackSessionLogsDb.inst());
        frLogic.initLogicDependencies(FeedbackResponsesDb.inst(), usersLogic, fqLogic, frcLogic,
                instructorPermissionsLogic);
        frcLogic.initLogicDependencies(ResponseInstructorCommentsDb.inst(), frLogic);
        fqLogic.initLogicDependencies(FeedbackQuestionsDb.inst(), coursesLogic, frLogic, usersLogic, fsLogic,
                instructorPermissionsLogic);
        notificationsLogic.initLogicDependencies(NotificationsDb.inst(), accountsLogic);
        usageStatisticsLogic.initLogicDependencies(frLogic, coursesLogic, usersLogic, accountVerificationsLogic);
        usersLogic.initLogicDependencies(UsersDb.inst(), coursesLogic, frLogic, instructorPermissionsLogic);
        instructorPermissionsLogic.initLogicDependencies(InstructorPermissionsDb.inst());
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
