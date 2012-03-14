package teammates.testing.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

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

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * A browser instance represents a real browser instance + context to the app
 * 
 * Used to be BaseTest
 * 
 * @author Huy
 * 
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
	public By courseTab = By.className("t_courses");
	public By evaluationTab = By.className("t_evaluations");
	public By helpTab = By.className("t_help");
	public By logoutTab = By.className("t_logout");

	/**
	 * course:
	 * 
	 */
	// add course:
	public By addCoursePageTitle = By.xpath("//div[@id='headerOperation']//h1");
	public By inputCourseID = By.id("courseid");
	public By inputCourseName = By.id("coursename");
	public By addCourseButton = By.id("btnAddCourse");

	public By courseIDSorting = By.id("button_sortcourseid");
	public By courseNameSorting = By.id("button_sortname");

	// enrol:
	public By enrolInfo = By.id("information");
	public By enrolButton = By.id("button_enrol");
	public By enrolBackButton = By.className("t_back");

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

	// student detail:
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

	public String getReviewerIndividualClaimedPoint() {
		String equal = getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@class='result_table']//th[%d]//div[%d]", 2, 1)));
		String point = getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@class='result_table']//th[%d]//span[%d]", 2, 2)));
		return equal + point;
	}

	// detail result:
	public By resultTopButton = By.id("button_top");

	/**
	 * coordinator:
	 */
	public By coordEvaluationSubmitButton = By.id("button_editevaluationresultsbyreviewee");
	/**
	 * student:
	 */
	// student course:
	public By inputRegKey = By.id("regkey");
	public By studentJoinCourseButton = By.id("btnJoinCourse");
	// student evaluation:
	public By studentSubmitEvaluationButton = By.name("submitEvaluation");
	public By studentEvaluationBackButton = By.className("t_back");
	public By studentEvaluationCancelButton = By.className("t_back");

	/**
	 * message:
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

	public final String MESSAGE_ENROL_REMIND_TO_JOIN = "Emails have been sent to unregistered students.";
	public final String ERROR_MESSAGE_ENROL_INVALID_EMAIL = "E-mail address should contain less than 40 characters and be of a valid syntax.";

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

		verifyCoordinatorPage();
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
		verifyStudentPage();
	}

	/**
	 * Coordinator & Student Logout
	 */
	public void logout() {
		cout("Signing out.");
		waitAndClick(logoutTab);
		// Check that we're at the main page after logging out
		if (Config.inst().TEAMMATES_URL.contains("localhost")) {
			cout("localhost testing");
			selenium.open(Config.inst().TEAMMATES_URL);

		}
		verifyMainPage();
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

	public void studentClickEditEvaluation(int row) {
		waitAndClick(By.id("editEvaluation" + row));
	}

	public void studentClickEditEvaluation(String courseId, String evalName) {
		int row = this.studentFindEvaluationRow(courseId, evalName);
		if (row > -1) {
			studentClickEditEvaluation(row);
		} else {
			fail("Student's evaluation not found. Cannot click edit evaluation.");
		}
	}

	public void studentClickEvaluationViewResults(int row) {
		By link = By.xpath(String.format("//div[@id='studentPastEvaluations']//table[@id='dataform']//tr[%d]//td[%d]//a['View Results']", row + 2, 5));
		waitAndClick(link);
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
		waitAndClick(courseTab);
		justWait();
		verifyCoordinatorPage();
	}

	public void clickCourseTab() {
		waitAndClick(courseTab);
	}

	/**
	 * page: Add Course
	 * 
	 * @param row
	 */
	public void addCourse(String courseid, String coursename, int courseIndex) {
		wdFillString(inputCourseID, courseid);
		wdFillString(inputCourseName, coursename);
		// wdClick(addCourseButton);
		waitAndClickAndCheck(addCourseButton, By.id("courseID" + courseIndex));
		justWait();
	}

	public void addCourse(String courseid, String coursename) {
		wdFillString(inputCourseID, courseid);
		wdFillString(inputCourseName, coursename);
		waitAndClick(addCourseButton);
		justWait();
	}

	public By getCourseID(int row) {
		return By.id("courseID" + row);
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

	public By getCourseName(int row) {
		return By.id("courseName" + row);
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

		wdFillString(enrolInfo, getStudentsString(students));
		wdClick(enrolButton);
		justWait();
	}

	public void enrollStudents(List<Student> students, String courseID) {
		clickCourseEnrol(courseID);
		verifyEnrollPage();

		wdFillString(enrolInfo, getStudentsString(students));
		wdClick(enrolButton);
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
	// Evaluation:
	/**
	 * Snippet to go to Evaluations page
	 */
	public void gotoEvaluations() {
		wdClick(evaluationTab);
		justWait();
		verifyEvaluationPage();
	}

	public void clickEvaluationTab() {
		wdClick(evaluationTab);
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
		// wdClick(addEvaluationButton);
		waitAndClickAndCheck(addEvaluationButton, By.id("evaluation" + evalIndex));
	}

	public void addEvaluation(String courseID, String evalName, String dateValue, String nextTimeValue, String comments, String instructions, Integer gracePeriod) {
		clickEvaluationTab();

		// Select the course
		waitAndClick(inputCourseID);
		selectDropdownByValue(inputCourseID, courseID);

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

	public void clickEvaluationViewResults(int row) {
		By link = By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_view']", row + 2, 5));
		waitAndClick(link);
	}

	public void clickEvaluationViewResults(String courseId, String evalName) {
		int row = findEvaluationRow(courseId, evalName);

		if (row > -1) {
			clickEvaluationViewResults(row);
		} else {
			fail("Evaluation not found.");
		}
	}

	public void clickEvaluationEdit(int row) {
		By link = By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_edit']", row + 2, 5));
		waitAndClick(link);
	}

	public void clickEvaluationEdit(String courseId, String evalName) {
		int row = findEvaluationRow(courseId, evalName);
		if (row > -1) {
			clickEvaluationEdit(row);
		} else {
			fail("Evaluation not found.");
		}
	}

	public void clickEvaluationPublish(int row) {
		By link = By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_publish']", row + 2, 5));
		clickAndConfirm(link);
	}

	public void clickEvaluationPublish(String courseId, String evalName) {
		int row = findEvaluationRow(courseId, evalName);
		if (row > -1) {
			clickEvaluationPublish(row);
		} else {
			fail("Evaluation not found.");
		}
	}

	public void clickEvaluationUnpublish(int row) {
		By link = By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_unpublish']", row + 2, 5));
		clickAndConfirm(link);
	}

	public void clickEvaluationUnpublish(String courseId, String evalName) {
		int row = findEvaluationRow(courseId, evalName);
		if (row > -1) {
			clickEvaluationUnpublish(row);
		} else {
			fail("Evaluation not found.");
		}
	}

	public void clickAndConfirmEvaluationRemind(int row) {
		By link = By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_remind']", row + 2, 5));
		clickAndConfirm(link);
	}

	public void clickAndConfirmEvaluationRemind(String courseId, String evalName) {
		int row = findEvaluationRow(courseId, evalName);
		if (row > -1) {
			clickAndConfirmEvaluationRemind(row);
		} else {
			fail("Evaluation not found.");
		}
	}

	public void clickAndConfirmEvaluationDelete(int row) {
		By link = By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_delete']", row + 2, 5));
		clickAndConfirm(link);
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
		By link = By.xpath(String.format("//div[@id='coordinatorEvaluationTable']//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_eval_delete']", row + 2, 5));
		waitAndClickAndCancel(link);
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
	public void clickReviewerSummaryView(int row) {
		waitAndClick(By.id("viewEvaluationResults" + row));
	}

	public void clickReviewerSummaryEdit(int row) {
		waitAndClick(By.id("editEvaluationResults" + row));
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
	public By getReviewerIndividualToStudent(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", row + 2, 1));
	}

	public By getReviewerIndividualToStudentPoint(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", row + 2, 2));
	}

	// reviewee individual:
	public By getRevieweeIndividualFromStudent(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", row + 2, 1));
	}

	public By getRevieweeIndividualFromStudentPoint(int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@id='dataform']//tr[%d]//td[%d]", row + 2, 2));
	}

	// reviewer detail:
	public By getReviewerDetailClaimed(int team, int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[%d]//tr[%d]//td[%d]", team, row + 2, 2));
	}

	public By getReviewerDetailPerceived(int team, int row) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[%d]//tr[%d]//td[%d]", team, row + 3, 2));
	}

	public By getReviewerDetailToStudent(int position, int studentIndex) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//tr[%d]//table[@id='dataform']//tr[%d]//td[%d]", position + 7, studentIndex + 2, 1));
	}

	public By getReviewerDetailToStudentPoint(int position, int studentIndex) {
		return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//tr[%d]//table[@id='dataform']//tr[%d]//td[%d]", position + 7, studentIndex + 2, 2));
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
		this.waitForElementPresent(locator);
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
			}

			assertNull("Actual had more lines then the expected.", actual.readLine());

			actual.close();
			expected.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	public void verifyCurrentPageHTML(String filepath) throws Exception {
		try {
			String pageSrc = driver.getPageSource();
			FileInputStream refSrc = new FileInputStream(filepath);
			BufferedReader actual = new BufferedReader(new StringReader(pageSrc));
			BufferedReader expected = new BufferedReader(new InputStreamReader(new DataInputStream(refSrc)));

			String expectedLine;
			String actualLine;
			while ((expectedLine = expected.readLine()) != null) {
				actualLine = actual.readLine();
				assertNotNull("Expected had more lines then the actual.", actualLine);
				assertEquals(expectedLine, actualLine);
			}

			assertNull("Actual had more lines then the expected.", actual.readLine());

			actual.close();
			expected.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
	
}
