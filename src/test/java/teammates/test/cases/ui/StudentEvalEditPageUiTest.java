package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.openqa.selenium.By;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.TestProperties;

import com.google.appengine.api.datastore.Text;

/**
 * Tests Student Evaluation Edit (submit) Page
 */
public class StudentEvalEditPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appUrl = TestProperties.inst().TEAMMATES_URL;
	private static String jsonString;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/StudentEvalEditUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCourses(jsonString);
		BackDoor.deleteInstructors(jsonString);
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		String course = "SEvalEditUiT.CS2104";
		
		// Next, we edit some student data to cover editing of students
		// after creating evaluations.

		// move one student out of Team 2
		StudentAttributes extraGuy = scn.students.get("ExtraGuy");
		moveToTeam(extraGuy, "New Team");

		// delete one student
		StudentAttributes dropOutGuy = scn.students.get("DropOut");
		backDoorOperationStatus = BackDoor.deleteStudent(dropOutGuy.course,
				dropOutGuy.email);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);

		
		// add a new student to Team 2, and change his email
		String newGuyOriginalEmail = "old@guy.com";
		StudentAttributes newGuy = new StudentAttributes("Team 2|New Guy|"
				+ newGuyOriginalEmail, course);
		backDoorOperationStatus = BackDoor.createStudent(newGuy);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		newGuy.email = "new@guy.com";
		backDoorOperationStatus = BackDoor.editStudent(newGuyOriginalEmail,
				newGuy);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		//move new guy out and bring him back again
		moveToTeam(newGuy, "Team x");
		moveToTeam(newGuy, "Team 2");

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
	public void testStudentEvalSubmitHTML() throws Exception{
		
		// Pending evaluation
		String link = Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.evaluations.get("First Eval").courseId);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("First Eval").name);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, scn.students.get("Charlie").googleId);
		bi.goToUrl(appUrl+link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/StudentEvalEditPendingHTML.html");
	}

	@Test	
	public void testStudentEvalEditHTMLAndAction() throws Exception{
		
		
		______TS("load evauation for editing");
		
		String link = Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.evaluations.get("First Eval").courseId);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("First Eval").name);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, scn.students.get("Danny").googleId);
		bi.goToUrl(appUrl+link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/StudentEvalEditSubmittedHTML.html");
		
		______TS("submitting edited evaluation");
		
		EvaluationAttributes eval = scn.evaluations.get("First Eval");
		
		SubmissionAttributes[] subs = new SubmissionAttributes[3];
		subs[0] = scn.submissions.get("DannyCharlie");
		subs[1] = scn.submissions.get("DannyDanny");
		subs[2] = scn.submissions.get("DannyEmily");
		for(int i=0; i<3; i++){
			subs[i].points-=10;
			subs[i].justification = new Text(subs[i].justification.getValue()+"(edited)");
			subs[i].p2pFeedback= new Text(subs[i].p2pFeedback.getValue()+"(edited)");
		}
		int charlieEditRowID = bi.getStudentRowIdInEditSubmission(scn.students.get("Charlie").name);
		bi.setSubmissionPoint(charlieEditRowID, subs[0].points+"");
		bi.setSubmissionJustification(charlieEditRowID, subs[0].justification.getValue());
		bi.setSubmissionComments(charlieEditRowID, subs[0].p2pFeedback.getValue());
		
		int dannyEditRowID = bi.getStudentRowIdInEditSubmission("self");
		bi.setSubmissionPoint(dannyEditRowID, subs[1].points+"");
		bi.setSubmissionJustification(dannyEditRowID, subs[1].justification.getValue());
		bi.setSubmissionComments(dannyEditRowID, subs[1].p2pFeedback.getValue());
		
		int emilyEditRowID = bi.getStudentRowIdInEditSubmission(scn.students.get("Emily").name);
		bi.setSubmissionPoint(emilyEditRowID, subs[2].points+"");
		bi.setSubmissionJustification(emilyEditRowID, subs[2].justification.getValue());
		bi.setSubmissionComments(emilyEditRowID, subs[2].p2pFeedback.getValue());
		
		//Fill review for "New Guy" with same values as given for Emily above.
		//  This is for convenience. Cannot submit with empty values.
		int newGuyEditRowID = bi.getStudentRowIdInEditSubmission("New Guy");
		bi.setSubmissionPoint(newGuyEditRowID, subs[2].points+"");
		bi.setSubmissionJustification(newGuyEditRowID, subs[2].justification.getValue());
		bi.setSubmissionComments(newGuyEditRowID, subs[2].p2pFeedback.getValue());
		
		bi.click(By.id("button_submit"));
		
		String charlieEmail = scn.students.get("Charlie").email;
		String dannyEmail = scn.students.get("Danny").email;
		String emilyEmail = scn.students.get("Emily").email;
		//No need to check for New Guy. No reason for the behavior to be 
		// different than the above three.
		
		print("Checking status message");
		bi.getSelenium().selectWindow("null");
		bi.waitForStatusMessage(String.format(Common.MESSAGE_STUDENT_EVALUATION_SUBMISSION_RECEIVED,eval.name,eval.courseId).replace("<br />", "\n"));

		print("Checking modified data");
		String json = "";
		json = BackDoor.getSubmissionAsJson(eval.courseId, eval.name, dannyEmail, charlieEmail);
		SubmissionAttributes charlieModified = Common.getTeammatesGson().fromJson(json, SubmissionAttributes.class);
		assertEquals(subs[0].points+"",charlieModified.points+"");
		assertEquals(subs[0].justification.getValue(),charlieModified.justification.getValue());
		assertEquals(subs[0].p2pFeedback.getValue(),charlieModified.p2pFeedback.getValue());

		json = BackDoor.getSubmissionAsJson(eval.courseId, eval.name, dannyEmail, dannyEmail);
		SubmissionAttributes dannyModified = Common.getTeammatesGson().fromJson(json, SubmissionAttributes.class);
		assertEquals(subs[1].points+"",dannyModified.points+"");
		assertEquals(subs[1].justification.getValue(),dannyModified.justification.getValue());
		assertEquals(subs[1].p2pFeedback.getValue(),dannyModified.p2pFeedback.getValue());

		json = BackDoor.getSubmissionAsJson(eval.courseId, eval.name, dannyEmail, emilyEmail);
		SubmissionAttributes emilyModified = Common.getTeammatesGson().fromJson(json, SubmissionAttributes.class);
		assertEquals(subs[2].points+"",emilyModified.points+"");
		assertEquals(subs[2].justification.getValue(),emilyModified.justification.getValue());
		assertEquals(subs[2].p2pFeedback.getValue(),emilyModified.p2pFeedback.getValue());
	}
	
	@Test
	public void testStudentEvalEditPageWithP2PDisabled() throws Exception{
		EvaluationAttributes eval = scn.evaluations.get("Second Eval");
		
		______TS("verify page functional with p2p feedback field disabled");
		String link = Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.evaluations.get("Second Eval").courseId);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("Second Eval").name);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, scn.students.get("Danny").googleId);
		bi.goToUrl(appUrl+link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/StudentEvalEditP2PDisabled.html");
		
		bi.click(By.id("button_submit"));
		bi.waitForStatusMessage(String.format(Common.MESSAGE_STUDENT_EVALUATION_SUBMISSION_RECEIVED,eval.name,eval.courseId).replace("<br />", "\n"));
	}
	
	@Test
	public void testStudentEvalEditPageWithAllFieldsDisabled() throws Exception{
			EvaluationAttributes eval = scn.evaluations.get("Closed Unpublished Eval");
			
			______TS("verify page functional with all fields disabled");
			String link = Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
			link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, eval.courseId);
			link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME, eval.name);
			link = Common.addParamToUrl(link, Common.PARAM_USER_ID, scn.students.get("Danny").googleId);
			
			bi.goToUrl(appUrl+link);
			bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/StudentEvalEditEntryFieldsDisabled.html");
			bi.click(By.id("button_submit"));
			bi.waitForStatusMessage(String.format(Common.MESSAGE_EVALUATION_NOT_OPEN,eval.name,eval.courseId).replace("<br />", "\n"));
	}
	
	private static void moveToTeam(StudentAttributes student, String newTeam) {
		String backDoorOperationStatus;
		student.team = newTeam;
		backDoorOperationStatus = BackDoor.editStudent(student.email, student);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
	}
}