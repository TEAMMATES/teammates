package teammates.testing.lib;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

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
import teammates.testing.object.Evaluation;
import teammates.testing.object.Student;
import teammates.testing.object.TeamFormingSession;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * A browser instance represents a real browser instance + context to the app
 * 
 * Used to be BaseTest
 * 
 * @author Huy
 * @Xialin
 * @Shakthi
 */
public class BrowserInstance {
	protected DefaultSelenium selenium = null;
	private WebDriver driver = null;
	protected ChromeDriverService chromeService = null;
	private boolean inUse = false;

	// ----------------------UI Element:
	/**
	 * homepage:
	 */
	public final By COORD_LOGIN_BUTTON = By.name("COORDINATOR_LOGIN");
	public final By STUDENT_LOGIN_BUTTON = By.name("STUDENT_LOGIN");

	/**
	 * tabs:
	 */
	public By homeTab = By.className("t_home");
	public By teamFormingTab = By.className("t_teamForming");
	public By coursesTab = By.className("t_courses");
	public By evaluationsTab = By.className("t_evaluations");
	public By helpTab = By.className("t_help");
	public By logoutTab = By.className("t_logout");
	/*
	 * table elements:
		 */
		public By pageTitle = By.xpath("//div[@id='headerOperation']//h1");
		
		public final String HEADER_FORM_TABLE_CELL = "//table[@class='headerform']//tbody//tr//td";
		public final String DETAIL_FORM_TABLE_CELL = "//table[@class='detailform']//tbody//tr//td";
		public final String DATAFORM_TABLE_ROW = "//table[@id='dataform']//tr";
		public final String DATAFORM_TABLE_CELL = DATAFORM_TABLE_ROW + "[%d]//td[%d]";
		
	// -------------------------------- COORDINATOR -------------------------------- //
	/**
	 * home page:
	 */
	public By coordAddNewCourseLink = By.id("addNewCourse");
	
	// course box:
	public By getCoordEnrolLink(int courseNo) {
		return By.className("t_course_enrol" + courseNo);
	}
	
	public By getCoordViewLink(int courseNo) {
		return By.className("t_course_view" + courseNo);
	}
	
	public By getCoordAddEvaluationLink(int courseNo) {
		return By.className("t_course_add_eval" + courseNo);
	}
	
	public By getCoordDeleteLink(int courseNo) {
		return By.className("t_course_delete" + courseNo);
	}
	
	// evaluation table:
	public By getCoordViewResultsLink(int linkNo) {
		return By.id("viewEvaluation" + linkNo);
	}
	
	public By getCoordEditEvaluationLink(int linkNo) {
		return By.id("editEvaluation" + linkNo);
	}
	
	public By getCoordDeleteEvaluationLink(int linkNo) {
		return By.id("deleteEvaluation" + linkNo);
	}
	
	public By getCoordRemindEvaluationLink(int linkNo) {
		return By.id("remindEvaluation" + linkNo);
	}
	
	public By getCoordPublishEvaluationLink(int linkNo) {
		return By.id("publishEvaluation" + linkNo);
	}
	/**
	 * course:
	 */
	// add course:
	//public By addCoursePageTitle = By.xpath("//div[@id='headerOperation']//h1");
	public By coordInputCourseID = By.id("courseid");
	public By coordInputCourseName = By.id("coursename");
	public By coordAddCourseButton = By.id("btnAddCourse");
	public By coordCourseIDSorting = By.id("button_sortcourseid");
	public By coordCourseNameSorting = By.id("button_sortcoursename");
	// courses table:
	public By getCourseID(int row) {
		return By.id("courseID" + row);
	}
	
	public By getCourseName(int row) {
		return By.id("courseName" + row);
	}
	
	public By getCoordCourseEnrol(int row) {
		return By.xpath(String.format(DATAFORM_TABLE_CELL + "//a[@class='t_course_enrol']", row + 2, 6));
	}
	
	public By getCoordCourseView(int row) {
		return By.xpath(String.format(DATAFORM_TABLE_CELL + "//a[@class='t_course_view']", row + 2, 6));
	}
	
	public By getCoordCourseDelete(int row) {
		return By.xpath(String.format(DATAFORM_TABLE_CELL + "//a[@class='t_course_delete']", row + 2, 6));
	}
	
	// enrol:
	public By coordEnrolInfo = By.id("information");
	public By coordEnrolButton = By.id("button_enrol");
	public By coordEnrolBackButton = By.className("t_back");
	// enrollment results:
	public By coordStudentsAdded = By.id("t_studentsAdded");
	public By coordStudentsEdited = By.id("t_studentsEdited");
	public By coordStudentsAddedRow = By.xpath("//tr[@id='rowAddedStudents']//td");
	public By coordStudentsEditedRow = By.xpath("//tr[@id='rowEditedStudents']//td");
	// course details:
	public By courseDetailCourseID = By.xpath("//table[@class='headerform']//tbody//tr[1]//td[2]");
	public By courseDetailCourseName = By.xpath("//table[@class='headerform']//tbody//tr[2]//td[2]");
	public By courseDetailTeams = By.xpath("//table[@class='headerform']//tbody//tr[3]//td[2]");
	public By courseDetailTotalStudents = By.xpath("//table[@class='headerform']//tbody//tr[4]//td[2]");

	public By courseDetailStudentNameSorting = By.id("button_sortstudentname");
	public By courseDetailTeamSorting = By.id("button_sortstudentteam");
	public By courseDetailJoinStatusSorting = By.id("button_sortstudentstatus");

	public By remindStudentsButton = By.id("button_remind");
	public By deleteStudentsButton = By.className("t_delete_students");
	public By courseViewBackButton = By.className("t_back");

	// student details:
	public By studentDetailName = By.xpath("//table[@class='detailform']//tbody//tr[1]//td[2]");
	public By studentDetailTeam = By.xpath("//table[@class='detailform']//tbody//tr[2]//td[2]");
	public By studentDetailEmail = By.xpath("//table[@class='detailform']//tbody//tr[3]//td[2]");
	public By studentDetailGoogle = By.xpath("//table[@class='detailform']//tbody//tr[4]//td[2]");
	public By studentDetailComment = By.xpath("//table[@class='detailform']//tbody//tr[6]//td[2]");
	public By studentDetailKey = By.id("t_courseKey");
	public By studentDetailBackButton = By.className("t_back");

	// edit student:
	public By studentEditName = By.id("editname");
	public By studentEditTeam = By.id("editteamname");
	public By studentEditEmail = By.id("editemail");
	public By studentEditID = By.id("editgoogleid");
	public By studentEditComments = By.id("editcomments");

	public By studentEditSaveButton = By.id("button_editstudent");
	/**
     * team forming:
     */
    // team forming default:
    public final String TEAMFORMINGSESSION_STATUS_AWAITING = "AWAITING";
    public By inputTeamName = By.id("teamName");
    public By inputTeamProfile = By.id("teamProfile");        
    public By inputNewTeamName = By.id("newteamName");
    public By inputStudentProfileDetail = By.id("studentprofiledetail");
    
	/**
	 * evaluation:
	 */
	// evaluation default:
	public final String EVAL_STATUS_AWAITING = "AWAITING";
	public final String EVAL_STATUS_PUBLISHED = "PUBLISHED";
	public final String EVAL_STATUS_CLOSED = "CLOSED";

	public By inputEvaluationName = By.id("evaluationname");
	public By inputInstruction = By.id("instr");
	public By inputClosingTime = By.id("deadlinetime");
	public By inputClosingDate = By.xpath("//*[@id='deadline']");
	public By inputGracePeriod = By.id("graceperiod");
	public By inputProfileTemplate = By.id("profile_template");
	public By createTeamFormingSessionButton = By.id("t_btnCreateTeamFormingSession");
	// edit team forming session:
	public By editTeamFormingSessionButton = By.id("button_editteamformingsession");
	       
	//edit team profile:
    public By coordEditTeamProfile0 = By.id("viewTeamProfile0");
    public By saveTeamProfile = By.id("button_saveTeamProfile");
    public By saveChangeStudentTeam = By.id("button_saveTeamChange");
    public By saveStudentProfile = By.id("button_savestudentprofile");
    
    //edit student team:
    public By coordChangeStudentTeam11 = By.id("changeStudentTeam-1/1");
    public By coordAllocateStudentTeam1 = By.id("allocateStudentTeam1");
	public By addEvaluationButton = By.id("t_btnAddEvaluation");

	public By evaluationCourseIDSorting = By.id("button_sortcourseid");
	public By evlauationNameSorting = By.id("button_sortname");
	
	// edit evaluation:
	public By editEvaluationButton = By.id("button_editevaluation");
	public By editEvaluationBackButton = By.className("t_back");

	// result:
	public By resultSummaryRadio = By.id("radio_summary");
	public By resultDetailRadio = By.id("radio_detail");
	public By resultReviewerRadio = By.id("radio_reviewer");
	public By resultRevieweeRadio = By.id("radio_reviewee");

	public By resultPublishButton = By.id("button_publish");
	public By resultBackButton = By.id("button_back");

	// summary result:
	public By resultTeamSorting = By.id("button_sortteamname");
	public By resultStudentSorting = By.id("button_sortname");
	public By resultSubmittedSorting = By.id("button_sortsubmitted");
	public By resultClaimedSorting = By.id("button_sortaverage");
	public By resultDifferenceSorting = By.id("button_sortdiff");
	public By resultEditButton = By.id("button_editevaluationresultsbyreviewee");
	public By resultEditCancelButton = By.id("button_back");

	// individual result:
	public By resultNextButton = By.id("button_next");
	public By resultPreviousButton = By.id("button_previous");
	public By resultIndividualEditButton = By.id("button_edit");

	public By pointReviewerIndividualClaimed = By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='result_table']//th[2]"));;
	public By pointReviewerIndividualPerceived = By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='result_table']//th[3]"));
	public By pointRevieweeIndividualClaimed = By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='result_table']//th[2]"));
	public By pointRevieweeIndividualPerceived = By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='result_table']//th[3]"));

	// detail result:
	public By resultTopButton = By.id("button_top");

	// edit result:
	public By coordEvaluationSubmitButton = By.id("button_editevaluationresultsbyreviewee");
	// ---------------------------------- STUDENT ---------------------------------- //
	// course details:
	public By studentCourseDetailCourseID = By.xpath(String.format(DETAIL_FORM_TABLE_CELL, 1, 2));
	public By studentCourseDetailTeamName = By.xpath(String.format(DETAIL_FORM_TABLE_CELL, 2, 2));
	public By studentCourseDetailCourseName = By.xpath(String.format(DETAIL_FORM_TABLE_CELL, 3, 2));
	public By studentCourseDetailStudentName = By.xpath(String.format(DETAIL_FORM_TABLE_CELL, 4, 2));
	public By studentCourseDetailCoordinatorName = By.xpath(String.format(DETAIL_FORM_TABLE_CELL, 5, 2));
	public By studentCourseDetailStudentEmail = By.xpath(String.format(DETAIL_FORM_TABLE_CELL, 6, 2));
	public By studentCourseDetailStudentTeammates = By.xpath(String.format(DETAIL_FORM_TABLE_CELL, 7, 2));
	/**
	 * home page:
	 */
	public By studentJoinNewCourseLink = By.id("joinNewCourse");
	
	// course box:
	public By getStudentViewLink(int courseNo) {
		return By.className("t_course_view" + courseNo);
	}
	
	// evaluation table:
	public By getStudentDoEvaluationLink(int linkNo) {
		return By.id("doEvaluation" + linkNo);
	}
	
	public By getStudentViewResultsLink(int linkNo) {
		return By.id("viewEvaluation" + linkNo);
	}
	
	public By getStudentEditEvaluationSubmissionLink(int linkNo) {
		return By.id("editEvaluation" + linkNo);
	}	
	/**
	 * student:
	 */
	// student course:
	public By studentInputRegKey = By.id("regkey");
	public By studentJoinCourseButton = By.id("btnJoinCourse");
	
	// student evaluation:
	public By studentSubmitEvaluationButton = By.name("submitEvaluation");
	public By studentEvaluationBackButton = By.className("t_back");
	public By studentEvaluationCancelButton = By.className("t_back");
	/**
	 * evaluations page:
	 */
	public final String PENDING_EVALUATIONS_HEADER = "Pending Evaluations:";
	public final String PAST_EVALUATIONS_HEADER = "Past Evaluations:";
	// do evaluation:
	// edit evaluation submission:
	public By studentEvaluationCourseID = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 1, 2));
	public By studentEvaluationEvaluationName = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 2, 2));
	public By studentEvaluationOpeningTime = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 3, 2));
	public By studentEvaluationClosingTime = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 4, 2));
	public By studentEvaluationInstructions = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 5, 2));
	// evaluation results table:
	public final String STUDENT_EVALUATION_RESULTS_TABLE_ROW = "//div[@id='studentEvaluationResults']//table[@class='result_studentform']//tbody//tr";
	public final String STUDENT_EVALUATION_RESULTS_TABLE_CELL = STUDENT_EVALUATION_RESULTS_TABLE_ROW + "[%d]//td[%d]";
	
	public By studentEvaluationResultStudentName = By.xpath(String.format(STUDENT_EVALUATION_RESULTS_TABLE_CELL, 1, 2));
	public By studentEvaluationResultCourseID = By.xpath(String.format(STUDENT_EVALUATION_RESULTS_TABLE_CELL, 1, 4));
	public By studentEvaluationResultTeamName = By.xpath(String.format(STUDENT_EVALUATION_RESULTS_TABLE_CELL, 2, 2));
	public By studentEvaluationResultEvaluationName = By.xpath(String.format(STUDENT_EVALUATION_RESULTS_TABLE_CELL, 2, 4));
	public By studentEvaluationResultClaimedPoints = By.xpath(String.format(STUDENT_EVALUATION_RESULTS_TABLE_CELL, 3, 2));
	public By studentEvaluationResultOpeningTime = By.xpath(String.format(STUDENT_EVALUATION_RESULTS_TABLE_CELL, 3, 4));
	public By studentEvaluationResultPerceivedPoints = By.xpath(String.format(STUDENT_EVALUATION_RESULTS_TABLE_CELL, 4, 2));
	public By studentEvaluationResultClosingTime = By.xpath(String.format(STUDENT_EVALUATION_RESULTS_TABLE_CELL, 4, 4));
	/**
	 * messages:
	 */
	public By courseMessage = By.xpath("//div[@id='statusMessage']/font[1]");
	public By courseErrorMessage = By.xpath("//div[@id='statusMessage']/font[2]");
	public By statusMessage = By.id("statusMessage");
	public By editEvaluationResultsStatusMessage = By.id("coordinatorEditEvaluationResultsStatusMessage");

	public By footer = By.id("contentFooter");

	/**
	 * message contents:
	 */
	public final String MESSAGE_COURSE_EXISTS = "The course already exists.";
	public final String MESSAGE_COURSE_ADDED = "The course has been added. Click the 'Enrol' link in the table below to add students to the course.";
	public final String MESSAGE_COURSE_DELETED = "The course has been deleted.";
	public final String MESSAGE_COURSE_DELETED_STUDENT = "The student has been removed from the course.";
	public final String MESSAGE_COURSE_DELETED_ALLSTUDENTS = "All students have been removed from the course. Click here to enrol students.";

	public final String ERROR_COURSE_MISSING_FIELD = "Course ID and Course Name are compulsory fields.";
	public final String ERROR_COURSE_LONG_COURSE_NAME = "Course name should not exceed 38 characters.";
	public final String ERROR_COURSE_INVALID_ID = "Please use only alphabets, numbers, dots, hyphens, underscores and dollars in course ID.";

	public final String MESSAGE_ENROL_REMIND_TO_JOIN = "Emails have been sent to unregistered students.";
	public final String ERROR_MESSAGE_ENROL_INVALID_EMAIL = "E-mail address should contain less than 40 characters and be of a valid syntax.";
	//team forming session
    public final String MESSAGE_TEAMFORMINGSESSION_ADDED = "The team forming session has been added.";
    public final String MESSAGE_TEAMFORMINGSESSION_ADDED_WITH_EMPTY_CLASS = "The course does not have any students.";
    public final String MESSAGE_TEAMFORMINGSESSION_DELETED = "The team forming session has been deleted.";
    public final String MESSAGE_TEAMFORMINGSESSION_REMINDED = "Reminder e-mails have been sent out to those students.";
    public final String ERROR_MESSAGE_TEAMFORMINGSESSION_EXISTS = "The team forming session exists already.";
    public final String ERROR_INVALID_INPUT_TEAMFORMINGSESSION = "The team forming session schedule (start/deadline) is not valid.";
    public final String MESSAGE_TEAMFORMINGSESSION_EDITED = "The team forming session has been edited.";
    public final String ERROR_MESSAGE_TEAMPROFILE_EXISTS = "Same team profile exists already.";
    public final String MESSAGE_TEAMPROFILE_SAVED = "The team profile has been saved.";
    public final String MESSAGE_TEAMCHANGE_SAVED = "Student team has been changed.";
    public final String MESSAGE_LOG_REMINDSTUDENTS = "All your actions will be logged and can be viewed by the course coordinator.";
    public final String MESSAGE_STUDENTPROFILE_SAVED = "Your profile has been saved.";
    public final String MESSAGE_STUDENT_JOINEDTEAM = "You have joined a team.";
    public final String MESSAGE_STUDENT_LEFTTEAM = "You have left the team.";
    public final String MESSAGE_STUDENT_ADDTOTEAM = "Student has been added to your team.";
    public final String MESSAGE_STUDENT_NEWTEAMCREATED = "A new team has been created with the student.";
	// evaluations
	public final String MESSAGE_EVALUATION_ADDED = "The evaluation has been added.";
	public final String MESSAGE_EVALUATION_ADDED_WITH_EMPTY_TEAMS = "The evaluation has been added. Some students are without teams.";
	public final String MESSAGE_EVALUATION_DELETED = "The evaluation has been deleted.";
	public final String MESSAGE_EVALUATION_EDITED = "The evaluation has been edited.";
	public final String MESSAGE_EVALUATION_PUBLISHED = "The evaluation has been published.";
	public final String MESSAGE_EVALUATION_UNPUBLISHED = "The evaluation has been unpublished.";

	public final String ERROR_LONG_EVALUATION_NAME = "Evaluation name should not exceed 38 characters.";
	public final String ERROR_INVALID_EVALUATION_NAME = "Please use only alphabets, numbers and whitespace in evaluation name.";

	public final String MESSAGE_EVALUATION_RESULTS_EDITED = "The particular evaluation results have been edited.";

	public final String ERROR_MESSAGE_EVALUATION_EXISTS = "The evaluation exists already.";

	// student site
	public final String ERROR_STUDENT_JOIN_COURSE = "Registration key is invalid.";
	public final String MESSAGE_STUDENT_JOIN_COURSE = "You have successfully joined the course.";

	public final String FOOTER = "Best Viewed In Firefox, Chrome, Safari and Internet Explorer 8+. For Enquires:";

	// -----------------------------UI Actions ----------------------------->>
	// Homepage:
	/**
	 * Coordinator Login
	 * 
	 * @page Home page
	 */
	public void coordinatorLogin(String username, String password) {

		System.out.println("Logging in coordinator " + username + ".");
		// Click the Coordinator button on the main page
		wdClick(COORD_LOGIN_BUTTON);
		waitForPageLoad();
		/*
		 * IE Fix: for some reason in IE new profile is not created, thus user is already logged in. This will log user out.
		 */
		if (isElementPresent(logoutTab)) {
			driver.findElement(logoutTab).click();
			waitForPageLoad();
			// Check that we're at the main page after logging out
			verifyMainPage();

			wdClick(COORD_LOGIN_BUTTON);
			waitForPageLoad();
		}
		_login(username, password);
		waitAWhile(3000);
		verifyCoordHomePage();
		waitAWhile(1000);
	}

	/**
	 * Student Login
	 * 
	 * @page Homepage
	 */
	public void studentLogin(String username, String password) {

		cout("Logging in student " + username + ".");
		// Click the Student button on the main page
		wdClick(STUDENT_LOGIN_BUTTON);
		waitForPageLoad();
		/*
		 * IE Fix: for some reason in IE new profile is not created, thus user is already logged in. This will log user out.
		 */
		if (isElementPresent(logoutTab)) {
			driver.findElement(logoutTab).click();
			waitForPageLoad();
			// Check that we're at the main page after logging out
			verifyMainPage();
			wdClick(STUDENT_LOGIN_BUTTON);
			waitForPageLoad();
		}
		_login(username, password);
		waitAWhile(4000);
		verifyStudentHomePage();
	}

	/**
	 * Coordinator & Student Logout
	 */
	public void logout() {
		cout("Signing out.");
		waitAndClick(logoutTab);
		// Check that we're at the main page after logging out
		if (Config.inst().isLocalHost()) {
			selenium.open(Config.inst().TEAMMATES_URL);

		}
		verifyMainPage();
	}
	/**
	 * Click Home Tab
	 */
	public void clickHomeTab() {
		waitAndClick(homeTab);
	}
	
	/**
	 * Click Courses Tab
	 */
	public void clickCoursesTab() {
		waitAndClick(coursesTab);
	}
	
	/**
	 * Click Evaluations Tab
	 */
	public void clickEvaluationsTab() {
		waitAndClick(evaluationsTab);
	}	

	// -----------------------------UI Actions ----------------------------->>
	/** Student:
	 * 
	 * @param row
	 * @return
	 */

	// Join Course
	public String studentGetCourseID(int row) {
		row++;
		return selenium.getTable("id=dataform." + row + ".0");
	}

	public String studentGetCourseName(int row) {
		row++;
		return selenium.getTable("id=dataform." + row + ".1");
	}

	public String studentGetCourseName(String courseId) {
		int row = studentFindCourseRow(courseId);
		if (row > -1) {
			return studentGetCourseName(row);
		} else {
			fail("Student's course not found.");
			return null;
		}
	}

	public String studentGetCourseTeamName(int row) {
		row++;
		return selenium.getTable("id=dataform." + row + ".2");
	}

	public String studentGetCourseTeamName(String courseId) {
		int row = studentFindCourseRow(courseId);
		if (row > -1) {
			return studentGetCourseTeamName(row);
		} else {
			fail("Student's course not found.");
			return null;
		}
	}

	public void studentClickViewCourse(int row) {
		By link = By.xpath(String.format("//div[@id='studentCourseTable']//table[@id='dataform']//tr[%d]//td[%d]//a[1]", row + 2, 4));
		waitAndClick(link);
	}

	public void studentClickViewCourse(String courseId) {
		int row = studentFindCourseRow(courseId);
		if (row > -1) {
			studentClickViewCourse(row);
		} else {
			fail("Student's course not found. Can't click view course.");
		}
	}

	public int studentFindCourseRow(String courseId) {
		for (int i = 0; i < this.countTotalCourses(); i++) {
			if (studentGetCourseID(i).equals(courseId)) {
				return i;
			}
		}
		return -1;
	}

	public int studentCountTotalCourses() {
		WebElement dataform = driver.findElement(By.id("dataform"));
		return dataform.findElements(By.tagName("tr")).size();
	}

	// Pending Evaluation
	public By studentGetPendingEvaluationName(String courseId, String evalName) throws NullPointerException {
		int row = this.studentFindPendingEvaluationRow(courseId, evalName);
		if (row > -1) {
			return By.xpath(String.format("//div[@id='studentPendingEvaluations']//table[@id='dataform']//tr[%d]//td[%d]", row + 1, 1));
		} else {
			fail("Student's pending evaluation not found.");
			return null;
		}
	}

	public void studentClickDoEvaluation(int row) {
		waitAndClick(By.id("doEvaluation" + row));
	}

	public void studentClickDoEvaluation(String courseId, String evalName) {
		int row = studentFindPendingEvaluationRow(courseId, evalName);

		if (row > -1) {
			studentClickDoEvaluation(row);
		} else {
			fail("Student's pending evaluation not found. Cannot do evaluation.");
		}
	}

	public int studentFindPendingEvaluationRow(String courseId, String evalName) {

		for (int i = 0; i < studentCountTotalPendingEvaluations(); i++) {
			By course = By.xpath(String.format("//div[@id='studentPendingEvaluations']//table[@id='dataform']//tbody//tr[%d]//td[%d]", i + 1, 1));
			// System.out.println("course id = " + this.getElementText(course));
			By evaluation = By.xpath(String.format("//div[@id='studentPendingEvaluations']//table[@id='dataform']//tbody//tr[%d]//td[%d]", i + 1, 2));
			// System.out.println("eval id = " + this.getElementText(evaluation));
			if (this.getElementText(course).equals(courseId) && this.getElementText(evaluation).equals(evalName)) {
				return i;
			}
		}
		return -1;
	}

	public int studentCountTotalPendingEvaluations() {

		if (getElementText(By.xpath(String.format("//div[@id='studentPendingEvaluations']//table[@id='dataform']//tbody//tr[1]//td[1]"))).isEmpty()) {
			return 0;
		} else {
			return selenium.getXpathCount("//div[@id='studentPendingEvaluations']//table[@id='dataform']//tbody//tr").intValue();

		}
	}

	// Past Evaluations:
	public String studentGetEvaluationCourseID(int row) {
		row++;
		return selenium.getTable("id=dataform." + row + ".1");
	}

	public String studentGetEvaluationName(int row) {
		row++;
		return selenium.getTable("id=dataform." + row + ".2");
	}

	public String studentGetEvaluationStatus(int row) {
		row++;
		return selenium.getTable("id=dataform." + row + ".3");
	}

	public String studentGetEvaluationStatus(String courseId, String evalName) {
		int row = this.studentFindEvaluationRow(courseId, evalName);
		if (row > -1) {
			return studentGetEvaluationStatus(row);
		} else {
			fail("Student's evaluation not found.");
			return null;
		}
	}

	public By getStudentEditEvaluation(int row) {
		return By.id("editEvaluation" + row);
	}
	
	public void studentClickEditEvaluation(int row) {
		waitAndClick(getStudentEditEvaluation(row));
	}

	public void studentClickEditEvaluation(String courseId, String evalName) {
		int row = this.studentFindEvaluationRow(courseId, evalName);
		if (row > -1) {
			studentClickEditEvaluation(row);
		} else {
			fail("Student's evaluation not found. Cannot click edit evaluation.");
		}
	}

	public By getStudentEvaluationViewResults(int row) {
		return By.xpath(String.format("//div[@id='studentPastEvaluations']//table[@id='dataform']//tr[%d]//td[%d]//a['View Results']", row + 2, 5));
	}
	
	public void studentClickEvaluationViewResults(int row) {
		waitAndClick(getStudentEvaluationViewResults(row));
	}

	public void studentClickEvaluationViewResults(String courseId, String evalName) {
		int row = this.studentFindEvaluationRow(courseId, evalName);
		if (row > -1) {
			studentClickEvaluationViewResults(row);

		} else {
			fail("Student's evaluation not found.");
		}
	}

	public int studentFindEvaluationRow(String courseId, String evalName) {

		for (int i = 0; i < studentCountTotalEvaluations(); i++) {
			By course = By.xpath(String.format("//div[@id='studentPastEvaluations']//table[@id='dataform']//tbody//tr[%d]//td[%d]", i + 2, 1));
			System.out.println(this.getElementText(course));

			By evaluation = By.xpath(String.format("//div[@id='studentPastEvaluations']//table[@id='dataform']//tbody//tr[%d]//td[%d]", i + 2, 2));
			System.out.println(this.getElementText(evaluation));

			if (this.getElementText(course).equals(courseId) && this.getElementText(evaluation).equals(evalName)) {
				return i;
			}
		}
		return -1;
	}

	public int studentCountTotalEvaluations() {
		if (getElementText(By.xpath(String.format("//div[@id='studentPastEvaluations']//table[@id='dataform']//tbody//tr[2]//td[1]"))).isEmpty()) {
			return 0;
		} else {
			return selenium.getXpathCount("//div[@id='studentPastEvaluations']//table[@id='dataform']/tbody/tr").intValue() - 1;
		}
	}

	// Student Evaluation Results Page:
	public String studentGetEvaluationResultClaimedPoints() {
		return getElementText(By.xpath(String.format("//div[@id='studentEvaluationResults']//table[@class='result_studentform']//tr[%d]//td[%d]", 3, 2)));
	}

	public String studentGetEvaluationResultPerceivedPoints() {
		return getElementText(By.xpath(String.format("//div[@id='studentEvaluationResults']//table[@class='result_studentform']//tr[%d]//td[%d]", 4, 2)));
	}

	public By studentGetFeedbackFromOthers(int row) {
		return By.id("com" + row);
	}

	public boolean studentGetFeedbackFromOthers(String fromStudent, String toStudent) {
		return selenium.isTextPresent(String.format("This is a public comment from %s to %s", fromStudent, toStudent));
	}

	// -----------------------------Coordinator UI Actions ----------------------------->>
	// Course:
	/**
	 * Snippet to go to Courses page
	 */
	public void gotoCourses() {
		waitAndClick(coursesTab);
		justWait();
		verifyCoordinatorPage();
	}

	public void clickCourseTab() {
		waitAndClick(coursesTab);
	}

	/**
	 * page: Add Course
	 * 
	 * @param row
	 */
	public void addCourse(String courseid, String coursename, int courseIndex) {
		wdFillString(coordInputCourseID, courseid);
		wdFillString(coordInputCourseName, coursename);
		// wdClick(addCourseButton);
		waitAndClickAndCheck(coordAddCourseButton, By.id("courseID" + courseIndex));
		justWait();
	}

	public void addCourse(String courseid, String coursename) {
		wdFillString(coordInputCourseID, courseid);
		wdFillString(coordInputCourseName, coursename);
		waitAndClick(coordAddCourseButton);
		justWait();
	}


	public By getCourseID(String courseID) {
		int row = findCourseRow(courseID);
		if (row > -1) {
			return getCourseID(row);
		} else {
			fail("Course not found.");
			return null;
		}
	}


	public By getCourseName(String courseID) {
		int row = findCourseRow(courseID);
		if (row > -1) {
			return getCourseName(row);
		} else {
			fail("Course " + courseID + " not found.");
			return null;
		}
	}

	public String getCourseTeams(int row) {
		row++;// row starts from 0
		return selenium.getTable("id=dataform." + row + ".2");
	}

	public String getCourseTeams(String courseID) {
		int row = findCourseRow(courseID);
		if (row > -1) {
			return getCourseTeams(row);
		} else {
			fail("Course " + courseID + " not found.");
			return null;
		}
	}

	public String getCourseTotalStudents(int row) {
		row++;
		return selenium.getTable("id=dataform." + row + ".3");
	}

	public String getCourseTotalStudents(String courseID) {
		int row = findCourseRow(courseID);
		if (row > -1) {
			return getCourseTotalStudents(row);
		} else {
			fail("Course " + courseID + " not found.");
			return null;
		}
	}

	public String getCourseUnregisteredStudents(int row) {
		row++;
		return selenium.getTable("id=dataform." + row + ".4");
	}

	public String getCourseUnregisteredStudents(String courseID) {
		int row = findCourseRow(courseID);
		if (row > -1) {
			return getCourseUnregisteredStudents(row);
		} else {
			fail("Course " + courseID + " not found.");
			return null;
		}
	}

	public void clickCourseEnrol(int row) {
		waitAndClick(By.xpath(String.format("//table[@id='dataform']//tr[%d]//td[6]//a[@class='t_course_enrol']", row + 2)));
	}

	public void clickCourseEnrol(String courseID) {
		int row = findCourseRow(courseID);
		if (row > -1) {
			clickCourseEnrol(row);
		} else {
			fail("Course ID cannot be found");
		}
	}

	private int findCourseRow(String courseID) {
		for (int i = 0; i < countTotalCourses(); i++) {
			if (getElementText(getCourseID(i)).equals(courseID)) {
				return i;
			}
		}
		return -1;
	}

	public void clickCourseView(int row) {
		waitAndClick(By.xpath(String.format("//table[@id='dataform']//tr[%d]//td[6]//a[@class='t_course_view']", row + 2)));
	}

	public void clickCourseView(String courseID) {
		int row = findCourseRow(courseID);
		if (row > -1) {
			clickCourseView(row);
		} else {
			fail("Course ID cannot be found.");
		}
	}

	public void clickAndConfirmCourseDelete(int row) {
		clickAndConfirm(By.xpath(String.format("//table[@id='dataform']//tr[%d]//td[6]//a[@class='t_course_delete']", row + 2)));
	}

	public void clickAndConfirmCourseDelete(String courseID) {
		int row = findCourseRow(courseID);
		if (row > -1) {
			clickAndConfirmCourseDelete(row);
		} else {
			fail("Course ID cannot be found.");
		}
	}

	public int countTotalCourses() {
		if (getElementText(By.xpath(String.format("//table[@id='dataform']//tr[2]//td[1]"))).isEmpty()) {
			return 0;
		} else {
			return selenium.getXpathCount("//table[@id='dataform']/tbody/tr").intValue() - 1;
		}
	}

	/**
	 * page: Enrol Student
	 * 
	 * @param row
	 */
	public void enrollStudents(List<Student> students, int row) {
		clickCourseEnrol(row);
		verifyEnrollPage();

		wdFillString(coordEnrolInfo, getStudentsString(students));
		wdClick(coordEnrolButton);
		justWait();
	}

	public void enrollStudents(List<Student> students, String courseID) {
		clickCourseEnrol(courseID);
		verifyEnrollPage();

		wdFillString(coordEnrolInfo, getStudentsString(students));
		wdClick(coordEnrolButton);
	}

	/**
	 * page: View course (Course Detail)
	 * 
	 * @param row
	 */
	public String getCourseDetailStudentName(int row) {
		row++;
		return selenium.getTable("id=dataform." + row + ".0");
	}

	public String getCourseDetailTeamName(int row) {
		row++;
		return selenium.getTable("id=dataform." + row + ".1");
	}

	public void clickCourseDetailView(int row) {
		waitAndClick(By.xpath(String.format("//div[@id='coordinatorStudentTable']//table[@id='dataform']//tr[%d]//a[1]", row + 2)));
	}

	public void clickCourseDetailView(String student) {
		int row = findStudentRow(student);
		if (row > -1) {
			clickCourseDetailView(row);
		} else {
			fail("Student not found in this course.");
		}
	}

	private int findStudentRow(String student) {
		int i = 0;
		while (i < countCourseDetailTotalStudents()) {
			if (getCourseDetailStudentName(i).equals(student)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public void clickCourseDetailEdit(int row) {
		waitAndClick(By.xpath(String.format("//table[@id='dataform']//tr[%d]//td[4]//a[@class='t_student_edit']", row + 2)));
	}

	public void clickCourseDetailEdit(String student) {
		int row = findStudentRow(student);
		if (row > -1) {
			clickCourseDetailEdit(row);
		} else {
			fail("Student not found in this course.");
		}
	}

	public void clickCourseDetailInvite(int row) {
		By link = By.xpath(String.format("//table[@id='dataform']//tr[%d]//td[%d]//a[3]", row + 2, 4));
		waitAndClick(link);
	}

	public void clickCourseDetailInvite(String student) {
		int row = findStudentRow(student);
		if (row > -1) {
			clickCourseDetailInvite(row);
		} else {
			fail("Student not found in this course.");
		}
	}

	public void clickAndConfirmCourseDetailDelete(int row) {
		waitAndClickAndConfirm(By.xpath(String.format("//table[@id='dataform']//tr[%d]//a[@class='t_student_delete']", row + 2)));
	}

	public void clickAndConfirmCourseDetailDelete(String student) {
		int row = findStudentRow(student);
		if (row > -1) {
			clickAndConfirmCourseDetailDelete(row);
		} else {
			fail("Student not found in this course.");
		}
	}

	public int countCourseDetailTotalStudents() {
		if (getElementText(By.xpath(String.format("//table[@id='dataform']//tr[2]//td[1]"))).isEmpty()) {
			return 0;
		} else {
			return selenium.getXpathCount("//table[@id='dataform']/tbody/tr").intValue() - 1;
		}
	}

	/**
	 * Delete all available courses.
	 */
	public void deleteAllCourses() throws Exception {
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
	public void deleteAllStudents() {
		cout("delete all students");
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
	// -----------------------------UI Actions ----------------------------->>
		// Team Forming:
		/**
		 * Snippet to go to Team Forming page
		 */

		public void gotoTeamForming() {
			wdClick(teamFormingTab);
			justWait();
			verifyTeamFormingPage();
		}

		public void clickTeamFormingTab() {
			wdClick(teamFormingTab);
		}
		
		// Helper method to check that we're at the team forming page
		// Checks for the various fields expected.
		public void verifyTeamFormingPage() {
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
		
		// Helper method to check that we're at the manage team forming page
		// Checks for the various fields expected.
		public void verifyManageTeamFormingPage(ArrayList<Student> students) {
			boolean studentsChecked = true;
			for (int x = 0;; x++) {
				if (x >= 40)
					fail("timeout");
				
				for(int i=0; i<students.size(); i++)
					if(isTextPresent(students.get(i).name)==false)
						studentsChecked = false;
					
				
				if((isTextPresent("TEAMS FORMED")) && (isTextPresent("STUDENTS YET TO JOIN A TEAM")) 
						&& (isElementPresent(By.id("viewTeamProfile0"))) && (isElementPresent(By.id("viewTeamProfile1")))
						&& (isElementPresent(By.id("allocateStudentTeam0"))) && (isElementPresent(By.id("allocateStudentTeam1")))
						&& studentsChecked == true)
					break;
				waitAWhile(200);
			}
		}
		
		// Helper method to check that we're at the team detail page
		// Checks for the various fields expected.
		public void verifyTeamDetailPage() {
			for (int x = 0;; x++) {
				if (x >= 40)
					fail("timeout");				
				
				if((isTextPresent("TEAM DETAIL")) && (isElementPresent(By.id("teamName"))) 
						&& (isElementPresent(By.id("teamProfile")))	&& (isElementPresent(By.id("button_back"))) 
						&& (isElementPresent(By.id("button_saveTeamProfile"))))
					break;
				waitAWhile(200);
			}
		}
		
		// Helper method to check that we're at the view teams page
		// Checks for the various fields expected.
		public void verifyViewTeamsPage(ArrayList<Student> students) {
			boolean studentsChecked = true;
			for (int x = 0;; x++) {
				if (x >= 40)
					fail("timeout");
				
				for(int i=0; i<students.size(); i++)
					if(isTextPresent(students.get(i).name)==false)
						studentsChecked = false;				
				
				if((isTextPresent("TEAMS FORMED")) && (isTextPresent("STUDENTS YET TO JOIN A TEAM")) 
						&& ((isElementPresent(By.id("buttonJoin0"))) || (isElementPresent(By.id("buttonJoin1"))))
						&& ((isElementPresent(By.id("buttonAdd0"))) || (isElementPresent(By.id("buttonAdd1"))))
						&& studentsChecked == true)
					break;
				waitAWhile(200);
			}
		}
		
		// Helper method to check that we're at the change student team page
		// Checks for the various fields expected.
		public void verifyChangeStudentTeamPage() {
			for (int x = 0;; x++) {
				if (x >= 40)
					fail("timeout");				
				
				if((isTextPresent("Add to existing team:")) && (isTextPresent("Add to a new team:"))
						&& (isElementPresent(By.id("teamchange_newteam"))) && (isElementPresent(By.id("teamName"))) 
						&& (isElementPresent(By.id("newteamName"))) && (isElementPresent(By.id("button_saveTeamChange"))) 
						&& (isElementPresent(By.id("button_back"))))
					break;
				waitAWhile(200);
			}
		}
		
		public void addTeamFormingSession(TeamFormingSession teamForming) {
			addTeamFormingSession(teamForming.courseID, teamForming.dateValue, teamForming.nextTimeValue, 
					teamForming.gracePeriod, teamForming.instructions, teamForming.profileTemplate);
		}
		
		public void addTeamFormingSession(String courseID, String dateValue, String nextTimeValue, Integer gracePeriod, String instructions, String profileTemplate) {
			clickTeamFormingTab();
			
			// Select the course
			waitAndClick(coordInputCourseID);
			selectDropdownByValue(coordInputCourseID, courseID);

			// Fill in instructions
			wdFillString(inputInstruction, instructions);
			// Fill in profile template
			wdFillString(inputProfileTemplate, profileTemplate);
			justWait();

			// Select deadline date
			waitAndClick(inputClosingDate);
			selenium.waitForPopUp("window_deadline", "30000");
			selenium.selectWindow("name=window_deadline");
			waitAndClick(By.xpath("//a[contains(@href, '" + dateValue + "')]"));
			for (String s : driver.getWindowHandles()) {
				selenium.selectWindow(s);
				break;
			}
			justWait();
			selectDropdownByValue(inputClosingTime, nextTimeValue);
			// Select grace period
			selectDropdownByValue(inputGracePeriod, Integer.toString(gracePeriod));
			justWait();

			// Submit the form
			waitAndClick(createTeamFormingSessionButton);
		}
		
		public int countTotalTeamFormingSessions() {

			if (getElementText(By.xpath(String.format("//table[@id='dataform']//tr[2]//td[1]"))).isEmpty()) {
				return 0;
			} else {
				return selenium.getXpathCount("//table[@id='dataform']/tbody/tr").intValue() - 1;
			}
		}
		
		public String getTeamFormingSessionCourseID(int row) {
			row++;
			return selenium.getTable("id=dataform." + row + ".0");
		}
		
		public String getTeamFormingSessionStatus(int row) {
			row++;
			return selenium.getTable("id=dataform." + row + ".2");
		}
		
		public By getStudentNameFromManageTeamFormingSession(int row, int col) {
			return By.xpath(String.format("//div[@class='result_team']//table[@id='dataform']//tbody//tr[%d]//td[%d]", row, col));		
		}
		
		// Helper method to check that the team forming session was added successfully
		// Checks for the details of the evaluation that was added.
		public void verifyTeamFormingSessionAdded(String courseId, String status) {

			for (int i = 0; i < this.countTotalTeamFormingSessions(); i++) {
				if (this.getTeamFormingSessionCourseID(i).equals(courseId))
					assertEquals(status, getElementText(By.className("t_team_status")));
				assertEquals(status, this.getTeamFormingSessionStatus(i));
			}
		}

		public void clickTeamFormingSessionViewTeams(String courseId) {
			int row = findTeamFormingSessionRow(courseId);
			if (row > -1) {
				clickTeamFormingSessionViewTeams(row);
			} else {
				fail("Team forming session view teams not found.");
			}
		}
		
		public void clickTeamFormingSessionViewTeams(int row) {
			String elementID = "viewTeams" + row;
			clickAndConfirm(By.id(elementID));
		}
		
		public void clickTeamFormingSessionViewLog(String courseId) {
			int row = findTeamFormingSessionRow(courseId);
			if (row > -1) {
				clickTeamFormingSessionViewLog(row);
			} else {
				fail("Team forming session view log not found.");
			}
		}
		
		public void clickTeamFormingSessionViewLog(int row) {
			String elementID = "viewLogTeamFormingSession" + row;
			clickAndConfirm(By.id(elementID));
		}
		
		public void clickTeamFormingSessionEdit(String courseId) {
			int row = findTeamFormingSessionRow(courseId);
			if (row > -1) {
				clickTeamFormingSessionEdit(row);
			} else {
				fail("Team forming session not found.");
			}
		}
		
		public void clickTeamFormingSessionRemind(String courseId) {
			int row = findTeamFormingSessionRow(courseId);
			if (row > -1) {
				String elementID = "remindTeamFormingSession" + row;
				clickAndConfirm(By.id(elementID));
			}
		}
		
		public void clickTeamFormingSessionEdit(int row) {
			String elementID = "manageTeamFormingSession" + row;
			clickAndConfirm(By.id(elementID));
		}
		
		public void clickTeamFormingSessionDelete(String courseId) {
			int row = findTeamFormingSessionRow(courseId);
			if(row>-1){
				String elementID = "deleteTeamFormingSession" + row;
				clickAndConfirm(By.id(elementID));
			}
		}
		
		public boolean isTeamFormingSessionPresent(String courseId) {
			int totalTeamFormingSession = countTotalTeamFormingSessions();
			boolean isPresent = false;
			for (int i = 0; i < totalTeamFormingSession; i++) {
				if (getElementText(By.xpath(String.format("//table[@id='dataform']//tr["+(i+2)+"]//td[1]"))).equals(courseId)) {
					isPresent = true;
					continue;
				}
			}
			return isPresent;
		}
		
		/**
		 * Team Forming Session primary key: courseId
		 * 
		 * */
		private int findTeamFormingSessionRow(String courseId) {
			int i = 0;
			while (i < this.countTotalTeamFormingSessions()) {
				if (this.getTeamFormingSessionCourseID(i).equals(courseId))
					return i;
				i++;
			}
			return -1;
		}
	// -----------------------------UI Actions ----------------------------->>
	// Evaluation:
	/**
	 * Snippet to go to Evaluations page
	 */
	public void gotoEvaluations() {
		wdClick(evaluationsTab);
		justWait();
		verifyEvaluationPage();
	}

	public void clickEvaluationTab() {
		wdClick(evaluationsTab);
	}

	/**
	 * page: evaluation
	 * 
	 * @param eval
	 */
	public void addEvaluation(Evaluation eval) {
		addEvaluation(eval.courseID, eval.name, eval.dateValue, eval.nextTimeValue, eval.p2pcomments, eval.instructions, eval.gracePeriod);
	}

	public void addEvaluation(Evaluation eval, int evalIndex) {
		clickEvaluationTab();
		// Select the course
		waitAndClick(coordInputCourseID);
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
		// wdClick(addEvaluationButton);
		waitAndClickAndCheck(addEvaluationButton, By.id("evaluation" + evalIndex));
	}

	public void addEvaluation(String courseID, String evalName, String dateValue, String nextTimeValue, String comments, String instructions, Integer gracePeriod) {
		clickEvaluationTab();

		// Select the course
		waitAndClick(coordInputCourseID);
		justWait();
		selectDropdownByValue(coordInputCourseID, courseID);

		// Fill in the evaluation name
		wdFillString(inputEvaluationName, evalName);
		justWait();

		// Select deadline date
		waitAndClick(inputClosingDate);
		selenium.waitForPopUp("window_deadline", "30000");
		selenium.selectWindow("name=window_deadline");
		waitAndClick(By.xpath("//a[contains(@href, '" + dateValue + "')]"));
		for (String s : driver.getWindowHandles()) {
			selenium.selectWindow(s);
			break;
		}
		justWait();
		selectDropdownByValue(inputClosingTime, nextTimeValue);

		// Allow P2P comment
		waitAndClick(By.xpath("//*[@id='commentsstatus'][@value='" + comments + "']"));
		justWait();

		// Fill in instructions
		wdFillString(inputInstruction, instructions);
		justWait();

		// Select grace period
		selectDropdownByValue(inputGracePeriod, Integer.toString(gracePeriod));
		justWait();

		// Submit the form
		waitAndClick(addEvaluationButton);
	}
	public String fillInEvalName(String name)
	{
		wdFillString(inputEvaluationName, name);
		justWait();
		return selenium.getValue("id=evaluationname");
	}
	
	public String fillInCourseName(String name)
	{
		wdFillString(coordInputCourseName, name);
		justWait();
		return selenium.getValue("id=coursename");
	}
	
	public String fillInCourseID(String id)
	{
		wdFillString(coordInputCourseID, id);
		justWait();
		return selenium.getValue("id=courseid");
	}

	public String getEvaluationCourseID(int row) {
		row++;
		return selenium.getTable("id=dataform." + row + ".0");
	}

	public String getEvaluationName(int row) {
		row++;
		return selenium.getTable("id=dataform." + row + ".1");
	}

	public String getEvaluationName(String courseId, String evalName) {
		int row = findEvaluationRow(courseId, evalName);
		if (row > -1) {
			return getEvaluationName(row);
		} else {
			fail("Evaluation not found.");
			return null;
		}
	}

	/**
	 * Evaluation primary key: courseId + evaluationName
	 * 
	 * */
	private int findEvaluationRow(String courseId, String evalName) {
		int i = 0;
		while (i < this.countTotalEvaluations()) {
			if (this.getEvaluationCourseID(i).equals(courseId) && this.getEvaluationName(i).equals(evalName)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public boolean isEvaluationPresent(String courseId, String evalName) {
		for (int i = 0; i < this.countTotalEvaluations(); i++) {
			if (this.getEvaluationCourseID(i).equals(courseId) && this.getEvaluationName(i).equals(evalName)) {
				return true;
			}
		}
		return false;
	}

	public String getEvaluationStatus(int row) {
		row++;
		return selenium.getTable("id=dataform." + row + ".2");
	}

	public String getEvaluationStatus(String courseId, String evalName) {
		int row = findEvaluationRow(courseId, evalName);
		if (row > -1) {
			return getEvaluationStatus(row);
		} else {
			fail("Evaluation not found.");
			return "getEvaluationStatus(String evalName) failed.";
		}
	}

	public String getEvaluationResponse(int row) {
		row++;
		return selenium.getTable("id=dataform." + row + ".3");
	}

	public String getEvaluationResponse(String courseId, String evalName) {
		int row = findEvaluationRow(courseId, evalName);
		if (row > -1) {
			return getEvaluationResponse(row);
		} else {
			fail("Evaluation not found.");
			return "getEvaluationResponse(String evalName) failed.";
		}
	}

	public By getEvaluationViewResults(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_view']", row + 2, 5));
	}
	
	public void clickEvaluationViewResults(int row) {
		waitAndClick(getEvaluationViewResults(row));
	}

	public void clickEvaluationViewResults(String courseId, String evalName) {
		int row = findEvaluationRow(courseId, evalName);

		if (row > -1) {
			clickEvaluationViewResults(row);
		} else {
			fail("Evaluation not found.");
		}
	}

	public By getEvaluationEditResults(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_edit']", row + 2, 5));
	}

	public void clickEvaluationEdit(int row) {
		waitAndClick(getEvaluationEditResults(row));
	}

	public void clickEvaluationEdit(String courseId, String evalName) {
		int row = findEvaluationRow(courseId, evalName);
		if (row > -1) {
			clickEvaluationEdit(row);
		} else {
			fail("Evaluation not found.");
		}
	}

	public By getEvaluationPublishResults(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_publish']", row + 2, 5));
	}

	public void clickEvaluationPublish(int row) {
		clickAndConfirm(getEvaluationPublishResults(row));
	}

	public void clickEvaluationPublish(String courseId, String evalName) {
		int row = findEvaluationRow(courseId, evalName);
		if (row > -1) {
			clickEvaluationPublish(row);
		} else {
			fail("Evaluation not found.");
		}
	}

	public By getEvaluationUnpublishResults(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_unpublish']", row + 2, 5));
	}

	public void clickEvaluationUnpublish(int row) {
		clickAndConfirm(getEvaluationUnpublishResults(row));
	}

	public void clickEvaluationUnpublish(String courseId, String evalName) {
		int row = findEvaluationRow(courseId, evalName);
		if (row > -1) {
			clickEvaluationUnpublish(row);
		} else {
			fail("Evaluation not found.");
		}
	}

	public By getEvaluationRemindResults(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_remind']", row + 2, 5));
	}

	public void clickAndConfirmEvaluationRemind(int row) {
		clickAndConfirm(getEvaluationRemindResults(row));
	}

	public void clickAndConfirmEvaluationRemind(String courseId, String evalName) {
		int row = findEvaluationRow(courseId, evalName);
		if (row > -1) {
			clickAndConfirmEvaluationRemind(row);
		} else {
			fail("Evaluation not found.");
		}
	}

	public By getEvaluationDeleteResults(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_delete']", row + 2, 5));
	}

	public void clickAndConfirmEvaluationDelete(int row) {
		clickAndConfirm(getEvaluationDeleteResults(row));
	}

	public void clickAndConfirmEvaluationDelete(String courseId, String evalName) {
		int row = findEvaluationRow(courseId, evalName);
		if (row > -1) {
			clickAndConfirmEvaluationDelete(row);
		} else {
			fail("Evaluation not found.");
		}
	}

	public void clickAndCancelEvaluationDelete(int row) {
		waitAndClickAndCancel(getEvaluationDeleteResults(row));
	}

	public void clickAndCancelEvaluationDelete(String courseId, String evalName) {
		int row = findEvaluationRow(courseId, evalName);
		if (row > -1) {
			clickAndCancelEvaluationDelete(row);
		} else {
			fail("Evaluation not found.");
		}
	}

	public int countTotalEvaluations() {

		if (getElementText(By.xpath(String.format("//table[@id='dataform']//tr[2]//td[1]"))).isEmpty()) {
			return 0;
		} else {
			return selenium.getXpathCount("//table[@id='dataform']/tbody/tr").intValue() - 1;
		}
	}

	/**
	 * @page view evaluation result (result)
	 * 
	 * @param row
	 */
	// reviewer summary
	public By getReviewerSummaryView(int row) {
		return By.id("viewEvaluationResults" + row);
	}
	
	public void clickReviewerSummaryView(int row) {
		waitAndClick(getReviewerSummaryView(row));
	}
	
	public By getReviewerSummaryEdit(int row) {
		return By.id("editEvaluationResults" + row);
	}

	public void clickReviewerSummaryEdit(int row) {
		waitAndClick(getReviewerSummaryEdit(row));
	}

	public int countReviewerSummaryStudents() {
		if (getElementText(By.xpath(String.format("//table[@id='dataform']//tr[2]//td[1]"))).isEmpty()) {
			return 0;
		} else {
			return selenium.getXpathCount("//table[@id='dataform']/tbody/tr").intValue() - 1;
		}
	}

	// reviewee summary:
	public String getRevieweeSummaryClaimed(int studentIndex) {
		studentIndex++;
		return selenium.getTable("id=dataform." + studentIndex + ".2");
	}

	public String getRevieweeSummaryDifference(int studentIndex) {
		studentIndex++;
		return selenium.getTable("id=dataform." + studentIndex + ".3");
	}

	public void clickRevieweeSummaryView(int row) {
		waitAndClick(By.id("viewEvaluationResults" + row));
	}

	// reviewer individual:
	public String getReviewerIndividualClaimedPoint() {
		return this.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]", 2)));
	}
	
	public String getReviewerIndividualPerceivedPoint() {
		return this.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]", 3)));
	}
	
	public By getReviewerIndividualToStudent(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]", row + 4, 1));
	}

	public By getReviewerIndividualToStudentPoint(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]", row + 4, 2));
	}

	// reviewee individual:
	public String getRevieweeIndividualClaimedPoint() {
		return this.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]", 2)));
	}
	public String getRevieweeIndividualPerceivedPoint() {
		return this.getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]", 3)));
	}
	
	public By getRevieweeIndividualFromStudent(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]", row + 4, 1));
	}

	public By getRevieweeIndividualFromStudentPoint(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]", row + 4, 2));
	}

	// reviewer detail:
	public By getReviewerDetailClaimedPoint(int team, int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]", team, row, 2));
	}

	public By getReviewerDetailPerceived(int team, int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]", team, row, 3));
	}

	public By getReviewerDetailToStudent(int team, int position, int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]", team, position, row + 4, 1));
	}

	public By getReviewerDetailToStudentPoint(int team, int position, int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]", team, position, row + 4, 2));
	}

	// reviewee detail:
	public By getRevieweeDetailClaimed(int teamIndex, int position) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[%d]//tr[%d]//td[%d]", teamIndex, position + 2, 2));
	}

	public By getRevieweeDetailPerceived(int teamIndex, int position) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[%d]//tr[%d]//td[%d]", teamIndex, position + 3, 2));
	}

	public By getRevieweeDetailFromStudent(int position, int studentIndex) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//tr[%d]//table[@id='dataform']//tr[%d]//td[%d]", position + 7, studentIndex + 2, 1));
	}

	public By getRevieweeDetailFromStudentPoint(int position, int studentIndex) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//tr[%d]//table[@id='dataform']//tr[%d]//td[%d]", position + 7, studentIndex + 2, 2));
	}

	/**
	 * @page edit evaluation result
	 */
	public By getSubmissionPoint(int row) {
		return By.id("points" + row);
	}

	public void setSubmissionPoint(int row, String points) {
		selectDropdownByValue(By.id("points" + row), points);
	}

	public By getSubmissionJustification(int row) {
		return By.name("justification" + row);
	}

	public void setSubmissionJustification(int row, String justification) {
		wdFillString(By.name("justification" + row), justification);
	}

	public By getSubmissionComments(int row) {
		return By.name("commentstostudent" + row);
	}

	public void setSubmissionComments(int row, String comments) {
		wdFillString(By.name("commentstostudent" + row), comments);
	}

	/**
	 * Delete all evaluations
	 * 
	 * Must be in Evaluations page
	 */
	public void deleteAllEvaluations() {

		while (driver.findElements(By.className("t_eval_delete")).size() > 1) {
			System.out.println("Deleting 1 evaluation...");
			clickAndConfirm(By.className("t_eval_delete"));
			waitForElementText(statusMessage, "The evaluation has been deleted.");
			gotoEvaluations(); // This is to fix for Datastore delay problem
		}
	}

	/**
	 * Coord publish results 
	 * Send email to students
	 * */
	public boolean checkResultEmailsSent(String gmail, String password, String courseCode, String evaluationName) throws MessagingException, IOException {

		// Publish RESULTS Format
		final String HEADER_EVALUATION_PUBLISH = "TEAMMATES: Evaluation Published: %s %s";
		final String TEAMMATES_APP_URL = "You can view the result here: " + Config.inst().TEAMMATES_LIVE_SITE;
		final String TEAMMATES_APP_SIGNATURE = "If you encounter any problems using the system, email TEAMMATES support";

		Session sessioned = Session.getDefaultInstance(System.getProperties(), null);
		Store store = sessioned.getStore("imaps");
		store.connect("imap.gmail.com", gmail, password);

		// Retrieve the "Inbox"
		Folder inbox = store.getFolder("inbox");
		// Reading the Email Index in Read / Write Mode
		inbox.open(Folder.READ_WRITE);
		FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
		Message messages[] = inbox.search(ft);
		System.out.println(messages.length + " unread message");

		// Loop over all of the messages
		for (int i = messages.length - 1; i >= 0; i--) {
			Message message = messages[i];
			System.out.println(message.getSubject());

			System.out.println(String.format(HEADER_EVALUATION_PUBLISH, courseCode, evaluationName));
			// matching email subject:
			if (!message.getSubject().equals(String.format(HEADER_EVALUATION_PUBLISH, courseCode, evaluationName))) {
				continue;
			} else {
				System.out.println("match");
			}

			// matching email content:
			String body = "";
			if (message.getContent() instanceof String) {
				body = message.getContent().toString();
			} else if (message.getContent() instanceof Multipart) {
				Multipart multipart = (Multipart) message.getContent();
				BodyPart bodypart = multipart.getBodyPart(0);
				body = bodypart.getContent().toString();
			}

			// check line 1: "The results of the evaluation:"
			if (body.indexOf("The results of the evaluation:") == -1) {
				System.out.println("fail 1");
				continue;
			}
			// check line 2: courseCode evaluationName
			if (body.indexOf(body.indexOf(courseCode + " " + evaluationName)) == -1) {
				System.out.println("fail 2");
				continue;
			}
			// check line 3: "have been published."
			if (body.indexOf("have been published.") == -1) {
				System.out.println("fail 3");
				continue;
			}
			// check line 4: "You can view the result here: [URL]"
			if (body.indexOf(TEAMMATES_APP_URL) == -1) {
				System.out.println("fail 4");
				continue;

			}
			// check line 5: teammates signature
			if (body.indexOf(TEAMMATES_APP_SIGNATURE) == -1) {
				System.out.println("fail 5");
				continue;

			}

			// Mark the message as read
			message.setFlag(Flags.Flag.SEEN, true);

			return true;
		}
		return false;
	}

	// -----------------------------Helper Functions----------------------------->> Setup:

	/**
	 * Start Chrome service, return service instance
	 * 
	 * @return the service instance
	 */
	private ChromeDriverService startChromeDriverService() {
		chromeService = new ChromeDriverService.Builder().usingChromeDriverExecutable(new File(Config.inst().getChromeDriverPath())).usingAnyFreePort().build();
		try {
			chromeService.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return chromeService;
	}

	public void setupSelenium() {
		System.out.println("Initializing Selenium.");

		if (Config.inst().BROWSER.equals("htmlunit")) {
			System.out.println("Using HTMLUnit.");

			setDriver(new HtmlUnitDriver());
			selenium = new WebDriverBackedSelenium(getDriver(), Config.inst().TEAMMATES_URL);

		} else if (Config.inst().BROWSER.equals("firefox")) {
			System.out.println("Using Firefox.");
			setDriver(new FirefoxDriver());
			selenium = new WebDriverBackedSelenium(getDriver(), Config.inst().TEAMMATES_URL);

		} else if (Config.inst().BROWSER.equals("chrome")) {

			System.out.println("Using Chrome");

			// Use technique here:
			// http://code.google.com/p/selenium/wiki/ChromeDriver
			ChromeDriverService service = startChromeDriverService();
			setDriver(new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome()));

			System.out.println(getDriver().toString());
			selenium = new WebDriverBackedSelenium(getDriver(), Config.inst().TEAMMATES_URL);

			/*
			 * Chrome hack. Currently Chrome doesn't support confirm() yet. http://code.google.com/p/selenium/issues/detail?id=27
			 */
			JavascriptExecutor js = (JavascriptExecutor) getDriver();
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
			setDriver(new RemoteWebDriver(executor, dc));

		}

		selenium.windowMaximize();
		selenium.open("/");
	}

	/**
	 * Called when the run is over.
	 */
	protected void wrapUp() {
		selenium.stop();
		if (chromeService != null && chromeService.isRunning())
			chromeService.stop();
	}

	// ------------Helper Functions-------------
	// Wait, Click, Fill in Elements:

	/**
	 * waiting functions:
	 */
	public void justWait() {
		waitAWhile(1500);
	}

	public void waitAWhile(long miliseconds) {
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
	public void waitForPageLoad() {
		try {
			selenium.waitForPageToLoad("15000");
		} catch (SeleniumException e) {
			System.err.println(e.getMessage());
		}
	}

	public void waitForElementPresent(By by) {
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

	public void waitForElementText(By locator, String value) {
		int counter = 0;
		while (true) {

			System.out.println("Looking for:"+locator + ": " + value);
			System.out.println("But found  :"+locator + ": " + getElementText(locator));
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

	public void waitforElementTextChange(By locator) {
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
	public void wdClick(By by) {
		if (isElementPresent(by)) {
			getDriver().findElement(by).click();
		} else {
			fail("Element " + by.toString() + " does not exists.");
		}
	}

	public void waitAndClick(By by) {
		waitForElementPresent(by);
		getDriver().findElement(by).click();
	}

	public void waitAndClickAndCheck(By currentElement, By nextElement) {
		// int counter = 0;
		while (!isElementPresent(nextElement)) {
			// if (counter++ > 1000)
			// fail("Timeout");
			waitForElementPresent(currentElement);
			getDriver().findElement(currentElement).click();
		}
	}

	public void waitAndClickAndConfirm(By by) {
		waitForElementPresent(by);
		clickAndConfirm(by);
	}

	/**
	 * click and confirm functions:
	 */
	private void confirmYes() {
		/*
		 * if (!Config.inst().BROWSER.equals("chrome")) { Alert alert = driver.switchTo().alert(); alert.accept(); }
		 */
	}

	private void confirmNo() {
		/*
		 * if (!Config.inst().BROWSER.equals("chrome")) { Alert alert = driver.switchTo().alert(); alert.dismiss(); }
		 */
	}

	public void chromeConfirmYes() {
		/*
		 * Huy: I have no idea why the driver.switchTo().alert() approach doesn't work even in Firefox (it supposed to!). This is a workaround to press Yes in the confirmation box. Same for function
		 * below for No.
		 */

		// if (Config.inst().BROWSER.equals("chrome")) {
		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		js.executeScript("window.confirm = function(msg){ return true;};");
		// }
	}

	public void chromeConfirmNo() {
		// if (Config.inst().BROWSER.equals("chrome")) {
		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		js.executeScript("window.confirm = function(msg){ return false;};");
		// }
	}

	public void clickAndConfirm(By by) {
		chromeConfirmYes();
		waitAndClick(by);
		confirmYes();
	}

	public void waitAndClickAndCancel(By by) {
		chromeConfirmNo();
		waitAndClick(by);
		confirmNo();
	}

	/**
	 * WebDriver fills the input field with text value (will clear the data first)
	 */
	public void wdFillString(By by, String value) {
		WebElement ele = getDriver().findElement(by);
		ele.clear();
		ele.sendKeys(value);
	}

	public boolean isElementPresent(By by) {
		return getDriver().findElements(by).size() != 0;
	}

	/**
	 * Retrieve element's text through WebDriver.
	 * 
	 * Similar to getElementText()
	 * 
	 * @return empty string if element is not found.
	 */
	public String getElementText(By locator) {
		if (!isElementPresent(locator))
			return "";
		WebElement elm = getDriver().findElement(locator);
		return elm.getText();
	}

	/**
	 * Retrieve the element's `value` attribute. Usually used for elements like input, option, etc.
	 * 
	 * @param locator
	 * @return
	 */
	public String getElementValue(By locator) {
		return getDriver().findElement(locator).getAttribute("value");
	}

	public void selectDropdownByValue(By locator, String value) {
		waitForElementPresent(locator);
		justWait();
		Select select = new Select(getDriver().findElement(locator));
		justWait();
		select.selectByValue(value);
		justWait();
	}

	public String getDropdownSelectedValue(By locator) {
		Select select = new Select(getDriver().findElement(locator));
		return select.getFirstSelectedOption().getAttribute("value");
	}

	// -----------------------------Helper Functions----------------------------->> Check and Verify:
	/**
	 * Helper method to check that we're at the main page Checking for the Coordinator and Student links
	 */
	public void verifyMainPage() {
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
	public boolean isLocalLoginPage() {
		if (isElementPresent(By.id("email")) && isElementPresent(By.id("isAdmin")))
			return true;
		return false;
	}

	/**
	 * Helper method to check that we're at the login page Checking for the e-mail and password fields, and the sign in button
	 */
	public boolean isGoogleLoginPage() {
		if (isElementPresent(By.id("Email")) && isElementPresent(By.id("Passwd")) && isElementPresent(By.id("signIn")))
			return true;

		return false;
	}

	// WS: add function verifyGoogleLoginPage
	public void verifyGoogleLoginPage() {
		if (!isGoogleLoginPage())
			return;
		fail("Not in Google Login Page");
	}

	// Helper method to check that we're at the Coordinator page (after login)
	// Checking for links at the top, and add course form
	public void verifyCoordinatorPage() {
		if (isElementPresent(By.id("courseid")) && isElementPresent(By.id("coursename")))
			return;

		fail("Not in Coordinator Page");
	}

	// TODO: verify course detail page
	// public void verifyCoordViewCourseDetailPage() {
	// //course ID
	//
	// //course name
	//
	// //teams
	//
	// //total students
	// }

	// Helper method to check that we're at the Student page (after login)
	// Checking for links at the top, and add course form
	public void verifyStudentPage() {
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
	public void verifyEvaluationPage() {
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

	/**
	 * Checks that the course has been added Checking for the course details appearing in the table Page: Coordinator home TODO: change to any number of previous courses
	 */
	public void verifyAddedCourse(String courseId, String courseName) {
		// Check for courseId
		System.out.println("course id : " + this.getCourseID(this.findCourseRow(courseId)));
		assertEquals(courseId, this.getElementText(getCourseID(this.findCourseRow(courseId))));

		// Check for course name
		assertEquals(courseName, this.getElementText(getCourseName(this.findCourseRow(courseId))));

		// Check for default number of teams - 0
		assertEquals("0", this.getCourseTeams(this.findCourseRow(courseId)));
	}

	public boolean isCoursePresent(String courseId, String courseName) {
		int totalCourses = countTotalCourses();
		boolean isPresent = false;
		for (int i = 0; i < totalCourses; i++) {
			if (getElementText(By.id("courseID" + i)).equalsIgnoreCase(courseId) && getElementText(By.id("courseName" + i)).equals(courseName)) {
				isPresent = true;
				continue;
			}
		}

		return isPresent;
	}

	// Checks that we're at the student enrollment page
	// Checking for the form fields and the buttons
	public void verifyEnrollPage() {
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
	public void verifyEnrollment(int added, int edited) {
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
	public void verifyEvaluationAdded(String courseId, String evalName, String status, String resp) {

		for (int i = 0; i < this.countTotalEvaluations(); i++) {
			if (this.getEvaluationCourseID(i).equals(courseId) && this.getEvaluationName(i).equals(evalName)) {
				assertEquals(status, this.getEvaluationStatus(i));
				assertEquals(resp, this.getEvaluationResponse(i));
			}
		}
	}

	// -----------------------------Helper
	// Functions----------------------------->> Others:

	private void _login(String email, String password) {
		waitAWhile(1000);
		if (isLocalLoginPage()) {
			wdFillString(By.id("email"), email);
			selenium.click("css=input[value='Log In']");
			checkGoogleApplicationApproval();
			waitForPageLoad();
		} else if (isGoogleLoginPage()) {
			// Fill in login credentials
			wdFillString(By.id("Email"), email);
			getDriver().findElement(By.id("Passwd")).sendKeys("aa");
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
	private void checkGoogleApplicationApproval() {
		justWait();
		if (isElementPresent(By.id("approve_button"))) {
			wdClick(By.id("persist_checkbox"));
			wdClick(By.id("approve_button"));
		}
	}

	public void assertEqualsOr(String e1, String e2, String a) {
		if (e1.equalsIgnoreCase(a) || e2.equalsIgnoreCase(a)) {
			org.junit.Assert.assertTrue(true);
		} else {
			org.junit.Assert.assertEquals(e1, a);
			org.junit.Assert.assertEquals(e2, a);
		}

	}

	/**
	 * Helper function to clean up email account
	 * 
	 * @throws Exception
	 */
	// protected void cleanupGmailInbox() throws Exception {
	// for (int i = 0; i < sc.students.size(); i++) {
	// SharedLib.markAllEmailsSeen(sc.students.get(i).email,
	// Config.inst().TEAMMATES_APP_PASSWD);
	// System.out.println("clean up gmail Inbox for " + sc.students.get(i).name);
	// }
	// }

	/**
	 * Shortcut for System.out.println
	 */
	public void cout(String message) {
		System.out.println(message);
	}

	public String getStudentsString(List<Student> list) {
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

	public void setInUse(boolean b) {
		this.inUse = b;
	}

	public boolean isInUse() {
		return this.inUse;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public DefaultSelenium getSelenium() {
		return selenium;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public void gotoHome() {
		selenium.open("/");
	}

	public void openSelectedWindow(String url, String title) {
		selenium.open(url);
		selenium.selectWindow(title);
		selenium.windowFocus();
	}

	public void clickAndOpenNewWindow(By link, String window) {
		waitAndClick(link);

		selenium.selectWindow(window);
		selenium.windowFocus();

	}

	public void closeSelectedWindow() {
		// Close the window and back to the main one
		selenium.close();
		selenium.selectWindow("null");
		selenium.windowFocus();
	}

	public boolean isTextPresent(String text) {
		return selenium.isTextPresent(text);
	}

	public void verifyPageHTML(String url, String filepath) throws Exception {
		try {
			URL help = new URL(url);
			URLConnection yc = help.openConnection();
			FileInputStream helpFile = new FileInputStream(filepath);
			BufferedReader actual = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			BufferedReader expected = new BufferedReader(new InputStreamReader(new DataInputStream(helpFile)));

			String expectedLine;
			String actualLine;
			while ((expectedLine = expected.readLine()) != null) {
				actualLine = actual.readLine();
				assertNotNull("Expected had more lines then the actual.", actualLine);
				assertEquals(expectedLine, actualLine);
				System.out.println(actualLine);
			}

			assertNull("Actual had more lines then the expected.", actual.readLine());

			actual.close();
			expected.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			assertTrue(false);
		}
	}
	
	public void verifyObjectHTML(String filepath, String div) throws Exception {
		try {
			String pageSrc = driver.getPageSource();
			FileInputStream refSrc = new FileInputStream(filepath);
			BufferedReader actual = new BufferedReader(new StringReader(pageSrc));
			BufferedReader expected = new BufferedReader(new InputStreamReader(new DataInputStream(refSrc)));

			String expectedLine;
			String actualLine;
			while((actualLine = actual.readLine()) != null) {
				if(actualLine.contains(div)) {
					while ((expectedLine = expected.readLine()) != null) {
						assertNotNull("Expected had more lines then the actual.", actualLine);
						assertEquals(expectedLine, actualLine);
						System.out.println(actualLine);
						actualLine = actual.readLine();
					}
					break;
				}
			}
			
			actual.close();
			expected.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			assertTrue(false);
		}
	}
	
	public void verifyCurrentPageHTML(String filepath) throws Exception {

		String NL = System.getProperty("line.separator");
	    
		StringBuilder expectedContentBuilder = new StringBuilder();
	    Scanner scanner = new Scanner(new FileInputStream(filepath));    
	    while (scanner.hasNextLine()){
	    	expectedContentBuilder.append(scanner.nextLine() + NL);
	    }
    	scanner.close();
    	//Todo: fix the next line so that we do not have to change the version number every time. 
		String expectedContent = expectedContentBuilder.toString().replace("{{version}}", "4.17.1");
		
		String pageSrc = driver.getPageSource();
		BufferedReader actual = new BufferedReader(new StringReader(pageSrc));
		StringBuilder actualContentBuilder = new StringBuilder();
		String actualLine;
		while ((actualLine = actual.readLine()) != null) {
			actualContentBuilder.append(actualLine + NL);		
		}	
		actual.close();
		String actualContent = actualContentBuilder.toString();
		
		assertEquals(expectedContent, actualContent);
			
	}
	
	// --------------------------------- Home page ------------------------------ //
	/**
	 * Go to Coordinator home page
	 */
	public void goToCoordHome() {
		clickHomeTab();
		justWait();
		verifyCoordHomePage();
	}
	
	/**
	 * Click and cancel Delete of a particular course of the coordinator
	 */
	public void clickAndCancelCoordCourseDelete(By by) {
		waitAndClickAndCancel(by);
	}
	public void verifyCoordHomePage() {
		if (isElementPresent(homeTab) && isElementPresent(coursesTab) && isElementPresent(evaluationsTab) && isElementPresent(helpTab) && isElementPresent(logoutTab) 
				&& isElementPresent(coordAddNewCourseLink))
			return;
		
		fail("Not in Coordinator Page");
	}
	// --------------------------------- Home page ------------------------------ //
	/**
	 * Go to Student home page
	 */
	public void goToStudentHome() {
		clickHomeTab();
		justWait();
		verifyStudentHomePage();
	}
	public void verifyStudentHomePage() {
		if (isElementPresent(homeTab) && isElementPresent(coursesTab) && isElementPresent(evaluationsTab) && isElementPresent(helpTab) && isElementPresent(logoutTab) 
				&& isElementPresent(studentJoinNewCourseLink))
			return;

		fail("Not in Student Page");
	}
	// Helper method to verify that we're at the Student courses page
	// Checking for the various fields expected in add course form
	public void verifyStudentCoursesPage() {
		if (isElementPresent(pageTitle) && isElementPresent(studentInputRegKey) && isElementPresent(studentJoinCourseButton))
			return;

		fail("Not in Student Courses Page");
	}
	// Helper method to verify that we're at the Student evaluations page
	// Checking for the headers expected in evaluations page
	public void verifyStudentEvaluationsPage() {
		if (isTextPresent(PENDING_EVALUATIONS_HEADER) && isTextPresent(PAST_EVALUATIONS_HEADER))
			return;
		
		fail("Not in Student Evaluations Page");
	}
	
	// Helper method to verify that we're at the Student do evaluations page
	// Also, can be used to verify that we're at the Student edit evaluation submission page
	// Checking for the various fields expected in the do evaluations page
	public void verifyStudentDoOrEditEvaluationPage() {
		if (isElementPresent(studentEvaluationCourseID) && isElementPresent(studentEvaluationEvaluationName) 
				&& isElementPresent(studentEvaluationOpeningTime) && isElementPresent(studentEvaluationClosingTime) 
				 && isElementPresent(studentEvaluationInstructions))
			return;
		 
		fail("Not in Student Do Evaluation or Edit Evaluation Submission Page");
	}
	public void verifyStudentEvaluationResultsPage() {
			waitForPageLoad();
			for (int x = 0; ; x++) {
				if (x >= 40)
					fail("timeout");
				
				if (isElementPresent(studentEvaluationResultStudentName) && isElementPresent(studentEvaluationResultCourseID) 
						&& isElementPresent(studentEvaluationResultTeamName) && isElementPresent(studentEvaluationResultEvaluationName) 
						 && isElementPresent(studentEvaluationResultClaimedPoints) && isElementPresent(studentEvaluationResultOpeningTime) 
							&& isElementPresent(studentEvaluationResultPerceivedPoints) && isElementPresent(studentEvaluationResultClosingTime))
					break;
				
				waitAWhile(200);
			}
	} 
	// Helper method to verify that we're at the Student course details page
	// Checking for the various fields expected in course details page
	public void verifyStudentViewCourseDetailsPage() {
		if (isElementPresent(studentCourseDetailCourseID) && isElementPresent(studentCourseDetailTeamName) && isElementPresent(studentCourseDetailCourseName) 
				&& isElementPresent(studentCourseDetailStudentName) && isElementPresent(studentCourseDetailCoordinatorName) 
				&& isElementPresent(studentCourseDetailStudentEmail) && isElementPresent(studentCourseDetailStudentTeammates))
			return;
		
		fail("Not in Student Course Details Page");
	}
	/**
	 * Click and cancel Delete of a particular evaluation in a specific course of the coordinator
	 */
	public void clickAndCancelCoordEvaluationDelete(By by) {
		waitAndClickAndCancel(by);
	}
	// evaluations table:
	public By getCoordEvaluationViewResults(int row) {
		return By.xpath(String.format(DATAFORM_TABLE_CELL + "//a[@class='t_eval_view']", row + 2, 5));
	}
	
	public By getCoordEvaluationEditResults(int row) {
		return By.xpath(String.format(DATAFORM_TABLE_CELL + "//a[@class='t_eval_edit']", row + 2, 5));
	}
	
	public By getCoordEvaluationPublishResults(int row) {
		return By.xpath(String.format(DATAFORM_TABLE_CELL + "//a[@class='t_eval_publish']", row + 2, 5));
	}
	
	public By getCoordEvaluationUnpublishResults(int row) {
		return By.xpath(String.format(DATAFORM_TABLE_CELL + "//a[@class='t_eval_unpublish']", row + 2, 5));
	}
	
	public By getCoordEvaluationRemindResults(int row) {
		return By.xpath(String.format(DATAFORM_TABLE_CELL + "//a[@class='t_eval_remind']", row + 2, 5));
	}
	
	public By getCoordEvaluationDeleteResults(int row) {
		return By.xpath(String.format(DATAFORM_TABLE_CELL + "//a[@class='t_eval_delete']", row + 2, 5));
	}
	/**
	 * Click and cancel Publish of results of a particular evaluation in a specific course of the coordinator
	 */
	public void clickAndCancelCoordEvaluationPublish(int row) {
		clickAndConfirm(getCoordEvaluationPublishResults(row));
	}
	public int getCoordTotalEvaluationsCount() {
		if (getElementText(By.xpath(String.format(DATAFORM_TABLE_CELL, 2, 1))).isEmpty()) {
			return 0;
		 		} else {
		 	return selenium.getXpathCount(DATAFORM_TABLE_ROW).intValue() - 1;
		 		}
		 	}
	private int findCoordEvaluationRow(String courseID, String evalName) {
 		int i = 0;
		
		while (i < getCoordTotalEvaluationsCount()) {
			if (getEvaluationCourseID(i).equals(courseID) && getEvaluationName(i).equals(evalName)) {
 				return i;
 			}
 			i++;
 		}
 		return -1;
 	}	
	public void clickAndCancelCoordEvaluationPublish(String courseID, String evalName) {
		int row = findCoordEvaluationRow(courseID, evalName);
		
		if (row > -1) {
			clickAndCancelCoordEvaluationPublish(row);
		} else {
			fail("Evaluation not found.");
		}
	}
	public void clickAndCancelCoordEvaluationRemind(By by) {
		waitAndClickAndCancel(by);
 	}
	/**
	 * Click and cancel Unpublish of results of a particular evaluation in a specific course of the coordinator
	 */
	public void clickAndCancelCoordEvaluationUnpublish(int row) {
		clickAndConfirm(getCoordEvaluationPublishResults(row));
	}
	
	public void clickAndCancelCoordEvaluationUnpublish(String courseID, String evalName) {
		int row = findCoordEvaluationRow(courseID, evalName);
		
		if (row > -1) {
			clickAndCancelCoordEvaluationUnpublish(row);
		} else {
			fail("Evaluation not found.");
		}
	}
	// Helper method to verify that we're at the Coordinator evaluation results summary page
	// Checking for the various fields expected in the evaluation results summary page
	public void verifyCoordEvaluationResultsPage() {
		if (isElementPresent(resultSummaryRadio) && isElementPresent(resultDetailRadio) && isElementPresent(resultReviewerRadio) 
				&& isElementPresent(resultRevieweeRadio))
			return;
		 
		fail("Not in Coordinator Evaluation Results Page");
	}
	
	public By coordCourseDetailsCourseID = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 1, 2));
	public By coordCourseDetailsCourseName = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 2, 2));
	public By coordCourseDetailsTeams = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 3, 2));
	public By coordCourseDetailsTotalStudents = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 4, 2));
	
	public By coordCourseDetailsRemindStudentsButton = By.id("button_remind");
	public By coordCourseDetailsDeleteStudentsButton = By.className("t_delete_students");
	
	public By coordCourseDetailsStudentNameSorting = By.id("button_sortstudentname");
	public By coordCourseDetailsTeamSorting = By.id("button_sortstudentteam");
	public By coordCourseDetailsJoinStatusSorting = By.id("button_sortstudentstatus");
	// Helper method to verify that we're at the Coordinator course details page
	// Checking for the various fields expected in course details page
	 public void verifyCoordViewCourseDetailsPage() {
		 if (isElementPresent(coordCourseDetailsCourseID) && isElementPresent(coordCourseDetailsCourseName) && isElementPresent(coordCourseDetailsTeams) 
				 && isElementPresent(coordCourseDetailsTotalStudents))
			return;
		 
		fail("Not in Coordinator Course Details Page");
	 }
	// Helper method to verify that we're at the Coordinator courses page
	// Checking for the various fields expected in add course form
	public void verifyCoordCoursesPage() {
		if (isElementPresent(pageTitle) && isElementPresent(coordInputCourseID) && isElementPresent(coordInputCourseName) && isElementPresent(coordAddCourseButton))
			return;
		
		fail("Not in Coordinator Courses Page");
	}
	// Helper method to verify that we're at the student enrollment page
	// Checking for the form fields and the buttons
	public void verifyCoordEnrolPage() {
		for (int x = 0;; x++) {
			if (x >= 40)
				fail("timeout");
			
			if (isElementPresent(coordEnrolInfo) && isElementPresent(coordEnrolButton))
				break;
			
			waitAWhile(200);
		}
	}
	public By inputPeerFeedbackStatus = By.id("commentsstatus");
	public By inputOpeningDate = By.id("start");
	public By inputOpeningTime = By.id("starttime");
	public By inputTimeZone = By.id("timezone");
	// Helper method to verify that we're at the Coordinator evaluations page
	// Checking for the various fields expected in add evaluation form
	public void verifyCoordEvaluationsPage() {
		for (int x = 0;; x++) {
			if (x >= 40)
				fail("timeout");
            
			if (isElementPresent(coordInputCourseID) && isElementPresent(inputEvaluationName) && isElementPresent(inputPeerFeedbackStatus)
					&& isElementPresent(inputInstruction) && isElementPresent(inputOpeningDate) && isElementPresent(inputOpeningTime)
					&& isElementPresent(inputClosingDate) && isElementPresent(inputClosingTime) && isElementPresent(inputTimeZone)
					&& isElementPresent(inputGracePeriod))
				break;
			
			waitAWhile(200);
		}
	}
	public By coordEditCourseID = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 1, 1));
	public By coordEditEvaluationName = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 1, 2));
	public By coordEditPeerFeedbackStatus = By.id("commentsstatus");
	public By coordEditInstruction = By.id("instr");
	public By coordEditOpeningDate = By.id("start");
	public By coordEditOpeningTime = By.id("starttime");
	public By coordEditClosingDate = By.id("deadline");
	public By coordEditClosingTime = By.id("deadlinetime");
	public By coordEditTimeZone = By.id("timezone");
	public By coordEditGracePeriod = By.id("graceperiod");
	
	public By coordEditEvaluationButton = By.id("button_editevaluation");
	public By coordEditEvaluationBackButton = By.className("t_back");
	// Helper method to verify that we're at the Coordinator edit evaluation page
	// Checking for the various fields expected in edit evaluation form
	public void verifyCoordEditEvaluationPage() {
		if (isElementPresent(coordEditCourseID) && isElementPresent(coordEditEvaluationName) && isElementPresent(coordEditPeerFeedbackStatus)
				&& isElementPresent(coordEditInstruction) && isElementPresent(coordEditOpeningDate) && isElementPresent(coordEditOpeningTime)
				&& isElementPresent(coordEditClosingDate) && isElementPresent(coordEditClosingTime) && isElementPresent(coordEditTimeZone)
				&& isElementPresent(coordEditGracePeriod))
			return;
	}
	public void clickAndCancelCoordEvaluationUnpublish(By by) {
		waitAndClickAndCancel(by);
	}
	public void clickAndCancelCoordEvaluationPublish(By by) {
		waitAndClickAndCancel(by);
 	}
}
