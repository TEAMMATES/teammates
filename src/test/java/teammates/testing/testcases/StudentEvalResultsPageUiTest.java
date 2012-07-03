package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.testing.lib.BackDoor;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TestProperties;
import teammates.ui.Helper;

/**
 * Tests Student Evaluation Results page
 */
public class StudentEvalResultsPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/StudentEvalResultsUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCoordinators(jsonString);
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginAdmin(TestProperties.inst().TEST_ADMIN_ACCOUNT, TestProperties.inst().TEST_ADMIN_PASSWORD);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}

	@Test	
	public void testStudentEvalResultsPageHTML() throws Exception{
		
		
		______TS("typical case: more than two members");
		
		String link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Third Eval").course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Third Eval").name);
		link = Helper.addParam(link, Common.PARAM_USER_ID, scn.students.get("charlie.tmms").id);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsTypicalHTML.html");

		______TS("typical case: two members");
		
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Third Eval").course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Third Eval").name);
		link = Helper.addParam(link, Common.PARAM_USER_ID, scn.students.get("alice.tmms").id);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsTwoMembersTypicalHTML.html");

		______TS("extreme case: 1");
		
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Second Eval").course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Second Eval").name);
		link = Helper.addParam(link, Common.PARAM_USER_ID, scn.students.get("charlie.tmms").id);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsExtreme1HTML.html");

		______TS("extreme case: 2");
		
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Second Eval").course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Second Eval").name);
		link = Helper.addParam(link, Common.PARAM_USER_ID, scn.students.get("danny.tmms").id);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsExtreme2HTML.html");

		______TS("extreme case: 3");
		
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Second Eval").course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Second Eval").name);
		link = Helper.addParam(link, Common.PARAM_USER_ID, scn.students.get("emily.tmms").id);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsExtreme3HTML.html");

		______TS("student did not submitt");
		
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Second Eval").course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Second Eval").name);
		link = Helper.addParam(link, Common.PARAM_USER_ID, scn.students.get("alice.tmms").id);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsNotSubmittedHTML.html");

		______TS("teammates did not submit");
		
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Second Eval").course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Second Eval").name);
		link = Helper.addParam(link, Common.PARAM_USER_ID, scn.students.get("benny.tmms").id);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsTheOtherDidn'tSubmitHTML.html");
	}
}