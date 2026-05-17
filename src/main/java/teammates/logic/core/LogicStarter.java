package teammates.logic.core;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import teammates.common.util.Logger;
import teammates.storage.api.AccountRequestsDb;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.DeadlineExtensionsDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionLogsDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.NotificationsDb;
import teammates.storage.api.UsageStatisticsDb;
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
        accountsLogic.initLogicDependencies(AccountsDb.inst(), usersLogic, coursesLogic);
        coursesLogic.initLogicDependencies(CoursesDb.inst(), usersLogic, accountsLogic);
        dataBundleLogic.initLogicDependencies(accountsLogic, accountRequestsLogic, coursesLogic, notificationsLogic);
        deadlineExtensionsLogic.initLogicDependencies(DeadlineExtensionsDb.inst(), fsLogic, usersLogic);
        fsLogic.initLogicDependencies(FeedbackSessionsDb.inst(), frLogic, fqLogic, usersLogic);
        fslLogic.initLogicDependencies(FeedbackSessionLogsDb.inst());
        frLogic.initLogicDependencies(FeedbackResponsesDb.inst(), usersLogic, fqLogic, frcLogic);
        frcLogic.initLogicDependencies(FeedbackResponseCommentsDb.inst());
        fqLogic.initLogicDependencies(FeedbackQuestionsDb.inst(), coursesLogic, frLogic, usersLogic, fsLogic);
        notificationsLogic.initLogicDependencies(NotificationsDb.inst(), accountsLogic);
        usageStatisticsLogic.initLogicDependencies(UsageStatisticsDb.inst());
        usersLogic.initLogicDependencies(UsersDb.inst(), accountsLogic, coursesLogic, frLogic);
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
