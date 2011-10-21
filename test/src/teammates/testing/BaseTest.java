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

import teammates.testing.lib.SharedLib;
import teammates.testing.object.Evaluation;
import teammates.testing.object.Scenario;
import teammates.testing.object.Student;

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

	protected static ChromeDriverService chromeService = null;

	/**
	 * Start Chrome service, return service instance
	 * 
	 * @return the service instance
	 */
	private static ChromeDriverService startChromeDriverService() {
		chromeService = new ChromeDriverService.Builder()
				.usingChromeDriverExecutable(new File(Config.getChromeDriverPath()))
				.usingAnyFreePort().build();
		try {
			chromeService.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return chromeService;
	}

	protected static void setupSelenium() {
		System.out.println("Initializing Selenium.");
		
		
		if (Config.BROWSER.equals("htmlunit")) {

			System.out.println("Using HTMLUnit.");

			driver = new HtmlUnitDriver();

			selenium = new WebDriverBackedSelenium(driver, Config.TEAMMATES_URL);

		} else if (Config.BROWSER.equals("firefox")) {

			System.out.println("Using Firefox.");

			driver = new FirefoxDriver();
			selenium = new WebDriverBackedSelenium(driver, Config.TEAMMATES_URL);

		} else if (Config.BROWSER.equals("chrome")) {

			System.out.println("Using Chrome");

			// Use technique here:
			// http://code.google.com/p/selenium/wiki/ChromeDriver
			ChromeDriverService service = startChromeDriverService();
			driver = new RemoteWebDriver(service.getUrl(),
					DesiredCapabilities.chrome());

			System.out.println(driver.toString());
			selenium = new WebDriverBackedSelenium(driver, Config.TEAMMATES_URL);

			/*
			 * Chrome hack. Currently Chrome doesn't support confirm() yet.
			 * http://code.google.com/p/selenium/issues/detail?id=27
			 */
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("window.confirm = function(msg){ return true;};");

		} else {

			System.out.println("Using " + Config.BROWSER);

			// iexplore, opera, safari. For some not-supported-yet browsers, we use
			// legacy methods: Going through the RC server.
			String selBrowserIdentifierString = "*" + Config.BROWSER;

			selenium = new DefaultSelenium("localhost", 4444,
					selBrowserIdentifierString, Config.TEAMMATES_URL);
			CommandExecutor executor = new SeleneseCommandExecutor(selenium);
			DesiredCapabilities dc = new DesiredCapabilities();
			driver = new RemoteWebDriver(executor, dc);

		}

		selenium.windowMaximize();
		selenium.open("/");

	}

	protected static void setupScenario() {
		sc = Scenario.fromJSONFile("./scenario.json");
	}
	
	protected static void setupScenarioForBumpRatioTest(int index) {
		sc = Scenario.scenarioForBumpRatioTest("./bump_ratio_scenario.json", index);
	}

	/**
	 * Called when the run is over.
	 */
	protected static void wrapUp() {
		selenium.stop();
		if (chromeService != null && chromeService.isRunning())
			chromeService.stop();

	}

	/**
	 * Delete all available courses.
	 */
	public static void deleteAllCourses() throws Exception {
		while (driver.findElements(By.cssSelector("#coordinatorCourseTable tr"))
				.size() > 1) {
			System.out.println("Deleting a course...");

			clickAndConfirm(By.className("t_course_delete"));

			waitForElementText(By.id("statusMessage"), "The course has been deleted.");
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

			By by = By.xpath(String
					.format("//table[@id='dataform']//tr[%d]//a[4]", 2));
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
	public static void deleteAllEvaluations() {
	
		while (driver.findElements(By.className("t_eval_delete")).size() > 1) {
			System.out.println("Deleting 1 evaluation...");

			clickAndConfirm(By.className("t_eval_delete"));

			waitForElementText(By.id("statusMessage"),
					"The evaluation has been deleted.");
			gotoEvaluations(); // This is to fix for Datastore delay problem
		}
	}

	public static void waitForElementPresent(By by) {
		int counter = 0;
		while (!isElementPresent(by)) {
			if (counter++ > 200)
				fail("Timeout");
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * WebDriver click on element
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

	public static void waitAndClickAndConfirm(By by) {
		waitForElementPresent(by);
		clickAndConfirm(by);
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

	/**
	 * Wait 2 seconds.
	 * 
	 * Inspired by Lady Gaga's Just Dance
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

	/**
	 * Short snippet to wait for page-load.
	 * 
	 * Must be appended after every action that requires a page reload or an AJAX
	 * request being made
	 * 
	 * huy (Aug 26) - This should be deprecated. Since WebDriver makes sure the
	 * new page is loaded before returning the call
	 */
	public static void waitForPageLoad() {
		try {
			selenium.waitForPageToLoad("15000");
		} catch (SeleniumException e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Keep waiting until a particular element's text changes
	 * 
	 * @param string
	 */
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
	 * When authentication for the first few times, it might ask for the
	 * "grant permission" page. If that's the case we simply click "Grant"
	 */
	private static void checkGooglePermPage() {
		if (selenium.getTitle().equals("Google Accounts")) {
			driver.findElement(By.name("submit_true"));
			waitForPageLoad();
		}
	}

	private static void _login(String email, String password) {

		if (isLocalLoginPage()) {
			wdFillString(By.id("email"), email);
			selenium.click("css=input[value='Log In']");
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
			waitForPageLoad();

			// Check if this is the permission page
			checkGooglePermPage();
		} else {
			fail("Not in the correct Login page");
			return;
		}
	}

	/**
	 * Coordinator Login
	 * 
	 * @startpage Home page
	 */
	public static void coordinatorLogin(String username, String password) {

		cout("Logging in coordinator " + username + ".");
		// Click the Coordinator button on the main page
		wdClick(By.name("COORDINATOR_LOGIN"));
		waitForPageLoad();		
		/*
		 * IE Fix: for some reason in IE new profile is not created, thus user is
		 * already logged in. This will log user out.
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
	 * @startpage Homepage
	 */
	public static void studentLogin(String username, String password) {

		cout("Logging in student " + username + ".");

		// Click the Student button on the main page
		wdClick(By.name("STUDENT_LOGIN"));
		waitForPageLoad();

		/*
		 * IE Fix: for some reason in IE new profile is not created, thus user is
		 * already logged in. This will log user out.
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

	public void logout() {
		cout("Signing out.");
		waitAndClick(By.className("t_logout"));
		// Check that we're at the main page after logging out
		verifyMainPage();
	}

	public static void addCourse(String courseId, String courseName) {

		wdFillString(By.id("courseid"), courseId);
		wdFillString(By.id("coursename"), courseName);
		wdClick(By.id("btnAddCourse"));
		justWait();
	}

	/**
	 * WebDriver fills the input field with text value (will clear the data first)
	 */
	protected static void wdFillString(By by, String value) {
		WebElement ele = driver.findElement(by);
		ele.clear();
		ele.sendKeys(value);
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

	public static void addEvaluation(Evaluation eval) {
		waitAndClick(By.className("t_evaluations"));

		// Select the course
		waitAndClick(By.id("courseid"));
		waitAndClick(By.xpath("//option[@value='" + eval.courseID + "']"));

		// Fill in the evaluation name
		wdFillString(By.id("evaluationname"), eval.name);

		// Allow P2P comment
		wdClick(By.xpath("//*[@id='commentsstatus'][@value='" + eval.p2pcomments
				+ "']"));

		// Fill in instructions
		wdFillString(By.id("instr"), eval.instructions);

		// Select deadline date
		wdClick(By.xpath("//*[@id='deadline']"));
		selenium.waitForPopUp("window_deadline", "30000");
		selenium.selectWindow("name=window_deadline");
		wdClick(By.xpath("//a[contains(@href, '" + eval.dateValue + "')]"));

		for (String s : driver.getWindowHandles()) {
			selenium.selectWindow(s);
			break;
		}

		selectDropdownByValue(By.id("deadlinetime"), eval.nextTimeValue);

		// Select grace period
		selectDropdownByValue(By.id("graceperiod"),
				Integer.toString(eval.gracePeriod));

		// Submit the form
		wdClick(By.id("t_btnAddEvaluation"));
	}

	public static void enrollStudents(List<Student> students) {
		// To Enroll page
		waitAndClick(By.className("t_course_enrol"));
		verifyEnrollPage();

		wdFillString(By.id("information"), getStudentsString(students));
		wdClick(By.id("button_enrol"));
		justWait();
	}

	/**
	 * Snippet to go to Courses page
	 */
	public static void gotoCourses() {
		wdClick(By.className("t_courses"));
		justWait();
		verifyCoordinatorPage();
	}

	/**
	 * Snippet to go to Evaluations page
	 */
	public static void gotoEvaluations() {
		wdClick(By.className("t_evaluations"));
		justWait();
		verifyEvaluationPage();
	}

	/**
	 * Helper function to clean up email account
	 * 
	 * @throws Exception
	 */
	protected static void cleanupGmailInbox() throws Exception {
		for (int i = 0; i < sc.students.size(); i++) {
			SharedLib.markAllEmailsSeen(sc.students.get(i).email,
					Config.TEAMMATES_APP_PASSWD);
			System.out.println("clean up gmail Inbox for " + sc.students.get(i).name);
		}
	}

	/**
	 * Shortcut for System.out.println
	 */
	protected static void cout(String message) {
		System.out.println(message);
	}

	protected static boolean isElementPresent(By by) {
		return driver.findElements(by).size() != 0;
	}

	/**
	 * Helper method to check that we're at the main page Checking for the
	 * Coordinator and Student links
	 */
	public static void verifyMainPage() {
		for (int x = 0;; x++) {
			if (x >= 40)
				fail("timeout");

			if (isElementPresent(By.name("STUDENT_LOGIN"))
					&& isElementPresent(By.name("COORDINATOR_LOGIN")))
				break;

			waitAWhile(1000);
		}
	}

	/**
	 * For App Engine Local Run - Check if we're at the local login.
	 * 
	 * @return
	 */
	public static boolean isLocalLoginPage() {
		if (isElementPresent(By.id("email")) && isElementPresent(By.id("isAdmin")))
			return true;
		return false;
	}

	// Helper method to check that we're at the login page
	// Checking for the e-mail and password fields, and the sign in button
	public static boolean isGoogleLoginPage() {
		if (isElementPresent(By.id("Email")) && isElementPresent(By.id("Passwd"))
				&& isElementPresent(By.id("signIn")))
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
		if (isElementPresent(By.id("courseid"))
				&& isElementPresent(By.id("coursename")))
			return;

		fail("Not in Coordinator Page");
	}

	// Helper method to check that we're at the Student page (after login)
	// Checking for links at the top, and add course form
	public static void verifyStudentPage() {
		for (int x = 0;; x++) {
			if (x >= 40)
				fail("timeout");

			if (isElementPresent(By.name("regkey"))
					&& isElementPresent(By.className("t_evaluations"))
					&& isElementPresent(By.className("t_logout"))
					&& isElementPresent(By.className("t_courses")))
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

			if ((isElementPresent(By.id("courseid")))
					&& (isElementPresent(By.id("evaluationname")))
					&& (isElementPresent(By.xpath("//*[@id='commentsstatus']")))
					&& (isElementPresent(By.xpath("//*[@id='instr']")))
					&& (isElementPresent(By.xpath("//*[@id='start']")))
					&& (isElementPresent(By.xpath("//*[@id='starttime']")))
					&& (isElementPresent(By.xpath("//*[@id='deadline']")))
					&& (isElementPresent(By.xpath("//*[@id='deadlinetime']")))
					&& (isElementPresent(By.xpath("//*[@id='graceperiod']"))))
				break;
			waitAWhile(200);
		}
	}

	/**
	 * Checks that the course has been added Checking for the course details
	 * appearing in the table Page: Coordinator home TODO: change to any number of
	 * previous courses
	 */
	public static void verifyAddedCourse(String courseId, String courseName) {
		// Check for courseId
		assertEquals(courseId,
				getElementText(By
						.cssSelector("#coordinatorCourseTable td.t_course_code")));

		// Check for course name
		assertEquals(courseName,
				getElementText(By
						.cssSelector("#coordinatorCourseTable td.t_course_name")));

		// Check for default number of teams - 0
		assertEquals("0",
				getElementText(By
						.cssSelector("#coordinatorCourseTable td.t_course_teams")));
	}

	// Checks that we're at the student enrollment page
	// Checking for the form fields and the buttons
	public static void verifyEnrollPage() {
		for (int x = 0;; x++) {
			if (x >= 40)
				fail("timeout");
			if (isElementPresent(By.id("information"))
					&& isElementPresent(By.id("button_enrol")))
				break;
			waitAWhile(200);
		}
	}

	// Helper method to check that we've enrolled students successfully.
	// Checks that the number of students added/edited equals the number expected.
	public static void verifyEnrollment(int added, int edited) {
		for (int x = 0;; x++) {
			if (x >= 40)
				fail("timeout");
			if ((isElementPresent(By.xpath("//tr[@id='rowAddedStudents']/td")))
					&& (isElementPresent(By.xpath("//tr[@id='rowEditedStudents']/td"))))
				break;
			waitAWhile(200);
		}

		assertEquals(added,
				Integer.parseInt(getElementText(By.id("t_studentsAdded"))));
		assertEquals(edited,
				Integer.parseInt(getElementText(By.id("t_studentsEdited"))));
	}

	// Helper method to check that the evaluation was added successfully
	// Checks for the details of the evaluation that was added.
	public static void verifyEvaluationAdded(String courseId, String evalName,
			String status, String resp) {
		assertEquals(courseId, getElementText(By.className("t_eval_coursecode")));
		assertEquals(evalName, getElementText(By.className("t_eval_name")));
		assertEquals(status, getElementText(By.className("t_eval_status")));
		assertEquals(resp, getElementText(By.className("t_eval_response")));
	}

	private static void confirmYes() {

		/*
		 * if (!Config.BROWSER.equals("chrome")) { Alert alert =
		 * driver.switchTo().alert(); alert.accept(); }
		 */}

	private static void confirmNo() {
		/*
		 * if (!Config.BROWSER.equals("chrome")) { Alert alert =
		 * driver.switchTo().alert(); alert.dismiss(); }
		 */}

	public static void chromeConfirmYes() {
		/*
		 * Huy: I have no idea why the driver.switchTo().alert() approach doesn't
		 * work even in Firefox (it supposed to!). This is a workaround to press Yes
		 * in the confirmation box. Same for function below for No.
		 */

		// if (Config.BROWSER.equals("chrome")) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.confirm = function(msg){ return true;};");
		// }
	}

	public static void chromeConfirmNo() {
		// if (Config.BROWSER.equals("chrome")) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.confirm = function(msg){ return false;};");
		// }
	}

	public static void clickAndConfirm(By by) {
		chromeConfirmYes();
		wdClick(by);
		confirmYes();
	}

	public static void waitAndClickAndCancel(By by) {
		chromeConfirmNo();
		waitAndClick(by);
		confirmNo();
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
	 * Retrieve the element's `value` attribute. Usually used for elements like
	 * input, option, etc.
	 * 
	 * @param locator
	 * @return
	 */
	public static String getElementValue(By locator) {
		return driver.findElement(locator).getAttribute("value");
	}

	public static void selectDropdownByValue(By locator, String value) {
		Select select = new Select(driver.findElement(locator));
		select.selectByValue(value);
	}

	public static String getDropdownSelectedValue(By locator) {
		Select select = new Select(driver.findElement(locator));
		return select.getFirstSelectedOption().getAttribute("value");
	}

}
