package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.ui.controller.Action;
import teammates.ui.controller.AdminHomePageData;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.ShowPageResult; 

public class AdminInstructorAccountAddActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.ADMIN_INSTRUCTORACCOUNT_ADD;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		String[] submissionParams = new String[]{};
		verifyOnlyAdminsCanAccess(submissionParams);
	}

	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		final String newInstructorId = "JamesBond89";
		final String name = "JamesBond";
		final String email = "jamesbond89@gmail.com";
		final String institute = "National University of Singapore";
		final String adminUserId = "admin.user";
		
		______TS("Not enough parameters");
		
		gaeSimulation.loginAsAdmin(adminUserId);
		verifyAssumptionFailure();
		verifyAssumptionFailure(Const.ParamsNames.INSTRUCTOR_ID, newInstructorId);
		verifyAssumptionFailure(Const.ParamsNames.INSTRUCTOR_ID, newInstructorId,
				Const.ParamsNames.INSTRUCTOR_NAME, name);
		verifyAssumptionFailure(Const.ParamsNames.INSTRUCTOR_ID, newInstructorId,
				Const.ParamsNames.INSTRUCTOR_NAME, name,
				Const.ParamsNames.INSTRUCTOR_EMAIL, email);
		verifyAssumptionFailure(Const.ParamsNames.INSTRUCTOR_ID, newInstructorId,
				Const.ParamsNames.INSTRUCTOR_NAME, name,
				Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute);
		verifyAssumptionFailure(Const.ParamsNames.INSTRUCTOR_ID, newInstructorId,
				Const.ParamsNames.INSTRUCTOR_EMAIL, email,
				Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute);
		verifyAssumptionFailure(Const.ParamsNames.INSTRUCTOR_NAME, name,
				Const.ParamsNames.INSTRUCTOR_EMAIL, email,
				Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute);
		
		
		______TS("Normal case: not importing demo couse");
		
		Action a = getAction(
				Const.ParamsNames.INSTRUCTOR_ID, newInstructorId,
				Const.ParamsNames.INSTRUCTOR_NAME, name,
				Const.ParamsNames.INSTRUCTOR_EMAIL, email,
				Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute);
		
		RedirectResult r = (RedirectResult) a.executeAndPostProcess();
		
		assertEquals(false, r.isError);
		assertEquals("Instructor " + name + " has been successfully created", r.getStatusMessage());
		assertEquals(Const.ActionURIs.ADMIN_HOME_PAGE, r.destination);
		assertEquals(Const.ActionURIs.ADMIN_HOME_PAGE + "?message=Instructor+JamesBond+has+been+successfully+created&error=false&user=" + adminUserId, r.getDestinationWithParams());
		
		______TS("Error: already an instructor");
		
		ShowPageResult rForAlreadyExistingInstructor = (ShowPageResult) getAction(
				Const.ParamsNames.INSTRUCTOR_ID, newInstructorId,
				Const.ParamsNames.INSTRUCTOR_NAME, name,
				Const.ParamsNames.INSTRUCTOR_EMAIL, email,
				Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute).executeAndPostProcess();
		
		assertEquals(true, rForAlreadyExistingInstructor.isError);
		assertEquals("The Google ID " + newInstructorId + " is already registered as an instructor", rForAlreadyExistingInstructor.getStatusMessage());
		assertEquals(Const.ViewURIs.ADMIN_HOME, rForAlreadyExistingInstructor.destination);
		assertEquals(Const.ViewURIs.ADMIN_HOME + "?message=The+Google+ID+" + newInstructorId + "+is+already+registered+as+an+instructor&error=true&user=" + adminUserId, rForAlreadyExistingInstructor.getDestinationWithParams());
		AdminHomePageData pageData = (AdminHomePageData)rForAlreadyExistingInstructor.data;
		assertEquals(email, pageData.instructorEmail);
		assertEquals(newInstructorId, pageData.instructorId);
		assertEquals(institute, pageData.instructorInstitution);
		assertEquals(name, pageData.instructorName);
		
		______TS("Normal case: importing demo couse");
		
		final String anotherNewInstructorId = "JamesBond99";
		a = getAction(
				Const.ParamsNames.INSTRUCTOR_ID, anotherNewInstructorId,
				Const.ParamsNames.INSTRUCTOR_NAME, name,
				Const.ParamsNames.INSTRUCTOR_EMAIL, email,
				Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute,
				Const.ParamsNames.INSTRUCTOR_IMPORT_SAMPLE, "SELECTED");
		
		r = (RedirectResult) a.executeAndPostProcess();
		assertEquals(false, r.isError);
		assertEquals("Instructor " + name + " has been successfully created", r.getStatusMessage());
		assertEquals(Const.ActionURIs.ADMIN_HOME_PAGE, r.destination);
		assertEquals(Const.ActionURIs.ADMIN_HOME_PAGE + "?message=Instructor+JamesBond+has+been+successfully+created&error=false&user=" + adminUserId, r.getDestinationWithParams());

	}
	
	private Action getAction(String... parameters) throws Exception {
		return (Action)gaeSimulation.getActionObject(uri, parameters);
	}

}
