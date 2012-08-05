package teammates.test.cases;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.SubmissionData;
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

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/StudentEvalEditUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCoordinators(jsonString);
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		
		// Next, we edit some student data to cover editing of students
		// after creating evaluations.

		// move one student out of Team 2
		StudentData extraGuy = scn.students.get("ExtraGuy");
		moveToTeam(extraGuy, "New Team");

		// delete one student
		StudentData dropOutGuy = scn.students.get("DropOut");
		backDoorOperationStatus = BackDoor.deleteStudent(dropOutGuy.course,
				dropOutGuy.email);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);

		// move Charlie out of Team 2 and back again
		StudentData charlie = scn.students.get("Charlie");
		moveToTeam(charlie, "New Team");
		moveToTeam(charlie, "Team 2");

		// add a new student to Team 2, and change his email
		String newGuyOriginalEmail = "old@guy.com";
		StudentData newGuy = new StudentData("Team 2|New Guy|"
				+ newGuyOriginalEmail, charlie.course);
		backDoorOperationStatus = BackDoor.createStudent(newGuy);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		newGuy.email = "new@guy.com";
		backDoorOperationStatus = BackDoor.editStudent(newGuyOriginalEmail,
				newGuy);
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
	public void testStudentEvalSubmitHTML() throws Exception{
		
		// Pending evaluation
		String link = Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.evaluations.get("First Eval").course);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("First Eval").name);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, scn.students.get("Charlie").id);
		bi.goToUrl(appUrl+link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/StudentEvalEditPendingHTML.html");
	}

	@Test	
	public void testStudentEvalEditHTMLAndAction() throws Exception{
		
		
		______TS("load evauation for editing");
		
		String link = Common.PAGE_STUDENT_EVAL_SUBMISSION_EDIT;
		link = Common.addParamToUrl(link, Common.PARAM_COURSE_ID, scn.evaluations.get("First Eval").course);
		link = Common.addParamToUrl(link, Common.PARAM_EVALUATION_NAME, scn.evaluations.get("First Eval").name);
		link = Common.addParamToUrl(link, Common.PARAM_USER_ID, scn.students.get("Danny").id);
		bi.goToUrl(appUrl+link);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/StudentEvalEditSubmittedHTML.html");
		
		______TS("submitting edited evaluation");
		
		EvaluationData eval = scn.evaluations.get("First Eval");
		
		SubmissionData[] subs = new SubmissionData[3];
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
		bi.waitForStatusMessage(String.format(Common.MESSAGE_STUDENT_EVALUATION_SUBMISSION_RECEIVED,eval.name,eval.course).replace("<br />", "\n"));

		print("Checking modified data");
		String json = "";
		json = BackDoor.getSubmissionAsJson(eval.course, eval.name, dannyEmail, charlieEmail);
		SubmissionData charlieModified = Common.getTeammatesGson().fromJson(json, SubmissionData.class);
		assertEquals(subs[0].points+"",charlieModified.points+"");
		assertEquals(subs[0].justification.getValue(),charlieModified.justification.getValue());
		assertEquals(subs[0].p2pFeedback.getValue(),charlieModified.p2pFeedback.getValue());

		json = BackDoor.getSubmissionAsJson(eval.course, eval.name, dannyEmail, dannyEmail);
		SubmissionData dannyModified = Common.getTeammatesGson().fromJson(json, SubmissionData.class);
		assertEquals(subs[1].points+"",dannyModified.points+"");
		assertEquals(subs[1].justification.getValue(),dannyModified.justification.getValue());
		assertEquals(subs[1].p2pFeedback.getValue(),dannyModified.p2pFeedback.getValue());

		json = BackDoor.getSubmissionAsJson(eval.course, eval.name, dannyEmail, emilyEmail);
		SubmissionData emilyModified = Common.getTeammatesGson().fromJson(json, SubmissionData.class);
		assertEquals(subs[2].points+"",emilyModified.points+"");
		assertEquals(subs[2].justification.getValue(),emilyModified.justification.getValue());
		assertEquals(subs[2].p2pFeedback.getValue(),emilyModified.p2pFeedback.getValue());
	}
	
	private static void moveToTeam(StudentData student, String newTeam) {
		String backDoorOperationStatus;
		student.team = newTeam;
		backDoorOperationStatus = BackDoor.editStudent(student.email, student);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
	}
}