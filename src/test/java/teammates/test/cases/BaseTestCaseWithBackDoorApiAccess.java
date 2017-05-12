package teammates.test.cases;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CommentAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.test.driver.BackDoor;

/**
 * Base class for all test cases which are allowed to access the Datastore via {@link BackDoor}.
 */
public abstract class BaseTestCaseWithBackDoorApiAccess extends BaseTestCaseWithDatastoreAccess {

    private static final int BACKDOOR_GET_RETRY_COUNT = 100;
    private static final int BACKDOOR_GET_RETRY_DELAY_IN_MS = 3000;

    protected AccountAttributes getAccount(String googleId) {
        return BackDoor.getAccount(googleId);
    }

    @Override
    protected AccountAttributes getAccount(AccountAttributes account) {
        return getAccount(account.googleId);
    }

    @Override
    protected CommentAttributes getComment(CommentAttributes comment) {
        throw new UnsupportedOperationException("Method not used");
    }

    protected CourseAttributes getCourse(String courseId) {
        return BackDoor.getCourse(courseId);
    }

    @Override
    protected CourseAttributes getCourse(CourseAttributes course) {
        return getCourse(course.getId());
    }

    protected CourseAttributes getCourseWithRetry(String courseId) {
        CourseAttributes course = getCourse(courseId);
        int retriesRemaining = BACKDOOR_GET_RETRY_COUNT;
        while (course == null && retriesRemaining > 0) {
            print("Re-trying getCourse...");
            ThreadHelper.waitFor(BACKDOOR_GET_RETRY_DELAY_IN_MS);
            course = getCourse(courseId);
            retriesRemaining--;
        }
        return course;
    }

    @Override
    protected FeedbackQuestionAttributes getFeedbackQuestion(FeedbackQuestionAttributes fq) {
        return BackDoor.getFeedbackQuestion(fq.courseId, fq.feedbackSessionName, fq.questionNumber);
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

    protected InstructorAttributes getInstructor(String courseId, String instructorEmail) {
        return BackDoor.getInstructorByEmail(instructorEmail, courseId);
    }

    @Override
    protected InstructorAttributes getInstructor(InstructorAttributes instructor) {
        return getInstructor(instructor.courseId, instructor.email);
    }

    protected InstructorAttributes getInstructorWithRetry(String courseId, String instructorEmail) {
        InstructorAttributes instructor = getInstructor(courseId, instructorEmail);
        int retriesRemaining = BACKDOOR_GET_RETRY_COUNT;
        while (instructor == null && retriesRemaining > 0) {
            print("Re-trying getInstructorByEmail...");
            ThreadHelper.waitFor(BACKDOOR_GET_RETRY_DELAY_IN_MS);
            instructor = getInstructor(courseId, instructorEmail);
            retriesRemaining--;
        }
        return instructor;
    }

    protected String getKeyForInstructor(String courseId, String instructorEmail) {
        return BackDoor.getEncryptedKeyForInstructor(courseId, instructorEmail);
    }

    protected String getKeyForInstructorWithRetry(String courseId, String instructorEmail) {
        String key = getKeyForInstructor(courseId, instructorEmail);
        int retriesRemaining = BACKDOOR_GET_RETRY_COUNT;
        while (key.startsWith(Const.StatusCodes.BACKDOOR_STATUS_FAILURE) && retriesRemaining > 0) {
            print("Re-trying getEncryptedKeyForInstructor...");
            ThreadHelper.waitFor(BACKDOOR_GET_RETRY_DELAY_IN_MS);
            key = getKeyForInstructor(courseId, instructorEmail);
            retriesRemaining--;
        }
        return key;
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
