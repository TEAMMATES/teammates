package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static teammates.common.FieldValidator.END_TIME_FIELD_NAME;
import static teammates.common.FieldValidator.EVALUATION_NAME;
import static teammates.common.FieldValidator.START_TIME_FIELD_NAME;
import static teammates.common.FieldValidator.TIME_FRAME_ERROR_MESSAGE;

import java.util.Date;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.EvaluationsDb;
import teammates.test.cases.BaseComponentTest;

public class EvaluationsDbTest extends BaseComponentTest {
	
	//TODO: add missing test cases, refine existing ones. Follow the example
	//  of CoursesDbTest::testCreateCourse().

	private EvaluationsDb evaluationsDb = new EvaluationsDb();
	
	@BeforeClass
	public static void setupClass() throws Exception {
		printTestClassHeader();
		turnLoggingUp(EvaluationsDb.class);
	}

	@Test
	public void testCreateEvaluation() throws EntityAlreadyExistsException, InvalidParametersException {
		// SUCCESS
		EvaluationAttributes e = new EvaluationAttributes();
		e.courseId = "Computing101";
		e.name = "Very First Evaluation";
		e.startTime = new Date();
		e.endTime = new Date();
		evaluationsDb.createEntity(e);
		
		// SUCCESS even if keyword 'group' appears in the middle of the name (see Issue 380)
		e = new EvaluationAttributes();
		e.courseId = "Computing102";
		e.name = "text group text";
		e.startTime = new Date();
		e.endTime = new Date();
		evaluationsDb.createEntity(e);
		
		// FAIL : duplicate
		try {
			evaluationsDb.createEntity(e);
			signalFailureToDetectException();
		} catch (EntityAlreadyExistsException ex) {
			assertContains(String.format(EvaluationsDb.ERROR_CREATE_ENTITY_ALREADY_EXISTS, e.getEntityTypeAsString())
					+ e.getIdentificationString(), ex.getMessage());
		}
		
		// FAIL : invalid params
		e.startTime = null;
		try {
			evaluationsDb.createEntity(e);
			signalFailureToDetectException();
		} catch (AssertionError e1) {
			ignoreExpectedException();
		}
	
		
		// Null params check:
		try {
			evaluationsDb.createEntity(null);
			signalFailureToDetectException();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testGetEvaluation() throws InvalidParametersException {
		EvaluationAttributes e = createNewEvaluation();
		
		// Get existent
		EvaluationAttributes retrieved = evaluationsDb.getEvaluation(e.courseId, e.name);
		AssertJUnit.assertNotNull(retrieved);
		
		// Get non-existent - just return null
		retrieved = evaluationsDb.getEvaluation("non-existent-course", "Non existent Evaluation");
		AssertJUnit.assertNull(retrieved);
		
		// Null params check:
		try {
			evaluationsDb.getEvaluation(e.courseId, null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testUpdateEvaluation() throws EntityDoesNotExistException, InvalidParametersException {
		EvaluationAttributes e = createNewEvaluation();
				
		______TS("typical");
		
		e.instructions = "Foo Bar";
		evaluationsDb.updateEvaluation(e);
		
		______TS("invalid parameters");
		
		e.startTime = Common.getDateOffsetToCurrentTime(1);
		e.endTime = Common.getDateOffsetToCurrentTime(-1);
		try {
			evaluationsDb.updateEvaluation(e);
			Assert.fail();
		} catch (InvalidParametersException i) {
			String errorMessage = String.format(TIME_FRAME_ERROR_MESSAGE,
					START_TIME_FIELD_NAME, EVALUATION_NAME, END_TIME_FIELD_NAME) ;
			assertContains(errorMessage, i.getMessage());
		}
				
		______TS("non existent");
		
		e.name = "Non existent Evaluation";
		e.startTime = Common.getDateOffsetToCurrentTime(-1);
		e.endTime = Common.getDateOffsetToCurrentTime(1);
		try {
			evaluationsDb.updateEvaluation(e);
			Assert.fail();
		} catch (EntityDoesNotExistException a) {
			assertContains(EvaluationsDb.ERROR_UPDATE_NON_EXISTENT, a.getMessage());
		}
		
		______TS("null parameters");
		
		try {
			evaluationsDb.updateEvaluation(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		
	}
	
	@Test
	public void testDeleteEvaluation() throws InvalidParametersException {
		EvaluationAttributes e = createNewEvaluation();
		
		// Delete
		evaluationsDb.deleteEvaluation(e.courseId, e.name);
		
		EvaluationAttributes deleted = evaluationsDb.getEvaluation(e.courseId, e.name);
		AssertJUnit.assertNull(deleted);
		
		// delete again - should fail silently
		evaluationsDb.deleteEvaluation(e.courseId, e.name);
		
		// Null params check:
		try {
			evaluationsDb.deleteEvaluation(null, e.name);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
		}
		
		try {
			evaluationsDb.deleteEvaluation(e.courseId, null);
			Assert.fail();
		} catch (AssertionError a) {
			AssertJUnit.assertEquals(Common.ERROR_DBLEVEL_NULL_INPUT, a.getMessage());
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
		e.instructions = "typical instructions";
		e.startTime = Common.getDateOffsetToCurrentTime(-1);
		e.endTime = Common.getDateOffsetToCurrentTime(1);
		e.p2pEnabled = true;
		e.activated = true;
		e.published = false;
		return e;
	}

	private EvaluationAttributes createNewEvaluation() throws InvalidParametersException {
		EvaluationAttributes e = new EvaluationAttributes();
		e.courseId = "Computing101";
		e.name = "Basic Computing Evaluation1";
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
