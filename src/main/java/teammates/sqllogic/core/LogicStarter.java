package teammates.sqllogic.core;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import teammates.common.util.Logger;
import teammates.storage.sqlapi.AccountRequestsDb;
import teammates.storage.sqlapi.AccountsDb;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlapi.DeadlineExtensionsDb;
import teammates.storage.sqlapi.FeedbackQuestionsDb;
import teammates.storage.sqlapi.FeedbackResponseCommentsDb;
import teammates.storage.sqlapi.FeedbackResponsesDb;
import teammates.storage.sqlapi.FeedbackSessionLogsDb;
import teammates.storage.sqlapi.FeedbackSessionsDb;
import teammates.storage.sqlapi.NotificationsDb;
import teammates.storage.sqlapi.UsageStatisticsDb;
import teammates.storage.sqlapi.UsersDb;

/**
 * Setup in web.xml to register logic classes at application startup.
 */
public class LogicStarter implements ServletContextListener {

    private static final Logger log = Logger.getLogger();

    /**
     * Registers dependencies between different logic classes.
     */
    public static void initializeDependencies() {
        AccountsLogic accountsLogic = AccountsLogic.inst();
        AccountRequestsLogic accountRequestsLogic = AccountRequestsLogic.inst();
        CoursesLogic coursesLogic = CoursesLogic.inst();
        DataBundleLogic dataBundleLogic = DataBundleLogic.inst();
        DeadlineExtensionsLogic deadlineExtensionsLogic = DeadlineExtensionsLogic.inst();
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        FeedbackSessionLogsLogic fslLogic = FeedbackSessionLogsLogic.inst();
        FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
        FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
        FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
        NotificationsLogic notificationsLogic = NotificationsLogic.inst();
        UsageStatisticsLogic usageStatisticsLogic = UsageStatisticsLogic.inst();
        UsersLogic usersLogic = UsersLogic.inst();

        accountRequestsLogic.initLogicDependencies(AccountRequestsDb.inst());
        accountsLogic.initLogicDependencies(AccountsDb.inst(), notificationsLogic, usersLogic, coursesLogic);
        coursesLogic.initLogicDependencies(CoursesDb.inst(), fsLogic, usersLogic);
        dataBundleLogic.initLogicDependencies(accountsLogic, accountRequestsLogic, coursesLogic,
                deadlineExtensionsLogic, fsLogic, fslLogic, fqLogic, frLogic, frcLogic,
                notificationsLogic, usersLogic);
        deadlineExtensionsLogic.initLogicDependencies(DeadlineExtensionsDb.inst(), fsLogic);
        fsLogic.initLogicDependencies(FeedbackSessionsDb.inst(), coursesLogic, frLogic, fqLogic, usersLogic);
        fslLogic.initLogicDependencies(FeedbackSessionLogsDb.inst());
        frLogic.initLogicDependencies(FeedbackResponsesDb.inst(), usersLogic, fqLogic, frcLogic);
        frcLogic.initLogicDependencies(FeedbackResponseCommentsDb.inst());
        fqLogic.initLogicDependencies(FeedbackQuestionsDb.inst(), coursesLogic, frLogic, usersLogic, fsLogic);
        notificationsLogic.initLogicDependencies(NotificationsDb.inst());
        usageStatisticsLogic.initLogicDependencies(UsageStatisticsDb.inst());
        usersLogic.initLogicDependencies(UsersDb.inst(), accountsLogic, frLogic, frcLogic, deadlineExtensionsLogic);
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
