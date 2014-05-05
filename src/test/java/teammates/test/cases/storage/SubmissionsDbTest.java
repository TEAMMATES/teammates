package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static teammates.common.util.FieldValidator.EMAIL_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.api.SubmissionsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

import com.google.appengine.api.datastore.Text;

public class SubmissionsDbTest extends BaseComponentTestCase {
    
    //TODO: add missing test cases, refine existing ones. Follow the example
    //  of CoursesDbTest::testCreateCourse().

    private SubmissionsDb submissionsDb = new SubmissionsDb();
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(SubmissionsDb.class);
    }

    @Test
    public void testCreateSubmission() throws EntityAlreadyExistsException, InvalidParametersException {
        // SUCCESS
        SubmissionAttributes s = new SubmissionAttributes();
        s.course = "Computing101";
        s.evaluation = "Very First Evaluation";
        s.team = "team1";
        s.reviewee = "student1@gmail.com";
        s.reviewer = "student2@gmail.com";
        s.p2pFeedback = new Text("");
        s.justification = new Text("");
        
        submissionsDb.createEntity(s);
        
        // SUCCESS even if keyword 'group' appears in the middle of the name (see Issue 380) 
        s = new SubmissionAttributes();
        s.course = "Computing102";
        s.evaluation = "text group text";
        s.team = "team2";
        s.reviewee = "student1@gmail.com";
        s.reviewer = "student2@gmail.com";
        s.p2pFeedback = new Text("");
        s.justification = new Text("");
        submissionsDb.createEntity(s);
            
        // FAIL : duplicate
        try {
            submissionsDb.createEntity(s);
            Assert.fail();
        } catch (EntityAlreadyExistsException e) {
            AssertHelper.assertContains(String.format(SubmissionsDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, s.getEntityTypeAsString())
                    + s.getIdentificationString(), e.getMessage());
        }
        
        // FAIL : invalid params
        s.reviewer = "invalid.email";
        try {
            submissionsDb.createEntity(s);
            Assert.fail();
        } catch (InvalidParametersException a) {
            AssertHelper.assertContains(
                    String.format("Invalid email address for the student giving the evaluation: "+ EMAIL_ERROR_MESSAGE, s.reviewer, REASON_INCORRECT_FORMAT),
                    a.getMessage());
        } 
        
        // Null params check:
        try {
            submissionsDb.createEntity(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }
    
    @Test
    public void testGetSubmission() throws InvalidParametersException {
        SubmissionAttributes s = createNewSubmission();
        
        // Get existent
        SubmissionAttributes retrieved = submissionsDb.getSubmission(s.course,
                                                                s.evaluation,
                                                                s.reviewee,
                                                                s.reviewer);
        assertNotNull(retrieved);
        
        // Get non-existent - just return null
        retrieved = submissionsDb.getSubmission(s.course,
                                                s.evaluation,
                                                "dovahkiin@skyrim.com",
                                                s.reviewer);
        assertNull(retrieved);
        
        // Null params check:
        try {
            submissionsDb.getSubmission(null, s.evaluation, s.reviewee, s.reviewer);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
        try {
            submissionsDb.getSubmission(s.course, null, s.reviewee, s.reviewer);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
        try {
            submissionsDb.getSubmission(s.course, s.evaluation, null, s.reviewer);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
        try {
            submissionsDb.getSubmission(s.course, s.evaluation, s.reviewee, null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }
    
    @Test
    public void testEditSubmission() throws Exception {
        SubmissionAttributes s = createNewSubmission();
        
        // Edit existent
        s.justification = new Text("Hello World");
        submissionsDb.updateSubmission(s);
        
        // Edit non-existent
        s.reviewer = "non@existent.email";
        try {
            submissionsDb.updateSubmission(s);
            Assert.fail();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains(SubmissionsDb.ERROR_UPDATE_NON_EXISTENT, e.getMessage());
        }
        
        // Null params check:
        try {
            submissionsDb.updateSubmission(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }
    
    @Test
    public void testDeleteSubmission() throws InvalidParametersException {
        SubmissionAttributes s = createNewSubmission();
        
        // Delete
        submissionsDb.deleteAllSubmissionsForCourse(s.course);
        
        SubmissionAttributes deleted = submissionsDb.getSubmission(s.course,
                                                                s.evaluation,
                                                                s.reviewee,
                                                                s.reviewer);
        assertNull(deleted);
        
        // delete again - should fail silently
        submissionsDb.deleteAllSubmissionsForEvaluation(s.course, s.evaluation);
        
        // Null params check:
        try {
            submissionsDb.deleteAllSubmissionsForEvaluation(null, s.evaluation);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
        try {
            submissionsDb.deleteAllSubmissionsForEvaluation(s.course, null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }

    
    private SubmissionAttributes createNewSubmission() throws InvalidParametersException {
        SubmissionAttributes s = new SubmissionAttributes();
        s.course = "Computing101";
        s.evaluation = "Basic Computing Evaluation1";
        s.team = "team1";
        s.reviewee = "student1@gmail.com";
        s.reviewer = "student2@gmail.com";
        s.p2pFeedback = new Text("");
        s.justification = new Text("");
        
        try {
            submissionsDb.createEntity(s);
        } catch (EntityAlreadyExistsException e) {
            // Okay if it's already inside
        }
        
        return s;
    }
}
