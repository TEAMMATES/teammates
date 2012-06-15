package teammates.testing.testcases;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.jsp.Helper;
import teammates.jsp.StudentHomeHelper;
import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;

/**
 * Tests Student Evaluation Edit (submit) Page
 * @author Aldrian Obaja
 */
public class StudentEvalEditPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appURL = Config.inst().TEAMMATES_URL.replaceAll("/(?=$)","");

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("StudentEvalEditUITest");
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/StudentEvalEditUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		
		TMAPI.deleteCoordinators(jsonString);
		System.out.println("Importing test data...");
		long start = System.currentTimeMillis();
		System.out.println(TMAPI.persistNewDataBundle(jsonString));
		System.out.println("The test data was imported in "+(System.currentTimeMillis()-start)+" ms");
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginStudent(scn.students.get("alice.tmms@SEvalEditUiT.CS2104").id, Config.inst().TEAMMATES_APP_PASSWD);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter("StudentEvalEditUITest");
	}

	@Test	
	public void testStudentHomeCoursePageHTML() throws Exception{
		// Submitted evaluation
		String link = Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, scn.evaluations.get("SEvalEditUiT.CS2104:First Eval").course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("SEvalEditUiT.CS2104:First Eval").name);
		bi.goToUrl(appURL+link);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/StudentEvalEditSubmittedHTML.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/StudentEvalEditSubmittedHTML.html");

		// Pending evaluation
		link = Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, scn.evaluations.get("SEvalEditUiT.CS1101:Fifth Eval").course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("SEvalEditUiT.CS1101:Fifth Eval").name);
		bi.goToUrl(appURL+link);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/StudentEvalEditPendingHTML.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/StudentEvalEditPendingHTML.html");
	}
}