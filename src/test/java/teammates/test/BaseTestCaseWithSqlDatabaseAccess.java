package teammates.test;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.BaseEntity;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.UsageStatistics;
import teammates.ui.output.AccountData;
import teammates.ui.output.ApiOutput;
import teammates.ui.output.CourseData;
import teammates.ui.output.DeadlineExtensionData;
import teammates.ui.output.FeedbackQuestionData;
import teammates.ui.output.FeedbackResponseCommentData;
import teammates.ui.output.FeedbackResponseData;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.InstructorData;
import teammates.ui.output.NotificationData;
import teammates.ui.output.NumberOfEntitiesToGiveFeedbackToSetting;
import teammates.ui.output.StudentData;
import teammates.ui.output.UsageStatisticsData;

/**
 * Base class for all test cases which are allowed to access the database.
 */
public abstract class BaseTestCaseWithSqlDatabaseAccess extends BaseTestCase {

    private static final int VERIFICATION_RETRY_COUNT = 5;
    private static final int VERIFICATION_RETRY_DELAY_IN_MS = 1000;
    private static final int OPERATION_RETRY_COUNT = 5;
    private static final int OPERATION_RETRY_DELAY_IN_MS = 1000;

    /**
     * Removes and restores the databundle, with retries.
     */
    protected SqlDataBundle removeAndRestoreDataBundle(SqlDataBundle testData) {
        int retryLimit = OPERATION_RETRY_COUNT;
        SqlDataBundle dataBundle = doRemoveAndRestoreDataBundle(testData);
        while (dataBundle == null && retryLimit > 0) {
            retryLimit--;
            print("Re-trying removeAndRestoreDataBundle");
            ThreadHelper.waitFor(OPERATION_RETRY_DELAY_IN_MS);
            dataBundle = doRemoveAndRestoreDataBundle(testData);
        }
        assertNotNull(dataBundle);
        return dataBundle;
    }

    protected abstract SqlDataBundle doRemoveAndRestoreDataBundle(SqlDataBundle testData);

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
        } else if (expected instanceof DeadlineExtension) {
            DeadlineExtension expectedDeadlineExtension = (DeadlineExtension) expected;
            DeadlineExtensionData actualDeadlineExtension = (DeadlineExtensionData) actual;
            assertEquals(expectedDeadlineExtension.getEndTime().toEpochMilli(), actualDeadlineExtension.getEndTime());
            assertEquals(expectedDeadlineExtension.isClosingSoonEmailSent(),
                    actualDeadlineExtension.getSentClosingSoonEmail());
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
        int retryLimit = VERIFICATION_RETRY_COUNT;
        ApiOutput actual = getEntity(expected);
        while (actual == null && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(VERIFICATION_RETRY_DELAY_IN_MS);
            actual = getEntity(expected);
        }
        verifyEquals(expected, actual);
    }

    /**
     * Verifies that the given entity is absent in the database.
     */
    protected void verifyAbsentInDatabase(BaseEntity expected) {
        int retryLimit = VERIFICATION_RETRY_COUNT;
        ApiOutput actual = getEntity(expected);
        while (actual != null && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(VERIFICATION_RETRY_DELAY_IN_MS);
            actual = getEntity(expected);
        }
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

    protected abstract AccountData getAccount(Account account);

    protected abstract CourseData getCourse(Course course);

    protected abstract FeedbackQuestionData getFeedbackQuestion(FeedbackQuestion fq);

    protected abstract FeedbackResponseData getFeedbackResponse(FeedbackResponse fr);

    protected abstract FeedbackSessionData getFeedbackSession(FeedbackSession fs);

    protected abstract InstructorData getInstructor(Instructor instructor);

    protected abstract NotificationData getNotification(Notification notification);

    protected abstract StudentData getStudent(Student student);

}
