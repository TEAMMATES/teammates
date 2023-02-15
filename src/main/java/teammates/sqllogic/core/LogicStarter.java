package teammates.sqllogic.core;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import teammates.common.util.Logger;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlapi.FeedbackSessionsDb;
import teammates.storage.sqlapi.NotificationsDb;

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
        FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
        NotificationsLogic notificationsLogic = NotificationsLogic.inst();

        coursesLogic.initLogicDependencies(CoursesDb.inst(), fsLogic);
        fsLogic.initLogicDependencies(FeedbackSessionsDb.inst(), coursesLogic);
        notificationsLogic.initLogicDependencies(NotificationsDb.inst());

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
