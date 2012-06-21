package teammates.testing.testcases;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.jsp.Helper;
import teammates.testing.config.Config;
import teammates.testing.lib.BackDoor;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;

/**
 * Tests Student Evaluation Results page
 * @author Aldrian Obaja
 */
public class StudentEvalResultsPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appUrl = Config.inst().TEAMMATES_URL;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("StudentEvalResultsUITest");
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/StudentEvalResultsUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		
		System.out.println("Importing test data...");
		long start = System.currentTimeMillis();
		BackDoor.deleteCoordinators(jsonString);
		System.out.println(BackDoor.persistNewDataBundle(jsonString));
		System.out.println("The test data was imported in "+(System.currentTimeMillis()-start)+" ms");
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginAdmin(Config.inst().TEAMMATES_ADMIN_ACCOUNT, Config.inst().TEAMMATES_ADMIN_PASSWORD);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter("StudentEvalResultsUITest");
	}

	@Test	
	public void testStudentEvalResultsPageHTML() throws Exception{
		printTestCaseHeader("StudentEvalResultsTypical");
		String link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Third Eval").course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Third Eval").name);
		link = Helper.addParam(link, Common.PARAM_USER_ID, scn.students.get("charlie.tmms").id);
		bi.goToUrl(link);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/studentEvalResultsTypicalHTML.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsTypicalHTML.html");

		printTestCaseHeader("StudentEvalResultsTwoMembersTypical");
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Third Eval").course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Third Eval").name);
		link = Helper.addParam(link, Common.PARAM_USER_ID, scn.students.get("alice.tmms").id);
		bi.goToUrl(link);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/studentEvalResultsTwoMembersTypicalHTML.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsTwoMembersTypicalHTML.html");

		printTestCaseHeader("StudentEvalResultsExtreme1");
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Second Eval").course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Second Eval").name);
		link = Helper.addParam(link, Common.PARAM_USER_ID, scn.students.get("charlie.tmms").id);
		bi.goToUrl(link);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/studentEvalResultsExtreme1HTML.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsExtreme1HTML.html");

		printTestCaseHeader("StudentEvalResultsExtreme2");
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Second Eval").course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Second Eval").name);
		link = Helper.addParam(link, Common.PARAM_USER_ID, scn.students.get("danny.tmms").id);
		bi.goToUrl(link);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/studentEvalResultsExtreme2HTML.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsExtreme2HTML.html");

		printTestCaseHeader("StudentEvalResultsExtreme3");
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Second Eval").course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Second Eval").name);
		link = Helper.addParam(link, Common.PARAM_USER_ID, scn.students.get("emily.tmms").id);
		bi.goToUrl(link);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/studentEvalResultsExtreme3HTML.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsExtreme3HTML.html");

		printTestCaseHeader("StudentEvalResultsNotSubmitted");
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Second Eval").course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Second Eval").name);
		link = Helper.addParam(link, Common.PARAM_USER_ID, scn.students.get("alice.tmms").id);
		bi.goToUrl(link);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/studentEvalResultsNotSubmittedHTML.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsNotSubmittedHTML.html");

		printTestCaseHeader("StudentEvalResultsTheOtherDidn'tSubmit");
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Second Eval").course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Second Eval").name);
		link = Helper.addParam(link, Common.PARAM_USER_ID, scn.students.get("benny.tmms").id);
		bi.goToUrl(link);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/studentEvalResultsTheOtherDidn'tSubmitHTML.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsTheOtherDidn'tSubmitHTML.html");
	}
}