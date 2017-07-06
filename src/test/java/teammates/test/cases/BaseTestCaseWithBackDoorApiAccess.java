package teammates.test.cases;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.retry.MaximumRetriesExceededException;
import teammates.common.util.retry.RetryableTaskReturns;
import teammates.test.driver.BackDoor;

/**
 * Base class for all test cases which are allowed to access the Datastore via {@link BackDoor}.
 */
public abstract class BaseTestCaseWithBackDoorApiAccess extends BaseTestCaseWithDatastoreAccess {

    protected AccountAttributes getAccount(String googleId) {
        return BackDoor.getAccount(googleId);
    }

    @Override
    protected AccountAttributes getAccount(AccountAttributes account) {
        return getAccount(account.googleId);
    }

    protected AccountAttributes getAccountWithRetry(final String googleId) throws MaximumRetriesExceededException {
        return persistenceRetryManager.runUntilNotNull(new RetryableTaskReturns<AccountAttributes>("getAccount") {
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

    protected CourseAttributes getCourseWithRetry(final String courseId) throws MaximumRetriesExceededException {
        return persistenceRetryManager.runUntilNotNull(new RetryableTaskReturns<CourseAttributes>("getCourse") {
            @Override
            public CourseAttributes run() {
                return getCourse(courseId);
            }
        });
    }

    protected FeedbackQuestionAttributes getFeedbackQuestion(String courseId, String feedbackSessionName, int qnNumber) {
        return BackDoor.getFeedbackQuestion(courseId, feedbackSessionName, qnNumber);
    }

    @Override
    protected FeedbackQuestionAttributes getFeedbackQuestion(FeedbackQuestionAttributes fq) {
        return getFeedbackQuestion(fq.courseId, fq.feedbackSessionName, fq.questionNumber);
    }

    protected FeedbackQuestionAttributes getFeedbackQuestionWithRetry(
            final String courseId, final String feedbackSessionName, final int qnNumber)
            throws MaximumRetriesExceededException {
        return persistenceRetryManager.runUntilNotNull(new RetryableTaskReturns<FeedbackQuestionAttributes>(
                "getFeedbackQuestion") {
            @Override
            public FeedbackQuestionAttributes run() {
                return getFeedbackQuestion(courseId, feedbackSessionName, qnNumber);
            }
        });
    }

    @Override
    protected FeedbackResponseCommentAttributes getFeedbackResponseComment(FeedbackResponseCommentAttributes frc) {
        throw new UnsupportedOperationException("Method not used");
    }

    @Override
    protected FeedbackResponseAttributes getFeedbackResponse(FeedbackResponseAttributes fr) {
        return BackDoor.getFeedbackResponse(fr.feedbackQuestionId, fr.giver, fr.recipient);
    }

    protected FeedbackSessionAttributes getFeedbackSession(String courseId, String feedbackSessionName) {
        return BackDoor.getFeedbackSession(courseId, feedbackSessionName);
    }

    @Override
    protected FeedbackSessionAttributes getFeedbackSession(FeedbackSessionAttributes fs) {
        return getFeedbackSession(fs.getCourseId(), fs.getFeedbackSessionName());
    }

    protected FeedbackSessionAttributes getFeedbackSessionWithRetry(
            final String courseId, final String feedbackSessionName) throws MaximumRetriesExceededException {
        return persistenceRetryManager.runUntilNotNull(new RetryableTaskReturns<FeedbackSessionAttributes>(
                "getFeedbackSession") {
            @Override
            public FeedbackSessionAttributes run() {
                return getFeedbackSession(courseId, feedbackSessionName);
            }
        });
    }

    protected InstructorAttributes getInstructor(String courseId, String instructorEmail) {
        return BackDoor.getInstructorByEmail(instructorEmail, courseId);
    }

    @Override
    protected InstructorAttributes getInstructor(InstructorAttributes instructor) {
        return getInstructor(instructor.courseId, instructor.email);
    }

    protected InstructorAttributes getInstructorWithRetry(final String courseId, final String instructorEmail)
            throws MaximumRetriesExceededException {
        return persistenceRetryManager.runUntilNotNull(new RetryableTaskReturns<InstructorAttributes>("getInstructor") {
            @Override
            public InstructorAttributes run() {
                return getInstructor(courseId, instructorEmail);
            }
        });
    }

    protected String getKeyForInstructor(String courseId, String instructorEmail) {
        return BackDoor.getEncryptedKeyForInstructor(courseId, instructorEmail);
    }

    protected String getKeyForInstructorWithRetry(final String courseId, final String instructorEmail)
            throws MaximumRetriesExceededException {
        return persistenceRetryManager.runUntilSuccessful(new RetryableTaskReturns<String>("getKeyForInstructor") {
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

    @Override
    protected String doRemoveAndRestoreDataBundle(DataBundle testData) {
        return BackDoor.removeAndRestoreDataBundle(testData);
    }

    @Override
    protected String doPutDocuments(DataBundle testData) {
        return BackDoor.putDocuments(testData);
    }

}
