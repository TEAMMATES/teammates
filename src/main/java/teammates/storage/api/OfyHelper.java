package teammates.storage.api;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.googlecode.objectify.ObjectifyService;

import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.StudentProfile;

/**
 * Setup in web.xml to register Objectify at application startup.
 **/
public class OfyHelper implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ObjectifyService.register(Account.class);
        ObjectifyService.register(Course.class);
        ObjectifyService.register(StudentProfile.class);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
     // App Engine does not currently invoke this method.
    }
}
