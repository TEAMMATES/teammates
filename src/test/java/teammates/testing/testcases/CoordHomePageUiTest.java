package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.exception.NoAlertAppearException;
import teammates.jsp.Helper;
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
	
	private static String localhostAddress = "localhost:8080/";
	
	// Currently we just hardcode the row number of the courses and evaluations
	// If later deemed bad, we should change to the searching, although it takes longer
	// to code and also longer to run.
	private static int FIRST_COURSE_ROW_NUMBER = 1;
	private static int SECOND_COURSE_ROW_NUMBER = 0;

	private static int FIRST_EVAL_ROW_NUMBER = 4;
	private static int SECOND_EVAL_ROW_NUMBER = 3;
	private static int THIRD_EVAL_ROW_NUMBER = 0;
	private static int FOURTH_EVAL_ROW_NUMBER = 2;
	private static int FIFTH_EVAL_ROW_NUMBER = 1;

	@BeforeClass
	public static void classSetup() throws Exception {
		assertTrue(true);
		printTestClassHeader("CoordHomeUITest");
		String jsonString = Common.getFileContents(Common.TEST_DATA_FOLDER+"CoordHomeUITest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		
		TMAPI.deleteCoordinators(jsonString);
		System.out.println("Importing test data...");
		long start = System.currentTimeMillis();
		System.out.println(TMAPI.persistNewDataBundle(jsonString));
		System.out.println("The test data was imported in "+(System.currentTimeMillis()-start)+" ms");
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginCoord(scn.coords.get("teammates.test").id, Config.inst().TEAMMATES_APP_PASSWD);
		bi.goToUrl(localhostAddress+Common.JSP_COORD_HOME);
	}
	
	@Test
	public void testCoordHomeCourseAddLink(){
		printTestCaseHeader("testCoordHomeCourseAddLink");
		String link = bi.getElementRelativeHref(By.id("addNewCourse"));
		assertEquals(Common.JSP_COORD_COURSE,link);
	}

	@Test
	public void testCoordHomeCourseEnrollLink(){
		printTestCaseHeader("testCoordHomeCourseEnrollLink");
		String link = bi.getElementRelativeHref(By.className("t_course_enroll"+FIRST_COURSE_ROW_NUMBER));
		assertEquals(Helper.getCourseEnrollLink(scn.courses.get("CHomeUiT.CS2104").id),link);
	}

	@Test
	public void testCoordHomeCourseViewLink(){
		printTestCaseHeader("testCoordHomeCourseViewLink");
		String link = bi.getElementRelativeHref(By.className("t_course_view"+FIRST_COURSE_ROW_NUMBER));
		assertEquals(Helper.getCourseViewLink(scn.courses.get("CHomeUiT.CS2104").id),link);
	}

	@Test
	public void testCoordHomeCourseAddEvaluationLink(){
		printTestCaseHeader("testCoordHomeCourseAddEvaluationLink");
		String link = bi.getElementRelativeHref(By.className("t_course_add_eval"+FIRST_COURSE_ROW_NUMBER));
		assertEquals(Common.JSP_COORD_EVAL,link);
	}

	@Test
	public void testCoordHomeCourseDeleteLink(){
		printTestCaseHeader("testCoordHomeCourseDeleteLink");
		
		By deleteLinkLocator = By.className("t_course_delete"+FIRST_COURSE_ROW_NUMBER);
		String link = bi.getElementRelativeHref(deleteLinkLocator);
		assertEquals(Helper.getCourseDeleteLink(scn.courses.get("CHomeUiT.CS2104").id, Common.JSP_COORD_HOME),link);
		try{
			bi.clickAndCancel(deleteLinkLocator);
		} catch (NoAlertAppearException e){
			assertTrue("Delete course button unavailable",false);
		}
	}

	@Test
	public void testCoordHomeEvalViewLink(){
		printTestCaseHeader("testCoordHomeEvalViewLink");
		
		// Check View results link on Open evaluation: Evaluation 1 at Course 1
		By viewLinkLocator = By.id("viewEvaluation"+FIRST_EVAL_ROW_NUMBER);
		String link = bi.getElementRelativeHref(viewLinkLocator);
		assertEquals("Incorrect view link",Helper.getEvaluationViewLink(scn.courses.get("CHomeUiT.CS2104").id, scn.evaluations.get("CHomeUiT.CS2104:First Eval").name),link);
		assertFalse("View link unavailable on OPEN evaluation","none".equals(bi.getDriver().findElement(viewLinkLocator).getCssValue("text-decoration")));
		assertFalse("View link unavailable on OPEN evaluation","return false".equals(bi.getElementAttribute(viewLinkLocator, "onclick")));
		
		// Check View results link on Awaiting evaluation: Evaluation 4 at Course 2
		viewLinkLocator = By.id("viewEvaluation"+FOURTH_EVAL_ROW_NUMBER);
		link = bi.getElementRelativeHref(viewLinkLocator);
		assertEquals(Helper.getEvaluationViewLink(scn.courses.get("CHomeUiT.CS1101").id, scn.evaluations.get("CHomeUiT.CS1101:Fourth Eval").name),link);
		assertEquals("View link available on AWAITING evaluation","none",bi.getDriver().findElement(viewLinkLocator).getCssValue("text-decoration"));
		assertEquals("View link available on AWAITING evaluation","return false",bi.getElementAttribute(viewLinkLocator, "onclick"));
	}

	@Test
	public void testCoordHomeEvalEditLink(){
		printTestCaseHeader("testCoordHomeEvalEditLink");
		
		// Check the edit link for first evaluation, which is at fifth row in the page
		By editLinkLocator = By.id("editEvaluation"+FIRST_EVAL_ROW_NUMBER);
		String link = bi.getElementRelativeHref(editLinkLocator);
		assertEquals("Incorrect edit link",Helper.getEvaluationEditLink(scn.courses.get("CHomeUiT.CS2104").id, scn.evaluations.get("CHomeUiT.CS2104:First Eval").name),link);
		assertFalse("Edit link unavailable","none".equals(bi.getDriver().findElement(editLinkLocator).getCssValue("text-decoration")));
		assertFalse("Edit link unavailable","return false".equals(bi.getElementAttribute(editLinkLocator, "onclick")));
	}

	@Test
	public void testCoordHomeEvalDeleteLink(){
		printTestCaseHeader("testCoordHomeEvalDeleteLink");
		
		By deleteLinkLocator = By.id("deleteEvaluation"+FIRST_EVAL_ROW_NUMBER);
		String link = bi.getElementRelativeHref(deleteLinkLocator);
		assertEquals(Helper.getEvaluationDeleteLink(scn.courses.get("CHomeUiT.CS2104").id, scn.evaluations.get("CHomeUiT.CS2104:First Eval").name, Common.JSP_COORD_HOME),link);
		
		try{
			bi.clickAndCancel(deleteLinkLocator);
		} catch (NoAlertAppearException e){
			assertTrue("Delete link unavailable",false);
		}
	}

	@Test
	public void testCoordHomeEvalRemindLink(){
		printTestCaseHeader("testCoordHomeEvalRemindLink");
		
		// Check the remind link on Open Evaluation: Evaluation 1 at Course 1
		By remindLinkLocator = By.id("remindEvaluation"+FIRST_EVAL_ROW_NUMBER);
		
		try{
			bi.clickAndCancel(remindLinkLocator);
		} catch (NoAlertAppearException e){
			assertTrue("Remind link unavailable on OPEN evaluation",false);
		}
	}

	@Test
	public void testCoordHomeEvalPublishLink(){
		printTestCaseHeader("testCoordHomeEvalPublishLink");
		
		// Check the publish link on Closed Evaluation: Evaluation 3 at Course 2
		By publishLinkLocator = By.id("publishEvaluation"+THIRD_EVAL_ROW_NUMBER);
		
		try{
			bi.clickAndCancel(publishLinkLocator);
		} catch (NoAlertAppearException e){
			assertTrue("Publish link unavailable on CLOSED evaluation",false);
		}
		
		// Check the publish link on Open Evaluation: Evaluation 1 at Course 1
		publishLinkLocator = By.id("publishEvaluation"+FIRST_EVAL_ROW_NUMBER);
		try{
			bi.clickAndCancel(publishLinkLocator);
			assertTrue("Publish link available on OPEN evaluation",false);
		} catch (NoAlertAppearException e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testCoordHomeCoursePageHTML(){
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"CoordHomeHTML.html");
		
		TMAPI.deleteCourse(scn.courses.get("CHomeUiT.CS2104").id);
		TMAPI.deleteCourse(scn.courses.get("CHomeUiT.CS1101").id);
		
		bi.goToCoordHome();
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"CoordHomeHTMLEmpty.html");
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.deleteCourse(scn.courses.get("CHomeUiT.CS2104").id);
		TMAPI.deleteCourse(scn.courses.get("CHomeUiT.CS1101").id);
		
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordHomeUITest");
	}
}