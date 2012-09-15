package teammates.test.cases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.common.Common;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationData;
import teammates.test.driver.BackDoor;
import teammates.test.driver.BrowserInstance;
import teammates.test.driver.BrowserInstancePool;
import teammates.test.driver.NoAlertException;
import teammates.test.driver.TestProperties;

/**
 * Tests Coordinator Homepage UI
 */
public class CoordHomePageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static EvaluationData firstEval;
	private static EvaluationData secondEval;
	private static EvaluationData thirdEval;
	
	private static Boolean helpWindowClosed;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		String jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/CoordHomeUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		firstEval = scn.evaluations.get("First Eval");
		secondEval = scn.evaluations.get("Second Eval");
		thirdEval = scn.evaluations.get("Third Eval");
		BackDoor.deleteCoordinators(jsonString);
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();
		helpWindowClosed = true;
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginCoord(scn.coords.get("teammates.test").id, TestProperties.inst().TEAMMATES_COMMON_PASSWORD_FOR_STUDENT_ACCOUNTS);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		if (!helpWindowClosed){
			bi.closeSelectedWindow();
		}
		BrowserInstancePool.release(bi);
		printTestClassFooter();
	}

	@Test
	public void testCoordHomePage() throws Exception{
		testCoordHomeHTML();
		testCoordHomeEvalRemindLink();
		testCoordHomeEvalPublishLink();
		testCoordHomeEvalDeleteLink();
		testCoordHomeCourseDeleteLink();
		testCoordHomeEmptyHTML();
		testHelpLink();
	}
	
	public void testHelpLink() throws Exception{
		helpWindowClosed = false;
		bi.clickAndSwitchToNewWindow(bi.helpTab);
		assertContains("<title>Teammates Onlineds Peer Feedback System for Student Team Projects - Coordinator Help</title>", bi.getCurrentPageSource());
		bi.closeSelectedWindow();
		helpWindowClosed = true;
	}
	
	public void testCoordHomeHTML() throws Exception{
		if (!helpWindowClosed){
			bi.closeSelectedWindow();
		}
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordHomeHTML.html");
	}

	public void testCoordHomeEvalRemindLink(){
		if (!helpWindowClosed){
			bi.closeSelectedWindow();
		}
		
		// Check the remind link on Open Evaluation: Evaluation 1 at Course 1
		By remindLinkLocator = bi.getCoordHomeEvaluationRemindLinkLocator(firstEval.course, firstEval.name);
		
		try{
			bi.clickAndCancel(remindLinkLocator);
		} catch (NoAlertException e){
			fail("Remind link unavailable on OPEN evaluation, or it is available but no confirmation box");
		}
	}

	public void testCoordHomeEvalPublishLink(){
		if (!helpWindowClosed){
			bi.closeSelectedWindow();
		}
		
		______TS("publish link of CLOSED evaluation");
		
		// Check the publish link on Closed Evaluation: Evaluation 3 at Course 2
		By publishLinkLocator = bi.getCoordHomeEvaluationPublishLinkLocator(thirdEval.course, thirdEval.name);
		
		try{
			bi.clickAndCancel(publishLinkLocator);
		} catch (NoAlertException e){
			fail("Publish link unavailable on CLOSED evaluation, or it is available but no confirmation box");
		}
		
		______TS("publish link of OPEN evaluation");
		
		// Check the publish link on Open Evaluation: Evaluation 1 at Course 1
		publishLinkLocator = bi.getCoordHomeEvaluationPublishLinkLocator(firstEval.course, firstEval.name);
		try{
			bi.clickAndCancel(publishLinkLocator);
			fail("Publish link available on OPEN evaluation");
		} catch (NoAlertException e){}
		
		______TS("unpublish link of PUBLISHED evaluation");

		// Check the unpublish link on Published Evaluation: Evaluation 2 at Course 1
		By unpublishLinkLocator = bi.getCoordHomeEvaluationUnpublishLinkLocator(secondEval.course, secondEval.name);
		try{
			bi.clickAndCancel(unpublishLinkLocator);
		} catch (NoAlertException e){
			fail("Unpublish link unavailable on PUBLISHED evaluation");
		}
	}

	public void testCoordHomeEvalDeleteLink() throws Exception{
		if (!helpWindowClosed){
			bi.closeSelectedWindow();
		}
		
		______TS("click and cancel");
		
		By deleteLinkLocator = bi.getCoordHomeEvaluationDeleteLinkLocator(firstEval.course, firstEval.name);
		
		try{
			bi.clickAndCancel(deleteLinkLocator);
			String evaluation = BackDoor.getEvaluationAsJson(firstEval.course, firstEval.name);
			if(isNullJSON(evaluation)) fail("Evaluation was deleted when it's not supposed to be");
		} catch (NoAlertException e){
			fail("Delete link is unavailable or it is available but no confirmation box");
		}
		
		______TS("click and confirm");
		
		try{
			bi.clickAndConfirm(deleteLinkLocator);
			bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordHomeEvalDeleteSuccessful.html");
		} catch (NoAlertException e){
			fail("Delete link is unavailable or it is available but no confirmation box");
		}
	}

	public void testCoordHomeCourseDeleteLink() throws Exception{
		if (!helpWindowClosed){
			bi.closeSelectedWindow();
		}
		
		______TS("click and cancel");
		
		By deleteLinkLocator = bi.getCoordHomeCourseDeleteLinkLocator(scn.courses.get("CHomeUiT.CS2104").id);
		
		try{
			bi.clickAndCancel(deleteLinkLocator);
			String course = BackDoor.getCourseAsJson(scn.courses.get("CHomeUiT.CS2104").id);
			if(isNullJSON(course)) fail("Course was deleted when it's not supposed to be");
		} catch (NoAlertException e){
			fail("Delete course button unavailable, or it is available but no confirmation box");
		}
		
		______TS("click and confirm");
		
		try{
			bi.clickAndConfirm(deleteLinkLocator);
			bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordHomeCourseDeleteSuccessful.html");
		} catch (NoAlertException e){
			fail("Delete course button unavailable, or it is available but no confirmation box");
		}
	}
	
	public void testCoordHomeEmptyHTML() throws Exception{
		if (!helpWindowClosed){
			bi.closeSelectedWindow();
		}
		
		BackDoor.deleteCourse(scn.courses.get("CHomeUiT.CS2104").id);
		BackDoor.deleteCourse(scn.courses.get("CHomeUiT.CS1101").id);
		
		bi.clickHomeTab();
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/coordHomeHTMLEmpty.html");
	}
}