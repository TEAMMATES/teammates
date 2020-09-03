package teammates.test;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Arrays;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
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
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.retry.RetryManager;
import teammates.logic.api.Logic;

/**
 * Base class for all component tests.
 * It runs a simulated Datastore ({@link GaeSimulation}) which can be accessed via {@link Logic}.
 */
@Test(singleThreaded = true) // GaeSimulation is not thread safe
public class BaseComponentTestCase extends BaseTestCaseWithDatastoreAccess {

    protected static final GaeSimulation gaeSimulation = GaeSimulation.inst();
    protected static final Logic logic = new Logic();

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

    @Override
    protected RetryManager getPersistenceRetryManager() {
        return new RetryManager(TestProperties.PERSISTENCE_RETRY_PERIOD_IN_S / 2);
    }

    protected static String writeFileToGcs(String googleId, String filename) throws IOException {
        byte[] image = FileHelper.readFileAsBytes(filename);
        String contentType = URLConnection.guessContentTypeFromName(filename);
        return GoogleCloudStorageHelper.writeImageDataToGcs(googleId, image, contentType);
    }

    protected static boolean doesFileExistInGcs(String fileKey) {
        return GoogleCloudStorageHelper.doesFileExistInGcs(fileKey);
    }

    @Override
    protected AccountAttributes getAccount(AccountAttributes account) {
        return logic.getAccount(account.googleId);
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
        return logic.getFeedbackQuestion(fq.feedbackSessionName, fq.courseId, fq.questionNumber);
    }

    @Override
    protected FeedbackResponseCommentAttributes getFeedbackResponseComment(FeedbackResponseCommentAttributes frc) {
        return logic.getFeedbackResponseComment(frc.feedbackResponseId, frc.commentGiver, frc.createdAt);
    }

    @Override
    protected FeedbackResponseAttributes getFeedbackResponse(FeedbackResponseAttributes fr) {
        return logic.getFeedbackResponse(fr.feedbackQuestionId, fr.giver, fr.recipient);
    }

    @Override
    protected FeedbackSessionAttributes getFeedbackSession(FeedbackSessionAttributes fs) {
        return logic.getFeedbackSession(fs.getFeedbackSessionName(), fs.getCourseId());
    }

    @Override
    protected InstructorAttributes getInstructor(InstructorAttributes instructor) {
        return instructor.googleId == null
                ? logic.getInstructorForEmail(instructor.courseId, instructor.email)
                : logic.getInstructorForGoogleId(instructor.courseId, instructor.googleId);
    }

    @Override
    protected StudentAttributes getStudent(StudentAttributes student) {
        return logic.getStudentForEmail(student.course, student.email);
    }

    protected void removeAndRestoreTypicalDataBundle() {
        DataBundle dataBundle = getTypicalDataBundle();
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    protected String doRemoveAndRestoreDataBundle(DataBundle dataBundle) {
        try {
            logic.removeDataBundle(dataBundle);
            logic.persistDataBundle(dataBundle);
            return Const.StatusCodes.BACKDOOR_STATUS_SUCCESS;
        } catch (Exception e) {
            return Const.StatusCodes.BACKDOOR_STATUS_FAILURE + ": " + TeammatesException.toStringWithStackTrace(e);
        }
    }

    @Override
    protected String doPutDocuments(DataBundle dataBundle) {
        try {
            logic.putDocuments(dataBundle);
            return Const.StatusCodes.BACKDOOR_STATUS_SUCCESS;
        } catch (Exception e) {
            return Const.StatusCodes.BACKDOOR_STATUS_FAILURE + ": " + TeammatesException.toStringWithStackTrace(e);
        }
    }

    /*
     * Verifies that search results match with expected output.
     * Compares the text for each comment as it is unique.
     *
     * @param actual the results from the search query.
     * @param expected the expected results for the search query.
     */
    protected static void verifySearchResults(FeedbackResponseCommentSearchResultBundle actual,
            FeedbackResponseCommentAttributes... expected) {
        assertEquals(expected.length, actual.numberOfResults);
        assertEquals(expected.length, actual.comments.size());
        FeedbackResponseCommentAttributes.sortFeedbackResponseCommentsByCreationTime(Arrays.asList(expected));
        FeedbackResponseCommentAttributes[] sortedComments = Arrays.asList(expected)
                                                                     .toArray(new FeedbackResponseCommentAttributes[2]);
        int[] i = new int[] { 0 };
        actual.comments.forEach((key, comments) -> comments.forEach(comment -> {
            assertEquals(sortedComments[i[0]].commentText, comment.commentText);
            i[0]++;
        }));
    }
}
