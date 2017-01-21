package teammates.test.cases;

import java.io.IOException;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.test.driver.GaeSimulation;
import teammates.test.util.FileHelper;

import com.google.appengine.api.blobstore.BlobKey;

/**
 * Base class for all component tests.
 * It runs a simulated Datastore ({@link GaeSimulation}) which can be accessed via {@link BackDoorLogic}.
 */
public class BaseComponentTestCase extends BaseTestCaseWithDatastoreAccess {
    
    protected static GaeSimulation gaeSimulation = GaeSimulation.inst();
    protected static BackDoorLogic backDoorLogic = new BackDoorLogic();
    
    @BeforeTest
    public void testSetup() {
        gaeSimulation.setup();
    }
    
    protected static String writeFileToGcs(String googleId, String filename) throws IOException {
        byte[] image = FileHelper.readFileAsBytes(filename);
        return GoogleCloudStorageHelper.writeImageDataToGcs(googleId, image);
    }
    
    protected static boolean doesFileExistInGcs(BlobKey fileKey) {
        return GoogleCloudStorageHelper.doesFileExistInGcs(fileKey);
    }
    
    @Override
    protected AccountAttributes getAccount(AccountAttributes account) {
        return backDoorLogic.getAccount(account.googleId);
    }
    
    @Override
    protected CommentAttributes getComment(CommentAttributes comment) {
        return backDoorLogic.getComment(comment);
    }
    
    @Override
    protected CourseAttributes getCourse(CourseAttributes course) {
        return backDoorLogic.getCourse(course.getId());
    }
    
    @Override
    protected FeedbackQuestionAttributes getFeedbackQuestion(FeedbackQuestionAttributes fq) {
        return backDoorLogic.getFeedbackQuestion(fq.feedbackSessionName, fq.courseId, fq.questionNumber);
    }
    
    @Override
    protected FeedbackResponseCommentAttributes getFeedbackResponseComment(FeedbackResponseCommentAttributes frc) {
        return backDoorLogic.getFeedbackResponseComment(frc.feedbackResponseId, frc.giverEmail, frc.createdAt);
    }
    
    @Override
    protected FeedbackResponseAttributes getFeedbackResponse(FeedbackResponseAttributes fr) {
        return backDoorLogic.getFeedbackResponse(fr.feedbackQuestionId, fr.giver, fr.recipient);
    }
    
    @Override
    protected FeedbackSessionAttributes getFeedbackSession(FeedbackSessionAttributes fs) {
        return backDoorLogic.getFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId());
    }
    
    @Override
    protected InstructorAttributes getInstructor(InstructorAttributes instructor) {
        return instructor.googleId == null
                ? backDoorLogic.getInstructorForEmail(instructor.courseId, instructor.email)
                : backDoorLogic.getInstructorForGoogleId(instructor.courseId, instructor.googleId);
    }
    
    @Override
    protected StudentAttributes getStudent(StudentAttributes student) {
        return backDoorLogic.getStudentForEmail(student.course, student.email);
    }
    
    protected void removeAndRestoreTypicalDataBundle() {
        DataBundle dataBundle = getTypicalDataBundle();
        removeAndRestoreDataBundle(dataBundle);
    }
    
    @Override
    protected String doRemoveAndRestoreDataBundle(DataBundle dataBundle) {
        try {
            backDoorLogic.removeDataBundle(dataBundle);
            backDoorLogic.persistDataBundle(dataBundle);
            return Const.StatusCodes.BACKDOOR_STATUS_SUCCESS;
        } catch (Exception e) {
            return Const.StatusCodes.BACKDOOR_STATUS_FAILURE + ": " + TeammatesException.toStringWithStackTrace(e);
        }
    }
    
    @Override
    protected String doPutDocuments(DataBundle dataBundle) {
        try {
            backDoorLogic.putDocuments(dataBundle);
            return Const.StatusCodes.BACKDOOR_STATUS_SUCCESS;
        } catch (Exception e) {
            return Const.StatusCodes.BACKDOOR_STATUS_FAILURE + ": " + TeammatesException.toStringWithStackTrace(e);
        }
    }
    
    @AfterTest
    public void testTearDown() {
        gaeSimulation.tearDown();
    }
    
}
