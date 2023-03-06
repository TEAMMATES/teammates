package teammates.sqllogic.core;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import teammates.common.util.Logger;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlapi.DeadlineExtensionsDb;
import teammates.storage.sqlapi.FeedbackQuestionsDb;
import teammates.storage.sqlapi.FeedbackResponsesDb;
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
        CoursesLogic coursesLogic = CoursesLogic.inst();
        DeadlineExtensionsLogic deLogic = DeadlineExtensionsLogic.inst();
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
        FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
        NotificationsLogic notificationsLogic = NotificationsLogic.inst();
        UsageStatisticsLogic usageStatisticsLogic = UsageStatisticsLogic.inst();
        UsersLogic usersLogic = UsersLogic.inst();

        coursesLogic.initLogicDependencies(CoursesDb.inst(), fsLogic);
        deLogic.initLogicDependencies(DeadlineExtensionsDb.inst());
        fsLogic.initLogicDependencies(FeedbackSessionsDb.inst(), coursesLogic, frLogic, fqLogic);
        frLogic.initLogicDependencies(FeedbackResponsesDb.inst());
        fqLogic.initLogicDependencies(FeedbackQuestionsDb.inst());
        notificationsLogic.initLogicDependencies(NotificationsDb.inst());
        usageStatisticsLogic.initLogicDependencies(UsageStatisticsDb.inst());
        usersLogic.initLogicDependencies(UsersDb.inst());
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
