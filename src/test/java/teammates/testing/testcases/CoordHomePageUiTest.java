package teammates.testing.testcases;

import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.api.Common;
import teammates.datatransfer.DataBundle;
import teammates.datatransfer.EvaluationData;
import teammates.exception.NoAlertAppearException;
import teammates.testing.config.Config;
import teammates.testing.lib.BackDoor;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;

/**
 * Tests Coordinator Homepage UI
 * @author Aldrian Obaja
 */
public class CoordHomePageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static EvaluationData firstEval;
	private static EvaluationData secondEval;
	private static EvaluationData thirdEval;
	
//	private static String appURL = Config.inst().TEAMMATES_URL;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader("CoordHomeUITest");
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/CoordHomeUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		firstEval = scn.evaluations.get("First Eval");
		secondEval = scn.evaluations.get("Second Eval");
		thirdEval = scn.evaluations.get("Third Eval");
		
		System.out.println("Importing test data...");
		long start = System.currentTimeMillis();
		BackDoor.deleteCoordinators(jsonString);
		System.out.println(BackDoor.persistNewDataBundle(jsonString));
		System.out.println("The test data was imported in "+(System.currentTimeMillis()-start)+" ms");
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginCoord(scn.coords.get("teammates.test").id, Config.inst().TEAMMATES_APP_PASSWORD);
//		bi.goToUrl(appURL+Common.PAGE_COORD_HOME); // Not needed as it will by default go to homepage
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordHomeUITest");
	}

	@Test
	public void testCoordHomePage() throws Exception{
		testCoordHomeHTML();
		testCoordHomeEvalRemindLink();
		testCoordHomeEvalPublishLink();
		testCoordHomeEvalDeleteLink();
		testCoordHomeCourseDeleteLink();
		testCoordHomeEmptyHTML();
	}
	
	public void testCoordHomeHTML() throws Exception{
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordHomeHTML.html");
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordHomeHTML.html");
	}

	public void testCoordHomeEvalRemindLink(){
		printTestCaseHeader("testCoordHomeEvalRemindLink");
		
		// Check the remind link on Open Evaluation: Evaluation 1 at Course 1
		By remindLinkLocator = bi.getCoordHomeEvaluationRemindLinkLocator(firstEval.course, firstEval.name);
		
		try{
			bi.clickAndCancel(remindLinkLocator);
		} catch (NoAlertAppearException e){
			fail("Remind link unavailable on OPEN evaluation, or it is available but no confirmation box");
		}
	}

	public void testCoordHomeEvalPublishLink(){
		printTestCaseHeader("testCoordHomeEvalPublishLink");
		
		// Check the publish link on Closed Evaluation: Evaluation 3 at Course 2
		By publishLinkLocator = bi.getCoordHomeEvaluationPublishLinkLocator(thirdEval.course, thirdEval.name);
		
		try{
			bi.clickAndCancel(publishLinkLocator);
		} catch (NoAlertAppearException e){
			fail("Publish link unavailable on CLOSED evaluation, or it is available but no confirmation box");
		}
		
		// Check the publish link on Open Evaluation: Evaluation 1 at Course 1
		publishLinkLocator = bi.getCoordHomeEvaluationPublishLinkLocator(firstEval.course, firstEval.name);
		try{
			bi.clickAndCancel(publishLinkLocator);
			fail("Publish link available on OPEN evaluation");
		} catch (NoAlertAppearException e){}

		// Check the unpublish link on Published Evaluation: Evaluation 2 at Course 1
		By unpublishLinkLocator = bi.getCoordHomeEvaluationUnpublishLinkLocator(secondEval.course, secondEval.name);
		try{
			bi.clickAndCancel(unpublishLinkLocator);
		} catch (NoAlertAppearException e){
			fail("Unpublish link unavailable on PUBLISHED evaluation");
		}
	}

	public void testCoordHomeEvalDeleteLink() throws Exception{
		printTestCaseHeader("testCoordHomeEvalDeleteLink");
		
		By deleteLinkLocator = bi.getCoordHomeEvaluationDeleteLinkLocator(firstEval.course, firstEval.name);
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordHomeEvalDeleteInit.html");
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordHomeEvalDeleteInit.html");
		
		try{
			bi.clickAndCancel(deleteLinkLocator);
			bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordHomeEvalDeleteInit.html");
		} catch (NoAlertAppearException e){
			fail("Delete link is unavailable or it is available but no confirmation box");
		}
		
		try{
			bi.clickAndConfirm(deleteLinkLocator);
//			bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordHomeEvalDeleteSuccessful.html");
			bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordHomeEvalDeleteSuccessful.html");
		} catch (NoAlertAppearException e){
			fail("Delete link is unavailable or it is available but no confirmation box");
		}
	}

	public void testCoordHomeCourseDeleteLink() throws Exception{
		printTestCaseHeader("testCoordHomeCourseDeleteLink");
		
		By deleteLinkLocator = bi.getCoordHomeCourseDeleteLinkLocator(scn.courses.get("CHomeUiT.CS2104").id);
		
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordHomeCourseDeleteInit.html");
		bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordHomeCourseDeleteInit.html");
		
		try{
			bi.clickAndCancel(deleteLinkLocator);
			bi.verifyCurrentPageHTMLRegex(Common.TEST_PAGES_FOLDER+"/coordHomeCourseDeleteInit.html");
		} catch (NoAlertAppearException e){
			fail("Delete course button unavailable, or it is available but no confirmation box");
		}
		
		try{
			bi.clickAndConfirm(deleteLinkLocator);
//			bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordHomeCourseDeleteSuccessful.html");
			bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordHomeCourseDeleteSuccessful.html");
		} catch (NoAlertAppearException e){
			fail("Delete course button unavailable, or it is available but no confirmation box");
		}
	}
	
	public void testCoordHomeEmptyHTML() throws Exception{
		BackDoor.deleteCourse(scn.courses.get("CHomeUiT.CS2104").id);
		BackDoor.deleteCourse(scn.courses.get("CHomeUiT.CS1101").id);
		
		bi.goToCoordHome();
//		bi.printCurrentPage(Common.TEST_PAGES_FOLDER+"/coordHomeHTMLEmpty.html");
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordHomeHTMLEmpty.html");
	}
}