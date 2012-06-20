package teammates.testing.testcases;

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
		bi = BrowserInstancePool.getBrowserInstance();
		
		System.out.println("Recreating "+ts.coordinator.id);
		long start = System.currentTimeMillis();
		BackDoor.deleteCoord(ts.coordinator.id);
		BackDoor.createCoord(ts.coordinator);
		BackDoor.createEvaluation(ts.evaluation);
		System.out.println("Finished recreating in "+(System.currentTimeMillis()-start)+" ms");
		
		bi.loginCoord(ts.coordinator.id, Config.inst().TEAMMATES_APP_PASSWORD);
		String link = appUrl+Common.PAGE_COORD_EVAL_EDIT;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, ts.evaluation.course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, ts.evaluation.name);
		bi.goToUrl(link);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordEvalEditUITest");
	}

	@Test
	public void testCoordEvalEditPage() throws Exception{
		testCoordEvalEditHTML();
		testCoordEvalEditUiPaths();
		testCoordEvalEditLinks();
	}

	public void testCoordEvalEditHTML() throws Exception{
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordEvalEditNew.html");
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordEvalEditNew.html");
	}

	// TODO: Finish Evaluation Edit UI Path test
	public void testCoordEvalEditUiPaths() throws Exception{
		
	}

	// TODO: Finish Evaluation Edit Links test
	public void testCoordEvalEditLinks() throws Exception{
		
	}
	
	private static TestScenario loadTestScenario() throws JSONException, FileNotFoundException {
		String testScenarioJsonFile = Common.TEST_DATA_FOLDER + "/CoordEvalEditUiTest.json";
		String jsonString = Common.readFile(testScenarioJsonFile);
		TestScenario scn = Common.getTeammatesGson().fromJson(jsonString, TestScenario.class);
		return scn;
	}

	@SuppressWarnings("unused")
	private class TestScenario{
		public CoordData coordinator;
		public CourseData course;
		public EvaluationData evaluation;
		public EvaluationData evaluationInCourseWithNoTeams;
	}
}