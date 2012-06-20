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
import teammates.jsp.Helper;
import teammates.testing.config.Config;
import teammates.testing.lib.BackDoor;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;

/**
 * Tests coordEvalEdit.jsp from functionality and UI
 * @author Aldrian Obaja
 *
 */
public class CoordEvalEditPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static TestScenario ts;
	
	private static String appUrl = Config.inst().TEAMMATES_URL.replaceAll("/(?=$)","");
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("CoordEvalEditUITest");
		ts = loadTestScenario();
		
		System.out.println("Recreating "+ts.coordinator.id);
		long start = System.currentTimeMillis();
		BackDoor.deleteCoord(ts.coordinator.id);
		BackDoor.createCoord(ts.coordinator);
		BackDoor.createCourse(ts.course);
		BackDoor.createEvaluation(ts.evaluation);
		System.out.println("Finished recreating in "+(System.currentTimeMillis()-start)+" ms");

		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginAdmin(Config.inst().TEAMMATES_ADMIN_ACCOUNT, Config.inst().TEAMMATES_ADMIN_PASSWORD);
		String link = appUrl+Common.PAGE_COORD_EVAL_EDIT;
		link = Helper.addParam(link,Common.PARAM_COURSE_ID,ts.evaluation.course);
		link = Helper.addParam(link,Common.PARAM_EVALUATION_NAME,ts.evaluation.name);
		link = Helper.addParam(link,Common.PARAM_USER_ID,ts.coordinator.id);
		bi.goToUrl(link);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordEvalEditUITest");
	}

	@Test
	public void testCoordEvalEditHTML() throws Exception{
		printTestCaseHeader("CoordEvalEditHTML");
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordEvalEdit.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordEvalEdit.html");
	}
	
	@Test
	public void testCoordEvalEditUiPaths() throws Exception{
		printTestCaseHeader("CoordEvalEditChange");
		bi.editEvaluation(ts.newEvaluation.startTime, ts.newEvaluation.endTime, ts.newEvaluation.p2pEnabled, ts.newEvaluation.instructions, ts.newEvaluation.gracePeriod);
		
		// Verify status message
		bi.waitForStatusMessage(Common.MESSAGE_EVALUATION_EDITED);
		
		// Verify data
		String json = BackDoor.getEvaluationAsJason(ts.newEvaluation.course, ts.newEvaluation.name);
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