package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.StudentEvalEditPage;
import teammates.test.pageobjects.StudentHomePage;

import com.google.appengine.api.datastore.Text;

/**
 * Tests 'Edit Evaluation' view of students.
 * SUT: {@link StudentEvalEditPage}.
 */
public class StudentEvalEditSubmissionPageUiTest extends BaseUiTestCase {

	private static DataBundle testData;
	private static Browser browser;
	private StudentEvalEditPage editPage;
	
	
	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		testData = loadDataBundle("/StudentEvalEditSubmissionPageUiTest.json");
		restoreTestDataOnServer(testData);
		
		
		// Next, we edit some student data to cover editing of students
		// after creating evaluations.
		//TODO: The effect of editing student data should be checked at Logic level, not here.

		// move one student out of Team 2
		StudentAttributes extraGuy = testData.students.get("ExtraGuy");
		moveToTeam(extraGuy, "New Team");

		// delete one student
		StudentAttributes dropOutGuy = testData.students.get("DropOut");
		String backDoorOperationStatus = BackDoor.deleteStudent(dropOutGuy.course,
				dropOutGuy.email);
		assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);

		
		// add a new student to Team 2, and change his email
		String newGuyOriginalEmail = "old@guy.com";
		StudentAttributes newGuy = new StudentAttributes("Team 2", "New Guy"
				, newGuyOriginalEmail, "", "SEvalEditUiT.CS2104");
		backDoorOperationStatus = BackDoor.createStudent(newGuy);
		assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
		newGuy.email = "new@guy.com";
		backDoorOperationStatus = BackDoor.editStudent(newGuyOriginalEmail,
				newGuy);
		assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
		
		//move new guy out and bring him back again
		moveToTeam(newGuy, "Team x");
		moveToTeam(newGuy, "Team 2");

		browser = BrowserPool.getBrowser();
		
	}

	@Test
	public void testPendingEvaluation() throws Exception{
		
		______TS("content");
		
		editPage = loginToEvalEditPage("Charlie", "First Eval");
		editPage.verifyHtml("/StudentEvalEditPendingHTML.html");
		
		______TS("links");
		
		//No links to check. 
		
		______TS("input validation");
		
		//No input validation to check.
		
		______TS("action: submit");
		
		//TODO: implement this

	}

	@Test
	public void testEditingSubmission() throws Exception{
		
		EvaluationAttributes eval = testData.evaluations.get("First Eval");
		editPage = loginToEvalEditPage("Danny", eval.name);
		
		______TS("content");
		
		editPage.verifyHtml("/StudentEvalEditSubmittedHTML.html");
	
		______TS("action: submit");
		
		SubmissionAttributes[] subs = new SubmissionAttributes[3];
		subs[0] = testData.submissions.get("DannyCharlie");
		subs[1] = testData.submissions.get("DannyDanny");
		subs[2] = testData.submissions.get("DannyEmily");
		
		//create new values of all submissions
		for(int i=0; i<3; i++){
			subs[i].points-=10;
			subs[i].justification = new Text(subs[i].justification.getValue()+"\r\n"+"(edited)");
			subs[i].p2pFeedback= new Text(subs[i].p2pFeedback.getValue()+"\r\n"+"multilinetest"+"\r\n"+"\r\n"+"(edited)");
		}
		SubmissionAttributes subForNewGuy = new SubmissionAttributes();
		//Fill review for "New Guy" with same values as given for Emily above.
		//  This is for convenience. Cannot submit with empty values.
		subForNewGuy.p2pFeedback = subs[2].p2pFeedback;
		subForNewGuy.justification = subs[2].justification;
		
		//submit new values
		editPage.fillSubmissionValues("Charlie", subs[0]);
		editPage.fillSubmissionValues("self", subs[1]);
		editPage.fillSubmissionValues("Emily", subs[2]);
		editPage.fillSubmissionValues("New Guy", subs[2]);
		StudentHomePage homePage = editPage.submit();
		homePage.verifyStatus(String.format(Const.StatusMessages.STUDENT_EVALUATION_SUBMISSION_RECEIVED,eval.name,eval.courseId).replace("<br />", "\n"));
		
		//confirm new values were saved
		String charlieEmail = testData.students.get("Charlie").email;
		String dannyEmail = testData.students.get("Danny").email;
		String emilyEmail = testData.students.get("Emily").email;
		verifyEditSaved(subs[0], BackDoor.getSubmission(eval.courseId, eval.name, dannyEmail, charlieEmail));
		verifyEditSaved(subs[1], BackDoor.getSubmission(eval.courseId, eval.name, dannyEmail, dannyEmail));
		verifyEditSaved(subs[2], BackDoor.getSubmission(eval.courseId, eval.name, dannyEmail, emilyEmail));
		
		editPage = loginToEvalEditPage("Danny", eval.name);
		editPage.verifyHtml("/StudentEvalEditResubmittedHTML.html");
		
		//TODO: more tests are needed to cover the disabling of editing when the evaluation is CLOSED.
		// In particular, timezone differences should be considered in such testing. Currently, these
		// tests are done in AllAccessControlUiTest class.

	}

	@Test
	public void testP2PDisabledEvaluation() throws Exception{
		
		EvaluationAttributes eval = testData.evaluations.get("Second Eval");
		editPage = loginToEvalEditPage("Danny", eval.name);
		
		______TS("content");
		
		editPage.verifyHtml("/StudentEvalEditP2PDisabled.html");
		
		______TS("action: submit");
		
		StudentHomePage homePage = editPage.submit();
		homePage.verifyStatus(
				String.format(
						Const.StatusMessages.STUDENT_EVALUATION_SUBMISSION_RECEIVED,
						eval.name,
						eval.courseId)
						.replace("<br />", "\n"));
		
	}
	
	@Test
	public void testNotOpenEvaluation() throws Exception{
		EvaluationAttributes eval = testData.evaluations.get("Closed Unpublished Eval");
		editPage = loginToEvalEditPage("Danny", eval.name);
		
		______TS("content");
		
		editPage.verifyHtml("/StudentEvalEditEntryFieldsDisabled.html");
		
		______TS("action: submit");
		
		editPage.submitUnsuccessfully()
			.verifyStatus(String.format(Const.StatusMessages.EVALUATION_NOT_OPEN,eval.name,eval.courseId).replace("<br />", "\n"));
	
		//TODO: test for evaluation that closed while the student was editing submission.
	}
	
	private void verifyEditSaved(SubmissionAttributes expected,	SubmissionAttributes actual) {
        assertEquals((expected.points+"").trim(),actual.points+"");
		assertEquals(expected.justification.getValue().trim(),actual.justification.getValue());
		assertEquals(expected.p2pFeedback.getValue().trim(),actual.p2pFeedback.getValue());
	}

	private StudentEvalEditPage loginToEvalEditPage(String studentName,	String evalName) {
		Url editUrl = createUrl(Const.ActionURIs.STUDENT_EVAL_SUBMISSION_EDIT_PAGE)
			.withUserId(testData.students.get(studentName).googleId)
			.withCourseId(testData.evaluations.get(evalName).courseId)
			.withEvalName(testData.evaluations.get(evalName).name);
		return loginAdminToPage(browser, editUrl, StudentEvalEditPage.class);
	}

	private static void moveToTeam(StudentAttributes student, String newTeam) {
		String backDoorOperationStatus;
		student.team = newTeam;
		backDoorOperationStatus = BackDoor.editStudent(student.email, student);
		assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
	}

	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserPool.release(browser);
	}
}