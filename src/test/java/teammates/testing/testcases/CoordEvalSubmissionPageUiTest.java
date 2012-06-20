package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.SubmissionData;
import teammates.jsp.Helper;
import teammates.testing.config.Config;
import teammates.testing.lib.BackDoor;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;

import com.google.appengine.api.datastore.Text;

/**
 * Tests coordEvalSubmissionEdit.jsp from functionality and UI
 * @author Aldrian Obaja
 *
 */
public class CoordEvalSubmissionPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appUrl = Config.inst().TEAMMATES_URL.replaceAll("/(?=$)","");
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("CoordEvalSubmissionViewAndEditUITest");
		
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/CoordEvalSubmissionUiTest.json");
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
		printTestClassFooter("CoordEvalSubmissionViewAndEditUITest");
	}
	
	@Test
	public void testCoordEvalSubmissionViewAndEdit() throws Exception{
		printTestCaseHeader("TestCoordEvalSubmissionViewOpen");
		EvaluationData eval = scn.evaluations.get("First Eval");
		// Indirect link to View Submission through Evaluation Results
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
		
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordEvalSubmissionViewOpen.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordEvalSubmissionViewOpen.html");
		
		eval.endTime = new Date(new Date().getTime()-24*60*60*1000);
		BackDoor.editEvaluation(eval);

		printTestCaseHeader("TestCoordEvalSubmissionViewClosed");
		
		link = appUrl+Common.PAGE_COORD_EVAL_SUBMISSION_VIEW;
		link = Helper.addParam(link, Common.PARAM_COURSE_ID, eval.course);
		link = Helper.addParam(link, Common.PARAM_EVALUATION_NAME, eval.name);
		link = Helper.addParam(link, Common.PARAM_STUDENT_EMAIL, scn.students.get("Charlie").email);
		link = Helper.addParam(link, Common.PARAM_USER_ID, scn.coords.get("teammates.demo.coord").id);
		bi.goToUrl(link);
		
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordEvalSubmissionViewClosed.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordEvalSubmissionViewClosed.html");
		
		bi.click(By.id("button_edit"));

		printTestCaseHeader("TestCoordEvalSubmissionEdit");
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordEvalSubmissionEdit.html");
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
		
		System.out.println("Checking status message");
		bi.getSelenium().selectWindow("null");
		bi.waitForStatusMessage(String.format(Common.MESSAGE_COORD_EVALUATION_SUBMISSION_RECEIVED,scn.students.get("Charlie").name,eval.name,eval.course).replace("<br />", "\n"));

		System.out.println("Checking modified data");
		String json = "";
		json = BackDoor.getSubmissionAsJason(eval.course, eval.name, charlieEmail, charlieEmail);
		SubmissionData charlieModified = Common.getTeammatesGson().fromJson(json, SubmissionData.class);
		assertEquals(subs[0].points+"",charlieModified.points+"");
		assertEquals(subs[0].justification.getValue(),charlieModified.justification.getValue());
		assertEquals(subs[0].p2pFeedback.getValue(),charlieModified.p2pFeedback.getValue());

		json = BackDoor.getSubmissionAsJason(eval.course, eval.name, charlieEmail, dannyEmail);
		SubmissionData dannyModified = Common.getTeammatesGson().fromJson(json, SubmissionData.class);
		assertEquals(subs[1].points+"",dannyModified.points+"");
		assertEquals(subs[1].justification.getValue(),dannyModified.justification.getValue());
		assertEquals(subs[1].p2pFeedback.getValue(),dannyModified.p2pFeedback.getValue());

		json = BackDoor.getSubmissionAsJason(eval.course, eval.name, charlieEmail, emilyEmail);
		SubmissionData emilyModified = Common.getTeammatesGson().fromJson(json, SubmissionData.class);
		assertEquals(subs[2].points+"",emilyModified.points+"");
		assertEquals(subs[2].justification.getValue(),emilyModified.justification.getValue());
		assertEquals(subs[2].p2pFeedback.getValue(),emilyModified.p2pFeedback.getValue());
		
	}
}