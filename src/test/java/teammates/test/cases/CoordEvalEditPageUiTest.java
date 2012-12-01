package teammates.test.cases;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.common.Common;
import teammates.common.datatransfer.InstructorData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.EvaluationData;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.TestProperties;

/**
 * Tests instructorEvalEdit.jsp from functionality and UI
 */
public class InstructorEvalEditPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static TestScenario ts;
	
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		ts = loadTestScenario();
		BackDoor.deleteInstructor(ts.instructor.id);
		String backDoorOperationStatus = BackDoor.createInstructor(ts.instructor);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		backDoorOperationStatus = BackDoor.createCourse(ts.course);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		backDoorOperationStatus = BackDoor.createEvaluation(ts.evaluation);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();

		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginAdmin(TestProperties.inst().TEST_ADMIN_ACCOUNT, TestProperties.inst().TEST_ADMIN_PASSWORD);
		String link = appUrl+Common.PAGE_INSTRUCTOR_EVAL_EDIT;
		link = Common.addParamToUrl(link,Common.PARAM_COURSE_ID,ts.evaluation.course);
		link = Common.addParamToUrl(link,Common.PARAM_EVALUATION_NAME,ts.evaluation.name);
		link = Common.addParamToUrl(link,Common.PARAM_USER_ID,ts.instructor.id);
		bi.goToUrl(link);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}

	@Test
	public void testInstructorEvalEditHTML() throws Exception{
		
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorEvalEdit.html");
	}
	
	@Test
	public void testInstructorEvalEditUiPaths() throws Exception{
		
		bi.editEvaluation(ts.newEvaluation.startTime, ts.newEvaluation.endTime, ts.newEvaluation.p2pEnabled, ts.newEvaluation.instructions, ts.newEvaluation.gracePeriod);
		
		// Verify status message
		bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_EDITED);
		
		// Verify data
		String json = BackDoor.getEvaluationAsJson(ts.newEvaluation.course, ts.newEvaluation.name);
		EvaluationData newEval = Common.getTeammatesGson().fromJson(json, EvaluationData.class);
		assertEquals(ts.newEvaluation.startTime,newEval.startTime);
		assertEquals(ts.newEvaluation.endTime,newEval.endTime);
		assertEquals(ts.newEvaluation.instructions,newEval.instructions);
		assertEquals(ts.newEvaluation.timeZone+"",newEval.timeZone+"");
		assertEquals(ts.newEvaluation.gracePeriod,newEval.gracePeriod);
		assertEquals(ts.newEvaluation.p2pEnabled,newEval.p2pEnabled);
	}
	
	private static TestScenario loadTestScenario() throws JSONException, FileNotFoundException {
		String testScenarioJsonFile = Common.TEST_DATA_FOLDER + "/InstructorEvalEditUiTest.json";
		String jsonString = Common.readFile(testScenarioJsonFile);
		TestScenario scn = Common.getTeammatesGson().fromJson(jsonString, TestScenario.class);
		return scn;
	}

	private class TestScenario{
		public InstructorData instructor;
		public CourseData course;
		public EvaluationData evaluation;
		public EvaluationData newEvaluation;
	}
}