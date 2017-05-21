package teammates.storage.api;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.googlecode.objectify.ObjectifyService;

import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.StudentProfile;

/**
 * Setup in web.xml to register Objectify at application startup.
 **/
public class OfyHelper implements ServletContextListener {

    /**
     * Register entity classes in Objectify service.
     */
    public static void registerEntityClasses() {
        ObjectifyService.register(Account.class);
        ObjectifyService.register(Course.class);
        ObjectifyService.register(CourseStudent.class);
        ObjectifyService.register(Instructor.class);
        ObjectifyService.register(StudentProfile.class);
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        registerEntityClasses();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
     // App Engine does not currently invoke this method.
    }
}
