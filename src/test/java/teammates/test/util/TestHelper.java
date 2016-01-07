package teammates.test.util;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertNull;

import java.lang.reflect.Method;

import org.testng.Assert;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributes.UpdateStatus;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.StringHelper;
import teammates.common.util.Utils;
import teammates.logic.api.Logic;
import teammates.storage.api.CommentsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;
import teammates.test.cases.BaseComponentTestCase;

import com.google.gson.Gson;

public class TestHelper extends BaseComponentTestCase{
    
    private static final Logic logic = new Logic();

    private static final CoursesDb coursesDb = new CoursesDb();
    private static final InstructorsDb instructorsDb = new InstructorsDb();
    private static final StudentsDb studentsDb = new StudentsDb();
    private static final FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
    private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
    private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();
    private static final CommentsDb commentsDb = new CommentsDb();
    private static final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();

    private static Gson gson = Utils.getTeammatesGson();

    public static void verifyAbsentInDatastore(AccountAttributes account)
            throws Exception {
        assertNull(logic.getAccount(account.googleId));
    }

    public static void verifyAbsentInDatastore(InstructorAttributes expectedInstructor) {
        assertNull(instructorsDb.getInstructorForGoogleId(expectedInstructor.courseId, expectedInstructor.googleId));
    }

    public static void verifyAbsentInDatastore(CourseAttributes course) {
        assertNull(coursesDb.getCourse(course.id));
    }

    public static void verifyAbsentInDatastore(StudentAttributes student) {
        assertNull(logic.getStudentForEmail(student.course, student.email));
    }
    
    public static void verifyAbsentInDatastore(FeedbackSessionAttributes fsa) {
        assertNull(fsDb.getFeedbackSession(fsa.courseId, fsa.feedbackSessionName));    
    }
    
    public static void verifyAbsentInDatastore(FeedbackQuestionAttributes fqa) {
        assertNull(fqDb.getFeedbackQuestion(fqa.feedbackSessionName, fqa.courseId, fqa.questionNumber));    
    }
    
    public static void verifyAbsentInDatastore(FeedbackResponseAttributes fra) {
        assertNull(frDb.getFeedbackResponse(fra.feedbackQuestionId, fra.giverEmail, fra.recipientEmail));
    }
    
    public static void verifyAbsentInDatastore(CommentAttributes comment) {
        assertNull(commentsDb.getComment(comment));
    }
    
    public static void verifyAbsentInDatastore(FeedbackResponseCommentAttributes frComment) {
        assertNull(frcDb.getFeedbackResponseComment(frComment.feedbackResponseId, 
                frComment.giverEmail, frComment.createdAt));
    }
    
    /**
     * Only checks if the entity exists
     * @param expectedAccount
     */
    public static void verifyPresentInDatastore(AccountAttributes expectedAccount) {
        AccountAttributes actualAccount = logic.getAccount(expectedAccount.googleId);
        // Account when created by createInstructor may take up different values in NAME and EMAIL
        // from the typicalDataBundle. Hence we only check that the account exists in the DataStore
        assertTrue(actualAccount != null);
    }

    public static void verifyPresentInDatastore(StudentAttributes expectedStudent) {
        StudentAttributes actualStudent = studentsDb.getStudentForEmail(expectedStudent.course,
                expectedStudent.email);
        expectedStudent.updateStatus = UpdateStatus.UNKNOWN;
        expectedStudent.lastName = StringHelper.splitName(expectedStudent.name)[1];
        equalizeIrrelevantData(expectedStudent, actualStudent);
        assertEquals(gson.toJson(expectedStudent), gson.toJson(actualStudent));
    }

    public static void verifyPresentInDatastore(CourseAttributes expected) {
        CourseAttributes actual = coursesDb.getCourse(expected.id);
        // Ignore time field as it is stamped at the time of creation in testing
        actual.createdAt = expected.createdAt;
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }

    public static void verifyPresentInDatastore(InstructorAttributes expected) {
        InstructorAttributes actual;
        
        if (expected.googleId != null) {
            actual = instructorsDb.getInstructorForGoogleId(expected.courseId, expected.googleId);
        } else {
            actual = instructorsDb.getInstructorForEmail(expected.courseId, expected.email);
        }
        equalizeIrrelevantData(expected, actual);
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }
    
    public static void verifyPresentInDatastore(FeedbackSessionAttributes expected) {
        FeedbackSessionAttributes actual = fsDb.getFeedbackSession(expected.courseId, expected.feedbackSessionName);
        expected.respondingInstructorList = actual.respondingInstructorList;
        expected.respondingStudentList = actual.respondingStudentList;
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }

    public static void verifyPresentInDatastore(FeedbackQuestionAttributes expected) {
        FeedbackQuestionAttributes actual = fqDb.getFeedbackQuestion(
                expected.feedbackSessionName, expected.courseId, expected.questionNumber);
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }
    
    public static void verifyPresentInDatastore(FeedbackQuestionAttributes expected, boolean wildcardId) {
        FeedbackQuestionAttributes actual = fqDb.getFeedbackQuestion(
                expected.feedbackSessionName, expected.courseId, expected.questionNumber);
        if(wildcardId){
            expected.setId(actual.getId());
        }
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }
    
    public static void verifyPresentInDatastore(FeedbackResponseAttributes expected) {
        FeedbackResponseAttributes actual = frDb.getFeedbackResponse(
                expected.feedbackQuestionId, expected.giverEmail, expected.recipientEmail);
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }
    
    public static void verifyPresentInDatastore(FeedbackResponseAttributes expected, boolean wildcardId) {
        FeedbackResponseAttributes actual = frDb.getFeedbackResponse(
                expected.feedbackQuestionId, expected.giverEmail, expected.recipientEmail);
        if(wildcardId){
            expected.setId(actual.getId());
        }
        assertEquals(gson.toJson(expected), gson.toJson(actual));
    }
    
    public static void verifyPresentInDatastore(CommentAttributes expected){
        CommentAttributes actual = commentsDb.getComment(expected);
        assertEquals(expected.courseId, actual.courseId);
        assertEquals(expected.giverEmail, actual.giverEmail);
        assertEquals(expected.recipients, actual.recipients);
        assertEquals(expected.commentText, actual.commentText);
    }
    
    public static void verifyPresentInDatastore(FeedbackResponseCommentAttributes expected){
        FeedbackResponseCommentAttributes actual = frcDb.getFeedbackResponseComment(expected.courseId, expected.createdAt,
                expected.giverEmail);
        assertEquals(expected.courseId, actual.courseId);
        assertEquals(expected.giverEmail, actual.giverEmail);
        assertEquals(expected.feedbackSessionName, actual.feedbackSessionName);
        assertEquals(expected.commentText, actual.commentText);
    }
   

    public static void verifyEntityDoesNotExistException(String methodName,
            Class<?>[] paramTypes, Object[] params) throws Exception {

        Method method = Logic.class.getDeclaredMethod(methodName, paramTypes);

        try {
            method.setAccessible(true); // in case it is a private method
            method.invoke(logic, params);
            Assert.fail();
        } catch (Exception e) {
            assertEquals(EntityDoesNotExistException.class, e.getCause()
                    .getClass());
        }
    }
    
    public static void equalizeIrrelevantData(
            StudentAttributes expectedStudent,
            StudentAttributes actualStudent) {
        
        // For these fields, we consider null and "" equivalent.
        if ((expectedStudent.googleId == null) && (actualStudent.googleId.equals(""))) {
            actualStudent.googleId = null;
        }
        if ((expectedStudent.team == null) && (actualStudent.team.equals(""))) {
            actualStudent.team = null;
        }
        if ((expectedStudent.comments == null)
                && (actualStudent.comments.equals(""))) {
            actualStudent.comments = null;
        }

        // pretend keys match because the key is generated on the server side
        // and cannot be anticipated
        if ((actualStudent.key != null)) {
            expectedStudent.key = actualStudent.key;
        }
    }
    
    public static void equalizeIrrelevantData(InstructorAttributes expectedInstructor,
            InstructorAttributes actualInstructor) {
        
        // pretend keys match because the key is generated only before storing into database
        if ((actualInstructor.key != null)) {
            expectedInstructor.key = actualInstructor.key;
        }
        if (!expectedInstructor.instructorPrivilegesAsText.equals(actualInstructor.instructorPrivilegesAsText)
                && expectedInstructor.privileges.equals(actualInstructor.privileges)) {
            actualInstructor.instructorPrivilegesAsText = expectedInstructor.getTextFromInstructorPrivileges();
        }
    }

    @SuppressWarnings("unused")
    private void ____invoking_private_methods__() {
    }


    @SuppressWarnings("unused")
    private void ____test_object_manipulation_methods__() {
    }

}
