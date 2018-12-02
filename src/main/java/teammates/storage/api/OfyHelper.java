package teammates.storage.api;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.googlecode.objectify.ObjectifyService;

import teammates.storage.entity.Account;
import teammates.storage.entity.BaseEntity;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
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
        ObjectifyService.register(FeedbackQuestion.class);
        ObjectifyService.register(FeedbackResponse.class);
        ObjectifyService.register(FeedbackResponseComment.class);
        ObjectifyService.register(FeedbackSession.class);
        ObjectifyService.register(Instructor.class);
        ObjectifyService.register(StudentProfile.class);
        // enable the ability to use java.time.Instant to issue query
        ObjectifyService.factory().getTranslators().add(new BaseEntity.InstantTranslatorFactory());
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Invoked by GAE at application startup.
        registerEntityClasses();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // GAE does not currently invoke this method.
    }
}
