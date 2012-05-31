package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;

import teammates.Common;
import teammates.DataBundle;
import teammates.exception.NoAlertAppearException;
import teammates.jdo.Coordinator;
import teammates.jsp.Helper;
import teammates.testing.lib.BrowserInstance;
import teammates.testing.lib.BrowserInstancePool;
import teammates.testing.lib.SharedLib;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Scenario;

public class CoordHomePageUiTest extends BaseTestCase {
	private static BrowserInstance bi;
	private static Scenario scn;
	
	private static String localhostAddress = "localhost:8080/";
	
	@BeforeClass
	public static void classSetup() throws Exception {
		assertTrue(true);
		printTestClassHeader("CoordHomeUITest");
		scn = Scenario.scenarioForPageVerification(Common.TEST_DATA_FOLDER+"CoordHomeUITest.json");
//		scn = Common.getTeammatesGson().fromJson(SharedLib.getFileContents(Common.TEST_DATA_FOLDER+"CoordHomeUITest.json"),Scenario.class);
		bi = BrowserInstancePool.getBrowserInstance();
		
		TMAPI.cleanupByCoordinator(scn.coordinator.username);

		// -----Course 1-----//
		TMAPI.createCourse(scn.course, scn.coordinator.username);
		TMAPI.enrollStudents(scn.course.courseId, scn.course.students);
		TMAPI.studentsJoinCourse(scn.students, scn.course.courseId);
		
		// ..evaluation 1 OPEN
		scn.evaluation.p2pcomments = "false";
		TMAPI.createEvaluation(scn.evaluation);
		System.out.println("Evaluation 1: "+scn.evaluation.name);
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation.name);
		TMAPI.studentsSubmitFeedbacks(scn.course.students, scn.course.courseId, scn.evaluation.name);

		// ..evaluation 2 PUBLISHED
		TMAPI.createEvaluation(scn.evaluation2);
		System.out.println("Evaluation 2: "+scn.evaluation2.name);
		TMAPI.openEvaluation(scn.course.courseId, scn.evaluation2.name);
		TMAPI.studentsSubmitFeedbacks(scn.students.subList(1, scn.students.size() - 1), scn.course.courseId, scn.evaluation2.name);
		TMAPI.closeEvaluation(scn.course.courseId, scn.evaluation2.name);
		TMAPI.publishEvaluation(scn.course.courseId, scn.evaluation2.name);

		// -----Course 2-----//
		TMAPI.createCourse(scn.course2, scn.coordinator.username);
		TMAPI.enrollStudents(scn.course2.courseId, scn.course2.students);
		TMAPI.studentsJoinCourse(scn.students, scn.course2.courseId);

		// ..evaluation 3 CLOSED
		TMAPI.createEvaluation(scn.evaluation3);
		System.out.println("Evaluation 3: "+scn.evaluation3.name);
		TMAPI.openEvaluation(scn.course2.courseId, scn.evaluation3.name);
		TMAPI.studentsSubmitFeedbacks(scn.course2.students, scn.course2.courseId, scn.evaluation3.name);
		TMAPI.closeEvaluation(scn.course2.courseId, scn.evaluation3.name);

		// ..evaluation 4 AWAITING
		TMAPI.createEvaluation(scn.evaluation4);
		System.out.println("Evaluation 4: "+scn.evaluation4.name);
		
		// ..evaluation 5 OPEN
		TMAPI.createEvaluation(scn.evaluation5);
		System.out.println("Evaluation 5: "+scn.evaluation5.name);
		TMAPI.openEvaluation(scn.course2.courseId, scn.evaluation5.name);
		
		bi.loginCoord(scn.coordinator.username, scn.coordinator.password);
		bi.goToUrl(localhostAddress+Common.JSP_COORD_HOME);
	}
	
	//@Test
	public void testCoordHomeUiFunctionality(){
		printTestCaseHeader("testCoordHomeSuccessful");
		
	}
	
	@Test
	public void testCoordHomeAddCourseLink(){
		String link = bi.getElementRelativeHref(bi.coordHomeAddNewCourseLink);
		assertEquals(Common.JSP_COORD_COURSE,link);
	}
	
	@Test
	public void testCoordHomeCourseEnrollLink(){
		String link = bi.getElementRelativeHref(bi.getCoordHomeCourseEnrollLinkLocator(scn.course.courseId));
		assertEquals(Helper.getCourseEnrollLink(scn.course.courseId),link);
	}
	
	@Test
	public void testCoordHomeCourseViewLink(){
		String link = bi.getElementRelativeHref(bi.getCoordHomeCourseViewLinkLocator(scn.course.courseId));
		assertEquals(Helper.getCourseViewLink(scn.course.courseId),link);
	}
	
	@Test
	public void testCoordHomeCourseAddEvaluationLink(){
		String link = bi.getElementRelativeHref(bi.getCoordHomeCourseAddEvaluationLinkLocator(scn.course.courseId));
		assertEquals(Common.JSP_COORD_EVAL,link);
	}
	
	@Test
	public void testCoordHomeCourseDeleteLink(){
		String link = bi.getElementRelativeHref(bi.getCoordHomeCourseDeleteLinkLocator(scn.course.courseId));
		assertEquals(Helper.getCourseDeleteLink(scn.course.courseId, Common.JSP_COORD_HOME),link);
	}
	
	@Test
	public void testCoordHomeEvalViewLink(){
		// Check View results link on Open evaluation: Evaluation 1 at Course 1
		By viewLinkLocator = bi.getCoordHomeEvaluationViewResultsLinkLocator(scn.course.courseId, scn.evaluation.name);
		String link = bi.getElementRelativeHref(viewLinkLocator);
		assertEquals("Incorrect view link",Helper.getEvaluationViewLink(scn.course.courseId, scn.evaluation.name),link);
		assertFalse("View link unavailable on OPEN evaluation","none".equals(bi.getDriver().findElement(viewLinkLocator).getCssValue("text-decoration")));
		assertFalse("View link unavailable on OPEN evaluation","return false".equals(bi.getElementAttribute(viewLinkLocator, "onclick")));
		
		// Check View results link on Awaiting evaluation: Evaluation 4 at Course 2
		viewLinkLocator = bi.getCoordHomeEvaluationViewResultsLinkLocator(scn.course2.courseId, scn.evaluation4.name);
		link = bi.getElementRelativeHref(viewLinkLocator);
		assertEquals(Helper.getEvaluationViewLink(scn.course2.courseId, scn.evaluation4.name),link);
		assertEquals("View link available on AWAITING evaluation","none",bi.getDriver().findElement(viewLinkLocator).getCssValue("text-decoration"));
		assertEquals("View link available on AWAITING evaluation","return false",bi.getElementAttribute(viewLinkLocator, "onclick"));
	}
	
	@Test
	public void testCoordHomeEvalEditLink(){
		By editLinkLocator = bi.getCoordHomeEvaluationEditLinkLocator(scn.course.courseId,scn.evaluation.name);
		String link = bi.getElementRelativeHref(editLinkLocator);
		assertEquals("Incorrect edit link",Helper.getEvaluationEditLink(scn.course.courseId, scn.evaluation.name),link);
		assertFalse("Edit link unavailable","none".equals(bi.getDriver().findElement(editLinkLocator).getCssValue("text-decoration")));
		assertFalse("Edit link unavailable","return false".equals(bi.getElementAttribute(editLinkLocator, "onclick")));
	}
	
	@Test
	public void testCoordHomeEvalDeleteLink(){
		By deleteLinkLocator = bi.getCoordHomeEvaluationDeleteLinkLocator(scn.course.courseId, scn.evaluation.name);
		String link = bi.getElementRelativeHref(deleteLinkLocator);
		assertEquals(Helper.getEvaluationDeleteLink(scn.course.courseId, scn.evaluation.name, Common.JSP_COORD_HOME),link);
		
		try{
			bi.clickAndCancel(deleteLinkLocator);
		} catch (NoAlertAppearException e){
			assertTrue("Delete link unavailable",false);
		}
	}
	
	@Test
	public void testCoordHomeEvalRemindLink(){
		// Check the remind link on Open Evaluation: Evaluation 1 at Course 1
		By remindLinkLocator = bi.getCoordHomeEvaluationRemindLinkLocator(scn.course.courseId, scn.evaluation.name);
		
		try{
			bi.clickAndCancel(remindLinkLocator);
		} catch (NoAlertAppearException e){
			assertTrue("Remind link unavailable on OPEN evaluation",false);
		}
	}
	
	@Test
	public void testCoordHomeEvalPublishLink(){
		// Check the publish link on Closed Evaluation: Evaluation 3 at Course 2
		By publishLinkLocator = bi.getCoordHomeEvaluationPublishLinkLocator(scn.course2.courseId,scn.evaluation3.name);
		
		try{
			bi.clickAndCancel(publishLinkLocator);
		} catch (NoAlertAppearException e){
			assertTrue("Publish link unavailable on CLOSED evaluation",false);
		}
		
		// Check the publish link on Open Evaluation: Evaluation 1 at Course 1
		publishLinkLocator = bi.getCoordHomeEvaluationPublishLinkLocator(scn.course.courseId, scn.evaluation.name);
		try{
			bi.clickAndCancel(publishLinkLocator);
			assertTrue("Publish link available on OPEN evaluation",false);
		} catch (NoAlertAppearException e){
			assertTrue(true);
		}
	}
	
	@AfterClass
	public static void classTearDown() throws Exception {
		bi.logout();
		TMAPI.deleteCourse(scn.course.courseId);
		TMAPI.deleteCourse(scn.course2.courseId);
		
		BrowserInstancePool.release(bi);
		printTestClassFooter("CoordHomeUITest");
	}
}