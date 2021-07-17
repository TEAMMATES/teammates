package teammates.test;

import org.testng.annotations.Test;

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
import teammates.common.exception.TeammatesException;
import teammates.logic.api.LogicExtension;

/**
 * Base class for all component tests.
 */
@Test(singleThreaded = true)
public class BaseComponentTestCase extends BaseTestCaseWithDatabaseAccess {

    protected static final LogicExtension logic = new LogicExtension();

    @Override
    protected AccountAttributes getAccount(AccountAttributes account) {
        return logic.getAccount(account.getGoogleId());
    }

    @Override
    protected StudentProfileAttributes getStudentProfile(StudentProfileAttributes studentProfileAttributes) {
        return logic.getStudentProfile(studentProfileAttributes.googleId);
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
            print(TeammatesException.toStringWithStackTrace(e));
            return false;
        }
    }

    @Override
    protected boolean doPutDocuments(DataBundle dataBundle) {
        try {
            logic.putDocuments(dataBundle);
            return true;
        } catch (Exception e) {
            print(TeammatesException.toStringWithStackTrace(e));
            return false;
        }
    }

}
