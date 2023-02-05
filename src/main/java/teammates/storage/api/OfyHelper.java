package teammates.storage.api;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.google.cloud.datastore.DatastoreOptions;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import teammates.common.util.Config;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.BaseEntity;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Notification;
import teammates.storage.entity.UsageStatistics;

/**
 * Setup in web.xml to register Objectify at application startup.
 **/
public class OfyHelper implements ServletContextListener {

    private static void initializeDatastore() {
        DatastoreOptions.Builder builder = DatastoreOptions.newBuilder().setProjectId(Config.APP_ID);
        if (Config.IS_DEV_SERVER) {
            builder.setHost("http://localhost:" + Config.APP_LOCALDATASTORE_PORT);
        }
        ObjectifyService.init(new ObjectifyFactory(builder.build().getService()));
    }

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
        ObjectifyService.register(AccountRequest.class);
        ObjectifyService.register(UsageStatistics.class);
        ObjectifyService.register(DeadlineExtension.class);
        ObjectifyService.register(Notification.class);
        // enable the ability to use java.time.Instant to issue query
        ObjectifyService.factory().getTranslators().add(new BaseEntity.InstantTranslatorFactory());
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // Invoked by Jetty at application startup.
        initializeDatastore();
        registerEntityClasses();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // Nothing to do
    }
}
