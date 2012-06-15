package teammates.testing.testcases;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.jsp.Helper;
import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;

/**
 * Tests coordEval.jsp from UI functionality and HTML test
 * @author Aldrian Obaja
 *
 */
public class CoordEvalResultsPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appUrl = Config.inst().TEAMMATES_URL.replaceAll("/(?=$)","");
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("CoordEvalResultsUITest");
		bi = BrowserInstancePool.getBrowserInstance();

		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/CoordEvalResultsUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		
		TMAPI.deleteCoordinators(jsonString);
		System.out.println("Importing test data...");
		long start = System.currentTimeMillis();
		System.out.println(TMAPI.persistNewDataBundle(jsonString));
		System.out.println("The test data was imported in "+(System.currentTimeMillis()-start)+" ms");
		
		bi.loginCoord(scn.coords.get("teammates.demo.coord").id, Config.inst().TEAMMATES_APP_PASSWD);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordEvalResultsUITest");
	}

	@Test
	public void testCoordEvalResultsPage() throws Exception{
		testCoordEvalResultsHTML();
		testCoordEvalResultsUiPaths();
		testCoordEvalResultsLinks();
	}

	public void testCoordEvalResultsHTML() throws Exception{
		String link = Common.PAGE_COORD_EVAL_RESULTS;
		link = Helper.addParam(link,Common.PARAM_COURSE_ID,scn.courses.get("CEvalUiT.CS1101").id);
		link = Helper.addParam(link,Common.PARAM_EVALUATION_NAME,scn.evaluations.get("First Eval").name);
		bi.goToUrl(appUrl+link);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordEvalResultsOpenEval.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordEvalResultsOpenEval.html");

		link = Common.PAGE_COORD_EVAL_RESULTS;
		link = Helper.addParam(link,Common.PARAM_COURSE_ID,scn.courses.get("CEvalUiT.CS1101").id);
		link = Helper.addParam(link,Common.PARAM_EVALUATION_NAME,scn.evaluations.get("Second Eval").name);
		bi.goToUrl(appUrl+link);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordEvalResultsPublishedEval.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordEvalResultsPublishedEval.html");
		
		link = Common.PAGE_COORD_EVAL_RESULTS;
		link = Helper.addParam(link,Common.PARAM_COURSE_ID,scn.courses.get("CEvalUiT.CS1101").id);
		link = Helper.addParam(link,Common.PARAM_EVALUATION_NAME,scn.evaluations.get("Third Eval").name);
		bi.goToUrl(appUrl+link);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordEvalResultsClosedEval.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordEvalResultsClosedEval.html");
		
	}

	// TODO: Finish Evaluation UI Path test
	public void testCoordEvalResultsUiPaths() throws Exception{
		
	}

	// TODO: Finish Evaluation Links test
	public void testCoordEvalResultsLinks() throws Exception{
		
	}
}