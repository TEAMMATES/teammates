package teammates.e2e.cases;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.HttpRequestFailedException;
import teammates.common.util.Const;
import teammates.common.util.retry.MaximumRetriesExceededException;
import teammates.common.util.retry.RetryManager;
import teammates.common.util.retry.RetryableTaskReturns;
import teammates.e2e.util.BackDoor;
import teammates.e2e.util.TestProperties;
import teammates.test.BaseTestCaseWithDatastoreAccess;
import teammates.ui.output.CourseData;

/**
 * Base class for all test cases which are allowed to access the Datastore via {@link BackDoor}.
 */
@SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
public abstract class BaseTestCaseWithBackDoorApiAccess extends BaseTestCaseWithDatastoreAccess {

    @Override
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public void setupObjectify() {
        // Not necessary as BackDoor API is used instead
    }

    @Override
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public void tearDownObjectify() {
        // Not necessary as BackDoor API is used instead
    }

    @Override
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public void setUpGae() {
        // Not necessary as BackDoor API is used instead
    }

    @Override
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public void tearDownGae() {
        // Not necessary as BackDoor API is used instead
    }

    @Override
    protected RetryManager getPersistenceRetryManager() {
        return new RetryManager(TestProperties.PERSISTENCE_RETRY_PERIOD_IN_S / 2);
    }

    protected AccountAttributes getAccount(String googleId) {
        return BackDoor.getAccount(googleId);
    }

    @Override
    protected AccountAttributes getAccount(AccountAttributes account) {
        return getAccount(account.googleId);
    }

    @Override
    protected StudentProfileAttributes getStudentProfile(StudentProfileAttributes studentProfileAttributes) {
        return null; // BackDoor.getStudentProfile(studentProfileAttributes.googleId);
    }

    protected AccountAttributes getAccountWithRetry(String googleId) throws MaximumRetriesExceededException {
        return getPersistenceRetryManager().runUntilNotNull(new RetryableTaskReturns<AccountAttributes>("getAccount") {
            @Override
            public AccountAttributes run() {
                return getAccount(googleId);
            }
        });
    }

    protected CourseAttributes getCourse(String courseId) {
        return BackDoor.getCourse(courseId);
    }

    @Override
    protected CourseAttributes getCourse(CourseAttributes course) {
        return getCourse(course.getId());
    }

    protected CourseAttributes getArchivedCourse(String instructorId, String courseId) {
        return BackDoor.getArchivedCourse(instructorId, courseId);
    }

    protected CourseAttributes getCourseWithRetry(String courseId) throws MaximumRetriesExceededException {
        return getPersistenceRetryManager().runUntilNotNull(new RetryableTaskReturns<CourseAttributes>("getCourse") {
            @Override
            public CourseAttributes run() {
                return getCourse(courseId);
            }
        });
    }

    protected boolean isCourseInRecycleBin(String courseId) {
        CourseData courseData = BackDoor.getCourseData(courseId);
        if (courseData == null) {
            return false;
        }
        return courseData.getDeletionTimestamp() != 0;
    }

    protected FeedbackQuestionAttributes getFeedbackQuestion(String courseId, String feedbackSessionName, int qnNumber) {
        return BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, qnNumber);
    }

    @Override
    protected FeedbackQuestionAttributes getFeedbackQuestion(FeedbackQuestionAttributes fq) {
        return getFeedbackQuestion(fq.courseId, fq.feedbackSessionName, fq.questionNumber);
    }

    protected FeedbackQuestionAttributes getFeedbackQuestionWithRetry(
            String courseId, String feedbackSessionName, int qnNumber)
            throws MaximumRetriesExceededException {
        return getPersistenceRetryManager().runUntilNotNull(new RetryableTaskReturns<FeedbackQuestionAttributes>(
                "getFeedbackQuestion") {
            @Override
            public FeedbackQuestionAttributes run() {
                return getFeedbackQuestion(courseId, feedbackSessionName, qnNumber);
            }
        });
    }

    protected FeedbackResponseCommentAttributes getFeedbackResponseComment(String feedbackResponseId) {
        return BackDoor.getFeedbackResponseComment(feedbackResponseId);
    }

    @Override
    protected FeedbackResponseCommentAttributes getFeedbackResponseComment(FeedbackResponseCommentAttributes frc) {
        return getFeedbackResponseComment(frc.feedbackResponseId);
    }

    protected FeedbackResponseAttributes getFeedbackResponse(String feedbackQuestionId, String giver, String recipient) {
        return BackDoor.getFeedbackResponse(feedbackQuestionId, giver, recipient);
    }

    @Override
    protected FeedbackResponseAttributes getFeedbackResponse(FeedbackResponseAttributes fr) {
        return getFeedbackResponse(fr.feedbackQuestionId, fr.giver, fr.recipient);
    }

    protected FeedbackSessionAttributes getFeedbackSession(String courseId, String feedbackSessionName) {
        return BackDoor.getFeedbackSession(courseId, feedbackSessionName);
    }

    @Override
    protected FeedbackSessionAttributes getFeedbackSession(FeedbackSessionAttributes fs) {
        return getFeedbackSession(fs.getCourseId(), fs.getFeedbackSessionName());
    }

    protected FeedbackSessionAttributes getFeedbackSessionWithRetry(String courseId, String feedbackSessionName)
            throws MaximumRetriesExceededException {
        return getPersistenceRetryManager().runUntilNotNull(new RetryableTaskReturns<FeedbackSessionAttributes>(
                "getFeedbackSession") {
            @Override
            public FeedbackSessionAttributes run() {
                return getFeedbackSession(courseId, feedbackSessionName);
            }
        });
    }

    protected FeedbackSessionAttributes getSoftDeletedSession(String feedbackSessionName, String instructorId) {
        return BackDoor.getSoftDeletedSession(feedbackSessionName, instructorId);
    }

    protected InstructorAttributes getInstructor(String courseId, String instructorEmail) {
        return BackDoor.getInstructor(courseId, instructorEmail);
    }

    @Override
    protected InstructorAttributes getInstructor(InstructorAttributes instructor) {
        return getInstructor(instructor.courseId, instructor.email);
    }

    protected InstructorAttributes getInstructorWithRetry(String courseId, String instructorEmail)
            throws MaximumRetriesExceededException {
        return getPersistenceRetryManager().runUntilNotNull(new RetryableTaskReturns<InstructorAttributes>("getInstructor") {
            @Override
            public InstructorAttributes run() {
                return getInstructor(courseId, instructorEmail);
            }
        });
    }

    protected String getKeyForInstructor(String courseId, String instructorEmail) {
        return getInstructor(courseId, instructorEmail).getKey();
    }

    protected String getKeyForInstructorWithRetry(String courseId, String instructorEmail)
            throws MaximumRetriesExceededException {
        return getPersistenceRetryManager().runUntilSuccessful(new RetryableTaskReturns<String>("getKeyForInstructor") {
            @Override
            public String run() {
                return getKeyForInstructor(courseId, instructorEmail);
            }

            @Override
            public boolean isSuccessful(String result) {
                return !result.startsWith(Const.StatusCodes.BACKDOOR_STATUS_FAILURE);
            }
        });
    }

    @Override
    protected StudentAttributes getStudent(StudentAttributes student) {
        return BackDoor.getStudent(student.course, student.email);
    }

    protected String getKeyForStudent(StudentAttributes student) {
        return getStudent(student).getKey();
    }

    @Override
    protected String doRemoveAndRestoreDataBundle(DataBundle testData) throws HttpRequestFailedException {
        return BackDoor.removeAndRestoreDataBundle(testData);
    }

    @Override
    protected String doPutDocuments(DataBundle testData) {
        return BackDoor.putDocuments(testData);
    }

}
