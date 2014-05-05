package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static teammates.common.util.FieldValidator.END_TIME_FIELD_NAME;
import static teammates.common.util.FieldValidator.EVALUATION_NAME;
import static teammates.common.util.FieldValidator.START_TIME_FIELD_NAME;
import static teammates.common.util.FieldValidator.TIME_FRAME_ERROR_MESSAGE;

import java.util.Date;
import java.util.List;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.storage.api.EvaluationsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

public class EvaluationsDbTest extends BaseComponentTestCase {

    private EvaluationsDb evaluationsDb = new EvaluationsDb();
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(EvaluationsDb.class);
    }

    @Test
    public void testCreateEvaluation() throws EntityAlreadyExistsException, InvalidParametersException {
        
        ______TS("typical: create evaluation with valid attributes");
        EvaluationAttributes e = new EvaluationAttributes();
        e.courseId = "Computing101";
        e.name = "Very First Evaluation";
        e.instructions = new Text("Instruction to students");
        e.startTime = new Date();
        e.endTime = new Date();
        evaluationsDb.createEntity(e);
        
        ______TS("success: confirm sanitization of fields while creating an evaluation");
        e.courseId = "  Computing101  ";
        e.name = "\tSecond Evaluation\t";
        e.instructions = new Text("\t\ntypical instructions\t\n");
        e.startTime = new Date();
        e.endTime = new Date();
        evaluationsDb.createEntity(e);
        
        e = evaluationsDb.getEvaluation("Computing101", "Second Evaluation");
        assertEquals("typical instructions",e.instructions.getValue());
        
        ______TS("success: create evaluation even if keyword 'group' appears in the middle of the name (see Issue 380");
        e = new EvaluationAttributes();
        e.courseId = "Computing102";
        e.instructions = new Text("instructions");
        e.name = "text group text";
        e.startTime = new Date();
        e.endTime = new Date();
        evaluationsDb.createEntity(e);
        
        ______TS("fail: create duplicate evaluation");
        try {
            evaluationsDb.createEntity(e);
            signalFailureToDetectException();
        } catch (EntityAlreadyExistsException ex) {
            AssertHelper.assertContains(String.format(EvaluationsDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, e.getEntityTypeAsString())
                    + e.getIdentificationString(), ex.getMessage());
        }
        
        ______TS("fail: create evaluation with invalid parameters");
        e.startTime = null;
        try {
            evaluationsDb.createEntity(e);
            signalFailureToDetectException();
        } catch (AssertionError e1) {
            ignoreExpectedException();
        }
    
        
        ______TS("fail: create evaluation with null params");
        try {
            evaluationsDb.createEntity(null);
            signalFailureToDetectException();
        } catch (AssertionError a) {
            AssertJUnit.assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }
    
    @Test
    public void testGetEvaluation() throws InvalidParametersException {
        EvaluationAttributes e = createNewEvaluation();
        
        ______TS("typical: retrieve existing evaluation");
        EvaluationAttributes retrieved = evaluationsDb.getEvaluation(e.courseId, e.name);
        AssertJUnit.assertNotNull(retrieved);
        
        ______TS("typical: retrieve multiple evaluations of a course");
        List<EvaluationAttributes> retrievedList = evaluationsDb.getEvaluationsForCourse("Computing101");
        AssertJUnit.assertEquals(retrievedList.get(0).name, "Very First Evaluation");
        AssertJUnit.assertEquals(retrievedList.get(1).name, "Second Evaluation");
        AssertJUnit.assertEquals(retrievedList.size(), 2);
        
        ______TS("fail: retrieve non-existing evaluation");
        retrieved = evaluationsDb.getEvaluation("non-existent-course", "Non existent Evaluation");
        AssertJUnit.assertNull(retrieved);
        
        ______TS("fail: retrieve existing evaluation with null params");
        try {
            evaluationsDb.getEvaluation(e.courseId, null);
            Assert.fail();
        } catch (AssertionError a) {
            AssertJUnit.assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }
    
    @Test
    public void testUpdateEvaluation() throws EntityDoesNotExistException, InvalidParametersException {
        EvaluationAttributes e = createNewEvaluation();
                
        ______TS("typical: update an evaluation");
        e.instructions = new Text("Foo Bar");
        evaluationsDb.updateEvaluation(e);
        
        ______TS("fail: invalid parameters");
        
        e.startTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        try {
            evaluationsDb.updateEvaluation(e);
            Assert.fail();
        } catch (InvalidParametersException i) {
            String errorMessage = String.format(TIME_FRAME_ERROR_MESSAGE,
                    END_TIME_FIELD_NAME, EVALUATION_NAME, START_TIME_FIELD_NAME) ;
            AssertHelper.assertContains(errorMessage, i.getMessage());
        }
        
        ______TS("success: confirm sanitization during update of an evaluation");
        e.instructions = new Text("\t\n New Instructions\t \n ");
        e.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        evaluationsDb.updateEvaluation(e);
        
        e = evaluationsDb.getEvaluation(e.courseId, e.name);
        assertEquals("New Instructions", e.instructions.getValue());
        
        ______TS("fail: attempt to update a non-existent evaluation");
        
        e.name = "Non existent Evaluation";
        e.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        try {
            evaluationsDb.updateEvaluation(e);
            Assert.fail();
        } catch (EntityDoesNotExistException a) {
            AssertHelper.assertContains(EvaluationsDb.ERROR_UPDATE_NON_EXISTENT, a.getMessage());
        }
        
        ______TS("fail: attempt to update by passing null parameters");
        
        try {
            evaluationsDb.updateEvaluation(null);
            Assert.fail();
        } catch (AssertionError a) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }
    
    @Test
    public void testDeleteEvaluation() throws InvalidParametersException {
        EvaluationAttributes e = createNewEvaluation();
        
        ______TS("typical: delete existing evaluation");
        evaluationsDb.deleteEvaluation(e.courseId, e.name);
        
        EvaluationAttributes deleted = evaluationsDb.getEvaluation(e.courseId, e.name);
        AssertJUnit.assertNull(deleted);
        
        ______TS("fail: delete an already deleted(now non-existing) evaluation");
        evaluationsDb.deleteEvaluation(e.courseId, e.name);
        
        ______TS("success: delete all evaluations belonging to a course");
        //Create first evaluation
        e = createNewEvaluation();
        
        //Create second evaluation for same course by changing name
        e.name = "Second Evaluation";
        
        try {
            evaluationsDb.createEntity(e);
        } catch (EntityAlreadyExistsException e1) {
            Assert.fail();
        }
        
        List<EvaluationAttributes> retrievedList = evaluationsDb.getEvaluationsForCourse(e.courseId);
        assertEquals(retrievedList.size(),2);
        
        evaluationsDb.deleteAllEvaluationsForCourse(e.courseId);
        
        retrievedList = evaluationsDb.getEvaluationsForCourse(e.courseId);
        assertEquals(retrievedList.size(),0);
        
        
        ______TS("fail: delete an evaluation by passing null params");
        try {
            evaluationsDb.deleteEvaluation(null, e.name);
            Assert.fail();
        } catch (AssertionError a) {
            AssertJUnit.assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
        
        try {
            evaluationsDb.deleteEvaluation(e.courseId, null);
            Assert.fail();
        } catch (AssertionError a) {
            AssertJUnit.assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
        }
    }

    
    /**
     * @return An evaluation with typical data, in OPEN state.
     */
    public static EvaluationAttributes generateTypicalEvaluation(){
        EvaluationAttributes e = new EvaluationAttributes();
        e.courseId = "typical-course-id";
        e.name = "Typical Evaluation Name";
        e.timeZone = 0.0;
        e.gracePeriod = 0;
        e.instructions = new Text("typical instructions");
        e.startTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        e.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        e.p2pEnabled = true;
        e.activated = true;
        e.published = false;
        return e;
    }

    private EvaluationAttributes createNewEvaluation() throws InvalidParametersException {
        EvaluationAttributes e = new EvaluationAttributes();
        e.courseId = "Computing104";
        e.name = "Basic Computing Evaluation1";
        e.instructions = new Text("Instructions to student.");
        e.startTime = new Date();
        e.endTime = new Date();
        
        try {
            evaluationsDb.createEntity(e);
        } catch (EntityAlreadyExistsException ex) {
            // Okay if it's already inside
        }
        
        return e;
    }
}
