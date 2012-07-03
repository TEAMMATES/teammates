package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
import teammates.datatransfer.CoordData;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.EvaluationData;
import teammates.testing.lib.BackDoor;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.Config;
import teammates.ui.Helper;

/**
 * Tests coordEvalEdit.jsp from functionality and UI
 */
public class CoordEvalEditPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static TestScenario ts;
	
	private static String appUrl = Config.inst().TEAMMATES_URL;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		ts = loadTestScenario();
		BackDoor.deleteCoord(ts.coordinator.id);
		String backDoorOperationStatus = BackDoor.createCoord(ts.coordinator);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		backDoorOperationStatus = BackDoor.createCourse(ts.course);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		backDoorOperationStatus = BackDoor.createEvaluation(ts.evaluation);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();

		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginAdmin(Config.inst().TEST_ADMIN_ACCOUNT, Config.inst().TEST_ADMIN_PASSWORD);
		String link = appUrl+Common.PAGE_COORD_EVAL_EDIT;
		link = Helper.addParam(link,Common.PARAM_COURSE_ID,ts.evaluation.course);
		link = Helper.addParam(link,Common.PARAM_EVALUATION_NAME,ts.evaluation.name);
		link = Helper.addParam(link,Common.PARAM_USER_ID,ts.coordinator.id);
		bi.goToUrl(link);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}

	@Test
	public void testCoordEvalEditHTML() throws Exception{
		
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordEvalEdit.html");
	}
	
	@Test
	public void testCoordEvalEditUiPaths() throws Exception{
		
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
		String testScenarioJsonFile = Common.TEST_DATA_FOLDER + "/CoordEvalEditUiTest.json";
		String jsonString = Common.readFile(testScenarioJsonFile);
		TestScenario scn = Common.getTeammatesGson().fromJson(jsonString, TestScenario.class);
		return scn;
	}

	private class TestScenario{
		public CoordData coordinator;
		public CourseData course;
		public EvaluationData evaluation;
		public EvaluationData newEvaluation;
	}
}