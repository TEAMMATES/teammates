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
import teammates.test.driver.BackDoor;

/**
 * Base class for all test cases which are allowed to access the Datastore via {@link BackDoor}.
 */
public abstract class BaseTestCaseWithBackDoorApiAccess extends BaseTestCaseWithDatastoreAccess {

    @Override
    protected AccountAttributes getAccount(AccountAttributes account) {
        return BackDoor.getAccount(account.googleId);
    }

    @Override
    protected CommentAttributes getComment(CommentAttributes comment) {
        throw new UnsupportedOperationException("Method not used");
    }

    @Override
    protected CourseAttributes getCourse(CourseAttributes course) {
        return BackDoor.getCourse(course.getId());
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

    @Override
    protected FeedbackSessionAttributes getFeedbackSession(FeedbackSessionAttributes fs) {
        return BackDoor.getFeedbackSession(fs.getCourseId(), fs.getFeedbackSessionName());
    }

    @Override
    protected InstructorAttributes getInstructor(InstructorAttributes instructor) {
        return BackDoor.getInstructorByEmail(instructor.email, instructor.courseId);
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
