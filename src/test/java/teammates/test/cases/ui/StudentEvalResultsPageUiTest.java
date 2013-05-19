package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.TestProperties;

/**
 * Tests Student Evaluation Results page
 */
public class StudentEvalResultsPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;
	private static String jsonString;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/StudentEvalResultsUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCourses(jsonString);
		BackDoor.deleteInstructors(jsonString);
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

		// Always cleanup
		BackDoor.deleteCourses(jsonString);
	}

	@Test	
	public void testStudentEvalResultsPageHTML() throws Exception{
		
		
		______TS("typical case: more than two members");
		
		String link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Third Eval").course);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Third Eval").name);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, scn.students.get("SEvalRUiT.charlie.d").id);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsTypicalHTML.html");

		______TS("typical case: two members");
		
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Third Eval").course);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Third Eval").name);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, scn.students.get("SEvalRUiT.alice.b").id);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsTwoMembersTypicalHTML.html");

		______TS("extreme case: 1");
		
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Second Eval").course);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Second Eval").name);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, scn.students.get("SEvalRUiT.charlie.d").id);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsExtreme1HTML.html");

		______TS("extreme case: 2");
		
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Second Eval").course);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Second Eval").name);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, scn.students.get("SEvalRUiT.danny.e").id);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsExtreme2HTML.html");

		______TS("extreme case: 3");
		
		String studentId = scn.students.get("SEvalRUiT.emily.f").id;
		
		//recreate student account if it doesn't exist
		AccountAttributes testStudentAccount = new AccountAttributes(studentId, "Danny Tmms", false, "SEvalRUiT.emily.f@gmail.com", "National University of Singapore");
		String backDoorOperationStatus = BackDoor.createAccount(testStudentAccount);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Second Eval").course);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Second Eval").name);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, scn.students.get("SEvalRUiT.emily.f").id);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsExtreme3HTML.html");

		______TS("student did not submitt");
		
		studentId = scn.students.get("SEvalRUiT.alice.b").id;
		
		//recreate student account if it doesn't exist
		testStudentAccount = new AccountAttributes(studentId, "Alice Tmms", false, "SEvalRUiT.alice.b@gmail.com", "National University of Singapore");
		backDoorOperationStatus = BackDoor.createAccount(testStudentAccount);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
				
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Second Eval").course);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Second Eval").name);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, scn.students.get("SEvalRUiT.alice.b").id);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsNotSubmittedHTML.html");

		______TS("teammates did not submit");
		
		studentId = scn.students.get("SEvalRUiT.benny.c").id;
		
		//recreate student account if it doesn't exist
		testStudentAccount = new AccountAttributes(studentId, "Benny Tmms", false, "SEvalRUiT.benny.c@gmail.com", "National University of Singapore");
		backDoorOperationStatus = BackDoor.createAccount(testStudentAccount);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Second Eval").course);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Second Eval").name);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, scn.students.get("SEvalRUiT.benny.c").id);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsTheOtherDidn'tSubmitHTML.html");
		
		______TS("with p2pFeedback disabled");
		link = appUrl + Common.PAGE_STUDENT_EVAL_RESULTS;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.evaluations.get("P2P Disabled Eval").course);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("P2P Disabled Eval").name);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, scn.students.get("SEvalRUiT.benny.c").id);
		bi.goToUrl(link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/studentEvalResultsP2PDisabled.html");
	}
}