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
import teammates.common.util.retry.RetryManager;
import teammates.logic.api.LogicExtension;

/**
 * Base class for all component tests.
 * It runs a simulated Datastore ({@link GaeSimulation}) which can be accessed via {@link LogicExtension}.
 */
@Test(singleThreaded = true) // GaeSimulation is not thread safe
public class BaseComponentTestCase extends BaseTestCaseWithDatastoreAccess {

    protected static final GaeSimulation gaeSimulation = GaeSimulation.inst();
    protected static final LogicExtension logic = new LogicExtension();
    private static final MockFileStorage MOCK_FILE_STORAGE = new MockFileStorage();

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

    protected static void writeFileToStorage(String targetFileName, String sourceFilePath) throws IOException {
        byte[] bytes = FileHelper.readFileAsBytes(sourceFilePath);
        String contentType = URLConnection.guessContentTypeFromName(sourceFilePath);
        MOCK_FILE_STORAGE.create(targetFileName, bytes, contentType);
    }

    protected static void deleteFile(String fileName) {
        MOCK_FILE_STORAGE.delete(fileName);
    }

    protected static boolean doesFileExist(String fileName) {
        return MOCK_FILE_STORAGE.doesFileExist(fileName);
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
