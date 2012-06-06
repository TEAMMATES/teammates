package teammates.testing.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
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

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SeleneseCommandExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import teammates.api.Common;
import teammates.exception.NoAlertAppearException;
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
 * @author Xialin
 * @author Shakthi
 * @author Aldrian (since 14 May 2012)
 */
public class BrowserInstance {
	protected DefaultSelenium selenium = null;
	private WebDriver driver = null;
	protected ChromeDriverService chromeService = null;
	private boolean inUse = false;
	
	private static final int TIMEOUT = 10; // In seconds
	private static final int RETRY = 20;
	private static final int RETRY_TIME = TIMEOUT*1000/RETRY; // In milliseconds

	/* ---------------------------------------------------------------
	 * UI Element
	 * Below are the declaration for variables referring to UI Element.
	 * Each is represented by its By instance
	 * -------------------------------------------------------------- */
	
	// --------------------------- General ----------------------------- //
	// Homepage buttons
	public final By COORD_LOGIN_BUTTON = By.name("COORDINATOR_LOGIN");
	public final By STUDENT_LOGIN_BUTTON = By.name("STUDENT_LOGIN");

	// Tabs
	public By homeTab = By.className("t_home");
	public By teamFormingTab = By.className("t_teamForming");
	public By courseTab = By.className("t_courses");
	public By evaluationTab = By.className("t_evaluations");
	public By helpTab = By.className("t_help");
	public By logoutTab = By.className("t_logout");

	// Table elements
	public By pageTitle = By.xpath("//div[@id='headerOperation']//h1");

	public static final String HEADER_FORM_TABLE_CELL = "//table[@class='headerform']//tbody//tr//td";
	public static final String DETAIL_FORM_TABLE_CELL = "//table[@class='detailform']//tbody//tr//td";
	public static final String DATAFORM_TABLE_ROW = "//table[@id='dataform']//tr";
	public static final String DATAFORM_TABLE_CELL = DATAFORM_TABLE_ROW + "[%d]//td[%d]";

	// -------------------------------- Coordinator -------------------------------- //
	// Homepage
	public By coordHomeAddNewCourseLink = By.id("addNewCourse");

	// Course list at home
	/**
	 * Returns the rowID (at homepage) of a course based on course ID
	 * @param courseID
	 * @return
	 */
	public int getCoordHomeCourseRowID(String courseID) {
		int id = 0;
		while(isElementPresent(By.id("course"+id))){
			if(getElementText(By.xpath("//div[@id='course"+id+"']/div[@class='result_homeTitle']/h2")).startsWith("["+courseID+"]")){
				return id;
			}
			id++;
		}
		return -1;
	}
	
	public By getCoordHomeCourseEnrollLinkLocator(int rowID) { return By.className("t_course_enroll" + rowID); }
	public By getCoordHomeCourseViewLinkLocator(int rowID) { return By.className("t_course_view" + rowID); }
	public By getCoordHomeCourseAddEvaluationLinkLocator(int rowID) { return By.className("t_course_add_eval" + rowID); }
	public By getCoordHomeCourseDeleteLinkLocator(int rowID) { return By.className("t_course_delete" + rowID); }
	
	public By getCoordHomeCourseEnrollLinkLocator(String courseID) { return getCoordHomeCourseEnrollLinkLocator(getCoordHomeCourseRowID(courseID)); }
	public By getCoordHomeCourseViewLinkLocator(String courseID) { return getCoordHomeCourseViewLinkLocator(getCoordHomeCourseRowID(courseID)); }
	public By getCoordHomeCourseAddEvaluationLinkLocator(String courseID) { return getCoordHomeCourseAddEvaluationLinkLocator(getCoordHomeCourseRowID(courseID)); }
	public By getCoordHomeCourseDeleteLinkLocator(String courseID) { return getCoordHomeCourseDeleteLinkLocator(getCoordHomeCourseRowID(courseID)); }
	
	// Course list at course page
	/**
	 * Finds the rowID (at course page) number of a course based on course ID
	 * @param courseID
	 * @return
	 */
	public int getCourseRowID(String courseID) {
		for (int i = 0; i < countCourses(); i++) {
			if (getElementText(getCourseIDCell(i)).equals(courseID)) {
				return i;
			}
		}
		return -1;
	}
	public By getCoordCourseEnrollLinkLocator(int rowID) { return By.xpath(String.format("//div[@id='coordinatorCourseTable']"+DATAFORM_TABLE_CELL+"//a[@class='t_course_enroll']", rowID+2, 6)); }
	public By getCoordCourseViewLinkLocator(int rowID) { return By.xpath(String.format("//div[@id='coordinatorCourseTable']"+DATAFORM_TABLE_CELL+"//a[@class='t_course_view']", rowID+2, 6)); }
	public By getCoordCourseAddEvaluationLinkLocator(int rowID) { return By.xpath(String.format("//div[@id='coordinatorCourseTable']"+DATAFORM_TABLE_CELL+"//a[@class='t_course_add_eval']", rowID+2, 6)); }
	public By getCoordCourseDeleteLinkLocator(int rowID) { return By.xpath(String.format("//div[@id='coordinatorCourseTable']"+DATAFORM_TABLE_CELL+"//a[@class='t_course_delete']", rowID+2, 6)); }
	
	public By getCoordCourseEnrollLinkLocator(String courseID) { return getCoordCourseEnrollLinkLocator(getCourseRowID(courseID)); }
	public By getCoordCourseViewLinkLocator(String courseID) { return getCoordCourseViewLinkLocator(getCourseRowID(courseID)); }
	public By getCoordCourseAddEvaluationLinkLocator(String courseID) { return getCoordCourseAddEvaluationLinkLocator(getCourseRowID(courseID)); }
	public By getCoordCourseDeleteLinkLocator(String courseID) { return getCoordCourseDeleteLinkLocator(getCourseRowID(courseID)); }
	
	// Evaluation table at evaluation page
	/**
	 * Finds the rowID (at evaluation page) number of a specific evaluation based on
	 * the course ID and evaluation name 
	 * @param courseId
	 * @param evalName
	 * @return
	 */
	public int getEvaluationRowID(String courseId, String evalName) {
		int i = 0;
		while (i < countEvaluations()) {
			if (getEvaluationCourseID(i).equals(courseId) && getEvaluationName(i).equals(evalName)) {
				return i;
			}
			i++;
		}
		return -1;
	}
	public By getCoordEvaluationViewResultsLinkLocator(int rowID) { return By.id("viewEvaluation" + rowID); }
	public By getCoordEvaluationEditLinkLocator(int rowID) { return By.id("editEvaluation" + rowID); }
	public By getCoordEvaluationDeleteLinkLocator(int rowID) { return By.id("deleteEvaluation" + rowID); }
	public By getCoordEvaluationRemindLinkLocator(int rowID) { return By.id("remindEvaluation" + rowID); }
	public By getCoordEvaluationPublishLinkLocator(int rowID) { return By.id("publishEvaluation" + rowID); }
	public By getCoordEvaluationUnpublishLinkLocator(int rowID) { return By.id("unpublishEvaluation" + rowID); }
	
	// Evaluation table at homepage
	/**
	 * Finds the rowID (at evaluation page) number of a specific evaluation based on
	 * the course ID and evaluation name 
	 * @param course
	 * @param evalName
	 * @return
	 */
	public int getCoordHomeEvaluationRowID(String courseID, String evalName) {
		int courseRowID = getCoordHomeCourseRowID(courseID);
		if(courseRowID==-1) return -2;
		String template = "//div[@id='course%d']//table[@id='dataform']//tr[@id='evaluation%d']";
		int max = (Integer)selenium.getXpathCount("//div//table[@id='dataform']//tr");
		for(int id=0; id<max; id++){
			if(getElementText(By.xpath(String.format(template+"//td[1]",courseRowID,id))).equals(evalName)){
				return id;
			}
		}
		return -1;
	}
	public By getCoordHomeEvaluationViewResultsLinkLocator(String courseID, String evalName) { return By.id("viewEvaluation" + getCoordHomeEvaluationRowID(courseID,evalName)); }
	public By getCoordHomeEvaluationEditLinkLocator(String courseID, String evalName) { return By.id("editEvaluation" + getCoordHomeEvaluationRowID(courseID,evalName)); }
	public By getCoordHomeEvaluationDeleteLinkLocator(String courseID, String evalName) { return By.id("deleteEvaluation" + getCoordHomeEvaluationRowID(courseID,evalName)); }
	public By getCoordHomeEvaluationRemindLinkLocator(String courseID, String evalName) { return By.id("remindEvaluation" + getCoordHomeEvaluationRowID(courseID,evalName)); }
	public By getCoordHomeEvaluationPublishLinkLocator(String courseID, String evalName) { return By.id("publishEvaluation" + getCoordHomeEvaluationRowID(courseID,evalName)); }
	public By getCoordHomeEvaluationUnpublishLinkLocator(String courseID, String evalName) { return By.id("publishEvaluation" + getCoordHomeEvaluationRowID(courseID,evalName)); }
	
	/* -------------------------------- Course Page ------------------------------- */
	// Add course
	public By coordCourseInputCourseID = By.id("courseid");
	public By coordCourseInputCourseName = By.id("coursename");
	public By coordCourseAddButton = By.id("btnAddCourse");
	public By coordCourseSortByIdButton = By.id("button_sortcourseid");
	public By coordCourseSortByNameButton = By.id("button_sortcoursename");

	// ------------------------------- Courses Table ----------------------------- //
	/*
	 * In each row, the Cell containing course id has an HTML id attribute
	 * e.g. <tr><td id="courseID1">CS2103-TESTING</td>...</tr>
	 * The value depends on the original row number (it may be changed after sorting)
	 * Originally it is 1st row: "CourseID0", 2nd row: "CourseID1" and so on
	 * So at any time the lowest course ID will always have rowID 0, and so on. 
	 */
	public By getCourseIDCell(int rowID) { return By.id("courseID" + rowID); }
	public By getCourseNameCell(int rowID) { return By.id("courseName" + rowID); }

	// Enrollment
	public By coordEnrollInfo = By.id("information");
	public By coordEnrollButton = By.id("button_enroll");
	public By coordEnrollBackButton = By.className("t_back");
	
	// Enrollment results
	public By coordStudentsAdded = By.id("t_studentsAdded");
	public By coordStudentsEdited = By.id("t_studentsEdited");
	public By coordStudentsAddedRow = By.xpath("//tr[@id='rowAddedStudents']//td");
	public By coordStudentsEditedRow = By.xpath("//tr[@id='rowEditedStudents']//td");
	
	// Course details
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

	// Student details
	public By studentDetailName = By.xpath("//table[@class='detailform']//tbody//tr[1]//td[2]");
	public By studentDetailTeam = By.xpath("//table[@class='detailform']//tbody//tr[2]//td[2]");
	public By studentDetailEmail = By.xpath("//table[@class='detailform']//tbody//tr[3]//td[2]");
	public By studentDetailGoogle = By.xpath("//table[@class='detailform']//tbody//tr[4]//td[2]");
	public By studentDetailComment = By.xpath("//table[@class='detailform']//tbody//tr[6]//td[2]");
	public By studentDetailKey = By.id("t_courseKey");
	public By studentDetailBackButton = By.className("t_back");

	// Edit student
	public By studentEditName = By.id("editname");
	public By studentEditTeam = By.id("editteamname");
	public By studentEditEmail = By.id("editemail");
	public By studentEditID = By.id("editgoogleid");
	public By studentEditComments = By.id("editcomments");

	public By studentEditSaveButton = By.id("button_editstudent");
	
	// Team forming
	public final String TEAMFORMINGSESSION_STATUS_AWAITING = "AWAITING";
	public By inputTeamName = By.id("teamName");
	public By inputTeamProfile = By.id("teamProfile");        
	public By inputNewTeamName = By.id("newteamName");
	public By inputStudentProfileDetail = By.id("studentprofiledetail");
	public By createTeamFormingSessionButton = By.id("t_btnCreateTeamFormingSession");
	public By editTeamFormingSessionButton = By.id("button_editteamformingsession");

	// Evaluation default
	public final String EVAL_STATUS_AWAITING = "AWAITING";
	public final String EVAL_STATUS_PUBLISHED = "PUBLISHED";
	public final String EVAL_STATUS_CLOSED = "CLOSED";

	public By inputEvaluationName = By.id("evaluationname");
	public By inputPeerFeedbackStatus = By.id("commentsstatus");
	
	public By inputInstruction = By.id("instr");
	public By inputOpeningDate = By.id("start");
	public By inputOpeningTime = By.id("starttime");
	public By inputClosingDate = By.id("deadline");
	public By inputClosingTime = By.id("deadlinetime");
	
	public By inputTimeZone = By.id("timezone");
	public By inputGracePeriod = By.id("graceperiod");
	public By inputProfileTemplate = By.id("profile_template");

	// Edit team profile
	public By coordEditTeamProfile0 = By.id("viewTeamProfile0");
	public By saveTeamProfile = By.id("button_saveTeamProfile");
	public By saveChangeStudentTeam = By.id("button_saveTeamChange");
	public By saveChangeNewStudentTeam = By.id("teamchange_newteam");
	public By saveStudentProfile = By.id("button_savestudentprofile");

	// Edit student team
	public By coordChangeStudentTeam11 = By.id("changeStudentTeam-1/1");
	public By coordAllocateStudentTeam1 = By.id("allocateStudentTeam1");
	public By addEvaluationButton = By.id("t_btnAddEvaluation");

	public By evaluationCourseIDSorting = By.id("button_sortcourseid");
	public By evaluationNameSorting = By.id("button_sortname");

	// Edit evaluation
	public By editEvaluationButton = By.id("button_editevaluation");
	public By editEvaluationBackButton = By.className("t_back");

	// Evaluation Result
	public By resultSummaryRadio = By.id("radio_summary");
	public By resultDetailRadio = By.id("radio_detail");
	public By resultReviewerRadio = By.id("radio_reviewer");
	public By resultRevieweeRadio = By.id("radio_reviewee");

	public By resultPublishButton = By.id("button_publish");
	public By resultBackButton = By.id("button_back");

	// Summary result
	public By resultTeamSorting = By.id("button_sortteamname");
	public By resultStudentSorting = By.id("button_sortname");
	public By resultSubmittedSorting = By.id("button_sortsubmitted");
	public By resultClaimedSorting = By.id("button_sortaverage");
	public By resultDifferenceSorting = By.id("button_sortdiff");
	public By resultEditButton = By.id("button_editevaluationresultsbyreviewee");
	public By resultEditCancelButton = By.id("button_back");

	// Individual result
	public By resultNextButton = By.id("button_next");
	public By resultPreviousButton = By.id("button_previous");
	public By resultIndividualEditButton = By.id("button_edit");

	private final String COORD_EVALUATION_RESULT_TABLE = "//div[@id='coordinatorEvaluationSummaryTable']//table[@id='result_table']//th[%d]";
	public By pointReviewerIndividualClaimed = By.xpath(String.format(COORD_EVALUATION_RESULT_TABLE,2));
	public By pointReviewerIndividualPerceived = By.xpath(String.format(COORD_EVALUATION_RESULT_TABLE,3));
	public By pointRevieweeIndividualClaimed = By.xpath(String.format(COORD_EVALUATION_RESULT_TABLE,2));
	public By pointRevieweeIndividualPerceived = By.xpath(String.format(COORD_EVALUATION_RESULT_TABLE,3));

	// Detailed result
	public By resultTopButton = By.id("button_top");

	// Edit result
	public By coordEvaluationSubmitButton = By.id("button_editevaluationresultsbyreviewee");
	
	// ---------------------------------- Student --------------------------------- //
	// Course details
	public By studentCourseDetailCourseID = By.xpath(String.format(DETAIL_FORM_TABLE_CELL, 1, 2));
	public By studentCourseDetailTeamName = By.xpath(String.format(DETAIL_FORM_TABLE_CELL, 2, 2));
	public By studentCourseDetailCourseName = By.xpath(String.format(DETAIL_FORM_TABLE_CELL, 3, 2));
	public By studentCourseDetailStudentName = By.xpath(String.format(DETAIL_FORM_TABLE_CELL, 4, 2));
	public By studentCourseDetailCoordinatorName = By.xpath(String.format(DETAIL_FORM_TABLE_CELL, 5, 2));
	public By studentCourseDetailStudentEmail = By.xpath(String.format(DETAIL_FORM_TABLE_CELL, 6, 2));
	public By studentCourseDetailStudentTeammates = By.xpath(String.format(DETAIL_FORM_TABLE_CELL, 7, 2));
	
	// Student course
	public By studentInputRegKey = By.id("regkey");
	public By studentJoinCourseButton = By.id("btnJoinCourse");
	public By studentJoinNewCourseLink = By.id("joinNewCourse");

	// Student evaluation:
	public By studentSubmitEvaluationButton = By.name("submitEvaluation");
	public By studentEvaluationBackButton = By.className("t_back");

	// --------------------------------- Homepage --------------------------------- //
	// Course box
	public By getStudentViewLink(int rowID) { return By.className("t_course_view" + rowID); }

	// Evaluation table:
	
	// ------------------------------- Evaluation --------------------------------- //
	public By getStudentDoEvaluationLink(int rowID) { return By.id("doEvaluation" + rowID); }
	public By getStudentViewResultsLink(int rowID) { return By.id("viewEvaluation" + rowID); }
	public By getStudentEditEvaluationSubmissionLink(int rowID) { return By.id("editEvaluation" + rowID); }
	public By getStudentEvaluationViewResultsLink(int rowID) { return By.xpath(String.format("//div[@id='studentPastEvaluations']//table[@id='dataform']//tr[%d]//td[%d]//a['View Results']", rowID + 2, 5)); }
	
	public final String PENDING_EVALUATIONS_HEADER = "Pending Evaluations:";
	public final String PAST_EVALUATIONS_HEADER = "Past Evaluations:";
	
	// Edit evaluation submission
	public By studentEvaluationCourseID = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 1, 2));
	public By studentEvaluationEvaluationName = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 2, 2));
	public By studentEvaluationOpeningTime = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 3, 2));
	public By studentEvaluationClosingTime = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 4, 2));
	public By studentEvaluationInstructions = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 5, 2));
	
	// Evaluation results table
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
	
	// --------------------------------- Messages --------------------------------- //
	public By courseMessage = By.xpath("//div[@id='statusMessage']/font[1]");
	public By courseErrorMessage = By.xpath("//div[@id='statusMessage']/font[2]");
	public By statusMessage = By.id("statusMessage");
	public By editEvaluationResultsStatusMessage = By.id("coordinatorEditEvaluationResultsStatusMessage");

	public By footer = By.id("contentFooter");

	public final static String MESSAGE_COURSE_DELETED = "The course has been deleted.";
	public final static String MESSAGE_COURSE_DELETED_STUDENT = "The student has been removed from the course.";
	public final static String MESSAGE_COURSE_DELETED_ALLSTUDENTS = "All students have been removed from the course. Click here to enroll students.";

	public final static String ERROR_COURSE_LONG_COURSE_NAME = "Course name should not exceed 38 characters.";
	public final String MESSAGE_ENROLL_REMIND_TO_JOIN = "Emails have been sent to unregistered students.";
	public final String ERROR_MESSAGE_ENROLL_INVALID_EMAIL = "E-mail address should contain less than 40 characters and be of a valid syntax.";
	
	// Team forming session
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
	
	// Evaluations
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

	// Student
	public final String ERROR_STUDENT_JOIN_COURSE = "Registration key is invalid.";
	public final String MESSAGE_STUDENT_JOIN_COURSE = "You have successfully joined the course.";

	public final String FOOTER = "Best Viewed In Firefox, Chrome, Safari and Internet Explorer 8+. For Enquires:";

	/**
	 * Loads the TEAMMATES homepage into the browser
	 */
	public void init(){
		goToUrl(Config.inst().TEAMMATES_URL);
	}
	
	/*------------------------------------------------------------------------
	 * UI Actions (login and logout)
	 * ---------------------------------------------------------------------- */
	
	/**
	 * Logs in as coordinator.
	 * Verifies that the new page is loaded correctly before returning.
	 * @page Homepage
	 */
	public void loginCoord(String username, String password) {
		System.out.println("Logging in coordinator " + username + ".");

		// Click the Coordinator button on the main page
		clickWithWait(COORD_LOGIN_BUTTON);
		waitForPageLoad();

		/*
		 * IE Fix
		 * For some reason in IE new profile is not created, thus user is already logged in. This will log user out.
		 */
		if (isElementPresent(logoutTab)) {
			driver.findElement(logoutTab).click();
			waitForPageLoad();

			// Check that we're at the main page after logging out
			verifyMainPage();

			click(COORD_LOGIN_BUTTON);
			waitForPageLoad();
		}

		login(username, password);

		verifyCoordHomePage();
	}

	/**
	 * Logs in as student.
	 * Waits until the new page is fully loaded before returning.
	 * @page Homepage
	 */
	public void studentLogin(String username, String password) {
		System.out.println("Logging in student " + username + ".");
		
		// Click the Student button on the main page
		clickWithWait(STUDENT_LOGIN_BUTTON);
		waitForPageLoad();
		
		/*
		 * IE Fix
		 * For some reason in IE new profile is not created, thus user is already logged in. This will log user out.
		 */
		if (isElementPresent(logoutTab)) {
			driver.findElement(logoutTab).click();
			waitForPageLoad();
			
			// Check that we're at the main page after logging out
			verifyMainPage();
			click(STUDENT_LOGIN_BUTTON);
			waitForPageLoad();
		}
		
		login(username, password);
		
		verifyStudentHomePage();
	}
	
	private void login(String email, String password) {
		waitForPageLoad();
		if (isLocalLoginPage()) {
			fillString(By.id("email"), email);
			selenium.click("css=input[value='Log In']");
			checkGoogleApplicationApproval();
			waitForPageLoad();
		} else if (isGoogleLoginPage()) {
			// Fill in login credentials
			fillString(By.id("Email"), email);
			fillString(By.id("Passwd"), password);
			// Click sign in button
			click(By.id("signIn"));
			// Wait and check for the main Coordinator page to see
			// if login was successful
			checkGoogleApplicationApproval();
			waitForPageLoad();
	
		} else {
			fail("Not in the correct Login page");
			return;
		}
	}
	/**
	 * When authenticating for the first few times,
	 * it might ask for the "grant permission" page.
	 * If that's the case we simply click "Grant"
	 */
	private void checkGoogleApplicationApproval() {
		if (isElementPresent(By.id("approve_button"))) {
			clickWithWait(By.id("persist_checkbox"));
			clickWithWait(By.id("approve_button"));
		}
	}
	/**
	 * Logs out (both for coordinator and student)
	 * Will return immediately if logout button cannot be found at current page
	 */
	public void logout() {
		System.out.println("Signing out.");
		if(isElementPresent(logoutTab)){
			clickWithWait(logoutTab);
		}
		
		if (Config.inst().isLocalHost()) {
			selenium.open(Config.inst().TEAMMATES_URL);
		}
	}

	/*------------------------------------------------------------------------
	 * UI Actions (click)
	 * ---------------------------------------------------------------------- */

	/**
	 * WebDriver clicks on an element.
	 * Fails on non-existence of element.
	 * See {@link #clickWithWait(By)} for version that waits until timeout before failing
	 * @param by
	 */
	public void click(By by) {
		if (isElementPresent(by)) {
			driver.findElement(by).click();
		} else {
			fail("Element " + by.toString() + " does not exists.");
		}
	}
	
	/**
	 * WebDriver clicks on an element.
	 * Wait for the element to exist or timeout.
	 * @param by
	 */
	public void clickWithWait(By by) {
		waitForElementPresent(by);
		driver.findElement(by).click();
	}
	
	/**
	 * To be used for clicks on a link that opens a new window.
	 * Switch to the title of the window as specified.
	 * @param link
	 * @param window
	 */
	public void clickAndSwitchToNewWindow(By link, String window) {
		clickWithWait(link);
	
		selenium.selectWindow(window);
		selenium.windowFocus();
	}
	
	/* ------------------------- Navigational Clicks ------------------------ */
	/**
	 * Clicks Home Tab
	 */
	public void clickHomeTab() {
		clickWithWait(homeTab);
	}

	/**
	 * Clicks Courses Tab
	 */
	public void clickCourseTab() {
		clickWithWait(courseTab);
	}

	/**
	 * Clicks Evaluations Tab
	 */
	public void clickEvaluationTab() {
		clickWithWait(evaluationTab);
	}

	/**
	 * Clicks Team Forming Tab
	 */
	public void clickTeamFormingTab() {
		clickWithWait(teamFormingTab);
	}
	
	/* -------------------- Clicks followed by confirmation ------------------ */
	/**
	 * Confirm a dialog box, i.e., clicking Yes, by overriding the alert box.
	 * So, no alert box will appear, it will directly assumed as Yes.
	 * This works for Chrome and Firefox.
	 * <br />
	 * Consequently, this method needs to be called before the click.
	 * The delete(window.confirm) ensures that this overriding happens only once.
	 * <br />
	 * Does not wait for any further action to complete (i.e., returns immediately after confirming)
	 * 
	 */
	public void clickAndConfirm(By by) throws NoAlertAppearException{
		/*
		 * Huy: I have no idea why the driver.switchTo().alert() approach doesn't work even in Firefox (it supposed to!).
		 * This is a workaround to press Yes in the confirmation box. Same for function below for No.
		 * 
		 * Aldrian: I tried driver.switchTo().alert() approach in my local Firefox and it worked.
		 * But for more general usability I removed the old one and use this one instead.
		 */

		//if (Config.inst().BROWSER.equals("chrome")) {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.confirm = function(msg){ delete(window.confirm); return true;};");
			clickWithWait(by);
			
			if((Boolean)js.executeScript("return eval(window.confirm).toString()==eval(function(msg){ delete(window.confirm); return true;}).toString()")){
				// This means the click does not generate alert box
				throw new NoAlertAppearException(by.toString());
			}
			// Make sure it's deleted. Deleting twice does not hurt
			js.executeScript("delete(window.confirm)");
		//}
	}

	/**
	 * Cancels a dialog box, i.e., clicking No, by overriding the alert box.
	 * So, no alert box will appear, it will directly assumed as No.
	 * This works for Chrome and Firefox.
	 * <br />
	 * Consequently, this method needs to be called before the click.
	 * The delete(window.confirm) ensures that this overriding happens only once.
	 * <br />
	 * Does not wait for any further action to complete (i.e., returns immediately after cancelling)
	 * 
	 */
	public void clickAndCancel(By by) throws NoAlertAppearException{
		//if (Config.inst().BROWSER.equals("chrome")) {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.confirm = function(msg){ delete(window.confirm); return false;};");
			clickWithWait(by);
			
			if((Boolean)js.executeScript("return eval(window.confirm).toString()==eval(function(msg){ delete(window.confirm); return false;}).toString()")){
				// This means the click does not generate alert box
				throw new NoAlertAppearException(by.toString());
			}
			// Make sure it's deleted. Deleting twice does not hurt
			js.executeScript("delete(window.confirm)");
		//}
	}

	/**
	 * Click and confirm (Yes)
	 * @deprecated Reported to not work on Firefox on some instances. Use more general one {{@link #clickAndConfirm(By)}
	 */
	@SuppressWarnings("unused")
	private void confirmYes() {
		if (!Config.inst().BROWSER.equals("chrome")) { Alert alert = driver.switchTo().alert(); alert.accept(); }
	}

	/**
	 * Click and cancel (No)
	 * @deprecated Reported to not work on Firefox on some instances. Use more general one {{@link #clickAndConfirm(By)}
	 */
	@SuppressWarnings("unused")
	private void confirmNo() {
		if (!Config.inst().BROWSER.equals("chrome")) { Alert alert = driver.switchTo().alert(); alert.dismiss(); }
	}
	
	// The old methods
//	public void waitAndClickAndConfirm(By by){
//		chromeConfirmYes();
//		waitAndClick(by);
//		confirmYes();
//	}

//	public void waitAndClickAndCancel(By by){
//		chromeConfirmNo();
//		waitAndClick(by);
//		confirmNo();
//	}

	/**
	 * Clicks and confirms Delete of a course at a particular rowID.
	 * Pre-condition: Should be at Coordinator Homepage
	 * @param rowID
	 */
	public void clickCoordHomeCourseDeleteAndConfirm(int rowID) {
		clickAndConfirm(getCoordHomeCourseDeleteLinkLocator(rowID));
	}
	
	/**
	 * Click and cancels Delete of a particular course of the coordinator.
	 * Pre-condition: Should be at Coordinator Homepage
	 * @param rowID
	 */
	public void clickCoordHomeCourseDeleteAndCancel(String courseID) {
		clickAndCancel(getCoordHomeCourseDeleteLinkLocator(courseID));
	}

	/**
	 * Clicks and confirms Delete of a course at a particular rowID.
	 * Pre-condition: Should be at Coordinator Course Page
	 * @param rowID
	 */
	public void clickCoordCourseDeleteAndConfirm(int rowID) {
		clickAndConfirm(getCoordCourseDeleteLinkLocator(rowID));
	}

	/**
	 * Clicks and confirms Delete of a particular course.
	 * Pre-condition: Should be at Coordinator Course Page
	 * @param courseID
	 */
	public void clickCoordCourseDeleteAndConfirm(String courseID) {
		int rowID = getCourseRowID(courseID);
		if (rowID > -1) {
			clickCoordCourseDeleteAndConfirm(rowID);
		} else {
			fail("Course ID cannot be found.");
		}
	}
	
	/**
	 * Click and cancels Delete of a particular course of the coordinator.
	 * Pre-condition: Should be at Coordinator Course Page
	 * @param rowID
	 */
	public void clickCoordCourseDeleteAndCancel(int rowID) {
		clickAndCancel(getCoordCourseDeleteLinkLocator(rowID));
	}

	/**
	 * Clicks and cancels Delete of a particular course.
	 * Pre-condition: Should be at Coordinator Course Page
	 * @param courseID
	 */
	public void clickCoordCourseDeleteAndCancel(String courseID) {
		int rowID = getCourseRowID(courseID);
		if (rowID > -1) {
			clickCoordCourseDeleteAndCancel(rowID);
		} else {
			fail("Course ID cannot be found.");
		}
	}

	/**
	 * Clicks and confirms Delete a student at a particular rowID.
	 * Pre-condition: Should be in Course detail page
	 * @param rowID
	 */
	public void clickCoordCourseDetailStudentDeleteAndConfirm(int rowID) {
		clickAndConfirm(By.xpath(String.format("//table[@id='dataform']//tr[%d]//a[@class='t_student_delete']", rowID + 2)));
	}
	
	/**
	 * Clicks and confirms Delete a particular student.
	 * Pre-condition: Should be in Course detail page
	 * @param student
	 */
	public void clickCoordCourseDetailStudentDeleteAndConfirm(String student) {
		int rowID = findStudentRow(student);
		if (rowID > -1) {
			clickCoordCourseDetailStudentDeleteAndConfirm(rowID);
		} else {
			fail("Student not found in this course.");
		}
	}
	
	/**
	 * Clicks and confirms Delete of an evaluation at a particular rowID.
	 * Pre-condition: Should be at Evaluation list page
	 * @param rowID
	 */
	public void clickCoordEvaluationDeleteAndConfirm(int rowID) {
		clickAndConfirm(getCoordEvaluationDeleteLinkLocator(rowID));
	}

	/**
	 * Clicks and confirms Delete of a particular evaluation.
	 * Pre-condition: Should be at Evaluation list page
	 * @param courseId
	 * @param evalName
	 */
	public void clickCoordEvaluationDeleteAndConfirm(String courseId, String evalName) {
		int rowID = getEvaluationRowID(courseId, evalName);
		if (rowID > -1) {
			clickCoordEvaluationDeleteAndConfirm(rowID);
		} else {
			fail("Evaluation not found.");
		}
	}

	/**
	 * Clicks and cancels Delete of an evaluation at a particular rowID.
	 * Pre-condition: Should be at Evaluation list page
	 * @param rowID
	 */
	public void clickCoordEvaluationDeleteAndCancel(int rowID) {
		clickAndCancel(getCoordEvaluationDeleteLinkLocator(rowID));
	}

	/**
	 * Clicks and cancels Delete of a particular evaluation.
	 * Pre-condition: Should be at Evaluation list page
	 * @param courseId
	 * @param evalName
	 */
	public void clickCoordEvaluationDeleteAndCancel(String courseId, String evalName) {
		int rowID = getEvaluationRowID(courseId, evalName);
		if (rowID > -1) {
			clickCoordEvaluationDeleteAndCancel(rowID);
		} else {
			fail("Evaluation not found.");
		}
	}

	/**
	 * Clicks and confirms on Publish button of an evaluation 
	 * at particular rowID at evaluation page as coordinator.
	 * Pre-condition: Should be at Evaluation list page
	 * @param rowID
	 */
	public void clickCoordEvaluationPublishAndConfirm(int rowID) {
		clickAndConfirm(getCoordEvaluationPublishLinkLocator(rowID));
	}
	
	/**
	 * Clicks and confirms on Publish button of a particular 
	 * evaluation at evaluation page as coordinator.
	 * Pre-condition: Should be at Evaluation list page
	 * @param courseId
	 * @param evalName
	 */
	public void clickCoordEvaluationPublishAndConfirm(String courseId, String evalName) {
		int rowID = getEvaluationRowID(courseId, evalName);
		if (rowID > -1) {
			clickCoordEvaluationPublishAndConfirm(rowID);
		} else {
			fail("Evaluation not found.");
		}
	}
	
	/**
	 * Clicks and cancels Publish of results of an evaluation
	 * at particular rowID in the page as the coordinator.
	 * Pre-condition: Should be at Evaluation list page
	 * @param rowID
	 */
	public void clickCoordEvaluationPublishAndCancel(int rowID) {
		clickAndCancel(getCoordEvaluationPublishLinkLocator(rowID));
	}
	
	/**
	 * Clicks and cancels Publish of results of a particular evaluation
	 * in a specific course of the coordinator.
	 * Pre-condition: Should be at Evaluation list page
	 * @param courseID
	 * @param evalName
	 */
	public void clickCoordEvaluationPublishAndCancel(String courseID, String evalName) {
		int rowID = getEvaluationRowID(courseID, evalName);
	
		if (rowID > -1) {
			clickCoordEvaluationPublishAndCancel(rowID);
		} else {
			fail("Evaluation not found.");
		}
	}
	
	/**
	 * Clicks and confirms on Unpublish button of an evaluation
	 * at particular rowID.
	 * Pre-condition: Should be at Evaluation list page
	 * @param rowID
	 */
	public void clickCoordEvaluationUnpublishAndConfirm(int rowID) {
		clickAndConfirm(getCoordEvaluationUnpublishLinkLocator(rowID));
	}
	
	/**
	 * Clicks and confirms on Unpublish button of a particular evaluation.
	 * Pre-condition: Should be at Evaluation list page
	 * @param courseId
	 * @param evalName
	 */
	public void clickCoordEvaluationUnpublishAndConfirm(String courseId, String evalName) {
		int rowID = getEvaluationRowID(courseId, evalName);
		if (rowID > -1) {
			clickCoordEvaluationUnpublishAndConfirm(rowID);
		} else {
			fail("Evaluation not found.");
		}
	}
	
	/**
	 * Clicks and cancels Unpublish of results of an evaluation
	 * at a particular rowID in a specific course of the coordinator.
	 * Pre-condition: Should be at Evaluation list page
	 * @param rowID
	 */
	public void clickCoordEvaluationUnpublishAndCancel(int rowID) {
		clickAndConfirm(getCoordEvaluationPublishLinkLocator(rowID));
	}

	/**
	 * Clicks and cancels Unpublish of results of a particular evaluation
	 * in a specific course of the coordinator.
	 * Pre-condition: Should be at Evaluation list page
	 * @param courseID
	 * @param evalName
	 */
	public void clickCoordEvaluationUnpublishAndCancel(String courseID, String evalName) {
		int rowID = getEvaluationRowID(courseID, evalName);
	
		if (rowID > -1) {
			clickCoordEvaluationUnpublishAndCancel(rowID);
		} else {
			fail("Evaluation not found.");
		}
	}
	
	public void clickCoordEvaluationRemindAndConfirm(int rowID) {
		clickAndConfirm(getCoordEvaluationRemindLinkLocator(rowID));
	}
	
	public void clickCoordEvaluationRemindAndConfirm(String courseId, String evalName) {
		int rowID = getEvaluationRowID(courseId, evalName);
		if (rowID > -1) {
			clickCoordEvaluationRemindAndConfirm(rowID);
		} else {
			fail("Evaluation not found.");
		}
	}
	
	public void clickCoordEvaluationRemindAndCancel(int rowID) {
		clickAndCancel(getCoordEvaluationRemindLinkLocator(rowID));
	}
	
	public void clickCoordEvaluationRemindAndCancel(String courseId, String evalName) {
		int rowID = getEvaluationRowID(courseId, evalName);
		if (rowID > -1) {
			clickCoordEvaluationRemindAndCancel(rowID);
		} else {
			fail("Evaluation not found.");
		}
	}
	
	/**
	 * Clicks on the enroll link on a specific rowID.
	 * Does not verify the new page.
	 * @param rowID
	 */
	public void clickCoordCourseEnroll(int rowID) {
		clickWithWait(getCoordCourseEnrollLinkLocator(rowID));
	}
	/**
	 * Clicks on the enroll link of a specific course.
	 * Does not verify the new page.
	 * @param courseID
	 */
	public void clickCoordCourseEnroll(String courseID) {
		int rowID = getCourseRowID(courseID);
		if (rowID > -1) {
			clickCoordCourseEnroll(rowID);
		} else {
			fail("Course ID cannot be found");
		}
	}
	
	public void clickCoordCourseView(int rowID) {
		waitForElementPresent(By.id("dataform"));
		clickWithWait(getCoordCourseViewLinkLocator(rowID));
	}
	
	public void clickCoordCourseView(String courseID) {
		int rowID = getCourseRowID(courseID);
		if (rowID > -1) {
			clickCoordCourseView(rowID);
		} else {
			fail("Course ID cannot be found.");
		}
	}
	
	public void clickCoordCourseDetailStudentView(int rowID) {
		clickWithWait(By.xpath(String.format("//table[@id='dataform']//tr[%d]//td[4]//a[@class='t_student_view']", rowID + 2)));
	}
	
	public void clickCoordCourseDetailStudentView(String student) {
		int rowID = findStudentRow(student);
		if (rowID > -1) {
			clickCoordCourseDetailStudentView(rowID);
		} else {
			fail("Student not found in this course.");
		}
	}
	
	public void clickCoordCourseDetailStudentEdit(int rowID) {
		clickWithWait(By.xpath(String.format("//table[@id='dataform']//tr[%d]//td[4]//a[@class='t_student_edit']", rowID + 2)));
	}
	
	public void clickCoordCourseDetailStudentEdit(String student) {
		int rowID = findStudentRow(student);
		if (rowID > -1) {
			clickCoordCourseDetailStudentEdit(rowID);
		} else {
			fail("Student not found in this course.");
		}
	}
	
	public void clickCoordCourseDetailInvite(int rowID) {
		By link = By.xpath(String.format("//table[@id='dataform']//tr[%d]//td[%d]//a[@class='t_student_resend']", rowID + 2, 4));
		clickWithWait(link);
	}
	
	public void clickCoordCourseDetailInvite(String student) {
		int rowID = findStudentRow(student);
		if (rowID > -1) {
			clickCoordCourseDetailInvite(rowID);
		} else {
			fail("Student not found in this course.");
		}
	}
	
	public void clickCoordEvaluationViewResults(int rowID) {
		clickWithWait(getCoordEvaluationViewResultsLinkLocator(rowID));
	}
	
	public void clickCoordEvaluationViewResults(String courseId, String evalName) {
		int rowID = getEvaluationRowID(courseId, evalName);
	
		if (rowID > -1) {
			clickCoordEvaluationViewResults(rowID);
		} else {
			fail("Evaluation not found.");
		}
	}
	
	/* -------------------- Clicks without confirmation --------------------- */
	public void clickCoordEvaluationEdit(int rowID) {
		clickWithWait(getCoordEvaluationEditLinkLocator(rowID));
	}
	
	public void clickCoordEvaluationEdit(String courseId, String evalName) {
		int rowID = getEvaluationRowID(courseId, evalName);
		if (rowID > -1) {
			clickCoordEvaluationEdit(rowID);
		} else {
			fail("Evaluation not found.");
		}
	}
	
	public void clickCoordTFSViewTeams(int rowID) {
		clickWithWait(By.id("viewTeams"+rowID));
	}
	
	public void clickCoordTFSViewTeams(String courseId) {
		int rowID = findTeamFormingSessionRow(courseId);
		if (rowID > -1) {
			clickCoordTFSViewTeams(rowID);
		} else {
			fail("Team forming session view teams not found.");
		}
	}
	
	public void clickCoordTFSViewLog(int rowID) {
		String elementID = "viewLogTeamFormingSession" + rowID;
		clickWithWait(By.id(elementID));
	}
	
	public void clickCoordTFSViewLog(String courseId) {
		int rowID = findTeamFormingSessionRow(courseId);
		if (rowID > -1) {
			clickCoordTFSViewLog(rowID);
		} else {
			fail("Team forming session view log not found.");
		}
	}
	
	public void clickCoordTFSEdit(int rowID) {
		String elementID = "manageTeamFormingSession" + rowID;
		clickWithWait(By.id(elementID));
	}
	
	public void clickCoordTFSEdit(String courseId) {
		int rowID = findTeamFormingSessionRow(courseId);
		if (rowID > -1) {
			clickCoordTFSEdit(rowID);
		} else {
			fail("Team forming session not found.");
		}
	}
	
	public void clickCoordTFSRemind(String courseId) {
		int rowID = findTeamFormingSessionRow(courseId);
		if (rowID > -1) {
			String elementID = "remindTeamFormingSession" + rowID;
			clickAndConfirm(By.id(elementID));
		}
	}
	
	public void clickCoordTFSDelete(String courseId) {
		int rowID = findTeamFormingSessionRow(courseId);
		if(rowID>-1){
			String elementID = "deleteTeamFormingSession" + rowID;
			clickAndConfirm(By.id(elementID));
		}
	}
	
	public void clickCoordReviewerSummaryView(int rowID) {
		clickWithWait(getReviewerSummaryView(rowID));
	}
	
	public void clickCoordReviewerSummaryEdit(int rowID) {
		clickWithWait(getReviewerSummaryEdit(rowID));
	}
	
	public void clickCoordRevieweeSummaryView(int rowID) {
		clickWithWait(By.id("viewEvaluationResults" + rowID));
	}
	
	/**
	 * Clicks the sort course by name button.
	 * Waits for the element to appear.
	 * Pre-condition: Should be at Course Page
	 */
	public void clickCoordCourseSortByNameButton(){
		clickWithWait(coordCourseSortByNameButton);
	}

	/**
	 * Clicks the sort course by ID button.
	 * Waits for the element to appear.
	 * Pre-condition: Should be at Course Page
	 */
	public void clickCoordCourseSortByIdButton(){
		clickWithWait(coordCourseSortByIdButton);
	}

	// --------------------------------- Students -------------------------------- //

	/**
	 * Returns the rowID number for a specific course ID as student.
	 * Waits until the element exists or timeout.
	 * Pre-condition: Should be at course page.
	 * @param courseId
	 * @return
	 */
	public int studentFindCourseRow(String courseId) {
		for (int i = 0; i < countCourses(); i++) {
			if (studentGetCourseID(i).equals(courseId)) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Returns the rowID number for a specific course ID and evaluation name.
	 * Waits until the element exists or timeout.
	 * Pre-condition: Should be at evaluation page.
	 * @param courseId
	 * @param evalName
	 * @return
	 */
	public int studentFindEvaluationRow(String courseId, String evalName) {
		waitForElementPresent(By.id("dataform"));
		for (int i = 0; i < studentCountTotalEvaluations(); i++) {
			By course = By.xpath(String.format("//div[@id='studentPastEvaluations']//table[@id='dataform']//tbody//tr[%d]//td[%d]", i + 2, 1));
			System.out.println(getElementText(course));
	
			By evaluation = By.xpath(String.format("//div[@id='studentPastEvaluations']//table[@id='dataform']//tbody//tr[%d]//td[%d]", i + 2, 2));
			System.out.println(getElementText(evaluation));
	
			if (getElementText(course).equals(courseId) && getElementText(evaluation).equals(evalName)) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Returns the first pending evaluation name.
	 * Waits until the element exists or timeout.
	 * Pre-condition: Should be at evaluation page.
	 * @param courseId
	 * @param evalName
	 * @return
	 */
	public int studentFindPendingEvaluationRow(String courseId, String evalName) {
		for (int i = 0; i < studentCountTotalPendingEvaluations(); i++) {
			By course = By.xpath(String.format("//div[@id='studentPendingEvaluations']//table[@id='dataform']//tbody//tr[%d]//td[%d]", i + 1, 1));
			By evaluation = By.xpath(String.format("//div[@id='studentPendingEvaluations']//table[@id='dataform']//tbody//tr[%d]//td[%d]", i + 1, 2));
			if (getElementText(course).equals(courseId) && getElementText(evaluation).equals(evalName)) {
				return i;
			}
		}
		return -1;
	}
	/**
	 * Returns courseID from the table at specific rowID as student.
	 * Waits until the element exists or timeout.
	 * Pre-condition: Should be at course page.
	 * @param rowID
	 * @return
	 */
	public String studentGetCourseID(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".0");
	}

	/**
	 * Returns course name from the table at specific rowID as student.
	 * Waits until the element exists or timeout.
	 * Pre-condition: Should be at course page.
	 * @param rowID
	 * @return
	 */
	public String studentGetCourseName(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".1");
	}

	/**
	 * Returns course name from the table for specific courseID as student.
	 * Waits until the element exists or timeout.
	 * Pre-condition: Should be at course page.
	 * @param courseId
	 * @return
	 */
	public String studentGetCourseName(String courseId) {
		int rowID = studentFindCourseRow(courseId);
		if (rowID > -1) {
			return studentGetCourseName(rowID);
		} else {
			fail("Student's course not found.");
			return null;
		}
	}

	/**
	 * Returns the team name for specific rowID as student.
	 * Waits until the element exists or timeout.
	 * Pre-condition: Should be at course page.
	 * @param rowID
	 * @return
	 */
	public String studentGetCourseTeamName(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".2");
	}
	
	/**
	 * Returns the team name for specific course ID as student.
	 * Waits until the element exists or timeout.
	 * Pre-condition: Should be at course page.
	 * @param courseId
	 * @return
	 */
	public String studentGetCourseTeamName(String courseId) {
		int rowID = studentFindCourseRow(courseId);
		if (rowID > -1) {
			return studentGetCourseTeamName(rowID);
		} else {
			fail("Student's course not found.");
			return null;
		}
	}

	/**
	 * Clicks on the view course for specific rowID as student.
	 * Waits until the element exists or timeout.
	 * Pre-condition: Should be at course page.
	 * @param rowID
	 */
	public void studentClickCourseView(int rowID) {
		By link = By.xpath(String.format("//div[@id='studentCourseTable']//table[@id='dataform']//tr[%d]//td[%d]//a[1]", rowID + 2, 4));
		clickWithWait(link);
	}

	/**
	 * Clicks on the view course for specific course ID as student.
	 * Waits until the element exists or timeout.
	 * Pre-condition: Should be at course page.
	 * @param courseId
	 */
	public void studentClickCourseView(String courseId) {
		int rowID = studentFindCourseRow(courseId);
		if (rowID > -1) {
			studentClickCourseView(rowID);
		} else {
			fail("Student's course not found. Can't click view course.");
		}
	}

	/**
	 * Clicks on doEvaluation link for evaluation at specific rowID.
	 * Does not wait for the new page to load.
	 * @param rowID
	 */
	public void studentClickEvaluationDo(int rowID) {
		clickWithWait(By.id("doEvaluation" + rowID));
	}
	/**
	 * Clicks on doEvaluation link for evaluation for specific course ID and course name.
	 * Does not wait for the new page to load.
	 * @param courseId
	 * @param evalName
	 */
	public void studentClickEvaluationDo(String courseId, String evalName) {
		int rowID = studentFindPendingEvaluationRow(courseId, evalName);
	
		if (rowID > -1) {
			studentClickEvaluationDo(rowID);
		} else {
			fail("Student's pending evaluation not found. Cannot do evaluation.");
		}
	}
	/**
	 * Clicks on editEvaluation link for evaluation at specific rowID.
	 * Does not verify that the new page is loaded correctly before returning.
	 * Pre-condition: Should be at evaluation page.
	 * @param rowID
	 */
	public void studentClickEvaluationEdit(int rowID) {
		clickWithWait(getStudentEditEvaluationSubmissionLink(rowID));
	}
	/**
	 * Clicks on editEvaluation link for evaluation for specific course ID and evaluation name.
	 * Verifies that the new page is loaded correctly before returning.
	 * Pre-condition: Should be at evaluation page.
	 * @param courseId
	 * @param evalName
	 */
	public void studentClickEvaluationEdit(String courseId, String evalName) {
		int rowID = studentFindEvaluationRow(courseId, evalName);
		if (rowID > -1) {
			studentClickEvaluationEdit(rowID);
		} else {
			fail("Student's evaluation not found. Cannot click edit evaluation.");
		}
	}
	/**
	 * Clicks on the view results link for evaluation at specific rowID.
	 * Does not verify that the new page is loaded correctly before returning.
	 * Pre-condition: Should be at evaluation page.
	 * @param rowID
	 */
	public void studentClickEvaluationViewResults(int rowID) {
		clickWithWait(getStudentEvaluationViewResultsLink(rowID));
	}
	/**
	 * Clicks on the view results link for evaluation for specific course ID and evaluation name.
	 * Verifies that the new page is loaded correctly before returning.
	 * Pre-condition: Should be at evaluation page.
	 * @param courseId
	 * @param evalName
	 */
	public void studentClickEvaluationViewResults(String courseId, String evalName) {
		int rowID = studentFindEvaluationRow(courseId, evalName);
		if (rowID > -1) {
			studentClickEvaluationViewResults(rowID);
		} else {
			fail("Student's evaluation not found.");
		}
	}
	/**
	 * Returns the locator of the first pending evaluation for current student.
	 * Waits until the element exists or timeout.
	 * Pre-condition: Should be at evaluation page.
	 * @param courseId
	 * @param evalName
	 * @return
	 * @throws NullPointerException
	 */
	public By studentGetPendingEvaluationName(String courseId, String evalName) throws NullPointerException {
		int rowID = studentFindPendingEvaluationRow(courseId, evalName);
		if (rowID > -1) {
			return By.xpath(String.format("//div[@id='studentPendingEvaluations']//table[@id='dataform']//tr[%d]//td[%d]", rowID + 1, 1));
		} else {
			fail("Student's pending evaluation not found.");
			return null;
		}
	}

	/**
	 * Returns the course ID of evaluation for specific rowID.
	 * Waits until the element exists or timeout.
	 * Pre-condition: Should be at evaluation page.
	 * @param rowID
	 * @return
	 */
	public String studentGetEvaluationCourseID(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".1");
	}

	/**
	 * Returns the evaluation name for specific rowID.
	 * Waits until the element exists or timeout.
	 * Pre-condition: Should be at evaluation page.
	 * @param rowID
	 * @return
	 */
	public String studentGetEvaluationName(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".2");
	}

	/**
	 * Returns the evaluation status for specific rowID.
	 * Waits until the element exists or timeout.
	 * Pre-condition: Should be at evaluation page.
	 * @param rowID
	 * @return
	 */
	public String studentGetEvaluationStatus(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".3");
	}

	/**
	 * Returns the evaluation status for specific course ID and evaluation name.
	 * Waits until the element exists or timeout.
	 * Pre-condition: Should be at evaluation page.
	 * @param courseId
	 * @param evalName
	 * @return
	 */
	public String studentGetEvaluationStatus(String courseId, String evalName) {
		int rowID = studentFindEvaluationRow(courseId, evalName);
		if (rowID > -1) {
			return studentGetEvaluationStatus(rowID);
		} else {
			fail("Student's evaluation not found.");
			return null;
		}
	}

	/**
	 * Returns the total number of pending evaluations for current student.
	 * Waits until the element exists or timeout.
	 * Pre-condition: Should be at evaluation page.
	 * @return
	 */
	public int studentCountTotalPendingEvaluations() {
		waitForElementPresent(By.id("dataform"));
		if (getElementText(By.xpath(String.format("//div[@id='studentPendingEvaluations']//table[@id='dataform']//tbody//tr[1]//td[1]"))).isEmpty()) {
			return 0;
		} else {
			return selenium.getXpathCount("//div[@id='studentPendingEvaluations']//table[@id='dataform']//tbody//tr").intValue();
	
		}
	}
	/**
	 * Returns the total number of courses that current student takes.
	 * Waits until the element exists or timeout.
	 * Pre-condition: Should be at course page.
	 * @return
	 */
	public int studentCountTotalCourses() {
		waitForElementPresent(By.id("dataform"));
		WebElement dataform = driver.findElement(By.id("dataform"));
		return dataform.findElements(By.tagName("tr")).size();
	}
	/**
	 * Returns the total number of past evaluations for current student.
	 * Waits for the element exists or timeout.
	 * Pre-condition: Should be at evaluation page.
	 * @return
	 */
	public int studentCountTotalEvaluations() {
		waitForElementPresent(By.id("dataform"));
		if (getElementText(By.xpath(String.format("//div[@id='studentPastEvaluations']//table[@id='dataform']//tbody//tr[2]//td[1]"))).isEmpty()) {
			return 0;
		} else {
			return selenium.getXpathCount("//div[@id='studentPastEvaluations']//table[@id='dataform']/tbody/tr").intValue() - 1;
		}
	}

	/**
	 * Returns the evaluation result claimed points as seen in the UI by student.
	 * Waits for the element exists or timeout.
	 * Pre-condition: Should be at evaluation page.
	 * @return
	 */
	public String studentGetEvaluationResultClaimedPoints() {
		waitForElementPresent(By.id("studentEvaluationResults"));
		return getElementText(By.xpath(String.format("//div[@id='studentEvaluationResults']//table[@class='result_studentform']//tr[%d]//td[%d]", 3, 2)));
	}

	/**
	 * Returns the evaluation result perceived points as seen in the UI by student.
	 * Waits for the element exists or timeout.
	 * Pre-condition: Should be at evaluation page.
	 * @return
	 */
	public String studentGetEvaluationResultPerceivedPoints() {
		waitForElementPresent(By.id("studentEvaluationResults"));
		return getElementText(By.xpath(String.format("//div[@id='studentEvaluationResults']//table[@class='result_studentform']//tr[%d]//td[%d]", 4, 2)));
	}

	/**
	 * Verifies whether a student get the feedback from another student.
	 * This is done by checking whether a specific text exists in the page.
	 * Does not wait for any element to present.
	 * @param fromStudent
	 * @param toStudent
	 * @return
	 */
	public boolean studentVerifyGetFeedbackFromOthers(String fromStudent, String toStudent) {
		return selenium.isTextPresent(String.format("This is a public comment from %s to %s", fromStudent, toStudent));
	}

	/**
	 * Converts a list of Student objects into string to be enrolled
	 * @param list
	 * @return
	 */
	private String enrollStudentsConvert(List<Student> list) {
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
	/**
	 * page: Enroll Student
	 * 
	 * @param rowID
	 */
	public void enrollStudents(List<Student> students, int rowID) {
		clickCoordCourseEnroll(rowID);
		verifyCoordCourseEnrollPage();
	
		fillString(coordEnrollInfo, enrollStudentsConvert(students));
		clickWithWait(coordEnrollButton);
	}
	public void enrollStudents(List<Student> students, String courseID) {
		clickCoordCourseEnroll(courseID);
		verifyCoordCourseEnrollPage();
	
		fillString(coordEnrollInfo, enrollStudentsConvert(students));
		clickWithWait(coordEnrollButton);
	}
	/**
	 * Adds a new course with specified courseID and coursename.
	 * Does not wait until the course is added (i.e., immediately returns after clicking "Add Course")
	 * Pre-condition: Should be at coordinator course page.
	 * @param courseid
	 * @param coursename
	 */
	public void addCourse(String courseid, String coursename) {
		fillString(coordCourseInputCourseID, courseid);
		fillString(coordCourseInputCourseName, coursename);
		clickWithWait(coordCourseAddButton);
	}
	
	/**
	 * Add an evaluation through the UI.
	 * Pre-condition: Should be at Evaluation add page
	 * @param courseID
	 * @param evalName
	 * @param dateValue
	 * @param nextTimeValue
	 * @param comments
	 * @param instructions
	 * @param gracePeriod
	 */
	public void addEvaluation(String courseID, String evalName, String dateValue, String nextTimeValue, String comments, String instructions, Integer gracePeriod) {
		clickEvaluationTab();
	
		// Select the course
		clickWithWait(coordCourseInputCourseID);
		
		selectDropdownByValue(coordCourseInputCourseID, courseID);
	
		// Fill in the evaluation name
		fillString(inputEvaluationName, evalName);
	
		// Select deadline date
		clickWithWait(inputClosingDate);
		selenium.waitForPopUp("window_deadline", "30000");
		selenium.selectWindow("name=window_deadline");
		clickWithWait(By.xpath("//a[contains(@href, '" + dateValue + "')]"));
		for (String s : driver.getWindowHandles()) {
			selenium.selectWindow(s);
			break;
		}
		selectDropdownByValue(inputClosingTime, nextTimeValue);
	
		// Allow P2P comment
		clickWithWait(By.xpath("//*[@id='commentsstatus'][@value='" + comments + "']"));
	
		// Fill in instructions
		fillString(inputInstruction, instructions);
	
		// Select grace period
		selectDropdownByValue(inputGracePeriod, Integer.toString(gracePeriod));
	
		// Submit the form
		clickWithWait(addEvaluationButton);
	}
	
	/**
	 * Adds an evaluation through the UI.
	 * Pre-condition: Should be at Evaluation page.
	 * @param eval
	 */
	public void addEvaluation(Evaluation eval) {
		addEvaluation(eval.courseID, eval.name, eval.dateValue, eval.nextTimeValue, eval.p2pcomments, eval.instructions, eval.gracePeriod);
	}
	
	public void addTeamFormingSession(String courseID, String dateValue, String nextTimeValue, Integer gracePeriod, String instructions, String profileTemplate) {
		clickTeamFormingTab();
	
		// Select the course
		clickWithWait(coordCourseInputCourseID);
		selectDropdownByValue(coordCourseInputCourseID, courseID);
	
		// Fill in instructions
		fillString(inputInstruction, instructions);
		// Fill in profile template
		fillString(inputProfileTemplate, profileTemplate);
	
		// Select deadline date
		clickWithWait(inputClosingDate);
		selenium.waitForPopUp("window_deadline", TIMEOUT+"");
		selenium.selectWindow("name=window_deadline");
		clickWithWait(By.xpath("//a[contains(@href, '" + dateValue + "')]"));
		for (String s : driver.getWindowHandles()) {
			selenium.selectWindow(s);
			break;
		}
		selectDropdownByValue(inputClosingTime, nextTimeValue);
		// Select grace period
		selectDropdownByValue(inputGracePeriod, Integer.toString(gracePeriod));
		
		// Submit the form
		clickWithWait(createTeamFormingSessionButton);
	}
	public void addTeamFormingSession(TeamFormingSession teamForming) {
		addTeamFormingSession(teamForming.courseID, teamForming.dateValue, teamForming.nextTimeValue, 
				teamForming.gracePeriod, teamForming.instructions, teamForming.profileTemplate);
	}
	/**
	 * Delete all available courses.
	 */
	public void deleteAllCourses(){
		while (driver.findElements(By.cssSelector("#coordinatorCourseTable tr")).size() > 1 && isElementPresent(By.className("t_course_delete"))) {
			System.out.println("Deleting a course...");
			clickAndConfirm(By.className("t_course_delete"));
			waitForTextInElement(statusMessage, MESSAGE_COURSE_DELETED);
			goToCourses();
		}
	}
	/**
	 * Delete all students
	 */
	public void deleteAllStudents() {
		System.out.println("delete all students");
		driver.findElement(By.className("t_courses")).click();
		clickWithWait(By.className("t_course_view"));
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
	/**
	 * Delete all evaluations
	 * 
	 * Must be in Evaluations page
	 */
	public void deleteAllEvaluations() {
	
		while (driver.findElements(By.className("t_eval_delete")).size() > 1) {
			System.out.println("Deleting 1 evaluation...");
			clickAndConfirm(By.className("t_eval_delete"));
			waitForTextInElement(statusMessage, "The evaluation has been deleted.");
			goToEvaluation(); // This is to fix for Datastore delay problem
		}
	}
	/**
	 * Returns the locator of a specific course by the course ID.
	 * Waits until the element exists or timeout.
	 * @param courseID
	 * @return
	 */
	public By getCourseIDCell(String courseID) {
		int rowID = getCourseRowID(courseID);
		if (rowID > -1) {
			return getCourseIDCell(rowID);
		} else {
			fail("Course not found.");
			return null;
		}
	}
	
	/**
	 * Reads through list of courses in the UI (i.e., in form of HTML table)
	 * and returns the course name for a specified course id.<br />
	 * Error if specified course id is not found.
	 * @param courseID
	 * @return
	 */
	public String getCourseName(String courseID) {
		int rowID = getCourseRowID(courseID);
		if (rowID > -1) {
			return getElementText(getCourseNameCell(rowID));
		} else {
			fail("Course " + courseID + " not found.");
			return null;
		}
	}
	
	/**
	 * Returns the number of courses with the specified courseID in the list
	 * @param courseID
	 * @return
	 */
	public int getCourseIDCount(String courseID){
		int result = 0;
		for (int i = 0; i < countCourses(); i++) {
			if (getElementText(getCourseIDCell(i)).equals(courseID)) {
				result++;
			}
		}
		return result;
	}

	/**
	 * Returns number of teams in specific rowID.
	 * Waits until element exists or timeout.
	 * Pre-condition: Should be in the course page.
	 * @param rowID
	 * @return
	 */
	public String getCourseNumberOfTeams(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;// rowID starts from 0
		return selenium.getTable("id=dataform." + rowID + ".2");
	}

	/**
	 * Returns number of teams in specific course.
	 * Waits until element exists or timeout.
	 * Pre-condition: Should be in the course page.
	 * @param courseID
	 * @return
	 */
	public String getCourseNumberOfTeams(String courseID) {
		int rowID = getCourseRowID(courseID);
		if (rowID > -1) {
			return getCourseNumberOfTeams(rowID);
		} else {
			fail("Course " + courseID + " not found.");
			return null;
		}
	}

	/**
	 * Returns total number of students in specific rowID.
	 * Waits until element exists or timeout.
	 * Pre-condition: Should be in the course page.
	 * @param rowID
	 * @return
	 */
	public String getCourseTotalStudents(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".3");
	}

	/**
	 * Returns total number of students in specific course.
	 * Waits until element exists or timeout.
	 * Pre-condition: Should be in the course page.
	 * @param courseID
	 * @return
	 */
	public String getCourseTotalStudents(String courseID) {
		int rowID = getCourseRowID(courseID);
		if (rowID > -1) {
			return getCourseTotalStudents(rowID);
		} else {
			fail("Course " + courseID + " not found.");
			return null;
		}
	}

	/**
	 * Returns total number of unregistered students in specific course.
	 * Waits until element exists or timeout.
	 * Pre-condition: Should be in the course page.
	 * @param rowID
	 * @return
	 */
	public String getCourseUnregisteredStudents(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".4");
	}

	/**
	 * Returns total number of unregistered students in specific course.
	 * Waits until element exists or timeout.
	 * Pre-condition: Should be in the course page.
	 * @param courseID
	 * @return
	 */
	public String getCourseUnregisteredStudents(String courseID) {
		int rowID = getCourseRowID(courseID);
		if (rowID > -1) {
			return getCourseUnregisteredStudents(rowID);
		} else {
			fail("Course " + courseID + " not found.");
			return null;
		}
	}
	
	public String getCourseDetailStudentName(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".0");
	}
	
	public String getCourseDetailTeamName(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".1");
	}
	
	public String getTeamFormingSessionCourseID(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".0");
	}
	
	public String getTeamFormingSessionStatus(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".2");
	}
	
	public By getStudentNameFromManageTeamFormingSession(int rowID, int col) {
		return By.xpath(String.format("//div[@class='result_team']//table[@id='dataform']//tbody//tr[%d]//td[%d]", rowID, col));		
	}

	/**
	 * Finds the rowID number of a student based on the name
	 * @param student
	 * @return
	 */
	public int findStudentRow(String student) {
		int i = 0;
		while (i < countCourseDetailStudents()) {
			if (getCourseDetailStudentName(i).equals(student)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	/**
	 * Finds the rowID number of a specific Team-forming session
	 * based on the course ID
	 * @param courseId
	 * @return
	 */
	public int findTeamFormingSessionRow(String courseId) {
		int i = 0;
		while (i < countTFS()) {
			if (getTeamFormingSessionCourseID(i).equals(courseId))
				return i;
			i++;
		}
		return -1;
	}
	
	/* -----------------------------------------------------------------------
	 * Counting functions
	 * Currently all five are the same, which is looking for number of rows
	 * in dataform table.
	 * Can be modified so that each function is specific for its purpose
	 * --------------------------------------------------------------------- */
	
	public int countCourses() {
		waitForElementPresent(By.id("dataform"));
		if (getElementText(By.xpath(String.format("//table[@id='dataform']//tr[2]//td[1]"))).isEmpty()) {
			return 0;
		} else {
			return selenium.getXpathCount(DATAFORM_TABLE_ROW).intValue() - 1;
		}
	}
	
	public int countCourseDetailStudents() {
		waitForElementPresent(By.id("dataform"));
		if (getElementText(By.xpath(String.format("//table[@id='dataform']//tr[2]//td[1]"))).isEmpty()) {
			return 0;
		} else {
			return selenium.getXpathCount(DATAFORM_TABLE_ROW).intValue() - 1;
		}
	}

	public int countTFS() {
		waitForElementPresent(By.id("dataform"));
		if (getElementText(By.xpath(String.format("//table[@id='dataform']//tr[2]//td[1]"))).isEmpty()) {
			return 0;
		} else {
			return selenium.getXpathCount(DATAFORM_TABLE_ROW).intValue() - 1;
		}
	}

	public int countReviewerSummaryStudents() {
		waitForElementPresent(By.id("dataform"));
		if (getElementText(By.xpath(String.format("//table[@id='dataform']//tr[2]//td[1]"))).isEmpty()) {
			return 0;
		} else {
			return selenium.getXpathCount(DATAFORM_TABLE_ROW).intValue() - 1;
		}
	}
	
	public int countEvaluations() {
		waitForElementPresent(By.id("dataform"));
		if (getElementText(By.xpath(String.format("//table[@id='dataform']//tr[2]//td[1]"))).isEmpty()) {
			return 0;
		} else {
			return selenium.getXpathCount(DATAFORM_TABLE_ROW).intValue() - 1;
		}
	}

	public String getEvaluationCourseID(int rowID) {
		waitForElementPresent(By.id("dataform"));
		return selenium.getTable("id=dataform." + (rowID+1) + ".0");
	}
	
	public String getEvaluationName(int rowID) {
		waitForElementPresent(By.id("dataform"));
		return selenium.getTable("id=dataform." + (rowID+1) + ".1");
	}
	
	public String getEvaluationStatus(int rowID) {
		waitForElementPresent(By.id("dataform"));
		return selenium.getTable("id=dataform." + (rowID+1) + ".2");
	}

	public String getEvaluationStatus(String courseId, String evalName) {
		int rowID = getEvaluationRowID(courseId, evalName);
		if (rowID > -1) {
			return getEvaluationStatus(rowID);
		} else {
			fail("Evaluation not found.");
			return "";
		}
	}

	public String getEvaluationResponse(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".3");
	}

	public String getEvaluationResponse(String courseId, String evalName) {
		int rowID = getEvaluationRowID(courseId, evalName);
		if (rowID > -1) {
			return getEvaluationResponse(rowID);
		} else {
			fail("Evaluation not found.");
			return "getEvaluationResponse(String evalName) failed.";
		}
	}
	
	public String getHomeEvaluationName(String courseID, String courseName){
		waitForElementPresent(By.id("dataform"));
		int courseRowID = getCoordHomeCourseRowID(courseID);
		int evaluationRowID = getCoordHomeEvaluationRowID(courseID, courseName);
		return getElementText(By.xpath(String.format("//div[@id='course%d']//table[@id='dataform']//tr[@id='evaluation%d']/td[1]",courseRowID,evaluationRowID)));
	}
	
	public String getHomeEvaluationStatus(String courseID, String courseName){
		waitForElementPresent(By.id("dataform"));
		int courseRowID = getCoordHomeCourseRowID(courseID);
		int evaluationRowID = getCoordHomeEvaluationRowID(courseID, courseName);
		return getElementText(By.xpath(String.format("//div[@id='course%d']//table[@id='dataform']//tr[@id='evaluation%d']/td[2]",courseRowID,evaluationRowID)));
	}

	/* ----------------------------------------------------------------------
	 * Locator functions
	 * Returns the locator (By object) of some links/objects on the page
	 * -------------------------------------------------------------------- */
	// Reviewer summary
	public By getReviewerSummaryView(int rowID) { return By.id("viewEvaluationResults" + rowID); }
	public By getReviewerSummaryEdit(int rowID) { return By.id("editEvaluationResults" + rowID); }
	
	// Reviewer individual
	public By getReviewerIndividualToStudent(int rowID) { return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]", rowID + 4, 1)); }
	public By getReviewerIndividualToStudentPoint(int rowID) { return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]", rowID + 4, 2)); }
	
	// Reviewee individual
	public By getRevieweeIndividualFromStudent(int rowID) { return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]", rowID + 4, 1)); }
	public By getRevieweeIndividualFromStudentPoint(int rowID) { return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]", rowID + 4, 2)); }

	// Reviewer detail
	public By getReviewerDetailClaimed(int teamIdx, int rowID) { return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]", teamIdx, rowID, 2)); }
	public By getReviewerDetailPerceived(int teamIdx, int rowID) { return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]", teamIdx, rowID, 3)); }
	public By getReviewerDetailToStudent(int teamIdx, int studentIdx, int rowID) { return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]", teamIdx, studentIdx, rowID + 4, 1)); }
	public By getReviewerDetailToStudentPoint(int teamIdx, int studentIdx, int rowID) { return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]", teamIdx, studentIdx, rowID + 4, 2)); }

	// Reviewee detail
	public By getRevieweeDetailClaimed(int teamIdx, int rowID) { return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]", teamIdx, rowID, 2)); }
	public By getRevieweeDetailPerceived(int teamIdx, int rowID) { return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]", teamIdx, rowID, 3)); }
	public By getRevieweeDetailFromStudent(int teamIdx, int studentIdx, int rowID) { return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]", teamIdx, studentIdx, rowID + 4, 1)); }
	public By getRevieweeDetailFromStudentPoint(int teamIdx, int studentIdx, int rowID) { return By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]", teamIdx, studentIdx, rowID + 4, 2)); }

	// Reviewee summary
	public String getRevieweeSummaryClaimed(int studentIndex) {
		waitForElementPresent(By.id("dataform"));
		studentIndex++;
		return selenium.getTable("id=dataform." + studentIndex + ".2");
	}

	public String getRevieweeSummaryDifference(int studentIndex) {
		waitForElementPresent(By.id("dataform"));
		studentIndex++;
		return selenium.getTable("id=dataform." + studentIndex + ".3");
	}

	// reviewer individual:
	public String getReviewerIndividualClaimedPoint() {
		return getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]", 2)));
	}
	
	public String getReviewerIndividualPerceivedPoint() {
		return getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]", 3)));
	}

	// reviewee individual:
	public String getRevieweeIndividualClaimedPoint() {
		return getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]", 2)));
	}
	public String getRevieweeIndividualPerceivedPoint() {
		return getElementText(By.xpath(String.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]", 3)));
	}

	/**
	 * @page Edit evaluation result
	 * @param rowID
	 * @return
	 */
	public By getSubmissionPoint(int rowID) {
		return By.id("points" + rowID);
	}

	/**
	 * @page Edit evaluation result
	 * @param rowID
	 * @param points
	 */
	public void setSubmissionPoint(int rowID, String points) {
		selectDropdownByValue(By.id("points" + rowID), points);
	}

	/**
	 * @page Edit evaluation result
	 * @param rowID
	 * @return
	 */
	public By getSubmissionJustification(int rowID) {
		return By.name("justification" + rowID);
	}

	/**
	 * @page Edit evaluation result
	 * @param rowID
	 * @param justification
	 */
	public void setSubmissionJustification(int rowID, String justification) {
		fillString(By.name("justification" + rowID), justification);
	}

	/**
	 * @page Edit evaluation result
	 * @param rowID
	 * @return
	 */
	public By getSubmissionComments(int rowID) {
		return By.name("commentstostudent" + rowID);
	}

	/**
	 * @page Edit evaluation result
	 * @param rowID
	 * @param comments
	 */
	public void setSubmissionComments(int rowID, String comments) {
		fillString(By.name("commentstostudent" + rowID), comments);
	}

	/**
	 * Checks whether the Publish had actually sent the e-mails to students
	 * @param gmail
	 * @param password
	 * @param courseCode
	 * @param evaluationName
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
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

	/* -----------------------------------------------------------------------
	 * Functions dealing with UI testing setup
	 * ----------------------------------------------------------------------*/
	/**
	 * Start Chrome service, return service instance
	 * 
	 * @return the service instance
	 */
	private ChromeDriverService startChromeDriverService() {
		chromeService = new ChromeDriverService.Builder().usingChromeDriverExecutable(new File(Config.getChromeDriverPath())).usingAnyFreePort().build();
		try {
			chromeService.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return chromeService;
	}

	/**
	 * Sets up the Selenium for UI testing
	 */
	public void setupSelenium() {
		System.out.println("Initializing Selenium.");

		if (Config.inst().BROWSER.equals("htmlunit")) {
			System.out.println("Using HTMLUnit.");

			setDriver(new HtmlUnitDriver());
			selenium = new WebDriverBackedSelenium(driver, Config.inst().TEAMMATES_URL);

		} else if (Config.inst().BROWSER.equals("firefox")) {
			System.out.println("Using Firefox.");
			setDriver(new FirefoxDriver());
			selenium = new WebDriverBackedSelenium(driver, Config.inst().TEAMMATES_URL);

		} else if (Config.inst().BROWSER.equals("chrome")) {

			System.out.println("Using Chrome");

			// Use technique here:
			// http://code.google.com/p/selenium/wiki/ChromeDriver
			ChromeDriverService service = startChromeDriverService();
			setDriver(new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome()));

			System.out.println(driver.toString());
			selenium = new WebDriverBackedSelenium(driver, Config.inst().TEAMMATES_URL);

		} else {

			System.out.println("Using " + Config.inst().BROWSER);

			// iexplore, opera, safari. For some not-supported-yet browsers, we
			// use legacy methods: Going through the RC server.
			String selBrowserIdentifierString = "*" + Config.inst().BROWSER;

			selenium = new DefaultSelenium(Config.inst().SELENIUMRC_HOST, Config.inst().SELENIUMRC_PORT, selBrowserIdentifierString, Config.inst().TEAMMATES_URL);
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

	/* -------------------------------------------------------------------
	 * Waiting functions
	 * Uses Thread.sleep
	 * ----------------------------------------------------------------- */
	/**
	 * Do not use fixed duration wait, use wait on other elements to appear.
	 * @deprecated
	 */
	public void justWait() {
		waitAWhile(1500);
	}
	
	/**
	 * Waiting function used when we want to confirm sending e-mail.
	 * Waits for 5 seconds.
	 */
	public void waitForEmail(){
		waitAWhile(5000);
	}

	/**
	 * Convenience method to wait for a specified period of time.
	 * Using Thread.sleep
	 * @param miliseconds
	 */
	public void waitAWhile(long miliseconds) {
		try {
			Thread.sleep(miliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Short snippet to wait for page-load.
	 * Must be appended after every action that requires a page reload or an AJAX request being made.
	 * huy (Aug 26) - This should be deprecated. Since WebDriver makes sure the new page is loaded before returning the call
	 * Aldrian (May 21) - But there are still functions using Selenium, which does not guarantee so
	 */
	public void waitForPageLoad() {
		try {
			selenium.waitForPageToLoad("15000");
		} catch (SeleniumException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Waits for the element to present, fails on timeout
	 * @param by
	 */
	public void waitForElementPresent(final By by) {
		int counter = 0;
		while (!isElementPresent(by)) {
			if (counter++ > RETRY)
				fail("Timeout while waiting for "+by);
			waitAWhile(RETRY_TIME);
		}
	}
	
	/**
	 * Waits for the element to present, returns on timeout
	 * @param by
	 */
	public void waitForElementPresentWithoutFail(final By by) {
		try{
			(new WebDriverWait(driver, TIMEOUT))
			.until(new ExpectedCondition<WebElement>(){
				@Override
				public WebElement apply(WebDriver d) {
					return d.findElement(by);
				}});
		} catch (TimeoutException e){
			
		}
	}

	public void waitForStatusMessage(String message){
		waitForTextInElement(statusMessage, message);
	}

	/**
	 * Waits for a text to appear in an element
	 * @see {@link #TIMEOUT}
	 * @param locator
	 * @param value
	 */
	public void waitForTextInElement(By locator, String value) {
		int counter = 0;
		while (true) {
			if (isElementPresent(locator) && getElementText(locator).equals(value))
				return;
			System.err.println("Looking for:"+locator + ": " + value);
			System.err.println("But found  :"+locator + ": " + getElementText(locator));
			if (counter++ > RETRY)
				fail("Timeout while waiting for "+getElementText(locator)+" to be same as "+value);
			waitAWhile(RETRY_TIME);
		}
	}

	public void waitforElementTextChange(By locator) {
		String oldMessage = getElementText(locator);
		System.out.println(oldMessage);
		int counter = 0;
		while (true) {
			if (counter++ > RETRY)
				fail("Timeout while waiting for text at "+locator+" to change");
			if (!getElementText(locator).equals(oldMessage))
				break;
			waitAWhile(RETRY_TIME);
		}
	}

	/* ---------------------------------------------------------------------
	 * Form filling functions
	 * Functions that deal with forms, such as filling a text, selecting
	 * value from dropdown box, etc.
	 * -------------------------------------------------------------------- */
	/**
	 * WebDriver fills the input field with text value (will clear the data first) <br />
	 * It will wait for the element to exist until timeout.
	 */
	public void fillString(By by, String value) {
		waitForElementPresent(by);
		WebElement ele = driver.findElement(by);
		ele.clear();
		ele.sendKeys(value);
	}
	
	/**
	 * Fills in Evaluation Name.
	 * Waits for element exists or timeout.
	 * @param name
	 * @return
	 * 		The final value in the field 
	 */
	public String fillInEvalName(String name)
	{
		fillString(inputEvaluationName, name);
		return selenium.getValue("id=evaluationname");
	}
	
	/**
	 * Fills in Course Name.
	 * Waits for element exist or timeout.
	 * @param name
	 * @return
	 * 		The final value in the field
	 */
	public String fillInCourseName(String name)
	{
		fillString(coordCourseInputCourseName, name);
		return selenium.getValue("id=coursename");
	}
	
	/**
	 * Fills in course ID.
	 * Waits for element exists or timeout.
	 * @param id
	 * @return
	 * 		The final value in the field, whitespace-trimmed
	 */
	public String fillInCourseID(String id)
	{
		fillString(coordCourseInputCourseID, id);
		return selenium.getValue("id=courseid");
	}
	
	/**
	 * Retrieves element's text through WebDriver.
	 * Does not do any wait.
	 * @return empty string if element is not found.
	 */
	public String getElementText(By locator) {
		if (!isElementPresent(locator))
			return "";
		return driver.findElement(locator).getText();
	}
	
	/**
	 * Retrieves the element's attribute based on the attribute name 
	 * @param locator
	 * @param attrName
	 * @return
	 */
	public String getElementAttribute(By locator, String attrName){
		waitForElementPresent(locator);
		return driver.findElement(locator).getAttribute(attrName);
	}

	/**
	 * Retrieves the element's `value` attribute.
	 * Usually used for elements like input, option, etc.
	 * @param locator
	 * @return
	 */
	public String getElementValue(By locator) {
		return getElementAttribute(locator,"value");
	}
	
	/**
	 * Retrieves the element's 'href' attribute, returns the relative path
	 * (i.e., without "http://<main-app-url>/")
	 * @param locator
	 * @return
	 */
	public String getElementRelativeHref(By locator){
		String link = getElementAttribute(locator, "href");
		if( !link.startsWith("http") ) return link;
		String[] tokens = link.split("/");
		String result = "";
		for(int i=3; i<tokens.length; i++){
			result+=tokens[i];
		}
		return result;
	}

	/**
	 * Returns the first selected option on a dropdown box.
	 * Waits until element exists or timeout.
	 * @param locator
	 * @return
	 */
	public String getDropdownSelectedValue(By locator) {
		waitForElementPresent(locator);
		Select select = new Select(driver.findElement(locator));
		return select.getFirstSelectedOption().getAttribute("value");
	}
	
	/**
	 * Selects a value from a dropdown list.
	 * Waits until the element exists or timeout.
	 * @param locator
	 * @param value
	 */
	public void selectDropdownByValue(By locator, String value) {
		waitForElementPresent(locator);
		Select select = new Select(driver.findElement(locator));
		select.selectByValue(value);
	}

	public boolean isInUse() {
		return inUse;
	}
	public void setInUse(boolean b) {
		inUse = b;
	}
	
	/* -----------------------------------------------------------------------
	 * Checker functions (is*** methods)
	 * Checks whether some conditions are true, then return the result
	 * immediately (no waiting)
	 * --------------------------------------------------------------------- */
	/**
	 * Wrapper method to check whether an element exists (already loaded)<br />
	 * Issue: It is said that this method return true also when the element
	 * is partially loaded (probably rendered but not enabled yet)
	 * @param by
	 * @return
	 */
	public boolean isElementPresent(By by) {
		return driver.findElements(by).size() != 0;
	}
	/**
	 * Wrapper method to check whether an element exists (already loaded),
	 * wait until the element is present or timeout<br />
	 * Issue: It is said that this method return true also when the element
	 * is partially loaded (probably rendered but not enabled yet)
	 * @param by
	 * @return
	 */
	public boolean isElementPresentWithWait(By by) {
		waitForElementPresent(by);
		return driver.findElements(by).size() != 0;
	}
	
	/**
	 * Checks whether a text is present in current page
	 * @param text
	 * @return
	 */
	public boolean isTextPresent(String text) {
		return selenium.isTextPresent(text);
	}
	
	/**
	 * Checks whether we're at the local login page
	 */
	public boolean isLocalLoginPage() {
		if (isElementPresent(By.id("email")) && isElementPresent(By.id("isAdmin")))
			return true;
		return false;
	}

	/**
	 * Helper method to check that we're at the login page
	 * Checking for the e-mail and password fields, and the sign in button
	 * @return
	 */
	public boolean isGoogleLoginPage() {
		if (isElementPresent(By.id("Email")) && isElementPresent(By.id("Passwd")) && isElementPresent(By.id("signIn")))
			return true;
	
		return false;
	}
	
	/**
	 * Checks whether a course is present in Course page
	 * @page Course page
	 * @param courseId
	 * @param courseName
	 * @return
	 */
	public boolean isCoursePresent(String courseId, String courseName) {
		int totalCourses = countCourses();
		boolean isPresent = false;
		for (int i = 0; i < totalCourses; i++) {
			if (getElementText(By.id("courseID" + i)).equalsIgnoreCase(courseId) && getElementText(By.id("courseName" + i)).equals(courseName)) {
				isPresent = true;
				break;
			}
		}
		return isPresent;
	}
	
	/**
	 * Checks whether a course is present in Home page
	 * @param courseID
	 * @param courseName
	 * @return
	 */
	public boolean isHomeCoursePresent(String courseID, String courseName){
		int id = 0;
		while(isElementPresent(By.id("course"+id))){
			if(getElementText(By.xpath("//div[@id='course"+id+"']/div[@class='result_homeTitle']/h2")).equalsIgnoreCase("["+courseID+"] : "+courseName)){
				return true;
			}
			id++;
		}
		return false;
	}
	
	/**
	 * Checks whether a specific evaluation exists
	 * (based on courseID and evaluation name)
	 * @page Evaluation page
	 * @param courseId
	 * @param evalName
	 * @return
	 */
	public boolean isEvaluationPresent(String courseId, String evalName) {
		return getEvaluationRowID(courseId, evalName)>-1;
	}
	
	/**
	 * Checks whether a Team Forming Session is present for a course
	 * @page Team Forming Session page
	 * @param courseId
	 * @return
	 */
	public boolean isTeamFormingSessionPresent(String courseId) {
		int totalTeamFormingSession = countTFS();
		boolean isPresent = false;
		for (int i = 0; i < totalTeamFormingSession; i++) {
			if (getElementText(By.xpath(String.format("//table[@id='dataform']//tr["+(i+2)+"]//td[1]"))).equals(courseId)) {
				isPresent = true;
				continue;
			}
		}
		return isPresent;
	}

	public DefaultSelenium getSelenium() {
		return selenium;
	}

	public WebDriver getDriver() {
		return driver;
	}

	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}

	public void openSelectedWindow(String url, String title) {
		selenium.open(url);
		selenium.selectWindow(title);
		selenium.windowFocus();
	}

	public void closeSelectedWindow() {
		// Close the window and back to the main one
		selenium.close();
		selenium.selectWindow("null");
		selenium.windowFocus();
	}
	
	/* --------------------------------------------------------------------
	 * GoTo functions
	 * Goes to certain pages and verifies whether it is successful
	 * ------------------------------------------------------------------ */
	/**
	 * Goes to the URL as specified.
	 * Obviously, no verification is done for this.
	 * @param url
	 */
	public void goToUrl(String url){
		driver.get(url);
	}

	/**
	 * Goes to Main page by going to the root URL
	 * Verifies that the page is loaded correctly before returning.
	 */
	public void goToMain() {
		selenium.open("/");
		verifyMainPage();
	}
	
	/**
	 * Goes to Coordinator Home page by clicking on the link "Home" in the navigation bar.
	 * Pre-condition: Should be logged in as Coordinator
	 * Verifies that the page is loaded correctly before returning.
	 */
	public void goToCoordHome() {
		clickHomeTab();
		verifyCoordHomePage();
	}

	/**
	 * Goes to Student home page by clicking on the link "Home" in the navigation bar.
	 * Pre-condition: Should be logged in as Student
	 * Verifies that the page is loaded correctly before returning.
	 */
	public void goToStudentHome() {
		clickHomeTab();
		verifyStudentHomePage();
	}

	/**
	 * Goes to Courses page by clicking on the link "Courses" in the navigation bar.
	 * Verifies that the page is loaded correctly before returning.
	 */
	public void goToCourses() {
		clickCourseTab();
		verifyCoordCoursesPage();
	}
	/**
	 * Goes to Team Forming page by clicking on the link "Team-forming" in the navigation bar.
	 * Verifies that the page is loaded correctly before returning.
	 */
	public void goToTeamForming() {
		clickTeamFormingTab();
		verifyCoordTeamFormingPage();
	}
	/**
	 * Goes to Evaluation page by clicking on the link "Evaluation" in the navigation bar.
	 * Verifies that the page is loaded correctly before returning.
	 */
	public void goToEvaluation() {
		clickEvaluationTab();
		verifyCoordEvaluationsPage();
	}
	/* -----------------------------------------------------------------------
	 * Object comparison functions
	 * Compares objects and verifies that the objects are the same
	 * --------------------------------------------------------------------- */
	/**
	 * Verifies a HTML page as pointed by url against the HTML file stored at location pointed by filepath
	 * @param url
	 * @param filepath
	 * @throws Exception
	 */
	public void verifyPageHTML(String url, String filepath) throws Exception {
		try {
			URL help = new URL(url);
			URLConnection yc = help.openConnection();
			String actual = Common.readStream(yc.getInputStream());
			String expected = Common.readFile(filepath);
			
			HtmlHelper.assertSameHtml(actual, expected);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			fail("Error: " + e.getMessage());
		}
	}

	/**
	 * Verifies current page against the page stored at location as pointed by filepath.
	 * This method replaces the occurence of {version} in the reference file with the
	 * value stored at Common.VERSION
	 * @param filepath
	 * @throws Exception
	 */
	public void verifyCurrentPageHTML(String filepath)throws Exception{
		String pageSrc = driver.getPageSource();
		String inputStr = Common.readFile(filepath).replace("{version}",Common.VERSION);
		HtmlHelper.assertSameHtml(inputStr, pageSrc);
	}
	
	/**
	 * Method to print current page to a file.
	 * This is to be used in HTML testing, where we can generate the reference HTML file using this method.
	 * This method is deprecated so that you won't forget to remove it
	 * @param destination
	 * @deprecated
	 */
	public void printCurrentPage(String destination) throws Exception{
		String pageSrc = driver.getPageSource();
		TestFileWriter output = new TestFileWriter(new File(destination));
		output.write(pageSrc);
		output.close();
	}
	
	/**
	 * Verifies an object content (div) against the one stored at filepath
	 * @param filepath
	 * @param div
	 * @throws Exception
	 */
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

	/**
	 * Verifies current page with a reference page, i.e., finding the reference
	 * string in current page (so the reference does not have to be full page)<br />
	 * <br />
	 * This method has minimal placeholder capability, matching {*} in the
	 * reference with anything in current page, trying to maximize the match.
	 * This method also replaces {version} into the value stored at Common.VERSION<br />
	 * <br />
	 * Example usage is to test sorting elements, say we want to test the order
	 * of two known elements, which should be independent in the presence of other
	 * elements. We can also ignore the rowID which maybe different under different
	 * number of elements.<br />
	 * <br />
	 * This method will try to display the difference between the expected and
	 * actual if the match fails.
	 * @param filepath
	 * @param div
	 * @throws Exception
	 */
	public void verifyCurrentPageHTMLRegex(String filepath) throws Exception{
		String pageSrc = driver.getPageSource();
		String inputStr = Common.readFile(filepath).replace("{version}",Common.VERSION);
		Common.assertContainsRegex(inputStr.replace("\r\n", "\n"),pageSrc.replace("\r\n", "\n"));
	}

	/* -----------------------------------------------------------------------
	 * Verification functions
	 * Checks whether we are in the correct page,
	 * or checks whether something has been done correctly
	 * --------------------------------------------------------------------- */
	
	/**
	 * Helper method to check that we're at the main page
	 * Checking for the Coordinator and Student links
	 */
	public void verifyMainPage() {
		for (int x = 0;; x++) {
			if (x >= RETRY)
				fail("Not in main page");
	
			if (isElementPresent(By.name("STUDENT_LOGIN")) && isElementPresent(By.name("COORDINATOR_LOGIN")))
				break;
	
			waitAWhile(RETRY_TIME);
		}
	}

	/**
	 * Helper method to verify that we're at the login page.
	 * Checking for the e-mail and password fields and the sign in button
	 */
	public void verifyGoogleLoginPage() {
		for (int x = 0;; x++){
			if (x >= RETRY)
				fail("Not in Google Login Page");
			
			if (isGoogleLoginPage())
				break;
			
			waitAWhile(RETRY_TIME);
		}
	}
	/**
	 * Helper method to check that the team forming session was added successfully.
	 * Checks for the details of the evaluation that was added.
	 * @param courseId
	 * @param status
	 */
	public void verifyTeamFormingSessionAdded(String courseId, String status) {
		for (int i = 0; i < countTFS(); i++) {
			if (getTeamFormingSessionCourseID(i).equals(courseId))
				assertEquals(status, getElementText(By.className("t_team_status")));
			assertEquals(status, getTeamFormingSessionStatus(i));
		}
	}
	/**
	 * Checks that the course has been added.
	 * Checking for the course details appearing in the table.
	 * This checks for a new course, so this also verifies
	 * the default number of teams to be 0.
	 * Page: Coordinator home
	 */
	public void verifyCourseIsAdded(String courseId, String courseName) {
		// Check for courseId
		int rowNumber = getCourseRowID(courseId);
		System.out.println("BrowserInstance: Verifying course id : " + getCourseIDCell(rowNumber));
		assertEquals(courseId, getElementText(getCourseIDCell(rowNumber)));
	
		// Check for course name
		assertEquals(courseName, getElementText(getCourseNameCell(rowNumber)));
	
		// Check for default number of teams - 0
		assertEquals("0", getCourseNumberOfTeams(getCourseRowID(courseId)));
	}
	
	/**
	 * Helper method to check that we've enrolled students successfully.
	 * Checks that the number of students added/edited equals the number
	 * expected.
	 * @param added
	 * @param edited
	 */
	public void verifyEnrollment(int added, int edited) {
		for (int x = 0;; x++) {
			if (x >= RETRY)
				fail("Have not enrolled students successfully");
			
			if ((isElementPresent(By.xpath("//tr[@id='rowAddedStudents']/td"))) && (isElementPresent(By.xpath("//tr[@id='rowEditedStudents']/td"))))
				break;
			
			waitAWhile(RETRY_TIME);
		}
	
		assertEquals(added, Integer.parseInt(getElementText(By.id("t_studentsAdded"))));
		assertEquals(edited, Integer.parseInt(getElementText(By.id("t_studentsEdited"))));
	}
	
	/**
	 * Helper method to check that the evaluation was added successfully.
	 * Checks for the details of the evaluation that was added.
	 * @param courseId
	 * @param evalName
	 * @param status
	 * @param resp
	 */
	public void verifyEvaluationAdded(String courseId, String evalName, String status, String resp) {
	
		for (int i = 0; i < countEvaluations(); i++) {
			if (getEvaluationCourseID(i).equals(courseId) && getEvaluationName(i).equals(evalName)) {
				assertEquals(status, getEvaluationStatus(i));
				assertEquals(resp, getEvaluationResponse(i));
			}
		}
	}
	
	/**
	 * Helper method to verify that we're at the Student home page.
	 * Checking for the headers expected in evaluations page:
	 * <ul>
	 * <li>Class: t_home</li>
	 * <li>Class: t_courses</li>
	 * <li>Class: t_evaluations</li>
	 * <li>Class: t_help</li>
	 * <li>Class: t_logout</li>
	 * <li>ID: joinNewCourse</li>
	 * </ul>
	 */
	public void verifyStudentHomePage() {
		for (int x=0;; x++){
			if (x >= RETRY){
				fail("Not in Student Page");
			}
			
			if (isElementPresent(homeTab) && isElementPresent(courseTab)
					&& isElementPresent(evaluationTab) && isElementPresent(helpTab)
					&& isElementPresent(logoutTab) && isElementPresent(studentJoinNewCourseLink))
				break;
			
			waitAWhile(RETRY_TIME);
		}
	}

	/**
	 * Helper method to verify that we're at the Student courses page.
	 * Checking for these fields:
	 * <ul>
	 * <li>ID: regkey</li>
	 * <li>ID: btnJoinCourse</li>
	 * </ul>
	 */
	public void verifyStudentCoursesPage() {
		for (int x=0;; x++){
			if (x >= RETRY){
				fail("Not in Student Courses Page");
			}
			
			if (isElementPresent(studentInputRegKey)
					&& isElementPresent(studentJoinCourseButton))
				break;
			
			waitAWhile(RETRY_TIME);
		}
	}
	
	/**
	 * Helper method to verify that we're at the Student evaluations page.
	 * Checking for the headers expected in evaluations page:
	 * <ul>
	 * <li>Text: Pending Evaluations:</li>
	 * <li>Text: Past Evaluations:</li>
	 * </ul>
	 */
	public void verifyStudentEvaluationsPage() {
		for (int x=0;; x++){
			if (x >= RETRY){
				fail("Not in Student Evaluations Page");
			}
			
			if (isTextPresent(PENDING_EVALUATIONS_HEADER) && isTextPresent(PAST_EVALUATIONS_HEADER))
				break;
			
			waitAWhile(RETRY_TIME);
		}
	}

	/**
	 * Helper method to verify that we're at the Student do evaluations page.
	 * Also, can be used to verify that we're at the Student edit evaluation submission page.
	 * Checking for these fields:
	 * <ul>
	 * <li>XPath: StudentEvaluationCourseID</li>
	 * <li>XPath: StudentEvaluationEvaluationName</li>
	 * <li>XPath: StudentEvaluationOpeningTime</li>
	 * <li>XPath: StudentEvaluationClosingTime</li>
	 * <li>XPath: StudentEvaluationInstructions</li>
	 * </ul>
	 */
	public void verifyStudentDoOrEditEvaluationPage() {
		for (int x=0;; x++){
			if (x >= RETRY){
				fail("Not in Student Do Evaluation or Edit Evaluation Submission Page");
			}
			
			if (isElementPresent(studentEvaluationCourseID) && isElementPresent(studentEvaluationEvaluationName) 
					&& isElementPresent(studentEvaluationOpeningTime) && isElementPresent(studentEvaluationClosingTime) 
					&& isElementPresent(studentEvaluationInstructions))
				break;
			
			waitAWhile(RETRY_TIME);
		}
	}

	/**
	 * Helper method to verify that we're at the Student do evaluations page.
	 * Also, can be used to verify that we're at the Student edit evaluation submission page.
	 * Checking for these fields:
	 * <ul>
	 * <li>XPath: StudentEvaluationResultCourseID</li>
	 * <li>XPath: StudentEvaluationResultStudentName</li>
	 * <li>XPath: StudentEvaluationResultTeamName</li>
	 * <li>XPath: StudentEvaluationResultEvaluationName</li>
	 * <li>XPath: StudentEvaluationResultClaimedPoints</li>
	 * <li>XPath: StudentEvaluationResultOpeningTime</li>
	 * <li>XPath: StudentEvaluationResultPerceivedPoints</li>
	 * <li>XPath: StudentEvaluationResultClosingTime</li>\
	 * </ul>
	 */
	public void verifyStudentEvaluationResultsPage() {
		waitForPageLoad();
		for (int x = 0; ; x++) {
			if (x >= RETRY)
				fail("Not in Student Evaluation Result Page");

			if (isElementPresent(studentEvaluationResultStudentName) && isElementPresent(studentEvaluationResultCourseID) 
					&& isElementPresent(studentEvaluationResultTeamName) && isElementPresent(studentEvaluationResultEvaluationName) 
					&& isElementPresent(studentEvaluationResultClaimedPoints) && isElementPresent(studentEvaluationResultOpeningTime) 
					&& isElementPresent(studentEvaluationResultPerceivedPoints) && isElementPresent(studentEvaluationResultClosingTime))
				break;

			waitAWhile(RETRY_TIME);
		}
	} 
	
	/**
	 * Helper method to verify that we're at the Student course details page.
	 * Checking for the various fields expected in course details page:
	 * <ul>
	 * <li>XPath: StudentCourseDetailCourseID</li>
	 * <li>XPath: StudentCourseDetailTeamName</li>
	 * <li>XPath: StudentCourseDetailCourseName</li>
	 * <li>XPath: StudentCourseDetailStudentName</li>
	 * <li>XPath: StudentCourseDetailCoordinatorName</li>
	 * <li>XPath: StudentCourseDetailStudentEmail</li>
	 * <li>XPath: StudentCourseDetailStudentTeammates</li>
	 * </ul>
	 */
	public void verifyStudentViewCourseDetailsPage() {
		for (int x=0;; x++){
			if (x >= RETRY){
				fail("Not in Student Course Details Page");
			}
			
			if (isElementPresent(studentCourseDetailCourseID) && isElementPresent(studentCourseDetailTeamName) && isElementPresent(studentCourseDetailCourseName) 
					&& isElementPresent(studentCourseDetailStudentName) && isElementPresent(studentCourseDetailCoordinatorName) 
					&& isElementPresent(studentCourseDetailStudentEmail) && isElementPresent(studentCourseDetailStudentTeammates))
				break;
			
			waitAWhile(RETRY_TIME);
		}
	}
	
	/**
	 * Helper method to check that we're at the team detail page.
	 * Checks for the various fields expected:
	 * <ul>
	 * <li>Text: TEAM DETAIL</li>
	 * <li>ID: teamName</li>
	 * <li>ID: teamProfile</li>
	 * <li>ID: button_back</li>
	 * <li>ID: button_saveTeamProfile</li>
	 * </ul>
	 */
	public void verifyStudentTeamDetailPage() {
		for (int x = 0;; x++) {
			if (x >= RETRY)
				fail("Not in Team Detail Page");				
	
			if((isTextPresent("TEAM DETAIL")) && (isElementPresent(inputTeamName)) 
					&& (isElementPresent(inputTeamProfile)) && (isElementPresent(resultBackButton)) 
					&& (isElementPresent(saveTeamProfile)))
				break;
			
			waitAWhile(RETRY_TIME);
		}
	}
	/**
	 * Helper method to verify that we're at the Coordinator home page.
	 * Checking for these fields:
	 * <ul>
	 * <li>Home Tab</li>
	 * <li>Courses Tab</li>
	 * <li>Evaluations Tab</li>
	 * <li>Help Tab</li>
	 * <li>Logout Tab</li>
	 * <li>Coordinator Add New Course Link</li>
	 * </ul>
	 */
	public void verifyCoordHomePage() {
		for (int x=0;; x++){
			if (x >= RETRY){
				fail("Not in Coordinator Home Page");		
			}
	
			if (isElementPresent(homeTab) && isElementPresent(courseTab)
					&& isElementPresent(evaluationTab) && isElementPresent(helpTab)
					&& isElementPresent(logoutTab) && isElementPresent(coordHomeAddNewCourseLink))
				break;
			
			waitAWhile(RETRY_TIME);
		}
	}
	/**
	 * Helper method to verify that we're at the Coordinator courses page.
	 * Checking for these fields:
	 * <ul>
	 * <li>XPath: Page Title</li>
	 * <li>ID: courseid</li>
	 * <li>ID: coursename</li>
	 * <li>ID: btnAddCourse</li>
	 */
	public void verifyCoordCoursesPage() {
		for (int x = 0;; x++){
			if (x >= RETRY){
				fail("Not in Coordinator Courses Page");				
			}
			
			if (isElementPresent(pageTitle) && isElementPresent(coordCourseInputCourseID)
					&& isElementPresent(coordCourseInputCourseName) && isElementPresent(coordCourseAddButton))
				break;
			
			waitAWhile(RETRY_TIME);
		}
	
	}

	public By coordCourseDetailsCourseID = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 1, 2));
	public By coordCourseDetailsCourseName = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 2, 2));
	public By coordCourseDetailsTeams = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 3, 2));
	public By coordCourseDetailsTotalStudents = By.xpath(String.format(HEADER_FORM_TABLE_CELL, 4, 2));
	
	/**
	 * Helper method to verify that we're at the Coordinator course details page.
	 * Checking for these fields:
	 * <ul>
	 * <li>XPath: CourseDetailsCourseID</li>
	 * <li>XPath: CourseDetailsCourseName</li>
	 * <li>XPath: CourseDetailsTeams</li>
	 * <li>XPath: CourseDetailsTotalStudents</li>
	 * </ul>
	 */
	public void verifyCoordCourseDetailsPage() {
		for (int x=0;; x++){
			if (x >= RETRY){
				fail("Not in Coordinator Course Details Page");
			}
	
			if (isElementPresent(coordCourseDetailsCourseID) && isElementPresent(coordCourseDetailsCourseName)
					&& isElementPresent(coordCourseDetailsTeams) && isElementPresent(coordCourseDetailsTotalStudents))
				return;
	
			waitAWhile(RETRY_TIME);
		}
	}
	
	/**
	 * Helper method to verify that we're at the Coordinator course details page for a specific course.
	 * Checking for these fields:
	 * <ul>
	 * <li>XPath: {@link #coordCourseDetailsCourseID}</li>
	 * <li>XPath: {@link #CourseDetailsCourseName}</li>
	 * <li>XPath: {@link #CourseDetailsTeams}</li>
	 * <li>XPath: {@link #CourseDetailsTotalStudents}</li>
	 * <li>TEXT: {courseID in courseDetailsCourseID}</li>
	 * </ul>
	 */
	public void verifyCoordCourseDetailsPage(String courseID) {
		for (int x=0;; x++){
			if (x >= RETRY){
				fail("Not in Coordinator Course Details Page");
			}
	
			if (isElementPresent(coordCourseDetailsCourseID) && isElementPresent(coordCourseDetailsCourseName)
					&& isElementPresent(coordCourseDetailsTeams) && isElementPresent(coordCourseDetailsTotalStudents)
					&& getElementText(coordCourseDetailsCourseID).compareToIgnoreCase(courseID)==0)
				return;
	
			waitAWhile(RETRY_TIME);
		}
	}
	
	/**
	 * Helper method to verify that we're at the student enrollment page.
	 * Checking for these fields:
	 * <ul>
	 * <li>ID: information</li>
	 * <li>ID: button_enroll</li>
	 * </ul>
	 */
	public void verifyCoordCourseEnrollPage() {
		for (int x = 0;; x++) {
			if (x >= RETRY)
				fail("Not in Coordinator Enroll Page");
	
			if (isElementPresent(coordEnrollInfo) && isElementPresent(coordEnrollButton))
				break;
	
			waitAWhile(RETRY_TIME);
		}
	}
	
	/**
	 * Helper method to verify that we're at the student enrollment page for specific course
	 * Checking for these fields:
	 * <ul>
	 * <li>TEXT: ENROLL STUDENTS FOR {courseID in pageTitle}</li>
	 * <li>ID: information</li>
	 * <li>ID: button_enroll</li>
	 * </ul>
	 * @param courseID
	 */
	public void verifyCoordCourseEnrollPage(String courseID) {
		for (int x = 0;; x++) {
			if (x >= RETRY)
				fail("Not in Coordinator Enroll Page for "+courseID);
			if (getElementText(pageTitle).equalsIgnoreCase("Enroll Students for "+courseID) &&
					isElementPresent(coordEnrollInfo) && isElementPresent(coordEnrollButton))
				break;
			
			System.err.println("Looking for: "+"ENROLL STUDENTS FOR "+courseID.toUpperCase());
			System.err.println("But found:   "+getElementText(pageTitle));
	
			waitAWhile(RETRY_TIME);
		}
	}
	
	/**
	 * Helper method to verify that we're at the Coordinator evaluations page.
	 * Checking for these fields:
	 * <ul>
	 * <li>ID: courseid</li>
	 * <li>ID: coursename</li>
	 * <li>ID: commentsstatus</li>
	 * <li>ID: instr</li>
	 * <li>ID: start</li>
	 * <li>ID: starttime</li>
	 * <li>ID: deadline</li>
	 * <li>ID: deadlinetime</li>
	 * <li>ID: graceperiod</li>
	 * </ul>
	 */
	public void verifyCoordEvaluationsPage() {
		for (int x = 0;; x++) {
			if (x >= RETRY)
				fail("Not in Coordinator Evaluation Page");
	
			if (isElementPresent(coordCourseInputCourseID) && isElementPresent(inputEvaluationName) && isElementPresent(inputPeerFeedbackStatus)
					&& isElementPresent(inputInstruction) && isElementPresent(inputOpeningDate) && isElementPresent(inputOpeningTime)
					&& isElementPresent(inputClosingDate) && isElementPresent(inputClosingTime) && isElementPresent(inputGracePeriod))
				break;
	
			waitAWhile(RETRY_TIME);
		}
	}

	/**
	 * Helper method to verify that we're at the Coordinator edit evaluation page.
	 * Checking for these fields:
	 * <ul>
	 * <li>XPath: Coordinator Edit Evaluation Name (not yet implemented)</li>
	 * <li>ID: commentsstatus</li>
	 * <li>ID: instr</li>
	 * <li>ID: start</li>
	 * <li>ID: starttime</li>
	 * <li>ID: deadline</li>
	 * <li>ID: deadlinetime</li>
	 * <li>ID: graceperiod</li>
	 * </ul>
	 */
	public void verifyCoordEvaluationEditPage() {
		for (int x=0;; x++){
			if (x >= RETRY){
				fail("Not in Coordinator Edit Evaluation Page");
			}
			if (/*isElementPresent(coordEditEvaluationName) && */isElementPresent(inputPeerFeedbackStatus)
					&& isElementPresent(inputInstruction) && isElementPresent(inputOpeningDate) && isElementPresent(inputOpeningTime)
					&& isElementPresent(inputClosingDate) && isElementPresent(inputClosingTime) && isElementPresent(editEvaluationButton)
					&& isElementPresent(inputGracePeriod))
				break;
			
			waitAWhile(RETRY_TIME);
		}
	}
	/**
	 * Helper method to verify that we're at the Coordinator evaluation results summary page.
	 * Checking for these fields:
	 * <ul>
	 * <li>ID: radio_summary</li>
	 * <li>ID: radio_detail</li>
	 * <li>ID: radio_reviewer</li>
	 * <li>ID: radio_reviewee</li>
	 * </ul>
	 */
	public void verifyCoordEvaluationResultsPage() {
		for (int x=0;; x++){
			if (x >= RETRY){
				fail("Not in Coordinator Evaluation Results Page");
			}
			
			if (isElementPresent(resultSummaryRadio) && isElementPresent(resultDetailRadio)
					&& isElementPresent(resultReviewerRadio) && isElementPresent(resultRevieweeRadio))
				break;
			
			waitAWhile(RETRY_TIME);
		}
	}
	/**
	 * Helper method to check that we're at the team forming page.
	 * Checks for the various fields expected:
	 * <ul>
	 * <li>ID: courseid</li>
	 * <li>ID: profile_template</li>
	 * <li>ID: instr</li>
	 * <li>ID: start</li>
	 * <li>ID: starttime</li>
	 * <li>ID: deaddline</li>
	 * <li>ID: deadlinetime</li>
	 * <li>ID: graceperiod</li>
	 * </ul>
	 */
	public void verifyCoordTeamFormingPage() {
		for (int x = 0;; x++) {
			if (x >= RETRY)
				fail("Not in Team Forming Page");
	
			if ((isElementPresent(coordCourseInputCourseID)) && (isElementPresent(inputProfileTemplate))
					&& (isElementPresent(inputInstruction)) && (isElementPresent(inputOpeningDate))
					&& (isElementPresent(inputOpeningTime)) && (isElementPresent(inputClosingDate))
					&& (isElementPresent(inputClosingTime)) && (isElementPresent(inputGracePeriod)))
				break;
			
			waitAWhile(RETRY_TIME);
		}
	}
	/**
	 * Helper method to check that we're at the manage team forming page.
	 * Checks for the various fields expected.
	 * @param students
	 */
	public void verifyCoordManageTeamFormingPage(ArrayList<Student> students) {
		boolean studentsChecked = true;
		for (int x = 0;; x++) {
			if (x >= RETRY)
				fail("Not in Manage Team Forming Page");
	
			for(int i=0; i<students.size(); i++)
				if(isTextPresent(students.get(i).name)==false)
					studentsChecked = false;
	
			if((isTextPresent("TEAMS FORMED")) && (isTextPresent("STUDENTS YET TO JOIN A TEAM")) 
					&& (isElementPresent(By.id("viewTeamProfile0"))) && (isElementPresent(By.id("viewTeamProfile1")))
					&& (isElementPresent(By.id("allocateStudentTeam0"))) && (isElementPresent(By.id("allocateStudentTeam1")))
					&& studentsChecked == true)
				break;
			
			waitAWhile(RETRY_TIME);
		}
	}
	/**
	 * Helper method to check that we're at the view teams page.
	 * Checks for the various fields expected:
	 * <ul>
	 * <li>Text: TEAMS FORMED</li>
	 * <li>Text: STUDENTS YET TO JOIN A TEAM</li>
	 * <li>ID: buttonJoin0</li>
	 * <li>ID: buttonJoin1</li>
	 * <li>ID: buttonAdd0</li>
	 * <li>ID: buttonAdd1</li>
	 * </ul>
	 * @param students
	 */
	public void verifyViewTeamsPage(ArrayList<Student> students) {
		boolean studentsChecked = true;
		for (int x = 0;; x++) {
			if (x >= RETRY)
				fail("Not in View Teams Page");
	
			for(int i=0; i<students.size(); i++)
				if(isTextPresent(students.get(i).name)==false)
					studentsChecked = false;				
	
			if((isTextPresent("TEAMS FORMED")) && (isTextPresent("STUDENTS YET TO JOIN A TEAM")) 
					&& ((isElementPresent(By.id("buttonJoin0"))) || (isElementPresent(By.id("buttonJoin1"))))
					&& ((isElementPresent(By.id("buttonAdd0"))) || (isElementPresent(By.id("buttonAdd1"))))
					&& studentsChecked == true)
				break;
			
			waitAWhile(RETRY_TIME);
		}
	}
	/**
	 * Helper method to check that we're at the change student team page.
	 * Checks for these fields:
	 * <ul>
	 * <li>Text: Add to existing team:</li>
	 * <li>Text: Add to a new team:</li>
	 * <li>ID: teamchange_newteam</li>
	 * <li>ID: teamname</li>
	 * <li>ID: newteamname</li>
	 * <li>ID: button_saveTeamChange</li>
	 * <li>ID: button_back</li>
	 * </ul>
	 */
	public void verifyChangeStudentTeamPage() {
		for (int x = 0;; x++) {
			if (x >= RETRY)
				fail("Not in Student Change Team Page");				
	
			if((isTextPresent("Add to existing team:")) && (isTextPresent("Add to a new team:"))
					&& (isElementPresent(saveChangeNewStudentTeam)) && (isElementPresent(inputTeamName))
					&& (isElementPresent(inputNewTeamName)) && (isElementPresent(saveChangeStudentTeam))
					&& (isElementPresent(resultBackButton)))
				break;
			
			waitAWhile(RETRY_TIME);
		}
	}
}
