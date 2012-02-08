package teammates.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SeleneseCommandExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;

import teammates.testing.config.Config;
import teammates.testing.lib.SharedLib;
import teammates.testing.object.Evaluation;
import teammates.testing.object.Scenario;
import teammates.testing.object.Student;
import teammates.testing.object.TeamFormingSession;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * Base class for all testing classes. Provide basic facilities
 * 
 * @author Huy
 * 
 */
public class BaseTest {
	protected static DefaultSelenium selenium;
	protected static WebDriver driver;
	protected static Scenario sc;
	protected static Scenario nsc;
	protected static ChromeDriverService chromeService = null;

	// ----------------------UI Element:
	/**
	 * homepage:
	 */
	public static By coordLoginButton = By.name("COORDINATOR_LOGIN");
	public static By studentLoginButton = By.name("STUDENT_LOGIN");

	/**
	 * tabs:
	 */
	public static By courseTab = By.className("t_courses");
	public static By evaluationTab = By.className("t_evaluations");
	public static By logoutTab = By.className("t_logout");

	/**
	 * course:
	 */
	// course default:
	public static By inputCourseID = By.id("courseid");
	public static By inputCourseName = By.id("coursename");
	public static By addCourseButton = By.id("btnAddCourse");

	public static By courseIDSorting = By.id("button_sortcourseid");
	public static By courseNameSorting = By.id("button_sortname");
	public static By courseJoinStatusSorting = By.id("button_sortstudentstatus");

	public static By courseID = By.className("t_course_code");
	public static By courseName = By.className("t_course_name");
	public static By courseTeams = By.className("t_course_teams");
	public static By courseEnrol = By.className("t_course_enrol");
	public static By courseView = By.className("t_course_view");
	public static By courseDelete = By.className("t_course_delete");

	// enrol:
	public static By enrolInfo = By.id("information");
	public static By enrolButton = By.id("button_enrol");
	public static By enrolBackButton = By.className("t_back");

	// view:
	public static By remindStudentsButton = By.id("button_remind");
	public static By deleteStudentsButton = By.className("t_delete_students");
	public static By studentDetailKey = By.id("t_courseKey");
	public static By studentDetailBackButton = By.className("t_back");
	public static By courseViewBackButton = By.className("t_back");

	/**
	 * evaluation:
	 */
	// evaluation default:
	public static By inputEvaluationName = By.id("evaluationname");
	public static By inputInstruction = By.id("instr");
	public static By inputClosingTime = By.id("deadlinetime");
	public static By inputClosingDate = By.xpath("//*[@id='deadline']");
	public static By inputGracePeriod = By.id("graceperiod");
	public static By inputProfileTemplate = By.id("profile_template");
	public static By addEvaluationButton = By.id("t_btnAddEvaluation");
	public static By createTeamFormingSessionButton = By.id("t_btnCreateTeamFormingSession");

	public static By evaluationView = By.className("t_eval_view");
	public static By evaluationEdit = By.className("t_eval_edit");
	public static By evaluationPublish = By.className("t_eval_publish");
	public static By evaluationUnpublish = By.className("t_eval_unpublish");
	public static By evaluationDelete = By.className("t_eval_delete");

	// edit evaluation:
	public static By editEvaluationButton = By.id("button_editevaluation");	
	public static By editEvaluationBackButton = By.className("t_back");
	
	//edit team forming session:
	public static By editTeamFormingSessionButton = By.id("button_editteamformingsession");

	// result:
	public static By resultSummaryRadio = By.id("radio_summary");
	public static By resultDetailRadio = By.id("radio_detail");
	public static By resultReviewerRadio = By.id("radio_reviewer");
	public static By resultRevieweeRadio = By.id("radio_reviewee");

	public static By resultPublishButton = By.id("button_publish");
	public static By resultBackButton = By.id("button_back");

	// summary result:
	public static By resultTeamSorting = By.id("button_sortteamname");
	public static By resultStudentSorting = By.id("button_sortname");
	public static By resultSubmittedSorting = By.id("button_sortsubmitted");
	public static By resultClaimedSorting = By.id("button_sortaverage");
	public static By resultDifferenceSorting = By.id("button_sortdiff");
	public static By resultEditButton = By.id("button_editevaluationresultsbyreviewee");
	public static By resultEditCancelButton = By.id("button_back");

	// individual result:
	public static By resultNextButton = By.id("button_next");
	public static By resultPreviousButton = By.id("button_previous");
	public static By resultIndividualEditButton = By.id("button_edit");

	public static By pointReviewerIndividualClaimed = By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='data']//tr[%d]//td[%d]", 2, 2));;
	public static By pointReviewerIndividualPerceived = By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='data']//tr[%d]//td[%d]", 3, 2));
	public static By pointRevieweeIndividualClaimed = By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='data']//tr[%d]//td[%d]", 3, 2));
	public static By pointRevieweeIndividualPerceived = By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='data']//tr[%d]//td[%d]", 4, 2));

	public static String getReviewerIndividualClaimedPoint() {
		String equal = getElementText(By.xpath(String.format("//div[@class='result_table']//th[%d]//div[%d]", 2, 1)));
		String point = getElementText(By.xpath(String.format("//div[@class='result_table']//th[%d]//span[%d]", 2, 2)));
		return equal + point;
	}

	// detail result:
	public static By resultTopButton = By.id("button_top");

	/**
	 * coordinator:
	 */
	public static By coordEvaluationSubmitButton = By.id("button_editevaluationresultsbyreviewee");
	/**
	 * student:
	 */
	// student course:
	public static By inputRegKey = By.id("regkey");
	public static By studentJoinCourseButton = By.id("btnJoinCourse");
	// student evaluation:
	public static By studentSubmitEvaluationButton = By.name("submitEvaluation");
	public static By studentEvaluationBackButton = By.className("t_back");
	public static By studentEvaluationCancelButton = By.className("t_back");

	/**
	 * message:
	 */
	public static By courseMessage = By.xpath("//div[@id='statusMessage']/font[1]");
	public static By courseErrorMessage = By.xpath("//div[@id='statusMessage']/font[2]");
	public static By statusMessage = By.id("statusMessage");
	public static By editEvaluationResultsStatusMessage = By.id("coordinatorEditEvaluationResultsStatusMessage");

	/**
	 * message contents:
	 */
	public static final String MESSAGE_COURSE_EXISTS = "The course already exists.";
	public static final String MESSAGE_COURSE_ADDED = "The course has been added. Click the 'Enrol' link in the table below to add students to the course.";
	public static final String MESSAGE_COURSE_DELETED = "The course has been deleted.";
	public static final String MESSAGE_COURSE_DELETEDSTUDENT = "The student has been removed from the course.";
	public static final String MESSAGE_COURSE_DELETEDALLSTUDENTS = "All students have been removed from the course. Click here to enrol students.";
	
	
	// -----------------------------UI Actions ----------------------------->> Homepage:
	/**
	 * Coordinator Login
	 * 
	 * @page Home page
	 */
	public static void coordinatorLogin(String username, String password) {

		cout("Logging in coordinator " + username + ".");
		// Click the Coordinator button on the main page
		wdClick(By.name("COORDINATOR_LOGIN"));
		waitForPageLoad();
		/*
		 * IE Fix: for some reason in IE new profile is not created, thus user is already logged in. This will log user out.
		 */
		if (isElementPresent(By.className("t_logout"))) {
			driver.findElement(By.className("t_logout")).click();
			waitForPageLoad();
			// Check that we're at the main page after logging out
			verifyMainPage();

			wdClick(By.name("COORDINATOR_LOGIN"));
			waitForPageLoad();
		}
		_login(username, password);

		verifyCoordinatorPage();
		waitAWhile(1000);
	}

	/**
	 * Student Login
	 * 
	 * @page Homepage
	 */
	public static void studentLogin(String username, String password) {

		cout("Logging in student " + username + ".");
		// Click the Student button on the main page
		wdClick(By.name("STUDENT_LOGIN"));
		waitForPageLoad();
		/*
		 * IE Fix: for some reason in IE new profile is not created, thus user is already logged in. This will log user out.
		 */
		if (isElementPresent(By.className("t_logout"))) {
			driver.findElement(By.className("t_logout")).click();
			waitForPageLoad();
			// Check that we're at the main page after logging out
			verifyMainPage();
			wdClick(By.name("COORDINATOR_LOGIN"));
			waitForPageLoad();
		}
		_login(username, password);
		verifyStudentPage();
	}

	/**
	 * Coordinator & Student Logout
	 */
	public void logout() {
		cout("Signing out.");
		waitAndClick(By.className("t_logout"));
		// Check that we're at the main page after logging out
		if(Config.inst().TEAMMATES_URL.contains("localhost")) {
			cout("localhost testing");
			selenium.open(Config.inst().TEAMMATES_URL);
			
		}
		verifyMainPage();
	}

	// -----------------------------UI Actions ----------------------------->> Student:
	public static void studentClickEvaluationViewResults(int row) {
		By link = By.xpath(String.format("//div[@id='studentPastEvaluations']//table[@id='dataform']//tr[%d]//td[%d]//a[1]", row + 2, 5));
		waitAndClick(link);
	}

	public static void studentClickDoEvaluation(int row) {
		waitAndClickAndCheck(By.id("doEvaluation" + row), By.id("points0"));
	}

	public static By studentGetEvaluationStatus(int row) {
		return By.className("t_eval_status");
	}

	public static int studentCountCourses() {
		WebElement dataform = driver.findElement(By.id("dataform"));
		return dataform.findElements(By.tagName("tr")).size();
	}

	public static void studentClickEditEvaluation(int row) {
		waitAndClick(By.id("editEvaluation" + row));
	}

	// -----------------------------UI Actions ----------------------------->> Course:
	/**
	 * Snippet to go to Courses page
	 */
	public static void gotoCourses() {
		waitAndClick(By.className("t_courses"));
		justWait();
		verifyCoordinatorPage();
	}

	public static void clickCourseTab() {
		waitAndClick(By.className("t_courses"));
	}

	/**
	 * page: Display Course
	 * 
	 * @param row
	 */
	public static void addCourse(String courseid, String coursename, int courseIndex) {
		wdFillString(inputCourseID, courseid);
		wdFillString(inputCourseName, coursename);
		//wdClick(addCourseButton);
		waitAndClickAndCheck(addCourseButton, By.id("courseID"+courseIndex));
		justWait();
	}
	
	public static void addCourse(String courseid, String coursename) {
		wdFillString(inputCourseID, courseid);
		wdFillString(inputCourseName, coursename);
		wdClick(addCourseButton);
		justWait();
	}

	public static By getCourseID(int row) {
		return By.id("courseID" + row);
	}

	public static void getCourseName(int row) {

	}

	public static String getCourseTeams(int row) {
		if(row == 0) {
			return getElementText(courseTeams);
		}
		else {
			return getElementText(By.xpath(String.format("//table[@id='dataform']//tr[%d]//td[3]", row + 2)));
		}
	}
	
	public static String getCourseTotalStudents(int row) {
		return getElementText(By.xpath(String.format("//table[@id='dataform']/tbody/tr[%d]/td[4]", row + 2)));
	}

	public static String getCourseUnregisteredStudents(int row) {
		return getElementText(By.xpath(String.format("//table[@id='dataform']/tbody/tr[%d]/td[5]", row + 2)));
	}

	public static void clickCourseEnrol(int row) {
		// first row:
		waitAndClick(By.className("t_course_enrol"));
	}

	public static void clickCourseView(int row) {
		// first row:
		if(row == 0) {
			waitAndClick(By.className("t_course_view"));	
		}
		else {
			waitAndClick(By.xpath(String.format("//table[@id='dataform']//tr[%d]//td[6]//a[@class='t_course_view']", row + 2)));
		}
	}

	public static void clickAndConfirmCourseDelete(int row) {
		// first row:
		clickAndConfirm(By.className("t_course_delete"));
	}
	
	public static int countTotalCourses() {
		if(getElementText(By.xpath(String.format("//table[@id='dataform']//tr[2]//td[1]"))).isEmpty()){
			return 0;
		}
		else {
			WebElement dataform = driver.findElement(By.id("dataform"));
			return dataform.findElements(By.tagName("tr")).size() - 1;
		}
	}

	/**
	 * page: Enrol Student
	 */
	public static void enrollStudents(List<Student> students) {
		// To Enroll page
		waitAndClick(By.className("t_course_enrol"));
		verifyEnrollPage();

		wdFillString(By.id("information"), getStudentsString(students));
		wdClick(By.id("button_enrol"));
		justWait();
	}

	public static void clickEnrolStudents() {
		wdClick(By.className("t_course_enrol"));
	}

	public static void clickEnrolBack() {
		wdClick(By.className("t_back"));
	}

	/**
	 * page: View course (Course Detail)
	 */
	public static By getCourseDetailCourseID() {

		return By.cssSelector("#coordinatorCourseTable td.t_course_code");
	}

	public static void getCourseDetailCourseName() {

	}

	public static void getCourseDetailTeams() {

	}

	public static void getCourseDetailTotalStudents() {

	}

	public static void clickCourseDetailView(int row) {
		waitAndClick(By.xpath(String.format("//table[@id='dataform']//tr[%d]//a[1]", row + 2)));
	}

	public static void clickCourseDetailEdit(int row) {
		By link = By.xpath(String.format("//table[@id='dataform']//tr[%d]//a[2]", row + 2));
		waitAndClick(link);
	}

	public static void clickCourseDetailInvite(int row) {
		By link = By.xpath(String.format("//div[@id='coordinatorStudentTable']//table[@id='dataform']//tr[%d]//td[%d]//a[3]", row + 2, 4));
		waitAndClick(link);
	}

	public static void clickAndConfirmCourseDetailDelete(int row) {
		waitAndClickAndConfirm(By.xpath(String.format("//table[@id='dataform']//tr[%d]//a[3]", row + 2)));
	}

	public static int countCourseDetailStudents() {
		WebElement htmldiv = driver.findElement(By.id("coordinatorStudentTable"));
		return htmldiv.findElements(By.tagName("tr")).size();
	}

	// edit student:

	/**
	 * Delete all available courses.
	 */
	public static void deleteAllCourses() throws Exception {
		while (driver.findElements(By.cssSelector("#coordinatorCourseTable tr")).size() > 1 && isElementPresent(By.className("t_course_delete"))) {
			System.out.println("Deleting a course...");
			clickAndConfirm(By.className("t_course_delete"));
			waitForElementText(statusMessage, MESSAGE_COURSE_DELETED);
			gotoCourses();
		}
	}

	/**
	 * Delete all students
	 */
	public static void deleteAllStudents() {
		System.out.println("delete all students");
		driver.findElement(By.className("t_courses")).click();
		waitAndClick(By.className("t_course_view"));
		waitForElementPresent(By.id("dataform tr"));
		WebElement dataform = driver.findElement(By.id("dataform"));
		while (dataform.findElements(By.tagName("tr")).size() > 1) {
			System.out.println("Delete a student...");
			By by = By.xpath(String.format("//table[@id='dataform']//tr[%d]//a[4]", 2));
			waitForElementPresent(by);
			clickAndConfirm(by);
			waitForElementPresent(By.id("dataform tr"));
		}
	}

	// -----------------------------UI Actions ----------------------------->> Evaluation:
	/**
	 * Snippet to go to Evaluations page
	 */
	public static void gotoEvaluations() {
		wdClick(By.className("t_evaluations"));
		justWait();
		verifyEvaluationPage();
	}
	
	//by kalpit
	public static void gotoTeamForming() {
		wdClick(By.className("t_teamForming"));
		justWait();
		verifyTeamFormingPage();
	}
	
	public static void clickTeamFormingTab() {
		wdClick(By.className("t_teamForming"));
	}
	//end by kalpit

	public static void clickEvaluationTab() {
		wdClick(By.className("t_evaluations"));
	}

	/**
	 * page: evaluation
	 * 
	 * @param eval
	 */
	public static void addEvaluation(Evaluation eval, int evalIndex) {
		clickEvaluationTab();
		// Select the course
		waitAndClick(inputCourseID);
		cout("click " + eval.courseID);
		selectDropdownByValue(By.id("courseid"), eval.courseID);
		
		// Fill in the evaluation name
		wdFillString(inputEvaluationName, eval.name);
		// Allow P2P comment
		wdClick(By.xpath("//*[@id='commentsstatus'][@value='" + eval.p2pcomments + "']"));
		// Fill in instructions
		wdFillString(inputInstruction, eval.instructions);
		// Select deadline date
		wdClick(inputClosingDate);
		selenium.waitForPopUp("window_deadline", "30000");
		selenium.selectWindow("name=window_deadline");
		wdClick(By.xpath("//a[contains(@href, '" + eval.dateValue + "')]"));
		for (String s : driver.getWindowHandles()) {
			selenium.selectWindow(s);
			break;
		}
		selectDropdownByValue(inputClosingTime, eval.nextTimeValue);
		// Select grace period
		selectDropdownByValue(inputGracePeriod, Integer.toString(eval.gracePeriod));
		// Submit the form
		justWait();
		//wdClick(addEvaluationButton);
		waitAndClickAndCheck(addEvaluationButton, By.id("evaluation"+evalIndex));
	}
	
	//by kalpit
	/**
	 * page: team forming
	 * 
	 * @param teamForming
	 */
	public static void addTeamFormingSession(TeamFormingSession teamForming, int teamFormingIndex) {
		clickTeamFormingTab();
		// Select the course
		waitAndClick(inputCourseID);
		selectDropdownByValue(By.id("courseid"), teamForming.courseID);
		
		// Fill in instructions
		wdFillString(inputInstruction, teamForming.instructions);
		// Fill in profile template
		wdFillString(inputProfileTemplate, teamForming.profileTemplate);
		// Select deadline date
		wdClick(inputClosingDate);
		selenium.waitForPopUp("window_deadline", "30000");
		selenium.selectWindow("name=window_deadline");
		wdClick(By.xpath("//a[contains(@href, '" + teamForming.dateValue + "')]"));
		for (String s : driver.getWindowHandles()) {
			selenium.selectWindow(s);
			break;
		}
		selectDropdownByValue(inputClosingTime, teamForming.nextTimeValue);
		// Select grace period
		selectDropdownByValue(inputGracePeriod, Integer.toString(teamForming.gracePeriod));
		// Submit the form
		justWait();
		//wdClick(addEvaluationButton);
		waitAndClickAndCheck(createTeamFormingSessionButton, By.id("teamFormingSession"+teamFormingIndex));
	}
	//end by kalpit

	public static void clickEvaluationSortCourseID() {
		waitAndClick(courseIDSorting);
	}

	public static void clickEvaluationSortEvaluation() {
		waitAndClick(courseNameSorting);
	}

	public static By getEvaluationStatus(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]", row + 2, 3));
	}

	public static void clickEvaluationViewResults(int row) {
		By link = By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_view']", row + 2, 5));
		waitAndClick(link);
	}

	public static void clickEvaluationEdit(int row) {
		By link = By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_edit']", row + 2, 5));
		waitAndClick(link);
	}

	public static void clickEvaluationPublish(int row) {
		By link = By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_publish']", row + 2, 5));
		clickAndConfirm(link);
	}

	public static void clickEvaluationUnpublish(int row) {
		By link = By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_unpublish']", row + 2, 5));
		clickAndConfirm(link);
	}

	public static void clickAndConfirmEvaluationRemind(int row) {
		By link = By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_remind']", row + 2, 5));
		clickAndConfirm(link);
	}

	public static void clickAndConfirmEvaluationDelete(int row) {
		By link = By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_delete']", row + 2, 5));
		clickAndConfirm(link);
	}

	public static void clickAndCancelEvaluationDelete(int row) {
		By link = By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_delete']", row + 2, 5));
		waitAndClickAndCancel(link);
	}
	
	public static int countTotalEvaluations() {

		if(getElementText(By.xpath(String.format("//table[@id='dataform']//tr[2]//td[1]"))).isEmpty()){
			return 0;
		}
		else {
			WebElement dataform = driver.findElement(By.id("dataform"));
			return dataform.findElements(By.tagName("tr")).size() - 1;
		}
	}

	/**
	 * @page view evaluation result (result)
	 */
	// reviewer summary
	public static void clickReviewerSummaryView(int row) {
		waitAndClick(By.id("viewEvaluationResults" + row));
	}

	public static void clickReviewerSummaryEdit(int row) {
		waitAndClick(By.id("editEvaluationResults" + row));
	}

	public static int countReviewerSummaryStudents() {
		WebElement htmldiv = driver.findElement(By.id("coordinatorEvaluationSummaryTable"));
		return htmldiv.findElements(By.tagName("tr")).size();
	}

	// reviewee summary:
	public static By getRevieweeSummaryClaimed(int studentIndex) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", studentIndex + 2, 3));
	}

	public static By getRevieweeSummaryDifference(int studentIndex) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", studentIndex + 2, 4));
	}

	public static void clickRevieweeSummaryView(int row) {
		waitAndClick(By.id("viewEvaluationResults" + row));
	}

	// reviewer individual:
	public static By getReviewerIndividualToStudent(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", row + 2, 1));
	}

	public static By getReviewerIndividualToStudentPoint(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", row + 2, 2));
	}

	// reviewee individual:
	public static By getRevieweeIndividualFromStudent(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", row + 2, 1));
	}

	public static By getRevieweeIndividualFromStudentPoint(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", row + 2, 2));
	}

	// reviewer detail:
	public static By getReviewerDetailClaimed(int team, int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[%d]//tr[%d]//td[%d]", team, row + 2, 2));
	}

	public static By getReviewerDetailPerceived(int team, int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[%d]//tr[%d]//td[%d]", team, row + 3, 2));
	}

	public static By getReviewerDetailToStudent(int position, int studentIndex) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//tr[%d]//table[@id='dataform']//tr[%d]//td[%d]", position + 7, studentIndex + 2, 1));
	}

	public static By getReviewerDetailToStudentPoint(int position, int studentIndex) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//tr[%d]//table[@id='dataform']//tr[%d]//td[%d]", position + 7, studentIndex + 2, 2));
	}

	// reviewee detail:
	public static By getRevieweeDetailClaimed(int teamIndex, int position) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[%d]//tr[%d]//td[%d]", teamIndex, position + 2, 2));
	}

	public static By getRevieweeDetailPerceived(int teamIndex, int position) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[%d]//tr[%d]//td[%d]", teamIndex, position + 3, 2));
	}

	public static By getRevieweeDetailFromStudent(int position, int studentIndex) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//tr[%d]//table[@id='dataform']//tr[%d]//td[%d]", position + 7, studentIndex + 2, 1));
	}

	public static By getRevieweeDetailFromStudentPoint(int position, int studentIndex) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//tr[%d]//table[@id='dataform']//tr[%d]//td[%d]", position + 7, studentIndex + 2, 2));
	}

	/**
	 * @page edit evaluation result
	 */
	public static By getSubmissionPoint(int row) {
		return By.id("points" + row);
	}

	public static void setSubmissionPoint(int row, String points) {
		selectDropdownByValue(By.id("points" + row), points);
	}

	public static By getSubmissionJustification(int row) {
		return By.name("justification" + row);
	}

	public static void setSubmissionJustification(int row, String justification) {
		wdFillString(By.name("justification" + row), justification);
	}

	public static By getSubmissionComments(int row) {
		return By.name("commentstostudent" + row);
	}

	public static void setSubmissionComments(int row, String comments) {
		wdFillString(By.name("commentstostudent" + row), comments);
	}

	/**
	 * Delete all evaluations
	 * 
	 * Must be in Evaluations page
	 */
	public static void deleteAllEvaluations() {

		while (driver.findElements(evaluationDelete).size() > 1) {
			System.out.println("Deleting 1 evaluation...");
			clickAndConfirm(evaluationDelete);
			waitForElementText(statusMessage, "The evaluation has been deleted.");
			gotoEvaluations(); // This is to fix for Datastore delay problem
		}
	}

	// -----------------------------Helper Functions----------------------------->> Setup:

	/**
	 * Start Chrome service, return service instance
	 * 
	 * @return the service instance
	 */
	private static ChromeDriverService startChromeDriverService() {
		chromeService = new ChromeDriverService.Builder().usingChromeDriverExecutable(new File(Config.inst().getChromeDriverPath())).usingAnyFreePort().build();
		try {
			chromeService.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return chromeService;
	}

	protected static void setupSelenium() {
		System.out.println("Initializing Selenium.");

		if (Config.inst().BROWSER.equals("htmlunit")) {

			System.out.println("Using HTMLUnit.");

			driver = new HtmlUnitDriver();

			selenium = new WebDriverBackedSelenium(driver, Config.inst().TEAMMATES_URL);

		} else if (Config.inst().BROWSER.equals("firefox")) {

			System.out.println("Using Firefox.");

			driver = new FirefoxDriver();
			selenium = new WebDriverBackedSelenium(driver, Config.inst().TEAMMATES_URL);

		} else if (Config.inst().BROWSER.equals("chrome")) {

			System.out.println("Using Chrome");

			// Use technique here:
			// http://code.google.com/p/selenium/wiki/ChromeDriver
			ChromeDriverService service = startChromeDriverService();
			driver = new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome());

			System.out.println(driver.toString());
			selenium = new WebDriverBackedSelenium(driver, Config.inst().TEAMMATES_URL);

			/*
			 * Chrome hack. Currently Chrome doesn't support confirm() yet. http://code.google.com/p/selenium/issues/detail?id=27
			 */
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.confirm = function(msg){ return true;};");

		} else {

			System.out.println("Using " + Config.inst().BROWSER);

			// iexplore, opera, safari. For some not-supported-yet browsers, we
			// use
			// legacy methods: Going through the RC server.
			String selBrowserIdentifierString = "*" + Config.inst().BROWSER;

			selenium = new DefaultSelenium("localhost", 4444, selBrowserIdentifierString, Config.inst().TEAMMATES_URL);
			CommandExecutor executor = new SeleneseCommandExecutor(selenium);
			DesiredCapabilities dc = new DesiredCapabilities();
			driver = new RemoteWebDriver(executor, dc);

		}

		selenium.windowMaximize();
		selenium.open("/");

	}

	protected static void setupScenario() {
		sc = Scenario.fromJSONFile("target/test-classes/scenario.json");
	}
	
	protected static void setupNewScenarioForMultipleCourses(){
		nsc = Scenario.newScenario("target/test-classes/scenario.json");
	}

	protected static void setupScenarioForBumpRatioTest(int index) {
		sc = Scenario.scenarioForBumpRatioTest("bump_ratio_scenario.json", index);
	}

	/**
	 * Called when the run is over.
	 */
	protected static void wrapUp() {
		selenium.stop();
		if (chromeService != null && chromeService.isRunning())
			chromeService.stop();
	}

	// -----------------------------Helper Functions----------------------------->> Wait, Click, Fill in Elements:
	/**
	 * waiting functions:
	 */
	public static void justWait() {
		waitAWhile(1500);
	}

	public static void waitAWhile(long miliseconds) {
		try {
			Thread.sleep(miliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Short snippet to wait for page-load.
	 * 
	 * Must be appended after every action that requires a page reload or an AJAX request being made
	 * 
	 * huy (Aug 26) - This should be deprecated. Since WebDriver makes sure the new page is loaded before returning the call
	 */
	public static void waitForPageLoad() {
		try {
			selenium.waitForPageToLoad("15000");
		} catch (SeleniumException e) {
			System.err.println(e.getMessage());
		}
	}

	public static void waitForElementPresent(By by) {
		int counter = 0;
		while (!isElementPresent(by)) {
			if (counter++ > 300)
				fail("Timeout");
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
	}

	public static void waitForElementText(By locator, String value) {
		int counter = 0;
		while (true) {

			System.out.println(locator + ": " + getElementText(locator));
			if (isElementPresent(locator) && getElementText(locator).equals(value))
				return;
			if (counter++ > 50)
				fail("Timeout");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}

	public static void waitforElementTextChange(By locator) {
		String oldMessage = getElementText(locator);
		System.out.println(oldMessage);
		int counter = 0;
		while (true) {
			waitAWhile(500);
			if (counter++ > 50)
				fail("Timeout");
			if (!getElementText(locator).equals(oldMessage))
				break;
		}
	}

	/**
	 * Click functions: WebDriver click on element
	 */
	public static void wdClick(By by) {
		if (isElementPresent(by)) {
			driver.findElement(by).click();
		} else {
			fail("Element " + by.toString() + " does not exists.");
		}
	}

	public static void waitAndClick(By by) {
		waitForElementPresent(by);
		driver.findElement(by).click();
	}
	
	public static void waitAndClickAndCheck(By currentElement, By nextElement) {
		while(!isElementPresent(nextElement)){
			waitForElementPresent(currentElement);
			driver.findElement(currentElement).click();
		}
	}

	public static void waitAndClickAndConfirm(By by) {
		waitForElementPresent(by);
		clickAndConfirm(by);
	}

	/**
	 * click and confirm functions:
	 */
	private static void confirmYes() {
		/*
		 * if (!Config.inst().BROWSER.equals("chrome")) { Alert alert = driver.switchTo().alert(); alert.accept(); }
		 */
	}

	private static void confirmNo() {
		/*
		 * if (!Config.inst().BROWSER.equals("chrome")) { Alert alert = driver.switchTo().alert(); alert.dismiss(); }
		 */
	}

	public static void chromeConfirmYes() {
		/*
		 * Huy: I have no idea why the driver.switchTo().alert() approach doesn't work even in Firefox (it supposed to!). This is a workaround to press Yes in the confirmation box. Same for function
		 * below for No.
		 */

		// if (Config.inst().BROWSER.equals("chrome")) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.confirm = function(msg){ return true;};");
		// }
	}

	public static void chromeConfirmNo() {
		// if (Config.inst().BROWSER.equals("chrome")) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.confirm = function(msg){ return false;};");
		// }
	}

	public static void clickAndConfirm(By by) {
		chromeConfirmYes();
		waitAndClick(by);
		confirmYes();
	}

	public static void waitAndClickAndCancel(By by) {
		chromeConfirmNo();
		waitAndClick(by);
		confirmNo();
	}

	/**
	 * WebDriver fills the input field with text value (will clear the data first)
	 */
	protected static void wdFillString(By by, String value) {
		WebElement ele = driver.findElement(by);
		ele.clear();
		ele.sendKeys(value);
	}

	protected static boolean isElementPresent(By by) {
		return driver.findElements(by).size() != 0;
	}

	/**
	 * Retrieve element's text through WebDriver.
	 * 
	 * Similar to getElementText()
	 * 
	 * @return empty string if element is not found.
	 */
	public static String getElementText(By locator) {
		if (!isElementPresent(locator))
			return "";
		WebElement elm = driver.findElement(locator);
		return elm.getText();
	}

	/**
	 * Retrieve the element's `value` attribute. Usually used for elements like input, option, etc.
	 * 
	 * @param locator
	 * @return
	 */
	public static String getElementValue(By locator) {
		return driver.findElement(locator).getAttribute("value");
	}

	public static void selectDropdownByValue(By locator, String value) {
		Select select = new Select(driver.findElement(locator));
		justWait();
		select.selectByValue(value);
	}

	public static String getDropdownSelectedValue(By locator) {
		Select select = new Select(driver.findElement(locator));
		return select.getFirstSelectedOption().getAttribute("value");
	}

	// -----------------------------Helper Functions----------------------------->> Check and Verify:
	/**
	 * Helper method to check that we're at the main page Checking for the Coordinator and Student links
	 */
	public static void verifyMainPage() {
		for (int x = 0;; x++) {
			if (x >= 40)
				fail("timeout");

			if (isElementPresent(By.name("STUDENT_LOGIN")) && isElementPresent(By.name("COORDINATOR_LOGIN")))
				break;

			waitAWhile(1000);
		}
	}

	/**
	 * For App Engine Local Run - Check if we're at the local login.
	 * 
	 */
	public static boolean isLocalLoginPage() {
		if (isElementPresent(By.id("email")) && isElementPresent(By.id("isAdmin")))
			return true;
		return false;
	}

	/**
	 * Helper method to check that we're at the login page Checking for the e-mail and password fields, and the sign in button
	 */
	public static boolean isGoogleLoginPage() {
		if (isElementPresent(By.id("Email")) && isElementPresent(By.id("Passwd")) && isElementPresent(By.id("signIn")))
			return true;

		return false;
	}

	// WS: add function verifyGoogleLoginPage
	public static void verifyGoogleLoginPage() {
		if (!isGoogleLoginPage())
			return;
		fail("Not in Google Login Page");
	}

	// Helper method to check that we're at the Coordinator page (after login)
	// Checking for links at the top, and add course form
	public static void verifyCoordinatorPage() {
		if (isElementPresent(By.id("courseid")) && isElementPresent(By.id("coursename")))
			return;

		fail("Not in Coordinator Page");
	}

	// Helper method to check that we're at the Student page (after login)
	// Checking for links at the top, and add course form
	public static void verifyStudentPage() {
		for (int x = 0;; x++) {
			if (x >= 40)
				fail("timeout");

			if (isElementPresent(By.id("regkey")) && isElementPresent(By.className("t_evaluations")) && isElementPresent(By.className("t_logout")) && isElementPresent(By.className("t_courses")))
				break;

			waitAWhile(200);
		}
	}

	// Helper method to check that we're at the evaluations page
	// Checks for the various fields expected.
	public static void verifyEvaluationPage() {
		for (int x = 0;; x++) {
			if (x >= 40)
				fail("timeout");

			if ((isElementPresent(By.id("courseid"))) && (isElementPresent(By.id("evaluationname"))) && (isElementPresent(By.xpath("//*[@id='commentsstatus']")))
					&& (isElementPresent(By.xpath("//*[@id='instr']"))) && (isElementPresent(By.xpath("//*[@id='start']"))) && (isElementPresent(By.xpath("//*[@id='starttime']")))
					&& (isElementPresent(By.xpath("//*[@id='deadline']"))) && (isElementPresent(By.xpath("//*[@id='deadlinetime']"))) && (isElementPresent(By.xpath("//*[@id='graceperiod']"))))
				break;
			waitAWhile(200);
		}
	}
	
	//by kalpit
	// Helper method to check that we're at the team forming page
	// Checks for the various fields expected.
	public static void verifyTeamFormingPage() {
		for (int x = 0;; x++) {
			if (x >= 40)
				fail("timeout");

			if ((isElementPresent(By.id("courseid"))) && (isElementPresent(By.xpath("//*[@id='profile_template']")))
					&& (isElementPresent(By.xpath("//*[@id='instr']"))) && (isElementPresent(By.xpath("//*[@id='start']"))) && (isElementPresent(By.xpath("//*[@id='starttime']")))
					&& (isElementPresent(By.xpath("//*[@id='deadline']"))) && (isElementPresent(By.xpath("//*[@id='deadlinetime']"))) && (isElementPresent(By.xpath("//*[@id='graceperiod']"))))
				break;
			waitAWhile(200);
		}
	}
	//end by kalpit

	/**
	 * Checks that the course has been added Checking for the course details appearing in the table 
	 * Page: Coordinator home 
	 * TODO: change to any number of previous courses
	 */
	public static void verifyAddedCourse(String courseId, String courseName) {
		// Check for courseId
		assertEquals(courseId, getElementText(By.id("courseID0")));

		// Check for course name
		assertEquals(courseName, getElementText(By.id("courseName0")));

		// Check for default number of teams - 0
		assertEquals("0", getElementText(By.cssSelector("#dataform td.t_course_teams")));
	}
	
	public static boolean isCoursePresent(String courseId, String courseName) {
		int totalCourses = countTotalCourses();
		boolean isPresent = false;
		for(int i = 0; i < totalCourses; i++) {
			if(getElementText(By.id("courseID"+ i)).equalsIgnoreCase(courseId) && getElementText(By.id("courseName" + i)).equals(courseName)) {
				isPresent = true;
				continue;
			}
		}

		return isPresent;
	}

	// Checks that we're at the student enrollment page
	// Checking for the form fields and the buttons
	public static void verifyEnrollPage() {
		for (int x = 0;; x++) {
			if (x >= 40)
				fail("timeout");
			if (isElementPresent(By.id("information")) && isElementPresent(By.id("button_enrol")))
				break;
			waitAWhile(200);
		}
	}

	// Helper method to check that we've enrolled students successfully.
	// Checks that the number of students added/edited equals the number
	// expected.
	public static void verifyEnrollment(int added, int edited) {
		for (int x = 0;; x++) {
			if (x >= 40)
				fail("timeout");
			if ((isElementPresent(By.xpath("//tr[@id='rowAddedStudents']/td"))) && (isElementPresent(By.xpath("//tr[@id='rowEditedStudents']/td"))))
				break;
			waitAWhile(200);
		}

		assertEquals(added, Integer.parseInt(getElementText(By.id("t_studentsAdded"))));
		assertEquals(edited, Integer.parseInt(getElementText(By.id("t_studentsEdited"))));
	}

	// Helper method to check that the evaluation was added successfully
	// Checks for the details of the evaluation that was added.
	public static void verifyEvaluationAdded(String courseId, String evalName, String status, String resp) {
		assertEquals(courseId, getElementText(By.className("t_eval_coursecode")));
		assertEquals(evalName, getElementText(By.className("t_eval_name")));
		assertEquals(status, getElementText(By.className("t_eval_status")));
		assertEquals(resp, getElementText(By.className("t_eval_response")));
	}
	
	//by kalpit 
	// Helper method to check that the evaluation was added successfully
	// Checks for the details of the evaluation that was added.
	public static void verifyTeamFormingSessionAdded(String courseId, String status) {
		assertEquals(courseId, getElementText(By.className("t_team_coursecode")));
		assertEquals(status, getElementText(By.className("t_team_status")));
	}
	//end by kalpit

	// -----------------------------Helper Functions----------------------------->> Others:

	private static void _login(String email, String password) {
		waitAWhile(1000);
		if (isLocalLoginPage()) {
			wdFillString(By.id("email"), email);
			selenium.click("css=input[value='Log In']");
			checkGoogleApplicationApproval();
			waitForPageLoad();
		} else if (isGoogleLoginPage()) {
			// Fill in login credentials
			wdFillString(By.id("Email"), email);
			driver.findElement(By.id("Passwd")).sendKeys("aa");
			wdFillString(By.id("Passwd"), password);
			// Click sign in button
			wdClick(By.id("signIn"));
			// Wait and check for the main Coordinator page to see
			// if login was successful
			checkGoogleApplicationApproval();
			waitForPageLoad();

		} else {
			fail("Not in the correct Login page");
			return;
		}
	}

	/*
	 * When authentication for the first few times, it might ask for the "grant permission" page. If that's the case we simply click "Grant"
	 */
	private static void checkGoogleApplicationApproval() {
		justWait();
		if (isElementPresent(By.id("approve_button"))) {
			wdClick(By.id("persist_checkbox"));
			wdClick(By.id("approve_button"));
		}
	}

	protected static void assertEqualsOr(String e1, String e2, String a) {
		if(e1.equalsIgnoreCase(a) || e2.equalsIgnoreCase(a)) {
			org.junit.Assert.assertTrue(true);
		}else {
			org.junit.Assert.assertEquals(e1, a);
			org.junit.Assert.assertEquals(e2, a);
		}
		
	}
	/**
	 * Helper function to clean up email account
	 * 
	 * @throws Exception
	 */
	protected static void cleanupGmailInbox() throws Exception {
		for (int i = 0; i < sc.students.size(); i++) {
			SharedLib.markAllEmailsSeen(sc.students.get(i).email, Config.inst().TEAMMATES_APP_PASSWD);
			System.out.println("clean up gmail Inbox for " + sc.students.get(i).name);
		}
	}

	/**
	 * Shortcut for System.out.println
	 */
	protected static void cout(String message) {
		System.out.println(message);
	}

	public static String getStudentsString(List<Student> list) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			Student s = list.get(i);
			sb.append(String.format("%s|%s|%s|", s.teamName, s.name, s.email));
			if (i != list.size() - 1) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}
}
