package teammates.e2e.cases;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

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
import teammates.test.BaseTestCaseWithDatabaseAccess;
import teammates.test.FileHelper;
import teammates.test.ThreadHelper;

/**
 * Base class for all browser tests.
 *
 * <p>This type of test has no knowledge of the workings of the application,
 * and can only communicate via the UI or via {@link BackDoor} to obtain/transmit data.
 */
public abstract class BaseE2ETestCase extends BaseTestCaseWithDatabaseAccess {

    static final BackDoor BACKDOOR = BackDoor.getInstance();

    DataBundle testData;
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

    AccountAttributes getAccount(String googleId) {
        return BACKDOOR.getAccount(googleId);
    }

    @Override
    protected AccountAttributes getAccount(AccountAttributes account) {
        return getAccount(account.getGoogleId());
    }

    CourseAttributes getCourse(String courseId) {
        return BACKDOOR.getCourse(courseId);
    }

    @Override
    protected CourseAttributes getCourse(CourseAttributes course) {
        return getCourse(course.getId());
    }

    CourseAttributes getArchivedCourse(String instructorId, String courseId) {
        return BACKDOOR.getArchivedCourse(instructorId, courseId);
    }

    FeedbackQuestionAttributes getFeedbackQuestion(String courseId, String feedbackSessionName, int qnNumber) {
        return BACKDOOR.getFeedbackQuestion(courseId, feedbackSessionName, qnNumber);
    }

    @Override
    protected FeedbackQuestionAttributes getFeedbackQuestion(FeedbackQuestionAttributes fq) {
        return getFeedbackQuestion(fq.getCourseId(), fq.getFeedbackSessionName(), fq.getQuestionNumber());
    }

    FeedbackResponseCommentAttributes getFeedbackResponseComment(String feedbackResponseId) {
        return BACKDOOR.getFeedbackResponseComment(feedbackResponseId);
    }

    @Override
    protected FeedbackResponseCommentAttributes getFeedbackResponseComment(FeedbackResponseCommentAttributes frc) {
        return getFeedbackResponseComment(frc.getFeedbackResponseId());
    }

    FeedbackResponseAttributes getFeedbackResponse(String feedbackQuestionId, String giver, String recipient) {
        return BACKDOOR.getFeedbackResponse(feedbackQuestionId, giver, recipient);
    }

    @Override
    protected FeedbackResponseAttributes getFeedbackResponse(FeedbackResponseAttributes fr) {
        return getFeedbackResponse(fr.getFeedbackQuestionId(), fr.getGiver(), fr.getRecipient());
    }

    FeedbackSessionAttributes getFeedbackSession(String courseId, String feedbackSessionName) {
        return BACKDOOR.getFeedbackSession(courseId, feedbackSessionName);
    }

    @Override
    protected FeedbackSessionAttributes getFeedbackSession(FeedbackSessionAttributes fs) {
        return getFeedbackSession(fs.getCourseId(), fs.getFeedbackSessionName());
    }

    FeedbackSessionAttributes getSoftDeletedSession(String feedbackSessionName, String instructorId) {
        return BACKDOOR.getSoftDeletedSession(feedbackSessionName, instructorId);
    }

    InstructorAttributes getInstructor(String courseId, String instructorEmail) {
        return BACKDOOR.getInstructor(courseId, instructorEmail);
    }

    @Override
    protected InstructorAttributes getInstructor(InstructorAttributes instructor) {
        return getInstructor(instructor.getCourseId(), instructor.getEmail());
    }

    String getKeyForInstructor(String courseId, String instructorEmail) {
        return getInstructor(courseId, instructorEmail).getKey();
    }

    @Override
    protected StudentAttributes getStudent(StudentAttributes student) {
        return BACKDOOR.getStudent(student.getCourse(), student.getEmail());
    }

    String getKeyForStudent(StudentAttributes student) {
        return getStudent(student).getKey();
    }

    @Override
    protected AccountRequestAttributes getAccountRequest(AccountRequestAttributes accountRequest) {
        return BACKDOOR.getAccountRequest(accountRequest.getEmail(), accountRequest.getInstitute());
    }

    NotificationAttributes getNotification(String notificationId) {
        return BACKDOOR.getNotification(notificationId);
    }

    @Override
    protected NotificationAttributes getNotification(NotificationAttributes notification) {
        return getNotification(notification.getNotificationId());
    }

    @Override
    protected DeadlineExtensionAttributes getDeadlineExtension(DeadlineExtensionAttributes deadlineExtension) {
        return BACKDOOR.getDeadlineExtension(
                deadlineExtension.getCourseId(), deadlineExtension.getFeedbackSessionName(),
                deadlineExtension.getUserEmail(), deadlineExtension.getIsInstructor());
    }

    @Override
    protected boolean doRemoveAndRestoreDataBundle(DataBundle testData) {
        try {
            BACKDOOR.removeAndRestoreDataBundle(testData);
            return true;
        } catch (HttpRequestFailedException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected boolean doPutDocuments(DataBundle testData) {
        try {
            BACKDOOR.putDocuments(testData);
            return true;
        } catch (HttpRequestFailedException e) {
            e.printStackTrace();
            return false;
        }
    }

}
