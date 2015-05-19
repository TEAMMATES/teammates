package teammates.test.cases.common;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.TimeHelper;
import teammates.test.cases.BaseTestCase;

public class CommentAttributesTest extends BaseTestCase {
    private static String courseId;
    private static String giverEmail;
    private static CommentParticipantType recipientType;
    private static Set<String> recipients;
    private static Text commentText;
    private static Date createdAt;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        courseId = "test-course-id";
        giverEmail = "email from giver";
        recipientType = CommentParticipantType.PERSON;
        recipients = new HashSet<String>();
        recipients.add("recipient-1");
        recipients.add("recipient-2");
        recipients.add("recipient-3");
        commentText = new Text("test comment text");
        createdAt = TimeHelper.combineDateTime("09/05/2016", "1000");
    }
    
    @Test
    public void testBasicGetters() {
       CommentAttributes comment = new CommentAttributes(
                courseId,
                giverEmail,
                recipientType,
                recipients,
                createdAt,
                commentText
                );
        
        ______TS("get comment's attributes");
        
        assertEquals("test-course-id", comment.courseId);
        assertEquals("email from giver", comment.giverEmail);
        assertEquals(CommentParticipantType.PERSON, comment.recipientType);
        assertEquals(3, comment.recipients.size());
        assertTrue(comment.recipients.contains("recipient-2"));
        assertEquals("test comment text", comment.commentText.getValue());
        assertEquals(TimeHelper.combineDateTime("09/05/2016", "1000"), comment.createdAt);
    }
    
    @Test
    public void testValidate() {
        CommentAttributes comment = new CommentAttributes(
                null,
                null,
                null,
                null,
                null,
                null
                );
        
        ______TS("null parameter error messages");
        
        try{
            comment.getInvalidityInfo();
        } catch (AssertionError e) {
            ignoreExpectedException();
        }
        
        ______TS("invalid parameters error messages");
        String incorrectEmail = "incorrect-giver-email";
        
        comment = new CommentAttributes(
                "correct-courseId",
                incorrectEmail,
                null,
                recipients,
                createdAt,
                commentText
                );
        
        List<String> expectedErrorMessage = new ArrayList<String>();
        expectedErrorMessage.add(String.format(FieldValidator.EMAIL_ERROR_MESSAGE, incorrectEmail, FieldValidator.REASON_INCORRECT_FORMAT));
        expectedErrorMessage.add(String.format(FieldValidator.EMAIL_ERROR_MESSAGE, "recipient-1", FieldValidator.REASON_INCORRECT_FORMAT));
        expectedErrorMessage.add(String.format(FieldValidator.EMAIL_ERROR_MESSAGE, "recipient-3", FieldValidator.REASON_INCORRECT_FORMAT));
        expectedErrorMessage.add(String.format(FieldValidator.EMAIL_ERROR_MESSAGE, "recipient-2", FieldValidator.REASON_INCORRECT_FORMAT));
        
        List<String> errorMemssage = comment.getInvalidityInfo();
        assertEquals(4, errorMemssage.size());
        assertEquals(expectedErrorMessage.toString(), errorMemssage.toString());
    }
    
    @Test
    public void testSanitize() {
        String invalidRecipientId = "invalid-recipients-&-#-'-\\-/-\"";
        Set<String> recipientsToSanitize = new HashSet<String>();
        recipientsToSanitize.add(invalidRecipientId);
        
        CommentAttributes comment = new CommentAttributes(
                courseId,
                giverEmail,
                recipientType,
                recipientsToSanitize,
                createdAt,
                commentText
                );
        
        ______TS("Sanitize potentially harmful characters");
        
        comment.sanitizeForSaving();
        for(String recipientId : comment.recipients){
            assertEquals(Sanitizer.sanitizeForHtml(invalidRecipientId), recipientId);
        }
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
    }
}
