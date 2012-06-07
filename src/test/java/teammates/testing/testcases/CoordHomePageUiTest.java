package teammates.testing.testcases;

import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.exception.NoAlertAppearException;
import teammates.testing.config.Config;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.TMAPI;

/**
 * Tests Coordinator Homepage UI
 * @author Aldrian Obaja
 */
public class CoordHomePageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static String appURL = Config.inst().TEAMMATES_URL;
	
	/* TODO
	 * Currently we just hardcode the row number of the courses and evaluations
	 * If later deemed bad, we should change to the searching, although it takes longer
	 * to code and also longer to run.
	 */
	private static int FIRST_COURSE_ROW_NUMBER = 1;
	private static int SECOND_COURSE_ROW_NUMBER = 0;

	private static int FIRST_EVAL_ROW_NUMBER = 4;
	private static int SECOND_EVAL_ROW_NUMBER = 3;
	private static int THIRD_EVAL_ROW_NUMBER = 0;
	private static int FOURTH_EVAL_ROW_NUMBER = 2;
	private static int FIFTH_EVAL_ROW_NUMBER = 1;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("CoordHomeUITest");
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"CoordHomeUITest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		
		TMAPI.deleteCoordinators(jsonString);
		System.out.println("Importing test data...");
		long start = System.currentTimeMillis();
		System.out.println(TMAPI.persistNewDataBundle(jsonString));
		System.out.println("The test data was imported in "+(System.currentTimeMillis()-start)+" ms");
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginCoord(scn.coords.get("teammates.test").id, Config.inst().TEAMMATES_APP_PASSWD);
		bi.goToUrl(appURL+Common.JSP_COORD_HOME);
	}
	
	@Test
	public void testCoordHomePage() throws Exception{
		testCoordHomeCourseDeleteLink();
		testCoordHomeEvalDeleteLink();
		testCoordHomeEvalRemindLink();
		testCoordHomeEvalPublishLink();
		testCoordHomeCoursePageHTML();
	}

	public void testCoordHomeCourseDeleteLink(){
		printTestCaseHeader("testCoordHomeCourseDeleteLink");
		
		By deleteLinkLocator = By.className("t_course_delete"+FIRST_COURSE_ROW_NUMBER);
//		String link = bi.getElementRelativeHref(deleteLinkLocator);
//		assertEquals(CoordCourseAddHelper.getCourseDeleteLink(scn.courses.get("CHomeUiT.CS2104").id, Common.JSP_COORD_HOME),link);
		try{
			bi.clickAndCancel(deleteLinkLocator);
		} catch (NoAlertAppearException e){
			assertTrue("Delete course button unavailable, or it is available but no confirmation box",false);
		}
	}

	public void testCoordHomeEvalDeleteLink(){
		printTestCaseHeader("testCoordHomeEvalDeleteLink");
		
		By deleteLinkLocator = By.id("deleteEvaluation"+FIRST_EVAL_ROW_NUMBER);
		
		try{
			bi.clickAndCancel(deleteLinkLocator);
		} catch (NoAlertAppearException e){
			assertTrue("Delete link is unavailable or it is available but no confirmation box",false);
		}
	}

	public void testCoordHomeEvalRemindLink(){
		printTestCaseHeader("testCoordHomeEvalRemindLink");
		
		// Check the remind link on Open Evaluation: Evaluation 1 at Course 1
		By remindLinkLocator = By.id("remindEvaluation"+FIRST_EVAL_ROW_NUMBER);
		
		try{
			bi.clickAndCancel(remindLinkLocator);
		} catch (NoAlertAppearException e){
			assertTrue("Remind link unavailable on OPEN evaluation, or it is available but no confirmation box",false);
		}
	}

	public void testCoordHomeEvalPublishLink(){
		printTestCaseHeader("testCoordHomeEvalPublishLink");
		
		// Check the publish link on Closed Evaluation: Evaluation 3 at Course 2
		By publishLinkLocator = By.id("publishEvaluation"+THIRD_EVAL_ROW_NUMBER);
		
		try{
			bi.clickAndCancel(publishLinkLocator);
		} catch (NoAlertAppearException e){
			assertTrue("Publish link unavailable on CLOSED evaluation, or it is available but no confirmation box",false);
		}
		
		// Check the publish link on Open Evaluation: Evaluation 1 at Course 1
		publishLinkLocator = By.id("publishEvaluation"+FIRST_EVAL_ROW_NUMBER);
		try{
			bi.clickAndCancel(publishLinkLocator);
			assertTrue("Publish link available on OPEN evaluation",false);
		} catch (NoAlertAppearException e){}
	}
	
	public void testCoordHomeCoursePageHTML() throws Exception{
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"CoordHomeHTML.html");
		
		TMAPI.deleteCourse(scn.courses.get("CHomeUiT.CS2104").id);
		TMAPI.deleteCourse(scn.courses.get("CHomeUiT.CS1101").id);
		
		bi.goToCoordHome();
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"CoordHomeHTMLEmpty.html");
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordHomeUITest");
	}
}