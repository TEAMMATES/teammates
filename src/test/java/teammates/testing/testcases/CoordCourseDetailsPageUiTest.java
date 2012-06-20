package teammates.testing.testcases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.datatransfer.StudentData;
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
	
	private static String appUrl = Config.inst().TEAMMATES_URL.replaceAll("/(?=$)","");

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
	public void testCoordCourseDetailsPage() throws Exception{
		testCoordCourseDetailsPageHTML();
		testCoordCourseDetailsUiPaths();
	}
	
	public void testCoordCourseDetailsPageHTML() throws Exception{
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/CoordCourseDetailsPage.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/CoordCourseDetailsPage.html");
		
		bi.click(bi.coordCourseDetailSortByTeamName);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/CoordCourseDetailsByTeam.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/CoordCourseDetailsByTeam.html");
		
		bi.click(bi.coordCourseDetailSortByStatus);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/CoordCourseDetailsByStatus.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/CoordCourseDetailsByStatus.html");
		
		bi.click(bi.coordCourseDetailSortByStudentName);
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/CoordCourseDetailsPage.html");
	}
	
	public void testCoordCourseDetailsUiPaths() throws Exception{
		testCoordCourseDetailsRemindStudent();
		
		testCoordCourseDetailsDeleteStudent();
	}
	
	public void testCoordCourseDetailsRemindStudent(){
		if(Config.inst().isLocalHost()) return;
		String studentName = scn.students.get("benny.tmms@CCDetailsUiT.CS2104").name;
		String studentEmail = scn.students.get("benny.tmms@CCDetailsUiT.CS2104").email;
		String otherStudentEmail = scn.students.get("charlie.tmms@CCDetailsUiT.CS2104").email;
		String registeredStudentEmail = scn.students.get("alice.tmms@CCDetailsUiT.CS2104").email;
		
		// Test remind student
		bi.clickCoordCourseDetailInvite(studentName);
		bi.waitForEmail();
		assertTrue(SharedLib.getRegistrationKeyFromGmail(studentEmail, Config.inst().TEAMMATES_APP_PASSWORD, scn.courses.get("CCDetailsUiT.CS2104").id)!=null);
		
		// Test remind students
		bi.click(bi.coordCourseDetailRemindButton);
		bi.waitForEmail();
		assertTrue(SharedLib.getRegistrationKeyFromGmail(otherStudentEmail, Config.inst().TEAMMATES_APP_PASSWORD, scn.courses.get("CCDetailsUiT.CS2104").id)!=null);
		assertTrue(SharedLib.getRegistrationKeyFromGmail(registeredStudentEmail, Config.inst().TEAMMATES_APP_PASSWORD, scn.courses.get("CCDetailsUiT.CS2104").id)==null);
	}
	
	public void testCoordCourseDetailsDeleteStudent() throws Exception{
		// Test delete student
		String studentName = scn.students.get("benny.tmms@CCDetailsUiT.CS2104").name;
		
		int studentRowId = bi.getStudentRowId(studentName);
		assertTrue(studentRowId!=-1);

//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsStudentDeleteInit.html");
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsStudentDeleteInit.html");
		
		// Check delete link
		try{
			bi.clickCoordCourseDetailStudentDeleteAndCancel(studentRowId);
			bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsStudentDeleteInit.html");
		} catch (NoAlertAppearException e){
			fail("No alert box when clicking delete button at course details page.");
		}

		try{
			bi.clickCoordCourseDetailStudentDeleteAndConfirm(studentRowId);
//			bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsStudentDeleteSuccessful.html");
			bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordCourseDetailsStudentDeleteSuccessful.html");
		} catch (NoAlertAppearException e){
			fail("No alert box when clicking delete button at course details page.");
		}
	}
}