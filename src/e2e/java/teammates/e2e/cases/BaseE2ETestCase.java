package teammates.e2e.cases;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.exception.HttpRequestFailedException;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.Browser;
import teammates.e2e.pageobjects.HomePage;
import teammates.e2e.util.BackDoor;
import teammates.e2e.util.TestProperties;
import teammates.logic.entity.Account;
import teammates.logic.entity.BaseEntity;
import teammates.logic.entity.Course;
import teammates.logic.entity.FeedbackQuestion;
import teammates.logic.entity.FeedbackResponse;
import teammates.logic.entity.FeedbackResponseComment;
import teammates.logic.entity.FeedbackSession;
import teammates.logic.entity.Instructor;
import teammates.logic.entity.Notification;
import teammates.logic.entity.Student;
import teammates.logic.entity.UsageStatistics;
import teammates.test.BaseTestCase;
import teammates.test.FileHelper;
import teammates.test.ThreadHelper;
import teammates.ui.output.AccountData;
import teammates.ui.output.ApiOutput;
import teammates.ui.output.CourseData;
import teammates.ui.output.DeadlineExtensionsData;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.output.FeedbackResponseData;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionPublishStatus;
import teammates.ui.output.InstructorData;
import teammates.ui.output.NotificationData;
import teammates.ui.output.NumberOfEntitiesToGiveFeedbackToSetting;
import teammates.ui.output.StudentData;
import teammates.ui.output.UsageStatisticsData;

/**
 * Base class for all browser tests.
 *
 * <p>This type of test has no knowledge of the workings of the application,
 * and can only communicate via the UI or via {@link BackDoor} to obtain/transmit data.
 */
public abstract class BaseE2ETestCase extends BaseTestCase {
    /**
     * Backdoor used to call APIs.
     */
    protected static final BackDoor BACKDOOR = BackDoor.getInstance();

    /**
     * DataBundle used in tests.
     */
    protected DataBundle testData;

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
     * Removes and restores the databundle.
     */
    protected DataBundle removeAndRestoreDataBundle(DataBundle testData) {
        DataBundle dataBundle = null;
        try {
            dataBundle = BACKDOOR.removeAndRestoreDataBundle(testData);
        } catch (HttpRequestFailedException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(dataBundle);
        return dataBundle;
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
        // In order for the cookie injection to work, we need to be in the domain.
        // Use the home page to minimize the page load time.
        browser.goToUrl(TestProperties.TEAMMATES_FRONTEND_URL);

        String cookieValue = BACKDOOR.getUserCookie(userId);
        browser.addCookie(Const.SecurityConfig.AUTH_COOKIE_NAME, cookieValue, true, true);

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
     * Verifies that two entities are equal.
     */
    protected void verifyEquals(BaseEntity expected, ApiOutput actual) {
        if (expected instanceof FeedbackQuestion) {
            FeedbackQuestion expectedQuestion = (FeedbackQuestion) expected;
            FeedbackQuestionDetails expectedQuestionDetails = expectedQuestion.getQuestionDetailsCopy();
            FeedbackQuestionData actualQuestion = (FeedbackQuestionData) actual;
            FeedbackQuestionDetails actualQuestionDetails = actualQuestion.getQuestionDetails();
            assertEquals(expectedQuestion.getQuestionNumber(), (Integer) actualQuestion.getQuestionNumber());
            assertEquals(expectedQuestion.getDescription(), actualQuestion.getQuestionDescription());
            assertEquals(expectedQuestion.getGiverType(), actualQuestion.getGiverType());
            assertEquals(expectedQuestion.getRecipientType(), actualQuestion.getRecipientType());
            if (expectedQuestion.getNumOfEntitiesToGiveFeedbackTo() == Const.MAX_POSSIBLE_RECIPIENTS) {
                assertEquals(actualQuestion.getNumberOfEntitiesToGiveFeedbackToSetting(),
                        NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED);
                assertNull(actualQuestion.getCustomNumberOfEntitiesToGiveFeedbackTo());
            } else {
                assertEquals(actualQuestion.getNumberOfEntitiesToGiveFeedbackToSetting(),
                        NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM);
                assertEquals(expectedQuestion.getNumOfEntitiesToGiveFeedbackTo(),
                        actualQuestion.getCustomNumberOfEntitiesToGiveFeedbackTo());
            }
            assertEquals(expectedQuestionDetails.getJsonString(), actualQuestionDetails.getJsonString());
        } else if (expected instanceof FeedbackResponse) {
            FeedbackResponse expectedFeedbackResponse = (FeedbackResponse) expected;
            FeedbackResponseDetails expectedResponseDetails =
                    expectedFeedbackResponse.getFeedbackResponseDetailsCopy();
            FeedbackResponseData actualResponse = (FeedbackResponseData) actual;
            FeedbackResponseDetails actualResponseDetails = actualResponse.getResponseDetails();
            assertEquals(expectedFeedbackResponse.getGiver(), actualResponse.getGiverIdentifier());
            assertEquals(expectedFeedbackResponse.getRecipient(), actualResponse.getRecipientIdentifier());
            assertEquals(expectedResponseDetails.getAnswerString(),
                    actualResponse.getResponseDetails().getAnswerString());
            assertEquals(expectedResponseDetails.getQuestionType(),
                    actualResponse.getResponseDetails().getQuestionType());
            assertEquals(expectedResponseDetails.getJsonString(), actualResponseDetails.getJsonString());
        } else if (expected instanceof Account) {
            Account expectedAccount = (Account) expected;
            AccountData actualAccount = (AccountData) actual;
            assertEquals(expectedAccount.getGoogleId(), actualAccount.getGoogleId());
            assertEquals(expectedAccount.getName(), actualAccount.getName());
            assertEquals(expectedAccount.getEmail(), actualAccount.getEmail());
        } else if (expected instanceof Course) {
            Course expectedCourse = (Course) expected;
            CourseData actualCourse = (CourseData) actual;
            assertEquals(expectedCourse.getName(), actualCourse.getCourseName());
            assertEquals(expectedCourse.getTimeZone(), actualCourse.getTimeZone());
            assertEquals(expectedCourse.getInstitute(), actualCourse.getInstitute());
        } else if (expected instanceof FeedbackResponseComment) {
            FeedbackResponseComment expectedFeedbackResponseComment = (FeedbackResponseComment) expected;
            FeedbackResponseCommentData actualComment = (FeedbackResponseCommentData) actual;
            assertEquals(expectedFeedbackResponseComment.getGiver(), actualComment.getCommentGiver());
            assertEquals(expectedFeedbackResponseComment.getCommentText(), actualComment.getCommentText());
            assertEquals(expectedFeedbackResponseComment.getIsVisibilityFollowingFeedbackQuestion(),
                    actualComment.isVisibilityFollowingFeedbackQuestion());
            assertEquals(expectedFeedbackResponseComment.getLastEditorEmail(), actualComment.getLastEditorEmail());
        } else if (expected instanceof FeedbackSession) {
            FeedbackSession expectedFeedbackSession = (FeedbackSession) expected;
            FeedbackSessionData actualFeedbackSession = (FeedbackSessionData) actual;
            assertEquals(expectedFeedbackSession.getName(), actualFeedbackSession.getFeedbackSessionName());
            assertEquals(expectedFeedbackSession.getInstructions(), actualFeedbackSession.getInstructions());
            assertEquals(expectedFeedbackSession.getStartTime().toEpochMilli(),
                    actualFeedbackSession.getSubmissionStartTimestamp());
            assertEquals(expectedFeedbackSession.getEndTime().toEpochMilli(),
                    actualFeedbackSession.getSubmissionEndTimestamp());
            assertEquals(expectedFeedbackSession.getSessionVisibleFromTime().toEpochMilli(),
                    actualFeedbackSession.getSessionVisibleFromTimestamp().longValue());
            assertEquals(expectedFeedbackSession.getResultsVisibleFromTime().toEpochMilli(),
                    actualFeedbackSession.getResultVisibleFromTimestamp().longValue());
            assertEquals(expectedFeedbackSession.getGracePeriod().toMinutes(),
                    actualFeedbackSession.getGracePeriod().longValue());
            assertEquals(expectedFeedbackSession.isClosingSoonEmailEnabled(),
                    actualFeedbackSession.getIsClosingSoonEmailEnabled());
            assertEquals(expectedFeedbackSession.isPublishedEmailEnabled(),
                    actualFeedbackSession.getIsPublishedEmailEnabled());
        } else if (expected instanceof Instructor) {
            Instructor expectedInstructor = (Instructor) expected;
            InstructorData actualInstructor = (InstructorData) actual;
            assertEquals(expectedInstructor.getCourseId(), actualInstructor.getCourseId());
            assertEquals(expectedInstructor.getName(), actualInstructor.getName());
            assertEquals(expectedInstructor.getEmail(), actualInstructor.getEmail());
            // Cannot compare keys as actualInstructor's key is only generated before storing into the database.
            assertNotNull(actualInstructor.getKey());
            assertEquals(expectedInstructor.isDisplayedToStudents(), actualInstructor.getIsDisplayedToStudents());
            assertEquals(expectedInstructor.getDisplayName(), actualInstructor.getDisplayedToStudentsAs());
            assertEquals(expectedInstructor.getRole(), actualInstructor.getRole());
        } else if (expected instanceof Notification) {
            Notification expectedNotification = (Notification) expected;
            NotificationData actualNotification = (NotificationData) actual;
            assertEquals(expectedNotification.getStartTime().toEpochMilli(), actualNotification.getStartTimestamp());
            assertEquals(expectedNotification.getEndTime().toEpochMilli(), actualNotification.getEndTimestamp());
            assertEquals(expectedNotification.getStyle(), actualNotification.getStyle());
            assertEquals(expectedNotification.getTargetUser(), actualNotification.getTargetUser());
            assertEquals(expectedNotification.getTitle(), actualNotification.getTitle());
            assertEquals(expectedNotification.getMessage(), actualNotification.getMessage());
            assertEquals(expectedNotification.isShown(), actualNotification.isShown());
        } else if (expected instanceof Student) {
            Student expectedStudent = (Student) expected;
            StudentData actualStudent = (StudentData) actual;
            assertEquals(expectedStudent.getCourseId(), actualStudent.getCourseId());
            assertEquals(expectedStudent.getName(), actualStudent.getName());
            assertEquals(expectedStudent.getEmail(), actualStudent.getEmail());
            assertEquals(expectedStudent.getRegKey(), actualStudent.getKey());
            assertEquals(expectedStudent.getComments(), actualStudent.getComments());
            // TODO: A student might not have a team or section.
            // assertEquals(expectedStudent.getTeamName(), actualStudent.getTeamName());
            // assertEquals(expectedStudent.getSectionName(), actualStudent.getSectionName());
        } else if (expected instanceof UsageStatistics) {
            UsageStatistics expectedUsageStatistics = (UsageStatistics) expected;
            UsageStatisticsData actualUsageStatistics = (UsageStatisticsData) actual;
            assertEquals(expectedUsageStatistics.getStartTime().toEpochMilli(), actualUsageStatistics.getStartTime());
            assertEquals(expectedUsageStatistics.getTimePeriod(), actualUsageStatistics.getTimePeriod());
            assertEquals(expectedUsageStatistics.getNumResponses(), actualUsageStatistics.getNumResponses());
            assertEquals(expectedUsageStatistics.getNumCourses(), actualUsageStatistics.getNumCourses());
            assertEquals(expectedUsageStatistics.getNumStudents(), actualUsageStatistics.getNumStudents());
            assertEquals(expectedUsageStatistics.getNumInstructors(), actualUsageStatistics.getNumInstructors());
            assertEquals(expectedUsageStatistics.getNumAccountRequests(),
                    actualUsageStatistics.getNumAccountRequests());
            assertEquals(expectedUsageStatistics.getNumEmails(), actualUsageStatistics.getNumEmails());
            assertEquals(expectedUsageStatistics.getNumSubmissions(), actualUsageStatistics.getNumSubmissions());
        } else {
            fail("Unknown entity");
        }
    }

    /**
     * Verifies that the given entity is present in the database.
     */
    protected void verifyPresentInDatabase(BaseEntity expected) {
        assertNotNull(expected);
        ApiOutput actual = getEntity(expected);
        assertNotNull(actual);
        verifyEquals(expected, actual);
    }

    /**
     * Verifies that the given entity is absent in the database.
     */
    protected void verifyAbsentInDatabase(BaseEntity expected) {
        assertNotNull(expected);
        ApiOutput actual = getEntity(expected);
        assertNull(actual);
    }

    private ApiOutput getEntity(BaseEntity entity) {
        if (entity instanceof Account) {
            return getAccount((Account) entity);
        } else if (entity instanceof Course) {
            return getCourse((Course) entity);
        } else if (entity instanceof FeedbackQuestion) {
            return getFeedbackQuestion((FeedbackQuestion) entity);
        } else if (entity instanceof FeedbackResponse) {
            return getFeedbackResponse((FeedbackResponse) entity);
        } else if (entity instanceof FeedbackResponseComment) {
            return getFeedbackResponseComment((FeedbackResponseComment) entity);
        } else if (entity instanceof FeedbackSession) {
            return getFeedbackSession((FeedbackSession) entity);
        } else if (entity instanceof Instructor) {
            return getInstructor((Instructor) entity);
        } else if (entity instanceof Notification) {
            return getNotification((Notification) entity);
        } else if (entity instanceof Student) {
            return getStudent((Student) entity);
        } else {
            throw new RuntimeException("Unknown entity type");
        }
    }

    /**
     * Deletes a course from the database if it exists.
     */
    protected void deleteCourseIfExists(String courseId) {
        BACKDOOR.deleteCourseIfExists(courseId);
    }

    /**
     * Gets the feedback sessions of a course from the database.
     */
    protected List<FeedbackSessionData> getFeedbackSessionsForCourse(String courseId) {
        return BACKDOOR.getFeedbackSessionsForCourse(courseId);
    }

    /**
     * Gets the account data for the given Google ID.
     */
    protected AccountData getAccount(String googleId) {
        return BACKDOOR.getAccountData(googleId);
    }

    /**
     * Gets the account data for the given account.
     */
    protected AccountData getAccount(Account account) {
        return getAccount(account.getGoogleId());
    }

    /**
     * Gets the course data for the given course ID.
     */
    protected CourseData getCourse(String courseId) {
        return BACKDOOR.getCourseData(courseId);
    }

    /**
     * Gets the course data for the given course.
     */
    protected CourseData getCourse(Course course) {
        return getCourse(course.getId());
    }

    /**
     * Gets the feedback question data for the given question number and feedback session ID.
     */
    protected FeedbackQuestionData getFeedbackQuestion(int questionNumber, UUID feedbackSessionId) {
        return BACKDOOR.getFeedbackQuestionData(questionNumber, feedbackSessionId);
    }

    /**
     * Gets the feedback question data for the given feedback question.
     */
    protected FeedbackQuestionData getFeedbackQuestion(FeedbackQuestion fq) {
        return getFeedbackQuestion(fq.getQuestionNumber(), fq.getFeedbackSession().getId());
    }

    /**
     * Gets the feedback response data for the given question ID, giver, and recipient.
     */
    protected FeedbackResponseData getFeedbackResponse(String questionId, String giver, String recipient) {
        return BACKDOOR.getFeedbackResponseData(questionId, giver, recipient);
    }

    /**
     * Gets the feedback response data for the given feedback response.
     */
    protected FeedbackResponseData getFeedbackResponse(FeedbackResponse fr) {
        return getFeedbackResponse(fr.getFeedbackQuestion().getId().toString(), fr.getGiver(), fr.getRecipient());
    }

    /**
     * Gets the feedback response comment data for the given feedback response ID.
     */
    protected FeedbackResponseCommentData getFeedbackResponseComment(UUID feedbackResponseId) {
        return BACKDOOR.getFeedbackResponseCommentData(feedbackResponseId.toString());
    }

    /**
     * Gets the feedback response comment data for the given feedback response comment.
     */
    protected FeedbackResponseCommentData getFeedbackResponseComment(FeedbackResponseComment frc) {
        return getFeedbackResponseComment(frc.getFeedbackResponse().getId());
    }

    /**
     * Gets the feedback session data for the given feedback session ID.
     */
    protected FeedbackSessionData getFeedbackSession(UUID feedbackSessionId) {
        return BACKDOOR.getFeedbackSessionData(feedbackSessionId);
    }

    /**
     * Gets the feedback session data for the given feedback session.
     */
    protected FeedbackSessionData getFeedbackSession(FeedbackSession feedbackSession) {
        if (feedbackSession.getId() != null) {
            return getFeedbackSession(feedbackSession.getId());
        }

        // Feedback session may not have ID in some tests (where session is manually created).
        // As a workaround, we fetch all feedback sessions for the course and filter by fs name.
        // This is not ideal but should be sufficient for the tests that require this method.
        List<FeedbackSessionData> feedbackSessions = getFeedbackSessionsForCourse(feedbackSession.getCourseId());
        return feedbackSessions.stream()
                .filter(session -> session.getFeedbackSessionName().equals(feedbackSession.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Checks if the feedback session is published.
     */
    protected boolean isFeedbackSessionPublished(FeedbackSessionPublishStatus status) {
        return status == FeedbackSessionPublishStatus.PUBLISHED;
    }

    /**
     * Gets the soft-deleted feedback session data for the given feedback session name and instructor ID.
     */
    protected FeedbackSessionData getSoftDeletedSession(String feedbackSessionName, String instructorId) {
        return BACKDOOR.getSoftDeletedSessionData(feedbackSessionName, instructorId);
    }

    /**
     * Gets the instructor data for the given course ID and instructor email.
     */
    protected InstructorData getInstructor(String courseId, String instructorEmail) {
        return BACKDOOR.getInstructorData(courseId, instructorEmail);
    }

    /**
     * Gets the instructor data for the given instructor.
     */
    protected InstructorData getInstructor(Instructor instructor) {
        return getInstructor(instructor.getCourseId(), instructor.getEmail());
    }

    /**
     * Gets registration key for a given instructor.
     */
    protected String getKeyForInstructor(String courseId, String instructorEmail) {
        return getInstructor(courseId, instructorEmail).getKey();
    }

    /**
     * Gets a notification by given notification ID.
     */
    protected NotificationData getNotification(String notificationId) {
        return BACKDOOR.getNotificationData(notificationId);
    }

    /**
     * Gets a notification by given notification.
     */
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

    /**
     * Gets the student data for the given course ID and student email.
     */
    protected StudentData getStudent(String courseId, String studentEmailAddress) {
        return BACKDOOR.getStudentData(courseId, studentEmailAddress);
    }

    /**
     * Gets the student data for the given student.
     */
    protected StudentData getStudent(Student student) {
        return getStudent(student.getCourseId(), student.getEmail());
    }

    /**
     * Gets registration key for a given student.
     */
    protected String getKeyForStudent(Student student) {
        return getStudent(student).getKey();
    }

    /**
     * Gets feedback session deadline extensions data from the database.
     */
    protected DeadlineExtensionsData getDeadlineExtensions(UUID feedbackSessionId) {
        return BACKDOOR.getDeadlineExtensionsData(feedbackSessionId.toString());
    }

    /**
     * Updates the feedback response comment in the database.
     *
     * @param commentId the ID of the comment to update
     * @param commentText the new comment text
     * @param instructorGoogleId the Google ID of an instructor with permission to modify comments
     */
    protected void updateFeedbackResponseComment(UUID commentId, String commentText, String instructorGoogleId) {
        BACKDOOR.updateFeedbackResponseComment(commentId, commentText, instructorGoogleId);
    }
}
