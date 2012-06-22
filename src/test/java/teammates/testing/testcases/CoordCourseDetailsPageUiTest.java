package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.exception.NoAlertAppearException;
import teammates.jsp.Helper;
import teammates.testing.config.Config;
import teammates.testing.lib.BackDoor;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.SharedLib;

/**
 * Tests Coordinator Course Details UI
 * @author Aldrian Obaja
 */
public class CoordCourseDetailsPageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static boolean deleteStudentTestWasRun = false;
	
	private static String appUrl = Config.inst().TEAMMATES_URL;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("CoordCourseDetailsTest");
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/CoordCourseDetailsUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);

		System.out.println("Importing test data...");
		long start = System.currentTimeMillis();
		BackDoor.deleteCoordinators(jsonString);
		System.out.println(BackDoor.persistNewDataBundle(jsonString));
		System.out.println("The test data was imported in "+(System.currentTimeMillis()-start)+" ms");
		
		bi = BrowserInstancePool.getBrowserInstance();

		bi.loginAdmin(Config.inst().TEAMMATES_ADMIN_ACCOUNT, Config.inst().TEAMMATES_ADMIN_PASSWORD);
		String link = appUrl+Common.PAGE_COORD_COURSE_DETAILS;
		link = Helper.addParam(link,Common.PARAM_COURSE_ID,scn.courses.get("CCDetailsUiT.CS2104").id);
		link = Helper.addParam(link,Common.PARAM_USER_ID,scn.coords.get("teammates.test").id);
		bi.goToUrl(link);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordCourseDetailsUITest");
	}
	
	@Test
	public void testCoordCourseDetailsPageHTML() throws Exception{
		assertFalse("Delete student test was run before this test. This test will fail due to difference in HTML.",deleteStudentTestWasRun);
		
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsPage.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsPage.html");
		
		bi.click(bi.coordCourseDetailSortByTeamName);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsByTeam.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsByTeam.html");
		
		bi.click(bi.coordCourseDetailSortByStatus);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsByStatus.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsByStatus.html");
		
		bi.click(bi.coordCourseDetailSortByStudentName);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsPage.html");
	}
	
	@Test
	public void testCoordCourseDetailsRemindStudent(){
		assertFalse("Delete student test was run before this test. This test will fail due to difference in HTML.",deleteStudentTestWasRun);
		
		String studentName = scn.students.get("benny.tmms@CCDetailsUiT.CS2104").name;
		String studentEmail = scn.students.get("benny.tmms@CCDetailsUiT.CS2104").email;
		String otherStudentEmail = scn.students.get("charlie.tmms@CCDetailsUiT.CS2104").email;
		String registeredStudentEmail = scn.students.get("alice.tmms@CCDetailsUiT.CS2104").email;
		
		// Test remind student
		bi.clickCoordCourseDetailRemind(studentName);
		
		if(!Config.inst().isLocalHost()){
			String key = BackDoor.getKeyForStudent(scn.courses.get("CCDetailsUiT.CS2104").id, studentEmail);
			bi.waitForEmail();
			assertEquals(key,SharedLib.getRegistrationKeyFromGmail(studentEmail, Config.inst().TEAMMATES_APP_PASSWORD, scn.courses.get("CCDetailsUiT.CS2104").id));
		}
		
		// Test remind students
		bi.clickAndConfirm(bi.coordCourseDetailRemindButton);
		if(!Config.inst().isLocalHost()){
			bi.waitForEmail();
			String key = BackDoor.getKeyForStudent(scn.courses.get("CCDetailsUiT.CS2104").id, otherStudentEmail);
			assertEquals(key,SharedLib.getRegistrationKeyFromGmail(otherStudentEmail, Config.inst().TEAMMATES_APP_PASSWORD, scn.courses.get("CCDetailsUiT.CS2104").id));
			assertEquals(null,SharedLib.getRegistrationKeyFromGmail(registeredStudentEmail, Config.inst().TEAMMATES_APP_PASSWORD, scn.courses.get("CCDetailsUiT.CS2104").id));
		}
	}

	@Test
	// Should be the last test
	public void testCoordCourseDetailsDeleteStudent() throws Exception{
		deleteStudentTestWasRun = true;
		// Test delete student
		String studentName = scn.students.get("benny.tmms@CCDetailsUiT.CS2104").name;
		String studentEmail = scn.students.get("benny.tmms@CCDetailsUiT.CS2104").email;
		
		int studentRowId = bi.getStudentRowId(studentName);
		assertTrue(studentRowId!=-1);
		
		// Check delete link
		try{
			bi.clickCoordCourseDetailStudentDeleteAndCancel(studentRowId);
			String student = BackDoor.getStudentAsJason(scn.courses.get("CCDetailsUiT.CS2104").id, studentEmail);
			if(isNullJSON(student)) fail("Student was deleted when it's not supposed to be");
		} catch (NoAlertAppearException e){
			fail("No alert box when clicking delete button at course details page.");
		}

		try{
			bi.clickCoordCourseDetailStudentDeleteAndConfirm(studentRowId);
//			bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsStudentDeleteSuccessful.html");
			bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsStudentDeleteSuccessful.html");
		} catch (NoAlertAppearException e){
			fail("No alert box when clicking delete button at course details page.");
		}
	}
}