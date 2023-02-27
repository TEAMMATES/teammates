package teammates.sqllogic.core;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import teammates.common.util.Logger;
import teammates.storage.sqlapi.AccountRequestDb;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlapi.FeedbackSessionsDb;
import teammates.storage.sqlapi.NotificationsDb;
import teammates.storage.sqlapi.UsageStatisticsDb;

/**
 * Setup in web.xml to register logic classes at application startup.
 */
public class LogicStarter implements ServletContextListener {

    private static final Logger log = Logger.getLogger();

    /**
     * Registers dependencies between different logic classes.
     */
    public static void initializeDependencies() {
        AccountRequestLogic accountRequestLogic = AccountRequestLogic.inst();
        CoursesLogic coursesLogic = CoursesLogic.inst();
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        NotificationsLogic notificationsLogic = NotificationsLogic.inst();
        UsageStatisticsLogic usageStatisticsLogic = UsageStatisticsLogic.inst();

        accountRequestLogic.initLogicDependencies(AccountRequestDb.inst());
        coursesLogic.initLogicDependencies(CoursesDb.inst(), fsLogic);
        fsLogic.initLogicDependencies(FeedbackSessionsDb.inst(), coursesLogic);
        notificationsLogic.initLogicDependencies(NotificationsDb.inst());
        usageStatisticsLogic.initLogicDependencies(UsageStatisticsDb.inst());
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
