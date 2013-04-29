package teammates.test.cases;

import static org.testng.AssertJUnit.assertEquals;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;
import org.testng.Assert;
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
 * Tests Instructor Homepage UI
 */
public class InstructorHomePageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static DataBundle scn;
	
	private static EvaluationData firstEval;
	private static EvaluationData secondEval;
	private static EvaluationData thirdEval;
	
	private static Boolean helpWindowClosed;
	
	private static String jsonString;

	@BeforeClass
	public static void classSetup() throws Exception {
		printTestClassHeader();
		
		startRecordingTimeForDataImport();
		jsonString = Common.readFile(Common.TEST_DATA_FOLDER+"/InstructorHomeUiTest.json");
		scn = Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
		firstEval = scn.evaluations.get("First Eval");
		secondEval = scn.evaluations.get("Second Eval");
		thirdEval = scn.evaluations.get("Third Eval");
		BackDoor.deleteCourses(jsonString);
		BackDoor.deleteInstructors(jsonString);
		String backDoorOperationStatus = BackDoor.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, backDoorOperationStatus);
		reportTimeForDataImport();
		helpWindowClosed = true;
		
		bi = BrowserInstancePool.getBrowserInstance();
		
		bi.loginInstructor(scn.instructors.get("teammates.test.CS2104").googleId, TestProperties.inst().TEAMMATES_COMMON_PASSWORD_FOR_STUDENT_ACCOUNTS);
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		if (!helpWindowClosed){
			bi.closeSelectedWindow();
			helpWindowClosed = true;
		}
		BrowserInstancePool.release(bi);
		printTestClassFooter();

		// Always cleanup
		BackDoor.deleteCourses(jsonString);
	}
	
	@BeforeMethod
	public void testSetup() {
		if (!helpWindowClosed){
			bi.closeSelectedWindow();
			helpWindowClosed = true;
		}
	}

	@Test
	public void testInstructorHomePage() throws Exception{
		testInstructorHomeHTML();
		testInstructorHomeEvalRemindLink();
		testInstructorHomeEvalPublishLink();
		testInstructorHomeEvalDeleteLink();
		testInstructorHomeCourseDeleteLink();
		testInstructorHomeEmptyHTML();
		testHelpLink();
	}
	
	public void testHelpLink() throws Exception{
		helpWindowClosed = false;
		bi.clickAndSwitchToNewWindow(bi.helpTab);
		assertContains("<title>Teammates Online Peer Feedback System for Student Team Projects - Instructor Help</title>", bi.getCurrentPageSource());
	}
	
	public void testInstructorHomeHTML() throws Exception{
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorHomeHTML.html");
	}

	public void testInstructorHomeEvalRemindLink(){
		
		// Check the remind link on Open Evaluation: Evaluation 1 at Course 1
		By remindLinkLocator = bi.getInstructorHomeEvaluationRemindLinkLocator(firstEval.course, firstEval.name);
		
		try{
			bi.clickAndCancel(remindLinkLocator);
		} catch (NoAlertException e){
			Assert.fail("Remind link unavailable on OPEN evaluation, or it is available but no confirmation box");
		}
	}

	public void testInstructorHomeEvalPublishLink(){
		
		______TS("publish link of CLOSED evaluation");
		
		// Check the publish link on Closed Evaluation: Evaluation 3 at Course 2
		By publishLinkLocator = bi.getInstructorHomeEvaluationPublishLinkLocator(thirdEval.course, thirdEval.name);
		
		try{
			bi.clickAndCancel(publishLinkLocator);
		} catch (NoAlertException e){
			Assert.fail("Publish link unavailable on CLOSED evaluation, or it is available but no confirmation box");
		}
		
		______TS("publish link of OPEN evaluation");
		
		// Check the publish link on Open Evaluation: Evaluation 1 at Course 1
		publishLinkLocator = bi.getInstructorHomeEvaluationPublishLinkLocator(firstEval.course, firstEval.name);
		try{
			bi.clickAndCancel(publishLinkLocator);
			Assert.fail("Publish link available on OPEN evaluation");
		} catch (NoAlertException e){}
		
		______TS("unpublish link of PUBLISHED evaluation");

		// Check the unpublish link on Published Evaluation: Evaluation 2 at Course 1
		By unpublishLinkLocator = bi.getInstructorHomeEvaluationUnpublishLinkLocator(secondEval.course, secondEval.name);
		try{
			bi.clickAndCancel(unpublishLinkLocator);
		} catch (NoAlertException e){
			Assert.fail("Unpublish link unavailable on PUBLISHED evaluation");
		}
	}

	public void testInstructorHomeEvalDeleteLink() throws Exception{
		
		______TS("click and cancel");
		
		By deleteLinkLocator = bi.getInstructorHomeEvaluationDeleteLinkLocator(firstEval.course, firstEval.name);
		
		try{
			bi.clickAndCancel(deleteLinkLocator);
			String evaluation = BackDoor.getEvaluationAsJson(firstEval.course, firstEval.name);
			if(isNullJSON(evaluation)) Assert.fail("Evaluation was deleted when it's not supposed to be");
		} catch (NoAlertException e){
			Assert.fail("Delete link is unavailable or it is available but no confirmation box");
		}
		
		______TS("click and confirm");
		
		try{
			bi.clickAndConfirm(deleteLinkLocator);
			bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorHomeEvalDeleteSuccessful.html");
		} catch (NoAlertException e){
			Assert.fail("Delete link is unavailable or it is available but no confirmation box");
		}
	}

	public void testInstructorHomeCourseDeleteLink() throws Exception{
		
		______TS("click and cancel");
		
		By deleteLinkLocator = bi.getInstructorHomeCourseDeleteLinkLocator(scn.courses.get("CHomeUiT.CS2104").id);
		
		try{
			bi.clickAndCancel(deleteLinkLocator);
			String course = BackDoor.getCourseAsJson(scn.courses.get("CHomeUiT.CS2104").id);
			if(isNullJSON(course)) Assert.fail("Course was deleted when it's not supposed to be");
		} catch (NoAlertException e){
			Assert.fail("Delete course button unavailable, or it is available but no confirmation box");
		}
		
		______TS("click and confirm");
		
		try{
			bi.clickAndConfirm(deleteLinkLocator);
			bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorHomeCourseDeleteSuccessful.html");
		} catch (NoAlertException e){
			Assert.fail("Delete course button unavailable, or it is available but no confirmation box");
		}
	}
	
	public void testInstructorHomeEmptyHTML() throws Exception{
		
		BackDoor.deleteCourse(scn.courses.get("CHomeUiT.CS2104").id);
		BackDoor.deleteCourse(scn.courses.get("CHomeUiT.CS1101").id);
		
		bi.clickHomeTab();
		// TODO: Implement with Account (Instructor with no Course)
		bi.verifyCurrentPageHTML(Common.TEST_PAGES_FOLDER+"/instructorHomeHTMLEmpty.html");
	}
}