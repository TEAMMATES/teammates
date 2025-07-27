package teammates.e2e.cases.sql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.HttpRequestFailedException;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.Browser;
import teammates.e2e.pageobjects.DevServerLoginPage;
import teammates.e2e.pageobjects.HomePage;
import teammates.e2e.util.BackDoor;
import teammates.e2e.util.EmailAccount;
import teammates.e2e.util.TestProperties;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.Student;
import teammates.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.test.FileHelper;
import teammates.test.ThreadHelper;
import teammates.ui.output.AccountData;
import teammates.ui.output.CourseData;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.output.FeedbackResponseData;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionPublishStatus;
import teammates.ui.output.InstructorData;
import teammates.ui.output.NotificationData;
import teammates.ui.output.StudentData;

/**
 * Base class for all browser tests.
 *
 * <p>This type of test has no knowledge of the workings of the application,
 * and can only communicate via the UI or via {@link BackDoor} to obtain/transmit data.
 */
public abstract class BaseE2ETestCase extends BaseTestCaseWithSqlDatabaseAccess {

    /**
     * Backdoor used to call APIs.
     */
    protected static final BackDoor BACKDOOR = BackDoor.getInstance();

    /**
     * DataBundle used in tests.
     */
    protected SqlDataBundle testData;

    private Browser browser;

    @BeforeClass
    public void baseClassSetup() {
        prepareTestData();
        prepareBrowser();
    }

    /**
     * Prepares the browser used for the current test.
     */
    protected void prepareBrowser() {
        browser = new Browser();
    }

    /**
     * Prepares the test data used for the current test.
     */
    protected abstract void prepareTestData();

    /**
     * Contains all the tests for the page.
     *
     * <p>This approach is chosen so that setup and teardown are only needed once per test page,
     * thereby saving time. While it necessitates failed tests to be restarted from the beginning,
     * test failures are rare and thus not causing significant overhead.
     */
    protected abstract void testAll();

    @Override
    protected String getTestDataFolder() {
        return TestProperties.TEST_DATA_FOLDER;
    }

    @AfterClass
    public void baseClassTearDown(ITestContext context) {
        if (browser == null) {
            return;
        }
        boolean isSuccess = context.getFailedTests().getAllMethods()
                .stream()
                .noneMatch(method -> method.getConstructorOrMethod().getMethod().getDeclaringClass() == this.getClass());
        if (isSuccess || TestProperties.CLOSE_BROWSER_ON_FAILURE) {
            browser.close();
        }
    }

    /**
     * Creates an {@link AppUrl} for the supplied {@code relativeUrl} parameter.
     * The base URL will be the value of test.app.frontend.url in test.properties.
     * {@code relativeUrl} must start with a "/".
     */
    protected static AppUrl createFrontendUrl(String relativeUrl) {
        return new AppUrl(TestProperties.TEAMMATES_FRONTEND_URL + relativeUrl);
    }

    /**
     * Creates an {@link AppUrl} for the supplied {@code relativeUrl} parameter.
     * The base URL will be the value of test.app.backend.url in test.properties.
     * {@code relativeUrl} must start with a "/".
     */
    protected static AppUrl createBackendUrl(String relativeUrl) {
        return new AppUrl(TestProperties.TEAMMATES_BACKEND_URL + relativeUrl);
    }

    /**
     * Logs in to a page using the given credentials.
     */
    protected <T extends AppPage> T loginToPage(AppUrl url, Class<T> typeOfPage, String userId) {
        // When not using dev server, Google blocks log in by automation.
        // To work around that, we inject the user cookie directly into the browser session.
        if (!TestProperties.isDevServer()) {
            // In order for the cookie injection to work, we need to be in the domain.
            // Use the home page to minimize the page load time.
            browser.goToUrl(TestProperties.TEAMMATES_FRONTEND_URL);

            String cookieValue = BACKDOOR.getUserCookie(userId);
            browser.addCookie(Const.SecurityConfig.AUTH_COOKIE_NAME, cookieValue, true, true);

            return getNewPageInstance(url, typeOfPage);
        }

        // This will be redirected to the dev server login page.
        browser.goToUrl(url.toAbsoluteString());

        DevServerLoginPage loginPage = AppPage.getNewPageInstance(browser, DevServerLoginPage.class);
        loginPage.loginAsUser(userId);

        return getNewPageInstance(url, typeOfPage);
    }

    /**
     * Logs in to a page using admin credentials.
     */
    protected <T extends AppPage> T loginAdminToPage(AppUrl url, Class<T> typeOfPage) {
        return loginToPage(url, typeOfPage, TestProperties.TEST_ADMIN);
    }

    /**
     * Equivalent to clicking the 'logout' link in the top menu of the page.
     */
    protected void logout() {
        AppUrl url = createBackendUrl(Const.WebPageURIs.LOGOUT);
        if (!TestProperties.TEAMMATES_FRONTEND_URL.equals(TestProperties.TEAMMATES_BACKEND_URL)) {
            url = url.withParam("frontendUrl", TestProperties.TEAMMATES_FRONTEND_URL);
        }

        browser.goToUrl(url.toAbsoluteString());
        AppPage.getNewPageInstance(browser, HomePage.class).waitForPageToLoad();
    }

    /**
     * Deletes file with fileName from the downloads folder.
     */
    protected void deleteDownloadsFile(String fileName) {
        String filePath = TestProperties.TEST_DOWNLOADS_FOLDER + fileName;
        FileHelper.deleteFile(filePath);
    }

    /**
     * Verifies downloaded file has correct fileName and contains expected content.
     */
    protected void verifyDownloadedFile(String expectedFileName, List<String> expectedContent) {
        String filePath = TestProperties.TEST_DOWNLOADS_FOLDER + expectedFileName;
        int retryLimit = TestProperties.TEST_TIMEOUT;
        boolean actual = Files.exists(Paths.get(filePath));
        while (!actual && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(1000);
            actual = Files.exists(Paths.get(filePath));
        }
        assertTrue(actual);

        try {
            String actualContent = FileHelper.readFile(filePath);
            for (String content : expectedContent) {
                assertTrue(actualContent.contains(content));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Visits the URL and gets the page object representation of the visited web page in the browser.
     */
    protected <T extends AppPage> T getNewPageInstance(AppUrl url, Class<T> typeOfPage) {
        browser.goToUrl(url.toAbsoluteString());
        return AppPage.getNewPageInstance(browser, typeOfPage);
    }

    /**
     * Verifies that email with subject is found in inbox.
     * Email used must be an authentic gmail account.
     */
    protected void verifyEmailSent(String email, String subject) {
        if (TestProperties.isDevServer() || !TestProperties.INCLUDE_EMAIL_VERIFICATION) {
            return;
        }
        if (!TestProperties.TEST_EMAIL.equals(email)) {
            fail("Email verification is allowed only on preset test email.");
        }
        EmailAccount emailAccount = new EmailAccount(email);
        try {
            emailAccount.getUserAuthenticated();
            int retryLimit = 5;
            boolean actual = emailAccount.isRecentEmailWithSubjectPresent(subject, TestProperties.TEST_SENDER_EMAIL);
            while (!actual && retryLimit > 0) {
                retryLimit--;
                ThreadHelper.waitFor(1000);
                actual = emailAccount.isRecentEmailWithSubjectPresent(subject, TestProperties.TEST_SENDER_EMAIL);
            }
            assertTrue(actual);
        } catch (Exception e) {
            fail("Failed to verify email sent:" + e);
        }
    }

    /**
     * Removes and restores the databundle using BACKDOOR.
     */
    @Override
    protected SqlDataBundle doRemoveAndRestoreDataBundle(SqlDataBundle testData) {
        try {
            return BACKDOOR.removeAndRestoreSqlDataBundle(testData);
        } catch (HttpRequestFailedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Puts the documents in the database using BACKDOOR.
     * @param dataBundle the data to be put in the database
     * @return the result of the operation
     */
    protected String putDocuments(SqlDataBundle dataBundle) {
        try {
            return BACKDOOR.putSqlDocuments(dataBundle);
        } catch (HttpRequestFailedException e) {
            e.printStackTrace();
            return null;
        }
    }

    AccountData getAccount(String googleId) {
        return BACKDOOR.getAccountData(googleId);
    }

    @Override
    protected AccountData getAccount(Account account) {
        return getAccount(account.getGoogleId());
    }

    CourseData getCourse(String courseId) {
        return BACKDOOR.getCourseData(courseId);
    }

    @Override
    protected CourseData getCourse(Course course) {
        return getCourse(course.getId());
    }

    FeedbackQuestionData getFeedbackQuestion(String courseId, String feedbackSessionName, int qnNumber) {
        return BACKDOOR.getFeedbackQuestionData(courseId, feedbackSessionName, qnNumber);
    }

    @Override
    protected FeedbackQuestionData getFeedbackQuestion(FeedbackQuestion fq) {
        return getFeedbackQuestion(fq.getCourseId(), fq.getFeedbackSession().getName(), fq.getQuestionNumber());
    }

    FeedbackResponseData getFeedbackResponse(String questionId, String giver, String recipient) {
        return BACKDOOR.getFeedbackResponseData(questionId, giver, recipient);
    }

    @Override
    protected FeedbackResponseData getFeedbackResponse(FeedbackResponse fr) {
        return getFeedbackResponse(fr.getFeedbackQuestion().getId().toString(), fr.getGiver(), fr.getRecipient());
    }

    FeedbackSessionData getFeedbackSession(String courseId, String feedbackSessionName) {
        return BACKDOOR.getFeedbackSessionData(courseId, feedbackSessionName);
    }

    @Override
    protected FeedbackSessionData getFeedbackSession(FeedbackSession feedbackSession) {
        return getFeedbackSession(feedbackSession.getCourse().getId(), feedbackSession.getName());
    }

    /**
     * Checks if the feedback session is published.
     */
    protected boolean isFeedbackSessionPublished(FeedbackSessionPublishStatus status) {
        return status == FeedbackSessionPublishStatus.PUBLISHED;
    }

    FeedbackSessionData getSoftDeletedSession(String feedbackSessionName, String instructorId) {
        return BACKDOOR.getSoftDeletedSessionData(feedbackSessionName, instructorId);
    }

    InstructorData getInstructor(String courseId, String instructorEmail) {
        return BACKDOOR.getInstructorData(courseId, instructorEmail);
    }

    @Override
    protected InstructorData getInstructor(Instructor instructor) {
        return getInstructor(instructor.getCourseId(), instructor.getEmail());
    }

    /**
     * Gets registration key for a given instructor.
     */
    protected String getKeyForInstructor(String courseId, String instructorEmail) {
        return getInstructor(courseId, instructorEmail).getKey();
    }

    NotificationData getNotification(String notificationId) {
        return BACKDOOR.getNotificationData(notificationId);
    }

    @Override
    protected NotificationData getNotification(Notification notification) {
        return getNotification(notification.getId().toString());
    }

    /**
     * Deletes the notification with the given ID.
     *
     * @param notificationId the ID of the notification to delete
     */
    protected void deleteNotification(UUID notificationId) {
        BACKDOOR.deleteNotification(notificationId);
    }

    /**
     * Deletes the notification with the given ID.
     *
     * @param notificationId the ID of the notification to delete
     */
    protected void deleteNotification(String notificationId) {
        BACKDOOR.deleteNotification(notificationId);
    }

    StudentData getStudent(String courseId, String studentEmailAddress) {
        return BACKDOOR.getStudentData(courseId, studentEmailAddress);
    }

    @Override
    protected StudentData getStudent(Student student) {
        return getStudent(student.getCourseId(), student.getEmail());
    }

    /**
     * Gets registration key for a given student.
     */
    protected String getKeyForStudent(Student student) {
        return getStudent(student).getKey();
    }
}
