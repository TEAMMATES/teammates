package teammates.test.cases;

import java.io.IOException;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.blobstore.BlobKey;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.test.driver.FileHelper;
import teammates.test.driver.GaeSimulation;

/**
 * Base class for all component tests.
 * It runs a simulated Datastore ({@link GaeSimulation}) which can be accessed via {@link BackDoorLogic}.
 */
@Test(singleThreaded = true) // GaeSimulation is not thread safe
public class BaseComponentTestCase extends BaseTestCaseWithDatastoreAccess {

    protected static final GaeSimulation gaeSimulation = GaeSimulation.inst();
    protected static final BackDoorLogic backDoorLogic = new BackDoorLogic();

    @Override
    @BeforeClass
    public void setUpGae() {
        gaeSimulation.setup();
    }

    @Override
    @AfterClass
    public void tearDownGae() {
        gaeSimulation.tearDown();
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

}
