package teammates.test.driver;

//TODO: remove junit dependencies
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

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

import teammates.common.Common;
import teammates.test.cases.BaseTestCase; //TODO: remove this dependency

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.SeleniumException;

/**
 * A browser instance represents a real browser instance + context to the app
 */
public class BrowserInstance {
	protected DefaultSelenium selenium = null;
	private WebDriver driver = null;
	protected ChromeDriverService chromeService = null;
	private boolean inUse = false;
	private String loggedInUser = "";

	private static final int TIMEOUT = 10; // In seconds
	private static final int RETRY = 20;
	private static final int RETRY_TIME = TIMEOUT * 1000 / RETRY; // In milliseconds
	private static final int PAGE_VERIFY_RETRY = 3;

	//TODO: reorganize this class based on page rather than type of function
	
	/*
	 * --------------------------------------------------------------- 
	 * UI Element Below are the declaration for variables referring 
	 * to UI Element. Each is represented by its By instance
	 * --------------------------------------------------------------
	 */
	

	@SuppressWarnings("unused")
	private void ____General_UI_Elements__________________________________() {
	}

	// Homepage buttons
	public final By COORD_LOGIN_BUTTON = By
			.name(Common.PARAM_LOGIN_COORD);
	public final By STUDENT_LOGIN_BUTTON = By.name(Common.PARAM_LOGIN_STUDENT);

	// Tabs
	public By homeTab = By.className("t_home");
	public By courseTab = By.className("t_courses");
	public By evaluationTab = By.className("t_evaluations");
	public By helpTab = By.className("t_help");
	public By logoutTab = By.className("t_logout");

	// Table elements
	public By pageTitle = By.xpath("//div[@id='headerOperation']//h1");

	public static final String HEADER_FORM_TABLE_CELL = "//table[@class='headerform']//tbody//tr//td";
	public static final String DETAIL_FORM_TABLE_CELL = "//table[@class='detailform']//tbody//tr//td";
	public static final String DATAFORM_TABLE_ROW = "//table[@id='dataform']//tr";
	public static final String DATAFORM_TABLE_CELL = DATAFORM_TABLE_ROW
			+ "[%d]//td[%d]";
	
	@SuppressWarnings("unused")
	private void ____Login_Page_UI_Elements__________________________________() {
	}
	
	public By coordLoginButton = By.id("btnCoordLogin");
	public By studentLoginButton = By.id("btnStudentLogin");

	@SuppressWarnings("unused")
	private void ____Coord_UI_Elements__________________________________() {
	}

	// Homepage
	public By coordHomeAddNewCourseLink = By.id("addNewCourse");

	// Course list at home
	/**
	 * Returns the rowID (at homepage) of a course based on course ID
	 * 
	 * @param courseID
	 * @return
	 */
	public int getCoordHomeCourseRowID(String courseID) {
		int id = 0;
		while (isElementPresent(By.id("course" + id))) {
			if (getElementText(
					By.xpath("//div[@id='course" + id
							+ "']/div[@class='result_homeTitle']/h2"))
					.startsWith("[" + courseID + "]")) {
				return id;
			}
			id++;
		}
		return -1;
	}

	public By getCoordHomeCourseEnrollLinkLocator(int rowID) {
		return By.className("t_course_enroll" + rowID);
	}

	public By getCoordHomeCourseViewLinkLocator(int rowID) {
		return By.className("t_course_view" + rowID);
	}

	public By getCoordHomeCourseAddEvaluationLinkLocator(int rowID) {
		return By.className("t_course_add_eval" + rowID);
	}

	public By getCoordHomeCourseDeleteLinkLocator(int rowID) {
		return By.className("t_course_delete" + rowID);
	}

	public By getCoordHomeCourseEnrollLinkLocator(String courseID) {
		return getCoordHomeCourseEnrollLinkLocator(getCoordHomeCourseRowID(courseID));
	}

	public By getCoordHomeCourseViewLinkLocator(String courseID) {
		return getCoordHomeCourseViewLinkLocator(getCoordHomeCourseRowID(courseID));
	}

	public By getCoordHomeCourseAddEvaluationLinkLocator(String courseID) {
		return getCoordHomeCourseAddEvaluationLinkLocator(getCoordHomeCourseRowID(courseID));
	}

	public By getCoordHomeCourseDeleteLinkLocator(String courseID) {
		return getCoordHomeCourseDeleteLinkLocator(getCoordHomeCourseRowID(courseID));
	}

	// Course list at course page
	/**
	 * Finds the rowID (at course page) number of a course based on course ID
	 * 
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

	public By getCoordCourseEnrollLinkLocator(int rowID) {
		return By.className("t_course_enroll" + rowID);
	}

	public By getCoordCourseViewLinkLocator(int rowID) {
		return By.className("t_course_view" + rowID);
	}

	public By getCoordCourseAddEvaluationLinkLocator(int rowID) {
		return By.className("t_course_add_eval" + rowID);
	}

	public By getCoordCourseDeleteLinkLocator(int rowID) {
		return By.className("t_course_delete" + rowID);
	}

	public By getCoordCourseEnrollLinkLocator(String courseID) {
		return getCoordCourseEnrollLinkLocator(getCourseRowID(courseID));
	}

	public By getCoordCourseViewLinkLocator(String courseID) {
		return getCoordCourseViewLinkLocator(getCourseRowID(courseID));
	}

	public By getCoordCourseAddEvaluationLinkLocator(String courseID) {
		return getCoordCourseAddEvaluationLinkLocator(getCourseRowID(courseID));
	}

	public By getCoordCourseDeleteLinkLocator(String courseID) {
		return getCoordCourseDeleteLinkLocator(getCourseRowID(courseID));
	}

	// Evaluation table at evaluation page
	/**
	 * Finds the rowID (at evaluation page) number of a specific evaluation
	 * based on the course ID and evaluation name
	 * 
	 * @param courseId
	 * @param evalName
	 * @return
	 */
	public int getEvaluationRowID(String courseId, String evalName) {
		int i = 0;
		while (i < countEvaluations()) {
			if (getEvaluationCourseID(i).equals(courseId)
					&& getEvaluationName(i).equals(evalName)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public By getCoordEvaluationViewResultsLinkLocator(int rowID) {
		return By.className("t_eval_view" + rowID);
	}

	public By getCoordEvaluationEditLinkLocator(int rowID) {
		return By.className("t_eval_edit" + rowID);
	}

	public By getCoordEvaluationDeleteLinkLocator(int rowID) {
		return By.className("t_eval_delete" + rowID);
	}

	public By getCoordEvaluationRemindLinkLocator(int rowID) {
		return By.className("t_eval_remind" + rowID);
	}

	public By getCoordEvaluationPublishLinkLocator(int rowID) {
		return By.className("t_eval_publish" + rowID);
	}

	public By getCoordEvaluationUnpublishLinkLocator(int rowID) {
		return By.className("t_eval_unpublish" + rowID);
	}

	// Evaluation table at homepage
	/**
	 * Finds the rowID (at evaluation page) number of a specific evaluation
	 * based on the course ID and evaluation name
	 * 
	 * @param course
	 * @param evalName
	 * @return
	 */
	public int getCoordHomeEvaluationRowID(String courseID, String evalName) {
		int courseRowID = getCoordHomeCourseRowID(courseID);
		if (courseRowID == -1)
			return -2;
		String template = "//div[@id='course%d']//table[@id='dataform']//tr[@id='evaluation%d']";
		int max = (Integer) selenium
				.getXpathCount("//div//table[@id='dataform']//tr");
		for (int id = 0; id < max; id++) {
			if (getElementText(
					By.xpath(String.format(template + "//td[1]", courseRowID,
							id))).equals(evalName)) {
				return id;
			}
		}
		return -1;
	}

	public By getCoordHomeEvaluationViewResultsLinkLocator(String courseID,
			String evalName) {
		return By.className("t_eval_view"
				+ getCoordHomeEvaluationRowID(courseID, evalName));
	}

	public By getCoordHomeEvaluationEditLinkLocator(String courseID,
			String evalName) {
		return By.className("t_eval_edit"
				+ getCoordHomeEvaluationRowID(courseID, evalName));
	}

	public By getCoordHomeEvaluationDeleteLinkLocator(String courseID,
			String evalName) {
		return By.className("t_eval_delete"
				+ getCoordHomeEvaluationRowID(courseID, evalName));
	}

	public By getCoordHomeEvaluationRemindLinkLocator(String courseID,
			String evalName) {
		return By.className("t_eval_remind"
				+ getCoordHomeEvaluationRowID(courseID, evalName));
	}

	public By getCoordHomeEvaluationPublishLinkLocator(String courseID,
			String evalName) {
		return By.className("t_eval_publish"
				+ getCoordHomeEvaluationRowID(courseID, evalName));
	}

	public By getCoordHomeEvaluationUnpublishLinkLocator(String courseID,
			String evalName) {
		return By.className("t_eval_unpublish"
				+ getCoordHomeEvaluationRowID(courseID, evalName));
	}

	// Add course
	public By coordCourseInputCourseID = By.id("courseid");
	public By coordCourseInputCourseName = By.id("coursename");
	public By coordCourseAddButton = By.id("btnAddCourse");
	public By coordCourseSortByIdButton = By.id("button_sortcourseid");
	public By coordCourseSortByNameButton = By.id("button_sortcoursename");

	// ------------------------------- Courses Table
	// ----------------------------- //
	/*
	 * In each row, the Cell containing course id has an HTML id attribute e.g.
	 * <tr><td id="courseid1">CS2103-TESTING</td>...</tr> The value depends on
	 * the original row number (it may be changed after sorting) Originally it
	 * is 1st row: "courseid0", 2nd row: "courseid1" and so on So at any time
	 * the lowest course ID will always have rowID 0, and so on.
	 */
	public By getCourseIDCell(int rowID) {
		return By.id("courseid" + rowID);
	}

	public By getCourseNameCell(int rowID) {
		return By.id("coursename" + rowID);
	}

	// Enrollment
	public By coordEnrollInfo = By.id("information");
	public By coordEnrollButton = By.id("button_enroll");
	public By coordEnrollBackButton = By.id("button_back");

	// Enrollment results
	public By coordEnrollResultError = By.className("enroll_result0");
	public By coordEnrollResultAdded = By.className("enroll_result1");
	public By coordEnrollResultModified = By.className("enroll_result2");
	public By coordEnrollResultUnmodified = By.className("enroll_result3");
	public By coordEnrollResultUnknown = By.className("enroll_result4");

	// Course details
	public By coordCourseDetailsCourseID = By.id("courseid");
	public By coordCourseDetailsCourseName = By.id("coursename");
	public By coordCourseDetailsTotalTeams = By.id("total_teams");
	public By coordCourseDetailsTotalStudents = By.id("total_students");

	public By coordCourseDetailSortByStudentName = By
			.id("button_sortstudentname");
	public By coordCourseDetailSortByTeamName = By.id("button_sortstudentteam");
	public By coordCourseDetailSortByStatus = By.id("button_sortstudentstatus");

	public int getStudentRowId(String studentName) {
		int studentCount = driver.findElements(By.className("student_row"))
				.size();
		for (int i = 0; i < studentCount; i++) {
			if (driver
					.findElement(
							By.xpath("//tr[@class='student_row' and @id='student"
									+ i
									+ "']//td[@id='"
									+ Common.PARAM_STUDENT_NAME + "']"))
					.getText().equals(studentName)) {
				return i;
			}
		}
		return -1;
	}

	public By getCoordCourseDetailStudentViewLinkLocator(int rowId) {
		return By.className("t_student_view" + rowId);
	}

	public By getCoordCourseDetailStudentEditLinkLocator(int rowId) {
		return By.className("t_student_edit" + rowId);
	}

	public By getCoordCourseDetailStudentResendLinkLocator(int rowId) {
		return By.className("t_student_resend" + rowId);
	}

	public By getCoordCourseDetailStudentDeleteLinkLocator(int rowId) {
		return By.className("t_student_delete" + rowId);
	}

	public By coordCourseDetailRemindButton = By.id("button_remind");
	public By coordCourseDetailBackButton = By.id("button_back");

	// Student details
	public By studentDetailName = By.id(Common.PARAM_STUDENT_NAME);
	public By studentDetailTeam = By.id(Common.PARAM_TEAM_NAME);
	public By studentDetailEmail = By.id(Common.PARAM_STUDENT_EMAIL);
	public By studentDetailNewEmail = By.id(Common.PARAM_NEW_STUDENT_EMAIL);
	public By studentDetailGoogle = By.id(Common.PARAM_USER_ID);
	public By studentDetailComment = By.id(Common.PARAM_COMMENTS);
	public By studentDetailKey = By.id(Common.PARAM_REGKEY);
	public By studentDetailBackButton = By.className("button_back");

	// Edit student
	public By coordCourseDetailsStudentEditSaveButton = By.id("button_submit");

	// Input on evaluation
	public By inputEvaluationName = By.id("evaluationname");
	public By inputPeerFeedbackStatus = By.id("commentsstatus");

	public By inputInstruction = By.id("instr");
	public By inputOpeningDate = By.id("start");
	public By inputOpeningTime = By.id("starttime");
	public By inputClosingDate = By.id("deadline");
	public By inputClosingTime = By.id("deadlinetime");

	public By inputTimeZone = By.id("timezone");
	public By inputGracePeriod = By.id("graceperiod");

	public By addEvaluationButton = By.id("button_submit");

	// Edit evaluation
	public By editEvaluationButton = By.id("button_submit");
	public By editEvaluationBackButton = By.id("button_back");

	// Evaluation Result
	public By resultSummaryRadio = By.id("radio_summary");
	public By resultReviewerRadio = By.id("radio_reviewer");
	public By resultRevieweeRadio = By.id("radio_reviewee");

	public By resultPublishButton = By.id("button_publish");
	public By resultBackButton = By.id("button_back");

	// Summary result
	public By resultTeamSorting = By.id("button_sortteamname");
	public By resultStudentSorting = By.id("button_sortname");
	public By resultClaimedSorting = By.id("button_sortclaimed");
	public By resultPerceivedSorting = By.id("button_sortperceived");
	public By resultDifferenceSorting = By.id("button_sortdiff");
	public By resultEditButton = By
			.id("button_editevaluationresultsbyreviewee");
	public By resultEditCancelButton = By.id("button_back");

	// Individual result
	public By resultIndividualEditButton = By.id("button_edit");

	// Detailed result
	public By resultTopButton = By.id("button_top");

	// Edit result
	public By coordEvaluationSaveButton = By.id("button_save");

	// Team forming
	public final String TEAMFORMINGSESSION_STATUS_AWAITING = "AWAITING";
	public By inputTeamName = By.id("teamName");
	public By inputTeamProfile = By.id("teamProfile");
	public By inputNewTeamName = By.id("newteamName");
	public By inputStudentProfileDetail = By.id("studentprofiledetail");
	public By createTeamFormingSessionButton = By
			.id("t_btnCreateTeamFormingSession");
	public By editTeamFormingSessionButton = By
			.id("button_editteamformingsession");
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

	public By evaluationCourseIDSorting = By.id("button_sortcourseid");
	public By evaluationNameSorting = By.id("button_sortname");

	@SuppressWarnings("unused")
	private void ____Student_UI_elements__________________________________() {
	}

	// ---------------------------------- Student
	// --------------------------------- //
	// Course details
	public By studentCourseDetailCourseID = By.id(Common.PARAM_COURSE_ID);
	public By studentCourseDetailCourseName = By.id(Common.PARAM_COURSE_NAME);
	public By studentCourseDetailTeamName = By.id(Common.PARAM_TEAM_NAME);
	public By studentCourseDetailStudentName = By.id(Common.PARAM_STUDENT_NAME);
	public By studentCourseDetailCoordinatorName = By
			.id(Common.PARAM_COORD_NAME);
	public By studentCourseDetailStudentEmail = By
			.id(Common.PARAM_STUDENT_EMAIL);
	public By studentCourseDetailStudentTeammates = By
			.id(Common.PARAM_TEAMMATES);

	// Student course
	public By studentInputRegKey = By.id("regkey");
	public By studentJoinCourseButton = By.id("button_join_course");

	// Student evaluation:
	public By studentSubmitEvaluationButton = By.id("button_submit");
	public By studentEvaluationBackButton = By.id("button_back");

	// --------------------------------- Homepage
	// --------------------------------- //
	// Course box
	public By getStudentViewLink(int rowID) {
		return By.className("t_course_view" + rowID);
	}

	// ------------------------------- Evaluation
	// --------------------------------- //
	public By getStudentDoEvaluationLink(int rowID) {
		return By.id("submitEvaluation" + rowID);
	}

	public By getStudentViewResultsLink(int rowID) {
		return By.id("viewEvaluationResults" + rowID);
	}

	public By getStudentEditEvaluationSubmissionLink(int rowID) {
		return By.id("editEvaluationSubmission" + rowID);
	}

	// Edit evaluation submission
	public By studentEvaluationCourseID = By.id(Common.PARAM_COURSE_ID);
	public By studentEvaluationEvaluationName = By
			.id(Common.PARAM_EVALUATION_NAME);
	public By studentEvaluationOpeningTime = By
			.id(Common.PARAM_EVALUATION_STARTTIME);
	public By studentEvaluationClosingTime = By
			.id(Common.PARAM_EVALUATION_DEADLINETIME);
	public By studentEvaluationInstructions = By
			.id(Common.PARAM_EVALUATION_INSTRUCTIONS);

	public By statusMessage = By.id("statusMessage");
	public By footer = By.id("contentFooter");

	public static final String FOOTER = "Best Viewed In Firefox, Chrome, Safari and Internet Explorer 8+. For Enquires:";

	/**
	 * Loads the TEAMMATES homepage into the browser
	 */
	public void init() {
		goToUrl(TestProperties.inst().TEAMMATES_URL);
	}

	@SuppressWarnings("unused")
	private void ____Login_and_logout_functions____________________________() {
	}

	/*------------------------------------------------------------------------
	 * UI Actions (login and logout)
	 * ---------------------------------------------------------------------- */

	/**
	 * Logs in as coordinator.
	 * 
	 * @page Homepage
	 */
	public void loginCoord(String username, String password) {
		if (loggedInUser.equals(username)) {
			System.out.println(username + " is already logged in.");
			return;
		}
		System.out.println("Logging in coordinator " + username + ".");

		// Logout first to make sure we will be in login page later
		goToUrl(TestProperties.inst().TEAMMATES_URL + Common.JSP_LOGOUT);

		// Login as coordinator
		goToUrl(TestProperties.inst().TEAMMATES_URL + Common.PAGE_LOGIN
				+ "?" + Common.PARAM_LOGIN_COORD);

		login(username, password, false);
		loggedInUser = username;
	}

	/**
	 * Logs in as student.
	 * 
	 * @page Homepage
	 */
	public void loginStudent(String username, String password) {
		if (loggedInUser.equals(username)) {
			System.out.println(username + " is already logged in.");
			return;
		}

		System.out.println("Logging in student " + username + ".");

		// Logout first to make sure we will be in login page later
		goToUrl(TestProperties.inst().TEAMMATES_URL + Common.JSP_LOGOUT);

		// Login as student
		goToUrl(TestProperties.inst().TEAMMATES_URL + Common.PAGE_LOGIN + "?"+Common.PARAM_LOGIN_STUDENT);

		login(username, password, false);

		loggedInUser = username;
	}

	/**
	 * Logs in as administrator.
	 * 
	 * @page Homepage
	 */
	public void loginAdmin(String username, String password) {
		if (loggedInUser.equals(username)) {
			System.out.println(username + " is already logged in.");
			return;
		}
		System.out.println("Logging in administrator " + username + ".");

		// Logout first to make sure we will be in login page later
		goToUrl(TestProperties.inst().TEAMMATES_URL + Common.JSP_LOGOUT);

		// Login as administrator
		goToUrl(TestProperties.inst().TEAMMATES_URL + Common.PAGE_LOGIN
				+ "?"+Common.PARAM_LOGIN_ADMIN);

		login(username, password, true);
		loggedInUser = username;
	}

	public void login(String email, String password, boolean isAdmin) {
		waitForPageLoad();
		if (isLocalLoginPage()) {
			fillString(By.id("email"), email);
			if (isAdmin) {
				selenium.check("id=isAdmin");
			}
			selenium.click("css=input[value='Log In']");
		} else if (isGoogleLoginPage()) {
			// Fill in login credentials
			fillString(By.id("Email"), email);
			fillString(By.id("Passwd"), password);
			// Click sign in button
			click(By.id("signIn"));
			// Wait and check for the main Coordinator page to see
			// if login was successful
			checkGoogleApplicationApproval();

		} else {
			fail("Not in the correct Login page");
			return;
		}
	}

	/**
	 * When authenticating for the first few times, it might ask for the
	 * "grant permission" page. If that's the case we simply click "Grant"
	 */
	private void checkGoogleApplicationApproval() {
		if (isElementPresent(By.id("approve_button"))) {
			clickWithWait(By.id("persist_checkbox"));
			clickWithWait(By.id("approve_button"));
			waitForPageLoad();
		}
	}

	/**
	 * Logs out (both for coordinator and student) Will return immediately if
	 * logout button cannot be found at current page
	 */
	public void logout() {
		System.out.println("Signing out.");
		goToUrl(TestProperties.inst().TEAMMATES_URL + Common.JSP_LOGOUT);
		loggedInUser = "";
	}

	@SuppressWarnings("unused")
	private void ____Basic_click_functions________________________________() {
	}

	/*------------------------------------------------------------------------
	 * UI Actions (click)
	 * ---------------------------------------------------------------------- */

	/**
	 * WebDriver clicks on an element. Fails on non-existence of element. See
	 * {@link #clickWithWait(By)} for version that waits until timeout before
	 * failing
	 * 
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
	 * WebDriver clicks on an element. Wait for the element to exist or timeout.
	 * 
	 * @param by
	 */
	public void clickWithWait(By by) {
		waitForElementPresent(by);
		WebElement element = driver.findElement(by);
		element.click();
	}

	/**
	 * To be used for clicks on a link that opens a new window. Switch to the
	 * new window.
	 * 
	 * @param link
	 * @param window
	 */
	public void clickAndSwitchToNewWindow(By link) {
		clickWithWait(link);

		String curWin = driver.getWindowHandle();
		for (String handle : driver.getWindowHandles()) {
			if (handle.equals(curWin))
				continue;
			selenium.selectWindow(handle);
			selenium.windowFocus();
		}
	}

	@SuppressWarnings("unused")
	private void ____Navigational_click_functions__________________________() {
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

	@SuppressWarnings("unused")
	private void ____Click_and_confirm_functions___________________________() {
	}

	/* -------------------- Clicks followed by confirmation ------------------ */

	/**
	 * Confirm a dialog box, i.e., clicking Yes, by overriding the alert box.
	 * So, no alert box will appear, it will directly assumed as Yes. This works
	 * for Chrome and Firefox. <br />
	 * Consequently, this method needs to be called before the click. The
	 * delete(window.confirm) ensures that this overriding happens only once. <br />
	 * Does not wait for any further action to complete (i.e., returns
	 * immediately after confirming)
	 * 
	 */
	public void clickAndConfirm(By by) throws NoAlertException {
		/*
		 * Huy: I have no idea why the driver.switchTo().alert() approach
		 * doesn't work even in Firefox (it supposed to!). This is a workaround
		 * to press Yes in the confirmation box. Same for function below for No.
		 * 
		 * Aldrian: I tried driver.switchTo().alert() approach in my local
		 * Firefox and it worked. But for more general usability I removed the
		 * old one and use this one instead.
		 */
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.confirm = function(msg){ delete(window.confirm); return true;};");
		clickWithWait(by);

		if ((Boolean) js
				.executeScript("return eval(window.confirm).toString()==eval(function(msg){ delete(window.confirm); return true;}).toString()")) {
			// This means the click does not generate alert box
			js.executeScript("delete(window.confirm)");
			throw new NoAlertException(by.toString());
		}
		// Make sure it's deleted. Deleting twice does not hurt
		js.executeScript("delete(window.confirm)");
	}

	/**
	 * Cancels a dialog box, i.e., clicking No, by overriding the alert box. So,
	 * no alert box will appear, it will directly assumed as No. This works for
	 * Chrome and Firefox. <br />
	 * Consequently, this method needs to be called before the click. The
	 * delete(window.confirm) ensures that this overriding happens only once. <br />
	 * Does not wait for any further action to complete (i.e., returns
	 * immediately after cancelling)
	 * 
	 */
	public void clickAndCancel(By by) throws NoAlertException {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.confirm = function(msg){ delete(window.confirm); return false;};");
		clickWithWait(by);

		if ((Boolean) js
				.executeScript("return eval(window.confirm).toString()==eval(function(msg){ delete(window.confirm); return false;}).toString()")) {
			// This means the click does not generate alert box
			js.executeScript("delete(window.confirm)");
			throw new NoAlertException(by.toString());
		}
		// Make sure it's deleted. Deleting twice does not hurt
		js.executeScript("delete(window.confirm)");
	}

	/**
	 * Clicks and confirms Delete of a course at a particular rowID.
	 * Pre-condition: Should be at Coordinator Homepage
	 * 
	 * @param rowID
	 */
	public void clickCoordHomeCourseDeleteAndConfirm(int rowID) {
		clickAndConfirm(getCoordHomeCourseDeleteLinkLocator(rowID));
	}

	/**
	 * Click and cancels Delete of a particular course of the coordinator.
	 * Pre-condition: Should be at Coordinator Homepage
	 * 
	 * @param rowID
	 */
	public void clickCoordHomeCourseDeleteAndCancel(String courseID) {
		clickAndCancel(getCoordHomeCourseDeleteLinkLocator(courseID));
	}

	/**
	 * Clicks and confirms Delete of a course at a particular rowID.
	 * Pre-condition: Should be at Coordinator Course Page
	 * 
	 * @param rowID
	 */
	public void clickCoordCourseDeleteAndConfirm(int rowID) {
		clickAndConfirm(getCoordCourseDeleteLinkLocator(rowID));
	}

	/**
	 * Clicks and confirms Delete of a particular course. Pre-condition: Should
	 * be at Coordinator Course Page
	 * 
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
	 * 
	 * @param rowID
	 */
	public void clickCoordCourseDeleteAndCancel(int rowID) {
		clickAndCancel(getCoordCourseDeleteLinkLocator(rowID));
	}

	/**
	 * Clicks and cancels Delete of a particular course. Pre-condition: Should
	 * be at Coordinator Course Page
	 * 
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
	 * 
	 * @param rowID
	 */
	public void clickCoordCourseDetailStudentDeleteAndConfirm(int rowID) {
		clickAndConfirm(getCoordCourseDetailStudentDeleteLinkLocator(rowID));
	}

	/**
	 * Clicks and confirms Delete a particular student. Pre-condition: Should be
	 * in Course detail page
	 * 
	 * @param student
	 */
	public void clickCoordCourseDetailStudentDeleteAndConfirm(String student) {
		int rowID = getStudentRowId(student);
		if (rowID > -1) {
			clickCoordCourseDetailStudentDeleteAndConfirm(rowID);
		} else {
			fail("Student not found in this course.");
		}
	}

	/**
	 * Clicks and confirms Delete a student at a particular rowID.
	 * Pre-condition: Should be in Course detail page
	 * 
	 * @param rowID
	 */
	public void clickCoordCourseDetailStudentDeleteAndCancel(int rowID) {
		clickAndCancel(getCoordCourseDetailStudentDeleteLinkLocator(rowID));
	}

	/**
	 * Clicks and confirms Delete a particular student. Pre-condition: Should be
	 * in Course detail page
	 * 
	 * @param student
	 */
	public void clickCoordCourseDetailStudentDeleteAndCancel(String student) {
		int rowID = getStudentRowId(student);
		if (rowID > -1) {
			clickCoordCourseDetailStudentDeleteAndCancel(rowID);
		} else {
			fail("Student not found in this course.");
		}
	}

	/**
	 * Clicks and confirms Delete of an evaluation at a particular rowID.
	 * Pre-condition: Should be at Evaluation list page
	 * 
	 * @param rowID
	 */
	public void clickCoordEvaluationDeleteAndConfirm(int rowID) {
		clickAndConfirm(getCoordEvaluationDeleteLinkLocator(rowID));
	}

	/**
	 * Clicks and confirms Delete of a particular evaluation. Pre-condition:
	 * Should be at Evaluation list page
	 * 
	 * @param courseId
	 * @param evalName
	 */
	public void clickCoordEvaluationDeleteAndConfirm(String courseId,
			String evalName) {
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
	 * 
	 * @param rowID
	 */
	public void clickCoordEvaluationDeleteAndCancel(int rowID) {
		clickAndCancel(getCoordEvaluationDeleteLinkLocator(rowID));
	}

	/**
	 * Clicks and cancels Delete of a particular evaluation. Pre-condition:
	 * Should be at Evaluation list page
	 * 
	 * @param courseId
	 * @param evalName
	 */
	public void clickCoordEvaluationDeleteAndCancel(String courseId,
			String evalName) {
		int rowID = getEvaluationRowID(courseId, evalName);
		if (rowID > -1) {
			clickCoordEvaluationDeleteAndCancel(rowID);
		} else {
			fail("Evaluation not found.");
		}
	}

	/**
	 * Clicks and confirms on Publish button of an evaluation at particular
	 * rowID at evaluation page as coordinator. Pre-condition: Should be at
	 * Evaluation list page
	 * 
	 * @param rowID
	 */
	public void clickCoordEvaluationPublishAndConfirm(int rowID) {
		clickAndConfirm(getCoordEvaluationPublishLinkLocator(rowID));
	}

	/**
	 * Clicks and confirms on Publish button of a particular evaluation at
	 * evaluation page as coordinator. Pre-condition: Should be at Evaluation
	 * list page
	 * 
	 * @param courseId
	 * @param evalName
	 */
	public void clickCoordEvaluationPublishAndConfirm(String courseId,
			String evalName) {
		int rowID = getEvaluationRowID(courseId, evalName);
		if (rowID > -1) {
			clickCoordEvaluationPublishAndConfirm(rowID);
		} else {
			fail("Evaluation not found.");
		}
	}

	/**
	 * Clicks and cancels Publish of results of an evaluation at particular
	 * rowID in the page as the coordinator. Pre-condition: Should be at
	 * Evaluation list page
	 * 
	 * @param rowID
	 */
	public void clickCoordEvaluationPublishAndCancel(int rowID) {
		clickAndCancel(getCoordEvaluationPublishLinkLocator(rowID));
	}

	/**
	 * Clicks and cancels Publish of results of a particular evaluation in a
	 * specific course of the coordinator. Pre-condition: Should be at
	 * Evaluation list page
	 * 
	 * @param courseID
	 * @param evalName
	 */
	public void clickCoordEvaluationPublishAndCancel(String courseID,
			String evalName) {
		int rowID = getEvaluationRowID(courseID, evalName);

		if (rowID > -1) {
			clickCoordEvaluationPublishAndCancel(rowID);
		} else {
			fail("Evaluation not found.");
		}
	}

	/**
	 * Clicks and confirms on Unpublish button of an evaluation at particular
	 * rowID. Pre-condition: Should be at Evaluation list page
	 * 
	 * @param rowID
	 */
	public void clickCoordEvaluationUnpublishAndConfirm(int rowID) {
		clickAndConfirm(getCoordEvaluationUnpublishLinkLocator(rowID));
	}

	/**
	 * Clicks and confirms on Unpublish button of a particular evaluation.
	 * Pre-condition: Should be at Evaluation list page
	 * 
	 * @param courseId
	 * @param evalName
	 */
	public void clickCoordEvaluationUnpublishAndConfirm(String courseId,
			String evalName) {
		int rowID = getEvaluationRowID(courseId, evalName);
		if (rowID > -1) {
			clickCoordEvaluationUnpublishAndConfirm(rowID);
		} else {
			fail("Evaluation not found.");
		}
	}

	/**
	 * Clicks and cancels Unpublish of results of an evaluation at a particular
	 * rowID in a specific course of the coordinator. Pre-condition: Should be
	 * at Evaluation list page
	 * 
	 * @param rowID
	 */
	public void clickCoordEvaluationUnpublishAndCancel(int rowID) {
		clickAndConfirm(getCoordEvaluationPublishLinkLocator(rowID));
	}

	/**
	 * Clicks and cancels Unpublish of results of a particular evaluation in a
	 * specific course of the coordinator. Pre-condition: Should be at
	 * Evaluation list page
	 * 
	 * @param courseID
	 * @param evalName
	 */
	public void clickCoordEvaluationUnpublishAndCancel(String courseID,
			String evalName) {
		int rowID = getEvaluationRowID(courseID, evalName);

		if (rowID > -1) {
			clickCoordEvaluationUnpublishAndCancel(rowID);
		} else {
			fail("Evaluation not found.");
		}
	}

	/**
	 * Clicks and confirms Remind to do an evaluation at a particular rowID in a
	 * specific course of the coordinator. Pre-condition: Should be at
	 * Evaluation list page
	 * 
	 * @param rowID
	 */
	public void clickCoordEvaluationRemindAndConfirm(int rowID) {
		clickAndConfirm(getCoordEvaluationRemindLinkLocator(rowID));
	}

	/**
	 * Clicks and confirms Remind to do a particular evaluation in a specific
	 * course of the coordinator. Pre-condition: Should be at Evaluation list
	 * page
	 * 
	 * @param courseID
	 * @param evalName
	 */
	public void clickCoordEvaluationRemindAndConfirm(String courseId,
			String evalName) {
		int rowID = getEvaluationRowID(courseId, evalName);
		if (rowID > -1) {
			clickCoordEvaluationRemindAndConfirm(rowID);
		} else {
			fail("Evaluation not found.");
		}
	}

	/**
	 * Clicks and cancels Remind to do an evaluation at a particular rowID in a
	 * specific course of the coordinator. Pre-condition: Should be at
	 * Evaluation list page
	 * 
	 * @param rowID
	 */
	public void clickCoordEvaluationRemindAndCancel(int rowID) {
		clickAndCancel(getCoordEvaluationRemindLinkLocator(rowID));
	}

	/**
	 * Clicks and cancels Remind to do a particular evaluation in a specific
	 * course of the coordinator. Pre-condition: Should be at Evaluation list
	 * page
	 * 
	 * @param courseID
	 * @param evalName
	 */
	public void clickCoordEvaluationRemindAndCancel(String courseId,
			String evalName) {
		int rowID = getEvaluationRowID(courseId, evalName);
		if (rowID > -1) {
			clickCoordEvaluationRemindAndCancel(rowID);
		} else {
			fail("Evaluation not found.");
		}
	}

	/**
	 * Clicks on the enroll link on a specific rowID. Does not verify the new
	 * page.
	 * 
	 * @param rowID
	 */
	public void clickCoordCourseEnroll(int rowID) {
		clickWithWait(getCoordCourseEnrollLinkLocator(rowID));
	}

	/**
	 * Clicks on the enroll link of a specific course. Does not verify the new
	 * page.
	 * 
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
		By link = By.className("t_student_view" + rowID);
		clickWithWait(link);
	}

	public void clickCoordCourseDetailStudentView(String student) {
		int rowID = getStudentRowId(student);
		if (rowID > -1) {
			clickCoordCourseDetailStudentView(rowID);
		} else {
			fail("Student not found in this course.");
		}
	}

	public void clickCoordCourseDetailStudentEdit(int rowID) {
		By link = By.className("t_student_edit" + rowID);
		clickWithWait(link);
	}

	public void clickCoordCourseDetailStudentEdit(String student) {
		int rowID = getStudentRowId(student);
		if (rowID > -1) {
			clickCoordCourseDetailStudentEdit(rowID);
		} else {
			fail("Student not found in this course.");
		}
	}

	public void clickCoordCourseDetailRemind(int rowID) {
		By link = By.className("t_student_resend" + rowID);
		clickWithWait(link);
	}

	public void clickCoordCourseDetailRemind(String student) {
		int rowID = getStudentRowId(student);
		if (rowID > -1) {
			clickCoordCourseDetailRemind(rowID);
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

	@SuppressWarnings("unused")
	private void ____Just_click_functions__________________________________() {
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

	public void clickCoordReviewerSummaryView(int rowID) {
		clickWithWait(getReviewerSummaryView(rowID));
	}

	public void clickCoordReviewerSummaryEdit(int rowID) {
		clickWithWait(getReviewerSummaryEdit(rowID));
	}

	/**
	 * Clicks the sort course by name button. Waits for the element to appear.
	 * Pre-condition: Should be at Course Page
	 */
	public void clickCoordCourseSortByNameButton() {
		clickWithWait(coordCourseSortByNameButton);
	}

	/**
	 * Clicks the sort course by ID button. Waits for the element to appear.
	 * Pre-condition: Should be at Course Page
	 */
	public void clickCoordCourseSortByIdButton() {
		clickWithWait(coordCourseSortByIdButton);
	}

	// --------------------------------- Students
	// -------------------------------- //

	/**
	 * Returns courseID from the table at specific rowID as student. Waits until
	 * the element exists or timeout. Pre-condition: Should be at home page.
	 * 
	 * @param rowID
	 * @return
	 */
	public String studentGetCourseID(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".0");
	}

	/**
	 * Returns course name from the table at specific rowID as student. Waits
	 * until the element exists or timeout. Pre-condition: Should be at course
	 * page.
	 * 
	 * @param rowID
	 * @return
	 */
	public String studentGetCourseName(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".1");
	}

	/**
	 * Returns the team name for specific rowID as student. Waits until the
	 * element exists or timeout. Pre-condition: Should be at course page.
	 * 
	 * @param rowID
	 * @return
	 */
	public String studentGetCourseTeamName(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".2");
	}

	/**
	 * Clicks on the view course for specific rowID as student. Waits until the
	 * element exists or timeout. Pre-condition: Should be at course page.
	 * 
	 * @param rowID
	 */
	public void studentClickCourseView(int rowID) {
		By link = By
				.xpath(String
						.format("//div[@id='studentCourseTable']//table[@id='dataform']//tr[%d]//td[%d]//a[1]",
								rowID + 2, 4));
		clickWithWait(link);
	}

	/**
	 * Clicks on doEvaluation link for evaluation at specific rowID. Does not
	 * wait for the new page to load.
	 * 
	 * @param rowID
	 */
	public void studentClickEvaluationDo(int rowID) {
		clickWithWait(By.id("doEvaluation" + rowID));
	}

	/**
	 * Clicks on editEvaluation link for evaluation at specific rowID. Does not
	 * verify that the new page is loaded correctly before returning.
	 * Pre-condition: Should be at evaluation page.
	 * 
	 * @param rowID
	 */
	public void studentClickEvaluationEdit(int rowID) {
		clickWithWait(getStudentEditEvaluationSubmissionLink(rowID));
	}

	/**
	 * Returns the course ID of evaluation for specific rowID. Waits until the
	 * element exists or timeout. Pre-condition: Should be at evaluation page.
	 * 
	 * @param rowID
	 * @return
	 */
	public String studentGetEvaluationCourseID(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".1");
	}

	/**
	 * Returns the evaluation name for specific rowID. Waits until the element
	 * exists or timeout. Pre-condition: Should be at evaluation page.
	 * 
	 * @param rowID
	 * @return
	 */
	public String studentGetEvaluationName(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".2");
	}

	/**
	 * Returns the evaluation status for specific rowID. Waits until the element
	 * exists or timeout. Pre-condition: Should be at evaluation page.
	 * 
	 * @param rowID
	 * @return
	 */
	public String studentGetEvaluationStatus(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".3");
	}

	/**
	 * Returns the total number of courses that current student takes. Waits
	 * until the element exists or timeout. Pre-condition: Should be at course
	 * page.
	 * 
	 * @return
	 */
	public int studentCountTotalCourses() {
		waitForElementPresent(By.id("dataform"));
		WebElement dataform = driver.findElement(By.id("dataform"));
		return dataform.findElements(By.tagName("tr")).size();
	}

	/**
	 * Returns the total number of past evaluations for current student. Waits
	 * for the element exists or timeout. Pre-condition: Should be at evaluation
	 * page.
	 * 
	 * @return
	 */
	public int studentCountTotalEvaluations() {
		waitForElementPresent(By.id("dataform"));
		if (getElementText(
				By.xpath(String
						.format("//div[@id='studentPastEvaluations']//table[@id='dataform']//tbody//tr[2]//td[1]")))
				.isEmpty()) {
			return 0;
		} else {
			return selenium
					.getXpathCount(
							"//div[@id='studentPastEvaluations']//table[@id='dataform']/tbody/tr")
					.intValue() - 1;
		}
	}

	/**
	 * Returns the evaluation result claimed points as seen in the UI by
	 * student. Waits for the element exists or timeout. Pre-condition: Should
	 * be at evaluation page.
	 * 
	 * @return
	 */
	public String studentGetEvaluationResultClaimedPoints() {
		waitForElementPresent(By.id("studentEvaluationResults"));
		return getElementText(By
				.xpath(String
						.format("//div[@id='studentEvaluationResults']//table[@class='result_studentform']//tr[%d]//td[%d]",
								3, 2)));
	}

	/**
	 * Returns the evaluation result perceived points as seen in the UI by
	 * student. Waits for the element exists or timeout. Pre-condition: Should
	 * be at evaluation page.
	 * 
	 * @return
	 */
	public String studentGetEvaluationResultPerceivedPoints() {
		waitForElementPresent(By.id("studentEvaluationResults"));
		return getElementText(By
				.xpath(String
						.format("//div[@id='studentEvaluationResults']//table[@class='result_studentform']//tr[%d]//td[%d]",
								4, 2)));
	}

	/**
	 * Verifies whether a student get the feedback from another student. This is
	 * done by checking whether a specific text exists in the page. Does not
	 * wait for any element to present.
	 * 
	 * @param fromStudent
	 * @param toStudent
	 * @return
	 */
	public boolean studentVerifyGetFeedbackFromOthers(String fromStudent,
			String toStudent) {
		return selenium.isTextPresent(String.format(
				"This is a public comment from %s to %s", fromStudent,
				toStudent));
	}

	/**
	 * Adds a new course with specified courseID and coursename. Does not wait
	 * until the course is added (i.e., immediately returns after clicking
	 * "Add Course") Pre-condition: Should be at coordinator course page.
	 * 
	 * @param courseid
	 * @param coursename
	 */
	public void addCourse(String courseid, String coursename) {
		fillString(coordCourseInputCourseID, courseid);
		fillString(coordCourseInputCourseName, coursename);
		clickWithWait(coordCourseAddButton);
	}

	/**
	 * Add an evaluation through the UI. Pre-condition: Should be at Evaluation
	 * add page
	 * 
	 * @param courseID
	 * @param evalName
	 * @param dateValue
	 *            Format: (YYYY,MM,DD)
	 * @param nextTimeValue
	 *            Format: HHMM
	 * @param comments
	 * @param instructions
	 * @param gracePeriod
	 */
	public void addEvaluation(String courseID, String evalName, Date startTime,
			Date endTime, boolean p2pEnabled, String instructions,
			Integer gracePeriod) {

		// Fill in the evaluation name
		fillString(inputEvaluationName, evalName);

		// Select the course
		clickWithWait(coordCourseInputCourseID);
		
		selectDropdownByValue(coordCourseInputCourseID, courseID);
		
		// Select start date
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("$('#" + Common.PARAM_EVALUATION_START
				+ "')[0].value='" + Common.formatDate(startTime) + "';");
		selectDropdownByValue(inputOpeningTime,
				Common.convertToOptionValueInTimeDropDown(startTime));

		// Select deadline date
		js.executeScript("$('#" + Common.PARAM_EVALUATION_DEADLINE
				+ "')[0].value='" + Common.formatDate(endTime) + "';");
		selectDropdownByValue(inputClosingTime,
				Common.convertToOptionValueInTimeDropDown(endTime));

		// Allow P2P comment
		if (p2pEnabled) {
			clickWithWait(By.id("commentsstatus_enabled"));
		} else {
			clickWithWait(By.id("commentsstatus_disabled"));
		}

		// Fill in instructions
		fillString(inputInstruction, instructions);

		// Select grace period
		selectDropdownByValue(inputGracePeriod, Integer.toString(gracePeriod));

		// Submit the form
		clickWithWait(addEvaluationButton);
	}

	/**
	 * Edit an evaluation through the UI. Pre-condition: Should be at Evaluation
	 * page.
	 * 
	 * @param startTime
	 *            Format: (YYYY,MM,DD)
	 * @param nextTimeValue
	 *            Format: HHMM
	 * @param comments
	 * @param instructions
	 * @param gracePeriod
	 */
	public void editEvaluation(Date startTime, Date endTime,
			boolean p2pEnabled, String instructions, Integer gracePeriod) {
		// Select start date
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("$('#" + Common.PARAM_EVALUATION_START
				+ "')[0].value='" + Common.formatDate(startTime) + "';");
		selectDropdownByValue(inputOpeningTime,
				Common.convertToOptionValueInTimeDropDown(startTime));

		// Select deadline date
		js.executeScript("$('#" + Common.PARAM_EVALUATION_DEADLINE
				+ "')[0].value='" + Common.formatDate(endTime) + "';");
		selectDropdownByValue(inputClosingTime,
				Common.convertToOptionValueInTimeDropDown(endTime));

		// Allow P2P comment
		if (p2pEnabled) {
			clickWithWait(By.id("commentsstatus_enabled"));
		} else {
			clickWithWait(By.id("commentsstatus_disabled"));
		}

		// Fill in instructions
		fillString(inputInstruction, instructions);

		// Select grace period
		selectDropdownByValue(inputGracePeriod, Integer.toString(gracePeriod));
		// Submit the form
		clickWithWait(editEvaluationButton);
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
			By by = By.xpath(String.format(
					"//table[@id='dataform']//tr[%d]//a[4]", 2));
			waitForElementPresent(by);
			clickAndConfirm(by);
			waitForElementPresent(By.id("dataform tr"));
		}
	}

	/**
	 * Returns the locator of a specific course by the course ID. Waits until
	 * the element exists or timeout.
	 * 
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
	 * Reads through list of courses in the UI (i.e., in form of HTML table) and
	 * returns the course name for a specified course id.<br />
	 * Error if specified course id is not found.
	 * 
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
	 * 
	 * @param courseID
	 * @return
	 */
	public int getCourseIDCount(String courseID) {
		int result = 0;
		for (int i = 0; i < countCourses(); i++) {
			if (getElementText(getCourseIDCell(i)).equals(courseID)) {
				result++;
			}
		}
		return result;
	}

	/**
	 * Returns number of teams in specific rowID. Waits until element exists or
	 * timeout. Pre-condition: Should be in the course page.
	 * 
	 * @param rowID
	 * @return
	 */
	public String getCourseNumberOfTeams(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;// rowID starts from 0
		return selenium.getTable("id=dataform." + rowID + ".2");
	}

	/**
	 * Returns number of teams in specific course. Waits until element exists or
	 * timeout. Pre-condition: Should be in the course page.
	 * 
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
	 * Returns total number of students in specific rowID. Waits until element
	 * exists or timeout. Pre-condition: Should be in the course page.
	 * 
	 * @param rowID
	 * @return
	 */
	public String getCourseTotalStudents(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".3");
	}

	/**
	 * Returns total number of students in specific course. Waits until element
	 * exists or timeout. Pre-condition: Should be in the course page.
	 * 
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
	 * Returns total number of unregistered students in specific course. Waits
	 * until element exists or timeout. Pre-condition: Should be in the course
	 * page.
	 * 
	 * @param rowID
	 * @return
	 */
	public String getCourseUnregisteredStudents(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".4");
	}

	/**
	 * Returns total number of unregistered students in specific course. Waits
	 * until element exists or timeout. Pre-condition: Should be in the course
	 * page.
	 * 
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
		return By
				.xpath(String
						.format("//div[@class='result_team']//table[@id='dataform']//tbody//tr[%d]//td[%d]",
								rowID, col));
	}

	@SuppressWarnings("unused")
	private void ____Counting_functions__________________________________() {
	}

	/*
	 * -----------------------------------------------------------------------
	 * Counting functions Currently all five are the same, which is looking for
	 * number of rows in dataform table. Can be modified so that each function
	 * is specific for its purpose
	 * ---------------------------------------------------------------------
	 */

	public int countCourses() {
		waitForElementPresent(By.className("courses_row"));
		return driver.findElements(By.className("courses_row")).size();
	}

	public int countHomeCourses() {
		waitForElementPresent(By.className("home_courses_div"));
		return driver.findElements(By.className("home_courses_div")).size();
	}

	public int countStudents() {
		waitForElementPresent(By.className("student_row"));
		return driver.findElements(By.className("student_row")).size();
	}

	public int countEvaluations() {
		waitForElementPresent(By.className("evaluations_row"));
		return driver.findElements(By.className("evaluations_row")).size();
	}

	public int countHomeEvaluations() {
		waitForElementPresent(By.className("home_evaluations_row"));
		return driver.findElements(By.className("home_evaluations_row")).size();
	}

	public String getEvaluationCourseID(int rowID) {
		waitForElementPresent(By.id("dataform"));
		return selenium.getTable("id=dataform." + (rowID + 1) + ".0");
	}

	public String getEvaluationName(int rowID) {
		waitForElementPresent(By.id("dataform"));
		return selenium.getTable("id=dataform." + (rowID + 1) + ".1");
	}

	public String getEvaluationStatus(int rowID) {
		waitForElementPresent(By.id("dataform"));
		return selenium.getTable("id=dataform." + (rowID + 1) + ".2");
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

	public String getEvaluationResponseRate(int rowID) {
		waitForElementPresent(By.id("dataform"));
		rowID++;
		return selenium.getTable("id=dataform." + rowID + ".3");
	}

	public String getEvaluationResponse(String courseId, String evalName) {
		int rowID = getEvaluationRowID(courseId, evalName);
		if (rowID > -1) {
			return getEvaluationResponseRate(rowID);
		} else {
			fail("Evaluation not found.");
			return null;
		}
	}

	public String getHomeEvaluationName(String courseID, String courseName) {
		waitForElementPresent(By.id("dataform"));
		int courseRowID = getCoordHomeCourseRowID(courseID);
		int evaluationRowID = getCoordHomeEvaluationRowID(courseID, courseName);
		return getElementText(By
				.xpath(String
						.format("//div[@id='course%d']//table[@id='dataform']//tr[@id='evaluation%d']/td[1]",
								courseRowID, evaluationRowID)));
	}

	public String getHomeEvaluationStatus(String courseID, String courseName) {
		waitForElementPresent(By.id("dataform"));
		int courseRowID = getCoordHomeCourseRowID(courseID);
		int evaluationRowID = getCoordHomeEvaluationRowID(courseID, courseName);
		return getElementText(By
				.xpath(String
						.format("//div[@id='course%d']//table[@id='dataform']//tr[@id='evaluation%d']/td[2]",
								courseRowID, evaluationRowID)));
	}

	@SuppressWarnings("unused")
	private void ____Locator_funcations__________________________________() {
	}

	/*
	 * ----------------------------------------------------------------------
	 * Locator functions Returns the locator (By object) of some links/objects
	 * on the page To get the rowID, call getStudentRowID method
	 * --------------------------------------------------------------------
	 */
	// Reviewer summary
	public By getReviewerSummaryView(int rowID) {
		return By.id("viewEvaluationResults" + rowID);
	}

	public By getReviewerSummaryEdit(int rowID) {
		return By.id("editEvaluationResults" + rowID);
	}

	// Reviewer individual
	public By getReviewerIndividualToStudent(int rowID) {
		return By
				.xpath(String
						.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]",
								rowID + 4, 1));
	}

	public By getReviewerIndividualToStudentPoint(int rowID) {
		return By
				.xpath(String
						.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]",
								rowID + 4, 2));
	}

	// Reviewee individual
	public By getRevieweeIndividualFromStudent(int rowID) {
		return By
				.xpath(String
						.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]",
								rowID + 4, 1));
	}

	public By getRevieweeIndividualFromStudentPoint(int rowID) {
		return By
				.xpath(String
						.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//tr[%d]//td[%d]",
								rowID + 4, 2));
	}

	// Reviewer detail
	public By getReviewerDetailClaimed(int teamIdx, int rowID) {
		return By
				.xpath(String
						.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]",
								teamIdx, rowID, 2));
	}

	public By getReviewerDetailPerceived(int teamIdx, int rowID) {
		return By
				.xpath(String
						.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]",
								teamIdx, rowID, 3));
	}

	public By getReviewerDetailToStudent(int teamIdx, int studentIdx, int rowID) {
		return By
				.xpath(String
						.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]",
								teamIdx, studentIdx, rowID + 4, 1));
	}

	public By getReviewerDetailToStudentPoint(int teamIdx, int studentIdx,
			int rowID) {
		return By
				.xpath(String
						.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]",
								teamIdx, studentIdx, rowID + 4, 2));
	}

	// Reviewee detail
	public By getRevieweeDetailClaimed(int teamIdx, int rowID) {
		return By
				.xpath(String
						.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]",
								teamIdx, rowID, 2));
	}

	public By getRevieweeDetailPerceived(int teamIdx, int rowID) {
		return By
				.xpath(String
						.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//thead//th[%d]",
								teamIdx, rowID, 3));
	}

	public By getRevieweeDetailFromStudent(int teamIdx, int studentIdx,
			int rowID) {
		return By
				.xpath(String
						.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]",
								teamIdx, studentIdx, rowID + 4, 1));
	}

	public By getRevieweeDetailFromStudentPoint(int teamIdx, int studentIdx,
			int rowID) {
		return By
				.xpath(String
						.format("//div[@id='coordinatorEvaluationSummaryTable']//div[@id='detail']//div[%d]//table[%d]//tr[%d]//td[%d]",
								teamIdx, studentIdx, rowID + 4, 2));
	}

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
		return getElementText(By
				.xpath(String
						.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]",
								2)));
	}

	public String getReviewerIndividualPerceivedPoint() {
		return getElementText(By
				.xpath(String
						.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]",
								3)));
	}

	// reviewee individual:
	public String getRevieweeIndividualClaimedPoint() {
		return getElementText(By
				.xpath(String
						.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]",
								2)));
	}

	public String getRevieweeIndividualPerceivedPoint() {
		return getElementText(By
				.xpath(String
						.format("//div[@id='coordinatorEvaluationSummaryTable']//table[@class='result_table']//thead//th[%d]",
								3)));
	}

	/**
	 * Returns the rowID of the specified student for use in edit submission
	 * both in coordinator page and student page.<br />
	 * This works by looking up the name in the section title. To get the row ID
	 * for the student itself (in case of student self submission which has no
	 * name in its title), put "self" as the student name.
	 * 
	 * @param studentNameOrSelf
	 * @return
	 */
	public int getStudentRowIdInEditSubmission(String studentNameOrSelf) {
		int max = driver.findElements(By.className("reportheader")).size();
		for (int i = 0; i < max; i++) {
			if (driver.findElement(By.id("sectiontitle" + i)).getText()
					.toUpperCase().contains(studentNameOrSelf.toUpperCase())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @page Edit evaluation result
	 * @param rowID
	 * @return
	 */
	public By getSubmissionPoint(int rowID) {
		return By.id(Common.PARAM_POINTS + rowID);
	}

	/**
	 * @page Edit evaluation result
	 * @param rowID
	 * @param points
	 */
	public void setSubmissionPoint(int rowID, String points) {
		selectDropdownByValue(By.id(Common.PARAM_POINTS + rowID), points);
	}

	/**
	 * @page Edit evaluation result
	 * @param rowID
	 * @return
	 */
	public By getSubmissionJustification(int rowID) {
		return By.id(Common.PARAM_JUSTIFICATION + rowID);
	}

	/**
	 * @page Edit evaluation result
	 * @param rowID
	 * @param justification
	 */
	public void setSubmissionJustification(int rowID, String justification) {
		fillString(By.id(Common.PARAM_JUSTIFICATION + rowID), justification);
	}

	/**
	 * @page Edit evaluation result
	 * @param rowID
	 * @return
	 */
	public By getSubmissionComments(int rowID) {
		return By.id(Common.PARAM_COMMENTS + rowID);
	}

	/**
	 * @page Edit evaluation result
	 * @param rowID
	 * @param comments
	 */
	public void setSubmissionComments(int rowID, String comments) {
		fillString(By.id(Common.PARAM_COMMENTS + rowID), comments);
	}

	@SuppressWarnings("unused")
	private void ____Test_setup_funcations__________________________________() {
	}

	/*
	 * -----------------------------------------------------------------------
	 * Functions dealing with UI testing setup
	 * ----------------------------------------------------------------------
	 */
	/**
	 * Start Chrome service, return service instance
	 * 
	 * @return the service instance
	 */
	private ChromeDriverService startChromeDriverService() {
		chromeService = new ChromeDriverService.Builder()
				.usingChromeDriverExecutable(
						new File(TestProperties.getChromeDriverPath()))
				.usingAnyFreePort().build();
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

		if (TestProperties.inst().BROWSER.equals("htmlunit")) {
			System.out.println("Using HTMLUnit.");

			setDriver(new HtmlUnitDriver());
			selenium = new WebDriverBackedSelenium(driver,
					TestProperties.inst().TEAMMATES_URL);

		} else if (TestProperties.inst().BROWSER.equals("firefox")) {
			System.out.println("Using Firefox.");
			setDriver(new FirefoxDriver());
			selenium = new WebDriverBackedSelenium(driver,
					TestProperties.inst().TEAMMATES_URL);

		} else if (TestProperties.inst().BROWSER.equals("chrome")) {

			System.out.println("Using Chrome");

			// Use technique here:
			// http://code.google.com/p/selenium/wiki/ChromeDriver
			ChromeDriverService service = startChromeDriverService();
			setDriver(new RemoteWebDriver(service.getUrl(),
					DesiredCapabilities.chrome()));

			System.out.println(driver.toString());
			selenium = new WebDriverBackedSelenium(driver,
					TestProperties.inst().TEAMMATES_URL);

		} else {

			System.out.println("Using " + TestProperties.inst().BROWSER);

			// iexplore, opera, safari. For some not-supported-yet browsers, we
			// use legacy methods: Going through the RC server.
			String selBrowserIdentifierString = "*" + TestProperties.inst().BROWSER;

			selenium = new DefaultSelenium(TestProperties.inst().SELENIUMRC_HOST,
					TestProperties.inst().SELENIUMRC_PORT, selBrowserIdentifierString,
					TestProperties.inst().TEAMMATES_URL);
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

	@SuppressWarnings("unused")
	private void ____Waiting_functions__________________________________() {
	}

	/*
	 * -------------------------------------------------------------------
	 * Waiting functions Uses Thread.sleep
	 * -----------------------------------------------------------------
	 */

	/**
	 * Waiting function used when we want to confirm sending e-mail. Waits for 5
	 * seconds.
	 */
	public void waitForEmail() {
		waitAWhile(5000);
	}

	/**
	 * Convenience method to wait for a specified period of time. Using
	 * Thread.sleep
	 * 
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
	 * Short snippet to wait for page-load. Must be appended after every action
	 * that requires a page reload or an AJAX request being made. huy (Aug 26) -
	 * This should be deprecated. Since WebDriver makes sure the new page is
	 * loaded before returning the call Aldrian (May 21) - But there are still
	 * functions using Selenium, which does not guarantee so
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
	 * 
	 * @param by
	 */
	public void waitForElementPresent(final By by) {
		int counter = 0;
		while (!isElementPresent(by)) {
			if (counter++ > RETRY)
				fail("Timeout while waiting for " + by);
			waitAWhile(RETRY_TIME);
		}
	}

	/**
	 * Waits for the element to present, returns on timeout
	 * 
	 * @param by
	 */
	public void waitForElementPresentWithoutFail(final By by) {
		try {
			(new WebDriverWait(driver, TIMEOUT))
					.until(new ExpectedCondition<WebElement>() {
						@Override
						public WebElement apply(WebDriver d) {
							return d.findElement(by);
						}
					});
		} catch (TimeoutException e) {

		}
	}

	/**
	 * Waits for the status message (div id=statusMessage) to have the exact
	 * content as specified)
	 * 
	 * @param message
	 */
	public void waitForStatusMessage(String message) {
		waitForTextInElement(statusMessage, message);
	}

	/**
	 * Waits for a text to appear in an element
	 * 
	 * @see {@link #TIMEOUT}
	 * @param locator
	 * @param value
	 */
	public void waitForTextInElement(By locator, String value) {
		int counter = 0;
		while (true) {
			if (isElementPresent(locator)
					&& getElementText(locator).equals(value))
				return;
			System.err.println("Looking for:" + locator + ": " + value);
			System.err.println("But found  :" + locator + ": "
					+ getElementText(locator));
			if (counter++ > RETRY)
				fail("Timeout while waiting for " + getElementText(locator)
						+ " [i.e. actual] to become same as [i.e. expected] "
						+ value);
			waitAWhile(RETRY_TIME);
		}
	}

	public void waitforElementTextChange(By locator) {
		String oldMessage = getElementText(locator);
		System.out.println(oldMessage);
		int counter = 0;
		while (true) {
			if (counter++ > RETRY)
				fail("Timeout while waiting for text at " + locator
						+ " to change");
			if (!getElementText(locator).equals(oldMessage))
				break;
			waitAWhile(RETRY_TIME);
		}
	}

	@SuppressWarnings("unused")
	private void ____Form_filling_funcations_______________________________() {
	}

	/*
	 * ---------------------------------------------------------------------
	 * Form filling functions Functions that deal with forms, such as filling a
	 * text, selecting value from dropdown box, etc.
	 * --------------------------------------------------------------------
	 */
	/**
	 * WebDriver fills the input field with text value (will clear the data
	 * first) <br />
	 * It will wait for the element to exist until timeout.
	 */
	public void fillString(By by, String value) {
		waitForElementPresent(by);
		WebElement ele = driver.findElement(by);
		ele.clear();
		ele.sendKeys(value);
	}

	/**
	 * Fills in Evaluation Name (using id=evaluationname) Waits for element
	 * exists or timeout.
	 * 
	 * @param name
	 * @return The final value in the field
	 */
	public String fillInEvalName(String name) {
		fillString(inputEvaluationName, name);
		return selenium.getValue("id=evaluationname");
	}

	/**
	 * Fills in Course Name (using id=coursename) Waits for element exist or
	 * timeout.
	 * 
	 * @param name
	 * @return The final value in the field
	 */
	public String fillInCourseName(String name) {
		fillString(coordCourseInputCourseName, name);
		return selenium.getValue("id=coursename");
	}

	/**
	 * Fills in course ID (using id=courseid) Waits for element exists or
	 * timeout.
	 * 
	 * @param id
	 * @return The final value in the field, whitespace-trimmed
	 */
	public String fillInCourseID(String id) {
		fillString(coordCourseInputCourseID, id);
		return selenium.getValue("id=courseid");
	}

	/**
	 * Retrieves element's text through WebDriver. Does not do any wait.
	 * 
	 * @return empty string if element is not found.
	 */
	public String getElementText(By locator) {
		if (!isElementPresent(locator))
			return "";
		return driver.findElement(locator).getText();
	}

	/**
	 * Retrieves the element's attribute based on the attribute name
	 * 
	 * @param locator
	 * @param attrName
	 * @return
	 */
	public String getElementAttribute(By locator, String attrName) {
		waitForElementPresent(locator);
		return driver.findElement(locator).getAttribute(attrName);
	}

	/**
	 * Retrieves the element's `value` attribute. Usually used for elements like
	 * input, option, etc.
	 * 
	 * @param locator
	 * @return
	 */
	public String getElementValue(By locator) {
		return getElementAttribute(locator, "value");
	}

	/**
	 * Retrieves the element's 'href' attribute, returns the relative path
	 * (i.e., without "http://<main-app-url>/")
	 * 
	 * @param locator
	 * @return
	 */
	public String getElementRelativeHref(By locator) {
		String link = getElementAttribute(locator, "href");
		if (!link.startsWith("http"))
			return link;
		String[] tokens = link.split("/");
		String result = "";
		for (int i = 3; i < tokens.length; i++) {
			result += tokens[i];
		}
		return result;
	}

	/**
	 * Returns the first selected option on a dropdown box. Waits until element
	 * exists or timeout.
	 * 
	 * @param locator
	 * @return
	 */
	public String getDropdownSelectedValue(By locator) {
		waitForElementPresent(locator);
		Select select = new Select(driver.findElement(locator));
		return select.getFirstSelectedOption().getAttribute("value");
	}

	/**
	 * Selects a value from a dropdown list. Waits until the element exists or
	 * timeout.
	 * 
	 * @param locator
	 * @param value
	 */
	public void selectDropdownByValue(By locator, String value) {
		waitForElementPresent(locator);
		Select select = new Select(driver.findElement(locator));
		select.selectByValue(value);
	}

	/**
	 * To be used in concurrent mode (which is no longer used), returns whether
	 * there is still a test using this browser instance
	 * 
	 * @return
	 */
	public boolean isInUse() {
		return inUse;
	}

	/**
	 * To be used in concurrent mode
	 * 
	 * @param b
	 */
	public void setInUse(boolean b) {
		inUse = b;
	}

	@SuppressWarnings("unused")
	private void ____Checker_functions__________________________________() {
	}

	/*
	 * -----------------------------------------------------------------------
	 * Checker functions (is*** methods) Checks whether some conditions are
	 * true, then return the result immediately (no waiting)
	 * ---------------------------------------------------------------------
	 */
	/**
	 * Wrapper method to check whether an element exists (already loaded)<br />
	 * Issue: It is said that this method return true also when the element is
	 * partially loaded (probably rendered but not enabled yet)
	 * 
	 * @param by
	 * @return
	 */
	public boolean isElementPresent(By by) {
		return driver.findElements(by).size() != 0;
	}

	/**
	 * Wrapper method to check whether an element exists (already loaded), wait
	 * until the element is present or timeout<br />
	 * Issue: It is said that this method return true also when the element is
	 * partially loaded (probably rendered but not enabled yet)
	 * 
	 * @param by
	 * @return
	 */
	public boolean isElementPresentWithWait(By by) {
		waitForElementPresent(by);
		return driver.findElements(by).size() != 0;
	}

	/**
	 * Checks whether a text is present in current page
	 * 
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
		if (isElementPresent(By.id("email"))
				&& isElementPresent(By.id("isAdmin")))
			return true;
		return false;
	}

	/**
	 * Helper method to check that we're at the login page Checking for the
	 * e-mail and password fields, and the sign in button
	 * 
	 * @return
	 */
	public boolean isGoogleLoginPage() {
		if (isElementPresent(By.id("Email"))
				&& isElementPresent(By.id("Passwd"))
				&& isElementPresent(By.id("signIn")))
			return true;

		return false;
	}

	/**
	 * Checks whether a course is present in Course page
	 * 
	 * @page Course page
	 * @param courseId
	 * @param courseName
	 * @return
	 */
	public boolean isCoursePresent(String courseId, String courseName) {
		int totalCourses = countCourses();
		boolean isPresent = false;
		for (int i = 0; i < totalCourses; i++) {
			if (getElementText(By.id("courseid" + i))
					.equalsIgnoreCase(courseId)
					&& getElementText(By.id("courseName" + i)).equals(
							courseName)) {
				isPresent = true;
				break;
			}
		}
		return isPresent;
	}

	/**
	 * Checks whether a course is present in Home page
	 * 
	 * @param courseID
	 * @param courseName
	 * @return
	 */
	public boolean isHomeCoursePresent(String courseID, String courseName) {
		int id = 0;
		while (isElementPresent(By.id("course" + id))) {
			if (getElementText(
					By.xpath("//div[@id='course" + id
							+ "']/div[@class='result_homeTitle']/h2"))
					.equalsIgnoreCase("[" + courseID + "] : " + courseName)) {
				return true;
			}
			id++;
		}
		return false;
	}

	/**
	 * Checks whether a specific evaluation exists (based on courseID and
	 * evaluation name)
	 * 
	 * @page Evaluation page
	 * @param courseId
	 * @param evalName
	 * @return
	 */
	public boolean isEvaluationPresent(String courseId, String evalName) {
		return getEvaluationRowID(courseId, evalName) > -1;
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

	@SuppressWarnings("unused")
	private void ____Goto_functions__________________________________() {
	}

	/*
	 * -------------------------------------------------------------------- GoTo
	 * functions Goes to certain pages and verifies whether it is successful
	 * ------------------------------------------------------------------
	 */
	/**
	 * Goes to the URL as specified. If the url is an absolute path, it will go
	 * directly there. Otherwise, it will go to TEAMMATES_URL+url (with url
	 * checked for "/"-prefix)
	 * 
	 * @param url
	 */
	public void goToUrl(String url) {
		if (url.startsWith("http")||url.startsWith("file")) {
			driver.get(url);
		} else {
			if (!url.startsWith("/")) {
				url = "/" + url;
			}
			driver.get(TestProperties.inst().TEAMMATES_URL + url);
		}
		if (!TestProperties.inst().isLocalHost()) {
			waitForPageLoad();
		}
	}

	@SuppressWarnings("unused")
	private void ____Object_comparison_functions___________________________() {
	}

	/*
	 * -----------------------------------------------------------------------
	 * Object comparison functions Compares objects and verifies that the
	 * objects are the same
	 * ---------------------------------------------------------------------
	 */
	/**
	 * Verifies a HTML page as pointed by url against the HTML file stored at
	 * location pointed by filepath
	 * 
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
	 * Verifies current page against the page stored at location as pointed by
	 * filepath. This method replaces the occurence of {version} in the
	 * reference file with the value stored at Common.VERSION
	 * 
	 * @param filepath
	 * @throws Exception
	 */
	public void verifyCurrentPageHTML(String filepath) throws Exception {
		String pageSrc = getCurrentPageSource();
		String inputStr = Common.readFile(filepath).replace("{version}",
				TestProperties.inst().TEAMMATES_VERSION);
		HtmlHelper.assertSameHtml(inputStr, pageSrc);
	}

	/**
	 * Verifies current page with a reference page, i.e., finding the reference
	 * string in current page (so the reference does not have to be full page)<br />
	 * <br />
	 * This method has minimal placeholder capability, matching {*} in the
	 * reference with anything in current page, trying to maximize the match.
	 * This method also replaces {version} into the value stored at
	 * Common.VERSION<br />
	 * <br />
	 * Example usage is to test sorting elements, say we want to test the order
	 * of two known elements, which should be independent in the presence of
	 * other elements. We can also ignore the rowID which maybe different under
	 * different number of elements.<br />
	 * <br />
	 * This method will try to display the difference between the expected and
	 * actual if the match fails.
	 * 
	 * @param filepath
	 * @param div
	 * @throws Exception
	 */
	public void verifyCurrentPageHTMLRegex(String filepath) throws Exception {
		String pageSrc = getCleanPageSource();
		String inputStr = getCleanExpectedHtml(filepath);
		BaseTestCase.assertContainsRegex(inputStr,pageSrc);
	}

	/**
	 * @param filepath
	 * @return Returns content of the file after replacing 
	 *    parameters e.g. {version} and transforming to "clean" HTML. 
	 * @throws Exception
	 */
	private String getCleanExpectedHtml(String filepath)
			throws Exception {
		String inputStr = Common.readFile(filepath).replace("{version}",
				TestProperties.inst().TEAMMATES_VERSION);
		inputStr = HtmlHelper.cleanupHtml(inputStr);
		return inputStr;
	}

	/**
	 * @return Returns content of the file after transforming to "clean" HTML.
	 * @throws Exception
	 */
	private String getCleanPageSource() throws Exception {
		String pageSrc = getCurrentPageSource();
		
		pageSrc = HtmlHelper.preProcessHTML(pageSrc);
		pageSrc = HtmlHelper.cleanupHtml(pageSrc);
		return pageSrc;
	}

	/**
	 * Verifies current page with a reference page, i.e., finding the reference
	 * string in current page (so the reference does not have to be full page)<br />
	 * This will reload the page from the given url up to two more times (that
	 * is, three times checking), depending on the variable
	 * {@link #PAGE_VERIFY_RETRY}<br />
	 * <br />
	 * This method has minimal placeholder capability, matching {*} in the
	 * reference with anything in current page, trying to maximize the match.
	 * This method also replaces {version} into the value stored at
	 * Common.VERSION<br />
	 * <br />
	 * Example usage is to test sorting elements, say we want to test the order
	 * of two known elements, which should be independent in the presence of
	 * other elements. We can also ignore the rowID which maybe different under
	 * different number of elements.<br />
	 * <br />
	 * This method will try to display the difference between the expected and
	 * actual if the match fails.<br />
	 * 
	 * @param filepath
	 * @param url
	 * @throws Exception
	 */
	public void verifyCurrentPageHTMLRegexWithRetry(String filepath, String url)
			throws Exception {
		String pageSrc = null;
		String inputStr = null;
		for (int i = 0; i < PAGE_VERIFY_RETRY; i++) {
			
			pageSrc = getCleanPageSource();
			inputStr = getCleanExpectedHtml(filepath);
			
			if (BaseTestCase.isContainsRegex(inputStr,pageSrc)) {
				return;
			}
			if (i == PAGE_VERIFY_RETRY - 1)
				break;
			System.out.println("Reloading page:" + url);
			waitAWhile(1000);
			goToUrl(url);
		}
		assertEquals(inputStr, pageSrc);
	}

	/**
	 * Method to print current page to a file. This is to be used in HTML
	 * testing, where we can generate the reference HTML file using this method.
	 * This method is deprecated so that you won't forget to remove it
	 * 
	 * @param destination
	 */
	public void printCurrentPage(String destination) throws Exception {
		waitForPageLoad();
		String pageSrc = getCurrentPageSource();
		FileWriter output = new FileWriter(new File(destination));
		output.write(pageSrc);
		output.close();
	}

	public String getCurrentPageSource() {
		waitForPageLoad();
		return driver.getPageSource();
	}

	/**
	 * Verifies an object content (div) against the one stored at filepath
	 * 
	 * @param filepath
	 * @param div
	 * @throws Exception
	 */
	public void verifyObjectHTML(String filepath, String div) throws Exception {
		try {
			String pageSrc = getCurrentPageSource();
			FileInputStream refSrc = new FileInputStream(filepath);
			BufferedReader actual = new BufferedReader(
					new StringReader(pageSrc));
			BufferedReader expected = new BufferedReader(new InputStreamReader(
					new DataInputStream(refSrc)));

			String expectedLine;
			String actualLine;
			while ((actualLine = actual.readLine()) != null) {
				if (actualLine.contains(div)) {
					while ((expectedLine = expected.readLine()) != null) {
						assertNotNull(
								"Expected had more lines then the actual.",
								actualLine);
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
}
