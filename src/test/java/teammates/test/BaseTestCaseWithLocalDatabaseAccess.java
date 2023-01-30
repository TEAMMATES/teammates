package teammates.test;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.testing.LocalDatastoreHelper;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.logic.api.LogicExtension;
import teammates.logic.core.LogicStarter;
import teammates.storage.api.OfyHelper;
import teammates.storage.search.AccountRequestSearchManager;
import teammates.storage.search.InstructorSearchManager;
import teammates.storage.search.SearchManagerFactory;
import teammates.storage.search.StudentSearchManager;

/**
 * Base class for all tests which require access to a locally run database.
 *
 * <p>As all tests are run against a single DB instance and we use shared test data for most tests,
 * the tests need to be run in a single thread to prevent test data contamination across different tests.
 */
@Test(singleThreaded = true)
public abstract class BaseTestCaseWithLocalDatabaseAccess extends BaseTestCaseWithDatabaseAccess {
    private static final LocalDatastoreHelper LOCAL_DATASTORE_HELPER = LocalDatastoreHelper.newBuilder()
            .setConsistency(1.0)
            .setPort(TestProperties.TEST_LOCALDATASTORE_PORT)
            .setStoreOnDisk(false)
            .build();
    private final LogicExtension logic = new LogicExtension();
    private Closeable closeable;

    @BeforeSuite
    public void setupDbLayer() throws Exception {
        LOCAL_DATASTORE_HELPER.start();
        DatastoreOptions options = LOCAL_DATASTORE_HELPER.getOptions();
        ObjectifyService.init(new ObjectifyFactory(
                options.getService()
        ));
        OfyHelper.registerEntityClasses();

        SearchManagerFactory.registerAccountRequestSearchManager(
                new AccountRequestSearchManager(TestProperties.SEARCH_SERVICE_HOST, true));
        SearchManagerFactory.registerInstructorSearchManager(
                new InstructorSearchManager(TestProperties.SEARCH_SERVICE_HOST, true));
        SearchManagerFactory.registerStudentSearchManager(
                new StudentSearchManager(TestProperties.SEARCH_SERVICE_HOST, true));

        LogicStarter.initializeDependencies();
    }

    @BeforeClass
    public void setupObjectify() {
        closeable = ObjectifyService.begin();
    }

    @AfterClass
    public void tearDownObjectify() {
        closeable.close();
    }

    @AfterClass
    public void resetDbLayer() throws Exception {
        SearchManagerFactory.getAccountRequestSearchManager().resetCollections();
        SearchManagerFactory.getInstructorSearchManager().resetCollections();
        SearchManagerFactory.getStudentSearchManager().resetCollections();

        LOCAL_DATASTORE_HELPER.reset();
    }

    @AfterSuite
    public void tearDownLocalDatastoreHelper() throws Exception {
        LOCAL_DATASTORE_HELPER.stop();
    }

    @Override
    protected AccountAttributes getAccount(AccountAttributes account) {
        return logic.getAccount(account.getGoogleId());
    }

    @Override
    protected CourseAttributes getCourse(CourseAttributes course) {
        return logic.getCourse(course.getId());
    }

    @Override
    protected FeedbackQuestionAttributes getFeedbackQuestion(FeedbackQuestionAttributes fq) {
        return logic.getFeedbackQuestion(fq.getFeedbackSessionName(), fq.getCourseId(), fq.getQuestionNumber());
    }

    @Override
    protected FeedbackResponseCommentAttributes getFeedbackResponseComment(FeedbackResponseCommentAttributes frc) {
        return logic.getFeedbackResponseComment(frc.getFeedbackResponseId(), frc.getCommentGiver(), frc.getCreatedAt());
    }

    @Override
    protected FeedbackResponseAttributes getFeedbackResponse(FeedbackResponseAttributes fr) {
        return logic.getFeedbackResponse(fr.getFeedbackQuestionId(), fr.getGiver(), fr.getRecipient());
    }

    @Override
    protected FeedbackSessionAttributes getFeedbackSession(FeedbackSessionAttributes fs) {
        return logic.getFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId());
    }

    @Override
    protected InstructorAttributes getInstructor(InstructorAttributes instructor) {
        return instructor.getGoogleId() == null
                ? logic.getInstructorForEmail(instructor.getCourseId(), instructor.getEmail())
                : logic.getInstructorForGoogleId(instructor.getCourseId(), instructor.getGoogleId());
    }

    @Override
    protected StudentAttributes getStudent(StudentAttributes student) {
        return logic.getStudentForEmail(student.getCourse(), student.getEmail());
    }

    @Override
    protected AccountRequestAttributes getAccountRequest(AccountRequestAttributes accountRequest) {
        return logic.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
    }

    @Override
    protected DeadlineExtensionAttributes getDeadlineExtension(DeadlineExtensionAttributes deadlineExtension) {
        return logic.getDeadlineExtension(
                deadlineExtension.getCourseId(), deadlineExtension.getFeedbackSessionName(),
                deadlineExtension.getUserEmail(), deadlineExtension.getIsInstructor());
    }

    @Override
    protected NotificationAttributes getNotification(NotificationAttributes notification) {
        return logic.getNotification(notification.getNotificationId());
    }

    protected void removeAndRestoreTypicalDataBundle() {
        DataBundle dataBundle = getTypicalDataBundle();
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    protected boolean doRemoveAndRestoreDataBundle(DataBundle dataBundle) {
        try {
            logic.removeDataBundle(dataBundle);
            logic.persistDataBundle(dataBundle);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected boolean doPutDocuments(DataBundle dataBundle) {
        try {
            logic.putDocuments(dataBundle);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
