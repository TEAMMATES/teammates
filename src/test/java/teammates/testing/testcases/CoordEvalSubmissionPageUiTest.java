package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.SubmissionData;
import teammates.testing.config.Config;
import teammates.testing.lib.BackDoor;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.ui.Helper;

import com.google.appengine.api.datastore.Text;

/**
 * Tests coordEvalSubmissionView.jsp and coordEvalSubmissionEdit.jsp
 */
public class CoordEvalSubmissionPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appUrl = Config.inst().TEAMMATES_URL;
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/CoordEvalSubmissionUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCoordinators(jsonString);
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();

		bi = BrowserInstancePool.getBrowserInstance();

		bi.loginAdmin(Config.inst().TEST_ADMIN_ACCOUNT, Config.inst().TEST_ADMIN_PASSWORD);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}
	
	@Test
	public void testCoordEvalSubmissionViewAndEdit() throws Exception{
		
		//Checking indirect link to View Submission through Evaluation Results
		EvaluationData eval = scn.evaluations.get("First Eval");
		
		______TS("view submissions for open evaluation, from results page");
		
		String link = appUrl+Common.PAGE_COORD_EVAL_RESULTS;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, eval.course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, eval.name);
		link = Helper.addParam(link, Common.PARAM_USER_ID, scn.coords.get("teammates.demo.coord").id);
		bi.goToUrl(link);
		
		int studentResultsRowID = bi.getStudentRowId(scn.students.get("Charlie").name);
		bi.click(bi.getReviewerSummaryView(studentResultsRowID));
		
		String current = bi.getDriver().getWindowHandle();
		for(String s: bi.getDriver().getWindowHandles()){
			if(!current.equals(s)){
				bi.getSelenium().selectWindow(s);
				break;
			}
		}
		
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordEvalSubmissionView.html");
		
		______TS("view submission for closed evaluation, using direct URL");
		
		link = appUrl+Common.PAGE_COORD_EVAL_SUBMISSION_VIEW;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, eval.course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, eval.name);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL, scn.students.get("Charlie").email);
		link = Helper.addParam(link, Common.PARAM_USER_ID, scn.coords.get("teammates.demo.coord").id);
		bi.goToUrl(link);
		
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordEvalSubmissionView.html");
		
		bi.click(By.id("button_edit"));

		______TS("editing submissions");

		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordEvalSubmissionEdit.html");
		
		SubmissionData[] subs = new SubmissionData[3];
		subs[0] = scn.submissions.get("CharlieCharlie");
		subs[1] = scn.submissions.get("CharlieDanny");
		subs[2] = scn.submissions.get("CharlieEmily");
		for(int i=0; i<3; i++){
			subs[i].points-=10;
			subs[i].justification = new Text(subs[i].justification.getValue()+"(edited)");
			subs[i].p2pFeedback= new Text(subs[i].p2pFeedback.getValue()+"(edited)");
		}
		int charlieEditRowID = bi.getStudentRowIdInEditSubmission(scn.students.get("Charlie").name);
		bi.setSubmissionPoint(charlieEditRowID, subs[0].points+"");
		bi.setSubmissionJustification(charlieEditRowID, subs[0].justification.getValue());
		bi.setSubmissionComments(charlieEditRowID, subs[0].p2pFeedback.getValue());
		
		int dannyEditRowID = bi.getStudentRowIdInEditSubmission(scn.students.get("Danny").name);
		bi.setSubmissionPoint(dannyEditRowID, subs[1].points+"");
		bi.setSubmissionJustification(dannyEditRowID, subs[1].justification.getValue());
		bi.setSubmissionComments(dannyEditRowID, subs[1].p2pFeedback.getValue());
		
		int emilyEditRowID = bi.getStudentRowIdInEditSubmission(scn.students.get("Emily").name);
		bi.setSubmissionPoint(emilyEditRowID, subs[2].points+"");
		bi.setSubmissionJustification(emilyEditRowID, subs[2].justification.getValue());
		bi.setSubmissionComments(emilyEditRowID, subs[2].p2pFeedback.getValue());
		
		bi.click(By.id("button_submit"));
		
		String charlieEmail = scn.students.get("Charlie").email;
		String dannyEmail = scn.students.get("Danny").email;
		String emilyEmail = scn.students.get("Emily").email;
		
		print("Checking status message");
		bi.getSelenium().selectWindow("null");
		bi.waitForStatusMessage(String.format(Common.MESSAGE_COORD_EVALUATION_SUBMISSION_RECEIVED,scn.students.get("Charlie").name,eval.name,eval.course).replace("<br />", "\n"));

		print("Checking modified data");
		String json = "";
		json = BackDoor.getSubmissionAsJson(eval.course, eval.name, charlieEmail, charlieEmail);
		SubmissionData charlieModified = Common.getTeammatesGson().fromJson(json, SubmissionData.class);
		assertEquals(subs[0].points+"",charlieModified.points+"");
		assertEquals(subs[0].justification.getValue(),charlieModified.justification.getValue());
		assertEquals(subs[0].p2pFeedback.getValue(),charlieModified.p2pFeedback.getValue());

		json = BackDoor.getSubmissionAsJson(eval.course, eval.name, charlieEmail, dannyEmail);
		SubmissionData dannyModified = Common.getTeammatesGson().fromJson(json, SubmissionData.class);
		assertEquals(subs[1].points+"",dannyModified.points+"");
		assertEquals(subs[1].justification.getValue(),dannyModified.justification.getValue());
		assertEquals(subs[1].p2pFeedback.getValue(),dannyModified.p2pFeedback.getValue());

		json = BackDoor.getSubmissionAsJson(eval.course, eval.name, charlieEmail, emilyEmail);
		SubmissionData emilyModified = Common.getTeammatesGson().fromJson(json, SubmissionData.class);
		assertEquals(subs[2].points+"",emilyModified.points+"");
		assertEquals(subs[2].justification.getValue(),emilyModified.justification.getValue());
		assertEquals(subs[2].p2pFeedback.getValue(),emilyModified.p2pFeedback.getValue());
		
	}
}