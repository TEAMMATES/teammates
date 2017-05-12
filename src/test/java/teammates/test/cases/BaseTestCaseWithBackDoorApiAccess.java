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
import teammates.test.driver.TestProperties;

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
        for (int delay = 1; course == null
                && delay <= TestProperties.PERSISTENCE_RETRY_PERIOD_IN_S / 2; delay *= 2) {
            print("Backdoor getCourse failed; waiting " + delay + "s before retry");
            ThreadHelper.waitFor(delay * 1000);
            course = getCourse(courseId);
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
        for (int delay = 1; instructor == null
                && delay <= TestProperties.PERSISTENCE_RETRY_PERIOD_IN_S / 2; delay *= 2) {
            print("Backdoor getInstructorByEmail failed; waiting " + delay + "s before retry");
            ThreadHelper.waitFor(delay * 1000);
            instructor = getInstructor(courseId, instructorEmail);
        }
        return instructor;
    }

    protected String getKeyForInstructor(String courseId, String instructorEmail) {
        return BackDoor.getEncryptedKeyForInstructor(courseId, instructorEmail);
    }

    protected String getKeyForInstructorWithRetry(String courseId, String instructorEmail) {
        String key = getKeyForInstructor(courseId, instructorEmail);
        for (int delay = 1; key.startsWith(Const.StatusCodes.BACKDOOR_STATUS_FAILURE)
                && delay <= TestProperties.PERSISTENCE_RETRY_PERIOD_IN_S / 2; delay *= 2) {
            print("Backdoor getEncryptedKeyForInstructor failed; waiting " + delay + "s before retry");
            ThreadHelper.waitFor(delay * 1000);
            key = getKeyForInstructor(courseId, instructorEmail);
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
