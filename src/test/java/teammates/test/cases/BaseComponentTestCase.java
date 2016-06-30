package teammates.test.cases;

import java.io.IOException;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributes.UpdateStatus;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.StringHelper;
import teammates.common.util.Utils;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.CommentsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;
import teammates.test.driver.GaeSimulation;
import teammates.test.util.FileHelper;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.gson.Gson;

/** Base class for Component tests.
 * Automatically sets up the GAE Simulation @BeforeTest and tears it down @AfterTest
 */
public class BaseComponentTestCase extends BaseTestCase {

    protected static GaeSimulation gaeSimulation = GaeSimulation.inst();
    
    private static final AccountsDb accountsDb = new AccountsDb();
    private static final CommentsDb commentsDb = new CommentsDb();
    private static final CoursesDb coursesDb = new CoursesDb();
    private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
    private static final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();
    private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
    private static final FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
    private static final InstructorsDb instructorsDb = new InstructorsDb();
    private static final StudentsDb studentsDb = new StudentsDb();

    private static Gson gson = Utils.getTeammatesGson();

    @BeforeTest
    public void testSetUp() {
        gaeSimulation = GaeSimulation.inst();
        gaeSimulation.setup();
        
    }
    
    protected static String writeFileToGcs(String googleId, String filename) throws IOException {
        byte[] image = FileHelper.readFileAsBytes(filename);
        return GoogleCloudStorageHelper.writeImageDataToGcs(googleId, image);
    }
    
    protected static boolean doesFileExistInGcs(BlobKey fileKey) {
        return GoogleCloudStorageHelper.doesFileExistInGcs(fileKey);
    }
    
    protected static void verifyAbsentInDatastore(AccountAttributes account) {
        assertNull(accountsDb.getAccount(account.googleId));
    }

    protected static void verifyPresentInDatastore(AccountAttributes expectedAccount) {
        AccountAttributes actualAccount = accountsDb.getAccount(expectedAccount.googleId);
        // Account when created by createInstructor may take up different values in NAME and EMAIL
        // from the typicalDataBundle. Hence we only check that the account exists in the DataStore
        assertTrue(actualAccount != null);
    }

    protected static void verifyAbsentInDatastore(CommentAttributes comment) {
        assertNull(commentsDb.getComment(comment));
    }
    
    protected static void verifyPresentInDatastore(CommentAttributes expected) {
        CommentAttributes actual = commentsDb.getComment(expected);
        assertEquals(expected.courseId, actual.courseId);
        assertEquals(expected.giverEmail, actual.giverEmail);
        assertEquals(expected.recipients, actual.recipients);
        assertEquals(expected.commentText, actual.commentText);
    }
    
    protected static void verifyAbsentInDatastore(CourseAttributes course) {
        assertNull(coursesDb.getCourse(course.getId()));
    }

    protected static void verifyPresentInDatastore(CourseAttributes expected) {
        CourseAttributes actual = coursesDb.getCourse(expected.getId());
        // Ignore time field as it is stamped at the time of creation in testing
        actual.createdAt = expected.createdAt;
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }

    protected static void verifyAbsentInDatastore(FeedbackQuestionAttributes fq) {
        assertNull(fqDb.getFeedbackQuestion(fq.feedbackSessionName, fq.courseId, fq.questionNumber));
    }
    
    protected static void verifyPresentInDatastore(FeedbackQuestionAttributes expected) {
        verifyPresentInDatastore(expected, false);
    }
    
    protected static void verifyPresentInDatastore(FeedbackQuestionAttributes expected, boolean wildcardId) {
        FeedbackQuestionAttributes actual = fqDb.getFeedbackQuestion(expected.feedbackSessionName,
                                                                     expected.courseId, expected.questionNumber);
        if (wildcardId) {
            expected.setId(actual.getId());
        }
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }
    
    protected static void verifyAbsentInDatastore(FeedbackResponseCommentAttributes frc) {
        assertNull(frcDb.getFeedbackResponseComment(frc.feedbackResponseId, frc.giverEmail, frc.createdAt));
    }
    
    protected static void verifyPresentInDatastore(FeedbackResponseCommentAttributes expected) {
        FeedbackResponseCommentAttributes actual =
                                        frcDb.getFeedbackResponseComment(expected.courseId, expected.createdAt,
                                                                         expected.giverEmail);
        assertEquals(expected.courseId, actual.courseId);
        assertEquals(expected.giverEmail, actual.giverEmail);
        assertEquals(expected.feedbackSessionName, actual.feedbackSessionName);
        assertEquals(expected.commentText, actual.commentText);
    }
   
    protected static void verifyAbsentInDatastore(FeedbackResponseAttributes fr) {
        assertNull(frDb.getFeedbackResponse(fr.feedbackQuestionId, fr.giver, fr.recipient));
    }
    
    protected static void verifyPresentInDatastore(FeedbackResponseAttributes expected) {
        verifyPresentInDatastore(expected, false);
    }
    
    protected static void verifyPresentInDatastore(FeedbackResponseAttributes expected, boolean wildcardId) {
        FeedbackResponseAttributes actual = frDb.getFeedbackResponse(expected.feedbackQuestionId,
                                                                     expected.giver, expected.recipient);
        if (wildcardId) {
            expected.setId(actual.getId());
        }
        
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }
    
    protected static void verifyAbsentInDatastore(FeedbackSessionAttributes fs) {
        assertNull(fsDb.getFeedbackSession(fs.getCourseId(), fs.getFeedbackSessionName()));
    }
    
    protected static void verifyPresentInDatastore(FeedbackSessionAttributes expected) {
        FeedbackSessionAttributes actual =
                fsDb.getFeedbackSession(expected.getCourseId(), expected.getFeedbackSessionName());
        expected.setRespondingInstructorList(actual.getRespondingInstructorList());
        expected.setRespondingStudentList(actual.getRespondingStudentList());
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }

    protected static void verifyAbsentInDatastore(InstructorAttributes instructor) {
        assertNull(instructorsDb.getInstructorForGoogleId(instructor.courseId, instructor.googleId));
    }

    protected static void verifyPresentInDatastore(InstructorAttributes expected) {
        InstructorAttributes actual = expected.googleId == null
                                    ? instructorsDb.getInstructorForEmail(expected.courseId, expected.email)
                                    : instructorsDb.getInstructorForGoogleId(expected.courseId, expected.googleId);
        equalizeIrrelevantData(expected, actual);

        assertTrue(expected.isEqualToAnotherInstructor(actual));
    }
    
    private static void equalizeIrrelevantData(InstructorAttributes expectedInstructor,
                                               InstructorAttributes actualInstructor) {
        
        // pretend keys match because the key is generated only before storing into database
        if (actualInstructor.key != null) {
            expectedInstructor.key = actualInstructor.key;
        }
    }

    protected static void verifyAbsentInDatastore(StudentAttributes student) {
        assertNull(studentsDb.getStudentForEmail(student.course, student.email));
    }
    
    protected static void verifyPresentInDatastore(StudentAttributes expected) {
        StudentAttributes actual = studentsDb.getStudentForEmail(expected.course, expected.email);
        expected.updateStatus = UpdateStatus.UNKNOWN;
        expected.lastName = StringHelper.splitName(expected.name)[1];
        equalizeIrrelevantData(expected, actual);
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }

    private static void equalizeIrrelevantData(
            StudentAttributes expectedStudent,
            StudentAttributes actualStudent) {
        
        // For these fields, we consider null and "" equivalent.
        if (expectedStudent.googleId == null && actualStudent.googleId.isEmpty()) {
            expectedStudent.googleId = "";
        }
        if (expectedStudent.team == null && actualStudent.team.isEmpty()) {
            expectedStudent.team = "";
        }
        if (expectedStudent.comments == null && actualStudent.comments.isEmpty()) {
            expectedStudent.comments = "";
        }

        // pretend keys match because the key is generated on the server side
        // and cannot be anticipated
        if (actualStudent.key != null) {
            expectedStudent.key = actualStudent.key;
        }
    }
    
    @AfterTest
    public void testTearDown() {
        gaeSimulation.tearDown();
    }
}
