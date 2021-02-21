package teammates.storage.api;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import teammates.common.util.Logger;
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
    private static final Logger log = Logger.getLogger();

    private static final double DB_CONSISTENCY = 1.0;

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

        // PRODUCTION
        //ObjectifyService.init();

        // LOCAL TEST
        LocalDatastoreHelper localDatastoreHelper = LocalDatastoreHelper.create(DB_CONSISTENCY);
        try {
            localDatastoreHelper.start();
        } catch (InterruptedException | IOException e) {
            log.warning("local datastore helper exception");
        }

        DatastoreOptions options = localDatastoreHelper.getOptions();
        ObjectifyService.init(new ObjectifyFactory(
                options.getService()
        ));

        // MANUAL TEST
        /*
        ObjectifyService.init(new ObjectifyFactory(
                DatastoreOptions.newBuilder()
                        .setHost("http://localhost:8080")
                        .setProjectId("tm-obj-v6-test")
                        .build()
                        .getService()
        ));
        */

        registerEntityClasses();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // GAE does not currently invoke this method.
    }
}
